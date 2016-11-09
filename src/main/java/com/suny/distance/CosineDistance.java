package com.suny.distance;


import com.suny.document.item.IndexVector;

/**
 * Class for calculating cosine distance between Vectors. 
 * 计算两个向量的余弦相似度，值越大就表示越相似
 * */
public class CosineDistance extends DistanceMetric {
	@Override
	public double calcDistance(IndexVector vector1, IndexVector vector2) {
		double simScore = 0.0;
		
		if (vector1 == null || vector2 == null) {
			return simScore;
		}
		double vec_norm1 = vector1.norm();
		double vec_norm2 = vector2.norm();
		if (vec_norm1 == 0.0 || vec_norm2 == 0.0) {
			return simScore;
		}
		
		simScore = vector1.innerProduct(vector2);
		if (Double.isNaN(simScore) == true) {
			return 0.0;
		}
		else {
			// 余弦值越接近1，就表明夹角越接近0度，也就是两个向量越相似，
			// 夹角等于0，即两个向量相等，这就叫"余弦相似性"。
			simScore = simScore / vec_norm1 / vec_norm2;
		}

		return simScore;
	}
}
