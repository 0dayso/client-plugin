package com.cninfo.meyes.client.plugin.util;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Properties;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class DB2Util extends AbstractDBUtil {
	private static final String DRIVER_CLASS = "com.ibm.db2.jcc.DB2Driver";
	private static final String URL = "jdbc:db2://{0}:{1}/{2}";

	/**
	 * @param host
	 * @param port
	 * @param dbname
	 * @param username
	 * @param password
	 */
	public DB2Util(String host, int port, String dbname, String username,
			String password) {
		MessageFormat mf = new MessageFormat(URL);
		Object[] o = { host, port + "", dbname };
		String url = mf.format(o);
		DriverManagerDataSource ds = new DriverManagerDataSource(url, username,
				password);
		ds.setDriverClassName(DRIVER_CLASS);
		Properties prop = new Properties();
		// 设置超时参数
		// prop.put("connectionProperties",
		// "oracle.net.CONNECT_TIMEOUT=10000&oracle.net.READ_TIMEOUT=10000");
		// prop.put("oracle.net.CONNECT_TIMEOUT", 60000);
		// prop.put("oracle.jdbc.ReadTimeout", 60000);
		ds.setConnectionProperties(prop);
		setDataSource(ds);
	}

	public static void main(String[] args) {
		DBObject obj = new BasicDBObject();
		System.out.println(new Date());
		try {
			DB2Util o = new DB2Util("172.26.5.107", 50000, "db2inst1", "db2inst1",
					"admin");
			String sql = "select to_char(snapshot_timestamp,'YYYY/MM/DD HH24:MI:SS') LOGON_TIME,DB_NAME,APPL_NAME,AUTHID,APPL_ID ,APPL_STATUS,CLIENT_PLATFORM,CLIENT_NNAME,TPMON_CLIENT_WKSTN,TPMON_CLIENT_APP from sysibmadm.APPLICATIONS\r\n";
			BasicDBList list = o.queryForDBList(sql);
			obj.put("connections", list);

			System.out.println(list.toString());
			System.out.println(obj.toString());

		}
		catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println(new Date());

		System.out.println("hello".equals("hello"));
	}

}
