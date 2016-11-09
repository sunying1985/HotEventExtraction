package com.suny.mmseg.main;

/**
 * 源码来源： http://blog.chenlb.com/category/mmseg4j
 */

/**
 * 句子, 在一大串字符中断出连续中文的文本.
 * 
 * @author suny 20140818
 */
public class Sentence {

	private char[] text;
	private int startOffset;
	
	private int offset;

	public Sentence() {
		text = new char[0];
	}
	
	public Sentence(char[] text, int startOffset) {
		reinit(text, startOffset);
	}

	private void reinit(char[] text, int startOffset) {
		this.text = text;
		this.startOffset = startOffset;
		offset = 0;
	}
	
	public synchronized char[] getText() {
		return text;
	}

	/** 句子开始处理的偏移位置 */
	public synchronized int getOffset() {
		return offset;
	}

	/** 句子开始处理的偏移位置 */
	public synchronized void setOffset(int offset) {
		this.offset = offset;
	}

	public synchronized void addOffset(int inc) {
		offset += inc;
	}

	/** 句子处理完成 */
	public synchronized boolean isFinish() {
		return offset >= text.length;
	}

	/** 句子在文本中的偏移位置 */
	public synchronized int getStartOffset() {
		return startOffset;
	}

	/** 句子在文本中的偏移位置 */
	public synchronized void setStartOffset(int startOffset) {
		this.startOffset = startOffset;
	}
	
	public String toString() {
		return String.valueOf(text); 
	}
}
