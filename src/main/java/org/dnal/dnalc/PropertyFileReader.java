package org.dnal.dnalc;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class PropertyFileReader
{
	private String path;
	
	public PropertyFileReader(String path) {
		this.path = path;
	}
	
	public Properties read() {
		 Properties props = new Properties();
		 try
		 {
			 InputStream iStream = new FileInputStream(new File(path));
		     props.load(iStream);
		 }
		 catch (Exception e) {
		     e.printStackTrace();
		 }
		 return props;		
	}
	
}