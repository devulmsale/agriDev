package controllers.weixin;

import controllers.auth.WxMpAuth;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import models.base.WeixinUser;
import models.common.enums.OrderGoodsType;
import models.common.enums.OrderType;
import models.mert.Merchant;
import models.order.Order;
import models.order.OrderItem;
import models.product.ProductSetMeal;
import models.product.SetMeal;
import models.product.SetMealPic;
import order.OrderBuilder;
import play.Logger;
import play.mvc.Controller;
import play.mvc.With;
import util.common.RandomNumberUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/17.
 */
@With(WxMpAuth.class)
public class SetMealController extends Controller {
    private static final String IMG_URL="http://img.ulmsale.cn/getImageUrl";
    public static void index(){
        Merchant merchant= WxMpAuth.currentUser().merchant;
       // List<SetMealPic>  mealPicList= SetMealPic.findByMerchantId(21l);
        Map<SetMeal,String> map=new HashMap<>();
        List<SetMeal> setMealList=SetMeal.findByMerchant(merchant);
//        HttpRequest httpRequest = HttpRequest
//                .get("http://img.ulmsale.cn/getImageUrl")
//                .form(
//                        "uFid", smp.uFid
//                );
//        HttpResponse httpResponse = httpRequest.send();
//        String responseBody = httpResponse.body();
        for(SetMeal sm:setMealList){
            SetMealPic smp=SetMealPic.findBySetMealId(sm.id);
            if(smp!=null){
                //map.put(sm,smp.)
           HttpRequest httpRequest = HttpRequest
                .get(IMG_URL)
                .form(
                        "uFid", smp.uFid
                );
           HttpResponse httpResponse = httpRequest.send();
           String responseBody = httpResponse.body();
           map.put(sm,responseBody);
            }
        }
        Logger.info("map====="+map.size());
        render(map);
    }

    /**
     * 跳转到套餐详情页面
     * @param setmealId
     *
     */
    public static void detail(Long setmealId){
        Logger.info("setmealId==="+setmealId);
       Merchant merchant = WxMpAuth.currentUser().merchant;
        SetMeal setMeal=SetMeal.findByMerchantAndSetMealId(merchant.id,setmealId);
        List<ProductSetMeal> productSetMealList=ProductSetMeal.findProductSetMealBySetMeal(setMeal);
        List<SetMealPic> setMealPicList=SetMealPic.findBySetMeal(setMeal.id);
        List<String> imgUrlList=new ArrayList<>();
        for(SetMealPic smp:setMealPicList){
            HttpRequest httpRequest = HttpRequest
                    .get(IMG_URL)
                    .form(
                            "uFid", smp.uFid,
                            "width", "640",
                            "height", "327"
                    );
            HttpResponse httpResponse = httpRequest.send();
            String responseBody = httpResponse.body();
            Logger.info("responseBody==="+responseBody);
            imgUrlList.add(responseBody);
        }
        //生成uuId
        String uuid = RandomNumberUtil.generateRandomNumberString(16);
        render(setMeal, imgUrlList, productSetMealList,uuid);
    }


    public static void pays(Long setmealId , String uuid) {
     //   WeixinUser wxUser = WxMpAuth.currentUser();
        WeixinUser wxUser= WxMpAuth.currentUser();
        SetMeal setMeal=SetMeal.findByMerchantAndSetMealId(wxUser.merchant.id, setmealId);
       // SetMeal setMeal=SetMeal.findByMerchantAndSetMealId(wxUser.merchant.id,setmealId);

        Order uuidOrder = Order.findByUuid(uuid);

        if(uuidOrder!=null){
           // redirect("/weixin/setmeal/confirm?orderNumber=" + uuidOrder.orderNumber+"&setmealId="+setmealId);
            redirect("/weixin/setmeal/"+uuidOrder.orderNumber+"/"+setmealId+"/confirm");
        }else{
            Logger.info("2");
            Order order = null;
            if(setMeal != null) {
                Logger.info("3");
                // 生成订单

                OrderBuilder orderBuilder = OrderBuilder.forBuild().byUser(wxUser.user).type(OrderType.PC).goodsType(OrderGoodsType.COUPON).uuid(uuid);
                order = orderBuilder.save();  //生成订单号
                // 生成 orderItem  如果有多个 需要多条
                orderBuilder.addGoods(setMeal.findOrCreateGoods())
                        .originalPrice(setMeal.originalPrice)
                        .salePrice(setMeal.presentPrice)
                        .buyNumber(1)
                        .build()
                        .changeToUnPaid();
            }
            // redirect("/order/qrCode?orderNumber=" + order.orderNumber);
            redirect("/weixin/setmeal/"+order.orderNumber+"/"+setmealId+"/confirm");
        }



    }

    //防止刷新页面重新插入订单
    public static void confirm(String orderNumber , String setmealId){
        Logger.info("orderNumber: %s",orderNumber);
        Order order=Order.findByOrderNumber(orderNumber);
        List<OrderItem> orderItems = OrderItem.getListByOrder(order);
        render(order,orderItems);
    }

    public static void pay(String orderNumber){
        Logger.info("OrderNumber :%s",orderNumber);
        Order order=Order.findByOrderNumber(orderNumber);
        render(orderNumber,order);

    }

}
