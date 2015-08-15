package models.product;

import models.constants.DeletedStatus;
import models.mert.Merchant;
import play.Logger;
import play.data.validation.Required;

import play.db.jpa.JPA;
import play.db.jpa.Model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2015/8/13.
 */
@Entity
@Table(name = "product_setmeals")
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
    @Required(message = "请选择商品，不能为空")
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


    public static List<ProductSetMeal> findProductSetMealBySetMeal(SetMeal setMeal){
       return ProductSetMeal.find("deleted = ? and setMeal.id = ?",DeletedStatus.UN_DELETED,setMeal.id).fetch();
    }


    public static ProductSetMeal findProductSetMealBySetMealAndProduct(Long setMealId , Long productId){
        return ProductSetMeal.find("deleted = ? and setMeal.id = ? and product.id = ?", DeletedStatus.UN_DELETED, setMealId, productId).first();
    }

    public static void  deleteBySetMealId(Long setMealId){
       // Query query = JPA.em().createQuery("select * from Article");
     //   ProductSetMeal.
     //  String sql = "update product_setmeals p set p.deleted = :deleted where p.set_meal_id = :setMealId";
     //  Query query =   ProductSetMeal.em().createQuery(sql);
      //  Query query =  JPA.em().createQuery(" update Order as o set o.amount=o.amount+10 ");
        //update 的记录数
        StringBuffer sb=new StringBuffer();
        sb.append("update product_setmeals p set p.deleted =");
        sb.append(" "+DeletedStatus.DELETED.getValue());
        sb.append(" where p.set_meal_id =");
        sb.append(setMealId);
        sb.append("  and  p.deleted =");
        sb.append(""+DeletedStatus.UN_DELETED.getValue());
        //创建sql查询
        Logger.info("sb==="+sb.toString());
        Query query = ProductSetMeal.em().createNativeQuery(sb.toString(), ProductSetMeal.class);
        query.executeUpdate();
    }


}
