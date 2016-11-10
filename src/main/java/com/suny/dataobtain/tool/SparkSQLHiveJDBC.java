package com.suny.dataobtain.tool;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通过SparkJDBC获取数据
 * @author xiaogaunyu
 * @date   20160318
 * @note   20161107 暂时不能使用，Adolf Frank,
 * 					使用Presto进行数据抽取
 */

public class SparkSQLHiveJDBC {

	private String driverName = "org.apache.hive.jdbc.HiveDriver";

	//private String url = "jdbc:hive2://10.38.11.1:10001/data_center?connectTimeout=5184000&autoReconnect=true&useUnicode=true&characterEncoding=utf8";
	private String url = "jdbc:hive2://10.38.11.1:8080/hive/data_center";

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}


	public static String matchStr(String str,String regex1,String regex2){
		String dest = "";
		if(str == null){
			return dest;
		}
		Pattern p = Pattern.compile(regex1);
		Matcher m = p.matcher(str);
		dest = m.replaceAll(regex2);
		return dest;
	}

	/** 使用sparksql查询数据
	 * @param sql 	需要查询的sql
	 * */
	public List<Long> getIdBySpark(String sql) throws ClassNotFoundException, SQLException{

		List<Long> dataList = new ArrayList<Long>();
		Class.forName(this.driverName);
		Connection conn = null;
		conn = DriverManager.getConnection(this.url,"","");
		PreparedStatement pstm = null;
		pstm = conn.prepareStatement(sql) ;

		System.out.println("Running SQL: " + sql);
		ResultSet rs = null;
		rs = pstm.executeQuery();

		if(rs != null){
			while(rs.next()) {
				long id = rs.getLong("id");
				dataList.add(id);
			}
		}else{
			System.out.println("no date!");
		}
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
		return dataList;
	}

	/** 使用sparksql查询数据
	 * @param sql    需要查询的sql语句
	 * @param tableName：表名
	 * @param heads：需要查询的字段数组
	 * */
	public List<Map> getDataBySpark (String sql,String tableName,String[] heads ){
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pstm = null;
		Class clazz = null;
		if(tableName.equals("article_info")){
			try {
				clazz = Class.forName("com.peopleyuqing.bean.ArticleBean");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		List<Map> dataList = new ArrayList<Map>();
		try {
			Class.forName(driverName);
			conn = DriverManager.getConnection(url,"","");
			pstm = conn.prepareStatement(sql) ;
			System.out.println("Running SQL: " + sql);
			rs = pstm.executeQuery();
			if(rs != null){
				while(rs.next()) {
					Map<String,String> dataMap = new HashMap<String,String>();
					for(int i=0;i<heads.length;i++){
						String columnName = heads[i];
						String strValue = null;
						try {
							Field field = clazz.getDeclaredField(columnName);//根据提取字段获取对应字段的set方法（public、private的方法都可以获取到，getField只能获取public修饰的方法）
							if(field.getType() == String.class){
								if(columnName.equals("content")){//如果查询内容将换行符替换
									strValue = rs.getString(columnName);
									strValue = matchStr(strValue,"\r|\n","<br/>");
								}else{
									strValue = rs.getString(columnName);
									if(strValue == null || strValue.equals("null") || strValue.equals("NULL")){
										strValue = "";
									}else if(!strValue.equals("")){
										strValue.replaceAll("/t", "    ");//把tab键用4个空格代替
									}
								}
							}else if(field.getType() == int.class){
								int v = rs.getInt(columnName);
								//System.out.println("value:" + v);
								strValue = String.valueOf(v);
							}else if(field.getType() == float.class){
								float v = rs.getFloat(columnName);
								strValue = String.valueOf(v);
							}else if(field.getType() == long.class){
								long v = rs.getLong(columnName);
								strValue = String.valueOf(v);
							}else if(field.getType() == double.class){
								double v = rs.getDouble(columnName);
								strValue = String.valueOf(v);
							}
							dataMap.put(columnName, strValue);
						} catch (NoSuchFieldException e) {
							e.printStackTrace();
						}
					}
					dataList.add(dataMap);
				}

			}else{
				System.out.println("no date!");
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
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
		return dataList;
	}

	//根据id集合生成Spark sql 语句
	public String createSql(List<String> ids){
		String sql = "";
		if(ids != null && ids.size() > 0){
			StringBuffer sqlBuffer = new StringBuffer();
			sqlBuffer.append("select content from data_center.article_info where year='2015' and id in (");
			for(int i=0;i<ids.size();i++){
				String id = ids.get(i);
				if(i==0){
					sqlBuffer.append(id);
				}else{
					sqlBuffer.append("," + id);
				}
			}
			sqlBuffer.append(")");
			sql = sqlBuffer.toString();
		}
		System.out.println("生成的sql:" + sql);
		return sql;
	}

	//根据Spark Sql 语句获取文章的正文内容
	public List<String> quertContentBySpark(String sql){
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pstm = null;
		List<String> dataList = new ArrayList<String>();
		try {
			Class.forName(driverName);
			conn = DriverManager.getConnection(url,"","");
			pstm = conn.prepareStatement(sql) ;
			System.out.println("Running SQL: " + sql);
			rs = pstm.executeQuery();
			if(rs != null){
				while(rs.next()){
					String content = rs.getString("content");
					dataList.add(content);
				}

			}else{
				System.out.println("no date!");
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			//System.exit(1);
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
		return dataList;
	}

	/**
	 * 根据语义指纹获取相似文章数或者转载数目
	 * @param sql
	 * @param heads
	 * @return
	 * @author Adolf Frank  20160630  特殊需求
	 */
	public Map<String,Integer> queryFigerTimesBySpark(String sql,String[] heads){
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pstm = null;
		Map<String,Integer> dataList = new HashMap<String,Integer>();
		try {
			Class.forName(driverName);
			conn = DriverManager.getConnection(url,"","");
			pstm = conn.prepareStatement(sql) ;
			System.out.println("Running SQL: " + sql);
			rs = pstm.executeQuery();
			if(rs != null){
				while(rs.next()){
					String print = rs.getString(heads[0]);
					int simTimes = rs.getInt(heads[1]);
					System.out.println(print + "====" + simTimes);
					dataList.put(print, simTimes);
				}

			}else{
				System.out.println("no date!");
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
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
		return dataList;
	}

	/**
	 * 根据语义指纹获取相似文章数或者转载数目
	 * @param sql
	 * @param heads
	 * @return				属于特殊处理，按着Heads的内容确定对结果输出进行修改
	 * @author Adolf Frank  20160630  特殊需求
	 * @throws IOException
	 */
	public void queryIdByHiveJDBC(String sql,String[] heads,BufferedWriter bw) throws IOException, ClassNotFoundException{

		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pstm = null;
		System.out.println("Running SQL: " + sql);
		try {
			Class.forName(driverName);
			conn = DriverManager.getConnection(url,"","");
			pstm = conn.prepareStatement(sql) ;
			System.out.println("Running SQL: " + sql);
			rs = pstm.executeQuery();
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

}