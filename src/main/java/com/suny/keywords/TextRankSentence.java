package com.suny.keywords;

import com.suny.keywords.process.TextUtility;
import com.suny.mmseg.interfa.MMsegInterface;

import java.util.*;

/**
 * TextRank 自动摘要
 *
 * @author  Adolf Frank
 */
public class TextRankSentence
{
		
    /**
     * 阻尼系数（ＤａｍｐｉｎｇＦａｃｔｏｒ），一般取值为0.85
     */
    private final static double d = 0.85;
    /**
     * 最大迭代次数
     */
    private final static int max_iter = 200;
    private final static double min_diff = 0.001;
    /**
     * 文档句子的个数
     */
    private int D;
    /**
     * 拆分为[句子[单词]]形式的文档
     */
    private List<List<String>> docs;
    /**
     * 排序后的最终结果 score <-> index
     */
    private TreeMap<Double, Integer> top;

    /**
     * 句子和其他句子的相关程度
     */
    private  double[][] weight;
    /**
     * 该句子和其他句子相关程度之和
     */
    private double[] weight_sum;
    /**
     * 迭代之后收敛的权重
     */
    private double[] vertex;

    /**
     * BM25相似度
     */
    private RelevanceBM25 bm25;

    private MMsegInterface textSeg = null;
    
    public TextRankSentence(){
        textSeg = MMsegInterface.getInstance();
    }
    
    public TextRankSentence(List<List<String>> docs)
    {
        this.docs = docs;
        this.bm25 = new RelevanceBM25(docs);
        this.D = docs.size();
        this.weight = new double[D][D];
        this.weight_sum = new double[D];
        this.vertex = new double[D];
        this.top = new TreeMap<Double, Integer>(Collections.reverseOrder());
        solve();
    }
    

    private void solve()
    {
        int cnt = 0;
        for (List<String> sentence : docs)
        {
            double[] scores = bm25.simAll(sentence);
//            System.out.println(Arrays.toString(scores));
            weight[cnt] = scores;
            weight_sum[cnt] = sum(scores) - scores[cnt]; // 减掉自己，自己跟自己肯定最相似
            vertex[cnt] = 1.0;
            ++cnt;
        }
        for (int _ = 0; _ < max_iter; ++_)
        {
            double[] m = new double[D];
            double max_diff = 0;
            for (int i = 0; i < D; ++i)
            {
                m[i] = 1 - d;
                for (int j = 0; j < D; ++j)
                {
                    if (j == i || weight_sum[j] == 0) continue;
                    m[i] += (d * weight[j][i] / weight_sum[j] * vertex[j]);
                }
                double diff = Math.abs(m[i] - vertex[i]);
                if (diff > max_diff)
                {
                    max_diff = diff;
                }
            }
            vertex = m;
            if (max_diff <= min_diff) break;
        }
        // 我们来排个序吧
        for (int i = 0; i < D; ++i)
        {
            top.put(vertex[i], i);
        }
    }

    /**
     * 获取前几个关键句子
     *
     * @param size 要几个
     * @return 关键句子的下标
     */
    public int[] getTopSentence(int size)
    {
        Collection<Integer> values = top.values();
        size = Math.min(size, values.size());
        int[] indexArray = new int[size];
        Iterator<Integer> it = values.iterator();
        for (int i = 0; i < size; ++i)
        {
            indexArray[i] = it.next();
        }
        return indexArray;
    }

    /**
     * 简单的求和
     *
     * @param array
     * @return
     */
    private static double sum(double[] array)
    {
        double total = 0;
        for (double v : array)
        {
            total += v;
        }
        return total;
    }


    /**
     * 将文章分割为句子
     *
     * @param document
     * @return
     */
    public  List<String> spiltSentence(String document)
    {
        List<String> sentences = new ArrayList<String>();
        for (String line : document.split("[\r\n]"))
        {
            line = line.trim();
            if (line.length() == 0) continue;
            for (String sent : line.split("[，,。:：“”？?！!；;]"))
            {
                sent = sent.trim();
                if (sent.length() == 0) continue;
                sentences.add(sent);
            }
        }

        return sentences;
    }

