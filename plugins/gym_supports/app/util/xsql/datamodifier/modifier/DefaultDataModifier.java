package util.xsql.datamodifier.modifier;

import util.xsql.datamodifier.DataModifier;
import util.xsql.datamodifier.DefaultUtils;

/**
 * @author badqiu
 */
public class DefaultDataModifier implements DataModifier {
	private static final String DEFAULT_STRING = "";
	public Object modify(Object value, String modifierArgument) {
		if(value == null)
			return DefaultUtils.defaultString(modifierArgument, DEFAULT_STRING);
		return value;
	}
}
