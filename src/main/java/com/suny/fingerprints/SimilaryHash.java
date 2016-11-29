package com.suny.fingerprints;

import java.util.Arrays;

/**
 * Similarity Hash算法，
 * 其思想来自于Google的技术文章《Detecting Near-Duplicates for WebCrawling》
 * 作者为Charikar。
 */
public class SimilaryHash {
	
	private static final long[] Masks;
	
	static{
		Masks = new long[64];
		Masks[0] = 1;
		for (int i = 1; i < 64; i++) {
			Masks[i] = 2 * Masks[i - 1];
		}
	}
	
	public static int hash32(int[] hashValues, double[] weights) {
		if ((hashValues == null) || (weights == null)
				|| (hashValues.length != weights.length)) {
			throw new RuntimeException("Parameters are wrong.");
		}
		double[] V = new double[32];
		for (int i = 0; i < hashValues.length; i++) {
			int hashValue = hashValues[i];
			for (int j = 0; j < 32; j++) {
				if ((hashValue & Masks[j]) == Masks[j]) {
					V[j] += weights[i];
				} else {
					V[j] -= weights[i];
				}
			}
		}
		int f = 0;
		for (int i = 0; i < 32; i++) {
			if (V[i] > 0) {
				f |= Masks[i];
			}
		}
		return f;
	}

	public static long hash64(long[] hashValues, double[] weights) {
		if ((hashValues == null) || (weights == null)
				|| (hashValues.length != weights.length)) {
			throw new RuntimeException("Parameters are wrong.");
		}
		double[] V = new double[64];
		for (int i = 0; i < hashValues.length; i++) {
			long hashValue = hashValues[i];
			double weight = weights[i];
			for (int j = 0; j < 64; j++) {
				if ((hashValue & Masks[j]) == Masks[j]) {
					V[j] += weight;
				} else {
					V[j] -= weight;
				}
			}
		}
		long f = 0L;
		for (int i = 0; i < 64; i++) {
			if (V[i] > 0) {
				f |= Masks[i];
			}
		}
		return f;
	}

	/**
	 * 计算两个长整型数的海明距离。
	 */
	public static int hammingDistance32(int a, int b) {
		int dis = 0;
		int c = a ^ b;
		for (int i = 0; i < 32; i++) {
			if ((c & Masks[i]) == Masks[i]) {
				dis++;
			}
		}
		return dis;
	}
	
	/**
	 * 计算两个长整型数的海明距离。
	 */
	public static int hammingDistance64(long a, long b) {
		int dis = 0;
		long c = a ^ b;
		for (int i = 0; i < 64; i++) {
			if ((c & Masks[i]) == Masks[i]) {
				dis++;
			}
		}
		return dis;
	}
	
	
	public static void main(String[] args){
		String keys1 = "经济圈副中心;副中心;首都经济圈;城市;沧州;首都;北京;中心;发展;经济;京津;河北;地区;邯郸;规划;人口;保定;石家庄;候选城市;协同发展;";
		String keys2 = "经济圈副中心;副中心;首都经济圈;沧州;首都;北京;河北;中心;城市;石家庄;发展;经济;京津;地区;邯郸;规划;人口;保定;协同发展;产业;";
		String keys3 = "副中心;首都经济圈;经济圈副中心;沧州;城市;首都;北京;河北;中心;石家庄;发展;经济;京津;地区;规划;人口;保定;协同发展;产业;首都经济;";
		String keys4 = "中心,首都,城市,经济圈,沧州,北京,发展,经济,京津,人口";
		
		String[] keywords1 = keys1.split(";");
		String[] keywords2 = keys2.split(";");
		String[] keywords3 = keys3.split(";");
		String[] keywords4 = keys4.split(",");
		
	    long[] hashValues1 = new long[keywords1.length];
		for (int i = 0; i < keywords1.length; i++) {
			hashValues1[i] = MurmurHashs.hash64(keywords1[i]);
		}
		long[] hashValues2 = new long[keywords2.length];
		for (int i = 0; i < keywords2.length; i++) {
			hashValues2[i] = MurmurHashs.hash64(keywords2[i]);
		}
		long[] hashValues3 = new long[keywords3.length];
		for (int i = 0; i < keywords3.length; i++) {
			hashValues3[i] = MurmurHashs.hash64(keywords3[i]);
		}
		int[] hashValues4 = new int[keywords4.length];
		for (int i = 0; i < keywords4.length; i++) {
			hashValues4[i] = MurmurHashs.hash32(keywords4[i]);
		}
//		double[] weights1 = {2.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
//		double[] weights2 = { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
//		第一步：声明数组。
		double[] weights1 = new double[keywords1.length];
		double[] weights2 = new double[keywords2.length];
		double[] weights3 = new double[keywords3.length];
		double[] weights4 = new double[keywords4.length];
//		第二步：填充。(比如都初始化成3.14)
		Arrays.fill(weights1,1.0);
		Arrays.fill(weights2,1.0);
		Arrays.fill(weights3, 1.0);
		Arrays.fill(weights4, 1.0);
		
		long f1 = SimilaryHash.hash64(hashValues1, weights1);
		long f2 = SimilaryHash.hash64(hashValues2, weights2);
		long f3 = SimilaryHash.hash64(hashValues3, weights3);
		long f4 = SimilaryHash.hash32(hashValues4, weights4);
		
		System.out.println("fingerprint1 is " + f1);
		System.out.println("fingerprint2 is " + f2);
		System.out.println("fingerprint3 is " + f3);
		System.out.println("fingerprint4 is " + f4);
		System.out.println("1:2 hammingdistance is " + SimilaryHash.hammingDistance64(f1, f2));
		System.out.println("1:3 hammingdistance is " + SimilaryHash.hammingDistance64(f1, f3));
		System.out.println("2:3 hammingdistance is " + SimilaryHash.hammingDistance64(f2, f3));
		
	}
}
