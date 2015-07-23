package models.common.enums;

/**
 * 订单来源
 */
public enum OrderType {
    PC,// PC网站
    PHONE, //手机端
    WEIXIN, // 微信
    WEBSITE_CARD, //网上会员卡预订
    DISABLED, // 屏蔽
    SOLD, //出售
    WEIXIN_SALE, // 微信公众号售出
    QR_CODE; // 二维码扫码购买

    public static OrderType findByName(String name) {
        if (name.toUpperCase().equals(OrderType.PC.toString())) {
            return OrderType.PC;
        } else if (name.toUpperCase().equals(OrderType.PHONE.toString())) {
            return OrderType.PHONE;
        } else if (name.toUpperCase().equals(OrderType.WEIXIN.toString())) {
            return OrderType.WEIXIN;
        } else if (name.toUpperCase().equals(OrderType.DISABLED.toString())) {
            return OrderType.DISABLED;
        } else if (name.toUpperCase().equals(OrderType.SOLD.toString())) {
            return OrderType.SOLD;
        } else if (name.toUpperCase().equals(OrderType.WEBSITE_CARD.toString())) {
            return OrderType.WEBSITE_CARD;
        }else if (name.toUpperCase().equals(OrderType.WEIXIN_SALE.toString())) {
            return OrderType.WEIXIN_SALE;
        }else if (name.toUpperCase().equals(OrderType.QR_CODE.toString())) {
            return OrderType.QR_CODE;
        } else {
            return null;
        }
    }
}
