package controllers;

import cache.CacheHelper;
import controllers.operate.cas.Security;
import models.operator.OperateNavigation;
import models.operator.OperatePermission;
import models.operator.OperateRole;
import models.operator.OperateUser;
import operate.rbac.ContextedPermission;
import operate.rbac.NavigationHandler;
import operate.rbac.annotations.ActiveNavigation;
import operate.rbac.annotations.Right;
import org.apache.commons.lang.StringUtils;
import play.Logger;
import play.Play;
import play.cache.Cache;
import play.mvc.After;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Finally;
import play.mvc.Http;
import play.mvc.Http.Request;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Have a menu automatically injected in your renderArgs
 *
 * This MenuInjector has an @Before annotated action that reads comma separated menu names from the navigation.defaultMenus
 * configuration parameter in your conf/application.conf file, and injects those menus into your renderArgs.
 */
public class OperateRbac extends Controller {

    private static ThreadLocal<OperateUser> _user = new ThreadLocal<>();


    public static final String SESSION_USER_ID_KEY = "operate_login";

    public static final  String REMEMBERME_COOKIE_NAME = "userName";
    private static final String AUTO_LOGIN_COOKIE_NAME = "userToken";


    /**
     * Action for the login route. We simply redirect to CAS login page.
     *
     * @throws Throwable
     */
    public static void login(String username) throws Throwable {
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
        render("defaults/auth/Secure/login.html");
    }

    /**
     * Action for the logout route. We clear cache & session and redirect the user to CAS logout page.
     *
     * @throws Throwable
     */
    public static void logout() throws Throwable {
        String uid = session.get(SESSION_USER_ID_KEY);
        Cache.delete(SESSION_USER_ID_KEY + uid);
        response.removeCookie(AUTO_LOGIN_COOKIE_NAME);

        session.clear();

        redirect("/admin/");
    }

    /**
     * Action when the user authentification or checking rights fails.
     *
     * @throws Throwable
     */
    public static void fail() throws Throwable {
        forbidden();
    }

    /**
     * Action for the CAS return.
     *
     * @throws Throwable
     */
    public static void authenticate(String username, String password) throws Throwable {
        Logger.debug("username = " + username);

        OperateUser user = OperateUser.getLoginUser(username, password); //根据 登陆 帐号  密码 获取 登陆用户
        Logger.debug("authenticate findUser=" + user);
        if (user == null) {
            flash.put("error", "用户或密码错误!");
            login(username);
        } else {
            user.lastLoginIP = request.remoteAddress;
            user.lastLoginAt = new Date();
            user.save();
            session.put(SESSION_USER_ID_KEY, user.id);
            // we redirect to the original URL
            String url = (String) Cache.get("url_" + session.getId());
            Cache.delete("url_" + session.getId());
            if (url == null) {
                url = "/";
            }
            Logger.debug("[Secure]: redirect to url -> " + url);
            redirect(url);
        }
    }

    /**
     * Method that do CAS Filter and check rights.
     *
     * @throws Throwable
     */
    @Before(unless = { "login", "logout", "fail", "authenticate", "pgtCallBack", "setLoginUserForTest" })
    public static void injectCurrentMenus() {
        Logger.debug("[OperateRbac]: CAS Filter for URL -> " + request.url);
        long beginLoadCurrentMenus = System.currentTimeMillis();

        if (Security.isTestLogined()) {
            session.put(SESSION_USER_ID_KEY, Security.getLoginUserForTest());
        }

        if (request.invokedMethod == null)
            return;

        String userName = session.get(SESSION_USER_ID_KEY);

        Logger.debug("======================================== currentUser = " + userName);

        OperateUser user = null;

        // 检查权限
        if (userName != null) {
            // 查出当前用户的所有权限
            user = OperateUser.findUser(userName);
            Logger.debug(" ---------------------------- user : " + user);
            if (user != null) {
                renderArgs.put("currentUser", user);
            }
            if (Logger.isDebugEnabled() && user != null && user.roles != null) {
                Logger.debug("user.id = " + user.id + ", name=" + user.loginName);
                Logger.debug("get role " + user.roles);
                for (OperateRole role : user.roles) {
                    Logger.debug("user.role=%s, rold.id=%d", role.key, role.id);
                }
            }
            _user.set(user);
            ContextedPermission.init(user);
        }

        if (user == null) {
            Logger.debug("[OperateRbac]: user is not authenticated");
            // we put into cache the url we come from
            Cache.add("url_" + session.getId(), request.method == "GET" ? request.url : "/", "10min");

            redirect("/admin/login");
        }

        // 得到当前菜单的名字
        String currentMenuName = getCurrentMenuName();

        String applicationName = Play.configuration.getProperty("application.name");
        NavigationHandler.initContextMenu(applicationName, currentMenuName, user);
        renderArgs.put("topMenus", NavigationHandler.getTopMenus());
        renderArgs.put("secondLevelMenu", NavigationHandler.getSecondLevelMenus());

        // 检查权限
        checkRight(currentMenuName);

        long endLoadCurrentMenus = System.currentTimeMillis();
        Logger.debug("OperateRbac.loadCurrentMenu spent %d ms", (endLoadCurrentMenus - beginLoadCurrentMenus));
    }

