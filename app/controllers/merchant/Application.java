package controllers.merchant;

import controllers.merchant.auth.MerchantSecure;
import controllers.system.auth.Secure;
import models.mert.MerchantUser;
import models.operate.OperateUser;
import net.sf.oval.guard.Post;
import org.junit.Before;
import play.Logger;
import play.mvc.Controller;
import play.mvc.With;


@With(MerchantSecure.class)
public class Application extends Controller {

    public static void index() {
        MerchantUser merchantUser = MerchantSecure.getMerchantUser();
        render(merchantUser);
    }



}