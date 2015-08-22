package controllers.merchant;

import controllers.merchant.auth.MerchantSecure;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import models.constants.DeletedStatus;
import models.mert.Merchant;
import models.mert.MerchantUser;
import models.product.Product;
import models.product.ProductSetMeal;
import models.product.SetMeal;
import models.product.SetMealPic;
import play.Logger;
import play.data.validation.Valid;
import play.modules.paginate.JPAExtPaginator;
import play.mvc.Controller;
import play.mvc.With;

import java.util.*;

/**
 * Created by Administrator on 2015/8/13.
 */
@With(MerchantSecure.class)
public class ProductSetMealController extends Controller {

    private static final Integer PAGE_SIZE=15;

    private static final String  FOLDER_URL="http://img.ulmsale.cn/folderJSON/";

    /**
     * 跳转套餐列表
     * @param pageNumber
     */
    public static  void index(Integer pageNumber){

        pageNumber = pageNumber == null ? 1 : pageNumber;
        Map<String ,Object> map=new HashMap<String ,Object>();
        map.put("merchantId",MerchantSecure.getMerchant().id);
        JPAExtPaginator<SetMeal> resultPage=SetMeal.findByCondition(map, "id asc", pageNumber, PAGE_SIZE);
        render(resultPage,pageNumber);
    }

    /**
     * 跳转到套餐增加的页面
     */
    public static void add(){
        List<Product> productList=Product.findProductByMerchant(12l);

        render(productList);
    }

    /**
     * 套餐展示
     * @param id
     */
    public static void show(Long id){

        SetMeal setMeal=SetMeal.findById(id);

        List<ProductSetMeal> productSetMealList=ProductSetMeal.findProductSetMealBySetMeal(setMeal);

        render(setMeal,productSetMealList);
    }

    /**
     * 跳转到修改图片的页面
     * @param id
     */
    public static void editPic(Long id){
       List<SetMealPic> setMealPicList= SetMealPic.findBySetMeal(id);
      List<SetMealPic> picList=new ArrayList<SetMealPic>();
        //Logger.info("size---"+setMealPicList.size());
        for(SetMealPic smp:setMealPicList){
            HttpRequest httpRequest = HttpRequest
                    .get("http://img.ulmsale.cn/getImageUrl")
                    .form(
                            "uFid", smp.uFid
                    );
            HttpResponse httpResponse = httpRequest.send();
            String responseBody = httpResponse.body();
            smp.uFid=responseBody;
            picList.add(smp);
        }
        render(picList,id);
    }

    /**
     * 跳转更新图片页面
     * @param setMealPicId
     * @param id
     */
    public static void upEditPic(Long setMealPicId,Long id){
         render(setMealPicId, id);
    }

    /**
     * 更新图片
     * @param setMealPicId
     * @param uFid
     * @param id
     */
    public static void updatePic(Long setMealPicId,String uFid,Long id){
        SetMealPic setMealPic=SetMealPic.findById(setMealPicId);
        setMealPic.uFid=uFid;
        SetMealPic.update(setMealPicId,setMealPic);
        editPic(id);
    }


    /**
     * 跳转到上传图片的页面
     * @param setMealPicId//修改图片时用到
     * @param setMealId//添加套餐后下一步 上传图片时用到
     */
    public static void upPic(Long  setMealId,Long setMealPicId){
        render(setMealId, setMealPicId);
    }

    /**
     * 增加图片
     * @param setMealId
     * @param uFid
     */
    public static void createPic(Long setMealId,String uFid){
        Logger.info("setMealId-==" + setMealId);
        SetMealPic setMealPic=null;
        SetMeal setMeal=new SetMeal();
        setMeal.id=setMealId;
        if(uFid!=null&&!uFid.equals("")){
           String[] ufIds= uFid.split(",");
            for(int i=0;i<ufIds.length;i++){
                setMealPic=new SetMealPic();
                setMealPic.setMeal=setMeal;
                setMealPic.uFid=ufIds[i];
                setMealPic.createdAt=new Date();
                setMealPic.deleted=DeletedStatus.UN_DELETED;
                setMealPic.save();
            }
        }
        index(1);
    }

    /**
     * 增加套餐
     * @param setMeal
     * @param productId
     */
    public static void create(@Valid SetMeal setMeal,Long[] productId){

        if(validation.hasErrors()){
            params.flash();
            validation.keep();
          add();
        }
        if(productId==null||productId.length==0){
            flash.put("msg" , "添加的商品不能为空");
            add();
        }
        setMeal.merchant=MerchantSecure.getMerchant();
        setMeal.createdAt=new Date();
        setMeal.deleted=DeletedStatus.UN_DELETED;
        setMeal.save();
//        Logger.info("leng"+productId.length);
//        Logger.info("chomc===" + productId[1]);

        ProductSetMeal productSetMeal = null;
        for(int i=0;i<productId.length;i++){
            Logger.info("ddds=="+productId[i]);
            Product product= Product.findById(Long.valueOf(productId[i]));
            if(product != null) {
                productSetMeal = new ProductSetMeal();
                productSetMeal.product=product;
                productSetMeal.setMeal=setMeal;
                productSetMeal.createdAt=new Date();
                productSetMeal.deleted= DeletedStatus.UN_DELETED;
                productSetMeal.save();
            }
        }
       // index();
        upPic(setMeal.id, null);
    }

    /**
     * 跳转到更新套餐页面
     */
    public static void  edit(Long id,Integer pageNumber){
        SetMeal setMeal =SetMeal.findById(id);
        List<Product> productList=Product.findProductByMerchant(12l);
        render(productList, setMeal,pageNumber);
    }

    /**
     * 保存更新的的套餐
     */
    public static void update(Long id,@Valid SetMeal setMeal,Long[] productId,Integer pageNumber){
        if(validation.hasErrors()){
            params.flash();
            validation.keep();
            edit(id,pageNumber);
        }
        if(productId==null||productId.length==0){
            flash.put("msg" , "添加的商品不能为空");
            edit(id,pageNumber);
        }
        SetMeal.update(id, setMeal);
        ProductSetMeal.deleteBySetMealId(id);
        SetMeal newSetMeal=new SetMeal();
        newSetMeal.id=id;
        ProductSetMeal productSetMeal = null;
        for(int i=0;i<productId.length;i++){
            Product product= Product.findById(Long.valueOf(productId[i]));
            if(product != null) {
                productSetMeal = new ProductSetMeal();
                productSetMeal.product=product;
                productSetMeal.setMeal=newSetMeal;
                productSetMeal.createdAt=new Date();
                productSetMeal.deleted= DeletedStatus.UN_DELETED;
                productSetMeal.save();
            }
        }
        index(pageNumber);
    }


}
