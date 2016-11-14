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
            throw new InvalidParameterException("A similarity distance can't be computed on a null vector");
        }
        if (vector1.size() != vector2.size()) {
            throw new InvalidParameterException(
                    "A similarity distance can't be computed on two vectors of different dimension");
        }

        if (vector1.size() == 0) {
            throw new InvalidParameterException(
                    "A similarity distance can't be computed on two vectors without any dimension");
        }
        return computeDistance(vector1, vector2);
    }

    protected abstract double computeDistance(IndexVector vector1, IndexVector vector2);
}
