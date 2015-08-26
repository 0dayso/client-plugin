package com.cninfo.meyes.client.plugin.impl.server;

import java.io.File;
import java.io.FileFilter;

public class MyFileFilter implements FileFilter {
	private String match;
	
	public MyFileFilter(String match){
		this.match = match;
	}
	
	public boolean accept(File file) {
		// TODO Auto-generated method stub
		//System.out.println(file.getName()+"   "+match+"  "+file.getName().matches(match));
		return file.getName().matches(match);
	}

}
