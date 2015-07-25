package controllers.merchant;

import controllers.merchant.auth.MerchantSecure;
import controllers.system.auth.Secure;
import me.chanjar.weixin.common.util.StringUtils;
import models.constants.DeletedStatus;
import models.mert.Merchant;
import models.mert.MerchantUser;
import models.operate.OperateUser;
import models.product.Brand;
import org.apache.commons.codec.digest.DigestUtils;
import play.Logger;
import play.data.validation.Valid;
import play.modules.paginate.JPAExtPaginator;
import play.mvc.Controller;
import play.mvc.With;
import util.common.RandomNumberUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@With(MerchantSecure.class)
public class BrandController extends Controller {

    public static Integer PAGE_SIZE = 15;
    public static String  BASE_RETURN_INDEX = "/merchant/brands";

    public static void index(Integer pageNumber  , String searchName) {
        initData();
        pageNumber = pageNumber == null ? 1 : pageNumber;
        Map<String , Object> searchMap = new HashMap<>();
        if(StringUtils.isNotBlank(searchName)) {
            searchMap.put("searchName", "%"+searchName+"%");
        }
        JPAExtPaginator<Brand> resultPage = Brand.findByCondition(searchMap, "id asc", pageNumber, PAGE_SIZE);
        render(resultPage, pageNumber);
    }

    public static void add() {
        render();
    }

    public static void create(@Valid Brand brand) {
        if(validation.hasErrors()) {
            params.flash(); // add http parameters to the flash scope
            validation.keep(); // keep the errors for the next request
            add();
        }
        brand.deleted = DeletedStatus.UN_DELETED;
        brand.save();
        if(brand.brand != null) {
            addChild(brand.brand.id);
        } else {
            redirect(BASE_RETURN_INDEX);
        }
    }

    public static void edit(Long id , Integer pageNumber) {
        Brand brand = Brand.findById(id);
        render(brand , pageNumber);
    }

    public static void update(Long id , Brand brand) {
        Brand.update(id , brand);
        redirect(BASE_RETURN_INDEX);
    }


    public static void delete(Long id , Integer pageNumber) {
        Brand brand = Brand.findById(id);
        if(brand != null && brand.brand == null) {
            brand.deleted = DeletedStatus.UN_DELETED;
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




    private static void initData() {
        MerchantUser merchantUser = MerchantSecure.getMerchantUser();
        renderArgs.put("merchantUser" , merchantUser);
    }

}