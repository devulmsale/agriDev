package controllers.system;

import controllers.system.auth.Secure;
import me.chanjar.weixin.common.util.StringUtils;
import models.constants.DeletedStatus;
import models.mert.Merchant;
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
        render(merchant);
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