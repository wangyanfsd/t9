package t9.core.funcs.setdescktop.fav.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.funcs.system.url.data.T9Url;
import t9.core.global.T9SysProps;

public class T9FavLogic {
  
  private static Logger log = Logger.getLogger("t9.core.funcs.setdescktop.fav.act");
  
  public void addUrl(Connection conn,T9Url url) throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null;
    
    try{
      String tmp = "USER";
      String ms = T9SysProps.getProp("db.jdbc.dbms");
      if ("oracle".equals(ms)) {
        tmp = "\"USER\"";
      } else if ("sqlserver".equals(ms)) {
        tmp = "[USER]";
      }
      String sql = "insert into URL" +
      		" ( URL_DESC,URL_NO,URL," + tmp + " )" +
      		" values( ?,?,?,?)";
      ps = conn.prepareStatement(sql);
      ps.setString(1, url.getUrlDesc());
      ps.setInt(2, url.getUrlNo());
      ps.setString(3, url.getUrl());
      ps.setString(4, url.getUser());
      ps.executeUpdate();
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(ps, rs, log);
    }
  }
  
  public void modifyUrl(Connection conn,T9Url url) throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null;
    
    try{
      String sql = "update URL set" +
          " URL = ?" +
          ",URL_DESC = ?" +
          ",URL_NO = ?" +
          "," + ("oracle".equals(T9SysProps.getProp("db.jdbc.dbms")) ? "\"USER\"" : "USER") + " = ?" +
          " where SEQ_ID = ?";
      ps = conn.prepareStatement(sql);
      ps.setString(1, url.getUrl());
      ps.setString(2, url.getUrlDesc());
      ps.setInt(3, url.getUrlNo());
      ps.setString(4, url.getUser());
      ps.setInt(5, url.getSeqId());
      ps.executeUpdate();
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(ps, rs, log);
    }
  }
  
  public List list(Connection conn,int seqId) throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null;
    
    try{
      T9ORM orm = new T9ORM();
      List<Map<String,String>> list = new ArrayList<Map<String,String>>();
      

      String sql = "select URL" +
      		",SEQ_ID" +
          ",URL_DESC" +
          ",URL_NO" +
          " from URL" +
          " where " + ("oracle".equals(T9SysProps.getProp("db.jdbc.dbms")) ? "\"USER\"" : "USER") + " = ?" +
          " order by URL_NO";
      
      ps = conn.prepareStatement(sql);
      ps.setString(1, String.valueOf(seqId));
      rs = ps.executeQuery();
      while(rs.next()){
        
        Map<String,String> map = new HashMap<String,String>();
        
        String id = rs.getString("SEQ_ID");
        String url = rs.getString("URL");
        String openType = url.startsWith("1:") ? "1" : "0";
        url = url.startsWith("1:") ? url.substring(2) : url;
        String text = rs.getString("URL_DESC");
        
        map.put("id", id);
        map.put("iconCls", "icon-url");
        map.put("leaf", "1");
        map.put("openFlag", openType);
        map.put("text", T9Utility.encodeSpecial(text));
        map.put("url", T9Utility.encodeSpecial(url));
        map.put("leaf", "true");
        
        list.add(map);
      }
      return list;
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(ps, rs, log);
    }
  }
  
  public void deleteUrl(Connection dbConn,int SeqId) throws Exception{
    try{
      T9ORM orm = new T9ORM();
      orm.deleteSingle(dbConn, T9Url.class, SeqId);
    }catch(Exception e){
      throw e;
    }finally{
      
    }
  }
  
  
  private List<Map> resultSet2List(ResultSet rs) throws SQLException{
    List<Map> list = new ArrayList<Map>();
    ResultSetMetaData rsMeta = rs.getMetaData();
    while(rs.next()){
      Map<String,String> map = new HashMap<String,String>();
      for(int i = 0; i < rsMeta.getColumnCount(); ++i){   
        String columnName = rsMeta.getColumnName(i+1);   
        map.put(rsMeta.getColumnName(i+1), null == rs.getString(columnName)?"":rs.getString(columnName)); 
      }
      list.add(map);
    }
    return list;
  }
}
