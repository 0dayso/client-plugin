package com.cninfo.meyes.client.plugin.util;

import java.net.UnknownHostException;
import java.util.List;

import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

/**
 * <描述>
 * 
 * @author chenyusong Create Date： 2015年6月24日
 * @version 1.0.0
 * 
 */
public class MongodbUtil {

	private Mongo mongo;
	private String user;
	private String password;

	public MongodbUtil(String host, int port, String user, String password) {
		try {
			mongo = new Mongo(host, port);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.user = user;
		this.password = password;
	}

	public List<DBObject> query(String dbName, String collection, DBObject find) {
		DB db = mongo.getDB(dbName);
		if (user != null && password != null)
			db.authenticate(user, password.toCharArray());
		DBCollection coll = db.getCollection(collection);
		DBCursor cursor = coll.find(find);

		List<DBObject> re = cursor.toArray();
		if (cursor != null) {
			cursor.close();
		}
		db.requestDone();
		close();

		return re;
	}

	public DBObject queryOne(String dbName, String collection, DBObject find) {
		DB db = mongo.getDB(dbName);
		if (user != null && password != null)
			db.authenticate(user, password.toCharArray());
		DBCollection coll = db.getCollection(collection);
		DBObject re = coll.findOne(find);
		db.requestDone();
		close();

		return re;
	}

	public DBObject command(String dbName, String find) {
		DB db = mongo.getDB(dbName);
		if (user != null && password != null)
			db.authenticate(user, password.toCharArray());
		CommandResult coll = db.command(find);
		DBObject re = coll;
		db.requestDone();
		close();

		return re;
	}

	public void close() {
		if (null != mongo) {
			mongo.close();

		}

	}

}
