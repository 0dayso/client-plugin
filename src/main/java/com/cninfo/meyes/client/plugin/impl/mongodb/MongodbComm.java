package com.cninfo.meyes.client.plugin.impl.mongodb;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.Date;


import com.cninfo.meyes.client.model.EventData;
import com.cninfo.meyes.client.plugin.impl.AbstractGatherPlugin;
import com.cninfo.meyes.client.plugin.util.MongodbUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * <描述>
 * 
 * @author chenyusong Create Date： 2015年6月24日
 * @version 1.0.0
 * 
 */
public class MongodbComm extends AbstractGatherPlugin {

//	private static final Log logger = LogFactory.getLog(MongodbComm.class);
	private MongodbUtil util;

	public MongodbComm() {
	}

	public void process(EventData event) throws Exception {
		// TODO Auto-generated method stub

		String id = event.getId();
		String[] strs = id.split(",");
		String host = strs[1];
		int port = Integer.parseInt(strs[2]);
		String user = getParam("user").toString();
		String password = getParam("password").toString();
		util = new MongodbUtil(host, port, user, password);

		DBObject dbObject = util.command("admin", "serverStatus");
		event.getDoc().put("common", common(dbObject));
		event.getDoc().put("connections", connections(dbObject));
		event.getDoc().put("cursors", cursors(dbObject));
		event.getDoc().put("opcounters", opcounters(dbObject));
		event.getDoc().put("opcountersRepl", opcountersRepl(dbObject));
		event.getDoc().put("repl", repl(dbObject));
		event.getDoc().put("mem", mem(dbObject));
		event.getDoc().put("network", network(dbObject));

	}

	/*
	 * 返回mongodb的基本信息
	 */
	@SuppressWarnings("deprecation")
	public DBObject common(DBObject dbObject) throws UnknownHostException,
			ParseException {

		DBObject obj = null;

		obj = new BasicDBObject();
		obj.put("host", dbObject.get("host"));

		obj.put("version", dbObject.get("version"));
		obj.put("process", dbObject.get("process"));
		obj.put("pid", dbObject.get("pid"));
		obj.put("uptime", ((Double) dbObject.get("uptime")).intValue());
		obj.put("uptimeMillis", dbObject.get("uptimeMillis"));
		obj.put("uptimeEstimate",
				((Double) dbObject.get("uptimeEstimate")).intValue());

		obj.put("localTime",
				((Date) dbObject.get("localTime")).toLocaleString());
		obj.put("ok", ((Double) dbObject.get("ok")).intValue());

		return obj;

	}

	/*
	 * 获取连接数信息
	 */

	public DBObject connections(DBObject dbObject) throws UnknownHostException {

		DBObject obj = null;

		obj = (DBObject) dbObject.get("connections");

		return obj;

	}

	/*
	 * 获取打开的游标
	 */
	public DBObject cursors(DBObject dbObject) throws UnknownHostException {
		// TODO Auto-generated method stub
		DBObject obj = null;

		obj = (DBObject) dbObject.get("cursors");

		return obj;
	}

	/*
	 * 获取操作的数量
	 */
	public DBObject opcounters(DBObject dbObject) throws UnknownHostException {
		// TODO Auto-generated method stub
		DBObject obj = null;

		obj = (DBObject) dbObject.get("opcounters");

		return obj;
	}

	/*
	 * 获取副本集的操作数量
	 */
	public DBObject opcountersRepl(DBObject dbObject)
			throws UnknownHostException {
		// TODO Auto-generated method stub
		DBObject obj = null;

		obj = (DBObject) dbObject.get("opcountersRepl");

		return obj;
	}

	/*
	 * 获取副本集的情况
	 */
	public DBObject repl(DBObject dbObject) throws UnknownHostException {

		DBObject obj = null;

		obj = (DBObject) dbObject.get("repl");

		return obj;

	}

	/*
	 * 获取内存信息
	 */
	public DBObject mem(DBObject dbObject) throws UnknownHostException {

		DBObject obj = null;

		obj = (DBObject) dbObject.get("mem");

		return obj;

	}

	/*
	 * 获取网络的信息
	 */

	public DBObject network(DBObject dbObject) throws UnknownHostException {
		// TODO Auto-generated method stub
		DBObject obj = null;

		obj = (DBObject) dbObject.get("network");

		return obj;
	}

}
