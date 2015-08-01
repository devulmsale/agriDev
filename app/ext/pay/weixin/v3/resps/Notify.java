package ext.pay.weixin.v3.resps;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class Notify extends ResponseBase {
    // CONSTANTS
    //protected Boolean validity = null;

    public static final List<String> KEYS_PARAM_NAME = Arrays.asList(
            "appid",
            "attach",
            "bank_type",
            "cash_fee",
            "coupon_fee",
            "device_info",
            "err_code",
            "err_code_des",
            "fee_type",
            "is_subscribe",
            "mch_id",
            "nonce_str",
            "openid",
            "out_trade_no",
            "result_code",
            "return_code",
            "return_msg",
            "time_end",
            "total_fee",
            "trade_type",
            "transaction_id"
    );


    // CONSTRUCT
    public Notify(ResponseBase resp) {
        this(resp.respString, resp.respProp);
    }

    public Notify(String respString, Properties respProp) {
        super(respString, respProp);
    }

    // VERIFY
    @Override
    protected boolean verifySign(Properties conf)
            throws UnsupportedEncodingException {
        return (
                this.verifySign(KEYS_PARAM_NAME, conf)
        );
    }


    // GET
    public String getProperty(String key) {
        return (
                this.respProp.getProperty(key)
        );
    }

    // PROPERTY

}
