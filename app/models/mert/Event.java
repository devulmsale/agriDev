package models.mert;

import play.db.jpa.Model;

import javax.persistence.*;
import java.util.Date;

/**
 * 商户活动.
 */
@Entity
@Table(name = "mert_events")
public class Event extends Model {

    /**
     * 所属商户.
     */
    @ManyToOne
    @JoinColumn(name = "merchant_id")
    public Merchant merchant;

    /**
     * 活动名称.
     */
    @Column(name = "name", length = 100)
    public String name;

    /**
     * 活动开始时间.
     */
    @Column(name = "begin_at")
    public Date beginAt;

    /**
     * 活动结束时间.
     */
    @Column(name = "end_at")
    public Date endAt;


    /**
     * 创建时间
     */
    @Column(name = "created_at")
    public Date createdAt;

    /**
     * 修改时间
     */
    @Column(name = "updated_at")
    public Date updatedAt;

    /**
     * 找到商户最新的活动.
     */
    public static Event findLastByMerchant(Merchant merchant) {
        return Event.find("merchant.id=? order by id desc", merchant.id).first();
    }
}
