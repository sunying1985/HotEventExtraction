package com.suny.keywords.process;

/**
 * Created by Frank Adolf
 * Date on 2016/11/15.
 */
public class TitleFilter {

    private static final String [] wordsSuffix = {"无标题","广告","图片","公告"};

    public  static  boolean containsFilters(String item) {

        for (int i = 0; i < wordsSuffix.length; i++) {

            if(item.indexOf(wordsSuffix[i]) != -1) {
                return  true;
            }
        }
        return false;
    }
}
