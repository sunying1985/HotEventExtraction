package com.suny.distance;

/**
 * 主要计算标题的相似程度
 * @classDescription:最小编辑距离(Levenshtein Distance)算法实现
 * 可以使用的地方：DNA分析 　　拼字检查 　　语音辨识 　　抄袭侦测
 * @createTime: 20150722 
 * @author:     Adolf
 */
public class LVdistance{

	public static void main(String[] args) {
		//要比较的两个字符串
		String str1 = "过去的一年，面对复杂多变的国际政治经济环境和艰巨繁重的国内改革发展任务，全国各族人民在中国共产党领导下，同心同德，团结奋进，改革开放和社会主义现代化建设取得新的重大成就。国内生产总值47.2万亿元，比上年增长9.2%；公共财政收入10.37万亿元，增长24.8%；粮食产量57121万吨，再创历史新高；城镇新增就业1221万人，城镇居民人均可支配收入和农村居民人均纯收入实际增长8.4%和11.4%。我们巩固和扩大了应对国际金融危机冲击成果，实现了“十二五”时期良好开局。";
		String str2 = "第十一届全国人民代表大会第一次会议以来的五年，是我国发展进程中极不平凡的五年。我们有效应对国际金融危机的严重冲击，保持经济平稳较快发展，国内生产总值从26.6万亿元增加到51.9万亿元，跃升到世界第二位；公共财政收入从5.1万亿元增加到11.7万亿元；累计新增城镇就业5870万人，城镇居民人均可支配收入和农村居民人均纯收入年均分别增长8.8%、9.9%；粮食产量实现“九连增”；重要领域改革取得新进展，开放型经济达到新水平；创新型国家建设取得新成就，载人航天、探月工程、载人深潜、北斗卫星导航系统、超级计算机、高速铁路等实现重大突破，第一艘航母“辽宁舰”入列；成功举办北京奥运会、残奥会和上海世博会；夺取抗击汶川特大地震、玉树强烈地震、舟曲特大山洪泥石流等严重自然灾害和灾后恢复重建重大胜利。我国社会生产力和综合国力显著提高，人民生活水平和社会保障水平显著提高，国际地位和国际影响力显著提高。我们圆满完成“十一五”规划，顺利实施“十二五”规划。社会主义经济建设、政治建设、文化建设、社会建设、生态文明建设取得重大进展，谱写了中国特色社会主义事业新篇章。";
		//String str1 = "今天心情相当不好，不要打搅我！";
		//String str2 = "今天心情相当不错，不要打断他！";
		System.out.println(levenshteinDistance(str1,str2));
		
	}

	/**
	 * @createTime 20150722
	 */
	public static double levenshteinDistance(String str1,String str2) {
		//计算两个字符串的长度。
		int len1 = str1.length();
		int len2 = str2.length();
		//建立上面说的数组，比字符长度大一个空间
		int[][] dif = new int[len1 + 1][len2 + 1];
		//赋初值，步骤B。
		for (int a = 0; a <= len1; a++) {
			dif[a][0] = a;
		}
		for (int a = 0; a <= len2; a++) {
			dif[0][a] = a;
		}
		//计算两个字符是否一样，计算左上的值
		int temp;
		for (int i = 1; i <= len1; i++) {
			for (int j = 1; j <= len2; j++) {
				if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
					temp = 0;
				} else {
					temp = 1;
				}
				//取三个值中最小的
				dif[i][j] = min(dif[i - 1][j - 1] + temp, dif[i][j - 1] + 1,
						dif[i - 1][j] + 1);
			}
		}
		//System.out.print("字符串\""+str1+"\n与\n"+str2+"\n差异步骤：");
		//取数组右下角的值，同样不同位置代表不同字符串的比较
		//System.out.println(dif[len1][len2]);
		//计算相似度
		float similarity =1 - (float) dif[len1][len2] / Math.max(str1.length(), str2.length());
		//System.out.println("相似度："+similarity);	
		return similarity;
	}

	//得到最小值
	private static int min(int... is) {
		int min = Integer.MAX_VALUE;
		for (int i : is) {
			if (min > i) {
				min = i;
			}
		}
		return min;
	}

}