package helper;


import play.Logger;
import play.libs.WS;

/**
 * 百度地图功能辅助工具类.
 */
public class BaiDuApiHelper {


    public static String getBaudiAddress(String lat, String lon) {
        String url = "http://api.map.baidu.com/geocoder/v2/?ak=" + GlobalConfig.BAIDU_API_AK + "&callback=renderReverse&location=" + lat + "," + lon + "&output=json&pois=0";
        WS.HttpResponse response = WS.url(url).post();
        String responseBody = response.getString("UTF-8");
        Logger.info("responseBody :" + responseBody);
        responseBody = responseBody.substring(responseBody.indexOf("matted_address") , responseBody.indexOf("business"));
        return  responseBody.substring(17 , responseBody.length()-3);
//        Logger.info("responseBody Substring :" + responseBody);
//        JsonElement tmpJsonElement = Streams.parse(new JsonReader(new StringReader(responseBody.trim())));
//        Logger.info("tmpJsonElement :" + tmpJsonElement);
//        JsonObject tmpJsonObject = tmpJsonElement.getAsJsonObject();
//        Logger.info("address Name :" + tmpJsonObject.get("formatted_address").getAsString());
//        return tmpJsonObject.get("formatted_address").getAsString();
    }

}
