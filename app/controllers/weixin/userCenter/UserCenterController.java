package controllers.weixin.userCenter;

import controllers.auth.WxMpAuth;
import models.address.Address;
import models.base.WeixinUser;
import models.coupon.Coupon;
import models.order.Order;
import models.order.User;
import play.mvc.Controller;
import play.mvc.With;

import java.util.List;

/**
 * Created by Administrator on 2015/8/19.
 */
@With(WxMpAuth.class)
public class UserCenterController extends Controller {

    public static  void index(){
        User user = WxMpAuth.currentUser().user;
        Long unPayCount= Order.findUnOrderByUser(user);
        Long couponCount= Coupon.findCouponCountByLoginUser(user.id);
        render(user, unPayCount,couponCount);
    }

    public static void detail(){
        WeixinUser wxUser = WxMpAuth.currentUser();
        List<Address> addressList=Address.findAddressByUserId(wxUser.id);
        render(addressList);
    }

    public static  void  phonevaild(){
        render();
    }


}
