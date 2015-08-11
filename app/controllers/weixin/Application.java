package controllers.weixin;

import controllers.auth.WxMpAuth;
import jodd.http.HttpRequest;
import models.coupon.CouponBatch;
import models.mert.Merchant;
import models.mert.MerchantProductType;
import models.product.Product;
import play.Logger;
import play.mvc.Controller;
import play.mvc.With;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
        List<MerchantProductType> merchantProductTypeList=MerchantProductType.findMerchantProductType(12L);
        Logger.info("获取商户商品类别 :%s=",merchantProductTypeList.size());
        //获取商品
        List<Product> productList=Product.findProduct();
        Logger.info("productList :%s=",productList.size());
        render(merchantProductTypeList , productList);
    }

    //根据类别Id取商户商品
    public static void getProduct(Long productTypeId){
        List<Product> productList =Product.findProductByMerIdAndMerProductType(12L,productTypeId);
        render(productList);
    }

    public static void confirm() {
        render();
    }

    public static void detail() {
        render();
    }

    public static void pay() {
        render();
    }

}
