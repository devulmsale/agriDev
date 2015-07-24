package controllers.system;

import controllers.system.auth.Secure;
import me.chanjar.weixin.common.util.StringUtils;
import models.constants.DeletedStatus;
import models.mert.Merchant;
import models.mert.MerchantUser;
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
public class MerchantUserController extends Controller {
    public static Integer PAGE_SIZE = 15;

    public static void index(Integer pageNumber ,Long id,MerchantUser merchantUser , String searchName) {
        initData();
        pageNumber = pageNumber == null ? 1 : pageNumber;
        Logger.info("MerchantId : %s==",id);
        Map<String , Object> searchMap = new HashMap<>();
        searchMap.put("deleted", DeletedStatus.UN_DELETED);
        searchMap.put("merchantId",id);
        if(StringUtils.isNotBlank(searchName)) {
            Logger.info("searchName :%s=="+searchName);
            searchMap.put("searchName", "%"+searchName+"%");
        }
        JPAExtPaginator<MerchantUser> resultPage = MerchantUser.findByCondition(searchMap, "id asc", pageNumber, PAGE_SIZE);
        Logger.info("merchantspage :%s==",resultPage);
        render(resultPage, pageNumber, merchantUser);
    }

    public static void add(Merchant merchant , MerchantUser merchantUser){
        Logger.info("add-----");
        render(merchant , merchantUser);
    }

    public static void create(Merchant merchant , MerchantUser merchantUser){
        if(StringUtils.isBlank(merchant.fullName)){
            flash.put("fullNameerror","商户名称不能为空！");
            renderArgs.put("fullName",merchant.fullName);
            add(merchant , merchantUser);
        }
        if(StringUtils.isBlank(merchant.shortName)){
            flash.put("shortNameerror","商户简称不能为空！");
            renderArgs.put("shortName",merchant.shortName);
            add(merchant , merchantUser);
        }
       if(StringUtils.isBlank(merchant.phone)){
            flash.put("phoneerror","联系电话不能为空！");
           renderArgs.put("phone",merchant.phone);
           add(merchant , merchantUser);
        }
        if(StringUtils.isBlank(merchant.address)){
            flash.put("addresserror","商户地址不能为空！");
            renderArgs.put("address",merchant.address);
            add(merchant , merchantUser);
        }
        if(StringUtils.isBlank(merchantUser.loginName)){
            flash.put("loginNameerror","登录账号不能为空！");
            renderArgs.put("loginName",merchantUser.loginName);
            add(merchant , merchantUser);
        }
        if(StringUtils.isBlank(merchantUser.encryptedPassword)){
            flash.put("encryptedPassworderror","登录密码不能为空！");
            renderArgs.put("encryptedPassword",merchantUser.encryptedPassword);
            add(merchant , merchantUser);
        }
        merchant.createdAt=new Date();
        merchant.deleted= DeletedStatus.UN_DELETED;
        merchant.save();

        merchantUser.merchant = merchant;
        merchantUser.createdAt = new Date();
        merchantUser.passwordSalt = RandomNumberUtil.generateRandomNumberString(6);
        merchantUser.encryptedPassword = DigestUtils.md5Hex(merchantUser.confirmPassword + merchantUser.passwordSalt);
        merchantUser.deleted=DeletedStatus.UN_DELETED;
        merchantUser.save();
      //  index(1, merchant, null);
    }

    public static void edit(Long id,Integer pageNumber){
        Merchant merchant = Merchant.findById(id);
//        MerchantUser merchantUser = MerchantUser.findById(id);
        render(merchant, pageNumber);
    }

    public static void update(Long id,Integer pageNumber,Merchant merchant){
        Merchant.update(id, merchant);
       // index(pageNumber , null,null);
    }
    public static void delete(Long id,Integer pageNumber){
        Logger.info("id : %s ====" , id);
        Merchant.delete(id);
       // index(pageNumber, null,null);
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