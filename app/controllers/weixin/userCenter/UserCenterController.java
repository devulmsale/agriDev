package controllers.weixin.userCenter;

import models.coupon.Coupon;
import models.order.Order;
import models.order.User;
import play.mvc.Controller;

/**
 * Created by Administrator on 2015/8/19.
 */
public class UserCenterController extends Controller {

    public static  void index(){
        // User user=WxMpAuth.currentUser().user;
        //TODO 暂时取到userId
        User user=User.findById(2l);
        Long unPayCount= Order.findUnOrderByUser(user);
        Long couponCount= Coupon.findCouponCountByLoginUser(user.id);
        render(user, unPayCount,couponCount);
    }
}
