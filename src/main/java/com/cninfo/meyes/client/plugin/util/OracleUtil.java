package com.cninfo.meyes.client.plugin.util;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Properties;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.mongodb.DBObject;

/**
 * @author lunianping
 *
 */
public class OracleUtil extends AbstractDBUtil {
	
	private static final String DRIVER_CLASS= "oracle.jdbc.driver.OracleDriver";
	private static final String URL= "jdbc:oracle:thin:@{0}:{1}:{2}";
	
	/**
	 * @param host
	 * @param port
	 * @param sid
	 * @param username
	 * @param password
	 */
	public OracleUtil(String host,int port,String sid,String username,String password){
		MessageFormat mf = new  MessageFormat(URL);
		Object[] o = {host,port+"",sid};
		String url = mf.format(o);
		DriverManagerDataSource ds = new DriverManagerDataSource(url,username,password);
		ds.setDriverClassName(DRIVER_CLASS);
		Properties prop = new Properties();
		//设置超时参数
		//prop.put("connectionProperties", "oracle.net.CONNECT_TIMEOUT=10000&oracle.net.READ_TIMEOUT=10000");
		prop.put("oracle.net.CONNECT_TIMEOUT", 60000);
		prop.put("oracle.jdbc.ReadTimeout", 60000);
		ds.setConnectionProperties(prop);
		setDataSource(ds);
	}
	
	
	public static void main(String[] args){
		System.out.println(new Date());
		try{
		OracleUtil o = new OracleUtil("172.30.3.212",1521,"gddh","mon","ond6mesk");
		String sql = "select b.sid,b.username,b.machine,count(0) CURSOR_NUM from v$open_cursor a,v$session b where a.sid=b.sid and username is not null group by b.sid,b.username,b.machine";
		DBObject obj = o.queryForDBList(sql);
		System.out.println(obj.toString());
		
		sql = "select status,version,host_name,to_char(startup_time,'yyyy-mm-dd hh24:mi:ss') startup_time from v$instance";
		obj = o.queryForDBObject(sql);
		System.out.println(obj.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
		
		System.out.println(new Date());
	}
}
