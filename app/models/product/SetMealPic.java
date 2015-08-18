package models.product;

import jodd.bean.BeanCopy;
import models.constants.DeletedStatus;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2015/8/14.
 */
@Entity
@Table(name = "set_meal_pics")
public class SetMealPic extends Model {
    /**
     * 所属套餐
     */
    @JoinColumn(name = "set_meal_id")
    @ManyToOne
    public SetMeal setMeal;
    /**
     * 图片唯一的ufId
     */
    @Column(name = "ufid")
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


    /**
     * 根据套餐选取图片
     * @param id
     * @return
     */
    public static List<SetMealPic> findBySetMeal(Long id){
        return SetMealPic.find("deleted = ? and setMeal.id = ?",DeletedStatus.UN_DELETED,id).fetch();
    }

    public static SetMealPic findBySetMealId(Long id){
       return SetMealPic.find("deleted = ? and setMeal.id = ?",DeletedStatus.UN_DELETED,id).first();
    }

    /**
     * 根据商户获取图片跟套餐
     */
    public static List<SetMealPic> findByMerchantId(Long  merchantId){
        return SetMealPic.find("deleted = ? and setMeal.merchant.id = ?",DeletedStatus.UN_DELETED,merchantId).fetch();
    }

    /**
     * 更新
     * @param id
     * @param newObject
     */
    public static void update(Long id, SetMealPic newObject) {
        SetMealPic setMealPic=SetMealPic.findById(id);
        BeanCopy.beans(newObject, setMealPic).ignoreNulls(true).copy();
        setMealPic.save();
    }

}
