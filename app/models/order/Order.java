package models.order;

import jodd.bean.BeanCopy;
import models.base.enums.MemberCardType;
import models.base.enums.VenuePriceType;
import models.common.DateUtil;
import models.common.enums.GoodsStatus;
import models.common.enums.OrderStatus;
import models.common.enums.OrderType;
import models.constants.DeletedStatus;
import models.member.MemberCard;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import play.Logger;
import play.db.jpa.Model;
import play.modules.paginate.JPAExtPaginator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;


/**
 * 订单
 */
@Entity
@Table(name = "orders")

public class Order extends Model {

    private static final long serialVersionUID = 2194331136652323L;
    public static final String ORDER_CHECKOUT_ERROR = "error";
    public static final String ORDER_CHECKOUT_OK = "ok";

    /**
     * 订单号
     */
    @Column(name = "order_no")
    public String orderNumber;

    /**
     * 订单说明
     */
    @Column(name = "description")
    public String description;

    /**
     * 备注
     */
    @Column(name = "remark")
    public String remark;

    /**
     * 订单总金额
     */
    @Column(name = "amount")
    public BigDecimal amount;

    /**
     * 订单折扣金额  比如活动折扣
     */
    @Column(name = "discount_pay")
    public BigDecimal discountPay;

    /**
     * 已支付金额
     */
    @Column(name = "paymented_amount")
    public BigDecimal paymentedAmount;

    /**
     * 已退款金额
     */
    @Column(name = "refunded_amount")
    public BigDecimal refundedAmount;

    /**
     * 关联会员卡信息
     */
    @JoinColumn(name = "member_card_id", nullable = true)
    @ManyToOne
    public MemberCard memberCard;

    /**
     * 上游
     */
    @JoinColumn(name = "supplier_id", nullable = true)
    @ManyToOne
    public Supplier supplier;

    /**
     * 上游订单号
     */
    @Column(name = "supplier_order_number")
    public String supplierOrderNumber;

    /**
     * 订单用户
     */
    @JoinColumn(name = "user_id", nullable = true)
    @ManyToOne
    public User user;

    /**
     * 订单用户联系电话
     */
    @Column(name = "user_mobile")
    public String userMobile;

    /**
     * 分销商
     */
    @JoinColumn(name = "resaler_id", nullable = true)
    @ManyToOne
    public Resaler resaler;

    /**
     * 分销商订单号
     */
    @Column(name = "resaler_order_number")
    public String resalerOrderNumber;

    /**
     * 订单支付状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    public OrderStatus status;

    /**
     * 逻辑删除,0:未删除，1:已删除
     */
    @Enumerated(EnumType.ORDINAL)
    public DeletedStatus deleted;

    /**
     * 订单类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "order_type")
    public OrderType type;

    /**
     * 订单收款时间类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "price_type")
    public VenuePriceType priceType;



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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order")
    @OrderBy("id")
    public List<OrderItem> orderItems;


    /**
     * 分页查询.
     */
    public static JPAExtPaginator<Order>  findByCondition(Map<String, Object> conditionMap, String orderByExpress, int pageNumber, int pageSize) {
        StringBuilder xsqlBuilder = new StringBuilder(" 1=1 ")
                .append("/~ and t.id = {id} ~/")
                .append("/~ and t.orderNumber = {orderNumber} ~/")
                .append("/~ and t.description = {description} ~/")
                .append("/~ and t.remark = {remark} ~/")
                .append("/~ and t.amount = {amount} ~/")
                .append("/~ and t.discountPay = {discountPay} ~/")
                .append("/~ and t.paymentedAmount = {paymentedAmount} ~/")
                .append("/~ and t.refundedAmount = {refundedAmount} ~/")
                .append("/~ and t.supplier.id = {supplierId} ~/")
                .append("/~ and t.user.id = {userId} ~/")
                .append("/~ and t.userMobile = {userMobile} ~/")
                .append("/~ and t.user.phone = {userPhone} ~/")
                .append("/~ and t.resaler.id = {resalerId} ~/")
                .append("/~ and t.resalerOrderNumber = {resalerOrderNumber} ~/")
                .append("/~ and t.memberCard.type = {cardType} ~/")
                .append("/~ and t.memberCard.cardNumber = {cardNumber} ~/")
                .append("/~ and t.status = {status} ~/")
                .append("/~ and t.type = {type} ~/")
                .append("/~ and t.createdAt = {createdAt} ~/")
                .append("/~ and t.updatedAt = {updatedAt} ~/");

        /**
         *    Query query = t.id = {id};
         *    query.setPropery("id" , conditionMap.get("id"));
         */
        util.xsql.XsqlBuilder.XsqlFilterResult result = new util.xsql.XsqlBuilder().generateHql(xsqlBuilder.toString(), conditionMap);
        JPAExtPaginator<Order> orderPage = new JPAExtPaginator<Order>("Order t", "t", Order.class,
                result.getXsql(), conditionMap).orderBy(orderByExpress);
        orderPage.setPageNumber(pageNumber);
        orderPage.setPageSize(pageSize);
        orderPage.setBoundaryControlsEnabled(false);
        return orderPage;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", this.id)
                .append("orderNumber", this.orderNumber)
                .append("description", this.description)
                .append("remark", this.remark)
                .append("amount", this.amount)
                .append("discountPay", this.discountPay)
                .append("paymentedAmount", this.paymentedAmount)
                .append("refundedAmount", this.refundedAmount)
                .append("supplier", this.supplier)
                .append("user", this.user)
                .append("userMobile", this.userMobile)
                .append("resaler", this.resaler)
                .append("resalerOrderNumber", this.resalerOrderNumber)
                .append("status", this.status)
                .append("type", this.type)
                .append("createdAt", this.createdAt)
                .append("updatedAt", this.updatedAt)
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
        if(obj instanceof Order == false)
            return false;
        if(this == obj) return true;
        Order other = (Order)obj;
        return new EqualsBuilder()
                .append(this.id, other.id)
                .isEquals();
    }



