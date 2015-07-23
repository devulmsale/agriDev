package ext.html;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import play.templates.JavaExtensions;

/**
 * 页面上Html文字相关处理的扩展.
 */
public class HtmlExtension extends JavaExtensions {

    public static String html2text(String value) {
        if (StringUtils.isBlank(value)) {
            return "";
        }
        return Jsoup.parse(value).text();
    }

    public static String truncate(String value, Integer length) {
        if (StringUtils.isBlank(value)) {
            return "";
        }

        if (value.length() >= length) {
            return value.substring(0, length) + "...";
        }

        return value;
    }

    public static String truncatHtml(String htmlValue, Integer length) {
        return truncate(html2text(htmlValue), length);
    }

}
