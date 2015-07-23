package util.xsql;

import java.util.Map;


class ObjectUtils {
	
	public static Object getProperty(Object obj,String key) {
		if(obj == null) return null;
		if(obj instanceof Map) {
			Map map = (Map)obj;
			return map.get(key);
		}else {
			return MapAndObject.FastPropertyUtils.getBeanPropertyValue(obj, key);
		}
	}
	
}
