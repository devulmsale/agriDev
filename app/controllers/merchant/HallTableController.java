package controllers.merchant;

import models.mert.hall.HallTable;
import play.modules.paginate.JPAExtPaginator;
import play.mvc.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Administrator on 2015/8/12.
 */
public class HallTableController extends Controller {
    private static final Integer PAGE_SIZE=1;


    public static  void index(Integer pageNumber){
        Map<String ,Object> map=new HashMap<String ,Object>();
        JPAExtPaginator<HallTable> resultPage=HallTable.findByCondition(map, "id asc", pageNumber, PAGE_SIZE);
        render(resultPage,pageNumber);
    }

    public  static void add(){
        render();
    }

    public static void create(HallTable hallTable){
        render();
    }

    public static void update(HallTable hallTable){

    }

}
