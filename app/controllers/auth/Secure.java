package controllers.auth;


import cache.CacheCallBack;
import cache.CacheHelper;
import models.mert.MerchantUser;
import models.mert.enums.MerchantStatus;
import org.apache.commons.lang.StringUtils;
import play.Logger;
import play.cache.Cache;
import play.mvc.*;
import play.mvc.Http.Header;


/**
 * This class is a part of the play module secure-cas. It add the ability to check if the user have access to the
 * request. If the user is note logged, it redirect the user to the CAS login page and authenticate it.
 *
 * @author bsimard
 */
public class Secure extends Controller {

    public static final  String REMEMBERME_COOKIE_NAME = "userName";
    private static final String AUTO_LOGIN_COOKIE_NAME = "userToken";

    @Before
    public static void setUser() {
        MerchantUser user = getUser();
        renderArgs.put("currentUser", user);
    }

    /**
     * oauth用户的样例: UserProfile#SinaWeibo:1802362721
     *
     * @return
     */
    public static MerchantUser getUser() {
        MerchantUser user = null;
        String uid = session.get(MerchantUser.LOGIN_ID);
        Logger.debug("uid=" + uid);
        if (uid != null && !uid.equals("") && !uid.equals("null")) {
            final Long userId = Long.parseLong(uid);
            user = CacheHelper.getCache(MerchantUser.LOGIN_SESSION_USER + uid, new CacheCallBack<MerchantUser>() {
                @Override
                public MerchantUser loadData() {
                    return MerchantUser.findValidUser(userId);
                }
            });
            if (user == null || user.mobile == null) {
                Logger.info("user.mobile is null, 不能登录.");
                return  null;
            }
        }
        Logger.info("user=" + user);
        return user;
    }

    public static MerchantUser getUserForUpdate() {
        MerchantUser user = getUser();
        if (user != null) {
            return MerchantUser.findById(user.id);
        }
        return null;
    }


    /**
     * 登录.
     */
    public static void login(String username) {
        if (StringUtils.isBlank(username)) {
            // 从cookie中得到上次登录名
            Http.Cookie cookieUserName = request.cookies.get(REMEMBERME_COOKIE_NAME);
//            Http.Cookie cookieUserPWD = request.cookies.get(REMEMBERME_COOKIE_PWD);
            if (cookieUserName != null) {
                renderArgs.put("userName", cookieUserName.value);
            }
        } else {
            renderArgs.put("userName", username);
        }
        render("auth/Secure/login.html");
    }


    public static void authenticate(String username, String password) {
        Logger.debug("username = " + username + ", password=" + password);

        MerchantUser user = MerchantUser.findByLoginNameAndPassword(username, password); //根据 登陆 帐号  密码 获取 登陆用户
        Logger.debug("authenticate findUser=" + user);
        if (user == null) {
            flash.put("error", "用户或密码错误!");
            login(username);
        } else if (user.merchant.status == MerchantStatus.FREEZE) {
            session.put(MerchantUser.LOGIN_ID,user.id);
            flash.put("error" , "您的帐号被锁定或者没有被激活.");
            renderArgs.put("user",user);
            render("/auth/Secure/merchantLogin.html");
            //Regists.registSecond(user);
        }else {
            session.put(MerchantUser.LOGIN_ID, user.id);
            // we redirect to the original URL
            String url = (String) Cache.get("url_" + session.getId());
            Cache.delete("url_" + session.getId());
            if (url == null) {
                url = "/shanghu";
            }
            Logger.debug("[Secure]: redirect to url -> " + url);
            redirect(url);
        }
    }


    /**
     * Action for the logout route. We clear cache & session and redirect the user to CAS logout page.
     *
     * @throws Throwable
     */
    public static void logout() throws Throwable {
        String uid = session.get(MerchantUser.LOGIN_ID);
        Cache.delete(MerchantUser.LOGIN_SESSION_USER + uid);
        response.removeCookie(AUTO_LOGIN_COOKIE_NAME);

        session.clear();

        String casLogoutUrl = "/";
        redirect(casLogoutUrl);
    }

    /**
     * Action when the user authentification or checking rights fails.
     *
     * @throws Throwable
     */
    public static void fail() throws Throwable {
        // forbidden();
        // 如果失败，直接到logout先
        String casLogoutUrl = "/";
        redirect(casLogoutUrl);
    }


    private static boolean skipLoginCheck() {
        if (getActionAnnotation(SkipLoginCheck.class) != null ||
                getControllerInheritedAnnotation(SkipLoginCheck.class) != null) {
            Logger.info("SkipLoginCheck=true");
            return true;
        }
        Logger.info("SkipLoginCheck=false");
        return false;
    }

    /**
     * Method that do CAS Filter and check rights.
     *
     * @throws Throwable
     */
    @Before(unless = {"login", "logout", "fail", "authenticate"})
    public static void filter() throws Throwable {
        Logger.info("[Secure]: Filter for URL -> " + request.url);

        // 测试用，见 @Security.setLoginUserForTest说明
        /*
        if (Security.isTestLogined()) {
            Logger.debug("set test user %s", Security.getLoginUserForTest());
            session.put(SESSION_USER_KEY, Security.getLoginUserForTest());
        }
*/
        if (skipLoginCheck()) {
            Logger.info("[Secure]: Skip the CAS.");
            return;
        }
        // if user is authenticated, the username is in session !
        // Single Sign Out: 如果Cache.get(SESSION_USER_KEY + session.get(SESSION_USER_KEY))为空，则已经被其它应用注销.
        if (getUser() == null) {
            // We must avoid infinite loops after success authentication
            if (!Router.route(request).action.equals("auth.Secure.login")) {
                // we put into cache the url we come from
                Cache.add("url_" + session.getId(), request.method.equals("GET") ? request.url : "/", "10min");
            } else {
                Header header = request.headers.get("referer");
                if (header != null) {
                    String referer = header.value();
                    Cache.add("url_" + session.getId(), request.method.equals("GET") ? referer : "/", "10min");
                }
            }

            // we redirect the user to the cas login page
            redirect("/login");
        }
    }

    @After
    public static void cleanCacheHelper() {
        CacheHelper.cleanPreRead();
    }
}
