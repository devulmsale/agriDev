package controllers.weixin;

import controllers.auth.WxMpAuth;
import jodd.http.HttpRequest;
import models.mert.Merchant;
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
        render();
    }

    public static void products() {
        //获取二级分类

        //获取商品
        List<Product> productList=Product.findProduct();
        Logger.info("productList :%s=",productList.size());
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
