package util.xsql.datamodifier.modifier;

import util.xsql.datamodifier.DataModifier;

/**
 * @author badqiu
 */
public class BooleanDataModifier implements DataModifier {
	public Object modify(Object value, String modifierArgument) {
		if(value == null) return null;
		if(value instanceof Boolean) return value;
		return new Boolean(value.toString());
	}
}
