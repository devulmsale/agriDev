package util.xsql.datamodifier.modifier;

import util.xsql.datamodifier.DataModifier;

/**
 * @author badqiu
 */
public class FloatDataModifier implements DataModifier {
	public Object modify(Object value, String modifierArgument) {
		if(value == null) return null;
		if(value instanceof Float) return value;
		return new Float(value.toString());
	}
}
