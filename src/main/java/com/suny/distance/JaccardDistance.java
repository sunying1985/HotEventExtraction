package com.suny.distance;


import com.suny.docu.item.IndexVector;
/** 
 * Class for caculating Jaccard distance between Vectors.
 *  */
public class JaccardDistance extends DistanceMetric {
	@Override
	public double calcDistance(IndexVector vector1, IndexVector vector2) {
		
		if (vector1 == null || vector2 == null) {
			return 0.0;
		}
		double innerProduct = vector1.innerProduct(vector2);
		if (Double.isNaN(innerProduct) == true) {
			return 0.0;
		}
		double vec_norm1 = vector1.norm();
		double vec_norm2 = vector2.norm();
		if (vec_norm1 == 0 || vec_norm2 == 0) {
			return 0.0;
		}
		
		return Math.abs(1 - innerProduct / (vec_norm1 + vec_norm2 - innerProduct));
	}
}
