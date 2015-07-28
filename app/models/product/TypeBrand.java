package models.product;

import models.constants.DeletedStatus;
import play.db.jpa.Model;
import javax.persistence.*;
import java.util.Date;

/**
 * 类别 品牌表
 * Created by upshan on 15/7/7.
 */
@Entity
@Table(name = "type_brands")
public class TypeBrand extends Model {

    /**
     * 类别信息
     */
    @JoinColumn(name = "prodyct_type_id")
    @ManyToOne
    public ProductType productType;


    /**
     * 品牌信息
     */
    @JoinColumn(name = "brand_id")
    @ManyToOne
    public Brand brand;

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
