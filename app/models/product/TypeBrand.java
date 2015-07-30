package models.product;

import models.common.JSONEntity;
import models.constants.DeletedStatus;
import play.Logger;
import play.data.validation.Required;
import play.db.jpa.Model;
import javax.persistence.*;
import java.util.*;

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
    @Required(message = "品牌信息不能为空")
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

    public TypeBrand() {
        super();
    }

    public TypeBrand(ProductType type , Brand brand) {
        this.productType = type;
        this.brand = brand;
        this.deleted=DeletedStatus.UN_DELETED;
        this.createdAt = new Date();
        this.save();
    }


    public static List<TypeBrand> findByProductType(Long parentTypeId){
        return TypeBrand.find("deleted = ? and productType.id = ?" , DeletedStatus.UN_DELETED , parentTypeId).fetch();
    }

    public static List<TypeBrand> findByProduct(Long productId) {
        Product product = Product.findById(productId);
        return findByProductType(product.parentType.id);
    }



    public static List<JSONEntity> getMapProductType(Long productTypeId) {
        List<TypeBrand> typeBrandList = findByProductType(productTypeId);
        List<JSONEntity> jsonEntityList = new ArrayList<>();
        JSONEntity jsonEntity = null;
        for(TypeBrand tb : typeBrandList) {
            jsonEntity = new  JSONEntity();
            jsonEntity.id = tb.brand.id.toString();
            jsonEntity.name = tb.brand.name;
            jsonEntityList.add(jsonEntity);
        }
         return jsonEntityList;
    }


}
