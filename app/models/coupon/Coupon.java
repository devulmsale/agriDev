package models.coupon;

import models.constants.DeletedStatus;
import models.order.User;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import play.db.jpa.Model;
import play.modules.paginate.JPAExtPaginator;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;

/**
 * Created by upshan on 15/8/11.
 */
@Entity
@Table(name = "coupons")
public class Coupon extends Model {


    /**
     * 优惠券券号
     */
    @Column(name = "coupon_number")
    public String couponNumber;

    /**
     * 所属批次
     */
    @JoinColumn(name = "coupon_batch_id", nullable = true)
    @ManyToOne
    public CouponBatch couponBatch;

    /**
     * 优惠券所属人
     */
    @JoinColumn(name = "user_id", nullable = true)
    @ManyToOne
    public User user;

    /**
     * 创建时间
     */
    @Column(name = "created_at")
    public Date createdAt;

    /**
     * 绑定用户时间
     */
    @Column(name = "bind_user_at")
    public Date bindUserAt;

    /**
     * 逻辑删除,0:未删除，1:已删除
     */
    @Enumerated(EnumType.ORDINAL)
    public DeletedStatus deleted;

    public Coupon() {
        super();
    }

    public Coupon(String couponNumber ,  CouponBatch couponBatch) {
        this.couponBatch = couponBatch;
        this.deleted = DeletedStatus.UN_DELETED;
        this.bindUserAt = null;
        this.user = null;
        this.couponNumber = couponNumber;
        this.createdAt = new Date();
        this.save();
    }


    /**
     * 分页查询
     *
     */
    public static JPAExtPaginator<Coupon> findByCondition(Map<String, Object> conditionMap, String MemberCardByExpress, int pageNumber, int pageSize) {
        StringBuilder xsqlBuilder = new StringBuilder("t.deleted=models.constants.DeletedStatus.UN_DELETED")
                .append("/~ and t.id = {id} ~/")
                .append("/~ and t.couponNumber = {cardNumber} ~/")
                .append("/~ and t.user.id = {userId} ~/")
                .append("/~ and t.couponBatch.id = {venueId} ~/")
                .append("/~ and t.createdAt = {createdAt} ~/")
                .append("/~ and t.bindUserAt = {updatedAt} ~/");

        util.xsql.XsqlBuilder.XsqlFilterResult result = new util.xsql.XsqlBuilder().generateHql(xsqlBuilder.toString(), conditionMap);
        JPAExtPaginator<Coupon> memberCardPage = new JPAExtPaginator<Coupon>("Coupon t", "t", Coupon.class,
                result.getXsql(), conditionMap).orderBy(MemberCardByExpress);
        memberCardPage.setPageNumber(pageNumber);
        memberCardPage.setPageSize(pageSize);
        memberCardPage.setBoundaryControlsEnabled(false);
        return memberCardPage;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", this.id)
                .append("couponNumber", this.couponNumber)
                .append("couponBatch", this.couponBatch)
                .append("user", this.user)
                .append("createdAt", this.createdAt)
                .append("bindUserAt", this.bindUserAt)
                .append("deleted", this.deleted)
                .toString();
    }


    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.id)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Coupon == false)
            return false;
        if(this == obj) return true;
        Coupon other = (Coupon)obj;
        return new EqualsBuilder()
                .append(this.id, other.id)
                .isEquals();
    }



}
