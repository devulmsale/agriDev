package util.xsql;

import util.xsql.SafeSqlProcesser;
import util.xsql.safesql.DirectReturnSafeSqlProcesser;
import util.xsql.safesql.EscapeBackslashAndSingleQuotesSafeSqlProcesser;
import util.xsql.safesql.EscapeSingleQuotesSafeSqlProcesser;

import java.util.HashMap;
import java.util.Map;

/**
 * 工厂方法,提供不同数据库的SafeSqlProcesser实例生成工厂
 * @author badqiu
 *
 */
public class SafeSqlProcesserFactory {
	
	private SafeSqlProcesserFactory(){}
	
	public static SafeSqlProcesser getMysql() {
		return new EscapeBackslashAndSingleQuotesSafeSqlProcesser();
	}
	
	public static SafeSqlProcesser getPostgreSql() {
		return new EscapeBackslashAndSingleQuotesSafeSqlProcesser();
	}
	
	public static SafeSqlProcesser getMsSqlServer() {
		return new EscapeSingleQuotesSafeSqlProcesser();
	}
	
	public static SafeSqlProcesser getOracle() {
		return new EscapeSingleQuotesSafeSqlProcesser();
	}
	
	public static SafeSqlProcesser getDB2() {
		return new EscapeSingleQuotesSafeSqlProcesser();
	}
	
	public static SafeSqlProcesser getSybase() {
		return new EscapeSingleQuotesSafeSqlProcesser();
	}
	
	private static Map cacheJdbcUrlProcessMap = new HashMap();
	public static SafeSqlProcesser getFromCacheByJdbcUrl(String jdbcUrl) {
		SafeSqlProcesser safeSqlProcesser = (SafeSqlProcesser) cacheJdbcUrlProcessMap.get(jdbcUrl);
		if(safeSqlProcesser == null) {
			safeSqlProcesser = getByJdbcUrl(jdbcUrl);
			cacheJdbcUrlProcessMap.put(jdbcUrl, safeSqlProcesser);
		}
		return safeSqlProcesser;
	}

	public static SafeSqlProcesser getByJdbcUrl(String jdbcUrl) {
		SafeSqlProcesser result = null;
		if(jdbcUrl.indexOf("mysql") >= 0) {
			result = SafeSqlProcesserFactory.getMysql();
		}else if(jdbcUrl.indexOf("oci") >= 0) {
			result = SafeSqlProcesserFactory.getOracle();
		}else if(jdbcUrl.indexOf("db2") >= 0) {
			result = SafeSqlProcesserFactory.getDB2();
		}else if(jdbcUrl.indexOf("Postgre") >= 0) {
			result = SafeSqlProcesserFactory.getPostgreSql();
		}else if(jdbcUrl.indexOf("Sybase") >= 0) {
			result = SafeSqlProcesserFactory.getSybase();
		}else if(jdbcUrl.indexOf("sqlserver") >= 0) {
			result = SafeSqlProcesserFactory.getMsSqlServer();
		}else {
			result = new DirectReturnSafeSqlProcesser();
		}
		return result;
	}
}
