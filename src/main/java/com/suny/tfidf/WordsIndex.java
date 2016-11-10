package com.suny.tfidf;

import com.suny.docu.item.IndexVector;
import com.suny.docu.item.ItemFreq;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Frank Adolf
 * Date on 2016/11/8.
 * 将词向量索引化
 */
public class WordsIndex {

    /**
     * words index dictionary
     */
    private Map<String, Integer> dictIndex = null;

    public WordsIndex() {

        this.dictIndex = new HashMap<String, Integer>();
    }

    /**
     *	将词语转化为索引
     */
    public void initWordsDictIntoIndex(Set<String> wordsDict) {

        int index = 0;
        for (String word : wordsDict) {
            if(word.length() > 1 ) {
                this.dictIndex.put(word,index);
                index++;
            }
        }
    }

    /**
     * 根据输入的词语和词频计算 每一个词语的信息度权值
     * @param wordsArr   词语和其对应的频率
     * @return			文本向量化结果
     */
    public IndexVector calculationIndexTFIDF(ItemFreq wordsArr,WordsInformWeights wordsInforHandle) {

        if (wordsArr.itemFre.size() == 0) {
            return null;
        }
        IndexVector  retTFIDFValue = new IndexVector(this.dictIndex.size() + 1);

        Iterator<String> tfIter = wordsArr.itemFre.keySet().iterator();

        int wordsNumber = wordsArr.totalNumber;
        while(tfIter.hasNext()) {
            String word = tfIter.next().toString();

            int fre = wordsArr.itemFre.get(word);
            if(word.length() > 1 ) {
                if(this.dictIndex.containsKey(word) == true) {
                    double tfidfValue = (double)fre/wordsNumber*wordsInforHandle.getIdfValue(word);

                    retTFIDFValue.set(this.getWordsIndex(word),tfidfValue*1000);
                }
                // 没有的词语index 不存在，暂时不进行处理
            }

        }
        return retTFIDFValue;
    }

    /**
     * 获取词语的索引      20161114 Adolf add
     * @param words   词语
     * @return  the words index
     */
    public int getWordsIndex(String words) {

        if (words.equals("") == true || words.isEmpty() == true) {
            return 0;
        }
        return this.dictIndex.get(words);
    }

    /**
     * 返回索引表词典的大小
     * @return
     */
    public int indexDictionarySize() {
        return this.dictIndex.size();
    }

}