    /**
     * 将句子列表转化为文档
     *
     * @param sentenceList
     * @return
     */
    private  List<List<String>> convertSentenceListToDocument(List<String> sentenceList)
    {
        List<List<String>> docs = new ArrayList<List<String>>(sentenceList.size());
        for (String sentence : sentenceList)
        {
            List<String> wordList = this.textSeg.getWordsStrArrays(sentence);
            if(wordList != null) {
                docs.add(wordList);
            }

        }
        return docs;
    }

    /**
     * 一句话调用接口
     *
     * @param document 目标文档
     * @param size     需要的关键句的个数
     * @return 关键句列表
     */
    public  List<String> getTopSentenceList(String document, int size)
    {
        List<String> sentenceList = spiltSentence(document);
        List<List<String>> docs = this.convertSentenceListToDocument(sentenceList);
        TextRankSentence textRank = new TextRankSentence(docs);
        int[] topSentence = textRank.getTopSentence(size);
        List<String> resultList = new LinkedList<String>();
        for (int i : topSentence)
        {
            resultList.add(sentenceList.get(i));
        }
        return resultList;
    }

    /**
     * 一句话调用接口
     *
     * @param document   目标文档
     * @param max_length 需要摘要的长度
     * @return 摘要文本
     */
    public String getSummary(String document, int max_length)
    {
        List<String> sentenceList = spiltSentence(document);

        int sentence_count = sentenceList.size();
        int document_length = document.length();
        int sentence_length_avg = document_length / sentence_count;
        int size = max_length / sentence_length_avg + 1;
        List<List<String>> docs = convertSentenceListToDocument(sentenceList);
        TextRankSentence textRank = new TextRankSentence(docs);
        int[] topSentence = textRank.getTopSentence(size);
        List<String> resultList = new LinkedList<String>();
        for (int i : topSentence)
        {
            resultList.add(sentenceList.get(i));
        }

        resultList = permutation(resultList, sentenceList);
        resultList = pick_sentences(resultList, max_length);
        return TextUtility.join("。", resultList);
    }

    public static List<String> permutation(List<String> resultList, List<String> sentenceList)
    {
        int index_buffer_x;
        int index_buffer_y;
        String sen_x;
        String sen_y;
        int length = resultList.size();
        // bubble sort derivative
        for (int i = 0; i < length; i++)
            for (int offset = 0; offset < length - i; offset++)
            {
                sen_x = resultList.get(i);
                sen_y = resultList.get(i + offset);
                index_buffer_x = sentenceList.indexOf(sen_x);
                index_buffer_y = sentenceList.indexOf(sen_y);
                // if the sentence order in sentenceList does not conform that is in resultList, reverse it
                if (index_buffer_x > index_buffer_y)
                {
                    resultList.set(i, sen_y);
                    resultList.set(i + offset, sen_x);
                }
            }

        return resultList;
    }

    public static List<String> pick_sentences(List<String> resultList, int max_length)
    {
        int length_counter = 0;
        int length_buffer;
        int length_jump;
        List<String> resultBuffer = new LinkedList<String>();
        for (int i = 0; i < resultList.size(); i++)
        {
            length_buffer = length_counter + resultList.get(i).length();
            if (length_buffer <= max_length)
            {
                resultBuffer.add(resultList.get(i));
                length_counter += resultList.get(i).length();
            }
            else if (i < (resultList.size() - 1))
            {
                length_jump = length_counter + resultList.get(i + 1).length();
                if (length_jump <= max_length)
                {
                    resultBuffer.add(resultList.get(i + 1));
                    length_counter += resultList.get(i + 1).length();
                    i++;
                }
            }
        }
        return resultBuffer;
    }
    

