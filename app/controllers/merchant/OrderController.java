package controllers.merchant;

import controllers.merchant.auth.MerchantSecure;
import controllers.system.auth.Secure;
import me.chanjar.weixin.common.util.StringUtils;
import models.common.JSONEntity;
import models.common.enums.OrderStatus;
import models.constants.DeletedStatus;
import models.operate.OperateUser;
import models.product.*;
import play.Logger;
import play.data.validation.Valid;
import play.modules.paginate.JPAExtPaginator;
import play.mvc.Controller;
import play.mvc.With;
import models.order.Order;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单管理
 */
@With(MerchantSecure.class)
public class OrderController extends Controller {

    public static Integer PAGE_SIZE = 15;
    public static String  BASE_RETURN_INDEX = "/merchant/product";

    public static void index(Integer pageNumber  , String searchName) {
        initData();
        pageNumber = pageNumber == null ? 1 : pageNumber;
        Map<String , Object> searchMap = new HashMap<>();
        if(StringUtils.isNotBlank(searchName)) {
            searchMap.put("searchName", "%"+searchName+"%");
        }
        searchMap.put("status", OrderStatus.UNPAID);
        JPAExtPaginator<Order> resultPage = Order.findByCondition(searchMap, "id desc", pageNumber, PAGE_SIZE);
        render(resultPage, pageNumber);
    }

    public static void add() {
        render();
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
            ProductImageType productImageType=ProductImageType.findById(Long.valueOf(pid));
            Logger.info("uf :%s || pid :%s",uf,pid);
            ProductImage productImage=new ProductImage();
            Logger.info("UFID :%s=",ufid);
            productImage.deleted=DeletedStatus.UN_DELETED;
            productImage.createdAt=new Date();
            productImage.product=product;
            productImage.uFid=uf;
            productImage.productImageType=productImageType;
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

        render();
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



    private static void initData() {
        OperateUser operateUser = Secure.getOperateUser();
        renderArgs.put("operateUser" , operateUser);

        Long count = 8l;
        renderArgs.put("emailCount" , count);
    }
}