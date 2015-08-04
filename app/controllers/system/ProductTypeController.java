package controllers.system;

import controllers.merchant.auth.MerchantSecure;
import controllers.system.auth.Secure;
import me.chanjar.weixin.common.util.StringUtils;
import models.constants.DeletedStatus;
import models.mert.MerchantUser;
import models.operate.OperateUser;
import models.product.*;
import play.Logger;
import play.data.validation.Valid;
import play.modules.paginate.JPAExtPaginator;
import play.mvc.Controller;
import play.mvc.With;
import sun.rmi.runtime.Log;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类别管理
 */
@With(Secure.class)
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
        List<Brand> brandList=Brand.findBrand();
        List<Lable> lableList=Lable.fingLable();
        render(brandList,lableList);
    }
    public static void addChildPage(Long id) {
        List<Brand> brandList=Brand.findBrand();
        List<Lable> lableList=Lable.fingLable();
        render(brandList , lableList , id);
    }

    public static void editChildPage(Long id){
        Logger.info("editChildPage : %s=",id);
        ProductType productType=ProductType.findById(id);
        //根据id取品牌和属性
        List<Brand> brandList=Brand.findBrand();
        List<Lable> lableList=Lable.fingLable();
        List<TypeBrand> typeBrands=TypeBrand.findByProductType(id);
        List<TypeLable> typeLables=TypeLable.findByProductType(id);
        render(productType ,typeBrands , typeLables, brandList , lableList , id);
    }

    public static void create(@Valid ProductType productType, TypeBrand typeBrand,TypeLable typeLable,String brandbox,String lablebox) {
       Logger.info("productType.parentType :%s="+productType.parentType);
        if(validation.hasErrors()) {
            params.flash(); // add http parameters to the flash scope
            validation.keep(); // keep the errors for the next request
            add();
        }

        if (brandbox == null){
            flash.error("branderror","品牌不能为空！");
            add();
        }
        if (lablebox == null){
            flash.error("lableerror","属性不能为空！");
            add();
        }

        publicEditAndCreate(productType , brandbox, lablebox);

        if(productType.parentType != null) {
            addChild(productType.parentType.id);
        } else {
            redirect(BASE_RETURN_INDEX);
        }
    }

    private static void publicEditAndCreate(ProductType productType ,String brandbox,String lablebox ){
        //保存类别
        productType.deleted = DeletedStatus.UN_DELETED;
        productType.save();
        //保存品牌
        String [] boxs=brandbox.split(",");
        Brand brand =null;
        Logger.info("类别productType id :%s=",productType.id);
        List<TypeBrand> typeBrandList = TypeBrand.findByProductType(productType.id);
        Logger.info("remove 之前 : typeBrandList.size() : %s ---" , typeBrandList.size());
        for(String brandId : boxs) {
            brand = Brand.findById(Long.valueOf(brandId.trim()));
            if(brand != null && productType != null) {
                //以前已经存在 不管
                TypeBrand typeBrand = TypeBrand.findByTypeAndBrand(productType.id, brand.id);
                if(typeBrand == null) {
                    new TypeBrand(productType , brand);
                } else {
                    typeBrandList.remove(typeBrand);
                }
            }
        }
        Logger.info("remove 以后 : typeBrandList.size() : %s ---" , typeBrandList.size());
        for(TypeBrand typeBrand : typeBrandList) {
            typeBrand.deleted = DeletedStatus.DELETED;
            typeBrand.save();
        }
        //保存属性
        String[] lables=lablebox.split(",");
        Lable lable=null;
        List<TypeLable> typeLableList=TypeLable.findByProductType(productType.id);
        for(String lableId:lables){
            lable=Lable.findById(Long.valueOf(lableId.trim()));
            if(lable != null && productType != null){
                TypeLable typeLable=TypeLable.findByTypeAndLable(productType.id,lable.id);
                if(typeLable == null) {
                    new TypeLable(productType, lable);
                }else {
                    typeLableList.remove(typeLable);
                }
            }
        }
        for(TypeLable typeLable : typeLableList){
            typeLable.deleted=DeletedStatus.DELETED;
            typeLable.save();
        }
    }

    public static void edit(Long id , Integer pageNumber) {
        ProductType productType = ProductType.findById(id);
        List<Brand> brandList=Brand.findBrand();
        List<Lable> lableList=Lable.fingLable();
        List<TypeBrand> typeBrands=TypeBrand.findByProductType(id);
        List<TypeLable> typeLables=TypeLable.findByProductType(id);
        render(productType , pageNumber , typeBrands , typeLables, brandList , lableList , id);
    }

    public static void update(Long id , Long parentId, ProductType productType ,String brandbox , String lablebox) {
        Logger.info("update id %s | %s=",id , parentId);
        ProductType.update(id , productType);
        productType = ProductType.findById(id);
        publicEditAndCreate(productType , brandbox, lablebox);
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
        // 绠＄悊鍛樹俊鎭�
        OperateUser operateUser = Secure.getOperateUser();
        renderArgs.put("operateUser" , operateUser);

        //绠＄悊鍛橀偖绠�
        Long count = 8l;
        renderArgs.put("emailCount" , count);
    }
}