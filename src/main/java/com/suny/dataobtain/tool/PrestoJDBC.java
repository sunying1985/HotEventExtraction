package com.suny.dataobtain.tool;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 利用presto接口，以jdbc的形式连接数据库
 * @author lilingchen
 * @date   20160706
 * @note   使用JDK1.8进行编译
 */
public class PrestoJDBC {
	private static String driverName = "com.facebook.presto.jdbc.PrestoDriver";
	private static String url = "jdbc:presto://10.38.11.1:8080/hive/data_center";
	private Connection connection = null;
	private Statement createStatement = null;
	private ResultSet rs = null;

	static {
		try {
			Class.forName(driverName);
		} catch (ClassNotFoundException e) {
			System.out.println("加载驱动类错误");
			System.out.println(e.getMessage());
		}
	}

	private Connection getConnection() {
		try {
			connection = DriverManager.getConnection(url,"root","null");
		} catch (SQLException e) {
			System.out.println("连接获取异常");
			System.out.println(e.getMessage());
		}
		return connection;
	}

	public int queryOneInt(String sql,String tablename){
		int result = 0;
		Class clazz = null;
		if(tablename==null||"".equals(tablename)){
			System.out.println("未指定表名");
			return result;
		}else if(tablename.equals("article_info")){
			try {
				clazz = Class.forName("com.peopleyuqing.bean.ArticleBean");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}else if(tablename.equals("weibo_info")){
			try {
				clazz = Class.forName("com.peopleyuqing.bean.WeiboBean");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		 
		try {
			connection= this.getConnection();
			createStatement = connection.createStatement();
			rs = createStatement.executeQuery(sql);
			while(rs.next()){
				result = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			try {
				createStatement.close();
			} catch (SQLException e) {
				System.out.println("statement关闭异常");
				e.printStackTrace();
			}finally{
				try {
					connection.close();
				} catch (SQLException e) {
					System.out.println("连接关闭异常");
					e.printStackTrace();
				}
			}
		}
		
		return result;
	}
	
	public Map<String,Map<String, String>> excuteQueryMap(String sql, String tablename,
														  String[] columns) {
		
		Class clazz =null;
		if(tablename==null||"".equals(tablename)){
			System.out.println("未指定表名");
			return null;
		}else if(tablename.equals("article_info")){
			
			try {
				clazz = Class.forName("com.peopleyuqing.bean.ArticleBean");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}else if(tablename.equals("weibo_info")){
			
			try {
				clazz = Class.forName("com.peopleyuqing.bean.WeiboBean");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		Map<String,Map<String, String>> resultMap = new HashMap<String, Map<String,String>>();
		//List<Map<String, String>> artlist = new ArrayList<Map<String, String>>();
		try {
			connection = this.getConnection();
			createStatement = connection.createStatement();
			rs = createStatement.executeQuery(sql);
			while (rs.next()) {
				
				Map<String, String> dataMap = new HashMap<String, String>();
				for (int i = 0; i < columns.length; i++) {
					String columName = columns[i];
					String strvalue = null;
					try {
						Field field = clazz.getDeclaredField(columName);// 根据字段名通过反射获取bean的属性
						
						if (field.getType() == String.class) {
							if (columName.equals("content")) {
								strvalue = rs.getString(columName);
								strvalue = replace(strvalue,"\r|\n","<br/>");//把文章中的换行符转换
							}else{
								
								strvalue = rs.getString(columName);
								if(strvalue==null||("null").equals(strvalue)||("NULL").equals(strvalue)){
									strvalue="";
								}else if(!"".equals(strvalue)){
									strvalue.replaceAll("\t", "    ");//把字段中的分隔符  替换成空格
								}
							}
						}else if(field.getType()==int.class){
							int v = rs.getInt(columName);
							strvalue=String.valueOf(v);
						}else if(field.getType()==float.class){
							float v = rs.getFloat(columName);
							strvalue = String.valueOf(v);
						}else if(field.getType()==long.class){
							long v = rs.getLong(columName);
							strvalue = String.valueOf(v);
						}else if(field.getType()==double.class){
							double v = rs.getDouble(columName);
							strvalue = String.valueOf(v);
						}
						dataMap.put(columName, strvalue);
					} catch (SecurityException e) {
						System.out.println("字段不是public");
						System.out.println(e.getMessage());
					} catch (NoSuchFieldException e) {
						System.out.println("没有获取到对应字段");
						System.out.println(e.getMessage());
					}
				}
				resultMap.put(dataMap.get("id"),dataMap);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			
			try {
				createStatement.close();
			} catch (SQLException e) {
				
				System.out.println("statement关闭异常");
				e.printStackTrace();
			}finally{
				try {
					connection.close();
				} catch (SQLException e) {
					
					System.out.println("连接关闭异常");
					e.printStackTrace();
				}
			}
		}
		return resultMap;
	}	
	
	/**
	 * 查询SQL 目前限制10000条
	 * @param sql
	 * @param tablename  article_info文章表   weibo_info微博表
	 * @param columns
	 * @return
	 */
	public List<Map<String, String>> excuteQuery(String sql, String tablename,
												 String[] columns) {
		Class clazz =null;
		if(tablename==null||"".equals(tablename)){
			System.out.println("未指定表名");
			return null;
		}else if(tablename.equals("article_info")){
			try {
				clazz = Class.forName("com.peopleyuqing.bean.ArticleBean");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}else if(tablename.equals("weibo_info")){
			try {
				clazz = Class.forName("com.peopleyuqing.bean.WeiboBean");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		List<Map<String, String>> artlist = new ArrayList<Map<String, String>>();
		try {
			connection = this.getConnection();
			createStatement = connection.createStatement();
			rs = createStatement.executeQuery(sql);
			while (rs.next()) {
				Map<String, String> dataMap = new HashMap<String, String>();
				for (int i = 0; i < columns.length; i++) {
					String columName = columns[i];
					String strvalue = null;
					try {
						Field field = clazz.getDeclaredField(columName);// 根据字段名通过反射获取bean的属性
						if (field.getType() == String.class) {
							if (columName.equals("content")) {
								strvalue = rs.getString(columName);
								strvalue = replace(strvalue,"\r|\n","<br/>");//把文章中的换行符转换
							}else{
								strvalue = rs.getString(columName);
								if(strvalue==null||("null").equals(strvalue)||("NULL").equals(strvalue)){
									strvalue="";
								}else if(!"".equals(strvalue)){
									strvalue.replaceAll("\t", "    ");//把字段中的分隔符  替换成空格
								}
							}
						}else if(field.getType()==int.class){
							int v = rs.getInt(columName);
							strvalue=String.valueOf(v);
						}else if(field.getType()==float.class){
							float v = rs.getFloat(columName);
							strvalue = String.valueOf(v);
						}else if(field.getType()==long.class){
							long v = rs.getLong(columName);
							strvalue = String.valueOf(v);
						}else if(field.getType()==double.class){
							double v = rs.getDouble(columName);
							strvalue = String.valueOf(v);
						}
						dataMap.put(columName, strvalue);
					} catch (SecurityException e) {
						System.out.println("字段不是public");
						System.out.println(e.getMessage());
					} catch (NoSuchFieldException e) {
						System.out.println("没有获取到对应字段");
						System.out.println(e.getMessage());
					}
				}
				artlist.add(dataMap);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			
			try {
				createStatement.close();
			} catch (SQLException e) {
				System.out.println("statement关闭异常");
				e.printStackTrace();
			}finally{
				try {
					connection.close();
				} catch (SQLException e) {
					System.out.println("连接关闭异常");
					e.printStackTrace();
				}
			}
		}
		return artlist;
	}

	
	/**
	 * 根据语义指纹获取相似文章数或者转载数目
	 * @param sql		
	 * @param heads			
	 * @return				属于特殊处理，按着Heads的内容确定对结果输出进行修改
	 * @author Adolf Frank  20160630  特殊需求
	 * @throws IOException 
	 */
	public void queryFigerTimesByPresto(String sql,String[] heads,Set<String> dict,BufferedWriter bw) throws IOException{
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pstm = null;
		System.out.println("Running SQL: " + sql);
		try {
			connection = this.getConnection();
			createStatement = connection.createStatement();
			rs = createStatement.executeQuery(sql);
			int count =0;
			if(rs != null){
				while(rs.next()){
					count++;
					if(count%1000 == 0) {
						System.out.println("run line numbers\t" + count);
						bw.flush();
					}
					String print = rs.getString(heads[0]);
					if (print != null && print.length() > 2) {
						if(dict.contains(print) == true){
							bw.write(print + "\n");
						}
					}
				}
			}else{
				System.out.println("no date!");
			}
		}catch (SQLException e1) {
			e1.printStackTrace();
		}finally{
			try {
				if(rs != null){
					rs.close();
				}
				if(pstm != null){
					pstm.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 根据id获取复合条件的语义指纹
	 * @param sql		sql语句
	 * @param heads		输出文件标头
	 * @param dict		id集合
	 * @param bw		输出文件
     * @throws IOException
     */
	public void queryUsedIDGetFigerByPresto(String sql,String[] heads,Set<String> dict,BufferedWriter bw) throws IOException{
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pstm = null;
		System.out.println("Running SQL: " + sql);
		try {
			connection = this.getConnection();
			createStatement = connection.createStatement();
			rs = createStatement.executeQuery(sql);
			int count =0;
			if(rs != null){
				while(rs.next()){
					count++;
					if(count%1000 == 0) {
						System.out.println("run line numbers\t" + count);
						bw.flush();
					}
					String id = rs.getString(heads[0]);
					String print = rs.getString(heads[1]);
					if (id != null && id.length() > 2) {
						if(dict.contains(id) == true){
							if (print!= null && print.isEmpty() == false) {
								bw.write(id + "\t" + print + "\n");
							}
							else {
								System.out.println(id + "\t" + print + "\n");
							}
						}
					}
				}
			}else{
				System.out.println("no date!");
			}
		}catch (SQLException e1) {
			e1.printStackTrace();
		}finally{
			try {
				if(rs != null){
					rs.close();
				}
				if(pstm != null){
					pstm.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取符合条件的ID并保存到文件中
	 * @param sql		
	 * @param heads			
	 * @return				属于特殊处理，按着Heads的内容对结果输出
	 * @author Adolf Frank  20161107
	 * @throws IOException 
	 */
	public void queryIdByPresto(String sql,String[] heads,BufferedWriter bw) throws IOException{
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pstm = null;
		System.out.println("Running SQL: " + sql);
		try {
			connection = this.getConnection();
			createStatement = connection.createStatement();
			rs = createStatement.executeQuery(sql);
			if(rs != null){
				while(rs.next()){
					String id = rs.getString(heads[0]);

					if (id.equals("") == false) {
						bw.write(id + "\n");
					}
				}
				bw.flush();
				
			}else{
				System.out.println("no date!");
			}
		}catch (SQLException e1) {
			e1.printStackTrace();
		}finally{
			try {
				if(rs != null){
					rs.close();
				}
				if(pstm != null){
					pstm.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取符合条件的ID并保存到结果集合中
	 * @param sql
	 * @param heads
	 * @return				id结果集合
	 * @author Adolf Frank  20161107
	 * @throws IOException
	 */
	public Set<String> queryIdSetsByPresto(String sql,String[] heads){

		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pstm = null;
		System.out.println("Running SQL: " + sql);
		Set<String> idSet = new HashSet<String>();
		try {
			connection = this.getConnection();
			createStatement = connection.createStatement();
			rs = createStatement.executeQuery(sql);
			if(rs != null){
				while(rs.next()){
					String id = rs.getString(heads[0]);
					idSet.add(id);
				}
			}else{
				System.out.println("no date!");
			}
		}catch (SQLException e1) {
			e1.printStackTrace();
		}finally{
			try {
				if(rs != null){
					rs.close();
				}
				if(pstm != null){
					pstm.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return  idSet;
	}

	private String replace(String source,String regex1,String regex2){
		String result= "";
		if(source ==null){
			return result;
		}
		Pattern p = Pattern.compile(regex1);
		Matcher m = p.matcher(source);
		result = m.replaceAll(regex2);
		return result;
	}
}
