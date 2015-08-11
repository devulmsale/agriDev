package models.coupon;

import models.constants.DeletedStatus;
import models.mert.Merchant;
import models.order.User;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import play.db.jpa.Model;
import play.modules.paginate.JPAExtPaginator;
import util.common.RandomNumberUtil;

import javax.persistence.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Map;

/**
 * 优惠券批次
 * Created by upshan on 15/8/11.
 */
@Entity
@Table(name = "coupon_batchs")
public class CouponBatch extends Model{

    /**
     * 批次名称
     */
    @Column(name = "name")
    public String name;

    /**
     * 所属商家
     */
    @JoinColumn(name = "merchant_id", nullable = true)
    @ManyToOne
    public Merchant merchant;

    /**
     * 成本价格
     */
    public BigDecimal costPrice;


    /**
     * 销售价格
     */
    public BigDecimal salePrice;

    /**
     * 开始生效期  如果不限制为空
     */
    @Column(name = "begin_at")
    public Date beginAt;

    /**
     * 结束生效期 如果不限制为空
     */
    @Column(name = "end_at")
    public Date endAt;

    /**
     * 创建时间
     */
    @Column(name = "created_at")
    public Date createdAt;

    /**
     * 生成优惠券数量
     */
    @Column(name = "count")
    public Integer count;

    /**
     * 逻辑删除,0:未删除，1:已删除
     */
    @Enumerated(EnumType.ORDINAL)
    public DeletedStatus deleted;


    /**
     * 分页查询
     *
     */
    public static JPAExtPaginator<CouponBatch> findByCondition(Map<String, Object> conditionMap, String MemberCardByExpress, int pageNumber, int pageSize) {
        StringBuilder xsqlBuilder = new StringBuilder("t.deleted=models.constants.DeletedStatus.UN_DELETED")
                .append("/~ and t.id = {id} ~/")
                .append("/~ and t.name = {name} ~/")
                .append("/~ and t.merchant.id = {merchantId} ~/")
                .append("/~ and t.costPrice = {costPrice} ~/")
                .append("/~ and t.salePrice = {salePrice} ~/")
                .append("/~ and t.beginAt = {beginAt} ~/")
                .append("/~ and t.endAt = {endAt} ~/")
                .append("/~ and t.createdAt = {createdAt} ~/")
                .append("/~ and t.count = {count} ~/");

        util.xsql.XsqlBuilder.XsqlFilterResult result = new util.xsql.XsqlBuilder().generateHql(xsqlBuilder.toString(), conditionMap);
        JPAExtPaginator<CouponBatch> memberCardPage = new JPAExtPaginator<CouponBatch>("CouponBatch t", "t", CouponBatch.class,
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
                .append("name", this.name)
                .append("merchant", this.merchant)
                .append("costPrice", this.costPrice)
                .append("salePrice", this.salePrice)
                .append("beginAt", this.beginAt)
                .append("endAt", this.endAt)
                .append("createdAt", this.createdAt)
                .append("begicountnAt", this.count)
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
        if(obj instanceof CouponBatch == false)
            return false;
        if(this == obj) return true;
        CouponBatch other = (CouponBatch)obj;
        return new EqualsBuilder()
                .append(this.id, other.id)
                .isEquals();
    }


    /**
     * 根据 批次 生成 优惠券
     * @param batchId
     * @return
     */
    public static Integer createCouponByBatch(Long batchId) {
        CouponBatch batch = CouponBatch.findById(batchId);
        Integer count = 0;
        if(batch != null) {
            for(int i = 0 ; i < batch.count ; i++) {
                // 会员卡类型 sdf1_12sdfs
                String merchantID =  new DecimalFormat("00000").format(batch.merchant.id);
                String couponMember = RandomNumberUtil.generateRandomNumberString(3)+"_"+ merchantID + RandomNumberUtil.generateRandomNumberString(4);
                new Coupon(couponMember , batch);
                count ++;
            }
        }
        return count;
    }




}
