package order;

import models.constants.DeletedStatus;
import models.order.Goods;
import models.order.OrderItem;
import models.product.Product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 订单条目Builder.
 */
public class OrderItemBuilder {

    OrderBuilder orderBuilder;

    OrderItem orderItem;

    Goods goods;



    List<String> errorMessages;


    public OrderItemBuilder(OrderBuilder orderBuilder, Product product) {
        this(orderBuilder, product.findOrCreateGoods());
    }

    public OrderItemBuilder(OrderBuilder orderBuilder, Goods goods) {
        this.orderBuilder = orderBuilder;
        this.orderItem = new OrderItem();
        this.orderItem.order = orderBuilder.order;
        this.goods = goods;
        this.orderItem.goods = this.goods;
        this.orderItem.createdAt = new Date();
        this.orderItem.deleted = DeletedStatus.UN_DELETED;


        if (this.orderBuilder.order.orderItems == null) {
            this.orderBuilder.order.orderItems = new ArrayList<>();
        }
        this.orderBuilder.order.orderItems.add(this.orderItem);
    }



    public OrderItemBuilder originalPrice(BigDecimal originalPrice) {
        this.orderItem.originalPrice = originalPrice;
        return this;
    }

    public OrderItemBuilder salePrice(BigDecimal salePrice) {
        this.orderItem.salePrice = salePrice;
        return this;
    }

    public OrderItemBuilder buyNumber(Integer number) {
        this.orderItem.buyNumber = number;
        return this;
    }


    /**
     * 完成OrderItem的生成，并返回对应的OrderBuilder，以进行后继的生成.
     */
    public OrderBuilder build() {
        this.orderItem.save();
        this.orderBuilder.recalcOrderItem();
        this.orderBuilder.order.save();
        return orderBuilder;
    }
}
