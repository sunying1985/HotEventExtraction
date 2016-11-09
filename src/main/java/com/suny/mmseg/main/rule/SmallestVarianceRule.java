package com.suny.mmseg.main.rule;

import com.suny.mmseg.main.Chunk;

/**
 * 源码来源： http://blog.chenlb.com/category/mmseg4j
 */

/**
 * Smallest Variance of Word Lengths.<p/>
 * 
 * 标准差的平方
 * 
 * @see http://technology.chtsai.org/mmseg/
 * 
 * @author suny 20140718
 */
public class SmallestVarianceRule extends Rule {

	private double smallestVariance = Double.MAX_VALUE;
	
	@Override
	public void addChunk(Chunk chunk) {
		if(chunk.getVariance() <= smallestVariance) {
			smallestVariance = chunk.getVariance();
			super.addChunk(chunk);
		}
	}

	@Override
	public void reset() {
		smallestVariance = Double.MAX_VALUE;
		super.reset();
	}

	@Override
	protected boolean isRemove(Chunk chunk) {
		
		return chunk.getVariance() > smallestVariance;
	}

}
