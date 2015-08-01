package ext.pay.weixin;


import play.Logger;

import java.net.URLEncoder;
import java.util.*;
import java.util.Map.Entry;

public class CommonUtil {

    public static String createNoncestr(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String res = "";
        for (int i = 0; i < length; i++) {
            Random rd = new Random();
            res += chars.indexOf(rd.nextInt(chars.length() - 1));
        }
        return res;
    }

    public static String createNoncestr() {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String res = "";
        for (int i = 0; i < 16; i++) {
            Random rd = new Random();
            res += chars.charAt(rd.nextInt(chars.length() - 1));
        }
        return res;
    }

    public static String formatQueryParaMap(HashMap<String, String> parameters)
            throws SDKRuntimeException {

        String buff = "";
        try {
            List<Entry<String, String>> infoIds = new ArrayList<Entry<String, String>>(
                    parameters.entrySet());

            Collections.sort(infoIds,
                    new Comparator<Entry<String, String>>() {
                        public int compare(Entry<String, String> o1,
                                           Entry<String, String> o2) {
                            return (o1.getKey()).toString().compareTo(
                                    o2.getKey());
                        }
                    });

            for (int i = 0; i < infoIds.size(); i++) {
                Entry<String, String> item = infoIds.get(i);
                if (item.getKey() != "") {
                    buff += item.getKey() + "="
                            + URLEncoder.encode(item.getValue(), "utf-8") + "&";
                }
            }
            if (buff.isEmpty() == false) {
                buff = buff.substring(0, buff.length() - 1);
            }
        } catch (Exception e) {
            throw new SDKRuntimeException(e.getMessage());
        }

        return buff;
    }

    public static String formatBizQueryParaMap(HashMap<String, String> paraMap,
                                               boolean urlencode) throws SDKRuntimeException {

        String buff = "";
        try {
            List<Entry<String, String>> infoIds = new ArrayList<Entry<String, String>>(
                    paraMap.entrySet());

            Collections.sort(infoIds,
                    new Comparator<Entry<String, String>>() {
                        public int compare(Entry<String, String> o1,
                                           Entry<String, String> o2) {
                            return (o1.getKey()).toString().compareTo(
                                    o2.getKey());
                        }
                    });

            for (int i = 0; i < infoIds.size(); i++) {
                Entry<String, String> item = infoIds.get(i);
                //System.out.println(item.getKey());
                if (!"".equals(item.getKey())) {

                    String key = item.getKey();
                    String val = item.getValue();
                    Logger.info("key={}, value={}", key, val);
                    if (urlencode) {
                        val = URLEncoder.encode(val, "utf-8");
                    }
                    buff += key.toLowerCase() + "=" + val + "&";

                }
            }

            if (buff.isEmpty() == false) {
                buff = buff.substring(0, buff.length() - 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.warn("出错了。。。。", e);
            throw new SDKRuntimeException(e.getMessage());
        }
        return buff;
    }

    public static boolean isNumeric(String str) {
        if (str.matches("\\d *")) {
            return true;
        } else {
            return false;
        }
    }

    public static String arrayToXml(HashMap<String, String> arr) {
        String xml = "<xml>";

        Iterator<Entry<String, String>> iter = arr.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<String, String> entry = iter.next();
            String key = entry.getKey();
            String val = entry.getValue();
            if (isNumeric(val)) {
                xml += "<" + key + ">" + val + "</" + key + ">";

            } else {
                xml += "<" + key + "><![CDATA[" + val + "]]></" + key + ">";
            }
        }

        xml += "</xml>";
        return xml;
    }

}
