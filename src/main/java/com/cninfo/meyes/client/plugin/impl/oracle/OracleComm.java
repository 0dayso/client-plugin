package com.cninfo.meyes.client.plugin.impl.oracle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cninfo.meyes.client.model.EventData;
import com.cninfo.meyes.client.plugin.impl.AbstractGatherPlugin;
import com.cninfo.meyes.client.plugin.util.OracleUtil;
import com.mongodb.DBObject;

public class OracleComm extends AbstractGatherPlugin {

	private static final Log logger = LogFactory.getLog(OracleComm.class);
	private OracleUtil util;

	private String[] args;

	public OracleComm(String args) {
		this.args = args.split("[ |;]");
	}

	public OracleComm(String... args) {
		this.args = args;
	}

	public OracleComm() {

	}

	public void process(EventData event) throws Exception {
		// TODO Auto-generated method stub
		String id = event.getId();
		String[] strs = id.split(",");
		String host = strs[1];
		int port = Integer.parseInt(strs[2]);
		String sid = strs[3];
		String user = getParam("user").toString();
		String password = getParam("password").toString();
		util = new OracleUtil(host, port, sid, user, password);

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
			String f = "com/cninfo/meyes/client/plugin/impl/oracle/" + file;
			String sql = util.getSql(f);
			if(sql == null) continue;
			if (name.toLowerCase().startsWith("arr")) {
				obj = util.queryForDBList(sql);
			} else if (name.toLowerCase().startsWith("obj")) {
				obj = util.queryForDBObject(sql);
			}
			if(obj != null) doc.put(cate, obj);
		}
	}

	/**
	 * @return
	 */
	private DBObject getStatus() {
		String sql = "select version,host_name hostname,to_char(startup_time,'yyyy-mm-dd hh24:mi:ss') startup_time from v$instance";
		return util.queryForDBObject(sql);
	}

}
