package t9.core.funcs.setdescktop.setports.logic;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import org.apache.log4j.Logger;
import t9.core.funcs.setdescktop.setports.data.T9Person;
import t9.core.funcs.system.ispirit.communication.T9MsgPusher;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;

public class T9PersonInfoLogic {
  private static Logger log = Logger.getLogger("t9.core.funcs.setdescktop.setports.act");
  
  public T9Person getPersonInfo(Connection conn,int seqId) throws Exception{
    try {
      T9ORM orm = new T9ORM();
      T9Person pi = (T9Person) orm.loadObjSingle(conn, T9Person.class, seqId);
      return pi;
    } catch(Exception ex) {
      throw ex;
    } finally {
      
    }
  }
  
  public void updatePersonInfo(Connection conn,T9Person pi) throws Exception {
    PreparedStatement ps = null;
    try {
      String sql = "update PERSON" +
      		" set ADD_HOME = ?" +
      		",BP_NO = ?" +
      		",EMAIL = ?" +
      		",FAX_NO_DEPT = ?" +
      		",ICQ = ?" +
      		",MOBIL_NO = ?" +
      		",MOBIL_NO_HIDDEN = ?" +
      		",MSN = ?" +
      		",OICQ = ?" +
      		",POST_NO_HOME = ?" +
      		",USER_NAME = ?" +
      		",TEL_NO_DEPT = ?" +
      		",TEL_NO_HOME = ?" +
      		",SEX = ?" +
      		",BIRTHDAY = ?" +
      		" where SEQ_ID = ?";
      ps = conn.prepareStatement(sql);
      ps.setString(1, pi.getAddHome());
      ps.setString(2, pi.getBpNo());
      ps.setString(3, pi.getEmail());
      ps.setString(4, pi.getFaxNoDept());
      ps.setString(5, pi.getIcq());
      ps.setString(6, pi.getMobilNo());
      ps.setString(7, pi.getMobilNoHidden());
      ps.setString(8, pi.getMsn());
      ps.setString(9, pi.getOicq());
      ps.setString(10, pi.getPostNoHome());
      ps.setString(11, pi.getUserName());
      ps.setString(12, pi.getTelNoDept());
      ps.setString(13, pi.getTelNoHome());
      ps.setString(14, pi.getSex());
      if (pi.getBirthday() != null) {
        Date d = new Date(pi.getBirthday().getTime());
        ps.setDate(15, d);
      }
      else {
        ps.setDate(15, null);
      }
      ps.setInt(16, pi.getSeqId());
      ps.executeUpdate();
      T9MsgPusher.updateOrg(conn);
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(ps, null, log);
    }
  }
}
