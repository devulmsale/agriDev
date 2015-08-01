package ext.pay.weixin.v3.reqs;


import com.google.gson.Gson;
import ext.pay.weixin.v3.resps.ResponseBase;
import play.Logger;

import java.io.UnsupportedEncodingException;
import java.util.*;


public class GetBrandWCPayRequest extends RequestBase {
    public static final String KEY_GBWCPR_APP_ID    = "appId";
    public static final String KEY_GBWCPR_NONCE_STR = "nonceStr";
    public static final String KEY_PAY_SIGN         = "paySign";
    public static final String KEY_TIME_STAMP       = "timeStamp";
    public static final String KEY_SIGN_TYPE        = "signType";
    public static final String KEY_PREPAY_ID        = "prepay_id";
    public static final String KEY_PACKAGE          = "package";

    // KEYS
    public static final List<String> KEYS_PARAM_NAME = Arrays.asList(
            "appId",
            "nonceStr",
            "package",
            "signType",
            "timeStamp"
    );

    // CONSTRUCT
    public GetBrandWCPayRequest(Properties prop) {
        super(prop);

        if (this.getProperty(KEY_GBWCPR_APP_ID) == null) {
            this.setAppid(
                    this.getProperty(KEY_APPID)
            );
        }

        if (this.getProperty(KEY_GBWCPR_NONCE_STR) == null) {
            this.setNonceStr(
                    this.getProperty(KEY_NONCE_STR)
            );
        }

        if (this.getProperty(KEY_PACKAGE) == null) {
            this.setPackage(
                    this.getProperty(KEY_PREPAY_ID)
            );
        }

        this.setTimeStamp(new Date());
        this.setSignType("MD5");

        return;
    }

    // BUILD
    @Override
    public GetBrandWCPayRequest build() {
        return (this);
    }

    // SIGN
    @Override
    public GetBrandWCPayRequest sign()
            throws UnsupportedEncodingException {
        this.sign(KEYS_PARAM_NAME);

        this.setProperty(
                KEY_PAY_SIGN,
                this.getProperty(KEY_SIGN)
        );

        return (this);
    }

    // TO_URL

    /**
     * NOTE This method return javascript source, NOT URL
     */
    @Override
    public String toURL()
            throws UnsupportedOperationException {
        return (this.toJSON());
    }

    public String toJSON() {
        Map<String, String> jsonMap = new HashMap<>();

        for (String key : KEYS_PARAM_NAME) {
            jsonMap.put(
                    key,
                    this.getProperty(key)
            );
        }

        jsonMap.put(KEY_PAY_SIGN, this.getProperty(KEY_PAY_SIGN));

        String jsonResult = new Gson().toJson(jsonMap);
        Logger.info("GetBrandWCPayRequest.jsonResult=" + jsonResult);
        return jsonResult;
    }

    // EXECUTE
    @Override
    public ResponseBase execute() {
        throw (
                new UnsupportedOperationException("This request does not execute on server side")
        );
    }

    // PROPERTY

    /**
     * Overrided method for key changing
     */
    @Override
    public GetBrandWCPayRequest setAppid(String appid) {
        this.setProperty(KEY_GBWCPR_APP_ID, appid);

        return (this);
    }

    /**
     * 商户注册具有支付权限的公众号成功后即可获得
     */
    public GetBrandWCPayRequest setAppId(String appid) {
        this.setAppid(appid);

        return (this);
    }

    /**
     * 商户生成的随机字符串
     * Overrided method for key changing
     */
    @Override
    public RequestBase setNonceStr(String nonceStr) {
        this.setProperty(KEY_GBWCPR_NONCE_STR, nonceStr);

        return (this);
    }

    /**
     * 商户生成，从 1970 年 1 月 1 日 00：00：00 至今的秒数
     */
    public RequestBase setTimeStamp(Date timeStamp) {
        this.setProperty(
                KEY_TIME_STAMP,
                Long.toString(timeStamp.getTime() / 1000L)
        );

        return (this);
    }

    /**
     * 必须是"MD5" o(*≧▽≦)ツ
     * Overrided method for key changing
     */
    public RequestBase setSignType(String signType) {
        this.setProperty(KEY_SIGN_TYPE, signType);

        return (this);
    }

    /**
     * 统一支付接口返回的 prepay_id 参数值
     *
     * @param paxkage prepay_id, part after the equal
     */
    public RequestBase setPackage(String paxkage) {
        this.setProperty(KEY_PACKAGE, "prepay_id=" + paxkage);

        return (this);
    }


}
