package com.suny.mmseg.main.rule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.suny.mmseg.main.Chunk;

/**
 * 源码来源： http://blog.chenlb.com/category/mmseg4j
 */

/**
 * 过虑规则的抽象类。
 * 
 * @author suny 20140818
 */
public abstract class Rule {
	
	// 20161020 Adolf Frank
	//protected List<Chunk> chunks;
	protected CopyOnWriteArrayList<Chunk> chunks;
	
	public synchronized void addChunks(List<Chunk> chunks) {
		for(Chunk chunk : chunks) {
			addChunk(chunk);
		}
	}
	
	/**
	 * 添加 chunk
	 * @throws NullPointerException, if chunk == null.
	 * @author suny 20140818
	 */
	public synchronized void addChunk(Chunk chunk) {
		chunks.add(chunk);
	}
	
	/**
	 * @return 返回规则过虑后的结果。
	 * @author 20161020 Adolf Frank
	 */
	/*public List<Chunk> remainChunks() {
		for(Iterator<Chunk> it=chunks.iterator(); it.hasNext();) {
			Chunk chunk = it.next();
			if(isRemove(chunk)) {
				it.remove();
			}
		}
		return chunks;
	}
	*/
	// synchronized static 方法
	public synchronized CopyOnWriteArrayList<Chunk> remainChunks() {
		/*
		for(Iterator<Chunk> it=chunks.iterator(); it.hasNext();) {
			Chunk chunk = it.next();
			if(isRemove(chunk)) {
				it.remove();
			}
		}
		*/
		for (int i = 0; i < chunks.size(); i++) {
			Chunk chunk = chunks.get(i);
			if (isRemove(chunk)) {
				chunks.remove(chunk);
			}
		}
		
		return chunks;
	}
	/**
	 * 判断 chunk 是否要删除。
	 * @author suny 20140818
	 */
	protected abstract boolean isRemove(Chunk chunk);
	
	public synchronized void reset() {
		//chunks = new ArrayList<Chunk>();
		chunks = new CopyOnWriteArrayList<Chunk>();
	}
}
