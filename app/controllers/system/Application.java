package controllers.system;

import controllers.system.auth.Secure;
import models.operate.OperateUser;
import org.junit.Before;
import play.mvc.Controller;
import play.mvc.With;


@With(Secure.class)
public class Application extends Controller {

    public static void index() {
        initData();
        render();
    }


    private static void initData() {
        // 管理员信息
        OperateUser operateUser = Secure.getOperateUser();
        renderArgs.put("operateUser" , operateUser);

        //管理员邮箱
        Long count = 8l;
        renderArgs.put("emailCount" , count);
    }

}