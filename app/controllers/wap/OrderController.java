package controllers.wap;

import ext.pay.weixin.v3.WxpayFactory;
import me.chanjar.weixin.common.util.StringUtils;
import models.base.WeixinUser;
import models.common.enums.OrderType;
import models.member.MemberCard;
import models.order.Cart;
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

    public static void pay(Long productId , Integer number) {
       // Logger.info("productId : %s | number : %s" , productId , number);
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
        Logger.info("goodsIds :%s =",carts);
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
        Logger.info("cartToOrder %s==",carts);
        String[] cartArray = carts.split(",");
        for(String cartStr : cartArray) {
            String[] cart_Num_Array = cartStr.split("_");
            Long cartId = Long.valueOf(cart_Num_Array[0]);
            Integer number = Integer.valueOf(cart_Num_Array[1]);
            Cart cart = Cart.findById(cartId);
            if(cart != null) {
                orderBuilder.addGoods(cart.goods)
                        .originalPrice(BigDecimal.ONE)
                        .salePrice(cart.goods.salePrice.multiply(new BigDecimal(number)))
                        .buyNumber(number)
                        .build()
                        .changeToUnPaid();
            }
        }
    }

}
