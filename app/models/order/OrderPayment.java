package models.order;

import play.db.jpa.Model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 订单支付记录.
 */
@Entity
@Table(name = "order_payments")
public class OrderPayment extends Model {


    /**
     * 订单信息
     */
    @JoinColumn(name = "order_id", nullable = true)
    @ManyToOne
    public Order order;



    /**
     * 支付方式
     */
    @Column(name = "pay_method")
    public String pay_method;

    /**
     * 通过网银支付金额
     */
    @Column(name = "payment_amount")
    public BigDecimal paymentAmount;

    /**
     * 支付交易号
     */
    @Column(name = "payment_trade_no")
    public String paymentTradeNo;


    /**
     * 订单创建时间
     */
    @Column(name = "created_at")
    public Date createdAt;

    /**
     * 订单支付时间
     */
    @Column(name = "paid_at")
    public Date paidAt;

    /**
     * 结算时间
     */
    @Column(name = "cashed_at")
    public Date cashedAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    public Date updatedAt;




    /**
     * 更新对象.
     */
    public static OrderPayment update(Long id, OrderPayment newObject) {
        OrderPayment payment = OrderPayment.findById(id);
        if (payment == null) {
            return null;
        }
        payment.pay_method = newObject.pay_method;
        payment.save();
        return payment;
    }

}
