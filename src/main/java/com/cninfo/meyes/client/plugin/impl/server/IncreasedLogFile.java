package com.cninfo.meyes.client.plugin.impl.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cninfo.meyes.client.util.HttpUtil;
import com.cninfo.meyes.client.model.EventData;
import com.cninfo.meyes.client.plugin.impl.AbstractFileGatherPlugin;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * @author lunianping
 * 
 *         用于增量采集日志信息
 * 
 *         适用于RHEL或Windows操作系统
 * 
 */
public class IncreasedLogFile extends AbstractFileGatherPlugin {

	private static final Log logger = LogFactory.getLog(IncreasedLogFile.class);
	
	/* (non-Javadoc)
	 * @see com.cninfo.meyes.client.plugin.GatherPlugin#process(com.cninfo.meyes.client.model.EventData)
	 */
	public void process(EventData event) throws Exception {
		File file = getFile();
		String name = getName();
		
		if(file == null || !file.exists() || !file.isFile() || !file.canRead()){
			logger.error("File '"+getFilePath() + "' is not found");
			throw new FileNotFoundException("FileNotFoundException:File '"+getFilePath()+ "' is not found");
		}
		
		logger.debug("Name: "+ name +"     file:" + file.getPath());
		
		DBObject obj = new BasicDBObject();
		event.getDoc().put(KEY_PREFIX + name, obj);
		RandomAccessFile raf = null;
		try {
			DBObject thisInfo = fileInfo(file);
			obj.put("info", thisInfo);
			BasicDBList content = new BasicDBList();
			obj.put("content", content);
			//int rows = 0;
			//obj.put("size", rows);
			
			DBObject lastInfo = getLastFileInfo(event.getId(), KEY_PREFIX + name);
			logger.debug("Last file info:" + lastInfo.toString()
					+ "   This file info :" + thisInfo.toString());

			//如果是首次，则取当前的信息
			if (lastInfo == null || lastInfo.keySet().size() != 4)
				lastInfo = thisInfo;
			
			// 如果没有任何变化
			int re = isChanged(thisInfo, lastInfo);
			//System.out.println("Change:" + re);
			if ( re == 0) {
				return;
			}
			obj.put("last", lastInfo);
			
			int lastRows = Integer.parseInt(lastInfo.get("rows").toString());
			long lastLen = Long.parseLong(lastInfo.get("length").toString());
			
			int thisRows = Integer.parseInt(thisInfo.get("rows").toString());
			long thisLen = Long.parseLong(thisInfo.get("length").toString());
			
			long beginRow = 0;
			long beginLen = 0;
			
			if(re == 4){//如果文件名发生了变化
				lastLen = 0;
				lastRows = 0;
			}			
			beginRow = Math.max(lastRows, thisRows - MAX_ROW);
			beginLen = Math.max(lastLen, thisLen - MAX_LENGTH);
			
			logger.debug("thisLength:"+ thisLen +"   lastLen:" + lastLen+"   beginLen:"+beginLen+" beginRow:"+beginRow);
			
			raf = new RandomAccessFile(file, "r");
			raf.seek(lastLen);
			String line = null;
			int r = lastRows;
			
			while ((line = raf.readLine()) != null) {
				r++;
				if(raf.getFilePointer() >= beginLen){
					DBObject row = new BasicDBObject();
					row.put("id", r);
					row.put("line", new String(line.getBytes("ISO-8859-1"),"UTF-8"));
					content.add(row);
				}
			}
			obj.put("size", content.size());
			//System.out.println("size:"+content.size());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (raf != null) {
				try {
					raf.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 获取上次文件信息
	 * 
	 * @param id
	 * @return
	 */
	private DBObject getLastFileInfo(String id, String name) {
		HttpUtil http = new HttpUtil();
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", id);
		params.put("key", name);

		DBObject obj = http.get("/query/fixed/last.do", params);
		return obj;
	}

	/**
	 * 文件是否发生改变
	 * @param lastInfo
	 * @param thisInfo
	 * @return  0 代表未改变，1，代表只行数发生变化
	 */
	private int isChanged(DBObject lastInfo, DBObject thisInfo) {
		int re = 0;
		int lastRows = Integer.parseInt(lastInfo.get("rows").toString());
		String lastName = lastInfo.get("name").toString().trim();
		String lastTime = lastInfo.get("modifiy").toString();
		long lastLen = Long.parseLong(lastInfo.get("length").toString());

		int thisRows = Integer.parseInt(thisInfo.get("rows").toString());
		String thisName = thisInfo.get("name").toString().trim();
		String thisTime = thisInfo.get("modifiy").toString();
		long thisLen = Long.parseLong(thisInfo.get("length").toString());
		
		if (!lastName.equals(thisName)){
			re = 4;
		}else if (lastRows < thisRows){
			re = 1;
		}else if( !lastTime.equals(thisTime)){
			re = 2;
		}else if(lastLen < thisLen) {
			re = 3;
		}

		return re;
	}
	
	public static void main(String[] args){
		IncreasedLogFile s = new IncreasedLogFile();
		String id = "Server,172.31.8.248";
		String name = "test";
		String path = "J:/20150522.txt";
		
		
		//File file = new File(path);
		//int row = s.getRow(file,17);
		//System.out.println(row);
		//System.out.println(s.rowNum(file));
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		params.put("name", name);
		params.put("path", path);
		
		s.before(params);
		
		EventData event = new EventData();
		event.setId(id);
		try {
			s.process(event);
			System.out.println(event.getDoc());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
