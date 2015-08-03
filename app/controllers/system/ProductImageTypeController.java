package controllers.system;

import controllers.merchant.auth.MerchantSecure;
import controllers.system.auth.Secure;
import me.chanjar.weixin.common.util.StringUtils;
import models.constants.DeletedStatus;
import models.mert.MerchantUser;
import models.operate.OperateUser;
import models.product.ProductImageType;
import models.product.ProductType;
import models.product.enums.ImageType;
import play.Logger;
import play.data.validation.Valid;
import play.modules.paginate.JPAExtPaginator;
import play.mvc.Controller;
import play.mvc.With;

import java.util.HashMap;
import java.util.Map;

/**
 * 图片类型管理
 */
@With(Secure.class)
public class ProductImageTypeController extends Controller {

    public static Integer PAGE_SIZE = 15;
    public static String  BASE_RETURN_INDEX = "/system/productImageType";

    public static void index(Integer pageNumber  , String searchName) {
        initData();
        pageNumber = pageNumber == null ? 1 : pageNumber;
        Map<String , Object> searchMap = new HashMap<>();
        if(StringUtils.isNotBlank(searchName)) {
            searchMap.put("searchName", "%"+searchName+"%");
        }
        ImageType[] imageTypes = ImageType.values();
        JPAExtPaginator<ProductImageType> resultPage = ProductImageType.findByCondition(searchMap, "id asc", pageNumber, PAGE_SIZE);
        ProductImageType pit = new ProductImageType();
        render(resultPage, pageNumber , imageTypes , pit);
    }

    public static void add() {
        ImageType[] imageTypes = ImageType.values();
        render(imageTypes);
    }

    public static void create(@Valid ProductImageType productImageType) {
        if(validation.hasErrors()) {
            params.flash(); // add http parameters to the flash scope
            validation.keep(); // keep the errors for the next request
            add();
        }
        productImageType.deleted = DeletedStatus.UN_DELETED;
        productImageType.save();
        redirect(BASE_RETURN_INDEX);
    }

    public static void edit(Long id , Integer pageNumber) {
        ProductImageType productImageType = ProductImageType.findById(id);
        render(productImageType , pageNumber);
    }

    public static void update(Long id , ProductImageType productImageType) {
        ProductImageType.update(id, productImageType);
        redirect(BASE_RETURN_INDEX);
    }


    public static void delete(Long id , Integer pageNumber) {
        ProductImageType productImageType = ProductImageType.findById(id);
        if(productImageType != null ) {
            productImageType.deleted = DeletedStatus.DELETED;
            productImageType.save();
        }
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