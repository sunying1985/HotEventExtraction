package com.suny.keywords;

import com.suny.mmseg.interfa.MMsegInterface;

import java.util.*;


/**
 * 基于TextRank算法的关键字提取，适用于单文档
 * @author Frank Adolf
 */
public class TextRankKeyword
{
	// 分词句柄
    public MMsegInterface textSeg = null;
    /**
     * 提取多少个关键字
     */
    private int nKeyword = 10;
    /**
     * 阻尼系数（ＤａｍｐｉｎｇＦａｃｔｏｒ），一般取值为0.85
     */
    final static float d = 0.85f;
    /**
     * 最大迭代次数
     */
    final static int max_iter = 200;
    final static float min_diff = 0.001f;


    public TextRankKeyword() {
    	this.textSeg = MMsegInterface.getInstance();
    }
    /**
     * 提取关键词
     * @param document 文档内容
     * @param size 希望提取几个关键词
     * @return 一个列表
     */
    public  List<String> getKeywordList(String document, int size)
    {
        this.nKeyword = size;

        return this.getKeyword(document);
    }

    /**
     * 提取关键词
     * @param content
     * @return
     */
    private List<String> getKeyword(String content)
    {
        Set<Map.Entry<String, Float>> entrySet = getTermAndRank(content, nKeyword).entrySet();
        List<String> result = new ArrayList<String>(entrySet.size());
        for (Map.Entry<String, Float> entry : entrySet)
        {
            result.add(entry.getKey());
        }
        return result;
    }

    /**
     * 返回全部分词结果和对应的rank
     * @param content
     * @return
     */
    private Map<String,Float> getTermAndRank(String content)
    {
        assert content != null;
        List<String> termList = this.textSeg.getWordsStrArrays(content);
        return getRank(termList);
    }

