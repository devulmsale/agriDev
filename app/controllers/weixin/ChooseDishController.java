package controllers.weixin;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import me.chanjar.weixin.common.util.StringUtils;
import models.common.enums.OrderGoodsType;
import models.common.enums.OrderStatus;
import models.common.enums.OrderType;
import models.constants.DeletedStatus;
import models.coupon.Coupon;
import models.coupon.CouponBatch;
import models.mert.MerchantProductType;
import models.mert.hall.HallTable;
import models.mert.hall.MerchantHall;
import models.order.Order;
import models.order.OrderItem;
import models.order.OrderUser;
import models.product.Product;
import models.product.ProductImage;
import models.vo.GoodsTypeVO;
import models.vo.OrderItemVO;
import models.vo.OrderVO;
import order.OrderBuilder;
import play.Logger;
import play.mvc.Controller;
import util.common.RandomNumberUtil;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by upshan on 15/8/5.
 */
//@With(WxMpAuth.class)
public class ChooseDishController extends Controller {
    private static final String IMG_URL="http://img.ulmsale.cn/getImageUrl";

    public static void products(OrderGoodsType goodsType) {
        Logger.info("OrderGoodsType :%s",goodsType);
       //Merchant merchant = WxMpAuth.currentUser().merchant;
       // Logger.info("products 获取到的商户号 : %s ----" , merchant.id);
       // Merchant merchant = WxMpAuth.currentUser().merchant;
      //  Merchant merchant = Merchant.findByLinkId("kehao");
       // Logger.info("products 获取到的商户号 : %s ----" , merchant.id);
        Map<String , List<Product>> productMap = new HashMap<>();
        //根据商户查询商户商品类别
        //查询商户商品的类别  TODO 获取商户号 merchant.id
        List<MerchantProductType> merchantProductTypeList=MerchantProductType.findMerchantProductType(12l);
        for(MerchantProductType mpt : merchantProductTypeList) {
            //根据商户商品类别查询商品
            List<Product> productList = Product.findProductByMerIdAndMerProductType(mpt.id);
            Logger.info(" id = %s  |  productList : %s" ,mpt.id.toString() , productList.size());
           //根据商品Id查询图片
            for(Product pro:productList){
                Logger.info("商品Id :%s",pro.id);
                ProductImage productImage=ProductImage.findProductImage(pro.id);
                //TODO 商品图片宽高需要获取
                if(null != productImage) {
                    String imageUrl = images(productImage.uFid, "490", "290");
                    pro.url = imageUrl;
                }
            }
            productMap.put(mpt.id.toString(), productList);
        }
        String uuid = RandomNumberUtil.generateRandomNumberString(16);

        Logger.info("获取商户商品类别 :%s=",merchantProductTypeList.size());
        //TODO render 中传入merchant 为页面获取linkId
        render(merchantProductTypeList, productMap ,goodsType , uuid  );
    }



    public static void confirms(String carts , OrderGoodsType goodsType , String uuid) {
        Logger.info("微信端选择商品数量:%s=",carts);
        Logger.info("点餐类型:%s",goodsType);
        Logger.info("获取到的UUID  : %s -----" , uuid);
        goodsType = goodsType == null ? OrderGoodsType.DOT_FOOD : goodsType;
       // User user = WxMpAuth.currentUser().user;


        Order order = Order.findByUuid(uuid);
        if(order != null) {
            List<OrderItem> orderItems = OrderItem.getListByOrder(order);
            for(OrderItem orderItem : orderItems){
                orderItem.deleted = DeletedStatus.DELETED;
                orderItem.save();
            }
            if (StringUtils.isNotBlank(carts) && carts.indexOf("_") > 0) {
                OrderBuilder orderBuilder = OrderBuilder.orderNumber(order.orderNumber);
                cartToOrder(orderBuilder, carts);
            }
        }

        if(order == null) {
            if (StringUtils.isNotBlank(carts) && carts.indexOf("_") > 0) {
                //生成订单 并初初始化订单 TODO 发布时需要改成注释的orderBuilder,添加user
                //OrderBuilder orderBuilder = OrderBuilder.forBuild().byUser(user).type(OrderType.PC).goodsType(goodsType).uuid(uuid);
                OrderBuilder orderBuilder = OrderBuilder.forBuild().type(OrderType.PC).goodsType(goodsType).uuid(uuid);
                order = orderBuilder.save();  //生成订单号
                cartToOrder(orderBuilder, carts);
            }
        }
        redirect("/weixin/confirm?orderNumber=" + order.orderNumber + "&goodsType=" + goodsType);
    }

