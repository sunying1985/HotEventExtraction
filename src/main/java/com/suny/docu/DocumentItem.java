package com.suny.docu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Frank Adolf
 * Date on 2016/11/4.
 *
 */
public class DocumentItem {

    private Document curDoc = null;

    private List<Long> simIdArray = null;

    public DocumentItem() {
        this.curDoc = new Document();
        this.simIdArray = new ArrayList<Long>();
    }

    public Document getCurDoc() {
        return curDoc;
    }

    public void setCurDoc(Document curDoc) {
        this.curDoc = curDoc;
    }

    public List<Long> getSimIdArray() {
        return simIdArray;
    }

    public void setSimIdArray(List<Long> simIdArray) {
        this.simIdArray = simIdArray;
    }

    public void addSimlaryId(Long tempId) {
        this.simIdArray.add(tempId);
    }

    /**
     * get the similary document size
     * @return
     */
    public int getSimilarySize() {
        return this.simIdArray.size();
    }

    @Override
    public String toString() {
        return curDoc.toString() + "\t" + this.simIdArray.toString();
    }
 }
