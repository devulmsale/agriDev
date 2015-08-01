package ext.pay.weixin.v3.reqs;

import ext.pay.weixin.v3.constants.TradeType;

import java.util.Properties;

public class JSAPIUnifiedOrder extends UnifiedOrder {

    public JSAPIUnifiedOrder(Properties prop) {
        super(prop);
        this.setTradeType(TradeType.JSAPI);
    }
}
