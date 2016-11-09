package com.suny.mmseg.interfa;


public class MmsegInterfaceTest {

	public static void main(String[] args) {
		
		  String filepath = "D:/newsFile/";
		  //List<String> hs = st.readTexts(filepath);
		  String content = "2016年1月17日中华全国总工会十六届四次执委会，选举农民工巨晓林为中华全国总工会副主席，他是我国第一位普通农民工当选全总副主席。巨晓林，男，汉族，中共党员，1962年9月出生，高中文化，家住陕西省岐山县祝家庄镇杜城村谢家坡组，1987年3月成为中国中铁(601390,股吧)电气化局一公司的农民工。巨晓林参加工作23年，先后参加了北同蒲线、鹰厦线、大秦线、京郑线、哈大线、迁曹线等十几条国家重点电气化铁路工程的施工。他创新施工方法43项，创造经济效益600多万元；他主编的《接触网施工经验和方法》，被配发给数千名接触网工作为工具书。北京市总工会授予他“知识型职工先进个人”，北京市政府授予他“北京市劳动模范”，中华全国总工会授予他“全国五一劳动奖章”。2015年4月，他被评为全国劳动模范和先进工作者。";
		  String title = "测试程序";
		  long starttime = System.currentTimeMillis();
		  for(int i=0;i<10;i++){
			  MMsegThread sts = new MMsegThread(title,content);
			  sts.start();		
		  }
		  System.out.println(":::::::usetime="+(System.currentTimeMillis()-starttime));
	}
}
