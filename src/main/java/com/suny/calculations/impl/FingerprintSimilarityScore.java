package com.suny.calculations.impl;

import com.suny.distance.CosineDistance;
import com.suny.distance.DistanceMetric;
import com.suny.docu.IndexVector;

/**
 * Created by Frank Adolf
 * Date on 2016/11/11.
 * 使用语义指纹进行计算
 */
public class FingerprintSimilarityScore extends AbstractVectorSimilarityScore{

    @Override
    protected double computeDistance(IndexVector vectorA, IndexVector vectorB) {
        DistanceMetric distanceMetric = new CosineDistance();

        return distanceMetric.calcDistance(vectorA,vectorA);
    }
}
