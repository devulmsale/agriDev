package models.order;

import models.constants.DeletedStatus;
import models.product.Product;
import play.db.jpa.Model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;


/**
 * Created by upshan on 15/8/1.
 */

/**
 * 购物车
 */
@Entity
@Table(name = "cart")
public class Cart extends Model {

    /**
     * 商品信息
     */
    @JoinColumn(name = "goods_id", nullable = true)
    @ManyToOne
    public Goods goods;

    /**
     * 订单用户
     */
    @JoinColumn(name = "user_id", nullable = true)
    @ManyToOne
    public User user;

    /**
     * 数量
     */
    public Integer number;

    /**
     * 逻辑删除,0:未删除，1:已删除
     */
    @Enumerated(EnumType.ORDINAL)
    public DeletedStatus deleted;

    /**
     * 备注信息
     */
    @Column(name = "remark")
    public String remark;


    /**
     * 创建时间
     */
    @Column(name = "created_at")
    public Date createdAt;



    public static List<Cart> findCartsByUser(){
        return Cart.find("deleted = ?",DeletedStatus.UN_DELETED).fetch();
    }
}
