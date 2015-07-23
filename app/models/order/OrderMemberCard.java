package models.order;


import models.member.MemberCard;
import play.db.jpa.Model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "order_member_cards")
public class OrderMemberCard extends Model {

    /**
     * 会员卡信息
     */
    @JoinColumn(name = "member_card_id", nullable = true)
    @ManyToOne
    public MemberCard member;

    /**
     * 数量、金额
     */
    @Column(name = "change_number")
    public BigDecimal sumPrice;


    /**
     * 所属订单
     */
    @JoinColumn(name = "order_id", nullable = true)
    @ManyToOne
    public Order order;

    /**
     * 使用时间
     */
    @Column(name = "used_at")
    public Date usedAt;


    public OrderMemberCard() {
        super();
    }

    public OrderMemberCard(MemberCard card , BigDecimal sumPrice , Order order) {
        this.member = card;
        this.sumPrice = sumPrice;
        this.order = order;
        this.usedAt = new Date();
    }

}
