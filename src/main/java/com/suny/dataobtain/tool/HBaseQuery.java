package com.suny.dataobtain.tool;


import com.peopleyuqing.article.Article;
import com.peopleyuqing.article.ArticleInterface;
import com.peopleyuqing.article.ArticleService;
import com.peopleyuqing.weibo.Weibo;
import com.peopleyuqing.weibo.WeiboInterface;
import com.peopleyuqing.weibo.WeiboService;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 从HBase中提取数据，相应的接口可根据需求自行实现
 * @author Frank Adolf
 * @date   20160505
 */
public class HBaseQuery {
	
	static ArticleInterface inter = null;
	
	static WeiboInterface weboInter = null;
	
	/**
	 * @deprecated				根据ID列表获取文章正文内容
	 * @param List<Long> 		id组成的集合
	 * @return List<String>		文章内容组成的集合
	 * */
	public static List<String> getContentListFromHbase(List<Long> list){
		List<String> dataList = null;
		try {
			inter = ArticleService.getService();
			dataList = inter.getContentList(list, 1);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return dataList;
	}
	
	/**
	 * @deprecated				从HBase中根据ID列表获取文章所有字段内容
	 * @param List<Long> 		id组成的集合
	 * @return List<Article>	所有文章组成的集合
	 * */
	public static List<Article> getArticleListFromHbase(List<Long> list){
		List<Article> dataList = null;
		try {
			inter = ArticleService.getService();
			//dataList = inter.getArticleListFromDB(list);
			// 带有HBase是正确的
			// 1449435785876616475   最后一个的ID的语义指纹为空
			dataList = inter.getArticleListFromHBase(list, 1);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return dataList;
	}

	/**
	 * @deprecated			根据ID集合获取所有微博的正文内容
	 * @param List<Long> 	id组成的集合
	 * @return List<String> 正文内容组成的集合
	 * */
	public static List<String> getWeiboContentListFromHbase(List<Long> list){
		List<String> dataList = new ArrayList<String>();
		try {
			weboInter = WeiboService.getService();
			List<Weibo> allContent = new ArrayList<Weibo>();
			
			allContent = weboInter.getWeiboListFromHBase(list);
			if(allContent != null && allContent.size() > 0){
				for (int i = 0; i < allContent.size(); i++) {
					dataList.add(allContent.get(i).getContent());
				}
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return dataList;
	} 
	
	
	/**
	 * @deprecated			根据ID集合获取每一篇微博的所有字段
	 * @param List<Long> 	id组成的集合
	 * @return List<Weibo> 	返回的所有微博集合
	 * */
	public static List<Weibo> getWeiboListFromHbase(List<Long> list){
		
		List<Weibo> weiboList = new ArrayList<Weibo>();
		try {
			weboInter = WeiboService.getService();
			
			weiboList = weboInter.getWeiboListFromHBase(list);
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return weiboList;
	} 

}
