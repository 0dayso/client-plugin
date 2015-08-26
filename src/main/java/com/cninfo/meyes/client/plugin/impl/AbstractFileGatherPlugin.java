package com.cninfo.meyes.client.plugin.impl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cninfo.meyes.client.model.EventData;
import com.cninfo.meyes.client.plugin.GatherPlugin;
import com.cninfo.meyes.client.plugin.impl.server.MyFileFilter;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * @author lunianping
 *
 */
public abstract class AbstractFileGatherPlugin implements GatherPlugin {

	protected final static String KEY_PREFIX = "log_";

	protected static final long MAX_ROW = 300;

	protected static final long MAX_LENGTH = 102400;
	/**
	 * 
	 */
	protected Map<String, Object> params;

	public void before(Map<String, Object> params) {
		this.params = params;
	}

	public void after() {

	}

	public void failed(EventData event, Map<String, Object> p) {
		event.getDoc().put("status", p.get("message"));
	}

	/**
	 * 
	 * 获取文件的信息，改方法考虑到了日志轮转的情况，但是此处没有用到
	 */
	protected File getFile() {
		String path = getFilePath();
		File file = new File(path);
		File parent = file.getParentFile();
		if (!parent.isDirectory()) return file;

		String fileName = file.getName();
		MyFileFilter filter = new MyFileFilter(fileName);

		// 将父目录下满足条件的文件加入到list数组中
		File[] list = parent.listFiles(filter);
		File re = null;

		// 将数值中的元素加入到list中，排序之后，取出最后一个元素
		List<String> fileList = new ArrayList<String>();
		// System.out.println("==== "+path+" ====");
		for (File f : list) {
			fileList.add(f.getPath());
			re = f;
		}
		Collections.sort(fileList);
		/*
		 * for(String f : fileList){ System.out.println(f); }
		 */
		re = fileList.size() > 0 ? new File(fileList.get(fileList.size() - 1))
				: re;
		return re;
	}

	// 从params中获取文件的路径
	protected String getFilePath() {
		return this.params.get("path") == null ? "" : this.params.get("path")
				.toString().trim();
	}

	// 从params中获取文件名
	protected String getName() {
		return this.params.get("name").toString().trim();
	}

	protected int getFileLastTimeRows() {
		return Integer.parseInt(this.params.get("row").toString());
	}

	/**
	 * 获取文件基本信息
	 * 
	 * @param fileName
	 * @return
	 */
	protected DBObject fileInfo(File file) {
		// File file = new File(fileName);
		// 最后修改时间
		long lastModified = file.lastModified();
		Date date = new Date(lastModified);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String lastModifiedStr = df.format(date);

		// 文件长度
		long fileLength = file.length();
		// 文件行数
		int rowNum = rowNum(file);

		DBObject obj = new BasicDBObject();
		obj.put("name", file.getPath());
		obj.put("length", fileLength);
		obj.put("rows", rowNum);
		obj.put("modifiy", lastModifiedStr);
		return obj;
	}

	/**
	 * 获取文件行数
	 * 
	 * @param file
	 * @return
	 */
	protected int rowNum(File file) {
		int rowNum = 0;
		long fileLength = file.length();
		LineNumberReader rf = null;
		try {
			rf = new LineNumberReader(new FileReader(file));
			if (rf != null) {
				// 跳过文件的当前长度，获取当前的行数
				rf.skip(fileLength);
				rowNum = rf.getLineNumber();
			}
		}
		catch (IOException e) {
		}
		finally {
			if (rf != null) {
				try {
					rf.close();
				}
				catch (IOException ee) {
				}
			}
		}
		return rowNum;
	}
	/*
	 * protected int getRow(File file,long len) { int rowNum = 0;
	 * LineNumberReader rf = null; try { rf = new LineNumberReader(new
	 * FileReader(file)); if (rf != null) { rf.skip(len); //rf.readLine();
	 * rowNum = rf.getLineNumber(); } } catch (IOException e) { } finally { if
	 * (rf != null) { try { rf.close(); } catch (IOException ee) { } } } return
	 * rowNum; }
	 */
}
