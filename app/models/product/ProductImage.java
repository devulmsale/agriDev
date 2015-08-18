package models.product;

import models.constants.DeletedStatus;
import models.product.enums.ImageType;
import play.db.jpa.Model;

import javax.persistence.*;
import java.util.Date;
import play.Logger;

/**
 * 类别 品牌表
 * Created by upshan on 15/7/7.
 */
@Entity
@Table(name = "product_images")
public class ProductImage extends Model {

    /**
     * 类别信息
     */
    @JoinColumn(name = "product_id")
    @ManyToOne
    public Product product;


    /**
     * 图片类型
     */
    @JoinColumn(name = "product_images_type_id")
    @ManyToOne
    public ProductImageType productImageType;


    /**
     * 图片路径.
     */
    @Column(name = "img_url", length = 200)
    public String imgUrl;

    /**
     * 统一文件上传ID.
     */
    @Column(name = "u_fid", length = 200)
    public String uFid;


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


    public static ProductImage findProductImage(Long productId){
        return ProductImage.find("deleted = ? and product.id = ? and productImageType.imageType = ? " , DeletedStatus.UN_DELETED , productId , ImageType.INDEX).first();
    }

}
