package models.member;

import play.db.jpa.Model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 充值记录
 */
@Entity
@Table(name = "recharge_records")
public class RechargeRecord extends Model {

    private static final long serialVersionUID = 2191130633L;

    @Column(name = "created_at")
    public Date createdAt;

    @JoinColumn(name = "member_card_id", nullable = true)
    @ManyToOne
    public MemberCard memberCard;

    @Column(name = "created_by")
    public String createdBy;

    /**
     * 本次充值的总金额  假设 充值 500 赠送 200. 这里就是 700
     */
    @Column(name = "amount")
    public BigDecimal amount;

    /**
     * 可以在商场消费的钱
     */
    @Column(name = "shop_money")
    public BigDecimal shopMoney;

    /**
     * 用户真实充值的钱 假设 充值 500 赠送 200. 这里就是 500
     */
    @Column(name = "recharge_amount")
    public BigDecimal  rechargeMoney;

    /**
     * 用户充值赠送的钱. 假设 充值 500 赠送 200. 这里就是 200
     */
    @Column(name = "change_amount")
    public BigDecimal giveAmount;



    /**
     * 创建充值记录
     *
     * @param amount
     * @param createdBy
     * @param card
     * @return
     */
    public static RechargeRecord create(BigDecimal amount, String createdBy, MemberCard card) {
        RechargeRecord rechargeRecord = new RechargeRecord();

        rechargeRecord.amount = amount;
        rechargeRecord.createdAt = new Date();
        rechargeRecord.memberCard = card;
        rechargeRecord.createdBy = createdBy;
        rechargeRecord.save();
        return rechargeRecord;
    }

    /**
     * 创建充值记录
     *
     * @param createdBy
     * @param card
     * @return
     */
    public static RechargeRecord create(BigDecimal rechargeAmount, BigDecimal giveAmount , BigDecimal shopAmount , String createdBy, MemberCard card) {
        RechargeRecord rechargeRecord = new RechargeRecord();
        shopAmount = shopAmount == null ? BigDecimal.ZERO : shopAmount;
        rechargeRecord.rechargeMoney = rechargeAmount == null ? BigDecimal.ZERO : rechargeAmount; // 充值的本金
        rechargeRecord.giveAmount = giveAmount == null ? BigDecimal.ZERO : giveAmount; //赠送的金额
        rechargeRecord.amount = rechargeRecord.rechargeMoney.add(rechargeRecord.giveAmount); //可以用户场地消费的总金额
        rechargeRecord.shopMoney = shopAmount; //商品的钱
        rechargeRecord.createdAt = new Date();
        rechargeRecord.memberCard = card;
        rechargeRecord.createdBy = createdBy;
        rechargeRecord.save();

        card.amount = card.amount.add(rechargeRecord.amount);
        card.shopAmount = card.shopAmount == null ? BigDecimal.ZERO : card.shopAmount;
        card.shopAmount = card.shopAmount.add(shopAmount);
        card.save();
        return rechargeRecord;
    }

    public static List<RechargeRecord> getAllByCard(MemberCard card) {
        return RechargeRecord.find("memberCard = ? order by createdAt desc", card).fetch();
    }
}
