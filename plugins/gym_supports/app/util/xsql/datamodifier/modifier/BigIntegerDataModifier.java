package util.xsql.datamodifier.modifier;

import util.xsql.datamodifier.DataModifier;

import java.math.BigInteger;

/**
 * @author badqiu
 */
public class BigIntegerDataModifier implements DataModifier {
	public Object modify(Object value, String modifierArgument) {
		if(value == null) return null;
		if(value instanceof BigInteger) return value;
		return new BigInteger(value.toString());
	}
}
