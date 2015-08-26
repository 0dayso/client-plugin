package com.cninfo.meyes.gather.plugin;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class TestHpFans {
	public static void main(String[] args) {
		BasicDBList list = new BasicDBList();
		DBObject obj = null;

		String[] result = new String[] {
				"Fan  Location        Present Speed  of max  Redundant  Partner  Hot-pluggable"
						+ "",
				"---  --------        ------- -----  ------  ---------  -------  -------------"
						+ "",
				"#1   SYSTEM          Yes     NORMAL  6%      Yes        0        Yes ",
				"#2   SYSTEM          Yes     NORMAL  6%      Yes        0        Yes ",
				"#3   SYSTEM          Yes     NORMAL  6%      Yes        0        Yes ",
				"#4   SYSTEM          Yes     NORMAL  16%     Yes        0        Yes ",
				"#5   SYSTEM          Yes     NORMAL  27%     Yes        0        Yes   ",
				"#6   SYSTEM          Yes     NORMAL  27%     Yes        0        Yes  " };
		for (int i = 0; i < result.length; i++) {

			if (result[i].startsWith("#")) {
				obj = new BasicDBObject();
				list.add(obj);
				String[] str = result[i].split("\\s+");

				for (int j = 0; j < str.length; j++) {
					obj.put("Fan", str[0]);
					obj.put("Location", str[1]);
					obj.put("Present", str[2]);
					obj.put("Speed", str[3]);
					obj.put("ofMax", str[4]);
					obj.put("Redundant", str[5]);
					obj.put("Partner", str[6]);
					obj.put("HotPluggable", str[7]);
				}

			}
		}
		
		System.out.println(list.toString());

	}

}
