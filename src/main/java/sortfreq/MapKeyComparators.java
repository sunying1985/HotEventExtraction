package sortfreq;

import java.util.Comparator;

/** 
 * the class only compare the key of the map 
 * Frank suny    20141115
 */
public class MapKeyComparators implements Comparator<String>{

	public int compare(String str1, String str2) {  
		return str1.compareTo(str2);  
	}
}
