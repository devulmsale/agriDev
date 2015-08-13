package controllers.merchant;

import controllers.merchant.auth.MerchantSecure;
import models.constants.DeletedStatus;
import models.coupon.CouponBatch;
import models.mert.Merchant;
import models.mert.hall.MerchantHall;
import play.Logger;
import play.data.validation.Valid;
import play.modules.paginate.JPAExtPaginator;
import play.mvc.Controller;
import play.mvc.With;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * Created by Administrator on 2015/8/12.
 */
@With(MerchantSecure.class)
public class MerHallController extends Controller {

    private  static final Integer PAGE_SIZE=15;
    private static final String  BASE_INDEX_URL = "/merchant/hall/index";
    /**
     *
     * ��ת�������б�ҳ��
     *
     */
    public static  void index(Integer pageNumber){
        if(pageNumber==null){
            pageNumber=1;
        }
        Map<String,Object>  map=new HashMap<String,Object>();
        JPAExtPaginator<MerchantHall> resultPage=MerchantHall .findByCondition(map, "id asc", pageNumber, PAGE_SIZE);
        render(resultPage, pageNumber);
    }

    /**
     * ��ת�����Ӵ���ҳ��
     */

    public static void add(){
        render();
    }

    /**
     * ��ת������ҳ��
     */
     public static void edit(Long  id,Integer pageNumber){
      MerchantHall merchantHall=MerchantHall.findById(id);
         render(merchantHall,pageNumber);
     }

    /**
     * ���Ӵ�������
     * @param merchantHall
     */
    public static  void  create(@Valid MerchantHall merchantHall){
        if(validation.hasErrors()){
            params.flash();
            validation.keep();
            add();
        }
        merchantHall.merchant= MerchantSecure.getMerchant();
        merchantHall.deleted= DeletedStatus.UN_DELETED;
        merchantHall.createdAt=new Date();
        merchantHall.save();
        index(null);
    }


    /**
     * ���´�������
     * @param merchantHall
     */
    public static  void  update(Long id , @Valid MerchantHall merchantHall,Integer pageNumber){
        if(validation.hasErrors()){
            params.flash();
            validation.keep();
            edit(id,pageNumber);
        }
       MerchantHall.update(id, merchantHall);
     //  redirect(BASE_INDEX_URL+"?page");
        index(pageNumber);
    }

}
