package jobs;

import models.constants.DeletedStatus;
import models.mert.Event;
import models.mert.Merchant;
import models.mert.MerchantUser;
import models.mert.enums.MerchantStatus;
import play.Logger;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import util.DateHelper;

import java.util.Date;

/**
 * 启动时自动建立测试用数据内容.
 */
@OnApplicationStart
public class BootstrapMerchantData extends Job {

    public void doJob() {
//        if (!Play.runingInTestMode()) {  //开发模式下加载测试数据
//            createOperatorAndOperateUser();
//        } else {
//            Logger.info("没有要加载的数据");
//        }
        createYouLiangMerchant();
    }

    public static void createYouLiangMerchant() {
        String code = "ulm";  //用于URL中配置，需要保证在系统中唯一

        Merchant merchant = Merchant.findByLinkId(code);
        if (merchant != null) {
            Logger.info("已经存在" + code + "对应的公司");
            return;
        }

        // 生成公司.
        merchant = new Merchant();
        merchant.shortName = "ulm";
        merchant.fullName = "ulm";
        merchant.linkId = code;
        merchant.weixinAppId = "wx8616734787f27575";
        merchant.weixinAppSecret = "affe5a74ace7c2b116e28e53da7a9b40";
        merchant.weixinToken = "weixin2015";
        merchant.weixinAesKey = "04IC6il8kEo6ayCy0kmG1btzuTc0mX71NlrYYWdvs05";

        merchant.status = MerchantStatus.OPEN;
        merchant.expiredAt = DateHelper.afterDays(360);
        merchant.createdAt = new Date();
        merchant.updatedAt = new Date();
        merchant.deleted = DeletedStatus.UN_DELETED;
        merchant.save();

        Event event = new Event();
        event.name = "ulm";
        event.merchant = merchant;
        event.beginAt = new Date();
        event.endAt = DateHelper.afterDays(360);
        event.createdAt = new Date();
        event.updatedAt = new Date();
        event.save();

        Logger.info("加载ulm帐号成功:" + merchant.shortName);
    }





}
