package controllers.merchant;

import controllers.merchant.auth.MerchantSecure;
import controllers.system.auth.Secure;
import helper.imageupload.ImageUploadResult;
import helper.imageupload.ImageUploader;
import me.chanjar.weixin.common.util.StringUtils;
import models.common.JSONEntity;
import models.constants.DeletedStatus;
import models.mert.MerchantProductType;
import models.operate.OperateUser;
import models.product.*;
import models.product.enums.MarketingMode;
import models.product.enums.PackageMethod;
import models.product.enums.ShippingMethod;
import models.product.enums.StoreMethod;
import play.Logger;
import play.data.validation.Valid;
import play.modules.paginate.JPAExtPaginator;
import play.mvc.Controller;
import play.mvc.With;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品管理
 */
@With(MerchantSecure.class)
public class ProductController extends Controller {

    public static Integer PAGE_SIZE = 15;
    public static String  BASE_RETURN_INDEX = "/merchant/product";

    public static void index(Integer pageNumber  , String searchName) {
        initData();
        pageNumber = pageNumber == null ? 1 : pageNumber;
        Map<String , Object> searchMap = new HashMap<>();
        if(StringUtils.isNotBlank(searchName)) {
            searchMap.put("searchName", "%"+searchName+"%");
        }
       // searchMap.put("isTop" , true);
        JPAExtPaginator<Product> resultPage = Product.findByCondition(searchMap, "id asc", pageNumber, PAGE_SIZE);
        render(resultPage, pageNumber);
    }

    public static void add() {
        //查询所有类别
        List<ProductType> productTypelist=ProductType.findProductType();
        //查询一级类别
        List<ProductType> productTopTypeList=ProductType.findTopType();
        //储藏方式
        StoreMethod[] storeMethods=StoreMethod.values();
        //包装方式
        PackageMethod[] packageMethods=PackageMethod.values();
        //配送方式
        ShippingMethod[] shippingMethods=ShippingMethod.values();
        //营销模式
        MarketingMode[] marketingModes=MarketingMode.values();
       //取商户商品类别
        Logger.info("商户Id的 :%s=",MerchantSecure.getMerchant().id);
        List<MerchantProductType> merchantProductTypeList=MerchantProductType.findMerchantProductType(MerchantSecure.getMerchant().id);
        Logger.info("商户商品类别:%s=",merchantProductTypeList.size());
        //取图片类型
        List<ProductImageType> productImageTypeList=ProductImageType.findByproductImageType();
        render(productTypelist, productTopTypeList, storeMethods, packageMethods, shippingMethods, marketingModes, merchantProductTypeList ,productImageTypeList);
    }

    public static void create(@Valid Product product , String lablebox , File image , String imageName) throws Exception {
        if(validation.hasErrors()) {
            params.flash(); // add http parameters to the flash scope
            validation.keep(); // keep the errors for the next request
            add();
        }
        product.createdAt= new Date();
        product.deleted=DeletedStatus.UN_DELETED;
        Logger.info("商品所属商户: %s=",MerchantSecure.getMerchant());
        product.merchant=MerchantSecure.getMerchant();
        /*if (image != null && image.getName() != null) {
            // 图片上传上去
            ImageUploadResult imageUploadResult = ImageUploader.upload(image);
            //121312310010231  adbdsdf
            product.listUFID =  imageUploadResult.ufId;
            //http://121.42.146.152:9292/images/121312310010231.jpg
            product.listImage = ImageUploader.getImageUrl(imageUploadResult.ufId, "640x320");
        }*/

        product.save();

        //保存图片UFID
        Logger.info("商品图片ufid : %s=",imageName);
        String [] ufids=imageName.split(",");

        for(String ufid:ufids){
            String [] productImageTypeId=ufid.split("_");
            String uf=productImageTypeId[0];
            String pid=productImageTypeId[1];
            String imgUrl=productImageTypeId[2];
            ProductImageType productImageType=ProductImageType.findById(Long.valueOf(pid));
            Logger.info("uf :%s || pid :%s",uf,pid);
            ProductImage productImage=new ProductImage();
            Logger.info("UFID :%s=",ufid);
            productImage.deleted=DeletedStatus.UN_DELETED;
            productImage.createdAt=new Date();
            productImage.product=product;
            productImage.uFid=uf;
            productImage.productImageType=productImageType;
            productImage.imgUrl=imgUrl;
            productImage.save();
            Logger.info("商品图片保存");
        }

        //保存属性
        if( null != lablebox) {
            String[] lables = lablebox.split(",");
            Lable lable = null;
            for (String lableId : lables) {
                lable = Lable.findById(Long.valueOf(lableId.trim()));
                if (lable != null && product != null) {
                    new ProductLable(product, lable);
                }
            }
        }
        redirect(BASE_RETURN_INDEX);


    }

    public static void edit(Long id , Integer pageNumber) {
        Product product = Product.findById(id);
        //查询一级类别
        List<ProductType> productTopTypeList=ProductType.findTopType();
        //查询所有类别
        List<ProductType> productTypelist=ProductType.findProductType();
        //储藏方式
        StoreMethod[] storeMethods=StoreMethod.values();
        //包装方式
        PackageMethod[] packageMethods=PackageMethod.values();
        //配送方式
        ShippingMethod[] shippingMethods=ShippingMethod.values();
        //营销模式
        MarketingMode[] marketingModes=MarketingMode.values();
        render(product ,productTopTypeList , pageNumber , productTypelist , storeMethods , packageMethods , shippingMethods , marketingModes);
    }

    public static void update(Long id , Product product) {
        Product.update(id , product);
        redirect(BASE_RETURN_INDEX);
    }


    public static void delete(Long id , Integer pageNumber) {
        Product product = Product.findById(id);
        if(product != null) {
            product.deleted = DeletedStatus.DELETED;
            product.save();
        }
        redirect(BASE_RETURN_INDEX);
    }


    public static void getBrandAndLable(Long parentTypeId ) {
        Logger.info("parentTypeId :%s=",parentTypeId);
        Map<String , Object> resultMap = new HashMap<>();
        List<JSONEntity> typeBrandList=TypeBrand.getMapProductType(parentTypeId);
        List<JSONEntity> typeLableList=TypeLable.getMapProductType(parentTypeId);
        resultMap.put("brands" , typeBrandList);
        resultMap.put("lables" , typeLableList);
        renderJSON(resultMap);

    }

    public static void getSubClass(Long parentTypeId){
        Logger.info("parentTypeIdsubclass :%s=",parentTypeId);
        Map<String , Object> resultMap = new HashMap<>();
        //查询二级类别
        List<ProductType> productTypeList=ProductType.findByParentType(parentTypeId);
        resultMap.put("subclasss",productTypeList);
        renderJSON(resultMap);
    }

    private static void initData() {
        OperateUser operateUser = Secure.getOperateUser();
        renderArgs.put("operateUser" , operateUser);

        Long count = 8l;
        renderArgs.put("emailCount" , count);
    }
}