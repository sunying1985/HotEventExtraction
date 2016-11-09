package com.suny.mmseg.interfa;


public class MMsegThread extends Thread{
	
	private String text;
	private String title;
	
	public MMsegThread(String title,String content){
		this.title = title;
		this.text = content;
	}
	public void run(){
		MMsegInterface entityReg = MMsegInterface.getInstance();
		
		String retStr = entityReg.textWordSegStr(title+text);
			
		// 获取相应的属性结果
		//System.out.println("#######分析结果如下所示########");
		System.out.println(retStr);
	}
}
