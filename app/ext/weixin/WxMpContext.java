package ext.weixin;

import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutMessage;
import models.mert.Merchant;
import util.extension.InvocationContext;

/**
 * 微信公众号扩展上下文.
 */
public class WxMpContext implements InvocationContext {

    /**
     * 当前商户.
     */
    public Merchant merchant;

    public WxMpConfigStorage wxConfigStorage;

    public WxMpService wxService;

    public WxMpXmlMessage inMessage;

    public WxMpXmlOutMessage outMessage;

    private WxMpContext() {
    }

    public static WxMpContext build(Merchant merchant, WxMpConfigStorage wxCpConfigStorage, WxMpService wxCpService, WxMpXmlMessage inMessage) {
        WxMpContext context = new WxMpContext();
        context.merchant = merchant;
        context.wxConfigStorage = wxCpConfigStorage;
        context.wxService = wxCpService;
        context.inMessage = inMessage;
        context.outMessage = null;
        return context;
    }
}
