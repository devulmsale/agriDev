package mert;

import factory.ModelFactory;
import models.mert.Merchant;
import models.mert.enums.MerchantStatus;
import util.DateHelper;
import util.common.RandomNumberUtil;

/**
 * 商户工厂类.
 */
public class MerchantFactory extends ModelFactory<Merchant> {

    @Override
    public Merchant define() {
        Merchant merchant = new Merchant();
        merchant.fullName = "天下会有限公司";
        merchant.status = MerchantStatus.OPEN;
        merchant.shortName = "天下会";
        merchant.logoPath = "/0/0/0/logo.jpg";
        merchant.linkId = RandomNumberUtil.generateRandomNumberString(12); //数字
        merchant.createdAt = DateHelper.beforeDays(3);
        merchant.updatedAt = DateHelper.beforeDays(2);
        return merchant;
    }
}
