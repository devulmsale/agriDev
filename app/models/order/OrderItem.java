package models.order;

import models.common.enums.GoodsStatus;
import models.constants.DeletedStatus;
import play.db.jpa.Model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * 订单条目.
 */
@Entity
@Table(name = "order_items")
public class OrderItem extends Model {


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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "item")
    public List<Ecoupon> eCoupons;

    /**
     * 商品名称
     */
    @Column(name = "goods_name")
    public String goodsName;

    /**
     * 购买数量
     */
    @Column(name = "buy_number")
    public Integer buyNumber;


    /**
     * 销售单价(面值)
     */
    @Column(name = "face_value")
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
     * 状态
     */
    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    public GoodsStatus status;   //FIXME: OrderStatus or OrderItemStatus

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
    public static OrderItem update(Long id, OrderItem newObject) {
        OrderItem item = OrderItem.findById(id);
        if (item == null) {
            return null;
        }
        item.goodsName = newObject.goodsName;
        item.save();
        return item;
    }

    public static Boolean delete(Long id) {

        OrderItem item = OrderItem.findById(id);
        if (item == null) {
            return Boolean.FALSE;
        }
        item.deleted = DeletedStatus.DELETED;
        item.save();
        return Boolean.TRUE;
    }


    public static List<OrderItem> getListByOrder(Order order) {
//        List<OrderItem> list = OrderItem.find.where()
//                .join("order")
//                .eq("deleted", DeletedStatus.UN_DELETED)
//                .eq("order.id", order.id).findList();
//
//        return list;
        return OrderItem.find("order.id = ? and deleted = ?", order.id, DeletedStatus.UN_DELETED).fetch();
    }
}
