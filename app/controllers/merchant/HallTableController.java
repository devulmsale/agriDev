package controllers.merchant;

import controllers.merchant.auth.MerchantSecure;
import models.constants.DeletedStatus;
import models.mert.hall.HallTable;
import models.mert.hall.MerchantHall;
import play.Logger;
import play.data.validation.Valid;
import play.modules.paginate.JPAExtPaginator;
import play.mvc.Controller;
import play.mvc.With;

import java.util.*;

/**
 * Created by Administrator on 2015/8/12.
 */
@With(MerchantSecure.class)
public class HallTableController extends Controller {
    private static final Integer PAGE_SIZE=15;


    public static  void index(Integer pageNumber){
        if(pageNumber==null){
            pageNumber=1;
        }
        Map<String ,Object> map=new HashMap<String ,Object>();
        JPAExtPaginator<HallTable> resultPage=HallTable.findByCondition(map, "id asc", pageNumber, PAGE_SIZE);
        render(resultPage,pageNumber);
    }

    public  static void add(){
        List<MerchantHall> hallsList=MerchantHall.findByMerchant(MerchantSecure.getMerchant());
        render(hallsList);
    }

    public  static void edit(Long id,Integer pageNumber){

        HallTable hallTable=HallTable.findById(id);
        List<MerchantHall> hallsList=MerchantHall.findByMerchant(MerchantSecure.getMerchant());
        render(hallTable,hallsList,pageNumber);
    }

    public static void create(@Valid HallTable hallTable,Integer pageNumber ){
        if(validation.hasErrors()){
            params.flash();
            validation.keep();
            add();
        }

        hallTable.deleted= DeletedStatus.UN_DELETED;
        hallTable.createdAt=new Date();

        hallTable.save();
        index(pageNumber);
    }

    public static void update(Long id ,@Valid HallTable hallTable,Integer pageNumber){
        if(validation.hasErrors()){
            params.flash();
            validation.keep();
            edit(id,pageNumber);
        }
        HallTable.update(id,hallTable);
        index(pageNumber);
    }

}
