package t9.core.funcs.workflow.act;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9DbRecord;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.jexcel.logic.T9ExportLogic;
import t9.core.funcs.jexcel.util.T9JExcelUtil;
import t9.core.funcs.workflow.logic.T9WorkLogLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;

public class T9WorkLogAct {
  private static Logger log = Logger.getLogger("t9.core.funcs.workflow.act.T9WorkLogAct");
  public String getWorkLogList(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
      
      Connection dbConn = null;
      try {
        T9RequestDbConn requestDbConn = (T9RequestDbConn) request
            .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
        T9WorkLogLogic  wll = new T9WorkLogLogic();
        StringBuffer result = wll.getWorklogListLogic(dbConn, request.getParameterMap());
        PrintWriter pw = response.getWriter();
        pw.println( result.toString());
        pw.flush();
      } catch (Exception ex) {
        ex.printStackTrace();
        throw ex;
      }
      return null;
    }
  /**
   * 删除日志
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteLog(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
      Connection dbConn = null;
      try {
        T9RequestDbConn requestDbConn = (T9RequestDbConn) request
            .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
        T9WorkLogLogic  wll = new T9WorkLogLogic();
        String seqId = request.getParameter("seqId");
        int result = wll.deleteLog(dbConn, seqId);
        request.setAttribute(T9ActionKeys.RET_DATA,"\"" + result + "\"");
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      } catch (Exception ex) {
        ex.printStackTrace();
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
        throw ex;
      }
      return "/core/inc/rtjson.jsp";
    }
  /**
   * 删除日志
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteLogBySearch(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
      Connection dbConn = null;
      try {
        T9RequestDbConn requestDbConn = (T9RequestDbConn) request
            .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
        T9WorkLogLogic  wll = new T9WorkLogLogic();
        StringBuffer seqIds = wll.getAlldeleteSeqId(dbConn, request.getParameterMap());
        int result = 0;
        String seqIdStr = seqIds.toString();
        String[] seqIdArray = seqIdStr.split("\\*");
        for (int i = 0; i < seqIdArray.length; i++) {
          if(!"".equals(seqIdArray[i])){
            result += wll.deleteLog(dbConn, seqIdArray[i]);
          }
        }
        request.setAttribute(T9ActionKeys.RET_DATA,"\"" + result + "\"");
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
       } catch (Exception ex) {
        ex.printStackTrace();
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
        throw ex;
      }
      return "/core/inc/rtjson.jsp";
    }
  /**
   * 删除日志
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getPrcsName(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
      Connection dbConn = null;
      try {
        String flowId = request.getParameter("flowId");
        String flowPrcs = request.getParameter("flowPrcs");
        T9RequestDbConn requestDbConn = (T9RequestDbConn) request
            .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
        T9WorkLogLogic  wll = new T9WorkLogLogic();
         String result = wll.getPrcsName(dbConn, flowId, flowPrcs);
        request.setAttribute(T9ActionKeys.RET_DATA,"\"" + result + "\"");
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      } catch (Exception ex) {
        ex.printStackTrace();
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
        throw ex;
      }
      return "/core/inc/rtjson.jsp";
    }
  /**
   * 删除日志
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String export(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    OutputStream ops = null;
    Connection conn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      String fileName = URLEncoder.encode("工作流日志.xls","UTF-8");
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control","private");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Accept-Ranges","bytes");
      response.setHeader("Content-disposition","attachment; filename=\"" + fileName + "\"");
      ops = response.getOutputStream();
      T9WorkLogLogic  wll = new T9WorkLogLogic();
      ArrayList<T9DbRecord > dbL = wll.toExportData(conn, request.getParameterMap());
      T9JExcelUtil.writeExc(ops, dbL);
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    } finally {
      ops.close();
    }
    return null;
  }
  
}
