package com.cninfo.meyes.gather.plugin;

import java.net.UnknownHostException;

import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

public class MongodbTest {

	public static void main(String[] args) throws UnknownHostException {
		// TODO Auto-generated method stub

		MongodbDemo md = new MongodbDemo();

		DBObject obj = new BasicDBObject();
		obj.put("common", md.common());
		obj.put("connections", md.connections());
		obj.put("cursors", md.cursors());
		obj.put("opcounters", md.opcounters());
		obj.put("opcountersRepl", md.opcountersRepl());
		obj.put("repl", md.repl());
		obj.put("mem", md.mem());
		obj.put("network", md.network());

		System.out.println(obj);

	}

}

class MongodbDemo {

	public final String host = "172.26.5.71";
	public final int port = 27017;
	public final String username = "root";
	public final String password = "123456";
	public final String dbname = "admin";

	/*
	 * 返回mongodb的基本信息
	 */
	@SuppressWarnings("deprecation")
	public DBObject common() throws UnknownHostException {

		DBObject obj = null;

		CommandResult rs = ServerStatus();

		obj = new BasicDBObject();
		obj.put("host", rs.get("host"));

		obj.put("version", rs.get("version"));
		obj.put("process", rs.get("process"));
		obj.put("pid", rs.get("pid"));
		obj.put("uptime", ((Double) rs.get("uptime")).intValue());
		obj.put("uptimeMillis", rs.get("uptimeMillis"));
		obj.put("uptimeEstimate",
				((Double) rs.get("uptimeEstimate")).intValue());
		obj.put("localTime", rs.getDate("localTime").toLocaleString());
		obj.put("ok", ((Double) rs.get("ok")).intValue());

		return obj;

	}

	/*
	 * 获取连接数信息
	 */

	public DBObject connections() throws UnknownHostException {

		DBObject obj = null;

		CommandResult rs = ServerStatus();
		obj = (DBObject) rs.get("connections");

		return obj;

	}

	/*
	 * 获取打开的游标
	 */
	public DBObject cursors() throws UnknownHostException {
		// TODO Auto-generated method stub
		DBObject obj = null;

		CommandResult rs = ServerStatus();

		obj = (DBObject) rs.get("cursors");

		return obj;
	}

	/*
	 * 获取操作的数量
	 */
	public DBObject opcounters() throws UnknownHostException {
		// TODO Auto-generated method stub
		DBObject obj = null;

		CommandResult rs = ServerStatus();

		obj = (DBObject) rs.get("opcounters");

		return obj;
	}

	/*
	 * 获取副本集的操作数量
	 */
	public DBObject opcountersRepl() throws UnknownHostException {
		// TODO Auto-generated method stub
		DBObject obj = null;

		CommandResult rs = ServerStatus();

		obj = (DBObject) rs.get("opcountersRepl");

		return obj;
	}

	/*
	 * 获取副本集的情况
	 */
	public DBObject repl() throws UnknownHostException {

		DBObject obj = null;

		CommandResult rs = ServerStatus();
		obj = (DBObject) rs.get("repl");

		return obj;

	}

	/*
	 * 获取内存信息
	 */
	public DBObject mem() throws UnknownHostException {

		DBObject obj = null;

		CommandResult rs = ServerStatus();

		obj = (DBObject) rs.get("mem");

		return obj;

	}

	/*
	 * 获取网络的信息
	 */

	public DBObject network() throws UnknownHostException {
		// TODO Auto-generated method stub
		DBObject obj = null;

		CommandResult rs = ServerStatus();

		obj = (DBObject) rs.get("network");

		return obj;
	}

	private Mongo getConnection(String host, int port)
			throws UnknownHostException {

		Mongo mongo = null;
		mongo = new Mongo(host, port);

		return mongo;

	}

	private DB getDB(Mongo mongo, String dbname, String username,
			String password) {

		DB db = null;
		if (null != mongo) {
			db = mongo.getDB(dbname);

		}
		db.authenticate(username, password.toCharArray());
		return db;

	}

	private void close(Mongo mongo) {
		if (null != mongo) {
			mongo.close();

		}

	}

	private CommandResult ServerStatus() throws UnknownHostException {
		Mongo mongo = getConnection(host, port);

		DB db = getDB(mongo, dbname, username, password);

		CommandResult commandResult = db.command("serverStatus");
		close(mongo);
		return commandResult;

	}

}