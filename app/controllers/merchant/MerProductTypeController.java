package controllers.merchant;

import controllers.merchant.auth.MerchantSecure;
import controllers.system.auth.Secure;
import me.chanjar.weixin.common.util.StringUtils;
import models.common.JSONEntity;
import models.constants.DeletedStatus;
import models.mert.MerchantProductType;
import models.operate.OperateUser;
import models.product.*;
import play.Logger;
import play.data.validation.Valid;
import play.modules.paginate.JPAExtPaginator;
import play.mvc.Controller;
import play.mvc.With;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 商户类别管理
 */
@With(MerchantSecure.class)
public class MerProductTypeController extends Controller {

    public static Integer PAGE_SIZE = 15;
    public static String  BASE_RETURN_INDEX = "/merchant/merProductType";

    public static void index(Integer pageNumber  , String searchName) {
        initData();
        pageNumber = pageNumber == null ? 1 : pageNumber;
        Map<String , Object> searchMap = new HashMap<>();
        if(StringUtils.isNotBlank(searchName)) {
            searchMap.put("searchName", "%"+searchName+"%");
        }
        JPAExtPaginator<MerchantProductType> resultPage = MerchantProductType.findByCondition(searchMap, "id asc", pageNumber, PAGE_SIZE);
        render(resultPage, pageNumber);
    }

    public static void add(MerchantProductType merchantProductType) {

        render(merchantProductType);
    }

    public static void create(@Valid MerchantProductType merchantProductType ) throws Exception {
       Logger.info("商户添加--类别");
        if(validation.hasErrors()) {
            params.flash(); // add http parameters to the flash scope
            validation.keep(); // keep the errors for the next request
            add(merchantProductType);
        }
        Logger.info("商户类别添加");
        merchantProductType.deleted=DeletedStatus.UN_DELETED;
        merchantProductType.createdAt=new Date();
        merchantProductType.save();
        redirect(BASE_RETURN_INDEX);


    }

    public static void edit(Long id , Integer pageNumber) {
        MerchantProductType merchantProductType=MerchantProductType.findById(id);
        render(merchantProductType ,pageNumber);
    }

    public static void update(Long id , MerchantProductType merchantProductType) {
        Logger.info("商品类别更新 id :%s=",id);
        MerchantProductType.update(id , merchantProductType);
        redirect(BASE_RETURN_INDEX);
    }


    public static void delete(Long id , Integer pageNumber) {

        redirect(BASE_RETURN_INDEX);
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