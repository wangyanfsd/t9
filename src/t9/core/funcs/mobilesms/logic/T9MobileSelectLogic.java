package t9.core.funcs.mobilesms.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import t9.core.funcs.system.attendance.logic.T9SysParaLogic;
import t9.core.util.db.T9DBUtility;

public class T9MobileSelectLogic {
  private static Logger log = Logger.getLogger("t9.core.funcs.setdescktop.shortcut.act");
  
  // 得到参数
  public String getParaValue(Connection dbConn, String paraName)
      throws Exception {
    T9SysParaLogic t9pl = new T9SysParaLogic();
    String paraValue = "";
    paraValue = t9pl.selectPara(dbConn, paraName);
    return paraValue;
  }
  /**
   * 
   * @param conn
   * @param userId
   * @param type
   * @return
   * @throws Exception
   */
  public StringBuffer getSmsRimdData(Connection conn, int userId, String type) throws Exception{
    StringBuffer result = new StringBuffer();
    String sql = " select "
      + " TYPE_PRIV "
      + ",SMS2_REMIND_PRIV"
      + " from "
      + " SMS2_PRIV ";
    String typePriv = "";
    String sms2RemindPriv =  "";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      if (rs.next()) {
        typePriv = rs.getString(1);
        sms2RemindPriv = rs.getString(2);
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
    String moblieRemindFlag = "0";//不显示提示框
    if ((findId(sms2RemindPriv, String.valueOf(userId)) && findId(typePriv, type))) {//此时能显示提示框
      String sysRemind = getParaValue(conn, "SMS_REMIND");//2 |3 | 1
      String mobileRemind = "";
      mobileRemind =  sysRemind.substring(sysRemind.indexOf("|", 0) + 1, sysRemind.indexOf("|", sysRemind.indexOf("|", 0) + 1));
      if (findId(mobileRemind, type)) {
        moblieRemindFlag = "2"; //显示提示框，且默认选中
      }else {
        moblieRemindFlag = "1";//显示提示框，且默认不选中
      }
    }
    result.append("{").append("moblieRemindFlag:\"").append(moblieRemindFlag).append("\"}");
    return result;
  }
  /**
   * 查找ID是否在串中   * @param str
   * @param id
   * @return
   */
  public boolean findId(String str , String id){
    if (str == null || "".equals(str.trim())) {
      return false;
    }
    String[] strs = str.split(",");
    for (int i = 0; i < strs.length; i++) {
      if ("".equals(strs[i].trim())) {
        continue;
      }
      if (id.trim().equals(strs[i].trim())) {
        return true;
      }
    }
    return false;
  }
  
}

