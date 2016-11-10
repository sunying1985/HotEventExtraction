package com.suny.dataobtain.tool;
import java.io.BufferedReader;
import java.io.FileOutputStream;  
import java.io.IOException;  
import java.io.InputStreamReader;  
import java.io.OutputStream;  
import java.io.OutputStreamWriter;

/**
 * 远程操作shell脚本
 */

public class JavaShellUtil {  
	//基本路径  
	private static final String basePath = "/tmp/";  
	  
	//记录Shell执行状况的日志文件的位置(绝对路径)  
	private static final String executeShellLogFile = basePath + "executeShell.log";  
	  
	//发送文件到Kondor系统的Shell的文件名(绝对路径)  
	private static final String execShellName = basePath + "HotEventShell.sh";  
	  
	public int executeShell(String sql) throws IOException {  
		int success = 0;  
		StringBuffer stringBuffer = new StringBuffer();  
		BufferedReader bufferedReader = null;  
		//格式化日期时间，记录日志时使用  
		String starttime = DataTimeTool.getDateFormat("yyyy-MM-dd HH:mm:SS");
		  
		try {  
			stringBuffer.append(starttime).append("准备执行Shell命令 ");
			/*if(shellCommands.length > 0){
				for(String command : shellCommands){
					stringBuffer.append(command).append(" \r\n");  
				}
			}*/
			//Process pid = Runtime.getRuntime().exec(shellCommands);  
			Process pid = Runtime.getRuntime().exec("nohup sh "+execShellName+" '"+sql+"' &");
			if (pid != null) {  
				stringBuffer.append("进程号：").append(pid.toString()).append("\r\n");  
				//bufferedReader用于读取Shell的输出内容
				bufferedReader = new BufferedReader(new InputStreamReader(pid.getInputStream()), 1024);  
				int exitValue = pid.waitFor();
				if (0 != exitValue) {  
		            System.out.println("执行shell异常！");
		        }  
			} else {  
				stringBuffer.append("没有pid\r\n");  
			}  
			String endtime = DataTimeTool.getDateFormat("yyyy-MM-dd HH:mm:SS");
			stringBuffer.append(endtime).append("Shell命令执行完毕\r\n执行结果为：\r\n");  
			String line = null;  
			//读取Shell的输出内容，并添加到stringBuffer中  
			while (bufferedReader != null && (line = bufferedReader.readLine()) != null) {  
				stringBuffer.append(line).append("\r\n");  
			}  
		} catch (Exception ioe) {  
			stringBuffer.append("执行Shell命令时发生异常：\r\n").append(ioe.getMessage()).append("\r\n");  
		} finally {  
			if (bufferedReader != null) {  
				OutputStreamWriter outputStreamWriter = null;  
				try {  
					bufferedReader.close();  
					//将Shell的执行情况输出到日志文件中  
					OutputStream outputStream = new FileOutputStream(executeShellLogFile);  
					outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");  
					outputStreamWriter.write(stringBuffer.toString());  
				} catch (Exception e) {  
					e.printStackTrace();  
				} finally {  
					outputStreamWriter.close();  
				}  
			}  
			success = 1;  
		}  
		return success;  
	}  
	  
}  
