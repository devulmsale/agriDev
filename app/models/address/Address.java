package models.address;

import jodd.bean.BeanCopy;
import models.constants.DeletedStatus;
import models.order.User;
import play.db.jpa.Model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2015/8/21.
 */
@Entity
@Table(name = "user_receive_address")
public class Address extends Model {
    /**
     * 关联用户
     */
    @JoinColumn(name = "user_id")
    @ManyToOne
    public User user;

    /**
     * 省
     */
    @Column(name = "province")
    public String province;

    /**
     * 市
     */
    @Column(name = "city")
    public String city;

    /**
     * 区
     */
    @Column(name = "local")
    public String local;

    /**
     * 详细地址
     */
    @Column(name = "address")
    public String address;

    /**
     * 是否是默认地址
     * 0: 默认地址
     * 1:非默认地址
     */
    @Column(name = "default_type")
    public String defaultype;

    /**收货人姓名
     *
     */
    @Column(name = "name")
    public String name;
    /*
       联系电话
     */
    @Column(name = "phone")
    public String phone;

    /**
     * 逻辑删除,0:未删除，1:已删除
     */
    @Enumerated(EnumType.ORDINAL)
    public DeletedStatus deleted;

    /**
     * 创建时间
     */
    @Column(name = "created_at")
    public Date createdAt;


    /**
     * 更新
     * @param id
     * @param newObject
     */
    public static void update(Long id, Address newObject) {
        Address address=Address.findById(id);
        BeanCopy.beans(newObject, address).ignoreNulls(true).copy();
        address.save();
    }


    public static List<Address> findAddressByUserId(Long userId){
        return  Address.find("deleted  =  ?  and  user.id =  ?",DeletedStatus.UN_DELETED,userId).fetch();
    }
    //获取当前用户的默认地址
    public static Address findDefaultAddressByUser(Long userId){
        return  Address.find("deleted  =  ?  and  user.id =  ? and defaultype  = ? ",DeletedStatus.UN_DELETED,userId,"0").first();
    }

    public static Address findAddressById(Long id){
        return  Address.find("deleted  =  ?  and  id  =  ?",DeletedStatus.UN_DELETED,id).first();
    }

    public static Address findAddressByUserAndId(Long userId , Long id){
        return  Address.find("deleted  =  ?  and  user.id =  ? and id  = ? ",DeletedStatus.UN_DELETED,userId,id).first();
    }

}
