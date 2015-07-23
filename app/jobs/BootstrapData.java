package jobs;

import models.constants.DeletedStatus;
import models.operate.OperateUser;
import models.operate.Operator;
import org.apache.commons.codec.digest.DigestUtils;
import play.Logger;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import util.common.RandomNumberUtil;

import java.util.Date;

/**
 * 启动时自动建立测试用数据内容.
 */
@OnApplicationStart
public class BootstrapData extends Job {

    public void doJob() {
//        if (!Play.runingInTestMode()) {  //开发模式下加载测试数据
//            createOperatorAndOperateUser();
//        } else {
//            Logger.info("没有要加载的数据");
//        }
        Logger.info("开始预加载 数据模块...");
        createOperatorAndOperateUser();
    }

    private static void createOperatorAndOperateUser() {
        Logger.info("执行 createOperatorAndOperateUser 方法");
        String loginName = "ulmsale"; //一个特殊公司
        Operator operator = Operator.findByName(loginName);
        Logger.info("获取到的 Operator : %s " , operator);
        //如果公司不存在, 我们默认一个公司  . 可以让后台管理员登录
        if(operator == null) {
            operator = new Operator();
            operator.name = "ulmsale";
            operator.code = "ulmsale";
            operator.createdAt = new Date();
            operator.deleted = DeletedStatus.UN_DELETED;
            operator.save();
            Logger.info("初始化公司信息成功.!");
            findOrCreatedOperateUser(operator);
        }

        // 根据公司查找公司员工数量
        Long userCount = OperateUser.countByOperator(operator);
        if(userCount == 0) {
            findOrCreatedOperateUser(operator);
        }
        Logger.info("BootstrapData 预加载信息完成.");
    }


    private static void findOrCreatedOperateUser(Operator operator) {
        OperateUser operateUser = new OperateUser();
        operateUser.operator = operator;
        operateUser.loginName = "admin";
        operateUser.deleted = DeletedStatus.UN_DELETED;
        operateUser.passwordSalt = RandomNumberUtil.generateRandomNumberString(6);
        operateUser.encryptedPassword = DigestUtils.md5Hex("123456" + operateUser.passwordSalt);
        operateUser.save();
        Logger.info("初始化公司员工信息成功.!");
    }


}
