package sortfreq;

import java.util.Comparator;
import java.util.Map.Entry;

public class MapIntegerValueComparator implements Comparator<Entry<String, Integer>>{
	public int compare(Entry<String, Integer> me1, Entry<String, Integer> me2) {  
		  // sortfreq by the positive sequence
	      //return me1.getValue().compareTo(me2.getValue()); 
		  // The reverse sortfreq by value
		  return me2.getValue().compareTo(me1.getValue()); 
	 } 
}
