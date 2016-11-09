package com.suny.tfidf;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


public class WordsIdfInform {

	private Map<String, Double> words_idf_vocab = null;
	
	public WordsIdfInform (String fileName) {
		this.words_idf_vocab = new HashMap<String,Double>();
		try {
			this.loadIDFVocab(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * loading words idf words information dictionary
	 * @return  true if success otherwise is false
	 * @throws IOException
	 */
	public boolean loadIDFVocab(String loadFileName) throws IOException {
		
		if (loadFileName.isEmpty()) {
			return false;
		}
		
		try {
			FileInputStream fis = new FileInputStream(loadFileName);
			InputStreamReader isr = new InputStreamReader(fis,"UTF-8");
			BufferedReader br = new BufferedReader(isr);
			
			String textLine  = "";
			int totalDocument = 0;
			while (( textLine = br.readLine()) != null) {
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
					int pos = textLine.indexOf("\t");
					if (pos != -1) {
						word = textLine.substring(0,pos); 
						textLine = textLine.substring(pos + 1);
						if (textLine.indexOf("\t") != -1) {
							System.out.println(word + "\t" + textLine);
						}
						else {
							if (word.isEmpty() == false) {
								int freq = Integer.parseInt(textLine.trim());
								/**
								 * IDF = log(语料库中的文档总数/(包含该词语的文档数目+1))
								 */
								if (freq > 0) {
									double idfValue = Math.log(totalDocument/(freq + 1));
									this.words_idf_vocab.put(word, idfValue);
								}
							}
						}	
					}
				}
			}
			br.close();
			isr.close();
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println(this.words_idf_vocab.size());
		return true;
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
			return 0.000123;
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		//WordsIdfInform wii = new WordsIdfInform("E:\\peperStatic\\20151217\\lower_hotword_idf.txt");
		WordsIdfInform wii = new WordsIdfInform("E:\\my_work\\SentimentAnalysisCarField\\dict\\words_idf_inform.txt");
		//System.out.println(wii.getIdfValue("精神病鉴定的法定程序"));
		System.out.println(wii.getIdfValue("牙签万轴"));
		System.out.println(wii.getIdfValue("立案登记制"));
	}
}
