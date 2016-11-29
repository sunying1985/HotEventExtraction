package com.suny.fingerprints;

import com.suny.keywords.TextRankKeyword;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Vector;


/**
 * @author Adolf
 * @date   20150728
 * @note   计算两个字符串之间的相似度
 */
public class FingerPrint {

	private TextRankKeyword keyExtract = null;
	
	private static final int keyWordsNumber = 100;
	//构造函数.
	public FingerPrint() {
		keyExtract = new TextRankKeyword();
	}
	
	/**
	 * 计算文章的语义指纹
	 * @param title 标题，可为空
	 * @param text 文本
	 * @return 返回长整型的语义指纹
	 */
	private long fingerPrintText(String text) {
		long fp = 0L;

		Map<String,Float> keyWeight = keyExtract.getTermAndRankScore(text, this.keyWordsNumber);//固定20,格式:
		String keyWeightStr = this.keyWordsWeightText(keyWeight);
		if(keyWeight.equals("") == true) {
			return 0;
		}
		fp = fingerPrint_WithKeyweight(keyWeightStr);
		return fp;
	}

	private String keyWordsWeightText(Map<String,Float> keyWeight) {
		if (keyWeight == null || keyWeight.size() == 0) {
			return "";
		}
		String retValue = "";
		for (String key : keyWeight.keySet()) {
			float score = keyWeight.get(key);
			retValue += key + ":" + score + ";";
		}
		return  retValue;
	}

	private String fingerPrintWordsWeight(String text) {

		Map<String,Float> keyWeight = keyExtract.getTermAndRankScore(text, this.keyWordsNumber);//固定20,格式:
		String keyWeightStr = this.keyWordsWeightText(keyWeight);

		if(keyWeight.equals("") == true) {
			return "";
		}
		return keyWeightStr;
	}
	/**
	 * 计算文章的语义指纹
	 * @param keywords 关键词数组
	 * @param weights 对应关键词权值
	 * @return 返回长整型的语义指纹
	 */
	private long fingerPrint(Vector<String> keywords, Vector<Double> weights) {
		long fp = 0L;
		if (keywords.size() != weights.size()) {
			return 0L;
		}
		
		long[] hashValues = new long[keywords.size()];
		for (int i = 0; i < keywords.size(); i++) {
			hashValues[i] = MurmurHashs.hash64(keywords.get(i));
		}
		
		double[] wi = new double[weights.size()];
		for (int i = 0; i < wi.length; i++) {
			wi[i] = weights.get(i);
		}
		fp = SimilaryHash.hash64(hashValues, wi);
		
		return fp;
	}
	
	/**
	 * 计算文章关键词列表的语义指纹
	 * @param keyWeight 关键词串,格式为：领导传销活动:5.03;传销活动:4.18;传销:2.28;组织:2.20;
	 * @return 返回长整型的语义指纹
	 */
	private long fingerPrint_WithKeyweight(String keyWeight) {
		long fp = 0L;
		if (keyWeight == null || keyWeight.isEmpty()) {
			System.err.println("fingerPrinit is 0L, keyWeight is empty or null.");
			return 0L;
		}
		String[] keyWeights = keyWeight.split(";");
		if(keyWeights.length<1){
			System.err.println("fingerPrinit is 0L, keyWeights array length is 0.");
			return 0L;
		}
		Vector<String> keywords = new Vector<String>() ;
		Vector<Double> weights = new Vector<Double>() ;
		for (int i = 0; i < keyWeights.length; i++) {
			//暴下物;性之异:4.17、跑盘记:诚投天:10.5
			int index = keyWeights[i].lastIndexOf(":");
			if (index < 0) {
				continue;
			}
			keywords.add(keyWeights[i].substring(0, index));
			weights.add(Double.parseDouble(keyWeights[i].substring(index+1)));//进行判断
		}
		if (keywords.size() != weights.size()) {
			System.err.println("fingerPrinit is 0L, keywords.size != weights.size.");
			return 0L;
		}
		
		fp = fingerPrint(keywords, weights);
		return fp;
	}
	// same with the up founctions
	public long fingerPrintMapScore(Map<String,Float> keyWordsDict) {

		long fingerValue = 0L;
		if (keyWordsDict == null || keyWordsDict.size() == 0) {
			return fingerValue;
		}

		Vector<String> keywords = new Vector<String>() ;
		Vector<Double> weights = new Vector<Double>() ;
		for (String key : keyWordsDict.keySet()) {
			keywords.add(key);

			BigDecimal bigdec = new BigDecimal(String.valueOf(keyWordsDict.get(key)));
			double weight = bigdec.doubleValue();

			weights.add(weight);//进行判断
		}
		if (keywords.size() != weights.size()) {

			return 0L;
		}

		fingerValue = fingerPrint(keywords, weights);
		return fingerValue;
	}

	
	/**
	 * 计算文章关键词列表的语义指纹
	 * @param keys 关键词串,格式为：领导传销活动;传销活动;传销;组织;计算指纹时的权重为系统固定值.
	 * @return 返回长整型的语义指纹
	 */
	private long fingerPrint_WithKeyNoWeight(String keys) {
		long fp = 0L;
		if (keys == null || keys.isEmpty()) {
			System.err.println("fingerPrinit is 0L, keyWeight is empty or null.");
			return 0L;
		}
		String[] keyWeights = keys.split(";");
		if(keyWeights.length<1){

			System.err.println("fingerPrinit is 0L, keyWeights array length is 0.");
			return 0L;
		}
		Vector<String> keywords = new Vector<String>() ;
		Vector<Double> weights = new Vector<Double>() ;
		for (int i = 0; i < keyWeights.length; i++) {
		
			keywords.add(keyWeights[i]);
			// 所有的词语默认的权重都为5.0
			weights.add(5.0);
			
		}
		if (keywords.size() != weights.size()) {
			System.err.println("fingerPrinit is 0L, keywords.size != weights.size.");
			return 0L;
		}
		
		fp = fingerPrint(keywords, weights);
		return fp;
	}
	
