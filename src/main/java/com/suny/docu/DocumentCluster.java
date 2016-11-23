package com.suny.docu;

import com.suny.dataobtain.tool.DetermTextType;
import com.suny.dataobtain.tool.FileTool;
import com.suny.fingerprints.FingerPrint;
import com.suny.keywords.TextRankKeyword;
import com.suny.mmseg.interfa.MMsegInterface;
import com.suny.tfidf.WordsInformWeights;


import java.io.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Frank Adolf
 * Date on 2016/11/7.
 */
public class DocumentCluster {

    // 词频统计的阈值
    private static final  int wordFreCutoff = 3;

    // 所取关键词TopN数目
    private  static  final  int topWordsNum = 100;

    private MMsegInterface segHandle = MMsegInterface.getInstance();

    private WordsInformWeights wordsInfor = WordsInformWeights.getInstance();

    private  DocumentList  clusterArray = null;

    public  DocumentCluster() {
        this.clusterArray = new DocumentList();
    }

    /**
     * transform the content into words index
     * @param termList    the article words segments array
     */
    public ItemFreq textTransWordsFreq(List<String> termList) {
        if (termList == null) {
            return  null;
        }
        if(termList.size() < 4) {
            return  null;
        }
        ItemFreq itemFreq = new ItemFreq();

        for (int i = 0; i < termList.size(); i++) {
            itemFreq.addItemsAndFreq(termList.get(i),1);
        }

        return itemFreq;
    }

    // 序列化文本并计算
    public Document serializeDocument(String text) {
        Document curDoc = new Document();
        if(true == curDoc.createDocument(text)) {
            // words arrays
            List<String> termList = this.segHandle.getWordsStrArrays(curDoc.getContents());
            // 词频列表
            /*ItemFreq wordsFreq = this.textTransWordsFreq(termList);
            if (wordsFreq == null) {
                return curDoc;
            }
            // 进行cutoff used words Frequency
            if(wordsFreq.totalNumber < 10 || wordsFreq.itemFre.size() < 10) {
                curDoc.setWordsFreq(wordsFreq);
                // 关键词topN列表
                Map<String,Float> curValue = new HashMap<String,Float>();
                for (String key: wordsFreq.itemFre.keySet()) {
                    float val = (float)wordsFreq.itemFre.get(key);
                    curValue.put(key,val);
                }
                curDoc.setDoucValue(curValue);
            }
            else {
                ItemFreq tempItem = new ItemFreq();
                for (String key : wordsFreq.itemFre.keySet()) {
                    int freq = wordsFreq.itemFre.get(key);
                    if(freq > wordFreCutoff) {
                        tempItem.addItemsAndFreq(key,freq);
                    }
                }
                curDoc.setWordsFreq(tempItem);

                // 关键词topN列表
                TextRankKeyword textRankWord = new TextRankKeyword();
                Map<String,Float> curValue = textRankWord.getTermRankTopN(termList,topWordsNum);
                curDoc.setDoucValue(curValue);
            }
            */
            // 只有一种方法，提高速度
            // 关键词topN列表
            TextRankKeyword textRankWord = new TextRankKeyword();
            Map<String,Float> curValue = textRankWord.getTermRankTopN(termList,topWordsNum);

            if (curValue == null) {
                return null;
            }
            else {
                System.out.println(curValue.size());
                curDoc.setDoucValue(curValue);
                FingerPrint fingerHandel = new FingerPrint();
                long  fingerScore = fingerHandel.fingerPrintMapScore(curValue);
                curDoc.setPrintStr(fingerScore);
                return  curDoc;
            }

        }
        else {
            return  null;
        }
    }

