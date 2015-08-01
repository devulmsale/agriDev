package ext.weixin.invocation;

import ext.weixin.WxMpContext;
import ext.weixin.WxMpInvocation;
import helper.GlobalConfig;
import me.chanjar.weixin.mp.bean.WxMpXmlOutMessage;
import models.base.WeixinUser;
import play.Logger;

/**
 * 发送文本1，返回一段测试文字.
 */
public class WelcomeInvocation extends WxMpInvocation {

    @Override
    protected WxMpXmlOutMessage doExecute(final WxMpContext context) {
        //sendAsyncTextMessage("测试异步发送加密消息");

//        // 尝试读取用户基本信息.
//        try {
//            Logger.info("尝试读取用户信息.");
//            WxMpUser wxMpUser = context.wxService.userInfo(context.inMessage.getFromUserName(), "zh_CN");
//            Logger.info("wxMpUser=" + wxMpUser);
//        } catch (WxErrorException e) {
//            Logger.warn(e, "读取用户信息异常");
//        }
/*
        return WxMpXmlOutMessage.TEXT()
                .fromUser(context.inMessage.getToUserName())
                .toUser(context.inMessage.getFromUserName())
                .content("测试加密消息2.")
                .build(); */

        WeixinUser weixinUser = WeixinUser.findOrCreateMerchantWxUser(context.merchant, context.inMessage.getFromUserName());

        Logger.info("weixinUser=" + weixinUser);

        if (!weixinUser.subcribed) {
            return getRegisterTextMessage(context);
        }

        String url = GlobalConfig.WEIXIN_BASE_DOMAIN + "/register/"
                + context.merchant.linkId + "/"
                + context.inMessage.getFromUserName()
                + "?" + System.currentTimeMillis();
        return WxMpXmlOutMessage.TEXT()
                .fromUser(context.inMessage.getToUserName())
                .toUser(context.inMessage.getFromUserName())
                .content("你已经设置姓名和头像，消息可直接上墙，可点击修改个人信息：<a href=\"" + url + "\">修改</a>")
                .build();
    }

    @Override
    protected boolean matchText(String content) {
        return "1welecome-not-use".equals(content);
    }

}
