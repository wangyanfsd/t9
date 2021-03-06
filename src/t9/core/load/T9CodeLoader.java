package t9.core.load;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import t9.core.dto.T9CodeLoadParam;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;

public class T9CodeLoader {
  /**
   * log                                               
   */
  private static Logger log = Logger.getLogger("yzq.t9.core.load.T9CodeLoader");
  
  /**
   * 加载编码
   * @param dbConn
   * @param param
   * @return
   * @throws Exception
   */
  public static List<String[]> loadData(Connection dbConn, T9CodeLoadParam param) throws Exception {
    
    List rtList = new ArrayList<String[]>();
    Statement stmt = null;
    ResultSet rs = null;
    try {
      String codeField = param.getCodeField();
      String nameField = param.getNameField();
      String tableName = param.getTableName();
      String filterField = param.getFilterField();
      String filterValue = param.getFilterValue();
      String filter = null;
      if (!T9Utility.isNullorEmpty(filterField) && !T9Utility.isNullorEmpty(filterValue)) {
        if (filterValue.equalsIgnoreCase("[null]")) {
          filter = filterField + " is null";
        } else if(filterValue.equalsIgnoreCase("[no]")){
          filter = filterField + " is not null";
        } else {
          filter = filterField + "='" + filterValue + "'";
        }
      }
      String sql = "select " + codeField + ", " + nameField + " from " + tableName;
      if (!T9Utility.isNullorEmpty(filter)) {
        sql += " where " + filter;
      }
      String orderFiled = param.getOrder();
      if (!T9Utility.isNullorEmpty(orderFiled)) {
        sql += " order by " + orderFiled;
      }
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        String code = rs.getString(1);
        String name = rs.getString(2);
        rtList.add(new String[]{code, name});
      }
      return rtList;
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }
}
