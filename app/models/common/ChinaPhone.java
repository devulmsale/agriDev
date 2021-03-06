package models.common;

import play.data.validation.Check;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by upshan on 15/7/24.
 */
public class ChinaPhone  extends Check {

    public boolean isSatisfied(Object merchantUser ,  Object mobile){
        String regExp = "^[1]([3][0-9]{1}|51|52|59|58|80|82|86|87|88|89)[0-9]{8}$";
        Pattern p = Pattern.compile(regExp);
        mobile = mobile == null ? "" : mobile;
        Matcher m = p.matcher(mobile.toString());
        return m.find();//boolean
    }


}
