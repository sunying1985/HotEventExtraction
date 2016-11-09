package com.suny.dictionary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * read file content in file set file 
 * @author Frank suny
 *
 */
public class ReadConfigUtil {
	private static Properties config = null;
	static {
		InputStream in = null;
		try {
			File config_file_path = new File("dm_hotevent.properties");
			if (config_file_path.exists()) {
				in = new FileInputStream(config_file_path);
			} else {
				in = ReadConfigUtil.class.getClassLoader().getResourceAsStream(
						"dm_hotevent.properties");
			}
		} catch (FileNotFoundException e1) {
			System.out.println("config file not found dm_hotevent.properties!");
		}
		config = new Properties();
		try {
			if(in==null){
				System.out.println("config file not found dm_hotevent.properties!");
			}else {
				config.load(in);
				in.close();				
			}
		} catch (IOException e) {
			System.out.println("No dm_hotevent.properties defined error");
		}
	}

	// 根据key读取value
	public static String getValue(String key) {
		//Properties props = new Properties();
		try {
			String value = config.getProperty(key);
			return value;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("ConfigInfoError" + e.toString());
			return null;
		}
	}

}
