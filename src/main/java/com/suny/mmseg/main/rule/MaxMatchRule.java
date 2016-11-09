package com.suny.mmseg.main.rule;

import com.suny.mmseg.main.Chunk;

/**
 * 源码来源： http://blog.chenlb.com/category/mmseg4j
 */

/**
 * Maximum Matching.<p/>
 * 
 * chuck中各个词的长度之和
 * 
 * @see http://technology.chtsai.org/mmseg/
 * 
 * @author suny 20140818
 */
public class MaxMatchRule extends Rule{

	private int maxLen;
	
	public void addChunk(Chunk chunk) {
		if(chunk.getLen() >= maxLen) {
			maxLen = chunk.getLen();
			super.addChunk(chunk);
		}
	}
	
	@Override
	protected boolean isRemove(Chunk chunk) {
		
		return chunk.getLen() < maxLen;
	}

	public void reset() {
		maxLen = 0;
		super.reset();
	}
}
