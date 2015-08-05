package controllers.system;

import controllers.merchant.auth.MerchantSecure;
import controllers.system.auth.Secure;
import me.chanjar.weixin.common.util.StringUtils;
import models.constants.DeletedStatus;
import models.mert.MerchantUser;
import models.operate.OperateUser;
import models.product.Lable;
import models.product.ProductType;
import models.product.TypeLable;
import play.Logger;
import play.data.validation.Valid;
import play.modules.paginate.JPAExtPaginator;
import play.mvc.Controller;
import play.mvc.With;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 标签管理
 */
@With(Secure.class)
public class LableController extends Controller {

    public static Integer PAGE_SIZE = 15;
    public static String  BASE_RETURN_INDEX = "/system/lable";

    public static void index(Integer pageNumber  , String searchName) {
        initData();
        pageNumber = pageNumber == null ? 1 : pageNumber;
        Map<String , Object> searchMap = new HashMap<>();
        if(StringUtils.isNotBlank(searchName)) {
            searchMap.put("searchName", "%"+searchName+"%");
        }

        JPAExtPaginator<Lable> resultPage = Lable.findByCondition(searchMap, "id asc", pageNumber, PAGE_SIZE);
        render(resultPage, pageNumber);
    }

    public static void add() {
        //查询所有的一级大类
        List<ProductType> productTypeList=ProductType.findTopType();
        render(productTypeList);
    }

    public static void create(@Valid Lable lable ,String productTypes) {
        if(validation.hasErrors()) {
            params.flash(); // add http parameters to the flash scope
            validation.keep(); // keep the errors for the next request
            add();
        }
        lable.deleted = DeletedStatus.UN_DELETED;
        lable.operateUser= Secure.getOperateUser();
        lable.save();
        Logger.info("标签 :%s=",productTypes);
        //保存属性
        ProductType productType=null;
        String[] productTypess=productTypes.split(",");
        for(String productTypeId:productTypess){
            productType=ProductType.findById(Long.valueOf(productTypeId.trim()));
            if(lable != null && productType != null){
                new TypeLable(productType, lable);
            }
        }
        redirect(BASE_RETURN_INDEX);

    }


    public static void delete(Long id , Integer pageNumber) {
        Lable lable = Lable.findById(id);
        if(lable != null) {
            lable.deleted = DeletedStatus.DELETED;
            lable.save();
        }
        redirect(BASE_RETURN_INDEX);
    }

   /* private static void initData() {
        MerchantUser merchantUser = MerchantSecure.getMerchantUser();
        renderArgs.put("merchantUser" , merchantUser);
    }*/
   private static void initData() {
       // 绠＄悊鍛樹俊鎭�
       OperateUser operateUser = Secure.getOperateUser();
       renderArgs.put("operateUser" , operateUser);

       //绠＄悊鍛橀偖绠�
       Long count = 8l;
       renderArgs.put("emailCount" , count);
   }


}