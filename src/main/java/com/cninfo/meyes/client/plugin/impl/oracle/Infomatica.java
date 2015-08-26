package com.cninfo.meyes.client.plugin.impl.oracle;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cninfo.meyes.client.model.EventData;
import com.cninfo.meyes.client.plugin.impl.AbstractGatherPlugin;
import com.cninfo.meyes.client.plugin.util.OracleUtil;
import com.cninfo.meyes.client.util.HttpUtil;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class Infomatica extends AbstractGatherPlugin {
	private static final Log logger = LogFactory.getLog(Infomatica.class);
	
	private static String sql ="SELECT distinct a.WORKFLOW_RUN_ID, S.SUBJ_NAME || ''.'' || A.WORKFLOW_NAME || ''.'' || B.TASK_NAME obj_name,"
			+ "TO_CHAR (A.START_TIME, ''YYYY-MM-DD hh24:mi:ss'') start_time,"
			+ "TO_CHAR (A.END_TIME, ''YYYY-MM-DD hh24:mi:ss'') end_time,"
			+ "A.RUN_ERR_CODE + B.RUN_ERR_CODE ERR_CODE,"
			+ "nvl(A.RUN_ERR_MSG,'' '') WFLW_RUN_ERR_MSG,"			
			+ "nvl(B.RUN_ERR_MSG,'' '') TASK_RUN_ERR_MSG "
			+ "FROM {0}.opb_wflow_run a, {1}.opb_task_inst_run b, {2}.opb_subject s "
			+ "WHERE A.WORKFLOW_RUN_ID = B.WORKFLOW_RUN_ID AND A.SUBJECT_ID = S.SUBJ_ID "
			+ "AND A.END_TIME > TO_DATE (''{3}'', ''YYYY-MM-DD hh24:mi:ss'') "
			//+ "AND a.END_TIME is not null "
			+ "order by END_TIME desc";
	
	private static final String PREFIX = "informatica_";
	private static final int ROLL_MINUTE = 5;
	
	private static final String KEY_RUN_TIME = "maxTime";
	
	public void process(EventData event) throws Exception {
		String id = event.getId();
		String[] strs = id.split(",");
		String host = strs[1];
		int port = Integer.parseInt(strs[2]);
		String sid = strs[3];
		String user = getParam("user").toString();
		String password = getParam("password").toString();
		OracleUtil util = new OracleUtil(host,port,sid,user,password);
		Object obj  = getParam("schema");
		logger.debug(obj);
		String[] schemas = obj.toString().split(",");
		
		for(String schema : schemas){
			DBObject info = this.getLastInfo(id, schema);
			String lastTime = getLastTime(info); 
			logger.debug("lastStartTime:"+lastTime);
			
			MessageFormat mf = new MessageFormat(sql);
			String sqlStr = mf.format(new Object[]{schema,schema,schema,lastTime});
			BasicDBList content = util.queryForDBList(sqlStr);
			
			DBObject doc = new BasicDBObject();
			event.getDoc().put(PREFIX+schema, doc);
			DBObject infoObj = new BasicDBObject();
			infoObj.put(KEY_RUN_TIME, getThisTime(content,lastTime));
			
			doc.put("info", infoObj);
			doc.put("content", content);
			doc.put("size", content.size());
		}
		
	}
	
	/**
	 * @param content
	 * @param lastTime
	 * @return
	 */
	private String getThisTime(BasicDBList content,String lastTime){
		if(content == null || content.size()==0){
			return lastTime;
		}
		DBObject row = (DBObject)content.get(0);
		return (String)row.get("end_time");
	}
	
	/**
	 * @param info
	 * @return
	 */
	private String getLastTime(DBObject info){
		String lastTime = null; 
		Calendar cal=Calendar.getInstance();
		cal.add(Calendar.MINUTE, -ROLL_MINUTE);
		Date date = cal.getTime();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(info == null || (String)info.get(KEY_RUN_TIME) == null) {
			lastTime = format.format(date);
		}else{
			lastTime = (String)info.get(KEY_RUN_TIME);
			try {
				Date dd = format.parse(lastTime);
				lastTime = format.format(date.after(dd)?date:dd);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return lastTime;
	}
	
	/**
	 * 获取上次文件信息
	 * 
	 * @param id
	 * @return
	 */
	private DBObject getLastInfo(String id, String schema) {
		HttpUtil http = new HttpUtil();
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", id);
		params.put("key", PREFIX+schema);
		DBObject obj = http.get("/query/fixed/last.do", params);
		return obj;
	}
	
	
	public static void main(String[] args) {
		System.out.println(sql);
		String sqlStr = MessageFormat.format(sql,new Object[]{"rs","rs","rs","2015-05-11 12:50:00"});
		System.out.println(sqlStr);
	}
}
