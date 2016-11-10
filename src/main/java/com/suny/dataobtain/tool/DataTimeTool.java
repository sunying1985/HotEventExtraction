package com.suny.dataobtain.tool;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class DataTimeTool {
	//获得毫秒数
	public static long getTimeMillis(){
		return System.currentTimeMillis();
	}
	//获得秒数
	public static long getTimeSecond(){
		long second = (System.currentTimeMillis())/1000;
		return second;
	}
	//返回格式化后的日期
	public static String getDateFormat(String formatStr){
		Date dNow = new Date();   //当前时间
		SimpleDateFormat sdf=new SimpleDateFormat(formatStr); //设置时间格式
		String nowDate = sdf.format(dNow); //格式化当前时间
		return nowDate;
	}
	//将毫秒数转化为时间格式
	public static String formatMS2Date(long ms){
		 Date dat=new Date(ms);  
        GregorianCalendar gc = new GregorianCalendar();   
        gc.setTime(dat);  
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String sb=format.format(gc.getTime());
        return sb;
	}
	public static void main(String[] args){
		System.out.println("" + getDateFormat("yyyy-MM-dd HH:mm:ss"));
	}
}
