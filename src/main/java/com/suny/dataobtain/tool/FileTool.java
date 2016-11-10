package com.suny.dataobtain.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 从文件中加载id
 */

public class FileTool {
	
	/**
	 * 获取给定路径下的所有文件名称
	 * @param folder	存放文件的文件夹路径
	 * @return
	 */
	public static List<String> getFileNames(String folder){
		File file=new File(folder);
		File[] tempList = file.listFiles();
		System.out.println("该目录下文件个数：" + tempList.length);
		List<String> filenames = new ArrayList<String>();
		for (int n=0;n<tempList.length;n++) {
			if (tempList[n].isFile()) {
				File newfile = tempList[n];
				String fileName = newfile.getAbsolutePath();
				//System.out.println("fileName:" + fileName);
				filenames.add(fileName);
			}
		}
		return filenames;
	}
	
	/**
	 * 读取文件中的所有ID,
	 * @param filePath		含有文章ID的文件
	 * @param titleTag    0表示没有表头，1表示有文件表头,默认值为0
	 * @return 返回ID列表
	 */
	public static List<Long> readFileByFilePath(String filePath, int titleTag) {
		List<Long> list = new ArrayList<Long>();
		
        File file = new File(filePath);
        BufferedReader reader = null;
        try {
            System.out.println("文件路径:" + filePath);
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            // 一次读入一行，直到读入null为文件结束
            int i = 1;  // 该标记主要处理是否含有表头的情况
            while ((tempString = reader.readLine()) != null) {
            	if (i > titleTag) {
            		if(!tempString.equals("null") && !tempString.equals("")){
                		long l = Long.parseLong(tempString);
            			list.add(l);
                	}
            	}
            	i++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return list;
    }

	public static Set<String> readFileByFilePath2(String filePath) {
		Set<String> list = new HashSet<String>();
		
        File file = new File(filePath);
        BufferedReader reader = null;
        try {
            System.out.println("文件路径:" + filePath);
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
    			list.add("d:\\article\\" + tempString);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return list;
    }
	
	public static void main(String [] args) {
		
		FileTool pool = new FileTool();
		pool.getFileNames("E:\\IntelliIDEA\\Spider\\WebMagic\\Webmagic\\lib");
	}
}
