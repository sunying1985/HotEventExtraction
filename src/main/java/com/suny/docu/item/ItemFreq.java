package com.suny.docu.item;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 * only store Item and its Frequency
 * @author  Frank suny
 *
 */

public class ItemFreq {

	/**
	 * store Item and Frequence 
	 */
	public Map<String,Integer> itemFre = null;
	
	/**
	 * the total number of the struct
	 */
	public int totalNumber = 0;
	
	/**
	* construct functions
	**/
	public ItemFreq() {
		itemFre = new HashMap<String,Integer>();
	}
	
	/**
	 * 
	 * @param word  the input word
	 * @param fre   the frequency of the word
	 * @return true if successful
	 */
	public boolean addItemsAndFreq(String word, int fre) {
		if (word.isEmpty() == true) {
			return false;
		}
		if (fre < 1) {
			return false;
		}
		
		if (this.itemFre.containsKey(word) == true) {
			
			int oldfre = this.itemFre.get(word);
			oldfre += fre;
			this.itemFre.put(word, oldfre);
			
		}
		else {
			this.itemFre.put(word, fre);	
		}
		this.totalNumber += fre;
		return true;
	}
	
	/**
	 * 
	 * @param word  update item freq
	 * @param fre   the frequency of the word
	 * @return true if successful
	 */
	public boolean updateItemFreq(String word, int fre) {
		if (word.isEmpty() == true) {
			return false;
		}
		if (fre < 1) {
			return false;
		}
		
		if (this.itemFre.containsKey(word) == true) {
			
			int oldfre = this.itemFre.get(word);
			if (oldfre == 1) {
				this.itemFre.remove(word);
			}
			else {
				oldfre -= fre;
				this.itemFre.put(word, oldfre);
			}
		}
		else {
			return false;	
		}
		this.totalNumber -= fre;
		return true;
	}
	
	/**
	 * delete item from collection
	 * @param word  the input words
	 * @return
	 */
	public boolean removeItems(String word) {
		if (this.itemFre.containsKey(word) != true) {
			return false;
		}
		int freq = this.itemFre.get(word);
		this.totalNumber -= freq;
		
		this.itemFre.remove(word);
		return true;
	}
	/**
	 * Static word array information
	 * @param itemArray  the store item array
	 * @return true if success
	 */
	public boolean StaticFrequence(Vector<String> itemArray) {
		if (itemArray.isEmpty() == true) {
			return false;
		}
		
		for (int i = 0; i < itemArray.size(); i++) {
			String tmpItem = itemArray.get(i);
			if (this.itemFre.containsKey(tmpItem) == true) {
				
				int oldFre = this.itemFre.get(tmpItem);
				oldFre++;
				
				this.itemFre.put(tmpItem, oldFre);
			}
			else {
				this.itemFre.put(tmpItem, 1);
			}
			this.totalNumber++;
		}
		if (this.itemFre.size() == 0) {
			return false;
		}
		return true;
	}
	
	/**
	 * Static word array information
	 * @param itemArray  the store item array
	 * @return true if success
	 */
	public boolean StaticFrequence(String [] itemArray) {
		if (itemArray.length == 0) {
			return false;
		}
		
		for (int i = 0; i < itemArray.length; i++) {
			if (this.itemFre.containsKey(itemArray[i]) == true) {
				
				int oldFre = this.itemFre.get(itemArray[i]);
				oldFre++;
				this.itemFre.put(itemArray[i], oldFre);
			}
			else {
				this.itemFre.put(itemArray[i], 1);
			}
			this.totalNumber++;
		}
		if (this.itemFre.size() == 0) {
			return false;
		}
		return true;
	}
	
	/**
	 * only for test
	 * display the item and freq
	 */
	public void printItemAndFre() {
		
		Iterator<String> myIter = this.itemFre.keySet().iterator();
		
		System.out.println("the Items total Number is:" + this.totalNumber);
		while (myIter.hasNext()) {
			
			String itemKey = myIter.next().toString();
			
			System.out.print(itemKey + "\t" + this.itemFre.get(itemKey) + ";");
		}
		System.out.println();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

        ItemFreq itemDict = new ItemFreq();
        Vector<String> testArray = new Vector<String>();
        
        testArray.add("沈阳");
        testArray.add("朝阳");
        testArray.add("昌平");
        testArray.add("北京");
        testArray.add("杭州市");
        testArray.add("北京市");
        testArray.add("北京");
        testArray.add("杭州市");
        testArray.add("北京");
        testArray.add("杭州市");
        testArray.add("北京市");
        testArray.add("北京");
        
		itemDict.StaticFrequence(testArray);
		itemDict.printItemAndFre(); 
		String key = "[北京欢迎你]";
		String initKey = key.substring(1,key.length() - 1);
		System.out.println(initKey);
		
	}
}
