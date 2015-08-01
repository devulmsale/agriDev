package ext.pay.weixin.v3.resps;

public class UnifiedOrderResponse extends ResponseBase {

    public UnifiedOrderResponse(ResponseBase resp) {
        super(resp.respString, resp.respProp);
    }
}
