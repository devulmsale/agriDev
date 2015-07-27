package controllers.system;

import controllers.merchant.auth.MerchantSecure;
import me.chanjar.weixin.common.util.StringUtils;
import models.constants.DeletedStatus;
import models.mert.MerchantUser;
import models.product.Brand;
import models.product.Product;
import models.product.ProductType;
import play.Logger;
import play.data.validation.Valid;
import play.modules.paginate.JPAExtPaginator;
import play.mvc.Controller;
import play.mvc.With;

import java.util.HashMap;
import java.util.Map;


@With(MerchantSecure.class)
public class ProductTypeController extends Controller {

    public static Integer PAGE_SIZE = 15;
    public static String  BASE_RETURN_INDEX = "/system/productType";

    public static void index(Integer pageNumber  , String searchName) {
        initData();
        pageNumber = pageNumber == null ? 1 : pageNumber;
        Map<String , Object> searchMap = new HashMap<>();
        if(StringUtils.isNotBlank(searchName)) {
            searchMap.put("searchName", "%"+searchName+"%");
        }
        searchMap.put("isTop" , true);
        JPAExtPaginator<ProductType> resultPage = ProductType.findByCondition(searchMap, "id asc", pageNumber, PAGE_SIZE);
        render(resultPage, pageNumber);
    }

    public static void add() {

        render();
    }

    public static void create(@Valid ProductType productType) {
        if(validation.hasErrors()) {
            params.flash(); // add http parameters to the flash scope
            validation.keep(); // keep the errors for the next request
            add();
        }
        productType.deleted = DeletedStatus.UN_DELETED;
        productType.save();
        if(productType.parentType != null) {
            addChild(productType.parentType.id);
        } else {
            redirect(BASE_RETURN_INDEX);
        }
    }

    public static void edit(Long id , Integer pageNumber) {
        ProductType productType = ProductType.findById(id);
        render(productType , pageNumber);
    }

    public static void update(Long id , ProductType productType) {
        ProductType.update(id , productType);
        redirect(BASE_RETURN_INDEX);
    }


    public static void delete(Long id , Integer pageNumber) {
        ProductType productType = ProductType.findById(id);
        if(productType != null && productType.parentType == null) {
            productType.deleted = DeletedStatus.DELETED;
            productType.save();
        }
        redirect(BASE_RETURN_INDEX);
    }

    /**
     * 添加子类菜单
     * @param id
     */
    public static void addChild(Long id) {
        ProductType productType = ProductType.findById(id);
        Map<String , Object> searchMap = new HashMap<>();
        searchMap.put("parentTypeId" , id);
        JPAExtPaginator<ProductType> resultPage = ProductType.findByCondition(searchMap, "id asc", 1, PAGE_SIZE);
        render(productType , resultPage);
    }


    public static void updateName(Long id , String name) {
        ProductType productType = ProductType.findById(id);
        if(productType != null) {
            productType.name = name;
            productType.save();
        }
        if(productType.parentType != null) {
            addChild(productType.parentType.id);
        } else {
            redirect(BASE_RETURN_INDEX);
        }
    }




    private static void initData() {
        MerchantUser merchantUser = MerchantSecure.getMerchantUser();
        renderArgs.put("merchantUser" , merchantUser);
    }

}