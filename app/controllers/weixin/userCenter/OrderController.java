package controllers.weixin.userCenter;

import models.order.Order;
import models.order.User;
import play.Logger;
import play.mvc.Controller;

import java.util.List;

/**
 * Created by Administrator on 2015/8/19.
 */
public class OrderController extends Controller {

    /**
     *
     * @param type //type为如果执行了订单删除，则type为：“delete” 否责为null
     */
    public static void index(String type) {
        //TODO 暂时取到userId
        User user=User.findById(2l);
        List<Order> orderUnpaidList=Order.getUserUnPayOrders(user);
        List<Order> orderPaidList=Order.getUserPaidOrders(user);


        if(type!=null&&type.equals("delete")){
            flash.put("type" , "true");
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
}
