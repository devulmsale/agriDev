package models.product;

import models.constants.DeletedStatus;
import models.mert.Merchant;
import play.db.jpa.Model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Administrator on 2015/8/13.
 */
@Entity
@Table(name = "product_packages")
public class ProductSetMeal extends Model {

    /**
     * 所属套餐
     */
    @JoinColumn(name = "set_meal_id")
    @ManyToOne
    public SetMeal setMeal;


    /**
     * 添加的商品
     */
    @JoinColumn(name = "product_id")
    @ManyToOne
    public Product product;


    /**
     * 菜价
     *
     */
    @Column(name = "price")
    public BigDecimal price;


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
