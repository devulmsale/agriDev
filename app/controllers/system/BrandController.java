package controllers.system;

import controllers.merchant.auth.MerchantSecure;
import controllers.system.auth.Secure;
import me.chanjar.weixin.common.util.StringUtils;
import models.constants.DeletedStatus;
import models.mert.Merchant;
import models.mert.MerchantUser;
import models.operate.OperateUser;
import models.product.Brand;
import models.product.ProductImageType;
import models.product.ProductType;
import models.product.TypeBrand;
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
 * 品牌管理
 */
@With(Secure.class)
public class BrandController extends Controller {

    public static Integer PAGE_SIZE = 15;
    public static String  BASE_RETURN_INDEX = "/system/brands";

    public static void index(Integer pageNumber  , String searchName) {
        initData();
        pageNumber = pageNumber == null ? 1 : pageNumber;
        Map<String , Object> searchMap = new HashMap<>();
        if(StringUtils.isNotBlank(searchName)) {
            searchMap.put("searchName", "%"+searchName+"%");
        }
        searchMap.put("isTop" , true);
        JPAExtPaginator<Brand> resultPage = Brand.findByCondition(searchMap, "id asc", pageNumber, PAGE_SIZE);
        render(resultPage, pageNumber);
    }

    public static void add() {
        //查询所有的一级大类
       List<ProductType> productTypeList=ProductType.findTopType();
        render(productTypeList);
    }

    public static void create(@Valid Brand brand ,String productTypes) {
        if(validation.hasErrors()) {
            params.flash(); // add http parameters to the flash scope
            validation.keep(); // keep the errors for the next request
            add();
        }

        //创建品牌时选择的类别保存到type_brand
        Logger.info("品牌类 :%s=",productTypes);
        createAndEdit(brand , productTypes);
        redirect(BASE_RETURN_INDEX);
        /*if(brand.brand != null) {
            addChild(brand.brand.id);
        } else {
            redirect(BASE_RETURN_INDEX);
        }*/
    }

    private static void createAndEdit(Brand brand , String productTypes){
        //保存品牌
        brand.deleted = DeletedStatus.UN_DELETED;
        brand.save();
        //保存typebrand
        ProductType productType=null;
        String[] productTypess=productTypes.split(",");
        List<TypeBrand> typeBrandList = TypeBrand.findProductTypeByBrandId(brand.id);
        for(String typeId:productTypess){
            productType= ProductType.findById(Long.valueOf(typeId.trim()));
            Logger.info("productType %s= | brand.id %s=",productType , brand.id);
            if(productType != null && brand != null){
                //以前已经存在 不管
                TypeBrand typeBrand = TypeBrand.findByTypeAndBrand(productType.id, brand.id);
                if(typeBrand == null) {
                    new TypeBrand(productType, brand);
                }else {
                    typeBrandList.remove(typeBrand);
                }
            }
        }
    }

    public static void edit(Long id , Integer pageNumber) {
        Brand brand = Brand.findById(id);
        //查询所有的一级大类
        List<ProductType> productTypeList=ProductType.findTopType();
        //根据品牌id取已经选择的类别
        List<TypeBrand> typeBrands=TypeBrand.findProductTypeByBrandId(id);
        render(brand , productTypeList , typeBrands , pageNumber);
    }

    public static void update(Long id , Brand brand , String productTypes) {
        Brand.update(id , brand);
        createAndEdit(brand , productTypes);
        redirect(BASE_RETURN_INDEX);
    }


    public static void delete(Long id , Integer pageNumber) {
        Brand brand = Brand.findById(id);
        if(brand != null && brand.brand == null) {
            brand.deleted = DeletedStatus.DELETED;
            brand.save();
        }
        redirect(BASE_RETURN_INDEX);
    }

    /**
     * 添加子类菜单
     * @param id
     */
    public static void addChild(Long id) {
        Brand brand = Brand.findById(id);
        Map<String , Object> searchMap = new HashMap<>();
        searchMap.put("brandId" , id);
        JPAExtPaginator<Brand> resultPage = Brand.findByCondition(searchMap, "id asc", 1, PAGE_SIZE);
        render(brand , resultPage);
    }


    public static void updateName(Long id , String name) {
        Brand brand = Brand.findById(id);
        if(brand != null) {
            brand.name = name;
            brand.save();
        }
        if(brand.brand != null) {
            addChild(brand.brand.id);
        } else {
            redirect(BASE_RETURN_INDEX);
        }
    }

    /**
     * 根据一级类别Id取二级类别
     */
    public static void getSubClassByParentId(Long parentId , Long brandId){
        Logger.info("parentId : %s , brandId : %s" , parentId , brandId);
        List<ProductType> productTypeList=ProductType.findByParentType(parentId);
        for(ProductType productType : productTypeList) {
            productType.isHave = TypeBrand.isHaveBrand(productType.id, brandId);
            Logger.info(" productType.isHave : %s | parentId : %s , brandId : %s" , productType.isHave ,  parentId , brandId);
        }

        renderJSON(productTypeList);
    }

    /*private static void initData() {
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