    /**
     * 对外部提供的接口，
     * @param termList   提供分词去停用词的结果
     * @param size      关键词数目
     * @return
     */
    public Map<String, Float> getTermRankTopN(List<String> termList, int size) {

        this.nKeyword = size;
        Map<String, Float> map = this.getRank(termList);
        if (map == null) {
            return  null;
        }
        Map<String, Float> result = new LinkedHashMap<String, Float>();
        for (Map.Entry<String, Float> entry : new MaxHeap<Map.Entry<String, Float>>(size, new Comparator<Map.Entry<String, Float>>()
        {
            @Override
            public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2)
            {
                return o1.getValue().compareTo(o2.getValue());
            }
        }).addAll(map.entrySet()).toList())
        {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    /**
     * 返回分数最高的前size个分词结果和对应的rank
     * @param content
     * @param size
     * @return
     */
    private Map<String,Float> getTermAndRank(String content,/* List<String> termList,*/ Integer size)
    {
        Map<String, Float> map = getTermAndRank(content);
       // Map<String, Float> map = getTermRankTopN(termList,size);
        Map<String, Float> result = new LinkedHashMap<String, Float>();
        for (Map.Entry<String, Float> entry : new MaxHeap<Map.Entry<String, Float>>(size, new Comparator<Map.Entry<String, Float>>()
        {
            @Override
            public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2)
            {
                return o1.getValue().compareTo(o2.getValue());
            }
        }).addAll(map.entrySet()).toList())
        {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    /**
     * 返回分数最高的前size个分词结果和对应的rank
     * @param content
     * @param size
     * @return
     */
    public Map<String,Float> getTermAndRankScore(String content, int size)
    {
        this.nKeyword = size;
        Map<String, Float> map = getTermAndRank(content);
        Map<String, Float> result = new LinkedHashMap<String, Float>();
        for (Map.Entry<String, Float> entry : new MaxHeap<Map.Entry<String, Float>>(size, new Comparator<Map.Entry<String, Float>>()
        {
            @Override
            public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2)
            {
                return o1.getValue().compareTo(o2.getValue());
            }
        }).addAll(map.entrySet()).toList())
        {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }


    /**
     * 使用已经分好的词来计算rank
     * @param termList
     * @return
     */
    private Map<String,Float> getRank(List<String> termList)
    {
        if (termList == null) {
            return  null;
        }
        List<String> wordList = termList;
        //System.out.println(wordList);
        Map<String, Set<String>> words = new TreeMap<String, Set<String>>();
        Queue<String> que = new LinkedList<String>();
        for (String w : wordList)
        {
            if (!words.containsKey(w))
            {
                words.put(w, new TreeSet<String>());
            }
            que.offer(w);
            if (que.size() > 5)
            {
                que.poll();
            }

            for (String w1 : que)
            {
                for (String w2 : que)
                {
                    if (w1.equals(w2))
                    {
                        continue;
                    }

                    words.get(w1).add(w2);
                    words.get(w2).add(w1);
                }
            }
        }
       //System.out.println(words);
        Map<String, Float> score = new HashMap<String, Float>();
        for (int i = 0; i < max_iter; ++i)
        {
            Map<String, Float> m = new HashMap<String, Float>();
            float max_diff = 0;
            for (Map.Entry<String, Set<String>> entry : words.entrySet())
            {
                String key = entry.getKey();
                Set<String> value = entry.getValue();
                m.put(key, 1 - d);
                for (String element : value)
                {
                    int size = words.get(element).size();
                    if (key.equals(element) || size == 0) continue;
                    m.put(key, m.get(key) + d / size * (score.get(element) == null ? 0 : score.get(element)));
                }
                max_diff = Math.max(max_diff, Math.abs(m.get(key) - (score.get(key) == null ? 0 : score.get(key))));
            }
            score = m;
            if (max_diff <= min_diff) break;
        }

        return score;
    }
  
   public static void main(String[] args)
   {
       /*String content = "程序员(英文Programmer)是从事程序开发、维护的专业人员。" +
               "一般将程序员分为程序设计人员和程序编码人员，" +
               "但两者的界限并不非常清楚，特别是在中国。" +
               "软件从业人员分为初级程序员、高级程序员、系统" +
               "分析员和项目经理四大类。";
       */
	   TextRankKeyword trkw = new TextRankKeyword();
       String content = "国家信访局外严密布控ATM银行（维权人士独家提供）中国全国访民集访日以及e租宝投资者集体维权令当局如临大敌，在国家信访局外重兵布控。有访民向本台表示，所有驶经信访局的公交车辆均被禁止通行，无法靠近信访局。另一方面，前往北京维权的e租宝投资者忽然噤声，或遭控制。1月10日是中国访民原定的集访日，当天来自全国各地的访民们纷纷前往位于北京的国家信访局，不过，令他们错愕的是，信访局外部署了大批警力，他们根本无法靠近。来自深圳的访民黄美娟当天接受本台采访时表示，她于前一天抵达北京，在宾馆住宿时就遭到警察上门查房、警告，10日上午，原本驶经信访局的6路公交车全部被禁止通行。黄美娟：“因为今天10号，是全国访民本来准备约的集访日，所以今天国家信访局大门口，大量的警力把国家信访局大门口给封堵了。另外本来是有6辆公交车线路是通过国家信访局门口的，但是今天把国家信访局门口两端的道路全部封堵，禁止车辆通行。无论公交车、还是私家车还是什么车，都不允许通行。当然截访的车是可以的，另外还有一些公安的待命车、法院的待命车在那里是有的。”记者：“大家完全没有办法靠近信访局门口吗？”黄美娟：“完全不可以。现在信访局门口是没有访民的，访民都在封堵的路口的两端侧面的道路上。侧面到路上也有大量警察在那儿待命。我昨天入住一个酒店，因为以前我也上访过多次，我也进过久敬庄的，我的身份证在北京市进了他（警方）的系统了，所以我一进宾馆，不到半个小时警察就来了，来查房，查我的包里的材料，问我过来干什么，然后进行警告：不允许举牌，不允许上街，不允许进行非访活动这些。”黄美娟说，据悉参与稳控的还有从天津借调的警力。根据现场照片可见，大量警察在信访局门前层层列队，路边停泊着多辆警车，还有不少访民被送上车辆押往久敬庄。维权人士伍立娟告诉记者，根据现场访民的反馈，至少有数千名警察及上百辆警车在待命。“昨天国家信访局都布控了，昨天晚上别人（访民）发给我的小视频看到，都有警车一百多辆，警察几千人呢。”与此同时，e租宝投资者原本也准备于1月8日至10日集体前往北京维权，本台此前曾报道，他们中不少人遭到地方拦截。而根据网上流传的视频可见，1月8日，在国家信访局外上访维权的e租宝投资者全部被警察抓走，有投资者在现场高声质问警方“有人情吗？”黄美娟告诉记者，她10日在信访局外并未看见e租宝的投资者。另一名未参与赴京维权的e租宝投资人则告诉本台，往常用来讨论的QQ群内完全没有了交流，不知那些人目前情况如何，或已被控制。“人一直没有消息，他们在网上也没有消息，是被控制住了还是怎么样了就不知道了。”本台记者10日拨打数名在北京维权的投资者电话，但不是提示已关机就是无人接听。曾前往现场探访投资者的中国人权观察代理秘书长徐秦也自8日起失联，至今仍无消息。";
       //String content = "朱文晖：周本顺这次闪电落马，我自己来解读的话，可能至少破解了，就是我们对于北京反腐工作的三大疑虑，第一个疑虑是什么疑虑呢？就是这个一看周本顺的简历，你发现他长期是在公安系统工作，后来又到北京政法委系统工作了相当长时间，他是用中央政法委秘书长的身份到了河北接任的省委书记，我们也知道河北他是北京的周边，尤其比如说马上开的北戴河会议，那么这个保安工作河北有非常重大的压力，所以政法委和河北他有这种天然连接的关系，为中央提供警卫工作，为整个北京稳定提供帮助提供保障，所以他去那个地方看来是非常正常的。但是我们也知道，就是他在中央政法委的工作，那自然就和周永康是不是有关联，因为他当秘书长的时候，周永康正好是政法委书记，我们也确实看到，周本顺他原来是湖南省公安厅厅长一路提拔到政法委书记，然后到了中央北京去工作的，所以就是连带这个事件，你其实看到在这个十八大以后，这个中央政法委书记，就是不再由政治局常委来担任，而且就是今天特别网上还有很多地方的评论谈到了，就是政法委书记不再兼任省的公安厅厅长，这里边有一个非常明确的一种关系，但是这里边也就反映出来，就是周永康这个事件大家本来以为他就是完结了，因为他已经审判完结。那么另外就是令计划和这个事件有一定的关联，所以今天海外媒体也有报道，说当时车祸处理的过程当中，是不是他也牵涉到里头，因为令计划事情前一段时间也是移交司法进行处理了，大家本来以为这个事情就划一个句号，但现在看来周本顺找来找去，连接起来和他还是有一个关联的。";
   	   //List<String> termList = trkw.textSeg.getWordsStrArrays(content);
       //System.out.println(termList);
        /*
       List<String> keywordList = trkw.getKeywordList(content, 10);
       System.out.println(keywordList);
       */
        Map<String,Float> dictScore = trkw.getTermAndRankScore(content,10000);
       for(String key : dictScore.keySet()) {
           System.out.println(key + "\t" + dictScore.get(key));
       }
   }
}
