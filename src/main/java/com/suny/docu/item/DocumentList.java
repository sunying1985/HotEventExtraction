package com.suny.docu.item;

import com.suny.distance.CosineDistance;
import com.suny.distance.DistanceMetric;
import com.suny.distance.LVdistance;
import com.suny.tfidf.WordsIndex;
import com.suny.tfidf.WordsInformWeights;
import sortfreq.NumberSort;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** 
 * Class for storing a collection of documents to be clustered.
 *  */
public class DocumentList  {

    // the result sets
	private  List<DocumentItem> documents = new ArrayList<DocumentItem>();
	// the extera sources
	private WordsInformWeights wordsInfroHanle = null;

	/**
	 *  Construct an empty DocumentList. 
	 * */
	public DocumentList() {

	}

	public List<DocumentItem> getDocuments() {
		return documents;
	}

	public void setDocuments(List<DocumentItem> documents) {
		this.documents = documents;
	}

	public WordsInformWeights getWordsInfroHanle() {
		return wordsInfroHanle;
	}

	public void setWordsInfroHanle(WordsInformWeights wordsInfroHanle) {
		this.wordsInfroHanle = wordsInfroHanle;
	}

	/**
	 *	document 之前必须进行各个字段配置和序列化
	 */
	public boolean addToDocumentList(Document document) {
		if (document == null) {
			return  false;
		}
		// 没有正文或者
		if (document.getWordsFreq() == null) {
			return false;
		}

		if (this.documents.size() == 0) {
			DocumentItem  documentItem = new DocumentItem();
			documentItem.setCurDoc(document);
			documentItem.addSimlaryId(document.getId());

			this.documents.add(documentItem);
		}
		else {
			int initDocSize = this.documents.size();
			int lable = 0;
			for( int i = 0; i < initDocSize; i++) {
				Document curDocu = this.documents.get(i).getCurDoc();

				// 1. 计算标题相似度
				double titleValue = LVdistance.levenshteinDistance(document.getTitle(),curDocu.getTitle());
				// 2. 计算正文相似度
				double contentValue = this.getDocumentSimilaryScore(document.getWordsFreq(), curDocu.getWordsFreq());
				// 3. 添加序列或自成类
				if (titleValue > 0.8 && contentValue > 0.6) {

					DocumentItem  documentItem = this.documents.get(i);
					documentItem.setCurDoc(document);
					documentItem.addSimlaryId(document.getId());

					this.documents.set(i,documentItem);
					lable = 1;
					break;
				}
			}
			if(lable == 0) {
				DocumentItem  documentItem = new DocumentItem();
				documentItem.setCurDoc(document);
				documentItem.addSimlaryId(document.getId());

				this.documents.add(documentItem);
			}
		}
		return  true;
	}

	/**
	 *  Get the number of documents within the DocumentList. 
	 *  */
	public int size() {
		return documents.size();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (DocumentItem documentItem : documents) {
			sb.append(documentItem.getSimIdArray().size() + "  \n");
		}

		return sb.toString();
	}

	/**
	 * get the top hot number events
	 * @param topNum	the top num
	 * @return
     */
	public List<DocumentItem> getTopHotEvent(int topNum) {
		if(topNum < 1) {
			return  null;
		}
		int endSize = this.documents.size();
		int [] indexArray = new int[endSize];
		for (int i = 0; i < endSize; i++) {
			indexArray[i] = this.documents.get(i).getSimilarySize();
		}
		//NumberSort.quickSort(indexArray,0,endSize - 1);
		int [] indexSortArray = NumberSort.indexSort(indexArray,endSize);
		if (topNum > this.documents.size()) {
			return  this.documents;
		}
		else {
			int j = endSize - 1;
			List<DocumentItem> retTop = new ArrayList<DocumentItem>();
			int allSize = 0;
			while(j > 0) {
				retTop.add(allSize,this.documents.get(indexSortArray[j]));
				j--;
				allSize++;
				if(allSize > 9) {
					break;
				}
			}

			return  retTop;
		}
	}

	public double getDocumentSimilaryScore(ItemFreq res, ItemFreq dest) {
		if (res.itemFre.size() == 0 || dest.itemFre.size() == 0) {
			return  0.0;
		}
		// 获取词语集合
		Set<String> wordsSet = new HashSet<String>();
		for (String key : res.itemFre.keySet()) {
			wordsSet.add(key);
		}
		for(String key: dest.itemFre.keySet()) {
			wordsSet.add(key);
		}
		// 词语索引向量
		WordsIndex dictIndex = new WordsIndex();
		dictIndex.initWordsDictIntoIndex(wordsSet);
		// 正文索引化
		IndexVector resIndex = dictIndex.calculationIndexTFIDF(res,this.getWordsInfroHanle());
		IndexVector destIndex = dictIndex.calculationIndexTFIDF(dest,this.getWordsInfroHanle());


		DistanceMetric distanceMetric = new CosineDistance();

		return distanceMetric.calcDistance(resIndex,destIndex);
	}

}
