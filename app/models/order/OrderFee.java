package models.order;

import play.db.jpa.Model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 订单支付记录.
 */
@Entity
@Table(name = "order_fees")
public class OrderFee extends Model {

    /**
     * 订单信息
     */
    @JoinColumn(name = "order_id", nullable = true)
    @ManyToOne
    public Order order;

    /**
     * 记账账户ID
     */
    @Column(name = "account_id")
    public Long accountId;

    /**
     * 变动金额
     */
    @Column(name = "change_amount")
    public BigDecimal changeAmount;


    /**
     * 订单创建时间
     */
    @Column(name = "created_at")
    public Date createdAt;

    /**
     * 清算记账时间
     */
    @Column(name = "cleared_at")
    public Date clearedAt;

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
    public static OrderFee update(Long id, OrderFee newObject) {
        OrderFee fee = OrderFee.findById(id);
        if (fee == null) {
            return null;
        }
        fee.accountId = newObject.accountId;
        fee.save();
        return fee;
    }

}
