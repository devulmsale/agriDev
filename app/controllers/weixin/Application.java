package controllers.weixin;

import controllers.auth.WxMpAuth;
import models.coupon.CouponBatch;
import play.mvc.Controller;
import play.mvc.With;
import java.util.*;

/**
 * Created by upshan on 15/8/5.
 */
@With(WxMpAuth.class)
public class Application extends Controller {

    public static void index() {
        List<CouponBatch> couponBatchList = CouponBatch.findAll();
        render(couponBatchList);
    }


}
