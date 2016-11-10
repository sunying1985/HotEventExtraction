package com.suny.docu.item;

import com.suny.dataobtain.tool.DetermTextType;
import com.suny.dataobtain.tool.FileTool;
import com.suny.mmseg.interfa.MMsegInterface;
import com.suny.tfidf.WordsInformWeights;


import java.io.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Frank Adolf
 * Date on 2016/11/7.
 */
public class DocumentCluster {

    private MMsegInterface segHandle = MMsegInterface.getInstance();

    private WordsInformWeights wordsInfor = WordsInformWeights.getInstance();

    public  DocumentCluster() {

    }

    /**
     * transform the content into words index
     * @param text    the article context
     */
    public ItemFreq textTransWordsFreq(String text) {
        if (text.length() < 6 || false == DetermTextType.textType(text) ) {
            return  null;
        }
        // segment pos index
        int [] segPos = this.segHandle.textWordSegLable(text);
        //System.out.println(text);
        int  j = 0;
        int textLength = text.length();
        ItemFreq itemFreq = new ItemFreq();
        while (segPos[j] != 0 && j < textLength) {
            //System.out.println("j\t" + j);
            if (j == 0) {
                String curWord = text.substring(0,segPos[j]);
                if(false == this.wordsInfor.isStopWords(curWord) && curWord.length() > 1) {
                    itemFreq.addItemsAndFreq(curWord,1);
                }
            }
            else {
                String curWord = text.substring(segPos[j - 1],segPos[j]);
                if(false == this.wordsInfor.isStopWords(curWord) && curWord.length() > 1 ) {
                    itemFreq.addItemsAndFreq(curWord,1);
                }
            }
            j++;
        }

        return itemFreq;
    }

    public Document serializeDocument(String text) {
        Document curDoc = new Document();
        if(true == curDoc.createDocument(text)) {
            ItemFreq wordsFreq = this.textTransWordsFreq(curDoc.getContents());
            if (wordsFreq == null) {
                return curDoc;
            }
            // 进行cutoff used words Frequency
            if(wordsFreq.totalNumber < 10 || wordsFreq.itemFre.size() < 10) {
                curDoc.setWordsFreq(wordsFreq);
            }
            else {
                ItemFreq tempItem = new ItemFreq();
                for (String key : wordsFreq.itemFre.keySet()) {
                    int freq = wordsFreq.itemFre.get(key);
                    if(freq > 2) {
                        tempItem.addItemsAndFreq(key,freq);
                    }
                }
                curDoc.setWordsFreq(tempItem);
            }
            return  curDoc;
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
        DocumentList  clusterArray = new DocumentList();
        clusterArray.setWordsInfroHanle(this.wordsInfor);

        for (int k = 0; k < fileArrays.size(); k++) {
            String curFile = fileArrays.get(k);
            this.stepFileEvents(curFile,clusterArray);
            System.out.println("the current file is :" + curFile);
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
     * @param arrCluster           累加的结果集合
     * @return
     */
    public boolean stepFileEvents(String fileName,DocumentList arrCluster) {
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
                    //System.out.println(document.toString());
                    arrCluster.addToDocumentList(document);
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
    }
}
