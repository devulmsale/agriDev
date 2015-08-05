package controllers.weixin;

import controllers.auth.WxMpAuth;
import models.base.WeixinUser;
import play.mvc.Controller;
import play.mvc.With;

/**
 * Created by upshan on 15/8/5.
 */
@With(WxMpAuth.class)
public class Application extends Controller {

    public static void index() {
        WeixinUser wxUser = WxMpAuth.currentUser();
        render(wxUser);
    }
}
