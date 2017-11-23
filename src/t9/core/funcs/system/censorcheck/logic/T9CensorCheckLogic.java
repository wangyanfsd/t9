package t9.core.funcs.system.censorcheck.logic;

import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.core.funcs.diary.data.T9Diary;
import t9.core.funcs.diary.data.T9DiaryComment;
import t9.core.funcs.diary.logic.T9DiaryUtil;
import t9.core.funcs.mobilesms.data.T9Sms2;
import t9.core.funcs.mobilesms.logic.T9MobileSms2Logic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.sms.data.T9SmsBack;
import t9.core.funcs.sms.data.T9SmsBody;
import t9.core.funcs.sms.logic.T9SmsUtil;
import t9.core.funcs.system.censorcheck.data.T9CensorCheck;
import t9.core.funcs.system.censorwords.data.T9CensorModule;
import t9.core.funcs.system.censorwords.data.T9CensorWords;
import t9.core.global.T9Const;
import t9.core.util.db.T9DBUtility;
public class T9CensorCheckLogic {
  private static Logger log = Logger.getLogger("t9.core.funcs.dept.act");

  public String getBannedHint(Connection dbConn, String moduleCode)
      throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    String bannedHint = "";
    try {
      String queryStr = "select BANNED_HINT from CENSOR_MODULE where MODULE_CODE="
          + moduleCode;
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(queryStr);
      if (rs.next()) {
        bannedHint = rs.getString("BANNED_HINT");
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return bannedHint;
  }
  
  /**
   * select 审核不通过后的信息
   * @param dbConn
   * @param moduleCode
   * @return
   * @throws Exception
   */
  public String getModHint(Connection dbConn, String moduleCode)
      throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    String modHint = "";
    try {
      String queryStr = "select MOD_HINT from CENSOR_MODULE where MODULE_CODE="
          + moduleCode;
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(queryStr);
      if (rs.next()) {
        modHint = rs.getString("MOD_HINT");
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return modHint;
  }

  public ArrayList<T9CensorCheck> getCensorConetentJson(Connection dbConn, String moduleCode)
      throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9CensorCheck censorCheck = null;
    List list = new ArrayList();
    ArrayList<T9CensorCheck> checkList = new ArrayList<T9CensorCheck>();
    ArrayList<T9CensorCheck> result = new ArrayList<T9CensorCheck>();
    try {
      stmt = dbConn.createStatement();
      String sql = "select SEQ_ID, CENSOR_FLAG, CHECK_USER, CHECK_TIME, CONTENT from CENSOR_DATA where CENSOR_FLAG = 0 and MODULE_CODE='" + moduleCode + "'";
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        censorCheck = new T9CensorCheck();
        censorCheck.setSeqId(rs.getInt("SEQ_ID"));
        censorCheck.setCensorFlag(rs.getString("CENSOR_FLAG"));
        censorCheck.setCheckUser(rs.getString("CHECK_USER"));
        censorCheck.setCheckTime(rs.getDate("CHECK_TIME"));
        censorCheck.setContent(rs.getString("CONTENT"));
        checkList.add(censorCheck);
      }
      int num = 200;
      if (checkList.size() > num) {
        result.addAll(checkList.subList(0, num));
      } else {
        result = checkList;
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return result;
  }
  
  /**
   * 内部短信审核:待审核邮件查询列表
   * @param dbConn
   * @param moduleCode
   * @return
   * @throws Exception
   */
  public ArrayList<T9CensorCheck> getCensorConetentSmsJson(Connection dbConn,
      String moduleCode) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9CensorCheck censorCheck = null;
    List list = new ArrayList();
    ArrayList<T9CensorCheck> checkList = new ArrayList<T9CensorCheck>();
    ArrayList<T9CensorCheck> result = new ArrayList<T9CensorCheck>();
    try {
      stmt = dbConn.createStatement();
      String sql = "select SEQ_ID, CENSOR_FLAG, CHECK_USER, CHECK_TIME, CONTENT from CENSOR_DATA where CENSOR_FLAG = 0 and MODULE_CODE='"
          + moduleCode + "'";
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        censorCheck = new T9CensorCheck();
        censorCheck.setSeqId(rs.getInt("SEQ_ID"));
        censorCheck.setCensorFlag(rs.getString("CENSOR_FLAG"));
        censorCheck.setCheckUser(rs.getString("CHECK_USER"));
        censorCheck.setCheckTime(rs.getDate("CHECK_TIME"));
        censorCheck.setContent(rs.getString("CONTENT"));
        checkList.add(censorCheck);
      }
      int num = 200;
      if (checkList.size() > num) {
        result.addAll(checkList.subList(0, num));
      } else {
        result = checkList;
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return result;
  }
  
  /**
   * 手机短信审核:待审核邮件查询列表
   * @param dbConn
   * @param moduleCode
   * @return
   * @throws Exception
   */
  public ArrayList<T9CensorCheck> getMobilSmsJson(Connection dbConn,
      String moduleCode) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9CensorCheck censorCheck = null;
    List list = new ArrayList();
    ArrayList<T9CensorCheck> checkList = new ArrayList<T9CensorCheck>();
    ArrayList<T9CensorCheck> result = new ArrayList<T9CensorCheck>();
    try {
      stmt = dbConn.createStatement();
      String sql = "select SEQ_ID, CENSOR_FLAG, CHECK_USER, CHECK_TIME, CONTENT from CENSOR_DATA where CENSOR_FLAG = 0 and MODULE_CODE='"
          + moduleCode + "'";
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        censorCheck = new T9CensorCheck();
        censorCheck.setSeqId(rs.getInt("SEQ_ID"));
        censorCheck.setCensorFlag(rs.getString("CENSOR_FLAG"));
        censorCheck.setCheckUser(rs.getString("CHECK_USER"));
        censorCheck.setCheckTime(rs.getDate("CHECK_TIME"));
        censorCheck.setContent(rs.getString("CONTENT"));
        checkList.add(censorCheck);
      }
      int num = 200;
      if (checkList.size() > num) {
        result.addAll(checkList.subList(0, num));
      } else {
        result = checkList;
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return result;
  }
  
  /**
   * 条件查询（全）
   * @param dbConn
   * @param censorFlag
   * @param fromId      发信人
   * @param checkBegin  发送时间（开始）
   * @param checkEnd    审核时间 （结束）
   * @param subject     主题
   * @param content     内容
   * @param toId        收信人
   * @param beginDate   发送时间（开始）
   * @param endDate     审核时间 （结束）
   * @return
   * @throws Exception
   */

  public ArrayList<T9CensorCheck> getCensorConetentJson2(Connection dbConn, String censorFlag, String fromId, String checkBegin, String checkEnd, String subject, String content, String toId, String beginDate, String endDate, String moduleCode, String limitStr)
      throws Exception {
    String whereStr = "";
    String dateBegin = "";
    String dateEnd = "";
    String idComp = "";
    String checkTime = "";
    T9CensorCheck censorCheck = null;
    Map jsonM = null;
    List list = new ArrayList();
    T9FOM fom = new T9FOM();
    ArrayList<T9CensorCheck> checkList = new ArrayList<T9CensorCheck>();
    ArrayList<T9CensorCheck> result = new ArrayList<T9CensorCheck>();
    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = dbConn.createStatement();
      if(!T9Utility.isNullorEmpty(checkBegin)){
        dateBegin = T9DBUtility.getDateFilter("CHECK_TIME", checkBegin, "<=");
      }
      if(!T9Utility.isNullorEmpty(checkEnd)){
        dateEnd = T9DBUtility.getDateFilter("CHECK_TIME", checkEnd, ">=");
      }
      if(!T9Utility.isNullorEmpty(censorFlag)){
        whereStr = " and CENSOR_FLAG="+ censorFlag;
      }else if(!T9Utility.isNullorEmpty(checkBegin)){
        whereStr = " and "+ dateBegin;
      }else if(!T9Utility.isNullorEmpty(checkEnd)){
        whereStr = " and "+ dateEnd;
      }
      String sql = "select " 
      		      + "SEQ_ID, " 
      		     	+	"CENSOR_FLAG, " 
      		     	+	"CHECK_USER, " 
      		     	+	"CHECK_TIME, CONTENT from CENSOR_DATA where MODULE_CODE = '"+moduleCode+"'"+ whereStr +"order by CHECK_TIME DESC";
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        censorCheck = new T9CensorCheck();
        censorCheck.setSeqId(rs.getInt("SEQ_ID"));
        censorCheck.setCensorFlag(rs.getString("CENSOR_FLAG"));
        censorCheck.setCheckUser(rs.getString("CHECK_USER"));
        if(rs.getTimestamp("CHECK_TIME") != null){
          checkTime = T9Utility.getDateTimeStr(rs.getTimestamp("CHECK_TIME"));
          censorCheck.setCheckTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(checkTime));
        }
        censorCheck.setContent(rs.getString("CONTENT"));
        jsonM = fom.json2Map(rs.getString("CONTENT"));
        jsonM.get("BODY_ID");
        String subjectStr = (String) jsonM.get("SUBJECT");
        String contentStr = (String) jsonM.get("CONTENT");
        String fromIdStr = (String) jsonM.get("FROM_ID");
        String toIdStr = (String) jsonM.get("TO_ID");
        String beginTime = (String) jsonM.get("SEND_TIME");
        if((!T9Utility.isNullorEmpty(subject) && subjectStr.toLowerCase().indexOf(subject.toLowerCase()) == -1) 
            || (!T9Utility.isNullorEmpty(content) && contentStr.toLowerCase().indexOf(content.toLowerCase()) == -1)
            || (!T9Utility.isNullorEmpty(fromId) && !getToId(fromId, fromIdStr))
            || (!T9Utility.isNullorEmpty(toId) && !getToId(toId, toIdStr))
            || (!T9Utility.isNullorEmpty(beginDate) && beginTime.compareTo(beginDate) < 0)
            || (!T9Utility.isNullorEmpty(endDate) && beginTime.compareTo(endDate) > 0)){
          continue;
        }
        checkList.add(censorCheck);
      }
      if (!T9Utility.isNullorEmpty(limitStr)) {
        int num = Integer.parseInt(limitStr);
        if (checkList.size() > num) {
          result.addAll(checkList.subList(0, num));
        } else {
          result = checkList;
        }
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return result;
  }
  
  /**
   * 内部短信审核：条件查询（全）
   * @param dbConn
   * @param censorFlag
   * @param fromId
   * @param checkBegin
   * @param checkEnd
   * @param content
   * @param toId
   * @param beginDate
   * @param endDate
   * @param moduleCode
   * @param limitStr
   * @return
   * @throws Exception
   */
  
  public ArrayList<T9CensorCheck> getCensorConetentJsonSms(Connection dbConn,
      String censorFlag, String fromId, String checkBegin, String checkEnd,
      String content, String toId, String beginDate,
      String endDate, String moduleCode, String limitStr) throws Exception {
    String whereStr = "";
    String dateBegin = "";
    String dateEnd = "";
    String idComp = "";
    String checkTime = "";
    T9CensorCheck censorCheck = null;
    Map jsonM = null;
    List list = new ArrayList();
    T9FOM fom = new T9FOM();
    ArrayList<T9CensorCheck> checkList = new ArrayList<T9CensorCheck>();
    ArrayList<T9CensorCheck> result = new ArrayList<T9CensorCheck>();
    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = dbConn.createStatement();
      if (!T9Utility.isNullorEmpty(checkBegin)) {
        dateBegin = T9DBUtility.getDateFilter("CHECK_TIME", checkBegin, "<=");
      }
      if (!T9Utility.isNullorEmpty(checkEnd)) {
        dateEnd = T9DBUtility.getDateFilter("CHECK_TIME", checkEnd, ">=");
      }
      if (!T9Utility.isNullorEmpty(censorFlag)) {
        whereStr = " and CENSOR_FLAG=" + censorFlag;
      } else if (!T9Utility.isNullorEmpty(checkBegin)) {
        whereStr = " and " + dateBegin;
      } else if (!T9Utility.isNullorEmpty(checkEnd)) {
        whereStr = " and " + dateEnd;
      }
      String sql = "select SEQ_ID, CENSOR_FLAG, CHECK_USER, CHECK_TIME, CONTENT from CENSOR_DATA where MODULE_CODE = '"
          + moduleCode + "'" + whereStr + "order by CHECK_TIME DESC";
      rs = stmt.executeQuery(sql);
      //System.out.println(sql);
      while (rs.next()) {
        censorCheck = new T9CensorCheck();
        censorCheck.setSeqId(rs.getInt("SEQ_ID"));
        censorCheck.setCensorFlag(rs.getString("CENSOR_FLAG"));
        censorCheck.setCheckUser(rs.getString("CHECK_USER"));
        //if (rs.getString("time") != null) {
        //  censorCheck.setCheckTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        //      .parse(rs.getString("time")));
        //}
        if(rs.getTimestamp("CHECK_TIME") != null){
          checkTime = T9Utility.getDateTimeStr(rs.getTimestamp("CHECK_TIME"));
          censorCheck.setCheckTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(checkTime));
        }
        censorCheck.setContent(rs.getString("CONTENT"));
        jsonM = fom.json2Map(rs.getString("CONTENT"));
        //jsonM.get("BODY_ID");
        String contentStr = (String) jsonM.get("CONTENT");
        String fromIdStr = (String) jsonM.get("FROM_ID");
        String toIdStr = (String) jsonM.get("TO_ID");
        String beginTime = (String) jsonM.get("SEND_TIME");
        //String smsType = (String) jsonM.get("SMS_TYPE");
        //String remindUrl = (String) jsonM.get("REMIND_URL");
        
        if ((!T9Utility.isNullorEmpty(content) && contentStr.toLowerCase()
                .indexOf(content.toLowerCase()) == -1)
            || (!T9Utility.isNullorEmpty(fromId) && !getToId(fromId, fromIdStr))
            || (!T9Utility.isNullorEmpty(toId) && !getToId(toId, toIdStr))
            || (!T9Utility.isNullorEmpty(beginDate) && beginTime
                .compareTo(beginDate) < 0)
            || (!T9Utility.isNullorEmpty(endDate) && beginTime
                .compareTo(endDate) > 0)) {
          continue;
        }
        checkList.add(censorCheck);
      }
      if (!T9Utility.isNullorEmpty(limitStr)) {
        int num = Integer.parseInt(limitStr);
        if (checkList.size() > num) {
          result.addAll(checkList.subList(0, num));
        } else {
          result = checkList;
        }
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return result;
  }
  
  
  /**
   * 手机短信审核：条件查询（全）
   * @param dbConn
   * @param censorFlag
   * @param fromId
   * @param checkBegin
   * @param checkEnd
   * @param content
   * @param toId
   * @param beginDate
   * @param endDate
   * @param moduleCode
   * @param limitStr
   * @return
   * @throws Exception
   */
  
  public ArrayList<T9CensorCheck> getCensorConetentJsonMobilSms(Connection dbConn,
      String censorFlag, String fromId, String checkBegin, String checkEnd,
      String content, String toId, String beginDate,
      String endDate, String moduleCode, String limitStr, String phone1) throws Exception {
    String whereStr = "";
    String dateBegin = "";
    String dateEnd = "";
    String idComp = "";
    String checkTime = "";
    T9CensorCheck censorCheck = null;
    Map jsonM = null;
    List list = new ArrayList();
    T9FOM fom = new T9FOM();
    ArrayList<T9CensorCheck> checkList = new ArrayList<T9CensorCheck>();
    ArrayList<T9CensorCheck> result = new ArrayList<T9CensorCheck>();
    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = dbConn.createStatement();
      if (!T9Utility.isNullorEmpty(checkBegin)) {
        dateBegin = T9DBUtility.getDateFilter("CHECK_TIME", checkBegin, "<=");
      }
      if (!T9Utility.isNullorEmpty(checkEnd)) {
        dateEnd = T9DBUtility.getDateFilter("CHECK_TIME", checkEnd, ">=");
      }
      if (!T9Utility.isNullorEmpty(censorFlag)) {
        whereStr = " and CENSOR_FLAG=" + censorFlag;
      } else if (!T9Utility.isNullorEmpty(checkBegin)) {
        whereStr = " and " + dateBegin;
      } else if (!T9Utility.isNullorEmpty(checkEnd)) {
        whereStr = " and " + dateEnd;
      }
      String sql = "select " 
          		+ "SEQ_ID, "
          		+ "CENSOR_FLAG, " 
          		+ "CHECK_USER, " 
          		+ "CHECK_TIME, " 
          		+ "CONTENT from CENSOR_DATA where MODULE_CODE = '"
              + moduleCode + "'" + whereStr + "order by CHECK_TIME DESC";
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        censorCheck = new T9CensorCheck();
        censorCheck.setSeqId(rs.getInt("SEQ_ID"));
        censorCheck.setCensorFlag(rs.getString("CENSOR_FLAG"));
        censorCheck.setCheckUser(rs.getString("CHECK_USER"));
        if(rs.getTimestamp("CHECK_TIME") != null){
          checkTime = T9Utility.getDateTimeStr(rs.getTimestamp("CHECK_TIME"));
          censorCheck.setCheckTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(checkTime));
        }
        censorCheck.setContent(rs.getString("CONTENT"));
        jsonM = fom.json2Map(rs.getString("CONTENT"));
        String contentStr = (String) jsonM.get("CONTENT");
        String fromIdStr = (String) jsonM.get("FROM_ID");
        String toIdStr = (String) jsonM.get("TO_ID");
        String beginTime = (String) jsonM.get("SEND_TIME");
        String phone = (String)jsonM.get("PHONE");
        
        if ((!T9Utility.isNullorEmpty(content) && contentStr.toLowerCase()
                .indexOf(content.toLowerCase()) == -1)
            || (!T9Utility.isNullorEmpty(fromId) && !getToId(fromId, fromIdStr))
            || (!T9Utility.isNullorEmpty(toId) && !getToId(toId, toIdStr))
            || (!T9Utility.isNullorEmpty(phone) && !phone.startsWith(phone1))
            || (!T9Utility.isNullorEmpty(beginDate) && beginTime
                .compareTo(beginDate) < 0)
            || (!T9Utility.isNullorEmpty(endDate) && beginTime
                .compareTo(endDate) > 0)) {
          continue;
        }
        checkList.add(censorCheck);
      }
      if (!T9Utility.isNullorEmpty(limitStr)) {
        int num = Integer.parseInt(limitStr);
        if (checkList.size() > num) {
          result.addAll(checkList.subList(0, num));
        } else {
          result = checkList;
        }
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return result;
  }
  /**
   * 无条件查询
   * 
   * @param dbConn
   * @param censorFlag
   * @return
   * @throws Exception
   */
  
