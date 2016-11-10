package sortfreq;

import java.util.Comparator;
import java.util.Map.Entry;

public class MapDoubleValueComparator implements Comparator<Entry<String, Double>>{

	public int compare(Entry<String, Double> me1, Entry<String, Double> me2) {  
		  // sortfreq by the positive sequence
	      //return me1.getValue().compareTo(me2.getValue()); 
		  // The reverse sortfreq by value
		  return me2.getValue().compareTo(me1.getValue()); 
	 } 
}
