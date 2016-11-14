package com.suny.calculations.impl;


import com.suny.distance.CosineDistance;
import com.suny.distance.DistanceMetric;
import com.suny.docu.IndexVector;

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


}
