package jobs;

import models.mert.Merchant;
import models.mert.enums.MerchantStatus;
import play.jobs.Job;
import play.jobs.On;

import java.util.Date;
import java.util.List;

/**
 * 检查时间过期的商户. 并且修改状态
 * Created by upshan on 15/7/27.
 */

@On("0 0 2 * * ?")
public class CheckMerchantExPired extends Job {

    public void doJob() {
        List<Merchant> merchantList = Merchant.findByExpired(new Date());
        for(Merchant merchant : merchantList) {
            merchant.status = MerchantStatus.EXPIRED;
            merchant.save();
        }

    }
}
