package controllers.api;

import ext.weixin.WxMpContext;
import ext.weixin.WxMpInvocation;
import helper.GlobalConfig;
import helper.WxMpHelper;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.util.crypto.WxMpCryptUtil;
import me.chanjar.weixin.mp.util.xml.XStreamTransformer;
import models.base.WeixinUser;
import models.mert.Merchant;
import org.apache.commons.lang.StringUtils;
import play.Logger;
import play.libs.IO;
import play.mvc.Controller;
import util.extension.DefaultAction;
import util.extension.ExtensionInvoker;
import util.extension.ExtensionResult;

/**
 * 微信统一响应处理接口.
 */
public class WeixinAPI extends Controller {

    private final static ThreadLocal<WeixinUser> _currentUser = new ThreadLocal<>();
    /**
     * 响应微信心跳检查.
     *
     * @param signature
     * @param timestamp
     * @param nonce
     * @param echostr
     */
    public static void heartbeat(String linkId, String signature, String timestamp, String nonce, String echostr) {
        Logger.info("linkId=%s, signature=%s, timestamp=%s, nonce=%s, echostr=%s",
                linkId, signature, timestamp, nonce, echostr);
        if (StringUtils.isBlank(linkId)) {
            Logger.info("linkId is Empty");
            renderText("InvalidLinkId");
        }
        Merchant merchant = Merchant.findByLinkId(linkId);
        if (merchant == null) {
            Logger.info("找不到linkId(%s)对应的商户", linkId);
            renderText("BadMerchant");
        }
        WxMpConfigStorage wxMpConfigStorage = WxMpHelper.getWxMpConfigStorage(merchant);
        WxMpService wxMpService = WxMpHelper.getWxMpService(wxMpConfigStorage);
        Logger.info("信息获取成功,等待验证..");
        if (wxMpService.checkSignature(timestamp, nonce, signature)) {
            renderText(echostr);
        }
        Logger.warn("验证失败，请检查一下是否微信算法有变化？");
        renderText("BadSignature");
    }

    public static void message(String linkId) {
        String xmlBody = IO.readContentAsString(request.body);
        Logger.info("xmlBody=%s", xmlBody);
        String signature = params.get("signature");
        String timestamp = params.get("timestamp");
        String nonce = params.get("nonce");
        String encrypt_type = params.get("encrypt_type");
        String msg_signature = params.get("msg_signature");
        Logger.info("linkId=%s, signature=%s, timestamp=%s, nonce=%s, encrypt_type=%s",
                linkId, signature, timestamp, nonce, encrypt_type);
        if (StringUtils.isBlank(xmlBody)) {
            renderText("Bad Request.");
        }
        if (StringUtils.isBlank(linkId)) {
            Logger.info("linkId is Empty");
            renderText(""); //给微信服务器发送空串，以使微信不再通知
        }
        final Merchant merchant = Merchant.findByLinkId(linkId);
        if (merchant == null) {
            Logger.info("找不到linkId(%s)对应的商户", linkId);
            renderText(""); //给微信服务器发送空串，以使微信不再通知
            return;
        }

        if (!merchant.isAvaliable()) {
            Logger.info("商户已经过期（id:%s, name:%s, linkId:%s）", merchant.id, merchant.fullName, linkId);
            renderText(""); //给微信服务器发送空串，以使微信不再通知
        }
        WxMpConfigStorage wxMpConfigStorage = WxMpHelper.getWxMpConfigStorage(merchant);
        WxMpService wxMpService = WxMpHelper.getWxMpService(wxMpConfigStorage);

        if (!wxMpService.checkSignature(timestamp, nonce, signature)) {
            // 消息签名不正确，说明不是公众平台发过来的消息
            Logger.info("消息签名不正确，说明不是公众平台发过来的消息");
            renderText(""); //给微信服务器发送空串，以使微信不再通知
        }

        WxMpXmlMessage inMessage = null;
        if ("aes".equals(encrypt_type)) {
            // 是aes加密的消息
            WxMpCryptUtil cryptUtil = new WxMpCryptUtil(wxMpConfigStorage);
            String plainText = cryptUtil.decrypt(msg_signature, timestamp, nonce, xmlBody);
            Logger.info("AES plainText=%s", plainText);
            inMessage = WxMpXmlMessage.fromXml(plainText);
        } else {
            Logger.info("xmlBody : = %s" , xmlBody);
            inMessage = WxMpXmlMessage.fromXml(xmlBody);
        }

        WxMpContext wxMpContext = WxMpContext.build(merchant, wxMpConfigStorage, wxMpService, inMessage);

        // 如果没有认证 创建 WeixinUser
        if(!merchant.isAuth) {
            WeixinUser wxUser = WeixinUser.findOrCreateMerchantWxUser(merchant, inMessage.getFromUserName());
            if (wxUser != null) {
                session.put(GlobalConfig.WEIXIN_MP_SESSION_USER_KEY, wxUser.id);
                renderArgs.put("currentUser", wxUser);
                _currentUser.set(wxUser);
            }
        }


        ExtensionResult result = ExtensionInvoker.run(WxMpInvocation.class, wxMpContext, new DefaultAction<WxMpContext>() {
            @Override
            public ExtensionResult execute(WxMpContext context) {
                Logger.info("无法识别的操作，返回默认消息.");
                String content = merchant.weixinDefaultMessage;
                if (StringUtils.isBlank(content)) {
                    content = "欢迎使用微信公众号.";
                }
                return ExtensionResult.SUCCESS;
            }
        });

        if (result.isOk()) {
            WxMpXmlOutMessage outMessage = wxMpContext.outMessage;
            if (outMessage != null) {
                String outXml = outMessage.toEncryptedXml(wxMpConfigStorage);
                Logger.info("立即返回消息: original xml:\n"
                                + XStreamTransformer.toXml((Class) outMessage.getClass(), outMessage)
                                + "\n encrypt xml:\n" + outXml
                );
                Logger.info("立即返回消息 fromUser=%s, toUser=%s", outMessage.getFromUserName(), outMessage.getToUserName());
                renderXml(outXml);
            }
        }

        Logger.info("空串表示无响应");

        // 空串表示无响应，当然可能是异步回复
        renderText("");
    }

}
