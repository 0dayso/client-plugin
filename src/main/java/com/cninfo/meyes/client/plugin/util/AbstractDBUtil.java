package com.cninfo.meyes.client.plugin.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * @author chenyusong
 *
 */
public class AbstractDBUtil {
	
	private static final Log logger = LogFactory.getLog(AbstractDBUtil.class);
	
	private DataSource ds;
	
	protected void setDataSource(DataSource ds){
		this.ds = ds;
	}
	
	protected DataSource getDataSource(){
		return ds;
	}
	
	/**
	 * @param sql
	 * @return
	 */
	public BasicDBList queryForDBList(String sql){
		List<Map<String,Object>> rs = queryForList(sql);
		return listToDBList(rs);
	}
	
	/**
	 * @param sql
	 * @return
	 */
	public List<Map<String,Object>> queryForList(String sql){
		JdbcTemplate template = new JdbcTemplate(ds);
		List<Map<String,Object>> rs = template.queryForList(sql);
		return rs;
	}
	
	/**
	 * @param sql
	 * @return
	 */
	public Map<String,Object> queryForOne(String sql){
		JdbcTemplate template = new JdbcTemplate(ds);
		Map<String,Object> rs = template.queryForMap(sql);
		return rs;
	}
	
	/**
	 * @param sql
	 * @return
	 */
	public DBObject queryForDBObject(String sql){
		Map<String,Object> map = queryForOne(sql);
		DBObject obj = new BasicDBObject();
		for(String key : map.keySet()){
			obj.put(key.toLowerCase(), map.get(key));
		}
		return obj;
	}
	
	public BasicDBList listToDBList(List<Map<String,Object>> re){
		BasicDBList list = new BasicDBList();
		for(Map<String,Object> row : re){
			DBObject r = new BasicDBObject();
			for(String key : row.keySet()){
				r.put(key.toLowerCase(), row.get(key));
			}
			list.add(r);
		}
		return list;
	}
	
	/**
	 * @param file
	 * @return
	 */
	public String getSql(String file){
		StringBuffer buf = new StringBuffer();
		InputStream in = null;
		
		try {
			in = this.getClass().getClassLoader().getResourceAsStream(file);
			if(in == null){
				logger.error("SQL file '"+file+"' is not exists");
				return null;
			}
			BufferedReader read = new BufferedReader(new InputStreamReader(in));
			
			String line = null;
			while((line = read.readLine())!= null){
				buf.append(line.replaceAll("^\\s+", "")).append(" ");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(in != null)
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return buf.toString();
	}
	
	
	
	
}
