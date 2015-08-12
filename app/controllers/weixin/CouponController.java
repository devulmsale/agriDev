package controllers.weixin;

import me.chanjar.weixin.common.util.StringUtils;
import models.common.enums.OrderType;
import models.coupon.Coupon;
import models.coupon.CouponBatch;
import models.order.Cart;
import models.order.Order;
import models.order.User;
import order.OrderBuilder;
import play.Logger;
import play.modules.redis.Redis;
import play.mvc.Controller;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by upshan on 15/8/11.
 */
public class CouponController extends Controller {

    public static final String COUPON_BING_ID = "coupon_bind_id_";


    public static void show(Long batchId) {
        CouponBatch batch = CouponBatch.findById(batchId);
        render(batch);
        //redirect("/showCoupon");
    }

    public static void showCoupon(Long batchId) {
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
        //TODO 用户需要改成 登录用户
        User user = User.all().first();
        OrderBuilder orderBuilder = OrderBuilder.forBuild().byUser(user).type(OrderType.PC);
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

}