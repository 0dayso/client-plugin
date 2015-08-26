package com.cninfo.meyes.client.plugin.impl.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cninfo.meyes.client.model.EventData;
import com.cninfo.meyes.client.plugin.impl.AbstractFileGatherPlugin;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * @author lunianping
 * 
 *         用于采集Linux服务器的Volumn信息
 * 
 *         适用于RHEL及其它一般Linux操作系统
 * 
 */
public class FullLogFile extends AbstractFileGatherPlugin {

	private static final Log logger = LogFactory.getLog(FullLogFile.class);
	
	public void process(EventData event) throws Exception {
		File file = getFile();
		
		if(file == null || !file.exists() || !file.isFile() || !file.canRead()){
			logger.error("File '"+getFilePath() + "' is not found");
			throw new FileNotFoundException("FileNotFoundException:File '"+getFilePath()+ "' is not found");
		}

		String name = getName();

		DBObject obj = new BasicDBObject();
		event.getDoc().put(KEY_PREFIX + name, obj);

		obj.put("info", fileInfo(file));
		BasicDBList content = new BasicDBList(); 
		BufferedReader read = null;
		try {
			read = new BufferedReader(new FileReader(file));
			String line = null;
			int r = 0;
			while ((line = read.readLine()) != null) {
				DBObject row = new BasicDBObject();
				row.put("id", ++r);
				row.put("line", line);
				content.add(row);
			}
			obj.put("content", content);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (read != null) {
				try {
					read.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
