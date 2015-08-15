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
     * �����ײ�
     */
    @JoinColumn(name = "set_meal_id")
    @ManyToOne
    public SetMeal setMeal;
    /**
     * ͼƬΨһ��ufId
     */
    @Column(name = "ufid")
    public String uFid;




    /**
     * ����ʱ��
     */
    @Column(name = "created_at")
    public Date createdAt;

    /**
     * �߼�ɾ��,0:δɾ����1:��ɾ��
     */
    @Enumerated(EnumType.ORDINAL)
    public DeletedStatus deleted;


    public static List<SetMealPic> findBySetMeal(Long id){
        return SetMealPic.find("deleted = ? and setMeal.id = ?",DeletedStatus.UN_DELETED,id).fetch();
    }

    /**
     * ����
     * @param id
     * @param newObject
     */
    public static void update(Long id, SetMealPic newObject) {
        SetMealPic setMealPic=SetMealPic.findById(id);
        BeanCopy.beans(newObject, setMealPic).ignoreNulls(true).copy();
        setMealPic.save();
    }

}
