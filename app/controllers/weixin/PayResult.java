package controllers.weixin;

import ext.pay.weixin.MD5Util;
import ext.pay.weixin.v3.WxpayFactory;
import ext.pay.weixin.v3.constants.TradeType;
import ext.pay.weixin.v3.reqs.GetBrandWCPayRequest;
import ext.pay.weixin.v3.reqs.UnifiedOrder;
import ext.pay.weixin.v3.resps.UnifiedOrderResponse;
import me.chanjar.weixin.common.exception.WxErrorException;
import models.base.WeixinUser;
import models.mert.Merchant;
import models.order.Goods;
import models.order.Order;
import models.order.OrderItem;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import play.Logger;
import play.libs.IO;
import play.libs.XML;
import play.libs.XPath;
import play.mvc.Controller;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * 微网站订单页.
 // */
public class PayResult extends Controller {

    public static WxpayFactory wxpayFactory = WxpayFactory.getDefaultInstance();


    public static void payProductResult() throws WxErrorException {
        Properties conf = wxpayFactory.getConf();
        String restXml = IO.readContentAsString(request.body);
        Logger.info("restXML=%s", restXml);
        Document document = XML.getDocument(restXml);
        Node messageNode = XPath.selectNode("xml", document);
        String appId = XPath.selectText("appid", messageNode);
        String openid = XPath.selectText("openid", messageNode);
        String mch_id = XPath.selectText("mch_id", messageNode);
        String is_subscribe = XPath.selectText("is_subscribe", messageNode);
        String nonce_str = XPath.selectText("nonce_str", messageNode);
        String product_id = XPath.selectText("product_id", messageNode);
        String sign = XPath.selectText("sign", messageNode);

        Order order = Order.findByOrderNumber(product_id);
        Merchant merchant = Merchant.findByLinkId("ulm");
        WeixinUser wxUser =  WeixinUser.findOrCreateMerchantWxUser(merchant, openid);
        OrderItem orderItem = OrderItem.getByOrder(order);
        GetBrandWCPayRequest gbpq = createJsAPI(order , openid , orderItem.goods);
        if(gbpq != null) {
            String prepay_id = gbpq.getProperty(GetBrandWCPayRequest.KEY_PREPAY_ID);
            Logger.info("prepay_id : %s --- | key_id : %s " , prepay_id , gbpq.getProperty(GetBrandWCPayRequest.KEY_PACKAGE));
            StringBuilder xmlBulid = new StringBuilder();
            xmlBulid.append("<xml>");
            xmlBulid.append("<return_code><![CDATA[SUCCESS]]></return_code>");
            xmlBulid.append("<appid><![CDATA[" + appId + "]]></appid>");
            xmlBulid.append("<mch_id><![CDATA[" + mch_id + "]]></mch_id>");
            xmlBulid.append("<nonce_str><![CDATA[" + nonce_str + "]]></nonce_str>");
            xmlBulid.append("<sign><![CDATA[" + getSign(appId, mch_id, conf.getProperty("KEY"), nonce_str, prepay_id) + "]]></sign>");
            xmlBulid.append("<result_code><![CDATA[SUCCESS]]></result_code>");
            xmlBulid.append("<prepay_id><![CDATA["+prepay_id+"]]></prepay_id>");
            xmlBulid.append("</xml>");
            Logger.info("xmlBuild = %s ---------", xmlBulid.toString());

            renderXml(xmlBulid.toString());
        }

//        render(jsPackage , orderData);
    }



    private static GetBrandWCPayRequest createJsAPI(Order orderData , String openId , Goods goods) {
        String jsPackage = null;
        try {
            Double orderPrice = orderData.amount.doubleValue() * 100;
            String remoteAddr = request.remoteAddress.split(",")[0];
            UnifiedOrder unifiedWxOrder = wxpayFactory.newUnifiedOrder()
                    .setBody("产品名称 : "+ goods.name)
                    .setTotalFee(1) // TODO 暂时定支付价格为1
                    .setOpenid(openId)
                    .setOutTradeNo(orderData.orderNumber)
                    .setSpbillCreateIp(remoteAddr)
                    .setTradeType(TradeType.NATIVE)
                    .build()
                    .sign();
            UnifiedOrderResponse orderResp = unifiedWxOrder.execute();

            Logger.info("Weixin orderResp=" + orderResp.getProperties());
            return wxpayFactory.newGetBrandWCPayRequest(orderResp.getProperties())
                    .build()
                    .sign();
//            jsPackage = gbwxpr.toJSON();
//            Logger.info("jsPackage=" + jsPackage);
        } catch (UnsupportedEncodingException e) {
            Logger.error("UnsupportedEncodingException", e);
        }


        if (StringUtils.isEmpty(jsPackage)) {
            Logger.info("生成jsPackage失败.");
        }

        return null;
    }


    public static String getSign(String appid , String mchId ,String key , String nonce_str , String prepay_id) {
        StringBuffer signBuild = new StringBuffer();
        signBuild.append("appid="+appid);
        signBuild.append("&mch_id="+mchId);
        signBuild.append("&nonce_str="+ nonce_str);
        signBuild.append("&prepay_id="+ prepay_id);
        signBuild.append("&result_code=SUCCESS");
        signBuild.append("&return_code=SUCCESS");
        signBuild.append("&key="+key);
        return  MD5Util.md5(signBuild.toString()).toUpperCase();
    }


    public static void show(String orderNumber) {
        Order orderData = Order.findByOrderNumber(orderNumber);
        render(orderData);
    }
}
