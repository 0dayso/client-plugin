package com.cninfo.meyes.client.plugin.impl.mysql;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cninfo.meyes.client.model.EventData;
import com.cninfo.meyes.client.plugin.impl.AbstractGatherPlugin;
import com.cninfo.meyes.client.plugin.util.MysqlUtil;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * @author lunianping
 *
 */
public class MysqlComm extends AbstractGatherPlugin {
	
	private static final Log logger = LogFactory.getLog(MysqlComm.class);
	private MysqlUtil util;
	
	private String[] args;
	
	public MysqlComm(String args){
		this.args = args.split("[ |;]");
	}
	
	public MysqlComm(){
	}

	/* (non-Javadoc)
	 * @see com.cninfo.meyes.client.plugin.GatherPlugin#process(com.cninfo.meyes.client.model.EventData)
	 */
	public void process(EventData event) throws Exception {
		// TODO Auto-generated method stub
		String id = event.getId();
		String[] strs = id.split(",");
		String host = strs[1];
		int port = Integer.parseInt(strs[2]);
		String user = getParam("user").toString();
		String password = getParam("password").toString();
		util = new MysqlUtil(host,port,user,password);
		
		DBObject status = this.getStatus();
		DBObject doc = event.getDoc();
		doc.putAll(status.toMap());
		
		for(String file : args){
			logger.debug("args : "+file);
			String name = file.replaceAll(".+\\\\", "");
			String cate = name.replaceFirst("^[a-zA-z]+_",	"").replaceAll("\\..+$", "");
			logger.debug("cate : "+cate);
			String f = "com/cninfo/meyes/client/plugin/impl/mysql/"+file;
			String sql = util.getSql(f);
			if(sql == null) continue;
			if(name.toLowerCase().startsWith("arr")){
				DBObject obj = util.queryForDBList(sql);
				if(obj != null) doc.put(cate, obj);
			}else if(name.toLowerCase().startsWith("obj")){
				List<Map<String,Object>> list = util.queryForList(sql);
				BasicDBList re = util.listToDBList(list);
				DBObject obj = null;
				if(list.size()>1){
					obj = changeToObject(re);
				}else{
					obj = util.queryForDBObject(sql);
				}
				if(obj != null) doc.put(cate, obj);
			}else{
				DBObject obj = util.queryForDBObject(sql);
				if(obj != null) doc.putAll(obj.toMap());
			}
			
		}
	}
	
	/**将变量转换结果List转Object
	 * @param list
	 * @return
	 */
	private DBObject changeToObject(BasicDBList list){
		DBObject obj = new BasicDBObject();
		for(Object o : list){
			DBObject row = (DBObject)o;
			String key = row.get("variable_name").toString();
			String value = row.get("value").toString();
			obj.put(key, value);
		}
		return obj;
	}
	
	
	/**
	 * @return
	 */
	private DBObject getStatus(){
		String sql = "select @@version version";
		return util.queryForDBObject(sql);
	}
}
