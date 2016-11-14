package com.suny.calculations;

import com.suny.docu.IndexVector;

/**
 * Created by Frank Adolf
 * Date on 2016/11/11.
 */
public interface VectorSimilarity {
    double getSimilarityDistance(IndexVector vector1, IndexVector vector2);
}
