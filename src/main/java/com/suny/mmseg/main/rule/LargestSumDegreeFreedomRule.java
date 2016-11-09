package com.suny.mmseg.main.rule;

import com.suny.mmseg.main.Chunk;

/**
 * 源码来源： http://blog.chenlb.com/category/mmseg4j
 */

/**
 * Largest Sum of Degree of Morphemic Freedom of One-Character. <p/>
 * 
 * 各单字词词频的对数之和*100
 * 
 * @see http://technology.chtsai.org/mmseg/
 * 
 * @author suny 20140818
 */
public class LargestSumDegreeFreedomRule extends Rule {

	private int largestSumDegree = Integer.MIN_VALUE;
	@Override
	public void addChunk(Chunk chunk) {
		if(chunk.getSumDegree() >= largestSumDegree) {
			largestSumDegree = chunk.getSumDegree();
			super.addChunk(chunk);
		}
	}

	@Override
	public void reset() {
		largestSumDegree = Integer.MIN_VALUE;
		super.reset();
	}

	@Override
	protected boolean isRemove(Chunk chunk) {
		
		return chunk.getSumDegree() < largestSumDegree;
	}

}
