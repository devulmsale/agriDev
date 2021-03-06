package models.coupon;

import models.common.enums.GoodsStatus;
import models.constants.DeletedStatus;
import models.order.Goods;
import models.order.Order;
import models.order.User;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import play.Logger;
import play.db.jpa.JPA;
import play.db.jpa.Model;
import play.modules.paginate.JPAExtPaginator;
import util.common.RandomNumberUtil;

import javax.persistence.*;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by upshan on 15/8/11.
 */
@Entity
@Table(name = "coupons")
public class Coupon extends Model {

    public static final String REDIS_LOCK_KEY = "LocalCouponLock";

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
     * 绑定订单
     */
    @JoinColumn(name = "order_id", nullable = true)
    @ManyToOne
    public Order order;

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
     * 构建 Goods
     * @return
     */
    public Goods findOrCreateGoods() {
        Goods goods = Goods.find("serial = ?", "COUPON_" + this.id).first();
        if (goods == null) {
            goods = new Goods();
            goods.createdAt = new Date();
            goods.name = this.couponBatch.name;
            goods.deleted = DeletedStatus.UN_DELETED;
            goods.facePrice = this.couponBatch.costPrice;
            goods.originalPrice = this.couponBatch.salePrice;
            goods.salePrice = this.couponBatch.salePrice;
            goods.status = GoodsStatus.OPEN;
            goods.serial = "COUPON_" + this.id;
            goods.save();
        }
        return goods;
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


    public static Coupon findByBatchAndUnBind(Long batchId) {
        return Coupon.find("couponBatch.id = ? and user = null and deleted = ?" , batchId , DeletedStatus.UN_DELETED).first();
    }

    public static String getCouponNumber(Long merchantId) {
        // sdf000285wsd
        String merchantID =  new DecimalFormat("00000").format(merchantId);
        return RandomNumberUtil.generateRandomNumberString(3)+"_"+ merchantID + RandomNumberUtil.generateRandomNumberString(4);
    }

    public static List<Coupon> findByTimesAndNotUser(Date beginAt , Date endAt) {
        return Coupon.find("createdAt between ? and ? and user = null and deleted = ?" , beginAt , endAt , DeletedStatus.UN_DELETED).fetch();
    }

    public static List<Coupon> findByOrder(Order order) {
        return Coupon.find("order = ? and deleted = ?" , order , DeletedStatus.UN_DELETED).fetch();
    }


    public static Map findByOrderRetrunNaneAndCount(Order order) {
       Map<String , Integer> resultMap =  new HashMap<>();
       List<Coupon> couponList =  Coupon.find("order = ? and deleted = ?", order, DeletedStatus.UN_DELETED).fetch();
       for(Coupon coupon : couponList) {
           String _name = coupon.couponBatch.name;
            if(resultMap.keySet().contains(_name)) {
                resultMap.put(_name , resultMap.get(_name)+1);
            } else {
                resultMap.put(_name , 1);
            }
       }
        return resultMap;
    }




    //查询登录用户所购买的优惠券
    public static List<Coupon> findCouponByLoginUser(Long userId){
        return Coupon.find("deleted = ? and user.id = ? and order = null ",DeletedStatus.UN_DELETED,userId).fetch();
    }

    //查询该用户下优惠券的数量
    public static Long findCouponCountByLoginUser(Long userId){
        return Coupon.count("deleted = ? and user.id = ? and order = null ", DeletedStatus.UN_DELETED, userId);
    }

    @Transient
    public String getRedisLockKey() {
        return Coupon.REDIS_LOCK_KEY + this.id + "@coupon";
    }

    public Long  getCountCouponByCouponBatch(Long couponBatchId){
        return Coupon.count("deleted = ? and couponBatch.id = ? and order = null ", DeletedStatus.UN_DELETED, couponBatchId);
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
