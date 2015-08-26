package com.cninfo.meyes.gather.plugin;

import java.util.HashMap;
import java.util.Map;

import com.cninfo.meyes.client.model.EventData;
import com.cninfo.meyes.client.plugin.impl.oracle.OracleComm;

public class TestOracle {
	private EventData event;
	private Map<String, Object> params = new HashMap<String, Object>();

	public TestOracle() {
		event = new EventData();
		// event.setCategory("Server");
		// event.setId("Server,172.31.8.167");
		// event.setTime("2015-04-08 12:00:50");

		event.setCategory("ORACLE");
		event.setId("ORACLE,172.30.3.5,1521,trs");
		event.setTime("2015-04-08 12:00:50");
		params.put("user", "mon");
		params.put("password", "ond6mesk");

	}

	public void test() {
		// GatherPlugin plugin = new HardWareForDellAndWin();
		// GatherPlugin plugin = new HardWareForHPAndWin();
		OracleComm plugin = new OracleComm(
				"arr_tablespace.sql;arr_job.sql;arr_parameter.sql;arr_invalidobj.sql;arr_client.sql;arr_hits.sql");
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
		TestOracle test = new TestOracle();
		test.test();

	}
}
