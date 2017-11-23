package t9.rad.dsdef.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9DsField;
import t9.core.data.T9DsTable;
import t9.core.data.T9RequestDbConn;
import t9.core.exps.T9InvalidParamException;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.db.T9DBUtility;

public class T9DsDefLogic {

  private static Logger log = Logger
      .getLogger("cc.t9.core.act.action.T9TestAct");
  int totles = 0;

  public void DsDefUpdateTable(Connection dbConn, String tableNo, Object value,
      String tableNoNew) throws Exception {
    // TABLE_NO='" + table.getTableNo()+ "',
    T9DsTable table = (T9DsTable) value;
    Statement stmt = null;
    ResultSet rs = null;
    String sql = "UPDATE DS_TABLE SET TABLE_NO = '" + table.getTableNo()
        + "', TABLE_NAME='" + table.getTableName() + "', CLASS_NAME='"
        + table.getClassName() + "', TABLE_DESC='" + table.getTableDesc()
        + "',CATEGORY_NO='" + table.getCategoryNo() + "' WHERE TABLE_NO = '"
        + tableNo + "'";
    try {
      //System.out.println(sql);
      stmt = dbConn.createStatement();
      int i = stmt.executeUpdate(sql);
      //System.out.println(i);
      //System.out.println(sql);
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }

  public void DsDefUpdateField(Connection dbConn, String tableNo, Object value)
      throws Exception {
    T9DsField field = (T9DsField) value;
    Statement stmt = null;
    ResultSet rs = null;
    String sql = "UPDATE DSFIELD SET TABLENO='" + field.getTableNo()
        + "', FIELEDNO='" + field.getFieldNo() + "', FIELEDNAME='"
        + field.getFieldName() + "', PROPNAME='" + field.getPropName()
        + "', FIELEDDESC='" + field.getFieldDesc() + "',FKTABLENO='"
        + field.getFkTableNo() + "',FKTABLENO2='" + field.getFkTableNo2()
        + "',FKRELAFIELDNO='" + field.getFkRelaFieldNo() + "',FKNAMEFIELDNO='"
        + field.getFkNameFieldNo() + "',FKFILTER='" + field.getFkFilter()
        + "',CODECLASS='" + field.getCodeClass() + "',DEFAULTVALUE='"
        + field.getDefaultValue() + "',FORMATMODE='" + field.getFormatMode()
        + "',FORMATRULE='" + field.getFormatRule() + "',ERRORMSRG='"
        + field.getErrorMsrg() + "',FIELDPRECISION='"
        + field.getFieldPrecision() + "',FIELDSCALE='" + field.getFieldScale()
        + "',DATATYPE='" + field.getDataType() + "',ISPRIMKEY='"
        + field.getIsPrimaryKey() + "',ISIDENTITY='" + field.getIsIdentity()
        + "',DISPLAYLEN='" + field.getDisplayLen() + "',ISMUSTFILL='"
        + field.getIsMustFill() + "'WHERE TABLENO='" + tableNo + "'";
    try {
      stmt = dbConn.createStatement();
      int i = stmt.executeUpdate(sql);
      //System.out.println(i);
      //System.out.println(sql);
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }

  public String DsDefInsert(Connection dbConn, Object value, String tableNo)
      throws Exception {
    T9DsTable table = (T9DsTable) value;
    Statement stmt = null;
    ResultSet rs = null;
    String sql = "INSERT INTO DS_TABLE(TABLE_NO, TABLE_NAME, CLASS_NAME, TABLE_DESC, CATEGORY_NO) VALUES("
        + "'" + table.getTableNo() + "'"
        + ",'" + table.getTableName() + "'"        
        + ",'" + table.getClassName() + "'"        
        + ",'" + table.getTableDesc() + "'"        
        + ",'" + table.getCategoryNo() + "')";
    try {
      stmt = dbConn.createStatement();
      //System.out.println(stmt);
      int i = stmt.executeUpdate(sql);
      //System.out.println(i);
      //System.out.println(sql);
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return null;
  }
  
  public void DsDefInsertZ(Connection dbConn, String tableNo, Object value)
      throws Exception {
    //System.out.println(tableNo + "ddddddddddddddddddddddfffffffffffffffff");
    T9DsField field = (T9DsField) value;
    //System.out.println(field.getIsPrimaryKey() + "7777");
    Statement stmt = null;
    ResultSet rs = null;
    String sql = "INSERT INTO DS_FIELD(TABLE_NO, FIELD_NO, FIELD_NAME, PROP_NAME,FIELD_DESC, FK_TABLE_NO, FK_TABLE_NO2, FK_RELA_FIELD_NO, FK_NAME_FIELD_NO, FK_FILTER, CODE_CLASS, DEFAULT_VALUE, FORMAT_MODE, FORMAT_RULE, ERROR_MSRG, FIELD_PRECISION, FIELD_SCALE, DATA_TYPE, IS_PRIMARY_KEY, IS_IDENTITY, DISPLAY_LEN, IS_MUST_FILL, FK_NAME_FIELD_NO2) VALUES('"
        + tableNo
        + "', '"
        + field.getFieldNo()
        + "', '"
        + field.getFieldName()
        + "', '"
        + field.getPropName()
        + "', '"
        + field.getFieldDesc()
        + "', '"
        + field.getFkTableNo()
        + "', '"
        + field.getFkTableNo2()
        + "', '"
        + field.getFkRelaFieldNo()
        + "', '"
        + field.getFkNameFieldNo()
        + "', '"
        + field.getFkFilter()
        + "', '"
        + field.getCodeClass()
        + "', '"
        + field.getDefaultValue()
        + "', '"
        + field.getFormatMode()
        + "', '"
        + field.getFormatRule()
        + "', '"
        + field.getErrorMsrg()
        + "', '"
        + field.getFieldPrecision()
        + "', '"
        + field.getFieldScale()
        + "', '"
        + field.getDataType()
        + "', '"
        + field.getIsPrimaryKey()
        + "', '"
        + field.getIsIdentity()
        + "', '"
        + field.getDisplayLen()
        + "', '"
        + field.getIsMustFill() 
        + "', '"
        + field.getFkNameFieldNo2() + "')";
    try {
      stmt = dbConn.createStatement();
      //System.out.println(sql);
      stmt.executeUpdate(sql);
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }

  public void insertDsDef(Connection dbConn, String tableNo, Object value)
      throws Exception {
    //System.out.println(tableNo + "ddddddddddddddddddddddfffffffffffffffff");
    T9DsTable table = (T9DsTable) value;
    T9DsField field = (T9DsField) value;
    Statement stmt = null;
    ResultSet rs = null;
    String sqlTable = "INSERT INTO DS_TABLE(TABLE_NO, TABLE_NAME, CLASS_NAME, TABLE_DESC, CATEGORY_NO) VALUES('"
        + table.getTableNo()
        + "','"
        + table.getTableName()
        + "','"
        + table.getClassName()
        + "', '"
        + table.getTableDesc()
        + "', '"
        + table.getCategoryNo() + "')";
    String sql = "INSERT INTO DS_FIELD(TABLE_NO, FIELD_NO, FIELD_NAME, PROP_NAME,FIELD_DESC, FK_TABLE_NO, FK_TABLE_NO2, FK_RELA_FIELD_NO, FK_NAME_FIELD_NO, FK_FILTER, CODE_CLASS, DEFAULT_VALUE, FORMAT_MODE, FORMAT_RULE, ERROR_MSRG, FIELD_PRECISION, FIELD_SCALE, DATA_TYPE, IS_PRIMARY_KEY, IS_IDENTITY, DISPLAY_LEN, IS_MUST_FILL) VALUES('"
        + tableNo
        + "', '"
        + field.getFieldNo()
        + "', '"
        + field.getFieldName()
        + "', '"
        + field.getPropName()
        + "', '"
        + field.getFieldDesc()
        + "', '"
        + field.getFkTableNo()
        + "', '"
        + field.getFkTableNo2()
        + "', '"
        + field.getFkRelaFieldNo()
        + "', '"
        + field.getFkNameFieldNo()
        + "', '"
        + field.getFkFilter()
        + "', '"
        + field.getCodeClass()
        + "', '"
        + field.getDefaultValue()
        + "', '"
        + field.getFormatMode()
        + "', '"
        + field.getFormatRule()
        + "', '"
        + field.getErrorMsrg()
        + "', '"
        + field.getFieldPrecision()
        + "', '"
        + field.getFieldScale()
        + "', '"
        + field.getDataType()
        + "', '"
        + field.getIsPrimaryKey()
        + "', '"
        + field.getIsIdentity()
        + "', '"
        + field.getDisplayLen()
        + "', '"
        + field.getIsMustFill() + "')";
    try {
      stmt = dbConn.createStatement();
      //System.out.println(sql);
      rs = stmt.executeQuery(sqlTable);
      rs = stmt.executeQuery(sql);

    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }

  public void deleteUpdate(String tableSeqId, Connection dbConn, String tableNoF)
      throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    String sql1 = "DELETE FROM DS_FIELD WHERE TABLE_NO IN(SELECT TABLE_NO FROM DS_TABLE WHERE SEQ_ID='"
        + tableSeqId + "')";
    String sql = "DELETE FROM DS_TABLE WHERE SEQ_ID='" + tableSeqId + "'";
    //System.out.print(sql);
    try {
      stmt = dbConn.createStatement();
      stmt.executeUpdate(sql1);
      stmt.executeUpdate(sql);
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }

  public void delete(String tableNoField, Connection dbConn) throws Exception {
    //System.out.println(tableNoField + "<<<<<<<<<<<<<<<<<<<<<<,");
    Statement stmt = null;
    ResultSet rs = null;
    String sql = "DELETE FROM DS_FIELD WHERE table_No='" + tableNoField + "'";
    try {
      stmt = dbConn.createStatement();
      stmt.executeUpdate(sql);
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }

  public List selectTable(Connection dbConn, int total) throws Exception {
    List list = new ArrayList();
    T9DsTable dt = null;
    Statement stmt = null;
    ResultSet rs = null;
    String sql = "SELECT * FROM DS_TABLE";
    try {
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        dt = new T9DsTable();
        dt.setSeqId(rs.getInt("SEQ_ID"));
        dt.setTableNo(rs.getString("table_No"));
        dt.setTableName(rs.getString("table_Name"));
        dt.setTableDesc(rs.getString("table_Desc"));
        dt.setClassName(rs.getString("class_Name"));
        dt.setCategoryNo(rs.getString("category_No"));
        dt.setDbNo(rs.getString("db_No"));
        list.add(dt);
      }
      sql = "SELECT COUNT(*) FROM DS_TABLE";
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        totles = rs.getInt(1);
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return list;
  }

  public int getTotles() {
    return totles;
  }

  public List selectTableField(Connection dbConn, String tableNo) {
    List list = new ArrayList();
    T9DsTable dt = null;
    T9DsField df = null;
    Statement stmt = null;
    ResultSet rs = null;
    String sql1 = "SELECT tableNo FROM DS_TABLE JOIN DSFIELD ON DSTABLE.TABLENO = DSFIELD.TABLENO";
    String sql = "SELECT * FROM DS_FIELD WHERE TABLE_NO = '" + tableNo + "'";
    try {
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        df = new T9DsField();
        df.setSeqId(rs.getInt("SEQ_ID"));
        df.setTableNo(rs.getString("TABLE_NO"));
        df.setFieldNo(rs.getString("FIELD_NO"));
        df.setFieldName(rs.getString("FIELD_NAME"));
        df.setFieldDesc(rs.getString("FIELD_DESC"));
        df.setPropName(rs.getString("PROP_NAME"));
        df.setFkTableNo(rs.getString("FK_TABLE_NO"));
        df.setFkTableNo2(rs.getString("FK_TABLE_NO2"));
        df.setFkRelaFieldNo(rs.getString("FK_RELA_FIELD_NO"));
        df.setFkNameFieldNo(rs.getString("FK_NAME_FIELD_NO"));
        df.setFkFilter(rs.getString("FK_FILTER"));
        df.setCodeClass(rs.getString("CODE_CLASS"));
        df.setDefaultValue(rs.getString("DEFAULT_VALUE"));
        df.setFormatMode(rs.getString("FORMAT_MODE"));
        df.setFormatRule(rs.getString("FORMAT_RULE"));
        df.setErrorMsrg(rs.getString("ERROR_MSRG"));
        df.setFieldPrecision(rs.getInt("FIELD_PRECISION"));
        df.setFieldScale(rs.getInt("FIELD_SCALE"));
        df.setDataType(rs.getInt("DATA_TYPE"));
        df.setIsPrimaryKey(rs.getString("IS_PRIMARY_KEY"));
        df.setIsIdentity(rs.getString("IS_IDENTITY"));
        df.setDisplayLen(rs.getInt("DISPLAY_LEN"));
        df.setIsMustFill(rs.getString("IS_MUST_FILL"));
        // list.add(dt);
        list.add(df);
      }
    } catch (Exception ex) {
      //System.out.println(ex.getMessage());
      // throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return list;
  }
  
  public static void main(String args[]) {
    T9RequestDbConn requestDbConn = new T9RequestDbConn("");
    T9DsDefLogic t = new T9DsDefLogic();
    try {
      List list = t.selectTableField(requestDbConn.getSysDbConn(), "11111");
    }catch (SQLException e) {
      // TODO Auto-generated catch block
      //System.out.println(e.getMessage());
    }
  }

  public void deleteDsDef(String tableSeqId, Connection dbConn, String tableNoF)
      throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    String sql1 = "DELETE FROM DS_FIELD WHERE TABLE_NO IN(SELECT TABLE_NO FROM DS_TABLE WHERE SEQ_ID='"
        + tableSeqId + "')";
    String sql = "DELETE FROM DS_TABLE WHERE SEQ_ID='" + tableSeqId + "'";
    //System.out.print(sql);
    try {
      stmt = dbConn.createStatement();
      stmt.executeUpdate(sql1);
      stmt.executeUpdate(sql);
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }

  public boolean existsTableNo(Connection dbConn, String tableNo, String seqId)
      throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = dbConn.createStatement();
      String sql = "SELECT count(*) FROM DS_TABLE WHERE TABLE_NO = '" + tableNo + "' and SEQ_ID !=" + seqId;
//      String sqlField = "SELECT count(*) FROM DS_FIELD WHERE FIELD_NO = '" + tableNo
//      + "'";
      rs = stmt.executeQuery(sql);
      //rs = stmt.executeQuery(sqlField);
      long count = 0;
      if (rs.next()) {
        count = rs.getLong(1);
      }
      if (count == 1) {
        return true;
      } else {
        return false;
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }
}
