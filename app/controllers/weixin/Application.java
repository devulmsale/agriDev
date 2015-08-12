package controllers.weixin;

import controllers.auth.WxMpAuth;
import jodd.http.HttpRequest;
import me.chanjar.weixin.common.util.StringUtils;
import models.common.enums.OrderType;
import models.coupon.CouponBatch;
import models.mert.Merchant;
import models.mert.MerchantProductType;
import models.order.Cart;
import models.order.Order;
import models.order.OrderItem;
import models.order.User;
import models.product.Product;
import order.OrderBuilder;
import play.Logger;
import play.mvc.Controller;
import play.mvc.With;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by upshan on 15/8/5.
 */
//@With(WxMpAuth.class)
public class Application extends Controller {

    public static void index() {
//        Merchant merchant = WxMpAuth.currentUser().merchant;
        List<CouponBatch> couponBatchList = CouponBatch.findAll();
        render(couponBatchList);
    }

    public static void products() {
        //查询商户商品的类别  TODO 获取商户号
        //Merchant merchant = WxMpAuth.currentUser().merchant;
        Map<String , List<Product>> productMap = new HashMap<>();
        List<MerchantProductType> merchantProductTypeList=MerchantProductType.findMerchantProductType(12L);
        for(MerchantProductType mpt : merchantProductTypeList) {
            List<Product> productList = Product.findProductByMerIdAndMerProductType(mpt.id);
            Logger.info(" id = %s  |  productList : %s" ,mpt.id.toString() , productList.size());
            productMap.put(mpt.id.toString() , productList);
        }

        Logger.info("获取商户商品类别 :%s=",merchantProductTypeList.size());

        render(merchantProductTypeList , productMap);
    }

    //根据类别Id取商户商品
    public static void getProduct(Long productTypeId){
        List<Product> productList =Product.findProductByMerIdAndMerProductType(productTypeId);
        render(productList);
    }

    public static void confirm(String carts) {
        Logger.info("微信端选择商品数量:%s=",carts);
        //TODO 需要修改用户
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
        render();
    }

    private static void cartToOrder(OrderBuilder orderBuilder , String carts) {
        Logger.info("cartToOrder %s==",carts);
        String[] cartArray = carts.split(",");
        for(String cartStr : cartArray) {
            String[] cart_Num_Array = cartStr.split("_");
            Long ProductId = Long.valueOf(cart_Num_Array[0]);
            Integer number = Integer.valueOf(cart_Num_Array[1]);
           // Cart cart = Cart.findById(cartId);
            Product product=Product.findById(ProductId);
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
    public static void detail(String orderNumber)
    {
        render(orderNumber);
    }

    public static void pay(String orderNumber) throws Exception{
        Logger.info("orderNumber :%s=",orderNumber);
       // OrderController.qrCode(orderNumber);
        render(orderNumber);
    }

}
