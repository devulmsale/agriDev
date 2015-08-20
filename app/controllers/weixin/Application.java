package controllers.weixin;

import controllers.auth.WxMpAuth;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import me.chanjar.weixin.common.util.StringUtils;
import models.common.DateUtil;
import models.common.enums.OrderGoodsType;
import models.common.enums.OrderStatus;
import models.common.enums.OrderType;
import models.constants.DeletedStatus;
import models.coupon.Coupon;
import models.coupon.CouponBatch;
import models.mert.Merchant;
import models.mert.MerchantProductType;
import models.mert.hall.HallTable;
import models.mert.hall.MerchantHall;
import models.order.*;
import models.product.Product;
import models.product.ProductImage;
import models.vo.GoodsTypeVO;
import models.vo.OrderItemVO;
import models.vo.OrderVO;
import order.OrderBuilder;
import play.Logger;
import play.modules.redis.Redis;
import play.mvc.Controller;
import play.mvc.With;
import util.common.RandomNumberUtil;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by upshan on 15/8/5.
 */
//@With(WxMpAuth.class)
public class Application extends Controller {

    private static final String IMG_URL="http://img.ulmsale.cn/getImageUrl";

    public static void index() {
        List<CouponBatch> couponBatchList = CouponBatch.findAll();
        render(couponBatchList);
    }

   

}
