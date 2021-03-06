package t9.core.funcs.setdescktop.userinfo.logic;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.form.T9FOM;

public class T9UserinfoLogic {
  private static Logger log = Logger.getLogger("t9.core.funcs.setdescktop.userinfo.act");
  
  public String getSecOnStatus(Connection conn) throws Exception {
    Statement st = null;
    ResultSet rs = null;
    try {
      String sql = "select PARA_VALUE" +
      " from SYS_PARA" +
      " where PARA_NAME = 'SEC_ON_STATUS'";
      
      st = conn.createStatement();
      rs = st.executeQuery(sql);
      
      String value = null;
      if (rs.next()) {
        value = rs.getString("PARA_VALUE");
        if (value != null) {
          return value.trim();
        }
      }
      return value;
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(st, null, log);
    }
  }
  
  public void modifyOnStatus(Connection conn, int seqId, String onStatus) throws Exception {
    PreparedStatement ps = null;
    try {
      String sql = "update PERSON set" +
      		" ON_STATUS = ?" +
      		" where SEQ_ID = ?";
      
      ps = conn.prepareStatement(sql);
      ps.setString(1, onStatus);
      ps.setInt(2, seqId);
      ps.executeUpdate();
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(ps, null, log);
    }
  }
  
  public void modifyStatusUserOnline(Connection conn, int seqId, String onStatus) throws Exception {
    PreparedStatement ps = null;
    try {
      String sql = "update USER_ONLINE set" +
      " USER_STATE = ?" +
      " where USER_ID = ?";
      
      ps = conn.prepareStatement(sql);
      ps.setString(1, onStatus);
      ps.setInt(2, seqId);
      ps.executeUpdate();
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(ps, null, log);
    }
  }
  
  public void modifyMyStatus(Connection conn, int seqId, String myStatus) throws Exception {
    PreparedStatement ps = null;
    try {
      String sql = "update PERSON set" +
      " MY_STATUS = ?" +
      " where SEQ_ID = ?";
      
      ps = conn.prepareStatement(sql);
      ps.setString(1, myStatus);
      ps.setInt(2, seqId);
      ps.executeUpdate();
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(ps, null, log);
    }
  }
  
  public String queryDeptName(Connection conn, int seqId) throws Exception {
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      String sql = "select DEPT_NAME" +
          " from DEPARTMENT" +
          " where SEQ_ID = ?";
      
      ps = conn.prepareStatement(sql);
      ps.setInt(1, seqId);
      rs = ps.executeQuery();
      
      String name = null;
      if (rs.next()) {
        name = rs.getString("DEPT_NAME");
      }
      return name;
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
  }
  
  public String queryPrivName(Connection conn, int seqId) throws Exception {
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      String sql = "select PRIV_NAME" +
      " from USER_PRIV" +
      " where SEQ_ID = ?";
      
      ps = conn.prepareStatement(sql);
      ps.setInt(1, seqId);
      rs = ps.executeQuery();
      
      String name = null;
      if (rs.next()) {
        name = rs.getString("PRIV_NAME");
      }
      return name;
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
  }
  
  public void addUserParam(Connection dbConn, Map<String, String> map, T9Person person) throws Exception {
    Map<String, String> param = T9FOM.json2Map(person.getParamSet());
    param.putAll(map);
    String paramStr = T9FOM.toJson(param).toString();
    person.setParamSet(paramStr);
    
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      String sql = "update PERSON" +
      		" set PARAM_SET = ?" +
      		" where SEQ_ID = ?";
      
      ps = dbConn.prepareStatement(sql);
      ps.setString(1, paramStr);
      ps.setInt(2, person.getSeqId());
      ps.executeUpdate();
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
  }
  
  public void updateWeatherCity(Connection dbConn, T9Person person) throws Exception {
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      String sql = "update PERSON" +
      " set WEATHER_CITY = ?" +
      " where SEQ_ID = ?";
      
      ps = dbConn.prepareStatement(sql);
      ps.setString(1, person.getWeatherCity());
      ps.setInt(2, person.getSeqId());
      ps.executeUpdate();
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
  }
  
  public Map<String, String> queryInfo(Connection dbConn, T9Person user) throws Exception {
    int userPriv = 0;
    try {
      userPriv = Integer.parseInt(user.getUserPriv()); 
    } catch (NumberFormatException e) {
      
    }
    Map<String,String> map = new HashMap<String,String>();
    
    map.put("deptName", queryDeptName(dbConn, user.getDeptId()));
    map.put("privName", queryPrivName(dbConn, userPriv));
    map.put("userName", user.getUserName());
    map.put("onLine", String.valueOf(user.getOnLine()));
    map.put("onStatus", user.getOnStatus());
    map.put("myStatus", user.getMyStatus());
    map.put("avatar", user.getAuatar());
    map.put("notViewTable", user.getNotViewTable());
    map.put("notViewUser", user.getNotViewUser());
    map.put("panel", user.getPanel());
    map.put("smsOn", user.getSmsOn());
    map.put("sex", user.getSex());
    map.put("menuType", user.getMenuType());
    map.put("callSound", user.getCallSound());
    map.put("seqId", String.valueOf(user.getSeqId()));
    map.put("nevMenuOpen", user.getNevMenuOpen());
    map.put("menuExpand", user.getMenuExpand());
    map.putAll(T9FOM.json2Map(user.getParamSet()));
    return map;
  }
  
  public Map<String, String> queryCardInfo(Connection dbConn, T9Person user) throws Exception {
    int userPriv = 0;
    try {
      userPriv = Integer.parseInt(user.getUserPriv()); 
    } catch (NumberFormatException e) {
      
    }
    
    File file = new File(T9SysProps.getAttachPath() 
        + File.separator 
        + "hrms_pic" 
        + user.getUserId());
    
    Map<String,String> map = new HashMap<String,String>();
    
    String mobilNo = "";
    if ("0".equals(user.getMobilNoHidden())) {
      mobilNo = user.getMobilNo();
    }
    map.put("deptName", T9Utility.encodeSpecial(queryDeptName(dbConn, user.getDeptId())));
    map.put("privName", T9Utility.encodeSpecial(queryPrivName(dbConn, userPriv)));
    map.put("userName", T9Utility.encodeSpecial(user.getUserName()));
    map.put("tel", T9Utility.encodeSpecial(user.getTelNoDept()));
    map.put("mobilNo", mobilNo);
    map.put("email", T9Utility.encodeSpecial(user.getEmail()));
    map.put("email", T9Utility.encodeSpecial(user.getEmail()));
    map.put("qq", T9Utility.encodeSpecial(user.getOicq()));
    map.put("photo", T9Utility.encodeSpecial(user.getPhoto()));
    return map;
  }
  
  public static Map<String,String> resultSet2Map(ResultSet rs) throws SQLException {
    Map<String,String> map = new HashMap<String,String>();
    if(rs.next()){
      ResultSetMetaData rsMeta = rs.getMetaData();
      for(int i = 0; i < rsMeta.getColumnCount(); ++i){   
        String columnName = rsMeta.getColumnName(i+1);   
        map.put(rsMeta.getColumnName(i+1), null == rs.getString(columnName) ? "" : rs.getString(columnName).trim()); 
     }
    }
    return map;
  }
}
