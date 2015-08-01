package ext.weixin.invocation;

import ext.weixin.WxMpContext;
import ext.weixin.WxMpInvocation;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutMessage;
import models.base.WeixinUser;
import models.base.WeixinUserLocation;
import play.Logger;

/**
 * 更新地理位置的请求
 */
public class LocationInvocation extends WxMpInvocation {

    @Override protected WxMpXmlOutMessage doExecute(WxMpContext context) {
        WxMpXmlMessage inMessage = context.inMessage;
        Logger.info("inMessage.getEvent() :" + inMessage.getEvent() + " | longitude :" + inMessage.getLongitude() + " | latitude :" + inMessage.getLatitude() + "----------");

        String fromOpenId = inMessage.getFromUserName();
        Logger.info("fromOpenId :" + fromOpenId);
        WeixinUser wxUser = WeixinUser.findByOpenId(fromOpenId);
        Logger.info("wxUser :" + wxUser + "------------");
        if (wxUser != null) {
            WeixinUserLocation.create(wxUser, inMessage.getLatitude().toString(), inMessage.getLongitude().toString());
        }
        return null;
    }

    @Override protected boolean matchEvent(String event, String eventKey) {
        return WxConsts.EVT_LOCATION.equals(event);
    }
}
