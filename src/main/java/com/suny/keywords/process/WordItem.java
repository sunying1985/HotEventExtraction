package com.suny.keywords.process;

/**
 * Created by Frank Adolf
 * Date on 2016/11/14.
 */
public class WordItem {
    /**
     * @param  Adolf Frank
     * @date   20161111
     * 词条保存的信息
     */

    private String wordStr;
    private int wordFreq;

    public WordItem()
    {
        setWordStr("");
        setWordFreq(1);
    }

    public WordItem(String word, int i)
    {
        this();
        wordStr = word;
        wordFreq = i;
    }

    public WordItem(WordItem word)
    {
        wordStr = word.getWordStr();
        wordFreq = word.getWordFreq();
    }

    public String getWordStr()
    {
        return wordStr;
    }

    public void setWordStr(String wordStr)
    {
        this.wordStr = wordStr;
    }


    public int getWordFreq()
    {
        return wordFreq;
    }

    public void setWordFreq(int wordFreq)
    {
        this.wordFreq = wordFreq;
    }


    public String toString()
    {
        return (new StringBuilder("WordItem[wordStr=")).append(wordStr).append(", wordFreq=").append(wordFreq).append("]").toString();
    }

}
