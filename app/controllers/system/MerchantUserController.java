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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
        render(resultPage, pageNumber, merchantUser , id);
    }

    public static void add(Long id){
        Merchant merchant = Merchant.findById(id);
        render(merchant);
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
        String url = "/system/merchantUser/"+id;
        redirect(url);
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