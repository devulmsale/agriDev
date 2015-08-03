package models.order;

import models.constants.DeletedStatus;
import models.product.Lable;
import models.product.ProductLable;
import play.db.jpa.Model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by upshan on 15/8/1.
 */

/**
 * 购物车属性
 */
@Entity
@Table(name = "Cart")
public class CartLable extends Model {

    /**
     * 购物车
     */
    @JoinColumn(name = "cart_id", nullable = true)
    @ManyToOne
    public Cart cart;

    /**
     * 产品属性
     */
    @JoinColumn(name = "product_lable_id", nullable = true)
    @ManyToOne
    public ProductLable productLable;

    /**
     * 创建时间
     */
    @Column(name = "created_at")
    public Date createdAt;

    /**
     * 逻辑删除,0:未删除，1:已删除
     */
    @Enumerated(EnumType.ORDINAL)
    public DeletedStatus deleted;


}
