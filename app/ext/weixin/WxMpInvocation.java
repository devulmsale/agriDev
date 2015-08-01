package ext.weixin;

import helper.GlobalConfig;
import helper.WxMpHelper;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.WxMpCustomMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutMessage;
import play.Logger;
import play.jobs.Job;
import util.extension.ExtensionInvocation;
import util.extension.ExtensionResult;
import util.extension.annotation.ExtensionPoint;

/**
 * 定义微信扩展点.
 */
@ExtensionPoint("Weixin")
public abstract class WxMpInvocation implements ExtensionInvocation<WxMpContext> {

    private static final ThreadLocal<WxMpContext> _threadLocalWeixinContext = new ThreadLocal<>();

    @Override
    public ExtensionResult execute(WxMpContext context) {

        _threadLocalWeixinContext.set(context);

        context.outMessage = doExecute(context);

        _threadLocalWeixinContext.remove();

        return ExtensionResult.SUCCESS;
    }

    /**
     * 从ThreadLocal中得到当前的WeixinContext
     * @return
     */
    public WxMpContext getContext() {
        return _threadLocalWeixinContext.get();
    }

    /**
     * 处理微信消息响应的代码。
     */
    protected abstract WxMpXmlOutMessage doExecute(WxMpContext context);

    @Override
    public boolean match(WxMpContext context) {
        Logger.info("process match..." + this.getClass().getName());
        WxMpXmlMessage inMessage = context.inMessage;

        // 如果是文本消息
        if (WxConsts.XML_MSG_TEXT.equals(inMessage.getMsgType()) && matchText(inMessage.getContent())) {
            return true;
        }

        if (WxConsts.XML_MSG_EVENT.equals(inMessage.getMsgType())) {
            String event = inMessage.getEvent();
            String eventKey = inMessage.getEventKey();
            Logger.info("event:%s, eventKey: %s", event, eventKey);
            // 如果是菜单点击事件.
            if (WxConsts.EVT_CLICK.equals(event)) {
                if (getMenuKey() != null && getMenuKey().equals(eventKey)) {
                    // 匹配菜单点击
                    return true;
                }
                if (matchMenuKey(eventKey)) {
                    return true;
                }
            }

            if (matchEvent(event, eventKey)) {
                return true;
            }
        }

        return matchInMessage(context.inMessage);
    }

    /**
     * 生成签到文本消息.
     */
    protected WxMpXmlOutMessage getRegisterTextMessage(WxMpContext context) {
        String url = GlobalConfig.WEIXIN_BASE_DOMAIN + "/register/"
                + context.merchant.linkId + "/"
                + context.inMessage.getFromUserName()
                + "?" + System.currentTimeMillis();
        return WxMpXmlOutMessage.TEXT()
                .fromUser(context.inMessage.getToUserName())
                .toUser(context.inMessage.getFromUserName())
                .content("还没设置姓名和头像，消息不能显示，请先点击：<a href=\"" + url + "\">签到</a>")
                .build();
    }

    protected boolean matchInMessage(WxMpXmlMessage inMessage) {
        return false;
    }


    protected boolean matchEvent(String event, String eventKey) {
        return false;
    }

    /**
     * 对于文本类消息，对用户输入的内容进行响应.
     * 所有需要响应文本输入的Invocation使用此类.
     */
    protected boolean matchText(String content) {
        return false;
    }

    /**
     * 提供一个扩展点，使用正则之类的机制来匹配菜单的key，以灵活处理更多的内容.
     *
     * @param menuKey
     * @return
     */
    protected boolean matchMenuKey(String menuKey) {
        return false;
    }

    /**
     * 定义菜单key，如果有值，会检查菜单点击事件，并响应相同key的点击行为.
     *
     * @return 匹配的菜单key；默认为null，不使用
     */
    public String getMenuKey() {
        return null;
    }

    /**
     * 发送异步文本消息给当前的用户.
     */
    public void sendAsyncTextMessage(final String content) {
        final WxMpContext context = getContext();
        new Job() {
            @Override
            public void doJob() throws Exception {
                WxMpCustomMessage wxCpMessage = WxMpCustomMessage
                        .TEXT()
                        .toUser(context.inMessage.getFromUserName())
                        .content(content)
                        .build();
                WxMpService wxCpService = WxMpHelper.getWxMpService(context.wxConfigStorage);
                try {
                    wxCpService.customMessageSend(wxCpMessage);
                } catch (WxErrorException e) {
                    Logger.error(e, "发送消息出现异常");
                }
            }
        }.now();
    }
}
