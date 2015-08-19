package models.vo;

import models.order.OrderItem;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Administrator on 2015/8/18.
 */
public class OrderVO {

    public Boolean success;

    public BigDecimal price;

    public List<OrderItemVO> orderItems;

    public List<GoodsTypeVO> goodsTypeVOs;
}
