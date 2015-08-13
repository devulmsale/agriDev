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
public class ProductPackage extends Model {
    /**
     * �ײ�����
     */
    @Column(name = "name")
    public String name;
    /*
    ������֪
     */
    @Column(name = "content")
    public String content;

    /**
     * ��ӵ���Ʒ
     */
    @JoinColumn(name = "product_id")
    @ManyToOne
    public Product product;

    /**
     * �����̼�
     */
    @JoinColumn(name = "merchant_id")
    @ManyToOne
    public Merchant merchant;

    /**
     * �ּ�
     *
     */
    @Column(name = "present_price")
    public BigDecimal presentPrice;


    /**
     * �ײ�ʹ��ʱ��
     */
    @Column(name = "begin_date")
    public Date beginDate;

    /**
     * �ײͽ���ʱ��
     */
    @Column(name = "end_date")
    public Date endDate;



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
