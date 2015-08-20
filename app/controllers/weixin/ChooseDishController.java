package controllers.weixin;

import controllers.auth.WxMpAuth;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import me.chanjar.weixin.common.util.StringUtils;
import models.common.DateUtil;
import models.common.enums.OrderGoodsType;
import models.common.enums.OrderStatus;
import models.common.enums.OrderType;
import models.constants.DeletedStatus;
import models.coupon.Coupon;
import models.coupon.CouponBatch;
import models.mert.Merchant;
import models.mert.MerchantProductType;
import models.mert.hall.HallTable;
import models.mert.hall.MerchantHall;
import models.order.Order;
import models.order.OrderItem;
import models.order.OrderUser;
import models.order.User;
import models.product.Product;
import models.product.ProductImage;
import models.vo.GoodsTypeVO;
import models.vo.OrderItemVO;
import models.vo.OrderVO;
import order.OrderBuilder;
import play.Logger;
import play.modules.redis.Redis;
import play.mvc.Controller;
import play.mvc.With;
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
        //Merchant merchant = WxMpAuth.currentUser().merchant; //TODO 12L修改成merchant.id

        //  Logger.info("products 获取到的商户号 : %s ----" , merchant.id);
//        Merchant merchant = Merchant.findByLinkId("kehao");
        // Logger.info("products 获取到的商户号 : %s ----" , merchant.id);
        Map<String , List<Product>> productMap = new HashMap<>();
        List<Product> imgUrlList=new ArrayList<>();
        //根据商户查询商户商品类别
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
                    String responseBody = images(productImage.uFid, "490", "290");
                    pro.url = responseBody;
                }
                imgUrlList.add(pro);
                Logger.info("imgUrlList :%s",imgUrlList.size());
            }
            productMap.put(mpt.id.toString(), productList);
        }
        String uuid = RandomNumberUtil.generateRandomNumberString(16);

        Logger.info("获取商户商品类别 :%s=",merchantProductTypeList.size());
        //TODO 发布时去掉render的注释
        render(merchantProductTypeList, productMap ,goodsType , uuid , imgUrlList /*, merchant*/);
    }



    public static void confirms(String carts , OrderGoodsType goodsType , String uuid) {
        Logger.info("微信端选择商品数量:%s=",carts);
        Logger.info("点餐类型:%s",goodsType);
        Logger.info("获取到的UUID  : %s -----" , uuid);
        goodsType = goodsType == null ? OrderGoodsType.DOT_FOOD : goodsType;
        //TODO 发布时去掉user的注释
        //User user = WxMpAuth.currentUser().user;
        Order order = Order.findByUuid(uuid);
        if(order != null) {
            Logger.info("order不为空");
            List<OrderItem> orderItems = OrderItem.getListByOrder(order);
            for(OrderItem orderItem : orderItems){
                orderItem.deleted = DeletedStatus.DELETED;
                orderItem.salePrice=BigDecimal.ZERO;
                orderItem.save();
                Logger.info("删除orderItem");
            }
            if (StringUtils.isNotBlank(carts) && carts.indexOf("_") > 0) {
                Logger.info("order2:%s",order.orderNumber);
                OrderBuilder orderBuilder = OrderBuilder.orderNumber(order.orderNumber);
                cartToOrder(orderBuilder, carts);
            }
        }

        if(order == null) {
            Logger.info("order 为空！");
            if (StringUtils.isNotBlank(carts) && carts.indexOf("_") > 0) {
                //TODO 发布时改成注释的orderBuilder增加user
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
    public static void detail(String orderNumber , String useCoupon , OrderGoodsType goodsType){
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
        //User user = WxMpAuth.currentUser().user; TODO 获取用户user_id
        // 查询出所有可用的优惠券
        List<Coupon> userAllCanUsedList=Coupon.findCouponByLoginUser(2l);
        // 定义一个返回的优惠券
        List<Coupon> couponList = new ArrayList<>();
        // 循环优惠券.  查看 是否在 redis 中存在. 即 该优惠券 是否已经绑定其他订单. 如果绑定 则前台不再显示该优惠券
        for(Coupon coupon : userAllCanUsedList) {
            String redisCouponId = Redis.get(coupon.getRedisLockKey());
            // redis 中 不存在.   添加到 返回页面的 couponList 中
            if(StringUtils.isBlank(redisCouponId)) {
                couponList.add(coupon);
            }
        }
        Logger.info("用户卡券数量 :%s",couponList.size());
        render(orderNumber, tableMap , order , useCoupon , couponList , goods);
    }

    public static void pay(String orderNumber, OrderUser orderUser ,  String useCoupon , String couponIds , String date ,String time ,OrderGoodsType goodsType) throws Exception{
        // User user = WxMpAuth.currentUser().user;
        //保存orderUser
        Order order = Order.findByOrderNumber(orderNumber);
        String goods=goodsType.toString();
        // TODO 上线后 判断 订单用户跟登录用户是否一致  if(order != null && order.user == user) {
        if(order != null) {
            if(StringUtils.isNotBlank(date) && StringUtils.isNotBlank(time)) {
                String dateTimes = date + " " + time;
                orderUser.time = DateUtil.stringToDate(dateTimes, "yyyy-MM-dd HH:mm");
            }

            if(goodsType.toString().equals("DOT_FOOD") || goodsType.toString().equals("BOOK_FOOD")){
                orderUser.merchantHall = orderUser.hallTable.hall;
            }
            orderUser.deleted = DeletedStatus.UN_DELETED;
            orderUser.createdAt = new Date();
            orderUser.order = order;
            orderUser.save();
        }
        Logger.info("orderNumber :%s=",orderNumber);
        // 订单总金额
        BigDecimal orderAmount = order.amount;
        // 卡券支付金额
        BigDecimal couponPirce = BigDecimal.ZERO;
        BigDecimal needPay = BigDecimal.ZERO;
        if(StringUtils.isNotBlank(couponIds)){
            String[] coupons = couponIds.split(",");
            for(String couponId : coupons) {
                Coupon coupon = Coupon.findById(Long.valueOf(couponId));
                String redisCouponId = Redis.get(coupon.getRedisLockKey());
                // 证明这张会员卡 在 redis中存在 . 也就是说 这样优惠券 正在被别的订单所使用
                if(StringUtils.isNotBlank(redisCouponId)) {
                    flash.put("error" , "券号:"+ coupon.couponNumber + "被其他订单使用.");
                    detail(orderNumber, useCoupon, goodsType);
                } else {
                    CouponBatch couponBatch = CouponBatch.findById(Long.valueOf(coupon.couponBatch.id));
                    couponPirce = couponPirce.add(couponBatch.costPrice);
                    Redis.setex(coupon.getRedisLockKey(), 15 * 60, couponId);
                }
            }
            Logger.info("orderAmount :%s",orderAmount);
            needPay = orderAmount.subtract(couponPirce);
            Logger.info("needPay11 :%s",needPay);
            if(needPay.compareTo(BigDecimal.ZERO) < 1) {
                needPay = BigDecimal.ZERO;
            }
            Logger.info("needPay22 :%s",needPay);
            // 订单绑定优惠券 放到 redis 中
            Redis.setex(Order.ORDDR_LOCK_COUPON_IDS + order.id, 15 * 60, couponIds);
        }
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
