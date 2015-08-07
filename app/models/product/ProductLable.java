package models.product;

import models.constants.DeletedStatus;
import play.Logger;
import play.db.jpa.Model;
import javax.persistence.*;
import java.util.Date;

/**
 * 商品标签表
 * Created by upshan on 15/7/7.
 */
@Entity
@Table(name = "product_lables")
public class ProductLable extends Model {

    /**
     * 商品信息
     */
    @JoinColumn(name = "product_id")
    @ManyToOne
    public Product product;


    /**
     * 所属品牌
     */
    @JoinColumn(name = "lable_id")
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

    public ProductLable(){
        super();
    }

    public ProductLable(Product product , Lable lable){
        this.product=product;
        this.lable=lable;
        this.deleted = DeletedStatus.UN_DELETED;
        this.createdAt = new Date();
        this.save();
    }

    public static Boolean isHaveLable(Long productId , Long lableId) {
        Logger.info("lableId :%s=  this.ID : %s", lableId, productId);
        Long count =  ProductLable.count("product.id = ? and lable.id = ? and deleted = ?" , productId , lableId , DeletedStatus.UN_DELETED);
        return count > 0 ? true : false;
    }
}
