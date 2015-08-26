package com.cninfo.meyes.client.plugin.util;

import java.text.MessageFormat;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.mongodb.DBObject;

/**
 * @author lunianping
 *
 */
public class MysqlUtil extends AbstractDBUtil{
	
	private static final String DRIVER_CLASS= "com.mysql.jdbc.Driver";
	private static final String URL= "jdbc:mysql://{0}:{1}/test?useUnicode=true&characterEncoding=GBK&jdbcCompliantTruncation=false&connectTimeout=10000&socketTimeout=10000";
	
	/**
	 * @param host
	 * @param port
	 * @param sid
	 * @param username
	 * @param password
	 */
	public MysqlUtil(String host,int port,String username,String password){
		MessageFormat mf = new  MessageFormat(URL);
		String url = mf.format(new Object[]{host,port+""});
		DriverManagerDataSource ds = new DriverManagerDataSource(url,username,password);
		ds.setDriverClassName(DRIVER_CLASS);
		setDataSource(ds);
	}
	
	public static void main(String[] args){
		MysqlUtil o = new MysqlUtil("172.30.3.132",3306,"admin","123456");
		String sql = "show status";
		DBObject obj = o.queryForDBList(sql);
		System.out.println(obj.toString());
	}
	
}
