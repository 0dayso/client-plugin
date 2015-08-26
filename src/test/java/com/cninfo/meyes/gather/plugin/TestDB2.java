package com.cninfo.meyes.gather.plugin;


import java.util.HashMap;
import java.util.Map;

import com.cninfo.meyes.client.model.EventData;
import com.cninfo.meyes.client.plugin.impl.db2.DB2Comm;


public class TestDB2 {
	private EventData event;
	private Map<String,Object> params = new HashMap<String,Object>();
	public TestDB2() {
		event = new EventData();
//		event.setCategory("Server");
//		event.setId("Server,172.31.8.167");
//		event.setTime("2015-04-08 12:00:50");
		
		event.setCategory("Server");
		event.setId("Server,172.26.5.107,50000");
		event.setTime("2015-04-08 12:00:50");
		params.put("dbname","test");
		params.put("user","chen");
		params.put("password","chen");
		
	}

	public void test() {
		//GatherPlugin plugin = new HardWareForDellAndWin();
		// GatherPlugin plugin = new HardWareForHPAndWin();
		DB2Comm plugin = new DB2Comm("arr_client.sql obj_database.sql");
		plugin.before(params);


		try {
			plugin.process(event);
		} catch (Exception e) {
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
		TestDB2 test = new TestDB2();
		test.test();

	}
}
