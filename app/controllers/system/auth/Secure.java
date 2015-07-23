package controllers.system.auth;


import cache.CacheCallBack;
import cache.CacheHelper;
import models.operate.OperateUser;
import models.operate.OperateUserLoginHistory;
import models.operate.Operator;
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

    public static final  String REMEMBERME_COOKIE_NAME = "operateUser_Name";
    private static final String AUTO_LOGIN_COOKIE_NAME = "operateUser_Token";
    private static final String AUTO_LOGIN_URL = "/system/login";
    private static final String AUTO_LOGIN_BACK_URL = "/systems";


    @Before
    public static void setOperator() {
        Operator operateor = getOperateor();
        renderArgs.put("currentOperator", operateor);
    }

    /**
     * 获取场馆
     * @return
     */
    public static Operator getOperateor() {
        if(getOperateUser() == null) {
            return null;
        } else {
            return getOperateUser().operator;
        }
    }

    /**
     * oauth用户的样例: UserProfile#SinaWeibo:1802362721
     * @return
     */
    public static OperateUser getOperateUser() {
        OperateUser operateUser = null;
        String uid = session.get(OperateUser.LOGIN_ID);
        if (uid != null && !uid.equals("") && ! uid.equals("null")) {
            final Long operateUserId = Long.parseLong(uid);
            operateUser = CacheHelper.getCache(OperateUser.LOGIN_SESSION_USER + uid, new CacheCallBack<OperateUser>() {
                @Override
                public OperateUser loadData() {
                    return OperateUser.findById(operateUserId);
                }
            });
            if (operateUser == null) {
                Logger.info("operateUser is null, 不能登录.");
                return  null;
            }
        }
        return operateUser;
    }

    public static OperateUser getOperateUserForUpdate() {
        OperateUser operateUser = getOperateUser();
        if (operateUser != null) {
            return OperateUser.findById(operateUser.id);
        }
        return null;
    }


    /**
     * 登录.
     */
    public static void login(String loginName) {
        if(getOperateUser() != null){
            redirect(AUTO_LOGIN_BACK_URL);
        }
        if (StringUtils.isNotBlank(loginName)) {
            renderArgs.put("loginName", loginName);
        }
        render();
    }


    public static void authenticate(String username, String password) {
        Logger.info("username = " + username + ", password=" + password);
        // 记住用户名
        OperateUser operateUser = OperateUser.findByLoginNameAndPassword(username, password);//根据 登陆 帐号  密码 获取 登陆用户
        Logger.info("authenticate operateUser=" + operateUser);
        if (operateUser == null) {
            flash.put("error", "用户或密码错误!");
            login(username);
        } else {
            new OperateUserLoginHistory(operateUser , request.remoteAddress);
            session.put(OperateUser.LOGIN_ID, operateUser.id);
            session.put(OperateUser.LOGIN_NAME, operateUser.loginName);
            // we redirect to the original URL
            String url = (String) Cache.get("url_" + session.getId());
            Cache.delete("url_" + session.getId());
            if (url == null) {
                url = AUTO_LOGIN_BACK_URL;
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
        String uid = session.get(OperateUser.LOGIN_ID);
        Cache.delete(OperateUser.LOGIN_SESSION_USER + uid);
        response.removeCookie(AUTO_LOGIN_COOKIE_NAME);
        session.clear();

        String casLogoutUrl = AUTO_LOGIN_URL;
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
        if (getOperateUser() == null) {
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
//            redirect(AUTO_LOGIN_URL);
            login(null);
        }
    }

    @After
    public static void cleanCacheHelper() {
        CacheHelper.cleanPreRead();
    }


}
