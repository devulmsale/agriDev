package controllers.system;

import controllers.system.auth.Secure;
import me.chanjar.weixin.common.util.StringUtils;
import models.common.DateUtil;
import models.constants.DeletedStatus;
import models.mert.Merchant;
import models.mert.MerchantRenew;
import models.mert.MerchantUser;
import models.mert.enums.MerchantStatus;
import models.mert.enums.MerchantUserStatus;
import play.data.validation.*;
import play.data.validation.Error;
import models.operate.OperateUser;
import org.apache.commons.codec.digest.DigestUtils;
import play.Logger;
import play.modules.paginate.JPAExtPaginator;
import play.mvc.Controller;
import play.mvc.With;
import util.common.RandomNumberUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@With(Secure.class)
public class MerchantController extends Controller {
    public static Integer PAGE_SIZE = 15;
    public static String  BASE_RETURN_INDEX = "/system/merchant";

    public static void index(Integer pageNumber ,Merchant merchant , String searchName) {
        initData();
        pageNumber = pageNumber == null ? 1 : pageNumber;
        Map<String , Object> searchMap = new HashMap<>();
        searchMap.put("deleted", DeletedStatus.UN_DELETED);
        if(StringUtils.isNotBlank(searchName)) {
            Logger.info("searchName :%s=="+searchName);
            searchMap.put("searchName", "%"+searchName+"%");
        }
        JPAExtPaginator<Merchant> resultPage = Merchant.findByCondition(searchMap, "id asc", pageNumber, PAGE_SIZE);
        Logger.info("merchantspage :%s==",resultPage);
        render(resultPage, pageNumber, merchant);
    }

    public static void add(Merchant merchant , MerchantUser merchantUser){
        Logger.info("add-----");
        MerchantStatus[] merchantStatuses = MerchantStatus.values();
        render(merchant , merchantUser ,merchantStatuses);
    }

    public static void create(@Valid Merchant merchant , @Valid MerchantUser merchantUser){
        Logger.info("validation %s==",validation.hasErrors());
        if(validation.hasErrors()) {
            params.flash(); // add http parameters to the flash scope
            validation.keep(); // keep the errors for the next request
            add(merchant , merchantUser);
        }
        merchant.createdAt=new Date();
        merchant.deleted= DeletedStatus.UN_DELETED;
        merchant.save();

        merchantUser.merchant = merchant;
        merchantUser.createdAt = new Date();
        merchantUser.passwordSalt = RandomNumberUtil.generateRandomNumberString(6);
        merchantUser.encryptedPassword = DigestUtils.md5Hex(merchantUser.tmpPassword + merchantUser.passwordSalt);
        merchantUser.deleted=DeletedStatus.UN_DELETED;
        merchantUser.save();
        Logger.info("merchant And MerchantUser success!");
        redirect(BASE_RETURN_INDEX);
    }

    /**
     * 商家续费
     * @param id
     */
    public static void renew(Long id) {
        Merchant merchant = Merchant.findById(id);
        //查询 该商户的所有续费记录
        //需要展示内容 编号 充值日期 延期到期时间 金额  操作人
        List<MerchantRenew> merchantRenewList = MerchantRenew.findMerchantRenewInfo(id);
        Logger.info("MerchantRenew find %s="+MerchantRenew.findMerchantRenewInfo(id));
        Logger.info("MerchantRenewList size %s=="+merchantRenewList.size());
        render(merchant, merchantRenewList);
    }


    public static void renewUpdate(Long id ,  @Valid MerchantRenew renew) {
        if(validation.hasErrors()) {
            params.flash(); // add http parameters to the flash scope
            validation.keep(); // keep the errors for the next request
            renew(id);
        }
        Logger.info("renew : %s --" , renew.expireAt);
        Logger.info("newDate : %s --" , new Date());
        Logger.info("differenceMinute : %s ---" , DateUtil.differenceMinute(new Date() , renew.expireAt));
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


    public static void edit(Long id,Integer pageNumber){
        Merchant merchant = Merchant.findById(id);
        MerchantStatus[] merchantStatuses = MerchantStatus.values();
        render(merchant, pageNumber, merchantStatuses);
    }

    public static void update(Long id,Integer pageNumber,Merchant merchant){
        Merchant.update(id, merchant);
        index(pageNumber , null,null);
    }
    public static void delete(Long id,Integer pageNumber){
        Logger.info("id : %s ====" , id);
        Merchant.delete(id);
        index(pageNumber, null,null);
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