package com.suny.tfidf;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

import com.suny.dictionary.EncryptDictionary;
import com.suny.dictionary.SystemParas;
import com.suny.document.item.IndexVector;
import com.suny.document.item.ItemFreq;
import sort.InvertedItemValue;

/***
 * @author  Adolf Frank
 * @note    words tf_idf  and filter the stop words
 */
public class WordsInformWeights {

	// 异步形式
	private  static WordsInformWeights  instance = null;

	public synchronized static WordsInformWeights getInstance(){
		if(instance == null){
			instance = new WordsInformWeights();

		}
		return instance;
	}

	/**
	 * off line words idf dictionary
	 */
	private static Map<String, Double> words_idf_vocab = null;
	/**
	 * stop words
	 */
	public static Set<String> stopWordsDict = null;
	
	private WordsInformWeights () {
		this.words_idf_vocab = new HashMap<String,Double>();
		this.stopWordsDict = new HashSet<String>();

		try {
			this.loadStopWordsDictionary();
			this.loadIDFVocab();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String toUtf8(String str) throws UnsupportedEncodingException { 
        return new String(str.getBytes("UTF-8"),"UTF-8");
   }
	/**
	 * loading words idf words information dictionary
	 * @return  true if success otherwise is false
	 * @throws IOException
	 */
	public boolean loadIDFVocab() throws IOException {
			
		String idfDictPath = SystemParas.idf_words_dict;
		System.out.println(idfDictPath + "error");
		if (idfDictPath.isEmpty()) {
			System.err.println("ConfigInfoError" + "logging words idf information dictionary is error!");
			return false;
		}
		
		EncryptDictionary ed = new EncryptDictionary();
		int totalDocument = 0;
		//int count = 0;
		// only for debug
        /*FileOutputStream fos = new FileOutputStream("E:\\my_work\\SentimentAnalysisCarField\\dict\\tf_idf.txt");
		OutputStreamWriter osw = new OutputStreamWriter(fos,"UTF-8");
		BufferedWriter bw = new BufferedWriter(osw);
		*/
		
		if (true == ed.decryptOrderWordFile(idfDictPath)) {
			//System.out.println(ed.orderArray.size());
			for(int i = 0; i < ed.orderArray.size(); i++) {
					
				String textLine = this.toUtf8(ed.orderArray.get(i).toString());
				
				if (true == textLine.startsWith("#")) {
					
					int pos = textLine.indexOf("\t");
					if (pos != -1) {
						totalDocument = Integer.parseInt(textLine.substring(pos + 1));	
					}
					else {
						System.err.println("split tag is error, the document total number is ZEARO!");
					}
				}
				else {
					String word = "";
					//count++;
					//System.out.println(count);
					//bw.write(textLine + "\n");
					//System.out.println(textLine);
					int pos = textLine.indexOf("\t");
					if (pos != -1) {
						word = textLine.substring(0,pos); 
						textLine = textLine.substring(pos + 1);
						if (textLine.indexOf("\t") != -1) {
							System.out.println(word + "\t" + textLine);
						}
						else {
							if (word.isEmpty() == false && textLine.isEmpty() == false && 
								textLine.equals("") == false) {
								int freq = Integer.parseInt(textLine.trim());
								
								 //IDF = log(语料库中的文档总数/(包含该词语的文档数目+1))
								 
								if (freq > 0) {
									double idfValue = Math.log(totalDocument/(freq + 1));
									this.words_idf_vocab.put(word, idfValue);
								}
							}
						}
					}
				}
			}		
		}
		

		if (this.words_idf_vocab.size() == 0) {
			System.err.println("ConfigInfoError" + "logging words idf information dictionary is error!");
			return false;
		}
		//System.out.println(this.words_idf_vocab.size());
		return true;
	}

	/**
	 * loading the stop words dictionary
	 * @return  true if success otherwise is false
	 * @throws IOException
	 */
	private boolean loadStopWordsDictionary() throws IOException  {

		String stopwordDictPath = SystemParas.stop_word_dict;
		if (stopwordDictPath.isEmpty()) {
			System.err.println("ConfigInfoError" + "logging stop words dictionary is error!");
			return false;
		}

		EncryptDictionary ed = new EncryptDictionary();
		if (true == ed.decryptWordFile(stopwordDictPath)) {
			//System.out.println(ed.wordVocab.size());
			Iterator<String> iter = ed.wordVocab.iterator();
			while (iter.hasNext()) {

				String textLine = iter.next().toString();

				if (false == textLine.startsWith("#")) {
					String word = "";
					int pos = textLine.indexOf("\t");
					if (pos != -1) {
						word = textLine.substring(0,pos);
						int fre = Integer.parseInt(textLine.substring( pos + 1));
						if (fre > 400) {
							this.stopWordsDict.add(word);
						}
					}
					else {
						word = textLine;
						this.stopWordsDict.add(word);
					}
				}
			}
		}

		if (this.stopWordsDict.size() == 0) {
			System.err.println("ConfigInfoError" + "logging stop words dictionary SIZE is error!");
			return false;
		}

		return true;
	}

	/**
	 * 根据输入的词语和词频计算 每一个词语的信息度权值
	 * @param wordsArr   词语和其对应的频率
	 * @return
	 */
	public Map<String, Double> calculationTFIDF(ItemFreq wordsArr) {
		
		if (wordsArr.itemFre.size() == 0) {
			return null;
		}
		Map<String, Double>  retTFIDFValue = new HashMap<String, Double>();
		
		Iterator<String> tfIter = wordsArr.itemFre.keySet().iterator();
		
		int wordsNumber = wordsArr.totalNumber;
		while(tfIter.hasNext()) {
			String word = tfIter.next().toString();
			
			int fre = wordsArr.itemFre.get(word);
			// 处理没有idf的词语，赋值为很小的值
			if(this.words_idf_vocab.get(word) == null) {
				double tfidfValue = (double)fre/wordsNumber*0.000123;
				retTFIDFValue.put(word, tfidfValue);
			}
			else {
				double tfidfValue = (double)fre/wordsNumber*this.words_idf_vocab.get(word);
				retTFIDFValue.put(word, tfidfValue);
			}
		}
		return this.getInvertArray(retTFIDFValue);
	}


	/**
	 * 获取单个词语的idf信息      20151111 Adolf add
	 * @param words   词语
	 * @return idf信息度
	 */
	public double getIdfValue(String words) {
		
		if (words.isEmpty() == true) {
			return 0.00000001;
		}
		if (this.words_idf_vocab.containsKey(words) == true) {
			return this.words_idf_vocab.get(words);
		}
		else {
			return 1.24987;
		}
	}

	/**
	 * judage the stop words
	 * @param words
	 * @return
     */
	public  boolean isStopWords(String words) {

		if (this.stopWordsDict.contains(words) == true) {
			return true;
		}
		return false;
	}
	/**
	 * 将计算出的TF*IDF数据结果进行倒排
	 * 得到分值最高的词语
	 * @param tempSort
	 * @return
	 */
	public Map<String, Double> getInvertArray(Map<String, Double> tempSort) {
		
		InvertedItemValue iivHandle = new InvertedItemValue();
		
		Map<String, Double> sortedDict = new HashMap<String,Double>();
		sortedDict = iivHandle.sortMapByDoubleValue(tempSort);
		
		return sortedDict;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double fre = 8/(double)20;
		System.out.println(Math.log(2000/(16 + 1)));
		WordsInformWeights wiw = new WordsInformWeights();
	}
}
