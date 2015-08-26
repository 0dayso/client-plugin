package com.cninfo.meyes.gather.plugin;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

public class TestFile {
	public static void main(String[] args) {
		String name = "J:/20150522.txt";		
		File file = new File(name);
		
		LineNumberReader rf = null;
		try {
			rf = new LineNumberReader(new FileReader(file));
			String s = null;
			int i=0;
			while ((s = rf.readLine())!= null && i++ <10) {  
				System.out.println("当前行号为:"  
				                            + rf.getLineNumber());  
				                    rf.setLineNumber(20);  
				                    System.out.println("更改后行号为:"  
				                            + rf.getLineNumber());  
				                    System.out.println(s);  
				            }
		} catch (IOException e) {
		} finally {
			if (rf != null) {
				try {
					rf.close();
				} catch (IOException ee) {
				}
			}
		}
	}
}
