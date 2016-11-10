package com.suny.dataobtain.fatch;

import com.peopleyuqing.article.Article;
import com.peopleyuqing.weibo.Weibo;
import com.suny.dataobtain.tool.FileTool;
import com.suny.dataobtain.tool.HBaseQuery;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Adolf Frank
 * 20160505
 */
/**
* 供离线计算数据
* 使用HBase接口，通过ID获取Article的相关字段，
* 然后进行统计工作，如对文章的正文进行情感分析/热点发现等
*/

public class UsedIDGetArticleFromHBase {
	
	public UsedIDGetArticleFromHBase() {
		this.init();
	}
	
	private void init() {
	}

    /**
     * 获取文章的信息
     * @param infilePath
     * @return
     */
	public boolean getArticleFileArray(String infilePath) {
		 
		 if (infilePath.isEmpty() == true) {
			 return false;
		 }
		 List<String> fileArray = FileTool.getFileNames(infilePath);
		 for (int i = 0; i < fileArray.size(); i++) {
			String fileName = fileArray.get(i);
    		
    		 String outfile = fileName.substring(0,fileName.length() - 5) + "_res.txt";
    		 System.out.println("输出文件名称：\t" + outfile);
    		 
    		 this.getArticleContentUsedID(fileName, outfile);
		 }
		 
		 return true;
	}
	 
	/**
     * 获微博数据的信息
     * @param infilePath  文件路径
     *                        2.获取所有ID
     * @return
     */
	public boolean getWeiboFileArray(String infilePath) {
		 
		 if (infilePath.isEmpty() == true) {
			 return false;
		 }
		 List<String> fileArray = FileTool.getFileNames(infilePath);
		 for (int i = 0; i < fileArray.size(); i++) {
			String fileName = fileArray.get(i);
    		
    		 String outfile = fileName.substring(0,fileName.length() - 4) + "_res.txt";
    		 System.out.println("输出文件名称：\t" + outfile);
			 this.getWeiboContentUsedID(fileName, outfile);
		 }
		 
		 return true;
	}


