package controllers.merchant;

import controllers.merchant.auth.MerchantSecure;
import controllers.system.auth.Secure;
import me.chanjar.weixin.common.util.StringUtils;
import models.constants.DeletedStatus;
import models.coupon.CouponBatch;
import models.operate.OperateUser;
import models.product.Product;
import play.modules.paginate.JPAExtPaginator;
import play.mvc.Controller;
import play.mvc.With;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by upshan on 15/8/11.
 */
@With(MerchantSecure.class)
public class CouponBatchController extends Controller {

    public static Integer PAGE_SIZE = 15;
    public static String  BASE_RETURN_INDEX = "/merchant/coupons";


    public static void index(Integer pageNumber) {
        initData();
        pageNumber = pageNumber == null ? 1 : pageNumber;
        Map<String , Object> searchMap = new HashMap<>();
        searchMap.put("merchant", MerchantSecure.getMerchant());
        JPAExtPaginator<CouponBatch> resultPage = CouponBatch.findByCondition(searchMap, "id asc", pageNumber, PAGE_SIZE);
        render(resultPage, pageNumber);
    }


    public static void add() {
        render();
    }


    public static void create(CouponBatch batch) {
        batch.createdAt = new Date();
        batch.deleted = DeletedStatus.UN_DELETED;
        batch.surplusCount = 0;
        batch.merchant = MerchantSecure.getMerchant();
        batch.save();
        redirect(BASE_RETURN_INDEX);
    }



    private static void initData() {
        OperateUser operateUser = Secure.getOperateUser();
        renderArgs.put("operateUser" , operateUser);

        Long count = 8l;
        renderArgs.put("emailCount" , count);
    }


}