    private static void injectDefaultMenus() {
        for(String menuName : Play.configuration.getProperty("navigation.defaultMenus", "main").toString().split(",")) {
            if(!StringUtils.isBlank(menuName)) {
                renderArgs.put(menuName + "Menu", NavigationHandler.getMenu(StringUtils.trim(menuName)));
            }
        }
    }

    @Finally
    public static void cleanPermission() {
        ContextedPermission.clean();
        _user.set(null);
    }

    public static OperateUser currentUser() {
        return _user.get();
    }


    /**
     * 得到当前菜单的名字。
     * 从Controller或method上检查 {@link ActiveNavigation} 标注，取其value为当前菜单名。
     * 优先使用Method上的名字，只能有一个值。
     * @return
     */
    private static String getCurrentMenuName() {
        ActiveNavigation methodNavigation = getActionAnnotation(ActiveNavigation.class);
        String currentMenuName = null;
        if (methodNavigation != null) {
            currentMenuName = methodNavigation.value();
        } else {
            ActiveNavigation controllerNavigation = getControllerAnnotation(ActiveNavigation.class);
            if (controllerNavigation != null) {
                currentMenuName = controllerNavigation.value();
            }
        }
        return currentMenuName;
    }

    private static void checkRight(String currentMenuName) {
        Right methodRightAnnotation = getActionAnnotation(Right.class);
        String[] rights = null;
        if (methodRightAnnotation != null) {
            rights = methodRightAnnotation.value();
        } else {
            Right controllerRightAnnotation = getControllerAnnotation(Right.class);
            if (controllerRightAnnotation != null) {
                rights = controllerRightAnnotation.value();
            }
        }

        Set<String> rightSet = new HashSet<>();
        if (rights != null) {
            for (String r : rights) {
                rightSet.add(r);
            }
        }

        OperateNavigation currentNavigation = OperateNavigation.findByName(currentMenuName);
        // 把当前菜单上的权限也作为检查点，这样一个方法只需要指定@ActiveNavigation，就不需要再指定@Right了
        if (currentNavigation != null && currentNavigation.permissions != null) {
            for (OperatePermission perm : currentNavigation.permissions) {
                Logger.debug(" 当前菜单(" + currentMenuName + ")的权限是：" + perm.key);
                rightSet.add(perm.key);
            }
        }

        Logger.debug("-------------> current permission = " + ContextedPermission.getAllowPermissions() + ", url=" + Request.current().url);
        if (ContextedPermission.getAllowPermissions() != null) {
            for (String s : ContextedPermission.getAllowPermissions()) {
                Logger.debug("   perm:" + s);
            }
        }
        for (String r : rightSet) {
            Logger.debug("   right:" + r);
        }

        if (rightSet.size() > 0) {
            boolean hasRight = ContextedPermission.hasPermissionKeys(rightSet);
            if (!hasRight) {
                String message = "没有权限访问！";
                if (currentNavigation != null) {
                    message = "没有权限访问 <strong>" + currentNavigation.text + "</strong> 功能。";
                }
                renderTemplate("Defaults/index.html", message);
            }
        } // else 如果没有加上Right标注，不检查权限
    }

    @After
    public static void cleanCacheHelper() {
        CacheHelper.cleanPreRead();
    }
}