	/**
	 * 根据文章的ID从HBase中获取文章相应字段内容
	 * 根据id 提取相关的信息
	 * @param infile
	 * @param filePath
	 */
	public void getArticleContentUsedID(String infile, String filePath) {
		FileOutputStream fos = null;
	   	OutputStreamWriter os = null;
	   	BufferedWriter bw = null;
  	
		// 获取所有ID
		List<Long> idArray = new ArrayList<Long>();
		idArray = FileTool.readFileByFilePath(infile,0);
		System.out.println("ionit size" + idArray.size());
		if (idArray.size() > 0) {
			List<Long> timesArr = new ArrayList<Long>();
			int nameCount = 0;
			for (int i = 1; i<= idArray.size(); i++) {
				if(i%1000 == 0) {
					nameCount++;
					try {
						String outfile = filePath + "\\" + nameCount + "_res.txt";
						fos = new FileOutputStream(new File(outfile),true);
					   	os = new OutputStreamWriter(fos,"UTF-8");
					   	bw = new BufferedWriter(os);
						timesArr.add(idArray.get(i - 1));
						List<Article> textArray = HBaseQuery.getArticleListFromHbase(timesArr);
						
						System.out.println("article size" + textArray.size());
						if(textArray != null){
							for (int k =0; k < textArray.size(); k++) {
								// 根据具体需求获取Article的相关字段
								String contentText = this.proceSpecialCharacter(textArray.get(k).getContent());
								
								bw.write(textArray.get(k).getId() + "\t" + proceSpecialCharacter(textArray.get(k).getTitle()) + "\t" +contentText + "\n");
							}
						}
						bw.flush();	
						timesArr.clear();
					}
					catch (IOException e) {
						e.printStackTrace();
					}finally{
						try {
							if(bw != null){
								
								bw.close();
							}
							if(os != null){
								os.close();
							}
							if(fos != null){
								fos.close();
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				else {
					timesArr.add(idArray.get(i - 1));
				}
			}
			
			try {
				String outfile = filePath + "\\" + nameCount++ + "_res.txt";
				fos = new FileOutputStream(new File(outfile),true);
			   	os = new OutputStreamWriter(fos,"UTF-8");
			   	bw = new BufferedWriter(os);
			   	List<Article> textArray = HBaseQuery.getArticleListFromHbase(timesArr);
				System.out.println("article size" + textArray.size());
				if(textArray != null){
					for (int k =0; k < textArray.size(); k++) {
						// 根据具体需求获取Article的相关字段
						String contentText = this.proceSpecialCharacter(textArray.get(k).getContent());

						bw.write(textArray.get(k).getId() + "\t" + proceSpecialCharacter(textArray.get(k).getTitle()) + "\t" +contentText + "\n");
					}
				}
				bw.flush();	
				timesArr.clear();
			}
			catch (IOException e) {
				e.printStackTrace();
			}finally{
				try {
					if(bw != null){
						
						bw.close();
					}
					if(os != null){
						os.close();
					}
					if(fos != null){
						fos.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}	
		}		
	}
	
	/**
	 * 根据微博的ID从HBase中获取文章相应字段内容
	 * ID范围在千条之内，ID较少
	 * @param infile
	 * @param outfile
	 */
	public void getWeiboContentUsedID(String infile, String outfile) {
		FileOutputStream fos = null;
	   	OutputStreamWriter os = null;
	   	BufferedWriter bw = null;
		
		try {
			fos = new FileOutputStream(new File(outfile),true);
		   	os = new OutputStreamWriter(fos,"UTF-8");
		   	bw = new BufferedWriter(os);
		   	
			// 获取所有ID
			List<Long> idArray = new ArrayList<Long>();
			idArray = FileTool.readFileByFilePath(infile,0);
			if (idArray.size() > 0) {
				System.out.println("ionit size" + idArray.size());
				List<Weibo> textArray = HBaseQuery.getWeiboListFromHbase(idArray);
				System.out.println("article size" + textArray.size());
				if(textArray != null && textArray.size() > 0){
					for (int k =0; k < textArray.size(); k++) {
						
						String contentText = textArray.get(k).getContent();
						/*
						this.textAnaHandle.getEmotionsValueAndWords(contentText);
						double emScore = this.textAnaHandle.getTendencyVlue();
						//bw.write(emScore + "\n");
						*/
						// 根据具体需求获取Article的相关字段
						bw.write( textArray.get(k).getWeiboid()+ "\t" +
								textArray.get(k).getContent() + "\t" +
								textArray.get(k).getWeibourl() + "\n");

					}
				}
				bw.flush();	
			}			
		}
		catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(bw != null){
					
					bw.close();
				}
				if(os != null){
					os.close();
				}
				if(fos != null){
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
	}
	
	/***
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
	

	/**
	 * 20161028 对id进行排重，然后抽取指定数据的id
	 * @param infile
	 * @param outfile
	 * @return
	 */
	public boolean filterIDFromFile(String infile,String outfile) {
		
		Set<String>  dictid = new HashSet<String>();
    	if (infile.isEmpty() == true || infile.equals("") == true) {
    		return false;
    	}
    	try {
	    	FileInputStream fis = new FileInputStream(infile);
			InputStreamReader isr = new InputStreamReader(fis,"UTF-8");
			BufferedReader br = new BufferedReader(isr);
			String textLine = "";  
			int count = 0;
			while (( textLine = br.readLine()) != null) {
				textLine = textLine.trim();
				/*count++;
				if(count%5 == 0) {
					if(textLine.isEmpty() == false && textLine.equals("") == false) {
						dictid.add(textLine);
					}
				}
				*/
				if(textLine.isEmpty() == false && textLine.equals("") == false) {
					dictid.add(textLine);
				}
			}
			br.close();
			isr.close();
			br.close();
			System.out.println("总数大小：\t" + dictid.size());
	   }
       catch (IOException e) {
    	   e.printStackTrace();
	   }
    	FileOutputStream fos = null;
	   	OutputStreamWriter os = null;
	   	BufferedWriter bw = null;
		
		try {
			fos = new FileOutputStream(new File(outfile),true);
		   	os = new OutputStreamWriter(fos,"UTF-8");
		   	bw = new BufferedWriter(os);
		   	for(String key : dictid) {
		   		bw.write(key + "\n");
		   	}
		   	
		}
		catch (IOException e) {
	    	   e.printStackTrace();
		}
		finally{
			try {
				if(bw != null){
					
					bw.close();
				}
				if(os != null){
					os.close();
				}
				if(fos != null){
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
    	return true;
	 }
}
