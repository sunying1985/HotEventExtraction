package com.suny.mmseg.interfa;


import java.io.IOException;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.suny.dataobtain.tool.DetermTextType;
import com.suny.keywords.process.WordItem;
import com.suny.mmseg.main.ComplexSeg;
import com.suny.mmseg.main.Dictionary;
import com.suny.mmseg.main.MMSeg;
import com.suny.mmseg.main.Seg;
import com.suny.mmseg.main.Word;
import com.suny.tfidf.StopWordFilter;

/**
 * 分词对外部的接口，生成过程中需，设置好分词词典的路径名称
 * @author Frank Adolf
 *
 */

public class MMsegInterface {

	/**
	 * error logger information
	 */
	private static final Logger logInfo = Logger.getLogger(MMsegInterface.class.getName());
	
	// 异步形式
	private static MMsegInterface instance = null;

	public synchronized static MMsegInterface getInstance(){
		if(instance == null){
			instance = new MMsegInterface();
			
		}
		return instance;
	}
	/**
	 * words segment handle
	 */
	private Seg wordSegHandle = null;
	// stop words filters handles
	private StopWordFilter filterStopWords = null;

	
	private MMsegInterface() {
		this.init();
	}
	
	private void init() {
		
		// segment dictionary file name
		Dictionary dic = Dictionary.getInstance();
		// Forward maximum matching
		//this.wordSegHandle = new SimpleSeg(dic);
		this.wordSegHandle = new ComplexSeg(dic);
		this.filterStopWords = StopWordFilter.getInstance();
	}
	
	
	/**
	 * only store the word ends position
	 * @param  text   the input string
	 * @return the    segment tag pos array
	 */
	public int [] textWordSegLable(String text) {
		if (text.isEmpty()) {
			return null;
		}
		
		int [] segLable = new int[text.length()];
		for (int i = 0; i < text.length(); i++) {
			segLable[i] = 0;
		}
		
		MMSeg mmSeg = new MMSeg(new StringReader(text), this.wordSegHandle);
		Word word = null;
		int tag = 0;
		
		try {
			while((word = mmSeg.next()) != null) {
				/*System.out.print(word.getString()+" -> "+word.getStartOffset());
				//offset += word.length;
				System.out.println(", "+word.getEndOffset()+", "+word.getType());
				*/
				segLable[tag] = word.getEndOffset();
				tag++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return segLable;
	}
	
	
	/**
	 * text segment word result and split using $$
	 * @param  text   the input string
	 * @return the    segment tag pos array
	 */
	public String textWordSegStr(String text) {
		if (text.isEmpty()) {
			return null;
		}
		
		MMSeg mmSeg = new MMSeg(new StringReader(text), this.wordSegHandle);
		Word word = null;
		StringBuffer retBuffer = new StringBuffer();
		
		try {
			while((word = mmSeg.next()) != null) {
				/*System.out.print(word.getString()+" -> "+word.getStartOffset());
				//offset += word.length;
				System.out.println(", "+word.getEndOffset()+", "+word.getType());
				*/
				retBuffer.append(word.getString() + "$$");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return retBuffer.toString();
	}

	
	/**
	 * store the word ends position and the sentences ends position
	 * @param  text   the input string
	 * @return the    segment tag pos array
	 */
	private int [] textWordSegLable(String text,int [] senPos) {
		if (text.isEmpty()) {
			return null;
		}
		
		if (senPos == null) {
			return this.textWordSegLable(text);
		}
		
		int [] segLable = new int[text.length()];
		for (int i = 0; i < text.length(); i++) {
			segLable[i] = 0;
		}
		
		MMSeg mmSeg = new MMSeg(new StringReader(text), this.wordSegHandle);
		Word word = null;
		int tag = 0;
		int index = 0;
		try {
			while((word = mmSeg.next()) != null) {
				//System.out.print(word.getString()+" -> "+word.getStartOffset());
				//offset += word.length;
				//System.out.println(", "+word.getEndOffset()+", "+word.getType());
				
				if (word.getType() == "punct") {
					senPos[index] = word.getEndOffset();
					index++;
				}
				segLable[tag] = word.getEndOffset();
				tag++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return segLable;
	}


	/**
	 * 分词后转化为词语和频率的形式
	 * 同时去掉停用词语
	 * @param text
	 * @return
	 */
	public WordItem[] getWordsArrays(String text) {

		List<WordItem> wordsList = this.getWordsArray(text);

		WordItem[] wordsArr = new WordItem[wordsList.size()];
		for(int i = 0; i < wordsList.size(); i++) {
			wordsArr[i] =  wordsList.get(i);
		}
		return  wordsArr;
	}

	/*
    * 分词后转化为词语和频率的形式
    * 同时去掉停用词语
    * @param text
    * @return
            */
	public List<WordItem> getWordsArray(String text) {

		return  this.getWordsArray(text);
	}


	/**
	 * 另外一种实现方式，只返回去掉停用词后的词语数组
	 * @param text
	 * @return
	 */
	public List<String> getWordsStrArrays(String text) {
		if (text.length() < 6 ||
				false == DetermTextType.textType(text)) {
			return  null;
		}
		// segment pos index
		int [] segPos = this.textWordSegLable(text);
		int  j = 0;
		int textLength = text.length() - 1;
		List<String> listWords = new ArrayList<String>();
		int k = 0;
		while (j < textLength && segPos[j] != 0) {

			if (j == 0) {
				String curWord = text.substring(0,segPos[j]);
				if(curWord.length() > 1 &&  this.filterStopWords.isStopWord(curWord) == false) {
					listWords.add(k,curWord);
					k++;
				}
			}
			else {
				String curWord = text.substring(segPos[j - 1],segPos[j]);
				if(curWord.length() > 1 && this.filterStopWords.isStopWord(curWord) == false) {
					listWords.add(k,curWord);
					k++;
				}
			}
			j++;
		}

		return  listWords;

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MMsegInterface testex = new MMsegInterface();
		// 加载文本
		//String text = testex.loadingTextFromFile("E:\\my_work\\EntityNamedRecognition\\testCor\\test2.txt");
		String text = "邢台市人民检察院";
		// 分词标志位
		int [] segPos = testex.textWordSegLable(text);

		int  j = 0;
		while (segPos[j] != 0) {
			if (j == 0) {
				System.out.println(text.substring(0,segPos[j]));
			}
			else {
				System.out.println(text.substring(segPos[j - 1],segPos[j]));
			}
			j++;
		}
		
		System.out.println(testex.textWordSegStr(text));
	}
}