	/**
	 * 计算两篇文章的相似度值
	 * @param text1,text2  两篇文章的关键词数组 
	 * @return 返回两篇文章的相似度值
	 */
	public double similarityBetween2Text(String text1, String text2) {
		double sim = 0.0;
		// 可以获取文章的关键词，用向量空间模型等计算两篇文章的相似度
		//使用keyWordsNumber 控制比较的词条数目
		String keyWeightOne = this.fingerPrintWordsWeight(text1);
		String keyWeightTow = this.fingerPrintWordsWeight(text2);
		
		sim = this.similarityBetween2KeyWeight(keyWeightOne,keyWeightTow);
		return sim;
	}

	
	/**
	 * 2015.06.01
	 * 计算两个语义指纹的相似度值
	 * @param fp1,fp2  语义指纹 
	 * @return 返回两个语义指纹的相似度值
	 */
	private double similarityBetween2FingerPrint(long fp1,long fp2){
	
		int distance = 0;
		double sim = 0;
		distance = SimilaryHash.hammingDistance64(fp1, fp2);

		sim = 1 - ((double)distance)/(double)64 ;
		sim = (double)Math.round(sim*1000)/1000 ;
		
		return sim;
	}
	
	/**
	 * 计算两个文章主题词串的相似度值
	 * @param keyWeight1,keyWeight2 主题词串,如 keyWeight1="key1:weight1;key2:weight2;key3:weight3;key4:weight4"
	 * @return 返回两篇文章的相似度值
	 */
	private double similarityBetween2KeyWeight(String keyWeight1,String keyWeight2){

		if (keyWeight1 == null || keyWeight1.isEmpty()) {
			return 0.0;
		}
		if (keyWeight2 == null || keyWeight2.isEmpty()) {
			return 0.0;
		}
		
		long fp1 = fingerPrint_WithKeyweight(keyWeight1);
		long fp2 = fingerPrint_WithKeyweight(keyWeight2);
		System.out.println(fp1);
		System.out.println(fp2);
		
		return similarityBetween2FingerPrint(fp1,fp2);
	}

	private double similarityBetKeysDict(Map<String, Float> keyWeight1,Map<String, Float> keyWeight2){
		int distance = 0;
		double sim = 0;
		if (keyWeight1 == null || keyWeight1.size() == 0) {
			return 0.0;
		}
		if (keyWeight2 == null || keyWeight2.size() == 0) {
			return 0.0;
		}

		long fp1 = fingerPrintMapScore(keyWeight1);
		long fp2 = fingerPrintMapScore(keyWeight2);
		distance = SimilaryHash.hammingDistance64(fp1, fp2);

		sim = 1 - ((double)distance)/(double)64 ;
		sim = (double)Math.round(sim*1000)/1000 ;

		return sim;
	}
	/**
	 * loading text from file
	 * only used for test the performance 
	 */
    private String  loadingTextFromFile(String infile) {
		
    	if (infile.isEmpty() == true) {
    		return "";
    	}
		String allContext = "";
		try {
			FileInputStream fis = new FileInputStream(infile);
			InputStreamReader isr = new InputStreamReader(fis,"UTF-8");
			BufferedReader br = new BufferedReader(isr);
					
			String textLine = "";
			while (( textLine = br.readLine()) != null) {
				allContext += textLine;
			}
			br.close();
			isr.close();
			br.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return allContext;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FingerPrint fp = new FingerPrint();
		String  title = "朱文晖：周本顺这次闪电落马，我自己来解读的话，可能至少破解了，就是我们对于北京反腐工作的三大疑虑，第一个疑虑是什么疑虑呢？就是这个一看周本顺的简历，你发现他长期是在公安系统工作，后来又到北京政法委系统工作了相当长时间，他是用中央政法委秘书长的身份到了河北接任的省委书记，我们也知道河北他是北京的周边，尤其比如说马上开的北戴河会议，那么这个保安工作河北有非常重大的压力，所以政法委和河北他有这种天然连接的关系，为中央提供警卫工作，为整个北京稳定提供帮助提供保障，所以他去那个地方看来是非常正常的。但是我们也知道，就是他在中央政法委的工作，那自然就和周永康是不是有关联，因为他当秘书长的时候，周永康正好是政法委书记，我们也确实看到，周本顺他原来是湖南省公安厅厅长一路提拔到政法委书记，然后到了中央北京去工作的，所以就是连带这个事件，你其实看到在这个十八大以后，这个中央政法委书记，就是不再由政治局常委来担任，而且就是今天特别网上还有很多地方的评论谈到了，就是政法委书记不再兼任省的公安厅厅长，这里边有一个非常明确的一种关系，但是这里边也就反映出来，就是周永康这个事件大家本来以为他就是完结了，因为他已经审判完结。那么另外就是令计划和这个事件有一定的关联，所以今天海外媒体也有报道，说当时车祸处理的过程当中，是不是他也牵涉到里头，因为令计划事情前一段时间也是移交司法进行处理了，大家本来以为这个事情就划一个句号，但现在看来周本顺找来找去，连接起来和他还是有一个关联的。";
		String content = fp.loadingTextFromFile("E:\\my_work\\KeyWordsExtract\\test\\corpus.txt");
		

		double simval = fp.similarityBetween2Text(content, title);
		System.out.println(simval);
	}
}
