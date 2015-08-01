package ext.weixin;

public class MD5SignUtil {
    public static String Sign(String content, String key)
            throws SDKRuntimeException {
        if ("".equals(key)) {
            throw new SDKRuntimeException("财付通签名key不能为空！");
        }
        if ("".equals(content)) {
            throw new SDKRuntimeException("财付通签名内容不能为空");
        }
        String signStr = content + "&key=" + key;

        return MD5Util.MD5(signStr).toUpperCase();

    }

    public static boolean VerifySignature(String content, String sign, String md5Key) {
        String signStr = content + "&key=" + md5Key;
        String calculateSign = MD5Util.MD5(signStr).toUpperCase();
        String tenpaySign = sign.toUpperCase();
        return (calculateSign.equals(tenpaySign));
    }
}