  public ArrayList<T9CensorCheck> getCensorConetentJsonAll(Connection dbConn,
      String censorFlag) throws Exception {
    String checkTime = "";
    Statement stmt = null;
    ResultSet rs = null;
    T9CensorCheck censorCheck = null;
    List list = new ArrayList();
    ArrayList<T9CensorCheck> checkList = new ArrayList<T9CensorCheck>();
    try {
      stmt = dbConn.createStatement();
      String sql = "select SEQ_ID, CENSOR_FLAG, CHECK_USER, CHECK_TIME, CONTENT from CENSOR_DATA order by CHECK_TIME DESC";
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        censorCheck = new T9CensorCheck();
        censorCheck.setSeqId(rs.getInt("SEQ_ID"));
        censorCheck.setCensorFlag(rs.getString("CENSOR_FLAG"));
        censorCheck.setCheckUser(rs.getString("CHECK_USER"));
        if(rs.getTimestamp("CHECK_TIME") != null){
          checkTime = T9Utility.getDateTimeStr(rs.getTimestamp("CHECK_TIME"));
          censorCheck.setCheckTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(checkTime));
        }
        censorCheck.setContent(rs.getString("CONTENT"));
        checkList.add(censorCheck);
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return checkList;
  }
  
  /**
   * 邮件审核：删除

   * @param conn
   * @param seqIds
   * @return
   * @throws Exception 
   */

