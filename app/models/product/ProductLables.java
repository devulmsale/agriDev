package models.product;

import models.constants.DeletedStatus;
import play.db.jpa.Model;
import javax.persistence.*;
import java.util.Date;

/**
 * 商品标签表
 * Created by upshan on 15/7/7.
 */
@Entity
@Table(name = "product_lables")
public class ProductLables extends Model {


    /**
     * 商品信息
     */
    @JoinColumn(name = "product_id")
    @ManyToOne
    public Product product;


    /**
     * 所属品牌
     */
    @JoinColumn(name = "品牌信息")
    @ManyToOne
    public Lable lable;

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
