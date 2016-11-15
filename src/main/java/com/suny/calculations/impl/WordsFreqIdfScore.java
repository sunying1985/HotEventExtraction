package com.suny.calculations.impl;


import com.suny.distance.CosineDistance;
import com.suny.distance.DistanceMetric;
import com.suny.docu.IndexVector;
import com.suny.fingerprints.SimilaryHash;

/**
 * Created by Frank Adolf
 * Date on 2016/11/11.
 * 主要利用词语*IDF加权结果进行计算，所有的关键词全算
 */
public class WordsFreqIdfScore extends AbstractVectorSimilarityScore{
    @Override
    protected double computeDistance(IndexVector vectorA, IndexVector vectorB) {
        DistanceMetric distanceMetric = new CosineDistance();

        return distanceMetric.calcDistance(vectorA,vectorA);
    }

    /**
     * 2015.06.01
     * 计算两个语义指纹的相似度值
     * @param fp1,fp2  语义指纹
     * @return 返回两个语义指纹的相似度值
     */
    public double similary2FingerPrint(long fp1,long fp2){

        if(fp1 == 0 || fp2 == 2) {
            return  0.0;
        }
        int distance = 0;
        double sim = 0;
        distance = SimilaryHash.hammingDistance64(fp1, fp2);

        sim = 1 - ((double)distance)/(double)64 ;
        sim = (double)Math.round(sim*1000)/1000 ;

        return sim;
    }


}
