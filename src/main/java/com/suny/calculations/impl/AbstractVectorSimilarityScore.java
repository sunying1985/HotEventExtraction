package com.suny.calculations.impl;

import com.suny.calculations.VectorSimilarity;
import com.suny.docu.IndexVector;

import java.security.InvalidParameterException;

/**
 * Created by Frank Adolf
 * Date on 2016/11/11.
 * @note 考虑到速度上的差别，实现了3种方法
 */
public abstract class AbstractVectorSimilarityScore implements VectorSimilarity {
    public double getSimilarityDistance(IndexVector vector1, IndexVector vector2) {
        if (vector1 == null || vector2 == null) {
            return 0.0;
        }
        if (vector1.size() != vector2.size()) {
            return 0.0;
        }

        if (vector1.size() == 0) {
            return 0.0;
        }
        return computeDistance(vector1, vector2);
    }

    protected abstract double computeDistance(IndexVector vector1, IndexVector vector2);
}