    public static void main(String[] args)
    {
       String document_one = "算法可大致分为基本算法、数据结构的算法、数论算法、计算几何的算法、图的算法、动态规划以及数值分析、加密算法、排序算法、检索算法、随机化算法、并行算法、厄米变形模型、随机森林算法。\n" +
                "算法可以宽泛的分为三类，\n" +
                "一，有限的确定性算法，这类算法在有限的一段时间内终止。他们可能要花很长时间来执行指定的任务，但仍将在一定的时间内终止。这类算法得出的结果常取决于输入值。\n" +
                "二，有限的非确定算法，这类算法在有限的时间内终止。然而，对于一个（或一些）给定的数值，算法的结果并不是唯一的或确定的。\n" +
                "三，无限的算法，是那些由于没有定义终止定义条件，或定义的条件无法由输入的数据满足而不终止运行的算法。通常，无限算法的产生是由于未能确定的定义终止条件。";
        
    	String document = "国家信访局外严密布控（维权人士独家提供）中国全国访民集访日以及e租宝投资者集体维权令当局如临大敌，在国家信访局外重兵布控。有访民向本台表示，所有驶经信访局的公交车辆均被禁止通行，无法靠近信访局。另一方面，前往北京维权的e租宝投资者忽然噤声，或遭控制。1月10日是中国访民原定的集访日，当天来自全国各地的访民们纷纷前往位于北京的国家信访局，不过，令他们错愕的是，信访局外部署了大批警力，他们根本无法靠近。来自深圳的访民黄美娟当天接受本台采访时表示，她于前一天抵达北京，在宾馆住宿时就遭到警察上门查房、警告，10日上午，原本驶经信访局的6路公交车全部被禁止通行。黄美娟：“因为今天10号，是全国访民本来准备约的集访日，所以今天国家信访局大门口，大量的警力把国家信访局大门口给封堵了。另外本来是有6辆公交车线路是通过国家信访局门口的，但是今天把国家信访局门口两端的道路全部封堵，禁止车辆通行。无论公交车、还是私家车还是什么车，都不允许通行。当然截访的车是可以的，另外还有一些公安的待命车、法院的待命车在那里是有的。”记者：“大家完全没有办法靠近信访局门口吗？”黄美娟：“完全不可以。现在信访局门口是没有访民的，访民都在封堵的路口的两端侧面的道路上。侧面到路上也有大量警察在那儿待命。我昨天入住一个酒店，因为以前我也上访过多次，我也进过久敬庄的，我的身份证在北京市进了他（警方）的系统了，所以我一进宾馆，不到半个小时警察就来了，来查房，查我的包里的材料，问我过来干什么，然后进行警告：不允许举牌，不允许上街，不允许进行非访活动这些。”黄美娟说，据悉参与稳控的还有从天津借调的警力。根据现场照片可见，大量警察在信访局门前层层列队，路边停泊着多辆警车，还有不少访民被送上车辆押往久敬庄。维权人士伍立娟告诉记者，根据现场访民的反馈，至少有数千名警察及上百辆警车在待命。“昨天国家信访局都布控了，昨天晚上别人（访民）发给我的小视频看到，都有警车一百多辆，警察几千人呢。”与此同时，e租宝投资者原本也准备于1月8日至10日集体前往北京维权，本台此前曾报道，他们中不少人遭到地方拦截。而根据网上流传的视频可见，1月8日，在国家信访局外上访维权的e租宝投资者全部被警察抓走，有投资者在现场高声质问警方“有人情吗？”黄美娟告诉记者，她10日在信访局外并未看见e租宝的投资者。另一名未参与赴京维权的e租宝投资人则告诉本台，往常用来讨论的QQ群内完全没有了交流，不知那些人目前情况如何，或已被控制。“人一直没有消息，他们在网上也没有消息，是被控制住了还是怎么样了就不知道了。”本台记者10日拨打数名在北京维权的投资者电话，但不是提示已关机就是无人接听。曾前往现场探访投资者的中国人权观察代理秘书长徐秦也自8日起失联，至今仍无消息。";
        TextRankSentence trs = new TextRankSentence();
    	
    	
        System.out.println(trs.getTopSentenceList(document, 3));
    }
}
