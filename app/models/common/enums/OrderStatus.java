package models.common.enums;

/**
 * 订单状态.
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
public enum OrderStatus {
    NEW,  //新生成
    UNPAID,//未付款
    PAID,//已付款未发货,电子券最终状态为PAID
    CANCELED, //交易关闭
    REFUND   // 退款成功- 金额已经返还到用户来源（如支付宝、快钱账户）
}
