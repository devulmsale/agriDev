package controllers.wap;

import me.chanjar.weixin.common.util.StringUtils;
import models.common.enums.OrderType;
import models.constants.DeletedStatus;
import models.order.Cart;
import models.order.Order;
import models.order.OrderItem;
import models.order.User;
import models.product.Product;
import order.OrderBuilder;
import play.Logger;
import play.mvc.Controller;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 购物车.
 */
public class CartController extends Controller {

    public static void cart(Long productId , Integer number) {
        Logger.info("cart  productId : %s | number : %s" , productId , number);
        User user = User.all().first();
        Product product = Product.findById(productId);
        Cart cart =new Cart();
        cart.goods=product.findOrCreateGoods();
        cart.number=number;
        cart.deleted=DeletedStatus.UN_DELETED;
        cart.createdAt = new Date();
        cart.save();
        //有属性的存储到cartLable属性表中
        Logger.info("加入购物车成功！");
        redirect("/showCart");
    }

    public static void showCart(Long productId) {
        Cart cart = Cart.all().first();
        render(cart);
    }

    public static void shoppingCart(Cart cart){
        //展示此用户购物车
        List <Cart> cartList=Cart.findCartsByUser();
        render(cartList);

    }



}