  public void deleteAll(Connection conn, String seqIds) throws Exception {
    String sql = "DELETE FROM CENSOR_DATA WHERE SEQ_ID IN(" + seqIds + ")";
    PreparedStatement pstmt = null;
    try {
      pstmt = conn.prepareStatement(sql);
      pstmt.executeUpdate();
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(pstmt, null, null);
    }
  }
  
  /**
   * 邮件审核：通过

   * @param conn
   * @param seqIds
   * @param seqId
   * @return
   * @throws Exception 
   */
  
  public void passAll(Connection conn, String seqIds, int seqId, String timeStr) throws Exception {
    String sql = "UPDATE CENSOR_DATA SET CENSOR_FLAG = 1, CHECK_TIME = ?, CHECK_USER=" + seqId + " WHERE SEQ_ID IN ("
        + seqIds + ")";
    PreparedStatement pstmt = null;
    try {
      pstmt = conn.prepareStatement(sql);
      pstmt.setTimestamp(1, T9Utility.parseTimeStamp());
      pstmt.executeUpdate();
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(pstmt, null, null);
    }
  }
  
  public void passPerson(Connection conn, String seqIds) throws Exception {
    String sql = "UPDATE CENSOR_DATA SET CENSOR_FLAG = 1 WHERE SEQ_ID IN("
      + seqIds + ")";
    PreparedStatement pstmt = null;
    try {
      pstmt = conn.prepareStatement(sql);
      pstmt.executeUpdate();
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(pstmt, null, null);
    }
  }
  
