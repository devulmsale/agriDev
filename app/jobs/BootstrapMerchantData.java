package jobs;

import models.constants.DeletedStatus;
import models.mert.Merchant;
import models.mert.enums.MerchantStatus;
import play.Logger;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

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
        Logger.info("aaaaa-----merchant11111111111...");
        createMerchant();
    }

    private static void createMerchant() {
        Logger.info("执行 createMerchant 方法");
        String linkId = "111"; //一个特殊公司
        Merchant merchant= Merchant.findByLinkId(linkId);
        Logger.info("获取到的 Merchant : %s " , merchant);
        if(merchant == null) {
            merchant = new Merchant();
            merchant.fullName = "日照优粮城电子商务";
            merchant.linkId = linkId;
            merchant.phone="18265428637";
            merchant.status= MerchantStatus.OPEN;
            merchant.createdAt = new Date();
            merchant.deleted= DeletedStatus.UN_DELETED;
            merchant.save();
          /*  merchant = new Merchant();
            merchant.fullName = "日照优粮城电子商务2";
            merchant.linkId = "22222";
            merchant.phone="18265428637";
            merchant.address="日照市东港区济南路11号";
            merchant.status=MerchantStatus.OPEN;
            merchant.createdAt = new Date();
            merchant.save();*/
            Logger.info("init merInfo success.!");
        }


    }





}
