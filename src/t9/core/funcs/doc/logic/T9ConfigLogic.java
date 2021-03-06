package t9.core.funcs.doc.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import t9.core.funcs.doc.util.T9WorkFlowUtility;
import t9.core.util.db.T9DBUtility;
public class T9ConfigLogic {
  public String getSysPar (String par  , Connection conn) throws Exception {
    String result = "";
    String query = "select PARA_VALUE from SYS_PARA where PARA_NAME='" + par + "'";
    Statement stm = null;
    ResultSet rs = null;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      if (rs.next()){
        result = T9WorkFlowUtility.clob2String(rs.getClob("PARA_VALUE"));
        if (result == null) {
          result = "";
        }
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
    return result;
  }
  public void updateSysPar (String par ,String parValue , Connection conn) throws Exception {
    boolean isExist = false;
    String query = "select PARA_VALUE from SYS_PARA where PARA_NAME='" + par + "'";
    Statement stm1 = null;
    ResultSet rs1 = null;
    try {
      stm1 = conn.createStatement();
      rs1 = stm1.executeQuery(query);
      if (rs1.next()){
        isExist = true;
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm1, rs1, null); 
    }
    if (isExist) {
      query  = "update SYS_PARA set PARA_VALUE ='"+ parValue +"' where PARA_NAME='" + par + "'";
    } else {
      query = "insert into SYS_PARA values(null , '"+ par +"','"+ parValue +"')";
    }
    Statement stm = null;
    try {
      stm = conn.createStatement();
      stm.executeUpdate(query);
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, null, null); 
    }
  }
}
