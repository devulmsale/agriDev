package controllers.wap;

import ext.pay.weixin.v3.WxpayFactory;
import me.chanjar.weixin.common.util.StringUtils;
import models.base.WeixinUser;
import models.common.enums.OrderType;
import models.member.MemberCard;
import models.order.Order;
import models.order.OrderItem;
import models.order.User;
import models.product.Product;
import order.OrderBuilder;
import play.Logger;
import play.mvc.Controller;

import java.math.BigDecimal;
import java.util.*;

/**
 * 微网站订单页.
// */
public class OrderController extends Controller {

    public static WxpayFactory wxpayFactory = WxpayFactory.getDefaultInstance();



    public static void pay(Long productId , Integer number) {
        User user = User.all().first();
        Product product = Product.findById(productId);
        //生成订单 并初初始化订单
        OrderBuilder orderBuilder = OrderBuilder.forBuild().byUser(user).type(OrderType.PC);
        Order order = orderBuilder.save();  //生成订单号
        // 添加 orderItems 信息  每一个产品一个 orderItems . 或者每一个购物车 一个 OrderItems
        orderBuilder.addProduct(product)
                .originalPrice(BigDecimal.ONE)
                .salePrice(product.salePrice)
                .buyNumber(number)
                .build()
                .changeToUnPaid();

        List<OrderItem> orderItems = OrderItem.getListByOrder(order);
        render(order, orderItems);
    }



    public static void cartToPay(String carts) {
        User user = User.all().first();
        Order order = null;
        if(StringUtils.isNotBlank(carts) && carts.indexOf("_") > 0) {
            //生成订单 并初初始化订单
            OrderBuilder orderBuilder = OrderBuilder.forBuild().byUser(user).type(OrderType.PC);
            order = orderBuilder.save();  //生成订单号
            cartToOrder(orderBuilder , carts);
        }
        List<OrderItem> orderItems = OrderItem.getListByOrder(order);
        render(order, orderItems);
    }

    /**
     *
     * @param orderBuilder
     * @param carts
     */
    private static void cartToOrder(OrderBuilder orderBuilder , String carts) {
        String[] cartArray = carts.split(",");
        for(String cartStr : cartArray) {
            String[] productArray = cartStr.split("_");
            Long productId = Long.valueOf(productArray[0]);
            Integer number = Integer.valueOf(productArray[1]);
            Product product = Product.findById(productId);
            if(product != null) {
                orderBuilder.addProduct(product)
                        .originalPrice(BigDecimal.ONE)
                        .salePrice(product.salePrice)
                        .buyNumber(number)
                        .build()
                        .changeToUnPaid();
            }
        }
    }

}
