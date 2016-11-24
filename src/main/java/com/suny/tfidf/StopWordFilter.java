/**
 * 停用词过滤
 */

package com.suny.tfidf;

import com.suny.dictionary.EncryptDictionary;
import com.suny.dictionary.SystemParas;
import com.suny.keywords.process.TextUtility;
import com.suny.keywords.process.WordItem;

import java.io.IOException;

import java.util.*;

public class StopWordFilter {


    // 异步形式
    private  static StopWordFilter  instance = null;

    public synchronized static StopWordFilter getInstance(){
        if(instance == null){
            instance = new StopWordFilter();

        }
        return instance;
    }

    protected static HashSet<String> stopWords =null;

    public StopWordFilter() {
        this.stopWords = new HashSet<String>();

        try {
            this.loadStopWordsDictionary();

        } catch (IOException e) {
            e.printStackTrace();
        }
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
                            this.stopWords.add(word);
                        }
                    }
                    else {
                        word = textLine;
                        this.stopWords.add(word);
                    }
                }
            }
        }

        if (this.stopWords.size() == 0) {
            System.err.println("ConfigInfoError" + "logging stop words dictionary SIZE is error!");
            return false;
        }

        return true;
    }
    public List<String> phraseDel(String str) {
        ArrayList list = new ArrayList();
        List listTemp = Arrays.asList(str.split("\\s+"));
        int length = listTemp.size();

        for(int i = 0; i < length; ++i) {
            String s = (String)listTemp.get(i);
            if(!isStopWord(s)) {
                list.add(s);
            }
        }

        return list;
    }

    public List<WordItem> filtStopWord(WordItem[] words) {
        if(words == null) {
            return null;
        } else {
            ArrayList pureWordsList = new ArrayList();
            int len = words.length;

            for(int i = 0; i < len; ++i) {
                String str = words[i].getWordStr();
                if(!isStopWord(str)) {
                    pureWordsList.add(words[i]);
                }
            }

            return pureWordsList;
        }
    }

    public HashMap<String, WordItem> filtWords(WordItem[] words) {
        if(words == null) {
            return null;
        } else {
            HashMap pureWords = new HashMap();
            int len = words.length;

            for(int i = 0; i < len; ++i) {
                String str = words[i].getWordStr();
                if(!isStopWord(str)) {
                    pureWords.put(str, words[i]);
                }
            }

            return pureWords;
        }
    }

    public HashSet<String> filtWords_new(WordItem[] words) {
        if(words == null) {
            return null;
        } else {
            HashSet pureWords = new HashSet();
            int len = words.length;

            for(int i = 0; i < len; ++i) {
                String str = words[i].getWordStr();
                if(!isStopWord(str)) {
                    pureWords.add(str);
                }
            }

            return pureWords;
        }
    }

    public static boolean isStopWord(String word) {
        if(word.length() == 1) {
            return  true;
        }
        if(stopWords.contains(word) == true || true == TextUtility.isAllLetterOrNum(word)) {
            return  true;
        }
        return false;
    }
    
}
