package helper;

import play.Play;

/**
 * 全局配置变量.
 */
public class GlobalConfig {

    /**
     * 微信WAP网站、请求的主域名.
     */
    public final static String WEIXIN_BASE_DOMAIN = Play.configuration.getProperty("weixin.basedomain", "http://wx.ps321.net");

    public final static String WEIXIN_MP_SESSION_USER_KEY = "wxmp_user_id";

    public final static String BAIDU_API_AK = "9fb983ecd9b505f8fedcc9ab07c65e3e";

}
