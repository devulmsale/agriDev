package util.xsql.datamodifier.modifier;

import util.xsql.datamodifier.DataModifier;

import java.math.BigDecimal;

/**
 * @author badqiu
 */
public class BigDecimalDataModifier implements DataModifier {
	public Object modify(Object value, String modifierArgument) {
		if(value == null) return null;
		if(value instanceof BigDecimal) return value;
		return new BigDecimal(value.toString());
	}
}
