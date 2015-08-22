package models.address;

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
    @Column(name = "user_id")
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


    public static List<Address> findAddressByUserId(Long userId){
        return  Address.find("deleted  =  ?  and  user.id =  ?",DeletedStatus.UN_DELETED,userId).fetch();
    }

}