    public static void confirm(String orderNumber ,OrderGoodsType goodsType){
        Logger.info("confirm--orderNumber:%s==",orderNumber);
        Logger.info("confirm--goodsType:%s",goodsType);
        List<OrderItem> imgOrderItemList=new ArrayList<>();
        Order order = Order.findByOrderNumber(orderNumber);
        List<OrderItem> orderItems = OrderItem.getListByOrder(order);
        //TODO 获取图片 图片宽度和高度需要修改
        for(OrderItem oi : orderItems) {
            String goodsSerial=oi.goods.serial;
            String[] serial = goodsSerial.split("_");
            Long productId = Long.valueOf(serial[1]);
            ProductImage productImage = ProductImage.findProductImage(productId);
            if(null != productImage) {
                String responseBody = images(productImage.uFid, "490", "290");
                oi.url=responseBody;
            }
            imgOrderItemList.add(oi);
        }

        render(order, imgOrderItemList, goodsType);
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
    public static void detail(String orderNumber , String useCoupon , OrderGoodsType goodsType)
    {
        Logger.info("detail OrderGoodsType:%s==",goodsType);
        String goods=goodsType.toString();
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
        //根据用户查询用户购买的优惠券，判断用户购买的优惠券未绑定订单
        // User user = WxMpAuth.currentUser().user;TODO 上线时需要获取微信用户
        List<Coupon> couponList=Coupon.findCouponByLoginUser(2l);
        Logger.info("用户卡券数量 :%s",couponList.size());
        render(orderNumber, tableMap , order , useCoupon , couponList , goods);
    }

    public static void pay(String orderNumber, OrderUser orderUser ,  String useCoupon , String couponIds , String date ,String time ,OrderGoodsType goodsType) throws Exception{
        /*if(validation.hasErrors()) {
            params.flash(); // add http parameters to the flash scope
            validation.keep(); // keep the errors for the next request
            Logger.info("校验失败！");
            detail(orderNumber, useCoupon);
        }*/
        // User user = WxMpAuth.currentUser().user;
        //保存orderUser
        Order order = Order.findByOrderNumber(orderNumber);
        // TODO 上线后 判断 订单用户跟登录用户是否一致  if(order != null && order.user == user) {
        if(order != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            if(null != date && null != time) {
                String dt = date + " " + time;
                Date date1 = new Date();
                date1 = formatter.parse(dt);
                orderUser.time=date1;
            }
            if(goodsType.toString().equals("DOT_FOOD") || goodsType.toString().equals("BOOK_FOOD")){
                orderUser.merchantHall=orderUser.hallTable.hall;
            }
            orderUser.deleted = DeletedStatus.UN_DELETED;
            orderUser.createdAt = new Date();
            orderUser.order = order;
            orderUser.save();
        }
        Logger.info("orderNumber :%s=",orderNumber);
        BigDecimal orderAmount=order.amount;
        Logger.info("couponIds:%s=",couponIds);
        BigDecimal couponPirce= BigDecimal.valueOf(0.0);
        BigDecimal needPay=BigDecimal.valueOf(0.0);
        Coupon coupon=null;
        if(null != couponIds && !couponIds.equals("")){
            String [] coupons=couponIds.split(",");
            for(String couponId :coupons) {
                coupon = Coupon.findById(Long.valueOf(couponId));
                CouponBatch couponBatch= CouponBatch.findById(Long.valueOf(coupon.couponBatch.id));
                couponPirce= couponPirce.add(couponBatch.costPrice);
            }
            needPay=orderAmount.subtract(couponPirce);
            int pay =needPay.compareTo(BigDecimal.ZERO);
            if(pay ==0 || pay == -1){
                needPay=BigDecimal.ZERO;
            }else if(pay == 1){
                needPay=needPay;
            }
            Logger.info("needPay :%s",needPay);
        }
        String goods=goodsType.toString();
        render(orderNumber, order, needPay, useCoupon , goods);
    }

    //删除订单
    public static void deleteOrder(String orderNumber){
        Logger.info("删除订单 :%s=",orderNumber);
        Order order = Order.findByOrderNumber(orderNumber);
        if(order != null) {
            Order.delete(order.id);
        }
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






    public static String images(String ufid,String width ,String height){
        HttpRequest httpRequest = HttpRequest
                .get(IMG_URL)
                .form(
                        "uFid", ufid,
                        "width", width,
                        "height", height
                );
        HttpResponse httpResponse = httpRequest.send();
        String responseBody = httpResponse.body();
        Logger.info("responseBody==="+responseBody);
        return responseBody;
    }

    //根据uuid获取orderItem商品信息
    public static void getOrderItembyAjax(String uuid){
        Logger.info("执行 getOrderItembyAjax  -> uuid : %s" , uuid);
        Order order = Order.findByUuid(uuid);
        OrderVO orderVO = new OrderVO();
        if(order != null) {
            List<OrderItemVO> orderItemVOs = new ArrayList<>();
            List<GoodsTypeVO> goodsTypeVOList = new ArrayList<>();
            List<OrderItem> orderItems = OrderItem.getListByOrder(order);
            OrderItemVO itemVO = null;
            GoodsTypeVO goodsTypeVO = null;
            for (OrderItem orderItem : orderItems) {
                itemVO = new OrderItemVO();
                goodsTypeVO = new GoodsTypeVO();
                Boolean isHaveGoodsType = false;
                itemVO.number = orderItem.buyNumber;
                // PRODUCT_12  PRODUCT_15
                itemVO.productId = orderItem.goods.serial.replace("PRODUCT_", "").trim();
                Logger.info("商品id:%s",orderItem.goods.serial.replace("PRODUCT_", "").trim());
                Product product = Product.findById(Long.valueOf(itemVO.productId));
                itemVO.price = orderItem.goods.salePrice;


                goodsTypeVO.goodsTypeId = product.merchantProductType.id.toString();
                Logger.info("goodsTypeVOs.size() : %s" , goodsTypeVOList.size());
                for(int i = 0 ; i < goodsTypeVOList.size() ; i++) {
                    GoodsTypeVO typevo = goodsTypeVOList.get(i);
                    if (typevo.goodsTypeId.equals(goodsTypeVO.goodsTypeId)) {
                        isHaveGoodsType = true;
                        goodsTypeVO = typevo;
                        goodsTypeVOList.remove(typevo);
                    }
                }

                if (isHaveGoodsType) {
                    goodsTypeVO.number += 1;
                } else {
                    goodsTypeVO.number = 1;
                }

                goodsTypeVOList.add(goodsTypeVO);
                orderItemVOs.add(itemVO);
            }
            orderVO.success = true;
            orderVO.orderItems = orderItemVOs;
            orderVO.goodsTypeVOs = goodsTypeVOList;
            orderVO.price = order.amount;
        } else {
            orderVO.success = false;
        }
        renderJSON(orderVO);
    }

}
