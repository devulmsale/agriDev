package controllers.weixin;

import controllers.auth.WxMpAuth;
import jodd.http.HttpRequest;
import me.chanjar.weixin.common.util.StringUtils;
import models.common.enums.OrderStatus;
import models.common.enums.OrderType;
import models.constants.DeletedStatus;
import models.coupon.CouponBatch;
import models.mert.Merchant;
import models.mert.MerchantProductType;
import models.mert.hall.HallTable;
import models.mert.hall.MerchantHall;
import models.order.*;
import models.product.Product;
import order.OrderBuilder;
import play.Logger;
import play.data.validation.Valid;
import play.mvc.Controller;
import play.mvc.With;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

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


    public static void confirms(String carts) {
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
        //render(order, orderItems);
        redirect("/weixin/confirm?orderNumber="+order.orderNumber);
    }

    public static void confirm(String orderNumber){
        Logger.info("confirm--orderNumber %s==",orderNumber);
        Order order = Order.findByOrderNumber(orderNumber);
        List<OrderItem> orderItems = OrderItem.getListByOrder(order);
        render(order,orderItems);
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
                        .salePrice(product.salePrice.multiply(new BigDecimal(number)))
                                .buyNumber(number)
                                .build()
                                .changeToUnPaid();
            }
        }
    }
    public static void detail(String orderNumber , String useCoupon)
    {
        Logger.info("orderNumber :%s || useCoupon :%s",orderNumber , useCoupon);
        //取大厅，桌号
        //TODO 取商户Id
        List<HallTable> hallTableList=HallTable.findByMerchant(21L);
        Map<MerchantHall,List<HallTable>> tableMap=new HashMap<MerchantHall,List<HallTable>>();
        for(HallTable ht:hallTableList){
            List<HallTable> tableList=new ArrayList<>();
            if(tableMap.get(ht.hall)==null){
                tableList.add(ht);
                tableMap.put(ht.hall,tableList);
            }else{
                tableList= tableMap.get(ht.hall);
                tableList.add(ht);
                tableMap.put(ht.hall, tableList);
            }
        }
        Order order=Order.findByOrderNumber(orderNumber);
        render(orderNumber, tableMap , order , useCoupon);
    }

    public static void pay(String orderNumber, OrderUser orderUser ,  String useCoupon) throws Exception{
        /*if(validation.hasErrors()) {
            params.flash(); // add http parameters to the flash scope
            validation.keep(); // keep the errors for the next request
            Logger.info("校验失败！");
            detail(orderNumber, useCoupon);
        }*/
        // User user = WxMpAuth.currentUser().user;
        //保存orderUser
        Order order = Order.findByOrderNumber(orderNumber);
        Logger.info("orderUser :%s=",orderUser.hallTable.hall);
        // TODO 上线后 判断 订单用户跟登录用户是否一致  if(order != null && order.user == user) {
        if(order != null) {
            orderUser.deleted = DeletedStatus.UN_DELETED;
            orderUser.createdAt = new Date();
            orderUser.order = order;
            orderUser.merchantHall=orderUser.hallTable.hall;
            orderUser.save();
        }
        Logger.info("orderNumber :%s=",orderNumber);
        render(orderNumber,order);
    }

    //删除订单
    public static void deleteOrder(String orderNumber){
        Logger.info("删除订单 :%s=",orderNumber);
        Order order = Order.findByOrderNumber(orderNumber);
        order.deleted=DeletedStatus.DELETED;
        order.save();
        redirect("/weixin/products");
    }

    //取消订单
    public static void cancelOrder(String orderNumber){
        Logger.info("取消订单 orderNumber :%s=",orderNumber);
        Order order=Order.findByOrderNumber(orderNumber);
        order.status= OrderStatus.CANCELED;
        order.save();
        redirect("/weixin/products");
    }

    public static void member(){
        render();
    }

}
