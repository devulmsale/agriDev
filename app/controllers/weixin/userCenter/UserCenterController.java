package controllers.weixin.userCenter;

import controllers.auth.WxMpAuth;
import models.coupon.Coupon;
import models.order.Order;
import models.order.User;
import play.mvc.Controller;
import play.mvc.With;

/**
 * Created by Administrator on 2015/8/19.
 */
//@With(WxMpAuth.class)
public class UserCenterController extends Controller {

    public static  void index(){
        // User user=WxMpAuth.currentUser().user;
        //TODO 暂时取到userId
        User user=new User();
        user.id=2l;
        Long unPayCount= Order.findUnOrderByUser(user);
        Long couponCount= Coupon.findCouponCountByLoginUser(user.id);
        render(user, unPayCount,couponCount);
    }


}
