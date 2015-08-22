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
//@With(WxMpAuth.class)
public class UserCenterController extends Controller {

    public static  void index(){
        //TODO user_id
        //User user = WxMpAuth.currentUser().user;
        User user=new User();
        user.id=2l;
        Long unPayCount= Order.findUnOrderByUser(user);
        Long couponCount= Coupon.findCouponCountByLoginUser(user.id);
        render(user, unPayCount,couponCount);
    }

    public static void detail(){
        //TODO user_id
      //  WeixinUser wxUser = WxMpAuth.currentUser();
        WeixinUser wxUser= new WeixinUser();
        wxUser.id = 2l;
        List<Address> addressList=Address.findAddressByUserId(wxUser.id);
        render(addressList);
    }


}
