package models.product;

import models.common.JSONEntity;
import models.constants.DeletedStatus;
import play.db.jpa.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 类别 属性表
 * Created by upshan on 15/7/7.
 */
@Entity
@Table(name = "type_lable")
public class TypeLable extends Model {

    /**
     * 类别信息
     */
    @JoinColumn(name = "prodyct_type_id")
    @ManyToOne
    public ProductType productType;


    /**
     * 属性表
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

    public TypeLable(){
        super();
    }

    public TypeLable(ProductType type ,Lable lable){
        this.productType=type;
        this.lable=lable;
        this.deleted = DeletedStatus.UN_DELETED;
        this.createdAt = new Date();
        this.save();

    }

    /**
     * 根据类别ID取属性信息
     * @param parentTypeId
     * @return
     */
    public static List<TypeLable> findByProductType(Long parentTypeId){
        return TypeLable.find("deleted = ? and productType.id = ?" , DeletedStatus.UN_DELETED , parentTypeId).fetch();
    }
    public static List<JSONEntity> getMapProductType(Long productTypeId) {
        List<TypeLable> typeLableList = findByProductType(productTypeId);
        List<JSONEntity> jsonEntityList = new ArrayList<>();
        JSONEntity jsonEntity = null;
        for(TypeLable tb : typeLableList) {
            jsonEntity = new  JSONEntity();
            jsonEntity.id = tb.id.toString();
            jsonEntity.name = tb.lable.name;
            jsonEntityList.add(jsonEntity);
        }
        return jsonEntityList;
    }

}
