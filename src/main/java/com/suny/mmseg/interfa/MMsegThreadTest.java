package com.suny.mmseg.interfa;


public class MMsegThreadTest {
	public static void main(String[] args){

		  String content = "最近对长城汽车的车很关注，编程是数据科学的重要组成部分。在所有方面中，一般认为一个理解编程逻辑、循环、功能的大脑更有可能成为一个成功的数据科学家。那么，一个从来没有在学校或学院里学过编程项目的人呢？";
		  String title = "测试程序";
			
		  long starttime = System.currentTimeMillis();
		  
		  for(int i = 0; i < 10000; i++){
			  MMsegThread sts = new MMsegThread(title,content);
			  sts.start();
		  }
		  System.out.println(":::::::usetime="+(System.currentTimeMillis()-starttime));
	}
}
