package models.common.enums;

/**
 * 会员卡类型
 * Created by upshan on 14/11/12.
 */
public enum  MemberType {
    GIVE_MONEY,  //  赠送金钱
    DISCOUNT, // 打折
    GIVE_TIMES, //赠送次数
    CHANGE;  //转换  比如 500元转换成60次。 转换将清空充值金额


    public static MemberType convert(String name) {
        if ("GIVE_MONEY".equals(name)) {
            return GIVE_MONEY;
        } else if ("DISCOUNT".equals(name)) {
            return DISCOUNT;
        } else if ("GIVE_TIMES".equals(name)) {
            return GIVE_TIMES;
        } else if ("CHANGE".equals(name)) {
            return CHANGE;
        } else {
            return null;
        }
    }

}
