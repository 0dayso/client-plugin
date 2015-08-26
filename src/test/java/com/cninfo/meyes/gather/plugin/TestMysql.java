package com.cninfo.meyes.gather.plugin;

import java.util.HashMap;
import java.util.Map;

import com.cninfo.meyes.client.model.EventData;
import com.cninfo.meyes.client.plugin.impl.mysql.MysqlComm;

public class TestMysql {
	private EventData event;
	private Map<String, Object> params = new HashMap<String, Object>();

	public TestMysql() {
		event = new EventData();
		event.setCategory("MySQL");
		event.setId("MySQL,192.168.7.136,3309");
		event.setTime("2015-08-12 12:00:50");

		params.put("user", "admin");
		params.put("password", "gcqbd45u");

	}

	public void test() {
		MysqlComm plugin = new MysqlComm("arr_process.sql obj_dbstatu2s.sql obj_variables1.sql obj_variables.sql");
		plugin.before(params);

		try {
			plugin.process(event);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(event.getDoc().toString());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TestMysql test = new TestMysql();
		test.test();

	}
}
