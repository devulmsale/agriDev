package jobs;

import models.common.DateUtil;
import models.common.enums.OrderStatus;
import models.constants.DeletedStatus;
import models.coupon.Coupon;
import models.order.OrderUser;
import play.Logger;
import play.jobs.Every;
import play.jobs.Job;

import java.util.Date;
import java.util.List;

/**
 * 每分钟执行一次 .
 */
@Every("10mn")
public class CheckOrderStatus extends Job {

    public void doJob() {
        List<OrderUser> orderUserList = OrderUser.findByUnpaidAndTimes(new Date());
        for(OrderUser orderUser : orderUserList) {
            orderUser.order.status = OrderStatus.CANCELED;
            orderUser.order.remark = "预定时间已过， 无法进行支付";
            orderUser.order.save();
        }
    }
}
