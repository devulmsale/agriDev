package controllers.api;

import ext.pay.weixin.v3.WxpayFactory;
import ext.pay.weixin.v3.resps.Notify;
import helper.WxMpHelper;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.WxMpCustomMessage;
import models.base.WeixinUser;
import models.common.enums.OrderGoodsType;
import models.common.enums.OrderStatus;
import models.coupon.Coupon;
import models.order.Order;
import models.order.OrderItem;
import order.OrderBuilder;
import org.apache.commons.lang.StringUtils;
import play.Logger;
import play.libs.IO;
import play.modules.redis.Redis;
import play.mvc.Controller;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static ext.pay.weixin.v3.util.XMLParser.parseXML;

/**
 * 卡券支付回调.
 */
public class CouponPayCallback extends Controller {

    public static void execute(String orderNumber) {
                Order order = Order.findByOrderNumber(orderNumber);
                OrderBuilder.orderNumber(orderNumber).changeToPaid();
                // 根据 订单产品类型. 修改响应产品 状态
                changeOrderItemByGoodsType(order);
                Logger.info("OrderNumber: {} 状态改为Paid", orderNumber);
                WeixinUser wxUser = WeixinUser.findByUser(order.user);
                Logger.info("获取 Order.User : %s  | wxUser : %s ----", order.user, wxUser);
    }


    private static void changeOrderItemByGoodsType(Order order) {
        // 如果是优惠券  需要把 优惠券绑定到 用户身上
        if(order.goodsType == OrderGoodsType.COUPON) {
            List<OrderItem> orderItemList = OrderItem.getListByOrder(order);
            for(OrderItem orderItem : orderItemList) {
                String goodsName = orderItem.goods.serial;
                String couponId = goodsName.substring(7, goodsName.length());
                Coupon coupon = Coupon.findById(Long.valueOf(couponId));
                if(coupon != null) {
                    coupon.user = order.user;
                    coupon.bindUserAt = new Date();
                    coupon.save();
                }
            }
        }

        // 如果优惠券绑定订单. 那么 优惠券跟 order 绑定
        String couponIds = Redis.get(Order.ORDDR_LOCK_COUPON_IDS + order.orderNumber);
        if(StringUtils.isNotBlank(couponIds)) {
            String[] idArray = couponIds.split(",");
            for(String id : idArray) {
                Coupon coupon = Coupon.findById(Long.valueOf(id));
                if(coupon != null) {
                    coupon.order = order;
                    coupon.bindUserAt = new Date();
                    coupon.save();
                }
            }
        }
        Redis.del(new String[]{Order.ORDDR_LOCK_COUPON_IDS + order.orderNumber});
    }

}
