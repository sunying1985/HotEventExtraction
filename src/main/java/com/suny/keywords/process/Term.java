/**
 * 保存term的权重
 * 包含：文档频率/权重
 *     Word的相关内容
 */
package com.suny.keywords.process;


public class Term extends WordItem {
    private int df;
    private double weight;

    public Term() {
    }

    public Term(WordItem word) {
        super(word);
    }

    public String toString() {
        return super.toString() + ",Term[df=" + this.df + ", weight=" + this.weight + "]";
    }

    public int getDf() {
        return this.df;
    }

    public void setDf(int df) {
        this.df = df;
    }

    public double getWeight() {
        return this.weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
    // 测试函数
    public static void main(String[] args) {
        WordItem word = new WordItem("泪奔",3);
        Term term = new Term(word);
        System.out.println(term);
        
       
        term.setWordStr("天涯");
        term.setWordFreq(12);
        term.setDf(1);
        System.out.println(term);
    }
}