package com.cninfo.meyes.gather.plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TestSql {
	
	public static void main(String[] args) {
		
	}
	
	public String getSql(String file){
		StringBuffer buf = new StringBuffer();
		InputStream in = null;
		
		try {
			in = this.getClass().getClassLoader().getResourceAsStream(file);
			BufferedReader read = new BufferedReader(new InputStreamReader(in));
			
			String line = null;
			while((line = read.readLine())!= null){
				buf.append(line).append(" ");
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
