package order;

import models.gym.Field;
import models.order.Goods;
import models.product.Product;

/**
 * 商品信息构建器.
 */
public class GoodsBuilder {

    public static Goods forVenueField(Product product) {
        return product.findOrCreateGoods();
    }
}