  /**
   * 邮件审核：拒绝邮件

   * @param conn
   * @param seqIds
   * @param seqId
   * @return
   * @throws Exception 
   */

  public void denyAll(Connection conn, String seqIds, int seqId, String timeStr) throws Exception {
    String sql = "UPDATE CENSOR_DATA SET CENSOR_FLAG = 2, CHECK_TIME = ?, CHECK_USER=" + seqId + " WHERE SEQ_ID IN("
        + seqIds + ")";
    PreparedStatement pstmt = null;
    try {
      pstmt = conn.prepareStatement(sql);
      pstmt.setTimestamp(1, T9Utility.parseTimeStamp());
      pstmt.executeUpdate();
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(pstmt, null, null);
    }
  }
  
  /**
   * 把用户ID转换成对映的姓名（包含串 例如：196，201  转换成对映的姓名）
   * 
   * @param dbConn
   * @param idStrs
   * @return
   * @throws Exception
   */
  
  public String getUserId(Connection dbConn, String idStrs) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    String userId = "";
    try {
      if(T9Utility.isNullorEmpty(idStrs)){
        userId = ""+",";
      }else{
      String[] ids = idStrs.split(",");
      for (int i = 0; i < ids.length; i++) {
        stmt = dbConn.createStatement();
        String queryStr = "select USER_NAME from PERSON where SEQ_ID = "
            + Integer.parseInt(ids[i]);
        rs = stmt.executeQuery(queryStr);
        while (rs.next()) {
          userId += rs.getString("USER_NAME")+",";
        }
      }
    }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return userId;
  }
  
