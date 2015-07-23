package models.member;

import play.db.jpa.Model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 延期记录
 */
@Entity
@Table(name = "delay_records")
public class DelayRecord extends Model {

    private static final long serialVersionUID = 2191130633L;

    /**
     * 延期操作时间
     */
    @Column(name = "created_at")
    public Date createdAt;

    /**
     * 会员卡
     */
    @JoinColumn(name = "member_card_id", nullable = true)
    @ManyToOne
    public MemberCard memberCard;

    /**
     * 操作人
     */
    @Column(name = "created_by")
    public String createdBy;

    /**
     * 延期至
     */
    @Column(name = "delay_at")
    public Date delayAt;



    /**
     * 创建充值记录
     *
     * @param delayAt
     * @param createdBy
     * @param card
     * @return
     */
    public static DelayRecord create(Date delayAt, String createdBy, MemberCard card) {
        DelayRecord delayRecord = new DelayRecord();
        delayRecord.delayAt = delayAt;
        delayRecord.createdAt = new Date();
        delayRecord.memberCard = card;
        delayRecord.createdBy = createdBy;
        delayRecord.save();
        return delayRecord;
    }

    public static List<DelayRecord> getAllByCard(MemberCard card) {
        return DelayRecord.find("memberCard = ? order by createdAt desc", card).fetch();
    }
}
