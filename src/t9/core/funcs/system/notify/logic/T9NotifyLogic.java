package t9.core.funcs.system.notify.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.core.data.T9DsField;
import t9.core.data.T9DsTable;
import t9.core.funcs.dept.data.T9Department;
import t9.core.funcs.org.data.T9Organization;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.diary.data.T9Diary;
import t9.core.funcs.workflow.data.T9FlowProcess;
import t9.core.util.db.T9DBUtility;

public class T9NotifyLogic {
  private static Logger log = Logger.getLogger("t9.core.funcs.dept.act");

  public T9Diary get(Connection conn) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9Diary org = null;
    try {
      String queryStr = "select SEQ_ID, PARA_NAME, PARA_VALUE from SYS_PARA";
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      //System.out.println(queryStr);
      while (rs.next()) {
        org = new T9Diary();
        org.setSeqId(rs.getInt("SEQ_ID"));
        org.setParaName(rs.getString("PARA_NAME"));
        org.setParaValue(rs.getString("PARA_VALUE"));
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return org;
  }
  
  public T9Diary getNotify(Connection conn) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9Diary org = null;
    String days = "1";
    try {
      String queryStr = "select SEQ_ID, PARA_NAME, PARA_VALUE from SYS_PARA where PARA_NAME='NOTIFY_TOP_DAYS'";
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      //System.out.println(queryStr);
      while (rs.next()) {
        org = new T9Diary();
        org.setSeqId(rs.getInt("SEQ_ID"));
        org.setParaName(rs.getString("PARA_NAME"));
        days = rs.getString("PARA_VALUE");
        if (days != null) {
          int index = days.indexOf(".");
          if (index != -1) {
            days = days.substring(0 , index);
          }
        }
        org.setParaValue(days);
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return org;
  }
  
  public T9Diary getNotifySingle(Connection conn) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9Diary org = null;
    try {
      String queryStr = "select SEQ_ID, PARA_NAME, PARA_VALUE from SYS_PARA where PARA_NAME='NOTIFY_AUDITING_SINGLE'";
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      //System.out.println(queryStr);
      while (rs.next()) {
        org = new T9Diary();
        org.setSeqId(rs.getInt("SEQ_ID"));
        org.setParaName(rs.getString("PARA_NAME"));
        org.setParaValue(rs.getString("PARA_VALUE"));
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return org;
  }
  
  public T9Diary getNotifyRangeAll(Connection conn) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9Diary org = null;
    try {
      String queryStr = "select SEQ_ID, PARA_NAME, PARA_VALUE from SYS_PARA where PARA_NAME='NOTIFY_AUDITING_ALL'";
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      //System.out.println(queryStr);
      while (rs.next()) {
        org = new T9Diary();
        org.setSeqId(rs.getInt("SEQ_ID"));
        org.setParaName(rs.getString("PARA_NAME"));
        org.setParaValue(rs.getString("PARA_VALUE"));
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return org;
  }
  
  public T9Diary getNotifyExceptionId(Connection conn) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9Diary org = null;
    try {
      String queryStr = "select SEQ_ID, PARA_NAME, PARA_VALUE from SYS_PARA where PARA_NAME='NOTIFY_AUDITING_EXCEPTION'";
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      //System.out.println(queryStr);
      while (rs.next()) {
        org = new T9Diary();
        org.setSeqId(rs.getInt("SEQ_ID"));
        org.setParaName(rs.getString("PARA_NAME"));
        org.setParaValue(rs.getString("PARA_VALUE"));
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return org;
  }
  
  public void updateNotifyDays(Connection conn, int seqId, String sumStr) throws Exception{
    Statement stmt = null;
    try {
      stmt = conn.createStatement();
      String queryStr = "update SYS_PARA set PARA_VALUE='" + sumStr + "' where SEQ_ID=" + seqId;
      stmt.executeUpdate(queryStr);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  
  public void updateNotifyCheck(Connection conn, int seqId, String sumStr) throws Exception{
    Statement stmt = null;
    try {
      stmt = conn.createStatement();
      String queryStr = "update SYS_PARA set PARA_VALUE='" + sumStr + "' where SEQ_ID=" + seqId;
      stmt.executeUpdate(queryStr);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  
  public void updateNotifyExceptionId(Connection conn, int seqId, String sumStr) throws Exception{
    Statement stmt = null;
    try {
      stmt = conn.createStatement();
      String queryStr = "update SYS_PARA set PARA_VALUE='" + sumStr + "' where SEQ_ID=" + seqId;
      stmt.executeUpdate(queryStr);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  
  public void addDays(Connection conn, String subStr)throws Exception {
    PreparedStatement pstmt = null;
    String lock = "NOTIFY_TOP_DAYS";
    try{
      String queryStr = "insert into SYS_PARA (PARA_NAME, PARA_VALUE) values (?, ?)";
      pstmt = conn.prepareStatement(queryStr);
      pstmt.setString(1, lock);
      pstmt.setString(2, subStr);
      pstmt.executeUpdate();
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(pstmt, null, log);
    }
  }
  
  public void addCheck(Connection conn, String subStr)throws Exception {
    PreparedStatement pstmt = null;
    String lock = "NOTIFY_AUDITING_SINGLE";
    try{
      String queryStr = "insert into SYS_PARA (PARA_NAME, PARA_VALUE) values (?, ?)";
      pstmt = conn.prepareStatement(queryStr);
      pstmt.setString(1, lock);
      pstmt.setString(2, subStr);
      pstmt.executeUpdate();
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(pstmt, null, log);
    }
  }
  
  public void addExceptionId(Connection conn, String subStr)throws Exception {
    PreparedStatement pstmt = null;
    String lock = "NOTIFY_AUDITING_EXCEPTION";
    try{
      String queryStr = "insert into SYS_PARA (PARA_NAME, PARA_VALUE) values (?, ?)";
      pstmt = conn.prepareStatement(queryStr);
      pstmt.setString(1, lock);
      pstmt.setString(2, subStr);
      pstmt.executeUpdate();
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(pstmt, null, log);
    }
  }
  
  public void addRangeAll(Connection conn, String subStr)throws Exception {
    PreparedStatement pstmt = null;
    String lock = "NOTIFY_AUDITING_ALL";
    try{
      String queryStr = "insert into SYS_PARA (PARA_NAME, PARA_VALUE) values (?, ?)";
      pstmt = conn.prepareStatement(queryStr);
      pstmt.setString(1, lock);
      pstmt.setString(2, subStr);
      pstmt.executeUpdate();
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(pstmt, null, log);
    }
  }
}
