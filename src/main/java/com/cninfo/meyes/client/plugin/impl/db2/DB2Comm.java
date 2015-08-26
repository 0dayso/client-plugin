package com.cninfo.meyes.client.plugin.impl.db2;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cninfo.meyes.client.model.EventData;
import com.cninfo.meyes.client.plugin.impl.AbstractGatherPlugin;
import com.cninfo.meyes.client.plugin.util.DB2Util;
import com.mongodb.DBObject;

public class DB2Comm extends AbstractGatherPlugin {
	private static final Log logger = LogFactory.getLog(DB2Comm.class);

	private DB2Util util;

	private String[] args;

	public DB2Comm(String args) {
		this.args = args.split("[ |;]");
	}

	public DB2Comm(String... args) {
		this.args = args;
	}

	public DB2Comm() {

	}

	public void process(EventData event) throws Exception {
		// TODO Auto-generated method stub

		String id = event.getId();
		String[] strs = id.split(",");
		String host = strs[1];
		int port = Integer.parseInt(strs[2]);
		// String sid = strs[3];
		String dbname = getParam("dbname").toString();
		logger.debug("dbname:" + dbname);

		String user = getParam("user").toString();
		String password = getParam("password").toString();
		util = new DB2Util(host, port, dbname, user, password);

		DBObject status = this.getStatus();
		DBObject doc = event.getDoc();
		doc.putAll(status.toMap());

		for (String file : args) {
			logger.debug("args : " + file);
			String name = file.replaceAll(".+\\\\", "");
			String cate = name.replaceFirst("^[a-zA-z]+_", "").replaceAll(
					"\\..+$", "");
			logger.debug("cate : " + cate);
			DBObject obj = null;
			String f = "com/cninfo/meyes/client/plugin/impl/db2/" + file;
			String sql = util.getSql(f);
			if (sql == null) continue;
			if (name.toLowerCase().startsWith("arr")) {
				obj = util.queryForDBList(sql);
			}
			else
				if (name.toLowerCase().startsWith("obj")) {
					obj = util.queryForDBObject(sql);
				}
			if (obj != null) {
				doc.put(cate, obj);
			}
		}

	}

	private DBObject getStatus() {
		String sql = "select VERSIONNUMBER version,\r\n"
				+ "to_char(VERSION_TIMESTAMP,'yyyy-mm-dd hh24:mi:ss') startup_time,AUTHID instance_name,VERSIONBUILDLEVEL level \r\n"
				+ "from SYSIBM.SYSVERSIONS";
		return util.queryForDBObject(sql);
	}

}
