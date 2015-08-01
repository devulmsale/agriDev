package helper;

import cache.CacheCallBack;
import cache.CacheHelper;
import ext.weixin.CacheableWxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import models.base.WeixinUser;
import models.base.enums.Gender;
import models.mert.Merchant;
import play.Logger;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 微信公众号工具类.
 */
public class WxMpHelper {

    public final static Map<Long, WxMpInMemoryConfigStorage> WX_CP_CONFIG_STORAGE_MAP = new ConcurrentHashMap<>();

    /**
     * 通过CorpApp得到微信配置.
     * <p/>
     * TODO: 这个方法应当基于缓存实现，但是WxMpInMemoryConfigStorage目前不能放Memcached，目前配置成只使用内存Map缓存.
     */
    public static WxMpConfigStorage getWxMpConfigStorage(final Merchant merchant) {

        return CacheHelper.getCache("WX_MP_CONFIG_STORAGE_" + merchant.id, new CacheCallBack<WxMpConfigStorage>() {
            @Override public WxMpConfigStorage loadData() {
                CacheableWxMpConfigStorage wxMpConfigStorage = new CacheableWxMpConfigStorage();
                wxMpConfigStorage.setAppId(merchant.weixinAppId);     // 设置微信公众号的appid
                wxMpConfigStorage.setSecret(merchant.weixinAppSecret);    // 设置微信公众号的app corpSecret
                wxMpConfigStorage.setToken(merchant.weixinToken);       // 设置微信公众号的token
                wxMpConfigStorage.setAesKey(merchant.weixinAesKey);      // 设置微信公众号的EncodingAESKey
                return wxMpConfigStorage;
            }
        });
    }

    /**
     * 通过请求的hostname直接得到微信配置信息.
     */
    public static WxMpConfigStorage getWxMpConfigStorage(final String linkId) {
        Merchant merchant = Merchant.findByLinkId(linkId);
        if (merchant == null) {
            Logger.info("找不到" + linkId + "对应的商户");
            return null;
        }
        return getWxMpConfigStorage(merchant);
    }

    /**
     * 通过ConfigStore得到微信服务接口.
     */
    public static WxMpService getWxMpService(WxMpConfigStorage wxMpConfigStorage) {
        WxMpServiceImpl wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(wxMpConfigStorage);
        return wxMpService;
    }

    /**
     * 转换wxMpUser为User.
     */
    public static WeixinUser createOrUpdateUserFromWxMpUser(Merchant merchant, WxMpUser wxMpUser) throws Exception {
        WeixinUser user = WeixinUser.findOrCreateMerchantWxUser(merchant, wxMpUser.getOpenId());
        user.nickName = wxMpUser.getNickname();
        user.headImgUrl = wxMpUser.getHeadImgUrl();
        user.city = wxMpUser.getCity();
        user.country = wxMpUser.getCountry();
        user.language = wxMpUser.getLanguage();
        if ("1".equals(wxMpUser.getSex())) {
            user.sex = Gender.MALE;
        } else if ("2".equals(wxMpUser.getSex())) {
            user.sex = Gender.FEMALE;
        } else {
            user.sex = Gender.UNKNOWN;
        }
        user.subcribed = wxMpUser.isSubscribe();
        user.subscribedAt = new Date(); // new Time(wxMpUser.getSubscribeTime()); //CHECK:是不是要乘以1000？
        user.save();

        return user;
    }

}
