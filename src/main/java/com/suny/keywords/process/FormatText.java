package com.suny.keywords.process;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormatText {

	/**
	 * this class only for deal with the corpus text
	 * @author Aldof 
	 *
	 */
	 public String proceSpecialCharacter(String text) {
	    	
    	String destStr = "";
        if (text.isEmpty() == true || text == null) {
        	return destStr;
        }
        
        Pattern p = Pattern.compile("\\s*|\t|\r|\n");
        Matcher m = p.matcher(text);
        destStr = m.replaceAll("");
          
		return  destStr.toLowerCase();
	}

}
