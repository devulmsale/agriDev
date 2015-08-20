package controllers.weixin;

import controllers.auth.WxMpAuth;
import models.coupon.CouponBatch;
import models.mert.Merchant;
import play.mvc.Controller;
import play.mvc.With;
import java.util.*;

/**
 * Created by upshan on 15/8/5.
 */
@With(WxMpAuth.class)
public class Application extends Controller {

    public static void index() {
        //获取商户信息 TODO merchant_id  WxMpAuth.currentUser().merchant
        Merchant merchant=Merchant.findById(21l);
        List<CouponBatch> couponBatchList = CouponBatch.findAll();
        render(couponBatchList , merchant);
    }


}
