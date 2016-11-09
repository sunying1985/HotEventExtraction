package com.suny.mmseg.main;

/**
 * 源码来源： http://blog.chenlb.com/category/mmseg4j
 */


/**
 * 正向最大匹配的分词方式.
 * 
 * @author suny 20140818
 */
public class SimpleSeg extends Seg{
	
	public SimpleSeg(Dictionary dic) {
		super(dic);
	}

	public Chunk seg(Sentence sen) {
		Chunk chunk = new Chunk();
		char[] chs = sen.getText();
		for(int k=0; k<3&&!sen.isFinish(); k++) {
			int offset = sen.getOffset();
			int maxLen = 0;

			//有了 key tree 的支持可以从头开始 max match
			maxLen = dic.maxMatch(chs, offset);
			
			//chunk.words[k] = new Word(chs, sen.getStartOffset(), offset, maxLen+1);
			chunk.getWords()[k] = new Word(chs, sen.getStartOffset(), offset, maxLen+1);
			offset += maxLen + 1;
			sen.setOffset(offset);
		}
		
		return chunk;
	}
}
