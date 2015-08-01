package ext.weixin.invocation;

import ext.weixin.WxMpContext;
import ext.weixin.WxMpInvocation;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutMessage;
import models.base.WeixinUser;
import models.mert.Event;
import models.mert.EventMessage;
import models.mert.Merchant;
import models.mert.enums.MessageType;
import play.Logger;

import java.util.Date;

/**
 * 收取所有的文字.
 */
public class TextInvocation extends WxMpInvocation {
    @Override
    protected WxMpXmlOutMessage doExecute(WxMpContext context) {
        Logger.info("text=" + context.inMessage.getContent());
        WeixinUser weixinUser = WeixinUser.findOrCreateMerchantWxUser(context.merchant, context.inMessage.getFromUserName());

        Logger.info("weixinUser=" + weixinUser);

        if (!weixinUser.subcribed) {
            return getRegisterTextMessage(context);
        }

        Event event = Event.findLastByMerchant(context.merchant);
        EventMessage eventMessage = new EventMessage();
        eventMessage.content = context.inMessage.getContent();
        eventMessage.event = event;
        eventMessage.merchant = Merchant.findById(context.merchant.id);
        eventMessage.messageType = MessageType.TEXT;
        eventMessage.createdAt = new Date();
        eventMessage.weixinUser = weixinUser;
        eventMessage.save();
        return WxMpXmlOutMessage.TEXT()
                .fromUser(context.inMessage.getToUserName())
                .toUser(context.inMessage.getFromUserName())
                .content("收到发来的内容，将上墙：" + context.inMessage.getContent())
                .build();
    }

    @Override protected boolean matchInMessage(WxMpXmlMessage inMessage) {
        //return WxConsts.XML_MSG_TEXT.equals(inMessage.getMsgType()) && false;
        return false; //不再使用
    }
}
