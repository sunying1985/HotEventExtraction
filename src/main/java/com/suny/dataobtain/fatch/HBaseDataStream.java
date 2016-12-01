package com.suny.dataobtain.fatch;

import com.suny.dataobtain.tool.PrestoJDBC;

import java.io.*;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Frank Adolf
 * Date on 2016/11/3.
 * @note   根据日期和文章类型获取符号条件的ID
 */
public class HBaseDataStream {

    //private SparkSQLHiveJDBC sparkHandle = null;
    private PrestoJDBC prestoHandle = null;
    //
    //select count(distinct id) from data_center.article_info where year='2016' and month ='07' and
    //pubtime>='2016-07-03 00:00:00' and pubtime<='2016-07-03 23:59:59' and articletype = '1'
    public String sqlHead = "select id from data_center.article_info where year='2016' and month = ";
    //public String sqlHead = "select id from data_center.weibo_info where year='2016' and month = ";
    public String[] heads = {"id"};


    public HBaseDataStream() {
        //this.sparkHandle = new SparkSQLHiveJDBC();
        this.prestoHandle = new PrestoJDBC();
    }

    /**
     * 根据条件组建SparkSQL语句
     * @param month         月份
     * @param date          日期
     * @param articletype   数据类型
     * @return
     */
    public String getSparkSQL(int month, int date, int articletype) {

        String strSQL = this.sqlHead;
        if (month < 10) {
            String monthStr = "0" + month;
            if(date < 10) {
                String dateStr = monthStr + "-0" + date;
                strSQL = strSQL + "\'" + monthStr + "\' and pubtime>='2016-" + dateStr + " 00:00:00' and pubtime<='2016-"+ dateStr +
                          " 23:59:59' and articletype = '" + articletype + "\'";
            }
            else {
                String dateStr = monthStr + "-" + date;
                strSQL = strSQL + "\'" + monthStr + "\' and pubtime>='2016-" + dateStr + " 00:00:00' and pubtime<='2016-"+ dateStr +
                        " 23:59:59' and articletype = '" + articletype + "\'";
            }
        }
        else {
            String monthStr =  Integer.toString(month);
            if(date < 10) {
                String dateStr = monthStr + "-0" + date;
                strSQL = strSQL + "\'" + monthStr + "\' and pubtime>='2016-" + dateStr + " 00:00:00' and pubtime<='2016-"+ dateStr +
                        " 23:59:59' and articletype = '" + articletype + "\'";
            }
            else {
                String dateStr = monthStr + "-" + date;
                strSQL = strSQL + "\'" + monthStr + "\' and pubtime>='2016-" + dateStr + " 00:00:00' and pubtime<='2016-"+ dateStr +
                        " 23:59:59' and articletype = '" + articletype + "\'";
            }
        }

        return strSQL;
    }

    /**
     * 将spark SQL获取的满足条件的ID输出到文件中
     */
    public boolean querySqlPresto (String sql, String [] heads, String outfile) throws ClassNotFoundException, SQLException {

        if(sql.isEmpty() == true || sql.equals("") == true) {
            return false;
        }

        if (outfile.isEmpty() == true || outfile.equals("") == true) {
            return false;
        }

        try {
            FileOutputStream fos = new FileOutputStream(new File(outfile),true);
            OutputStreamWriter os = new OutputStreamWriter(fos,"UTF-8");
            BufferedWriter bw = new BufferedWriter(os);
            Set<String> idDict = new HashSet<String>();
            idDict = this.prestoHandle.queryIdSetsByPresto(sql, this.heads);
            System.out.println("the id total number is\t" + idDict.size());
            for (String key : idDict) {
                bw.write(key + "\n");
            }
            bw.close();
            os.close();
            fos.close();

        }catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * 将spark SQL获取的满足条件的ID输出到文件中
     */
    public boolean querySqlHive (String sql, String [] heads, String outfile) throws ClassNotFoundException, SQLException {

        if(sql.isEmpty() == true || sql.equals("") == true) {
            return false;
        }

        if (outfile.isEmpty() == true || outfile.equals("") == true) {
            return false;
        }

        try {
            FileOutputStream fos = new FileOutputStream(new File(outfile),true);
            OutputStreamWriter os = new OutputStreamWriter(fos,"UTF-8");
            BufferedWriter bw = new BufferedWriter(os);

            //this.prestoHandle.queryIdByPresto(sql, this.heads,bw);
            //this.sparkHandle.queryIdByHiveJDBC(sql, this.heads,bw);
            bw.close();
            os.close();
            fos.close();

        }catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }


    public  boolean getIDFronHBase(int month, int date, int type, String outIDFile) throws
                                                                                    ClassNotFoundException, SQLException {
        if (month < 0 || date < 0 || type < 0 || outIDFile.isEmpty()) {
            System.out.println("输入参数错误，请检查您输入的参数");
            return  false;
        }
        String  buildSql = this.getSparkSQL(month,date,type);
        return  this.querySqlPresto(buildSql,this.heads,outIDFile);
        //return  this.querySqlHive(buildSql,this.heads,outIDFile);
    }
}
