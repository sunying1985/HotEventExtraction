package com.suny.docu.item;

/**
 * Created by Frank Adolf
 * Date on 2016/11/4.
 * @note the item article
 *  Class containing an individual document.
 */
public class Document implements Comparable<Document> {

	private  static  final String splitTag = "\t";

	// article id
	private  long id;
	//  article title
	private  String title;
	//  the contents of parser article
	private String contents;

	//  the word vector
	private ItemFreq wordsFreq = null;

	public Document() {
		this.id = 0;
		this.title = "";
		this.contents = "";
	}

	public Document(long id, String contents, String title) {
		this.id = id;
		this.contents = contents;
		this.title = title;
	}

	/**
	 * @param  record    format is:id \t title \t content \t
	 */
	public boolean  createDocument(String record) {
		if(record.equals("") == true || record.isEmpty() == true) {
			return false;
		}
		else {
			String [] items = record.split(splitTag);
			if(items.length == 3) {
				this.setId(Long.parseLong(items[0]));
				this.setTitle(items[1]);
				this.setContents(items[2]);
				return true;
			}
			else {
				return false;
			}
		}
	}
	/**
	 * using text and id construct one document
	 * @param record          the text <content> and <title> 
	 * @param curDocumentID   the document id number
	 * @return
	 */
	public static Document createDocumentOne(String record, int curDocumentID) {
		Document document = null;
		int index = record.indexOf("\t");
		if (index == -1) {
			//  only have <content> 
			String contents = record;
			
			document = new Document(curDocumentID, contents, String.valueOf(curDocumentID));
		}
		else {
			//  <content> and <title> 
			String contents = record.substring(0,index);
			String title = record.substring(index + 1);
			document = new Document(curDocumentID, contents, title);
		}
		
		return document;
	}

	/**
	 *  Allow documents to be sorted by ID. 
	 * */
	@Override
	public int compareTo(Document documents) {
		if (getId() > documents.getId()) {
			return 1;
		} else if (id < documents.getId()) {
			return -1;
		} else {
			return 0;
		}
	}

	/** 
	 * Get the document ID. 
	 * */
	public long getId() {
		return id;
	}

	/**
	 * Get the document title.
	 * */
	public String getTitle() {
		return title;
	}
	/**
	 *  Get the document contents.
	 *  */
	public String getContents() {
		return contents;
	}


	public void setId(long id) {
		this.id = id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public ItemFreq getWordsFreq() {
		return wordsFreq;
	}

	public void setWordsFreq(ItemFreq wordsFreq) {
		this.wordsFreq = wordsFreq;
	}

	@Override
	public String toString() {
		// 不用管词语向量
		return "id:\t" + id  + "\t Title:\t" + title + "\tContents:\t " + contents;
	}
}