    public static Order findByOrderNumber(String orderNumber) {
        return find("orderNumber = ? and deleted = ?", orderNumber , DeletedStatus.UN_DELETED).first();
    }
//    /**
//     * 更新对象.
//     */
//    public static Order update(Long id, Order newObject) {
//        Order order = Order.findById(id);
//        if (order == null) {
//            return null;
//        }
//        order.orderNumber = newObject.orderNumber == null ? order.orderNumber : newObject.orderNumber;
//        if(order != null && order.orderNumber != null) {
//            order.orderNumber = newObject.orderNumber;
//        }
//        order.description = newObject.description;
//        order.remark = newObject.remark;
//        order.amount = newObject.amount;
//        order.discountPay = newObject.discountPay;
//        order.paymentedAmount = newObject.paymentedAmount;
//        order.refundedAmount = newObject.refundedAmount;
//        order.supplier = newObject.supplier;
//        order.supplierOrderNumber = newObject.supplierOrderNumber;
//        order.user = newObject.user;
//        order.userMobile = newObject.userMobile;
//        order.resaler = newObject.resaler;
//        order.resalerOrderNumber = newObject.resalerOrderNumber;
//        order.status = newObject.status;
//        order.type = newObject.type;
//        order.createdAt = newObject.createdAt;
//        order.updatedAt = newObject.updatedAt;
//        order.deleted = newObject.deleted == null ? DeletedStatus.UN_DELETED : newObject.deleted;
//        order.save();
//        return order;
//    }

    /**
     * 按Company值更新指定ID的company.
     */
    public static void update(Long id, Order newObject) {
        Order oldOrder = Order.findById(id);
        BeanCopy.beans(newObject, oldOrder).ignoreNulls(true).copy();
        oldOrder.save();
    }



        public static Boolean delete(Long id) {

        Order order = Order.findById(id);
        if (order == null) {
            return Boolean.FALSE;
        }
        order.deleted = DeletedStatus.DELETED;
        order.save();
        return Boolean.TRUE;
    }


    /**
     * 获取十分钟内未支付订单
     */
    public static List<Order> getUnPayOrders(Date startDate , Date endDate){
        startDate = startDate == null ? models.common.DateUtil.getBeforeMinutes(new Date(), 21) : startDate;
        endDate = endDate == null ? models.common.DateUtil.getBeforeMinutes(new Date(), 10) : endDate;
        return Order.find("deleted = ? and (createdAt between ? and ? ) and status = ? and type = ?",
                DeletedStatus.UN_DELETED, startDate, endDate, OrderStatus.UNPAID, OrderType.PC).fetch();
    }



    /**
     * 获取未支付订单
     * @param user
     */
    public static List<Order> getUserUnPayOrders(User user){
        return Order.find("deleted = ? and user.id = ? and status = ?  order by createdAt desc",
                DeletedStatus.UN_DELETED, user.id, OrderStatus.UNPAID).fetch();

    }


    /**
     * 获取未支付订单
     * @param cardNumber
     */
    public static List<Order> getCardUnPayOrders(String cardNumber){
        return Order.find("deleted = ? and memberCard.cardNumber = ? and status = ?  order by createdAt desc",
                DeletedStatus.UN_DELETED, cardNumber, OrderStatus.UNPAID).fetch();

    }


    /**
     * 获取已支付订单
     * @param user
     */
    public static List<Order> getUserPaidOrders(User user){
//        return Order.find.where().eq("deleted",DeletedStatus.UN_DELETED)
//                .join("user")
//                .eq("user.id",user.id)
//                .eq("status", OrderStatus.PAID)
//                .findList();
        return Order.find("deleted = ? and user.id = ? and status = ?  order by createdAt desc",
                DeletedStatus.UN_DELETED, user.id, OrderStatus.PAID).fetch();
    }

    /**
     * 获取已取消订单列表
     * @param user
     */
    public static List<Order> getUserCanceledOrders(User user){
        return Order.find("deleted = ? and user = ? and status = ? order by createdAt desc",
                DeletedStatus.UN_DELETED, user, OrderStatus.CANCELED).fetch();
    }

    /**
     * 根据用户现在结算的订单
     * 获取 用户今天 预订的订单
     * @param order
     * @return
     */
    public static List<Order> getUserBookOrdersByOrder(Order order) {
        return Order.find("user.id = ? and status = ? and type = ? and createdAt between ? and ?",
                order.user.id, OrderStatus.UNPAID, OrderType.WEBSITE_CARD, models.common.DateUtil.getBeginOfDay(order.createdAt), models.common.DateUtil.getEndOfDay(order.createdAt)).fetch();
    }

    public static Long countByCardAndUnPaid(MemberCard card , OrderStatus status) {
        return Order.count("memberCard = ?  and status = ? and deleted = ?", card, status, DeletedStatus.UN_DELETED);
    }


    /**
     * 获取当前用户正在进行的运动订单
     * @return
     */
    public static Order getUserOnlineOrder(User user){
        return Order.find("deleted = ? and user = ? and type = ? and status = ? and createdAt between ? and ?",
                DeletedStatus.UN_DELETED, user, OrderType.PC, OrderStatus.UNPAID, DateUtil.getBeginOfDay(), DateUtil.getEndOfDay()).first();
    }

}
