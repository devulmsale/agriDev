package models.order;

import models.product.Product;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Created by upshan on 15/8/1.
 */

/**
 * 购物车
 */
@Entity
@Table(name = "Cart")
public class Cart extends Model {

    /**
     * 产品信息
     */
    @JoinColumn(name = "product_id", nullable = true)
    @ManyToOne
    public Product product;

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



}