  /**
   * 用来处理查询 的发信人串（106,196,213.....）是否和原数据匹配库
   * @param toId
   * @param toIdStr
   * @return
   * @throws Exception
   */
  public boolean getToId(String toId, String toIdStr)
      throws Exception {
    String idComp = "";
    String[] idStrs = toId.split(",");
    for (int i = 0; i < idStrs.length; i++) {
      idComp = idStrs[i];
      if(toIdStr.equals(idComp)){
        return true;
      }else{
        continue;
      }
    }
    return false;
  }
  /**
   * 
   * @param dbConn
   * @param moduleCode  模块编号：0-邮件；1-短信；2-手机短信
   * @param jsonStr     邮件/短信/手机短信 的json数据
   */
  
  public static void addJsonContent(Connection dbConn, String moduleCode, String jsonStr, int userId){
    try {
      Map m =new HashMap();
      m.put("content", jsonStr);
      m.put("moduleCode", moduleCode);
      m.put("censorFlag", "0");
      T9ORM checkCensor = new T9ORM();
      checkCensor.saveSingle(dbConn, "censorData", m);
      getCensorModuleLogic(dbConn, userId, moduleCode);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public static void getCensorModuleLogic(Connection dbConn ,int userId, String moduleCode) throws Exception{
    T9ORM orm = new T9ORM();
    try {
      String codeFlag = "0";
      String content = "";
      String remindUrl = "";
      String[] moduleFalg = new String[]{"MODULE_CODE=" + codeFlag};
      ArrayList<T9CensorModule> dia = (ArrayList<T9CensorModule>) orm.loadListSingle(dbConn, T9CensorModule.class, moduleFalg);
      //发送内部短信      if(moduleCode.equals("0")){
        content = "有新的内部邮件需要您审核";
        remindUrl = "/core/funcs/system/censorcheck/email/index.jsp";
      }else if(moduleCode.equals("1")){
        content = "有新的内部短信需要您审核";
        remindUrl = "/core/funcs/system/censorcheck/sms/index.jsp";
      }else if(moduleCode.equals("2")){
        content = "有新的手机短信需要您审核";
        remindUrl = "/core/funcs/system/censorcheck/mobilesms/index.jsp";
      }
      String toId = String.valueOf(dia.get(0).getCheckUser());
      
      String smsFalg = getSmsRemindLogic(dbConn, moduleCode);
      String sms2Flag = getSms2RemindLogic(dbConn, moduleCode);
      if("1".equals(smsFalg)){
        doSmsBack(dbConn, content, userId, toId, String.valueOf(22), remindUrl);
      }
      if("1".equals(sms2Flag)){
        T9MobileSms2Logic mobileSms2 =  new T9MobileSms2Logic();
        mobileSms2.remindByMobileSms(dbConn, toId, userId, content, null);
      }
    } catch (Exception e) {
      throw e;
    }
  }
  
  /**
   * 词语过滤通过后帮助发短信
   * @param conn
   * @param content
   * @param fromId
   * @param toId
   * @param type
   * @param remindUrl
   * @throws Exception
   */
  public static void doSmsBack(Connection conn,String content,int fromId,String toId,String type,String remindUrl) throws Exception{
    T9SmsBack sb = new T9SmsBack();
    sb.setContent(content);
    sb.setFromId(fromId);
    sb.setToId(toId);
    sb.setSmsType(type);
    sb.setRemindUrl(remindUrl);
    T9SmsUtil.smsBack(conn, sb);
  }
  
  /**
   * 词语过滤通过后帮助发邮件
   * @param conn
   * @param content
   * @param fromId
   * @param toId
   * @param type
   * @param remindUrl
   * @throws Exception
   */
  public static void doEmailBack(Connection conn,String content,int fromId,String toId,String type,String remindUrl) throws Exception{
    T9SmsBack sb = new T9SmsBack();
    sb.setContent(content);
    sb.setFromId(fromId);
    sb.setToId(toId);
    sb.setSmsType(type);
    sb.setRemindUrl(remindUrl);
    T9SmsUtil.smsBack(conn, sb);
  }
  
  public static void doMobileSmsBack(Connection conn, Map map) throws Exception{

    //T9MobileSms2Logic.addSms(conn, sms);
  }
  
  /**
   * 禁止词语提示
   * @param dbConn
   * @param moduleCode  模块编号：0-邮件；1-短信；2-手机短信
   * @param jsonStr     邮件/短信/手机短信 的json数据
   * @throws Exception 
   */
  
  public static String getCensorBanned(Connection dbConn, String moduleCode) throws Exception{
    String bannedHint = "";
    T9CensorCheckLogic ccl = new T9CensorCheckLogic();
    bannedHint = ccl.getBannedHint(dbConn, moduleCode);
    return "/core/funcs/system/censorcheck/email/banned.jsp?bannedHint="+URLEncoder.encode(bannedHint,"UTF-8");
  }
  
  /**
   * 敏感词语提示
   * @param dbConn
   * @param moduleCode　模块编号：0-邮件；1-短信；2-手机短信
   * @return
   * @throws Exception
   */
  public static String getCensorMOD(Connection dbConn, String moduleCode) throws Exception{
    String modHint = "";
    T9CensorCheckLogic ccl = new T9CensorCheckLogic();
    modHint = ccl.getModHint(dbConn, moduleCode);
    return "/core/funcs/system/censorcheck/email/mod.jsp?modHint=" + URLEncoder.encode(modHint,"UTF-8");
  }
  
  /**
   * 
   * @param conn
   * @param bodyId
   * @throws Exception
   */
  public void sendMailByCoren(Connection conn,String bodyId) throws Exception{
    String sql = "UPDATE EMAIL_BODY SET SEND_FLAG = '1' WHERE SEQ_ID IN( '" + bodyId + "')";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = conn.prepareStatement(sql);
      ps.executeUpdate();
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
  }
  
  /**
   * 内部短信审核:通过审核后发短信
   * @param dbConn
   * @param moduleCode
   * @return
   * @throws Exception
   */
  public ArrayList<T9CensorCheck> getPassSmsJson(Connection dbConn,
      String seqId) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9CensorCheck censorCheck = null;
    List list = new ArrayList();
    ArrayList<T9CensorCheck> checkList = new ArrayList<T9CensorCheck>();
    ArrayList<T9CensorCheck> result = new ArrayList<T9CensorCheck>();
    try {
      stmt = dbConn.createStatement();
      String sql = "select SEQ_ID, CONTENT from CENSOR_DATA where SEQ_ID IN (" + seqId + ")";
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        censorCheck = new T9CensorCheck();
        censorCheck.setSeqId(rs.getInt("SEQ_ID"));
        censorCheck.setContent(rs.getString("CONTENT"));
        checkList.add(censorCheck);
      }
      int num = 200;
      if (checkList.size() > num) {
        result.addAll(checkList.subList(0, num));
      } else {
        result = checkList;
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return result;
  }
  
  /**
   * 内部邮件审核:通过审核后发短信
   * @param dbConn
   * @param moduleCode
   * @return
   * @throws Exception
   */
  public ArrayList<T9CensorCheck> getPassEmailJson(Connection dbConn,
      String seqId) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9CensorCheck censorCheck = null;
    List list = new ArrayList();
    ArrayList<T9CensorCheck> checkList = new ArrayList<T9CensorCheck>();
    ArrayList<T9CensorCheck> result = new ArrayList<T9CensorCheck>();
    try {
      stmt = dbConn.createStatement();
      String sql = "select SEQ_ID, CONTENT from CENSOR_DATA where SEQ_ID IN (" + seqId + ")";
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        censorCheck = new T9CensorCheck();
        censorCheck.setSeqId(rs.getInt("SEQ_ID"));
        censorCheck.setContent(rs.getString("CONTENT"));
        checkList.add(censorCheck);
      }
      int num = 200;
      if (checkList.size() > num) {
        result.addAll(checkList.subList(0, num));
      } else {
        result = checkList;
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return result;
  }
  
  /**
   * 是否用内部短信提醒
   * @param conn
   * @param moduleCode
   * @return
   * @throws Exception
   */
  
  public static String getSmsRemindLogic(Connection conn, String moduleCode) throws Exception{
    String result = "";
    String sql = "select SMS_REMIND from CENSOR_MODULE where MODULE_CODE=" + moduleCode;
    PreparedStatement ps = null;
    ResultSet rs = null ;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){
        String toId = rs.getString(1);
        if(toId != null){
          result = toId;
        }
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
    return result;
  }
  
  /**
   * 是否用手机短信提醒
   * @param conn
   * @param moduleCode
   * @return
   * @throws Exception
   */
  public static String getSms2RemindLogic(Connection conn, String moduleCode) throws Exception{
    String result = "";
    String sql = "select SMS2_REMIND from CENSOR_MODULE where MODULE_CODE=" + moduleCode;
    PreparedStatement ps = null;
    ResultSet rs = null ;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){
        String toId = rs.getString(1);
        if(toId != null){
          result = toId;
        }
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
    return result;
  }
  
  /**
   * 是否对信息过滤有审核权限
   * @param conn
   * @param moduleCode
   * @return
   * @throws Exception
   */
  public static String getCheckUserLogic(Connection conn, String moduleCode) throws Exception{
    String result = "";
    String sql = "select CHECK_USER from CENSOR_MODULE where MODULE_CODE=" + moduleCode;
    PreparedStatement ps = null;
    ResultSet rs = null ;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){
        String toId = rs.getString(1);
        if(toId != null){
          result = toId;
        }
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
    return result;
  }
  
  /**
   * 通过seqId获得CENSOR_DATA数据
   * @param dbConn
   * @param moduleCode
   * @return
   * @throws Exception
   */
  public ArrayList<T9CensorCheck> getCensorEmailContent(Connection dbConn,
      int seqId) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9CensorCheck censorCheck = null;
    List list = new ArrayList();
    ArrayList<T9CensorCheck> checkList = new ArrayList<T9CensorCheck>();
    ArrayList<T9CensorCheck> result = new ArrayList<T9CensorCheck>();
    try {
      stmt = dbConn.createStatement();
      String sql = "select SEQ_ID, CENSOR_FLAG, CHECK_USER, CHECK_TIME, CONTENT from CENSOR_DATA where SEQ_ID =" + seqId;
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        censorCheck = new T9CensorCheck();
        censorCheck.setSeqId(rs.getInt("SEQ_ID"));
        censorCheck.setCensorFlag(rs.getString("CENSOR_FLAG"));
        censorCheck.setCheckUser(rs.getString("CHECK_USER"));
        censorCheck.setCheckTime(rs.getDate("CHECK_TIME"));
        censorCheck.setContent(rs.getString("CONTENT"));
        checkList.add(censorCheck);
      }
      int num = 200;
      if (checkList.size() > num) {
        result.addAll(checkList.subList(0, num));
      } else {
        result = checkList;
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return result;
  }
}