    /**
     * the system enterence
     * @param filePath
     * @param outfile
     * @return
     */
    public boolean getTopEvents(String filePath, String outfile) {

        List<String> fileArrays = FileTool.getFileNames(filePath);

        clusterArray.setWordsInfroHanle(this.wordsInfor);

        for (int k = 0; k < fileArrays.size(); k++) {
            String curFile = fileArrays.get(k);
            System.out.println("the current file is :" + curFile);
            this.stepFileEvents(curFile);

        }
        try {
            FileOutputStream fos = new FileOutputStream(new File(outfile));
            OutputStreamWriter os = new OutputStreamWriter(fos,"UTF-8");
            BufferedWriter bw = new BufferedWriter(os);
            List<DocumentItem>  topArray = clusterArray.getTopHotEvent(10);
            for (int idx = 0; idx < topArray.size(); idx++) {
                bw.write(topArray.get(idx).toString() + "\n");
            }

            /*for (DocumentItem item : clusterArray.getDocuments()) {
                bw.write(item.toString() + "\n");
            }
            */
            bw.close();
            os.close();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return  true;
    }

    /**
     * 处理单个文件
     * @param fileName      输入的文件
     * @return
     */
    public boolean stepFileEvents(String fileName) {
        if (fileName.equals("") == true) {
            return  false;
        }
        try {
            FileInputStream fis = new FileInputStream(fileName);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String textLine = "";
            int countLine = 0;
            while ((textLine = br.readLine()) != null) {
                if(countLine == 58) {
                    System.out.println("current line numbers " + countLine++);
                }

                if (textLine.equals("") == false && textLine.isEmpty() == false) {
                    Document document = this.serializeDocument(textLine);
                    if(document != null) {
                        //System.out.println(document.toString());
                        // 该处确定计算相似度使用的方法
                        // words frequency
                         //this.clusterArray.addToDocumentListWordsFreqIdf(document);
                        // key words array
                        // this.clusterArray.addToDocumentListKeyWords(document);
                        //
                        this.clusterArray.addToDocumentListSimFinger(document);
                    }
                }
            }
            br.close();
            isr.close();
            fis.close();
        }
        catch ( IOException e) {
            e.printStackTrace();
        }
        return  true;
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        long start = System.currentTimeMillis();

        String fileOutfile = "E:\\IntelliIDEA\\HotEventExtraction\\corpus1";
        String  outfile = "E:\\IntelliIDEA\\HotEventExtraction\\data\\res_cluster.txt";
        DocumentCluster docuCluster = new DocumentCluster();
        docuCluster.getTopEvents(fileOutfile,outfile);
        /*
        ItemFreq  rest = docuCluster.textTransWordsFreq("mrnoonanintroducedthespecialratein2011　　hoteliersandrestauranteurshavewelcomedthedecisiontoretainthereduced9pcvatrateinbudget2017.　　share　　restauranteursrejoiceatdecisiontoretainreduced9pcvatrate　　independent.ie　　hoteliersandrestauranteurshavewelcomedthedecisiontoretainthereduced9pcvatrateinbudget2017.　　http://www.independent.ie/business/budget/restauranteurs-rejoice-at-decision-to-retain-reduced-9pc-vat-rate-35121753.html　　http://www.independent.ie/incoming/article35121751.ece/892bc/autocrop/h342/knife-fork-generic.jpg　　email　　goto　　comments　　butmichaelnoonanappearedtofireanotherwarningshotacrossthebowofthetouristindustryinparticular,notingthattheeconomiccaseforthespecialratehasarguablydiminished.　　“thetourismandhospitalityindustryhasrecoveredwell,”hesaid.“it’snowperformingstrongly,dueinnosmallparttothereducedvatrateiintroducedinourlastterminoffice.”　　mrnoonanintroducedthespecialratein2011.　　“thoughtheeconomicrationaleformaintainingthisreducedratemaynotbeasstrongtoday,iconsideritwouldbeprudenttoretainthereducedrateinthisyear’sbudget,”headded.“thiswillactasabufferforthesectoragainsttheweaknessinsterling,whichincreasesthecostofholidayinginirelandforbritishtourists.”　　joedolan,presidentoftheirishhotelsfederation,saidthereducedvatratehasbeeninstrumentalintherecoveryofthetourismindustry.　　&nbsp;“thismeasurehasbeenthesinglemostimportantfiscalinitiativeforirishtourisminthelastdecadeandwearepleasedthegovernmenthasretainedtherate,”hesaid.　　onlineeditors");
        if(rest != null) {
            rest.printItemAndFre();
        }
        */
        System.out.println("需要 "+(System.currentTimeMillis()-start)+" 微秒");
    }
}
