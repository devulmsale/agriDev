package controllers.system;

import controllers.system.auth.Secure;
import me.chanjar.weixin.common.util.StringUtils;
import models.common.DateUtil;
import models.constants.DeletedStatus;
import models.mert.Merchant;
import models.mert.MerchantRenew;
import models.mert.MerchantUser;
import models.mert.enums.MerchantStatus;
import models.operate.OperateUser;
import org.apache.commons.codec.digest.DigestUtils;
import play.Logger;
import play.data.validation.Valid;
import play.modules.paginate.JPAExtPaginator;
import play.mvc.Controller;
import play.mvc.With;
import util.common.RandomNumberUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商家续费管理
 */
@With(Secure.class)
public class MerchantRenewController extends Controller {

    /**
     * 商家续费
     * @param id
     */
    public static void renew(Long id) {
        Merchant merchant = Merchant.findById(id);
        List<MerchantRenew> merchantRenewList = MerchantRenew.findMerchantRenewInfo(id);
        render(merchant, merchantRenewList);
    }


    public static void renewUpdate(Long id ,  @Valid MerchantRenew renew) {
        if(validation.hasErrors()) {
            params.flash(); // add http parameters to the flash scope
            validation.keep(); // keep the errors for the next request
            renew(id);
        }
        if(DateUtil.differenceMinute(new Date() , renew.expireAt) > 0) {

            Merchant merchant = Merchant.findById(id);
            renew.deleted = DeletedStatus.UN_DELETED;
            renew.merchant = merchant;
            renew.operateUser = Secure.getOperateUser();
            renew.updateAt = new Date();
            renew.save();
            // 商家修改

            merchant.expiredAt = renew.expireAt;
            merchant.status = MerchantStatus.OPEN;
            merchant._save();
        } else {
            flash.put("error", "延期时间 小于当前时间, 延期失败");
        }
        renew(id);
    }


    private static void initData() {
        // 绠＄悊鍛樹俊鎭�
        OperateUser operateUser = Secure.getOperateUser();
        renderArgs.put("operateUser" , operateUser);

        //绠＄悊鍛橀偖绠�
        Long count = 8l;
        renderArgs.put("emailCount" , count);
    }
}