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

    // 根据Unicode编码完美的判断中文汉字和符号
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
            || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
            || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
            || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
            || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
            || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
           return  true;
        }
        return false;
    }

    private static boolean isChineseJdk8(char c) {
        Character.UnicodeScript sc = Character.UnicodeScript.of(c);
        if (sc == Character.UnicodeScript.HAN) {
            return true;
        }
        return false;
    }
    // 根据UnicodeBlock方法判断中文标点符号
    public static boolean isPunctuation(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (// punctuation, spacing, and formatting characters
            ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
            // symbols and punctuation in the unified Chinese, Japanese and Korean script
            || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
            // fullwidth character or a halfwidth character
            || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
            // vertical glyph variants for east Asian compatibility
            || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS
            // vertical punctuation for compatibility characters with the Chinese Standard GB 18030
            || ub == Character.UnicodeBlock.VERTICAL_FORMS
            // ascii
            || ub == Character.UnicodeBlock.BASIC_LATIN
            ) {
            return true;
        } else {
            return false;
        }
    }


    private static Boolean isUserDefined(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.NUMBER_FORMS
            || ub == Character.UnicodeBlock.ENCLOSED_ALPHANUMERICS
            || ub == Character.UnicodeBlock.LETTERLIKE_SYMBOLS
            || c == '\ufeff'
            || c == '\u00a0'
            )
            return true;
        return false;
    }

    /**
     * 判断基本的中文字符
     * @param ch
     * @return
     */
    public static Boolean baseChineseStr(char ch) {
        return  ch > '\u4E00' && ch < '\u9Fa5';
    }

    /**
     * 判断字符串是否为乱码
     * @param text
     * @return     true is the messy code
     */
    public static Boolean textMessyCode(String text)  {
        int len = text.length();
        if(len < 3) {
            return  false;
        }
        int foreTag = (int)(Math.random()*len);
        int midTag = (int)(Math.random()*len);
        int tailTag = (int)(Math.random()*len);
        if(true == baseChineseStr(text.charAt(foreTag)) &&
                true == baseChineseStr(text.charAt(midTag)) &&
                true == baseChineseStr(text.charAt(tailTag))) {
            return  true;
        }

        return  false;
    }


    public static void main(String[] args)  {
        char ch = '我';
        //ch = '6';
        //ch = 'A';
        //System.out.println(DetermTextType.isEnglishOrDigitChar(ch));
        String text = "杈光�娓哥帺鈥濊竟鎵捐�姘戝畢琛岀獌	杈光�娓哥帺鈥濊竟鎵捐�姘戝畢琛岀獌";
        //String text = "为后000续全面施工创造了条件";
        //System.out.println(textMessyCode(text));

        char [] arr = text.toCharArray();
        for(int i = 0; i < arr.length; i++) {
            System.out.println(baseChineseStr(arr[i]));
        }

    }
}
