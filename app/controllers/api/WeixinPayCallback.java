package controllers.api;

import ext.pay.weixin.v3.WxpayFactory;
import ext.pay.weixin.v3.resps.Notify;
import helper.WxMpHelper;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.WxMpCustomMessage;
import models.base.WeixinUser;
import models.common.enums.OrderStatus;
import models.order.Order;
import order.OrderBuilder;
import org.apache.commons.lang.StringUtils;
import play.Logger;
import play.libs.IO;
import play.mvc.Controller;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import static ext.pay.weixin.v3.util.XMLParser.parseXML;

/**
 * 微信支付回调.
 */
public class WeixinPayCallback extends Controller {

    public static void execute() {
        String xml = IO.readContentAsString(request.body);
        Logger.info("WeixinPayCallback.execute: xml=" + xml);
        Properties parsedProp = parseXML(xml);
        Logger.info("out_trade_no=" + parsedProp.get("out_trade_no"));
        Notify notify = new Notify(null, parsedProp);
        try {
            boolean verified = notify.verify(WxpayFactory.getDefaultInstance().getConf());
            Logger.info("verify notify = " + verified);
            if (verified) {
                Logger.info("notify.get out_trade_no = " + notify.getProperty("out_trade_no"));
                String orderNumber = notify.getProperty("out_trade_no");
                Logger.info("orderNumber=(" + orderNumber + ")");
                if (StringUtils.isBlank(orderNumber)) {
                    Logger.info("order is empty");
                    renderText("<xml><return_code>FAIL</return_code><return_msg>ORDER_NUMBER_EMPTY</return_msg></xml>");
                }
                Order order = Order.findByOrderNumber(orderNumber);
                if (order == null) {
                    Logger.info("order not exists");
                    renderText("<xml><return_code>FAIL</return_code><return_msg>ORDER_NOT_EXISTS</return_msg></xml>");
                }

                if (order.status == OrderStatus.PAID) {
                    Logger.info("OrderNumber: {} 状态为已经支付", orderNumber);
                    renderText("<xml><return_code>SUCCESS</return_code></xml>");
                }

                OrderBuilder.orderNumber(orderNumber).changeToPaid();
                Logger.info("OrderNumber: {} 状态改为Paid", orderNumber);
                WeixinUser wxUser = WeixinUser.findByUser(order.user);
                Logger.info("获取 Order.User : %s  | wxUser : %s ----" , order.user , wxUser);
                // 如果关注公众号  推广信息
                if(wxUser != null && wxUser.subcribed) {

                    //发送支付成功通知
                    WxMpConfigStorage wxMpConfigStorage = WxMpHelper.getWxMpConfigStorage(wxUser.merchant);
                    WxMpService wxMpService = WxMpHelper.getWxMpService(wxMpConfigStorage);
                    WxMpCustomMessage.WxArticle article1 = new WxMpCustomMessage.WxArticle();
                    String currentUrl = "http://" + request.host + "/orders/reservePay/" + orderNumber + "/youliang";
                    article1.setUrl(currentUrl);
                    article1.setPicUrl("http://yijiejingwu.tunnel.mobi/public/images/huangjinli.jpg");
                    article1.setDescription("扫码支付成功!点击图片查看商品详情!");
                    article1.setTitle("扫码支付成功");
//
                    WxMpCustomMessage message = WxMpCustomMessage.NEWS()
                            .toUser(wxUser.weixinOpenId)
                            .addArticle(article1)
                            .build();

//                    WxMpTemplateMessage templateMessage = new WxMpTemplateMessage();
//                    templateMessage.setToUser(wxUser.weixinOpenId);
//                    templateMessage.setTemplateId("orderPayOk_"+orderNumber);
//                    templateMessage.setUrl("http://" + request.host + "/orders/reservePay/" + orderNumber);
                    try {
//                        wxMpService.templateSend(templateMessage);
                        wxMpService.customMessageSend(message);
                    } catch (WxErrorException e) {
                        Logger.info("微信通知发送失败");
                        e.printStackTrace();
                    }
                }
                renderText("<xml><return_code>SUCCESS</return_code></xml>");
            } else {
                Logger.info("OrderNumber: {} 验证失败", parsedProp.get("out_trade_no"));
                renderText("<xml><return_code>FAIL</return_code><return_msg>BAD_SIGN</return_msg></xml>");
            }
        } catch (UnsupportedEncodingException e) {
            Logger.info("出错了", e);
        }
        Logger.info("OrderNumber: {} 处理异常", parsedProp.get("out_trade_no"));
        renderText("<xml><return_code>FAIL</return_code><return_msg>ERROR</return_msg></xml>");
    }

}
