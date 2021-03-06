package ext.pay.weixin.v3;

import ext.pay.weixin.v3.resps.Notify;

import java.util.ArrayList;
import java.util.List;

public class WxpayNotifyPublisher {
    // FIELDS
    protected List<WxpayNotifyListener> listNl;

    // CONSTRUCT
    public WxpayNotifyPublisher() {
        this.listNl = new ArrayList<WxpayNotifyListener>();

        return;
    }

    // SINGLETON
    private static class Singleton {
        public static WxpayNotifyPublisher instance = new WxpayNotifyPublisher();
    }

    public static WxpayNotifyPublisher getDefaultInstance() {
        return (
                Singleton.instance
        );
    }

    // EVENTQUEUE
    public void addListener(WxpayNotifyListener l) {
        this.listNl.add(l);

        return;
    }

    public void removeListener(WxpayNotifyListener l) {
        this.listNl.remove(l);

        return;
    }

    /**
     * @return true if one of the listener reports handled.
     */
    public boolean publish(Notify notice) {
        for (WxpayNotifyListener l : this.listNl) {
            try {
                if (l.handle(notice)) {
                    return (true);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                continue;
            }
        }

        return (false);
    }
}
