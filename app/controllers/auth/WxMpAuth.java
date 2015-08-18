package controllers.auth;

import cache.CacheCallBack;
import cache.CacheHelper;
import ext.weixin.CacheableWxMpConfigStorage;
import helper.GlobalConfig;
import helper.WxMpHelper;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import models.base.WeixinUser;
import models.common.DateUtil;
import models.mert.Merchant;
import org.apache.commons.lang.StringUtils;
import play.Logger;
import play.cache.Cache;
import play.mvc.After;
import play.mvc.Before;
import play.mvc.Controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 微信公众号认证，基于OAuth.
 */
public class WxMpAuth extends Controller {


    private final static ThreadLocal<WeixinUser> _currentUser = new ThreadLocal<>();

    /**
     * 检查是否已经OAuth认证过，如果没有，则调用认证.
     *
     * @throws Throwable
     */
    @Before(unless = {"login", "logout", "fail", "validate", "validation", "authenticate", "captcha"})
    public static void filter() throws Throwable {
        Logger.info("[Auth]: Filter for URL -> " + request.url);
        // 设置JSAPI
        UseJsApi annoUseJsAPI = getUseJsAPI();
        if (annoUseJsAPI != null) {
            String linkId = params.get("linkId");
            Logger.info("try params linkId=" + linkId);
            WxMpConfigStorage wxMpConfigStorage = WxMpHelper.getWxMpConfigStorage(linkId);
            if (wxMpConfigStorage == null) {
                Logger.info("找不到" + linkId + "对应的CorpApp");
                renderText("不存在的应用地址，请检查域名是否正确.");
                return;
            }

            WxMpService wxCpService = WxMpHelper.getWxMpService(wxMpConfigStorage);
            String jsapiTicket = wxCpService.getJsapiTicket(false);
            String currentUrl = "http://" + request.host + request.url;
            WxJsapiSignature jsSignature = wxCpService.createJsapiSignature(currentUrl);
            Logger.info("url=" + currentUrl + ", jsapiTicket=" + jsapiTicket);
            List<String> jsApis = Arrays.asList(annoUseJsAPI.value());
            renderArgs.put("jsSignature", jsSignature);
            renderArgs.put("jsApis", jsApis);
            renderArgs.put("jsAppId", wxMpConfigStorage.getAppId());
        }

        if (skipAuthCheck()) {
            Logger.info("[Auth]: Skip the Auth Check.");
            return;
        }

        WeixinUser user = getUserFromSession();
        Logger.info("Secure.LoginUser : %s  ====== ", user);

        if (user == null) {
            String linkId = params.get("linkId");
            WxMpConfigStorage wxCpConfigStorage = WxMpHelper.getWxMpConfigStorage(linkId);
            if (wxCpConfigStorage == null) {
                Logger.info("找不到" + linkId + "对应的CorpApp");
                renderText("不存在的应用地址，请检查域名是否正确.");
                return;
            }
            Merchant merchant = Merchant.findByLinkId(linkId);
            Logger.info("merchant ---- %s | linkId : %s | merchant.isAuth : %s" , merchant , linkId , merchant.isAuth);
            Logger.info("执行 WxMpAuth 开始 : %s " , DateUtil.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
            if(merchant.isAuth != null && merchant.isAuth) {
                WxMpService wxCpService = WxMpHelper.getWxMpService(wxCpConfigStorage);
                //是否有code&state参数？如果有，就是重定向回来的，否则就是进行重定向到微信OAuth地址.
                String code = params.get("code");
                String state = params.get("state");
                Logger.info("   -----> code=%s, state=%s, accessToken=%s", code, state, wxCpService.getAccessToken());
                if (StringUtils.isBlank(code) && !"STATE2015".equals(state)) {
                    // 重定向到OAuth认证地址.
                    String targetUrl = "http://" + request.host + request.url;
                    Logger.info("targetUrl -> %s", targetUrl);
                    CacheableWxMpConfigStorage.setOauth2redirectUri(targetUrl);
                    String redirectUrl = wxCpService.oauth2buildAuthorizationUrl("snsapi_userinfo", "STATE2015");
                    Logger.info("[Auth]: oauth2.redirectUrl -> %s", redirectUrl);
                    redirect(redirectUrl);
                } else {
                    // 获取用户信息，并检查数据库中是否存在，如果不存在，就自动创建.
                    WxMpOAuth2AccessToken wxMpOAuth2AccessToken = wxCpService.oauth2getAccessToken(code);
                    WxMpUser wxMpUser = wxCpService.oauth2getUserInfo(wxMpOAuth2AccessToken, "zh_CN");
                    Logger.info("通过微信接口读取用户信息，以在数据库中建立或更新用户.");
                    user = WxMpHelper.createOrUpdateUserFromWxMpUser(merchant, wxMpUser);
                }
            }
        }
        Logger.info("执行 WxMpAuth 结束 : %s " , DateUtil.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
        if (user != null) {
            session.put(GlobalConfig.WEIXIN_MP_SESSION_USER_KEY, user.id);
            renderArgs.put("currentUser", user);
            _currentUser.set(user);
        }
    }

    @After
    public static void cleanCurrentUser() {
        _currentUser.remove();
    }

    /**
     * 在Controller中使用WexinCpAuth可以得到当前用户.
     */
    public static WeixinUser currentUser() {
        return _currentUser.get();
    }

    /**
     * 尝试从Session中得到用户信息.
     */
    private static WeixinUser getUserFromSession() {
        WeixinUser user = null;
        String sessionUserId = session.get(GlobalConfig.WEIXIN_MP_SESSION_USER_KEY);
        if(StringUtils.isBlank(sessionUserId)) {
            Object cacheUserId = Cache.get(GlobalConfig.WEIXIN_MP_SESSION_USER_KEY);
            Logger.info("cacheUserId  : %s --------------" , cacheUserId);
            if(cacheUserId != null) {
                sessionUserId = cacheUserId.toString();
            }
        }
        Logger.info("sessionUserId=" + sessionUserId);
        if (StringUtils.isNotBlank(sessionUserId)) {
            final Long userId = Long.parseLong(sessionUserId);
            user = CacheHelper.getCache("WxMpUser2_" + sessionUserId, new CacheCallBack<WeixinUser>() {
                @Override
                public WeixinUser loadData() {
                    WeixinUser weixinUser = WeixinUser.findById(userId);
                    Logger.info("load user info=" + weixinUser.user);
                    return weixinUser;
                }
            });
            if (user == null) {
                Logger.info("user is null or not actived user=" + user);
                session.remove(GlobalConfig.WEIXIN_MP_SESSION_USER_KEY);
                return null;
            }
        }

        if (user != null) {
            session.put(GlobalConfig.WEIXIN_MP_SESSION_USER_KEY, user.id);
        }

        Logger.info("user=" + user);
        return user;
    }

    /**
     * 检查是否要跳过认证检查.
     *
     * @return 返回true表明跳过检查.
     */
    private static boolean skipAuthCheck() {
        if (getActionAnnotation(SkipAuth.class) != null ||
                getControllerInheritedAnnotation(SkipAuth.class) != null) {
            Logger.info("skipAuthCheck=true");
            return true;
        }
        Logger.info("skipAuthCheck=false");
        return false;
    }

    /**
     * 检查是否要使用JSAPI.
     *
     * 在类名或方法上使用@UseJsAPI标注，会在renderArgs中加入一个jsSignature的对象.
     *
     * @return UseJsAPI，其value指定需要的权限.
     */
    private static UseJsApi getUseJsAPI() {
        if (getActionAnnotation(UseJsApi.class) != null) {
            Logger.info("UseJsAPI on Action");
            return getActionAnnotation(UseJsApi.class);
        }
        if (getControllerInheritedAnnotation(UseJsApi.class) != null) {
            Logger.info("UseJsAPI on Controller");
            return getControllerInheritedAnnotation(UseJsApi.class);
        }
        Logger.info("UseJsAPI=false");
        return null;
    }

}
