package ext.weixin;

import cache.CacheHelper;
import me.chanjar.weixin.common.bean.WxAccessToken;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;

import java.io.Serializable;

/**
 * 可缓存的微信配置provider.
 */
public class CacheableWxMpConfigStorage implements WxMpConfigStorage, Serializable {

    private static final long serialVersionUID = 1036774298888477723L;

    private static final ThreadLocal<String> oauthUrlLocal = new ThreadLocal<>();

    protected volatile String appId;
    protected volatile String secret;
    protected volatile String token;
    protected volatile String aesKey;

    protected volatile String http_proxy_host;
    protected volatile int    http_proxy_port;
    protected volatile String http_proxy_username;
    protected volatile String http_proxy_password;

    private String getAccessTokenCacheKey() {
        return "WxMpAccessToken_" + appId;
    }

    public String getAccessToken() {
        return (String) CacheHelper.getCache(getAccessTokenCacheKey());
    }

    public boolean isAccessTokenExpired() {
        return !CacheHelper.exists(getAccessTokenCacheKey());
    }

    public void expireAccessToken() {
        CacheHelper.delete(getAccessTokenCacheKey());
    }


    public synchronized void updateAccessToken(WxAccessToken accessToken) {
        updateAccessToken(accessToken.getAccessToken(), accessToken.getExpiresIn());
    }

    public synchronized void updateAccessToken(String accessToken, int expiresInSeconds) {
        CacheHelper.setCache(getAccessTokenCacheKey(), accessToken, (expiresInSeconds - 200) + "s");
    }

    private String getJsapiTicketCacheKey() {
        return "WxMpJsapiTicket_" + appId;
    }

    public String getJsapiTicket() {
        return (String) CacheHelper.getCache(getJsapiTicketCacheKey());
    }

    public boolean isJsapiTicketExpired() {
        return !CacheHelper.exists(getJsapiTicketCacheKey());
    }

    public synchronized void updateJsapiTicket(String jsapiTicket, int expiresInSeconds) {
        CacheHelper.setCache(getJsapiTicketCacheKey(), jsapiTicket, (expiresInSeconds - 200) + "s");
    }

    public void expireJsapiTicket() {
        CacheHelper.delete(getJsapiTicketCacheKey());
    }

    public String getAppId() {
        return this.appId;
    }

    public String getSecret() {
        return this.secret;
    }

    public String getToken() {
        return this.token;
    }

    public long getExpiresTime() {
        return 0;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAesKey() {
        return aesKey;
    }

    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }

    public void setAccessToken(String accessToken) {
        updateAccessToken(accessToken, 7200);
    }

    public void setExpiresTime(long expiresTime) {

    }

    @Override
    public String getOauth2redirectUri() {
        return oauthUrlLocal.get();
    }

    public static void setOauth2redirectUri(String oauth2redirectUri) {
        oauthUrlLocal.set(oauth2redirectUri);
    }

    public String getHttp_proxy_host() {
        return http_proxy_host;
    }

    public void setHttp_proxy_host(String http_proxy_host) {
        this.http_proxy_host = http_proxy_host;
    }

    public int getHttp_proxy_port() {
        return http_proxy_port;
    }

    public void setHttp_proxy_port(int http_proxy_port) {
        this.http_proxy_port = http_proxy_port;
    }

    public String getHttp_proxy_username() {
        return http_proxy_username;
    }

    public void setHttp_proxy_username(String http_proxy_username) {
        this.http_proxy_username = http_proxy_username;
    }

    public String getHttp_proxy_password() {
        return http_proxy_password;
    }

    public void setHttp_proxy_password(String http_proxy_password) {
        this.http_proxy_password = http_proxy_password;
    }

    @Override
    public String toString() {
        return "CacheableWxMpConfigStorage{" +
                "appId='" + appId + '\'' +
                ", secret='" + secret + '\'' +
                ", token='" + token + '\'' +
                ", aesKey='" + aesKey + '\'' +
                ", http_proxy_host='" + http_proxy_host + '\'' +
                ", http_proxy_port=" + http_proxy_port +
                ", http_proxy_username='" + http_proxy_username + '\'' +
                ", http_proxy_password='" + http_proxy_password + '\'' +
                '}';
    }

}
