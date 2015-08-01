package ext.weixin.invocation;

import ext.weixin.WxMpContext;
import ext.weixin.WxMpInvocation;
import helper.imageupload.ImageUploadResult;
import helper.imageupload.ImageUploader;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutMessage;
import models.base.WeixinUser;
import models.mert.Event;
import models.mert.EventMessage;
import models.mert.Merchant;
import models.mert.enums.MessageType;
import org.apache.commons.io.FileUtils;
import play.Logger;

import java.io.File;
import java.net.URL;
import java.util.Date;

/**
 * 测试上传照片的接收保存.
 */
public class PhotoInvocation extends WxMpInvocation {
    @Override
    protected WxMpXmlOutMessage doExecute(WxMpContext context) {
        Logger.info("picUrl=" + context.inMessage.getPicUrl());
        WeixinUser weixinUser = WeixinUser.findOrCreateMerchantWxUser(context.merchant, context.inMessage.getFromUserName());
        Event event = Event.findLastByMerchant(context.merchant);
        EventMessage eventMessage = new EventMessage();
        //eventMessage.imageUrl = context.inMessage.getPicUrl();
        File tmpFile = null;
        try {
            tmpFile = File.createTempFile("wxmpimg", ".png");
            FileUtils.copyURLToFile(new URL(context.inMessage.getPicUrl()), tmpFile);

            ImageUploadResult imageUploadResult = ImageUploader.upload(tmpFile);
            eventMessage.imageUfid = imageUploadResult.ufId;
            eventMessage.imageUrl = ImageUploader.getImageUrl(imageUploadResult.ufId, "720x");
        } catch (Exception e) {
            Logger.warn(e, "处理临时文件异步");
            eventMessage.imageUrl = context.inMessage.getPicUrl();
        }
        Logger.info("imageUrl:" + eventMessage.imageUrl);
        eventMessage.event = event;
        eventMessage.merchant = Merchant.findById(context.merchant.id);
        eventMessage.messageType = MessageType.IMAGE;
        eventMessage.createdAt = new Date();
        eventMessage.weixinUser = weixinUser;
        eventMessage.save();

        if (!weixinUser.subcribed) {
            return getRegisterTextMessage(context);
        }

        return WxMpXmlOutMessage.TEXT()
                .fromUser(context.inMessage.getToUserName())
                .toUser(context.inMessage.getFromUserName())
                .content("收到上传的照片.")
                .build();
    }

    @Override protected boolean matchInMessage(WxMpXmlMessage inMessage) {
        //return WxConsts.XML_MSG_IMAGE.equals(inMessage.getMsgType());
        return false;
    }
}
