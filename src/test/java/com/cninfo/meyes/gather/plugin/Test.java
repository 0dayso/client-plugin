package com.cninfo.meyes.gather.plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cninfo.meyes.client.model.EventData;
import com.cninfo.meyes.client.plugin.GatherPlugin;
import com.cninfo.meyes.client.plugin.impl.mongodb.MongodbComm;
import com.cninfo.meyes.client.plugin.impl.server.ServerCommon;

public class Test {

	private EventData event;
	private Map<String, Object> params;

	public Test() {
		event = new EventData();
		event.setCategory("Server");
		event.setId("Server,172.31.8.248");
		event.setTime("2015-04-08 12:00:50");
		params = new HashMap<String, Object>();
		params.put("user", "root");
		params.put("password", "123456");
	}

	public void test() {
		// GatherPlugin plugin = new OracleComm("arr_dataguard.sql");
		GatherPlugin plugin = new ServerCommon();

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
		Test test = new Test();
		for (int i = 0; i < 1000; i++) {
			test.test();
		}
		// String line = "	    abc";
		// System.out.println(line.replaceAll("^\\s+", ""));
	}

	// 将文本的结果放在数组,每个数组元素存储文本的一行

	public static String[] result() {
		List<String> list = new ArrayList<String>();
		InputStream in = Test.class.getResourceAsStream("memory.result");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				list.add(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] arr = new String[]{};
		return list.toArray(arr);
	}

}