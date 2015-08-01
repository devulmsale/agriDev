package ext.pay.weixin.v3;

import ext.pay.weixin.v3.reqs.GetBrandWCPayRequest;
import ext.pay.weixin.v3.reqs.JSAPIUnifiedOrder;
import ext.pay.weixin.v3.reqs.UnifiedOrder;

import java.util.Iterator;
import java.util.Properties;

/**
 * 创建微信业务的工厂类
 */
public class WxpayFactory {
    // CONSTANT
    private static final String APP_ID     = "wxde945ba07ea568ad";
    private static final String APP_SECRET = "43fbd254daec35a8c62fdb54e69271e0";
    private static final String PAY_KEY    = "819765a0d0489cb1d58354388c8a2b0c"; //支付密钥
    //private static final String MCH_ID = "1223805701";
    private static final String MCH_ID     = "10031272";
    private static final String NOTIFY_URL = "http://wxtest.ps321.net/pay/notify/weixin";

    // CONFIG
    protected Properties conf;

    public Properties getConf() {
        return (this.conf);
    }

    // CONSTRUCT

    /**
     * Construct a new instance with blank config.
     */
    public WxpayFactory() {
        this.conf = new Properties();
        this.conf.put("appid", APP_ID);
        this.conf.put("SECRET", APP_SECRET);
        this.conf.put("mch_id", MCH_ID);
        this.conf.put("KEY", PAY_KEY);
        this.conf.put("notify_url", NOTIFY_URL);
    }

    // SINGLETON
    private static class Singleton {
        public static final WxpayFactory instance = new WxpayFactory();
    }

    /**
     * return default instance which load config from <code>/wxpay.properties</code>.
     * If you are binding multi-instance of WxpayFactory in your application, DO NOT use this method.
     */
    public static WxpayFactory getDefaultInstance() {
        return (Singleton.instance);
    }

    /**
     * @deprecated Please use <code>getDefaultInstance()</code> instead.
     * This method now forwarded to <code>getDefaultInstance()</code>
     */
    public static WxpayFactory getInstance() {
        return (
                getDefaultInstance()
        );
    }

    // FACTORY
    public UnifiedOrder newUnifiedOrder() {
        return (
                new UnifiedOrder(
                        new Properties(this.conf)
                ));
    }

    public UnifiedOrder newUnifiedOrder(Properties p) {
        return (
                new UnifiedOrder(
                        buildConf(p, this.conf)
                ));
    }

    public JSAPIUnifiedOrder newJSAPIUnifiedOrder() {
        return (
                new JSAPIUnifiedOrder(
                        new Properties(this.conf)
                ));
    }

    public JSAPIUnifiedOrder newJSAPIUnifiedOrder(Properties p) {
        return (
                new JSAPIUnifiedOrder(
                        buildConf(p, this.conf)
                ));
    }

    public GetBrandWCPayRequest newGetBrandWCPayRequest() {
        return (
                new GetBrandWCPayRequest(
                        new Properties(this.conf)
                ));
    }

    public GetBrandWCPayRequest newGetBrandWCPayRequest(Properties p) {
        return (
                new GetBrandWCPayRequest(
                        buildConf(p, this.conf)
                ));
    }

    // MISC
    protected static Properties buildConf(Properties prop, Properties defaults) {
        Properties ret = new Properties(defaults);
        Iterator<String> iter = prop.stringPropertyNames().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            ret.setProperty(key, prop.getProperty(key));
        }

        return (ret);
    }
}
