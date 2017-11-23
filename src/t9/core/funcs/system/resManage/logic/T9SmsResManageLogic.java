package t9.core.funcs.system.resManage.logic;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;

public class T9SmsResManageLogic {
  public String searchSms(String toId , String copyToId , String beginDate , String endDate , String content , Connection conn) throws Exception {
    StringBuffer sb = new StringBuffer();
    sb.append("[");
    String query = "select * from SMS,SMS_BODY where SMS.BODY_SEQ_ID=SMS_BODY.SEQ_ID "; 
    if(!T9Utility.isNullorEmpty(toId)) {
      query += " and (" + T9WorkFlowUtility.createFindSql("FROM_ID", toId) + ") ";
    }
     if(!T9Utility.isNullorEmpty(copyToId)) {
       query += " and (" + T9WorkFlowUtility.createFindSql("TO_ID", copyToId) + ") ";
     }
     if(!T9Utility.isNullorEmpty(beginDate)) {
       query +=" and "  + T9DBUtility.getDateFilter("SEND_TIME", beginDate, " >= ");
     } 
     if(!T9Utility.isNullorEmpty(endDate)) {
       query +=" and "  + T9DBUtility.getDateFilter("SEND_TIME", endDate, " <= ");
     }
     if(!T9Utility.isNullorEmpty(content)) {
       String dbms = T9SysProps.getProp("db.jdbc.dbms");
       if (dbms.equals("mysql")) {
         query+=" and CONTENT like '%"+content+"%'";
       }
     }
     
    query +=" order by SEND_TIME desc";
    int count = 0 ;
    Statement stm2 = null;
    ResultSet rs2 = null;
    try {
      stm2 = conn.createStatement();
      rs2 = stm2.executeQuery(query);
      while (rs2.next() && count < 30){
        int smsId = rs2.getInt("SEQ_ID");
        int fromId = rs2.getInt("FROM_ID");
        int toId1 = rs2.getInt("TO_ID");
        int bodyId = rs2.getInt("BODY_SEQ_Id");
        Timestamp sendTime = rs2.getTimestamp("SEND_TIME");
        String dateStr = T9Utility.getDateTimeStr(sendTime);
        String content1 = rs2.getString("CONTENT");
        if (content1 == null)  {
          content1 = "";
        }
        content1 = content1.replaceAll("\"", "“");
        content1 = content1.replaceAll("'", "’");
        
        String fromName = this.getUserName(conn, fromId);
        String toName = this.getUserName(conn, toId1);
        count ++;
        sb.append("{");
        sb.append("smsId:'" + smsId  +"'");
        sb.append(",bodyId:'" + bodyId + "'");
        sb.append(",content:'" + content1  +"'");
        sb.append(",sendTime:'" + dateStr  +"'");
        sb.append(",fromName:'" + fromName  +"'");
        sb.append(",toName:'" + toName  +"'");
        sb.append("},");
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm2, rs2, null); 
    }
    if (count > 0) {
      sb.deleteCharAt(sb.length() - 1);
    }
    sb.append("]");
    return sb.toString();
  }
  public String getUserName(Connection conn , int id) throws Exception {
    String userName = "";
    String query2  = "SELECT USER_NAME from PERSON where SEQ_ID='"+ id +"'";
    Statement stm = null;
    ResultSet rs = null;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query2);
      if (rs.next()){
        userName = rs.getString("USER_NAME");
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
    return userName;
  }
  public void deleteSms(Connection conn, String idStr) throws Exception {
    // TODO Auto-generated method stub
    String query  = "select BODY_SEQ_ID from SMS where SEQ_ID='"+idStr+"'";
    Statement stm = null;
    ResultSet rs = null;
    int bodyId = 0 ;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      if (rs.next()){
        bodyId = rs.getInt("BODY_SEQ_ID") ;
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
    String query2 = "select count(*) as c from SMS where BODY_SEQ_ID='"+bodyId+"'";
    Statement stm1 = null;
    ResultSet rs1 = null;
    int count = 0 ;
    try {
      stm1 = conn.createStatement();
      rs1 = stm1.executeQuery(query2);
      if (rs1.next()){
        count = rs1.getInt("c");
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm1, rs1, null); 
    }
    if (count <=1 ) {
      String query3 = "delete from SMS_BODY where SEQ_ID='"+bodyId+"'";
      this.deleteSQL(conn, query3);
    }
    String query4 ="delete from SMS where SEQ_ID='"+ idStr +"'";
    this.deleteSQL(conn, query4);
  }
  public void deleteSQL(Connection conn , String sql) throws Exception {
    Statement stm2 = null;
    try {
      stm2 = conn.createStatement();
      stm2.executeUpdate(sql);
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm2, null, null); 
    }
  }
}
