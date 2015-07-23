package order;

import me.chanjar.weixin.common.util.StringUtils;
import models.common.enums.GoodsStatus;
import models.common.enums.OrderStatus;
import models.common.enums.OrderType;
import models.constants.DeletedStatus;
import models.gym.Field;
import models.gym.Venue;
import models.member.MemberCard;
import models.order.*;
import models.product.Product;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 订单构造器.
 * <p>
 * 订单生成过程，逻辑上分成三步：
 * <ol>
 * <li>创建订单(NEW)：新生成订单，分配好订单号，在表中创建orders记录，但还没有绑定商品</li>
 * <li>订单等待支付(UNPAID)：绑定好订单商品，锁定商品库存，等待支付</li>
 * <li>订单支付成功(PAID)：订单支付成功后，确认为已经支付，商品减少库存</li>
 * <li>取消订单(CANCEL)：订单支付成功后，取消订单，商品商品解除锁定</li>
 * <li>退款订单(REFUND)：订单支付成功后，取消订单，资金完成退款，商品库存恢复</li>
 * </ol>
 * </p>
 */
public class OrderBuilder {

    Order order;

    List<String> errorMessages;

    /**
     * 无参数构建，生成全新的订单，设置初始值.
     */
    private OrderBuilder(Resaler resaler) {
        order = new Order();
        order.createdAt = new Date();
        // 15位订单长度，前6位为年月日
        order.deleted = DeletedStatus.UN_DELETED;
        order.resaler = resaler;
        order.status = OrderStatus.NEW;
        order.supplier = Supplier.defaultSuppler();
        order.type = OrderType.WEBSITE;
        order.paymentedAmount = BigDecimal.ZERO;
        order.amount = BigDecimal.ZERO;
        generateOrderNumber(order);
        initErrorMessage(); //建立空错误信息.
    }

    /**
     * 生成15位订单号。
     * <p>
     * 只有在订单是新创建未入数据库，并且订单号为空时才生成(已经有订单号的不会重新生成)；
     * TODO: 检查数据库中是否重复(已经加唯一索引），如果重复就重新生成一个
     * </p>
     */
    public static String generateOrderNumber(Order _order) {
        if (!_order.isPersistent() && StringUtils.isEmpty(_order.orderNumber)) {
            _order.orderNumber = DateFormatUtils.format(_order.createdAt, "yyMMdd") + RandomStringUtils.randomNumeric(9);
            return _order.orderNumber;
        }
        return null;
    }

    private OrderBuilder(String orderNumber) {
        order = Order.findByOrderNumber(orderNumber);
        initErrorMessage(); //建立空错误信息.
    }

    /**
     * 开始新订单的创建.
     *
     * @return OrderBuilder构建器
     */
    public static OrderBuilder forResaler(Resaler resaler) {
        return new OrderBuilder(resaler);
    }

    public OrderBuilder byUser(User user) {
        if (order.isPersistent()) {
            throw new IllegalStateException("Order can NOT change user after save!");
        }
        order.user = user;
        return this;
    }

    public OrderBuilder type(OrderType orderType) {
        order.type = orderType;
        return this;
    }


    public OrderBuilder venue(Venue venue) {
        order.venue = venue;
        return this;
    }

    public OrderBuilder memberCard(MemberCard memberCard) {
        order.memberCard = memberCard;
        return this;
    }

    public static OrderBuilder orderNumber(String orderNumber) {
        OrderBuilder orderBuilder = new OrderBuilder(orderNumber);
        if (orderBuilder.order == null) {
            orderBuilder.errorMessages.add("order.notExist");
        }
        return orderBuilder;
    }


    /**
     * 当前订单信息.
     */
    public Order getOrder() {
        return order;
    }

    /**
     * 保存订单后返回.
     *
     * @return 保存后的订单
     */
    public Order save() {
        if (order.isPersistent()) {
            if (checkCanNewOrder()) {
                order.save();
            }
        } else {
            order.save();
        }
        return order;
    }

    /**
     * 检查新订单是否可以被保存.
     */
    public boolean checkCanNewOrder() {
        if (order.isPersistent()) {
            errorMessages.add("order.notNewOrder");
        }
        if (StringUtils.isBlank(order.orderNumber)) {
            errorMessages.add("order.emptyOrderNumber");
        }
        return errorMessages.size() <= 0;
    }

    /**
     * 检查订单是否可以转换为UNPAID状态.
     */
    public boolean checkCanToUnpaid() {
        return !hasError();
    }

    /**
     * 检查订单是否可以转换为PAID状态.
     */
    private boolean checkCanToPaid() {
        return !hasError();
    }

    /**
     * 检查订单是否可以转换为PAID状态.
     */
    private boolean checkCanToRefund() {
        return !hasError();
    }

    private void initErrorMessage() {
        errorMessages = new ArrayList<>();
    }

    /**
     * 加入商品，此方法返回OrderItemBuilder，以进行后续OrderItem的生成。
     * @param goods 商品
     */
    public OrderItemBuilder addGoods(Goods goods) {
        return new OrderItemBuilder(this, goods);
    }


    /**
     * 加入，此方法返回OrderItemBuilder，以进行后续OrderItem的生成。
     * @param product 商品
     */
    public OrderItemBuilder addProduct(Product product) {
        return new OrderItemBuilder(this, product);
    }

    public OrderBuilder changeToUnPaid() {
        if (checkCanToUnpaid()) {
            this.order.status = OrderStatus.UNPAID;
            if (this.order.orderItems != null) {
                for (OrderItem orderItem : this.order.orderItems) {
                    orderItem.status = GoodsStatus.OPEN;
                    orderItem.save();
                }
            }
            this.order.save();
        }
        return this;
    }


    public boolean hasError() {
        return errorMessages != null && errorMessages.size() > 0;
    }


    public OrderBuilder changeToPaid() {
        if (checkCanToPaid()) {
            this.order.status = OrderStatus.PAID;
            this.order.save();
        }
        return this;
    }



    public OrderBuilder changeToRefund() {
        if (checkCanToRefund()) {
            this.order.status = OrderStatus.REFUND;
            this.order.save();
        }
        return this;
    }


    /**
     * 重新计算订单条目信息.
     */
    public OrderBuilder recalcOrderItem() {
        if (order.orderItems != null) {
            order.amount = BigDecimal.ZERO;
            for (OrderItem orderItem : order.orderItems) {
                if (orderItem.salePrice == null) {
                    throw new RuntimeException("必须设置orderItem.salePrice: order.id=" + order.id);
                }
                order.amount = order.amount.add(orderItem.salePrice);
            }
            order.paymentedAmount = order.amount;
        }
        return this;
    }
}
