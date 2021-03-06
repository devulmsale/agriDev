package controllers.weixin.userCenter;

import controllers.auth.WxMpAuth;
import models.base.WeixinUser;
import models.coupon.Coupon;
import models.order.Order;
import models.order.OrderItem;
import models.order.User;
import play.Logger;
import play.mvc.Controller;
import play.mvc.With;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/19.
 */
@With(WxMpAuth.class)
public class OrderController extends Controller {

    /**
     *
     * @param type //type为如果执行了订单删除，则type为：“delete” 否责为null
     */
    public static void index(String type) {
       WeixinUser wxUser = WxMpAuth.currentUser();
        List<Order> orderUnpaidList=Order.getUserUnPayOrders(wxUser.user);
        List<Order> orderPaidList=Order.getUserPaidOrders(wxUser.user);


        if(type!=null&&type.equals("delete")){
            flash.put("type", "true");
        }
        render(orderUnpaidList,orderPaidList);
    }

    public static void deleted(String orderNum) {
        Order order = Order.findByOrderNumber(orderNum);
        if(order != null) {
            Order.delete(order.id);
            index("delete");
        }
        index(null);
    }

    public  static void detail(String orderNumber){
        Order order=Order.findByOrderNumber(orderNumber);
        List<OrderItem> orderItemList = OrderItem.getListByOrder(order);
        Map<String , Integer> couponMap  = Coupon.findByOrderRetrunNaneAndCount(order);
        render(order, orderItemList, couponMap);
    }
}
