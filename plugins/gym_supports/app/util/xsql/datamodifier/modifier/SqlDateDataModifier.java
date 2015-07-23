package util.xsql.datamodifier.modifier;

import util.xsql.datamodifier.DataModifier;
import util.xsql.datamodifier.DefaultUtils;

import java.text.DateFormat;

/**
 * @author badqiu
 */
public class SqlDateDataModifier implements DataModifier {
	private static final String DEFAULT_SQLDATE_FORMAT = "yyyy-MM-dd";
	
	private String dateFormat = DEFAULT_SQLDATE_FORMAT;
	
	public SqlDateDataModifier(){}
	
	public SqlDateDataModifier(String dateFormat) {
		this.dateFormat = dateFormat;
	}
	
	public Object modify(Object value, String modifierArgument) throws Exception {
		if(value == null) return null;
		if(value instanceof java.sql.Date) return value;
		DateFormat df = DefaultUtils.defaultDateFormat(modifierArgument, dateFormat);
		return new java.sql.Date(df.parse(value.toString()).getTime());
	}
}
