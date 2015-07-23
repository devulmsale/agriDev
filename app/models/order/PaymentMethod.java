package models.order;

import models.common.enums.PaymentStatus;
import play.db.jpa.Model;

import javax.persistence.*;
import java.util.Date;


/**
 * 支付渠道定义.
 */
@Entity
@Table(name = "payment_methods")
public class PaymentMethod extends Model {


    /**
     * 支付配置信息
     */
    @JoinColumn(name = "payment_config_id", nullable = true)
    @ManyToOne
    public PaymentConfig config;



    /**
     * 支付子代码 选择支付方式的时候使用
     */
    @Column(name = "sub_payment_code")
    public String subPaymentCode;

    /**
     * 显示名字
     */
    @Column(name = "show_name")
    public String showName;

    /**
     * logo
     */
    @Column(name = "logo")
    public String logo;

    /**
     * 排序
     */
    @Column(name = "display_order")
    public Integer displayOrder;

    /**
     * 请求参数
     */
    @Column(name = "payment_param1")
    public String paymentParam1;

    /**
     * 请求参数
     */
    @Column(name = "payment_param2")
    public String paymentParam2;

    /**
     * 请求参数
     */
    @Column(name = "payment_param3")
    public String paymentParam3;


    /**
     * 当前状态
     */
    @Enumerated(EnumType.ORDINAL)
    public PaymentStatus status;

    /**
     * 订单创建时间
     */
    @Column(name = "created_at")
    public Date createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    public Date updatedAt;



    /**
     * 更新对象.
     */
    public static PaymentMethod update(Long id, PaymentMethod newObject) {
        PaymentMethod method = PaymentMethod.findById(id);
        if (method == null) {
            return null;
        }
        method.showName = newObject.showName;
        method.save();
        return method;
    }

    public static Boolean delete(Long id) {

        PaymentMethod method = PaymentMethod.findById(id);
        if (method == null) {
            return Boolean.FALSE;
        }
        method.status = PaymentStatus.CLOSE;
        method.save();
        return Boolean.TRUE;
    }


}
