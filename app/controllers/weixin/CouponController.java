package controllers.weixin;

import controllers.auth.WxMpAuth;
import me.chanjar.weixin.common.util.StringUtils;
import models.common.enums.OrderGoodsType;
import models.common.enums.OrderType;
import models.coupon.Coupon;
import models.coupon.CouponBatch;
import models.order.Cart;
import models.order.Order;
import models.order.OrderItem;
import models.order.User;
import models.product.Product;
import order.OrderBuilder;
import play.Logger;
import play.modules.redis.Redis;
import play.mvc.Controller;
import play.mvc.With;
import util.common.RandomNumberUtil;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by upshan on 15/8/11.
 */
@With(WxMpAuth.class)
public class CouponController extends Controller {

    public static final String COUPON_BING_ID = "coupon_bind_id_";


    public static void show(Long batchId) {
        CouponBatch batch = CouponBatch.findById(batchId);
        render(batch);
    }


    /**
     * 确定购买卡券
     * @param batchId
     * @param number
     */
    public static void createpay(Long batchId , Integer number) {
        Logger.info("卡券数量size :%s=",number);
        CouponBatch batch = CouponBatch.findById(batchId);
        User user = WxMpAuth.currentUser().user;
        OrderBuilder orderBuilder = OrderBuilder.forBuild().byUser(user).type(OrderType.PC).goodsType(OrderGoodsType.COUPON);
        Order order = orderBuilder.save();  //生成订单号
        // 第一  数据库中 有没有 没有被绑定的卡券
        // 第二  没有被绑定的卡券 我要判断下 是否在 redis 中被锁定
            for(int i = 0 ; i < number ; i++) {
                Coupon cn = new Coupon(Coupon.getCouponNumber(batch.merchant.id) , batch);
                cn.couponBatch.surplusCount -=1;
                cn.couponBatch.save();
                orderBuilder.addGoods(cn.findOrCreateGoods())
                        .originalPrice(cn.couponBatch.costPrice)
                        .salePrice(cn.couponBatch.salePrice)
                        .buyNumber(1)
                        .build()
                        .changeToUnPaid();
            }
        redirect("/order/qrCode?orderNumber="+order.orderNumber);
    }

    public static void pay(Long batchId , Integer number) {
        //TODO 查询此用户的订单
        Order order= Order.all().first();
        render(order);
    }

    public static void card(){
        List<CouponBatch> couponBatchList = CouponBatch.findAll();
        String uuid = RandomNumberUtil.generateRandomNumberString(16);
        render(couponBatchList ,uuid);
    }

    public static void showCouponsBefore(String carts, String uuid) {
        Order order=null;
        if (StringUtils.isNotBlank(carts) && carts.indexOf("_") > 0) {
            User user = WxMpAuth.currentUser().user;
            OrderBuilder orderBuilder = OrderBuilder.forBuild().byUser(user).type(OrderType.PC).goodsType(OrderGoodsType.COUPON);
//            OrderBuilder orderBuilder = OrderBuilder.forBuild().type(OrderType.PC).goodsType(OrderGoodsType.COUPON).uuid(uuid);
            order = orderBuilder.save();  //生成订单号
            cartToOrder(orderBuilder, carts);

        }
        List<OrderItem> orderItems = OrderItem.getListByOrder(order);
       // render(order , orderItems);
        redirect("/weixin/coupon/showCoupons?orderNumber="+order.orderNumber);
    }

    public static void showCoupons(String orderNumber){
        Order order=Order.findByOrderNumber(orderNumber);
        List<OrderItem> orderItems = OrderItem.getListByOrder(order);
        render(order , orderItems);
    }

    private static void cartToOrder(OrderBuilder orderBuilder , String carts) {
        Logger.info("cartToOrder %s==",carts);
        String[] cartArray = carts.split(",");
        for(String cartStr : cartArray) {
            String[] cart_Num_Array = cartStr.split("_");
            Long batchId = Long.valueOf(cart_Num_Array[0]);
            Integer number = Integer.valueOf(cart_Num_Array[1]);
            CouponBatch batch = CouponBatch.findById(batchId);
            if(batch != null) {
            for(int i = 0 ; i < number ; i++) {
                Coupon cn = new Coupon(Coupon.getCouponNumber(batch.merchant.id) , batch);
                cn.couponBatch.surplusCount -=1;
                cn.couponBatch.save();
                orderBuilder.addGoods(cn.findOrCreateGoods())
                        .originalPrice(cn.couponBatch.costPrice)
                        .salePrice(cn.couponBatch.salePrice)
                        .buyNumber(1)
                        .build()
                        .changeToUnPaid();
            }
            }
        }
    }

    public static void couponsPay(String orderNumber){
        Order order=Order.findByOrderNumber(orderNumber);
        render(order);
    }

}
