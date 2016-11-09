package com.suny.dictionary;

/**
 * 系统配置参数加载类
 * 
 * @author Frank Adolf suny
 * @date   20150130 
 * @note   
 */
public class SystemParas {
	/***************   emotional  analysis dictionary        ****************/
	/**
	 * the chinese stop words
	 */
	public static String stop_word_dict = ReadConfigUtil.getValue("stop_word_dict_file");
	
	/**
	 * the absolute path of emotional dictionary 
	 */
	public static String emotion_posi_dict = ReadConfigUtil.getValue("positive_dict_file");
	
	public static String emotion_nega_dict = ReadConfigUtil.getValue("negative_dict_file");
	
	/**
	 * the absolute path of family name and frequency dictionary
	 */
	public static String family_name_fre_dict = ReadConfigUtil.getValue("family_name_freq_dict");
	
	/**
	 * Traditional and simplified different characters
	 */
	public static String trad_diff_simpl_dict = ReadConfigUtil.getValue("trad_simple_dict_file");
	
	/**
	 * Special words for judge the 
	 */
	public static String special_words_dict = ReadConfigUtil.getValue("special_word_dict_file");
	
	/**
	 * synonym words dictionary
	 */
	public static String synonym_words_dict = ReadConfigUtil.getValue("synonym_word_dict_file");
	
	
	/**
	 *  the disable and illegal words dictionary
	 */
	public static String disable_words_dict = ReadConfigUtil.getValue("disable_word_dict_file");
	
	/**
	 * the all words idf dictionary
	 */
	public static String idf_words_dict = ReadConfigUtil.getValue("idf_word_dict_file");
	/**
	 * current running file path
	 */
	public static String currentRunPath = null;
	
	/**
	 * construction function 
	 */
	public SystemParas() {
		// load all the dictionary file
		this.init();
	}
	
	/**
	 * this function using initinal the file path
	 */
	public  void init() {
		if (null == currentRunPath) {
			currentRunPath = System.getProperty("user.dir")+"/data_entity";
		}
		System.out.println(currentRunPath + "error");
		
		this.setDictName();
	}
	
	/**
	 * set the dictionary name automatic
	 * @param 
	 */
	public  void setDictName() {
		
		if (stop_word_dict.isEmpty() == true) {
			stop_word_dict = currentRunPath + "/" + "chinese_stopword";
		}
		
		// 
		if (emotion_posi_dict.isEmpty() == true) {
			emotion_posi_dict = currentRunPath + "/" + "posi_words";
		}
		if (emotion_nega_dict.isEmpty() == true) {
			emotion_nega_dict = currentRunPath + "/" + "nega_words";
		}
		if (family_name_fre_dict.isEmpty() == true) {
			family_name_fre_dict = currentRunPath + "/" + "family_name_fre";
		}
		if (trad_diff_simpl_dict.isEmpty() == true) {
			trad_diff_simpl_dict = currentRunPath + "/" + "trad_simp";
		}
		if (special_words_dict.isEmpty() == true) {
			special_words_dict = currentRunPath + "/" + "posi_words";
		}
		if (synonym_words_dict.isEmpty() == true) {
			synonym_words_dict = currentRunPath + "/" + "synonym_words";
		}
		if (disable_words_dict.isEmpty() == true) {
			disable_words_dict = currentRunPath + "/" + "disable_words";
		}
		if (idf_words_dict.isEmpty() == true) {
			idf_words_dict = currentRunPath + "/" + "words_idf_inform";
		}
	}
}
