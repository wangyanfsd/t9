package t9.rad.dsdef.act;

import java.sql.Connection;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import t9.core.data.T9DsTable;
import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Out;
import t9.core.util.db.T9DBUtility;
import t9.rad.dsdef.logic.T9DsDefJsonlogic;
import t9.rad.dsdef.logic.T9DsDefLogic;
import java.lang.reflect.Field;

public class DsDefUpdateAct {
  private static Logger log = Logger.getLogger("t9.rad.dsdef.logic.T9DsDefSubmitAct");


 
  public String testMethod(HttpServletRequest request,
      HttpServletResponse response) throws Exception {

    try {

      Connection dbConn = null;
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      
    
      String tableNo = request.getParameter("tableNo");
      String fieldNo = request.getParameter("fieldNo");
      String id = request.getParameter("id");
     
      String classTable = (String) request.getParameter("T9DsTable");
      
      //System.out.println(classTable);
      
      int idName = Integer.parseInt(request.getParameter("id"));
      T9DsDefFormUpdateAct ds = new T9DsDefFormUpdateAct();
      Object obj = ds.build(request, classTable);
      
      String classField = (String) request.getParameter("T9DsField");
      
      
      //System.out.println(idName+":"+"xxxxxxxxxxxxxxxx");
      
      //////////////////////////////////////////////
      T9DsDefLogic df = new T9DsDefLogic();
      //df.delete(fieldNo, dbConn);
      
//      T9DsDefFormMoreUpdateAct dm = new T9DsDefFormMoreUpdateAct();
//      dm.build(request, classField, idName);

     
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "submit failed");
      throw ex;
    }
    return "/rad/dsdef/jsp/success.jsp";

    // fieldNo, fieldName, fieldDesc,
    // fkTableNo, fkTableNo2, fkRelaFieldNo, fkNameFieldNo, fkFilter,
    // codeClass, defaultValue, formatMode, formatRule, errorMsrg,
    // fieldPrecision, fieldScale, dataType, isPrimKey, isIdentity,
    // displayLen, isMustFill

    // T9DBUtility dbUtil = new T9DBUtility();
    // dbConn = dbUtil.getConnection(false, "sampledb");
    //return null;
  }
}
