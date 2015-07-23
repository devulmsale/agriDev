package models.order;

import models.common.enums.ECouponStatus;
import models.constants.DeletedStatus;
import play.db.jpa.Model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 订单分账记录.
 */
@Entity
@Table(name = "e_coupons")
public class Ecoupon extends Model {

    /**
     * 商品信息
     */
    @JoinColumn(name = "goods_id", nullable = true)
    @ManyToOne
    public Goods goods;

    /**
     * 订单信息
     */
    @JoinColumn(name = "order_id", nullable = true)
    @ManyToOne
    public Order order;

    /**
     * 订单条目
     */
    @JoinColumn(name = "order_item_id", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    public OrderItem item;


    /**
     * 购买数量
     */
    @Column(name = "buy_number")
    public Integer buyNumber;


    /**
     * 券号
     */
    @Column(name = "e_coupon_sn")
    public String eCouponSn;

    /**
     * 券号密码
     */
    @Column(name = "e_coupon_password")
    public String eCouponPassword;

    /**
     * 销售单价(面值)
     */
    @Column(name = "face_price")
    public BigDecimal facePrice;

    /**
     * 成本价
     */
    @Column(name = "original_price")
    public BigDecimal originalPrice;

    /**
     * 销售单价
     */
    @Column(name = "sale_price")
    public BigDecimal salePrice;

    /**
     * 生效时间
     */
    @Column(name = "effective_at")
    public Date effectiveAt;

    /**
     * 过期时间
     */
    @Column(name = "expire_at")
    public Date expireAt;

    /**
     * 退款时间
     */
    @Column(name = "refund_at")
    public Date refundAt;

    /**
     * 核销时间
     */
    @Column(name = "verified_at")
    public Date verifiedAt;

    /**
     * 核销类型
     */
    @Column(name = "verify_type")
    public String verifyType;


    /**
     * 状态
     */
    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    public ECouponStatus status;

    /**
     * 逻辑删除状态
     */
    @Column(name = "deleted")
    @Enumerated(EnumType.ORDINAL)
    public DeletedStatus deleted;

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
    public static Ecoupon update(Long id, Ecoupon newObject) {
        Ecoupon ecoupon = Ecoupon.findById(id);
        if (ecoupon == null) {
            return null;
        }
        ecoupon.eCouponSn = newObject.eCouponSn;
        ecoupon.save();
        return ecoupon;
    }

    public static Boolean delete(Long id) {

        Ecoupon ecoupon = Ecoupon.findById(id);
        if (ecoupon == null) {
            return Boolean.FALSE;
        }
        ecoupon.deleted = DeletedStatus.DELETED;
        ecoupon.save();
        return Boolean.TRUE;
    }


}
