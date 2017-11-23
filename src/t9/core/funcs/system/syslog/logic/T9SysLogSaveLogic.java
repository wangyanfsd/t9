package t9.core.funcs.system.syslog.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import t9.core.funcs.person.data.T9Person;
import t9.core.util.T9Out;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;

public class T9SysLogSaveLogic {
  public static String getSaveLog(Connection conn,T9Person person) throws Exception{
    StringBuffer sb = new StringBuffer();
    int allcount=0;
    Statement stmt = null;
    ResultSet rs = null;
    Statement stmt1 = null;
    ResultSet rs1 = null;
    Statement stmt2 = null;
    ResultSet rs2 = null;
    String time="";
    String selectdate="";
    DateFormat sdf = new SimpleDateFormat("yyyyMMdd"); 
    Date date = new Date();  
    String datetime= sdf.format(date);
    int ss =0;
    try{
        String ok="结转成功";
        String sory="结转失败";
        stmt = conn.createStatement();stmt1 = conn.createStatement();stmt2 = conn.createStatement();
        if(ss==0){
        String copytable ="create table SYS_LOG_"+datetime+" as select * from sys_log_30";
        rs=stmt.executeQuery(copytable);
        }
        int count1=0;
        String count ="select count(*) as count from sys_log_30"; 
        rs1 = stmt1.executeQuery(count);
        if(rs1.next()){
         count1= rs1.getInt("count");
        }
        if(count1 > 0){
        String deletable ="truncate table sys_log_30";
        rs2 = stmt2.executeQuery(deletable);
        return ok;
        }else{
          return sory;
        }
       }
    catch(Exception ex){
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
  }
  public static List getOkSaveLog(Connection conn,T9Person person) throws Exception{
    Statement stmt = null;
    ResultSet rs = null;
    String amount="";
    List list = new ArrayList();
    try{
         stmt = conn.createStatement();
         String systable = "select table_name from all_tables where table_name like '" + T9DBUtility.escapeLike("SYS_LOG_") + "%'" + T9DBUtility.escapeLike(); 
         rs = stmt.executeQuery(systable);
         while(rs.next()){
            amount = rs.getString(1);
            list.add("'" + amount + "'");
         }
    }catch(Exception ex){
        throw ex;
    } finally {
        T9DBUtility.close(stmt, rs, null);
    }
    return list;
  }
}
