package t9.core.funcs.workflow.logic;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import t9.core.funcs.sms.data.T9SmsBack;
import t9.core.funcs.sms.logic.T9SmsUtil;
import t9.core.funcs.workflow.util.T9FlowRunUtility;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.util.db.T9DBUtility;

public class T9FlowRunAssistLogic {
  public void addPrcsUser(Connection conn , int prcsId , int runId  , int flowId , int flowPrcs , int toId , String sortId , String skin, int userId , String contextPath) throws Exception {
    String query = "select TOP_FLAG,PARENT FROM FLOW_RUN_PRCS WHERE RUN_ID='"+ runId +"' AND PRCS_ID='"+ prcsId +"'";
    if (flowPrcs > 0) {
      query += " and FLOW_PRCS='" + flowPrcs + "'";
    }
    Statement stm2 = null;
    ResultSet rs2 = null;
    String topFlag = "0";
    String parent = null;
    try {
      stm2 = conn.createStatement();
      rs2 = stm2.executeQuery(query);
      if (rs2.next()) {
        topFlag = rs2.getString("TOP_FLAG");
        parent = rs2.getString("PARENT");
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm2, rs2, null); 
    }
    String insert = "insert into FLOW_RUN_PRCS (RUN_ID,PRCS_ID,FLOW_PRCS,USER_ID,PRCS_FLAG,OP_FLAG,TOP_FLAG,PARENT) values ('"+runId+"','"+ prcsId +"','"+ flowPrcs +"','"+toId+"',1,0,'"+ topFlag +"',?)";
    
    PreparedStatement stm3 = null;
    int result = 0 ;
    try {
      stm3 = conn.prepareStatement(insert);
      stm3.setString(1, parent);
      result = stm3.executeUpdate();
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm3, null, null); 
    }
    if (result != 0) {
      //工作流日志
      T9WorkFlowUtility fu = new T9WorkFlowUtility();
      String userName  = fu.getUserNameById(toId, conn);
      String content = "增加经办人：" + userName ;
      T9FlowRunLogLogic log = new T9FlowRunLogLogic();
      log.runLog(runId, prcsId, flowPrcs, userId , 7, content, "", conn);
      
      //内部短信提醒
      T9FlowRunUtility ru = new T9FlowRunUtility();
      String runName = ru.getRunNameById(runId, conn);
      content = "您有新的待办工作待您处理:" + runName;
      T9SmsBack sb = new T9SmsBack();
      sb.setSmsType("7");
      sb.setContent(content);
      sb.setFromId(userId);
      sb.setToId(String.valueOf(toId));
      sb.setRemindUrl( "/core/funcs/workflow/flowrun/list/inputform/index.jsp?skin="+ skin +"&sortId="+sortId +"&runId=" + runId + "&flowId=" + flowId + "&prcsId=" + prcsId + "&flowPrcs=" + flowPrcs);
      T9SmsUtil.smsBack(conn, sb);
    }
  }

  public String getOutline(int flowId, int runId, int userId, Connection conn) throws Exception {
    // TODO Auto-generated method stub
    String query = "select p.run_id,p.flow_prcs,r.run_name from flow_run_prcs p , flow_run r  where r.run_id = p.run_id and r.flow_id = " + flowId 
      + " and p.prcs_id=1 "
      + " and p.user_id=" + userId
      + " and r.END_TIME is null and r.DEL_FLAG='0'"
      + " and p.PRCS_FLAG='2'";
    StringBuffer sb = new StringBuffer();
    sb.append("[");
    int count = 0 ;
    Statement stm = null;
    ResultSet rs = null;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      while (rs.next()) {
        int runId2 = rs.getInt("run_id");
        if (runId2 != runId) {
          sb.append("{name:\"");
          String runName = rs.getString("run_name");
          sb.append("<div title='"+ runName +"' style='padding-top:5px;margin-left:5px;height:16px'>");
          if (runName.length() > 18) {
            runName = runName.substring(0 , 18) + "...";
          }
          sb.append(runName);
          sb.append("<div>\"");
          sb.append(",runId:" + runId2);
          sb.append(",action:useOutline");
          sb.append("},");
          count++;
        }
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null);
    }
    if (count > 0 ) {
      sb.deleteCharAt(sb.length() - 1);
    } else {
      sb.append("{name:\"");
      sb.append("<div style='padding-top:5px;margin-left:5px;height:16px'>");
      sb.append("无草稿");
      sb.append("<div>\"");
      sb.append("}");
    }
    sb.append("]");
    return sb.toString();
  }
  
}
