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
public class ProductPackage extends Model {
    /**
     * 套餐名称
     */
    @Column(name = "name")
    public String name;
    /*
    购买须知
     */
    @Column(name = "content")
    public String content;

    /**
     * 添加的商品
     */
    @JoinColumn(name = "product_id")
    @ManyToOne
    public Product product;

    /**
     * 所属商家
     */
    @JoinColumn(name = "merchant_id")
    @ManyToOne
    public Merchant merchant;

    /**
     * 现价
     *
     */
    @Column(name = "present_price")
    public BigDecimal presentPrice;


    /**
     * 套餐使用时间
     */
    @Column(name = "begin_date")
    public Date beginDate;

    /**
     * 套餐结束时间
     */
    @Column(name = "end_date")
    public Date endDate;



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
