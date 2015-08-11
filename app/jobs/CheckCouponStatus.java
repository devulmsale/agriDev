package jobs;

import models.common.DateUtil;
import models.constants.DeletedStatus;
import models.coupon.Coupon;
import play.Logger;
import play.jobs.Every;
import play.jobs.Job;
import java.util.Date;
import java.util.List;

/**
 * 每分钟执行一次 .
 */
@Every("1mn")
public class CheckCouponStatus extends Job {

    public void doJob() {
        // 检查 15分钟--16分 生成的卡. 并且 没有绑定用户的
        Logger.info("执行 CheckCouponStatus");
        Date beginAt = DateUtil.getBeforeMinutes(new Date() , 16);
        Date endAt = DateUtil.getBeforeMinutes(new Date() , 15);
        List<Coupon> couponList = Coupon.findByTimesAndNotUser(beginAt, endAt);
        for(Coupon coupon : couponList) {
            Logger.info("执行 coupon.id : %s 状态改变" , coupon.id);
            coupon.deleted = DeletedStatus.DELETED;
            coupon.save();
            coupon.couponBatch.surplusCount +=1;
            coupon.couponBatch.save();
        }
    }
}
