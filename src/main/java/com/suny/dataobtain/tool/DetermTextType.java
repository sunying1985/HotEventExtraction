package com.suny.dataobtain.tool;

/**
 * Created by Frank Adolf
 * Date on 2016/11/8.
 * 判定文本为全英文
 */
public class DetermTextType {

    public static  boolean strIsEnglish(String word) {
        boolean sign = true; // 初始化标志为为'true'
        for (int i = 0; i < word.length(); i++) {
            if (!(word.charAt(i) >= 'A' && word.charAt(i) <= 'Z')
                    && !(word.charAt(i) >= 'a' && word.charAt(i) <= 'z')) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断为数字或者英文字符
     * @param ch  characters
     * @return
     */
    public static  boolean isEnglishOrDigitChar(char ch) {

        if(true  == Character.isDigit(ch) ||
                true == Character.isLowerCase(ch) ||
                true == Character.isUpperCase(ch)) {

            return true;
        }
        return  false;
    }

    /**
     * 从文本中随机抽取3个位置进行判断
     * @param text
     * @return   true   all chinese text
     *           false  all english text
     */
    public static  boolean textType(String text) {
        int len = text.length();
        if(len < 3) {
            return  true;
        }
        int foreTag = (int)(Math.random()*len);
        int midTag = (int)(Math.random()*len);
        int tailTag = (int)(Math.random()*len);
        if(true == isEnglishOrDigitChar(text.charAt(foreTag)) &&
                true == isEnglishOrDigitChar(text.charAt(midTag)) &&
                true == isEnglishOrDigitChar(text.charAt(tailTag))) {
            return  false;
        }

        return  true;
    }
    public static void main(String[] args)  {
        char ch = '我';
        //ch = '6';
        //ch = 'A';
        System.out.println(DetermTextType.isEnglishOrDigitChar(ch));
    }
}
