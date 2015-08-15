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
public class ProductSetMeal extends Model {

    /**
     * �����ײ�
     */
    @JoinColumn(name = "set_meal_id")
    @ManyToOne
    public SetMeal setMeal;


    /**
     * ��ӵ���Ʒ
     */
    @JoinColumn(name = "product_id")
    @ManyToOne
    public Product product;


    /**
     * �˼�
     *
     */
    @Column(name = "price")
    public BigDecimal price;


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


}
