package controllers.system;

import controllers.system.auth.Secure;
import me.chanjar.weixin.common.util.StringUtils;
import models.constants.DeletedStatus;
import models.mert.Merchant;
import models.mert.MerchantUser;
import models.operate.OperateUser;
import org.apache.commons.codec.digest.DigestUtils;
import play.Logger;
import play.data.validation.*;
import play.data.validation.Error;
import play.modules.paginate.JPAExtPaginator;
import play.mvc.Controller;
import play.mvc.With;
import util.common.RandomNumberUtil;
import util.xsql.datamodifier.modifier.BooleanDataModifier;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@With(Secure.class)
public class MerchantUserController extends Controller {
    public static Integer PAGE_SIZE = 5;
    public static String  BASE_RETURN_INDEX = "/system/merchantUser";

    public static void index(Integer pageNumber ,Long id,MerchantUser merchantUser , String searchName) {
        initData();
        pageNumber = pageNumber == null ? 1 : pageNumber;
        Logger.info("MerchantId : %s==",id);
        Map<String , Object> searchMap = new HashMap<>();
        searchMap.put("deleted", DeletedStatus.UN_DELETED);
        if(id != null){
            searchMap.put("merchantId",id);
        }
        if(StringUtils.isNotBlank(searchName)) {
            Logger.info("searchName :%s=="+searchName);
            searchMap.put("searchName", "%"+searchName+"%");
        }
        JPAExtPaginator<MerchantUser> resultPage = MerchantUser.findByCondition(searchMap, "id asc", pageNumber, PAGE_SIZE);
        Logger.info("merchantspage :%s==",resultPage);
        render(resultPage, pageNumber, merchantUser , id);
    }

    public static void add(Long id){
        Merchant merchant = Merchant.findById(id);
        Boolean isEdit = true;
        render(merchant,isEdit);
    }

    public static void create(Long id ,@Valid MerchantUser merchantUser){
        Merchant merchant = Merchant.findById(id);
        if(validation.hasErrors()) {
            params.flash(); // add http parameters to the flash scope
            validation.keep(); // keep the errors for the next request
            add(id);
        }
        merchantUser.merchant = merchant;
        merchantUser.createdAt = new Date();
        merchantUser.passwordSalt = RandomNumberUtil.generateRandomNumberString(6);
        merchantUser.encryptedPassword = DigestUtils.md5Hex(merchantUser.tmpPassword + merchantUser.passwordSalt);
        merchantUser.deleted=DeletedStatus.UN_DELETED;
        merchantUser.tmpPassword = null;
        merchantUser.save();
        redirect(BASE_RETURN_INDEX+"/"+id);
    }

    public static void edit(Long id,Integer pageNumber){
        MerchantUser merchantUser = MerchantUser.findById(id);
        Boolean isEdit = false;
        render(merchantUser, pageNumber , isEdit);
    }

    /**
     * MerchantUser 修改
     * @param id
     * @param pageNumber
     * @param merchantUser
     */
    public static void update(Long id,Integer pageNumber,MerchantUser merchantUser){
        merchantUser =  MerchantUser.update(id, merchantUser);
        redirect(BASE_RETURN_INDEX+"/"+merchantUser.merchant.id);
    }
    public static void delete(Long id,Integer pageNumber){
        MerchantUser merchantUser = MerchantUser.findById(id);
        MerchantUser.delete(id);
        redirect(BASE_RETURN_INDEX+"/"+merchantUser.merchant.id);
    }

    /**
     * 密码重置
     * @param id
     */
    public static void passwordRest(Long id){
        Logger.info("id : %s==" ,id);
        MerchantUser merchantUser=MerchantUser.findById(id);
        Logger.info("MerchantUserLoginName password :%s="+merchantUser.loginName);
        merchantUser.passwordSalt = RandomNumberUtil.generateRandomNumberString(6);
        merchantUser.encryptedPassword = DigestUtils.md5Hex(merchantUser.loginName + merchantUser.passwordSalt);
        merchantUser.deleted=DeletedStatus.UN_DELETED;
        merchantUser.save();
        String url = "/system/merchantUser/"+merchantUser.merchant.id;
        flash.put("error" , "ok");
        redirect(url);
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