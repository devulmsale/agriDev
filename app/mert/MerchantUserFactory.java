package mert;

import factory.FactoryBoy;
import factory.ModelFactory;
import models.mert.Merchant;
import models.mert.MerchantUser;
import util.DateHelper;

/**
 * 商户操作员Factory.
 */
public class MerchantUserFactory extends ModelFactory<MerchantUser> {
    @Override
    public MerchantUser define() {
        MerchantUser merchantUser = new MerchantUser();
        merchantUser.merchant = FactoryBoy.lastOrCreate(Merchant.class);
        merchantUser.loginName = "bujy";
        merchantUser.mobile = "15026682165";
        merchantUser.showName = "步惊云";
        merchantUser.updatePassword("mypassword");
        merchantUser.createdAt = DateHelper.beforeDays(3);
        merchantUser.updatedAt = DateHelper.beforeDays(2);
        return merchantUser;
    }
}
