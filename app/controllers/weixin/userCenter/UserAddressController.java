package controllers.weixin.userCenter;

import models.address.Address;
import models.constants.DeletedStatus;
import models.order.Order;
import models.order.User;
import play.data.validation.Valid;
import play.mvc.Controller;

import java.util.Date;

/**
 * Created by Administrator on 2015/8/21.
 */
//@With(WxMpAuth.class)
public class UserAddressController extends Controller {

    public static void add(){
        render();
    }

    public static void crete(@Valid Address address , String checkbox){
        //TODO user_id
        //User user = WxMpAuth.currentUser().user;
        User user=new User();
        user.id=2l;
        if(validation.hasErrors()){
          params.flash();
          validation.keep();
          add();
        }
        if(checkbox == null){
            address.defaultype="1";

        }else{
            Address defaultAddress=Address.findDefaultAddressByUser(user.id);
            if(defaultAddress!=null){
                defaultAddress.defaultype="1";
                defaultAddress.save();
            }
                address.defaultype="0";


        }
        address.user=user;
        address.deleted = DeletedStatus.UN_DELETED;
        address.createdAt = new Date();
        address.save();
        redirect("/weixin/member/detail");
    }

    public static void  edit(Long addressId){
        Address address= Address.findAddressById(addressId);
        render(address);
    }

    public static  void update(@Valid Address address , Long addressId , String checkbox){
        //TODO user_id
        //User user = WxMpAuth.currentUser().user;
        User user=new User();
        user.id=2l;
        if(checkbox != null){
            Address defaultAddress=Address.findDefaultAddressByUser(user.id);
            if(defaultAddress!=null){
                defaultAddress.defaultype="1";
                defaultAddress.save();
            }
            address.defaultype="0";
        }
        Address.update(addressId,address);

        redirect("/weixin/member/detail");
    }

}
