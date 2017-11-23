package t9.core.funcs.address.act;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.log4j.Logger;

import t9.core.data.T9DbRecord;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.jexcel.util.T9CSVUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.address.data.T9Address;
import t9.core.funcs.address.data.T9AddressGroup;
import t9.core.funcs.address.logic.T9AddressLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.form.T9FOM;
public class T9AddressAct {
  private static Logger log = Logger.getLogger("cc.t9.core.funcs.system.address.T9AddressAct");

  /**
   * mysql findInSet 处理
   * @param str
   * @param dbFieldName
   * @return
   * @throws SQLException
   */
  public String findInSet(String str,String dbFieldName) throws SQLException{
    String dbms = T9SysProps.getProp("db.jdbc.dbms");
    String result = "";
    if (dbms.equals("sqlserver")) {
      result = "find_in_set('" +str+ "'," + dbFieldName + ")";
    }else if (dbms.equals("mysql")) {
      result = "find_in_set('" +str+ "'," + dbFieldName + ")";
    }else if (dbms.equals("oracle")) {
      result = "instr(" + dbFieldName + ",'" +str+ "') > 0";
    }else {
      throw new SQLException("not accepted dbms");
    }
    
    return result;
  }
  
  /**
   * 个人通讯簿：索引(按姓氏)
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getPrivateMb(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int loginSeqId = person.getSeqId();
      List<Map> list = new ArrayList();
      //StringBuffer sb = new StringBuffer("[");
      T9AddressLogic addLogic = new T9AddressLogic();
      String mbStrs = addLogic.getAddressMb(dbConn ,loginSeqId);
      //mbStrs = "";
      if(T9Utility.isNullorEmpty(mbStrs)){
        mbStrs = "[]";
      }
      //String sbStr = addLogic.getResultMb(mbStrs);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, mbStrs);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  
  public String getPublicMb(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      String groupIdStr = request.getParameter("groupIdStr");
      groupIdStr += groupIdStr + "0,";
      int loginSeqId = person.getSeqId();
      List<Map> list = new ArrayList();
      T9ORM orm = new T9ORM();
      HashMap map = null;
      //StringBuffer sb = new StringBuffer("[");
      T9AddressLogic addLogic = new T9AddressLogic();
      String mbStrs = addLogic.getAddressPublicMb(dbConn ,loginSeqId, groupIdStr);
      //mbStrs = "";
      if(T9Utility.isNullorEmpty(mbStrs)){
        mbStrs = "[]";
      }
      //String sbStr = addLogic.getResultMb(mbStrs);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, mbStrs);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 个人通讯簿：索引（姓氏）读取列表数据
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getNameIndex(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      String seqIdStr = request.getParameter("seqId");
      String seqId = seqIdStr.substring(0, seqIdStr.length() - 1);
      T9AddressLogic dl = new T9AddressLogic();
      String data = dl.getNameIndexJson(dbConn,request.getParameterMap(), seqId);
      
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  
  public String getNameIndex1(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String userName = "";
    ArrayList<T9Address> addressList = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      userName = person.getUserName();
      StringBuffer sb = new StringBuffer("[");
      //int seqId = Integer.parseInt(request.getParameter("seqId"));
      String seqIdStr = request.getParameter("seqId");
      String seqId = seqIdStr.substring(0, seqIdStr.length() - 1);
      //System.out.println(seqId+"YIGFHGFGH");
      T9AddressLogic addl = new T9AddressLogic();
      addressList = addl.getNameIndexJson1(dbConn, seqId);
      for(int i = 0; i < addressList.size(); i++){
        T9Address address = addressList.get(i);
        sb.append("{");
        sb.append("seqId:\"" + address.getSeqId() + "\"");
        sb.append(",userId:\"" + (address.getUserId() == null ? "" : address.getUserId()) + "\"");
        sb.append(",groupId:\"" + address.getGroupId() + "\"");
        sb.append(",psnName:\"" + (address.getPsnName() == null ? "" : address.getPsnName()) + "\"");
        sb.append(",sex:\"" + (address.getSex() == null ? "" : address.getSex()) + "\"");
        sb.append(",nickName:\"" + (address.getNickName() == null ? "" : address.getNickName()) + "\"");
        sb.append(",birthday:\"" + (address.getBirthday() == null ? "" : address.getBirthday()) + "\"");
        sb.append(",ministration:\"" + (address.getMinistration() == null ? "" : address.getMinistration()) + "\"");
        sb.append(",mate:\"" + (address.getMate() == null ? "" : address.getMate()) + "\"");
        sb.append(",child:\"" + (address.getChild() == null ? "" : address.getChild()) + "\"");
        sb.append(",deptName:\"" + (address.getDeptName() == null ? "" : address.getDeptName()) + "\"");
        sb.append(",addDept:\"" + (address.getAddDept() == null ? "" : address.getAddDept()) + "\"");
        sb.append(",postNoDept:\"" + (address.getPostNoDept() == null ? "" : address.getPostNoDept()) + "\"");
        sb.append(",telNoDept:\"" + (address.getTelNoDept() == null ? "" : address.getTelNoDept()) + "\"");
        sb.append(",faxNoDept:\"" + (address.getFaxNoDept() == null ? "" : address.getFaxNoDept()) + "\"");
        sb.append(",addHome:\"" + (address.getAddHome() == null ? "" : address.getAddHome()) + "\"");
        sb.append(",postNoHome:\"" + (address.getPostNoHome() == null ? "" : address.getPostNoHome()) + "\"");
        sb.append(",telNoHome:\"" + (address.getTelNoHome() == null ? "" : address.getTelNoHome()) + "\"");
        sb.append(",mobilNo:\"" + (address.getMobilNo() == null ? "" : address.getMobilNo()) + "\"");
        sb.append(",bpNo:\"" + (address.getBpNo() == null ? "" : address.getBpNo()) + "\"");
        sb.append(",email:\"" + (address.getEmail() == null ? "" : address.getEmail()) + "\"");
        sb.append(",oicqNo:\"" + (address.getOicqNo() == null ? "" : address.getOicqNo()) + "\"");
        sb.append(",icqNo:\"" + (address.getIcqNo() == null ? "" : address.getIcqNo()) + "\"");
        sb.append(",notes:\"" + (address.getNotes() == null ? "" : address.getNotes()) + "\"");
        sb.append(",psnNo:\"" + (address.getPsnNo() == 0 ? "" : address.getPsnNo()) + "\"");
        sb.append(",smsFlag:\"" + (address.getSmsFlag() == null ? "" : address.getSmsFlag()) + "\"");
        sb.append("},");
      }
      sb.deleteCharAt(sb.length() - 1);
      if (addressList.size() == 0) {
        sb = new StringBuffer("[");
      }
      sb.append("]");
      //System.out.println(sb+">>>>>>>>>>");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  
  /**
   * 新建分组
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addGroup(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int seqId = person.getSeqId();
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int orderNo = 0;
      String orderNos = request.getParameter("orderNo");
      String groupName = request.getParameter("groupName");
      if(orderNos.trim().equals("")){
        orderNo = 0;
      }else{
        orderNo = Integer.parseInt(request.getParameter("orderNo"));
      }
      Map m =new HashMap();
      m.put("orderNo", orderNo);
      m.put("groupName", groupName);
      m.put("userId", seqId);
      T9ORM t = new T9ORM();
      T9AddressLogic cwLogic = new T9AddressLogic();
      if(cwLogic.existsGroupName(dbConn, groupName)){
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        return "/core/inc/rtjson.jsp";
      }
//      else{
//        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
//        request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功");
//      }
      t.saveSingle(dbConn, "addressGroup", m);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"添加成功");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获取管理分组的列表数据
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getManageGroup(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int seqId = person.getSeqId();
      List<Map> list = new ArrayList();
      T9ORM orm = new T9ORM();
      HashMap map = null;
      StringBuffer sb = new StringBuffer("[");
      String[] filters = new String[]{"USER_ID='" + seqId + "' order by ORDER_NO asc,GROUP_NAME asc"};
      List funcList = new ArrayList();
      funcList.add("addressGroup");
      map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters);
      list.addAll((List<Map>) map.get("ADDRESS_GROUP"));
      
      for(Map ms : list){
        String groupName = (String) ms.get("groupName");
        if(!T9Utility.isNullorEmpty(groupName)){
          groupName = groupName.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
        }
        sb.append("{");
        sb.append("seqId:\"" + ms.get("seqId") + "\"");
        sb.append(",groupName:\"" + (ms.get("groupName") == null ? "" : groupName) + "\"");
        sb.append("},");
      }
      sb.deleteCharAt(sb.length() - 1); 
      if (list.size() == 0) {
        sb = new StringBuffer("[");
      }
      sb.append("]");
      //System.out.println(sb);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 根据SEQ_ID获取编辑数据
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getEditGroup(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int seqIdUser = person.getSeqId();
      List<Map> list = new ArrayList();
      T9ORM orm = new T9ORM();
      HashMap map = null;
      String seqId = request.getParameter("seqId");
      StringBuffer sb = new StringBuffer("[");
      String[] filters = new String[]{"SEQ_ID="+seqId};
      List funcList = new ArrayList();
      funcList.add("addressGroup");
      map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters);
      list.addAll((List<Map>) map.get("ADDRESS_GROUP"));
      for(Map ms : list){
        String groupName = (String) ms.get("groupName");
        if(!T9Utility.isNullorEmpty(groupName)){
          groupName = groupName.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
        }
        sb.append("{");
        sb.append("groupName:\"" + (ms.get("groupName") == null ? "" : groupName) + "\"");
        sb.append(",orderNo:\"" + ms.get("orderNo") + "\"");
        sb.append("},");
      }
      sb.deleteCharAt(sb.length() - 1); 
      sb.append("]");
      //System.out.println(sb);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 管理分组列表编辑
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String updateGroup(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int seqIdUser = person.getSeqId();
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int orderNo = 0;
      String seqId = request.getParameter("seqId");
      String orderNos = request.getParameter("orderNo");
      String groupName = request.getParameter("groupName");
      String groupNameOld = request.getParameter("groupNameOld");
      //System.out.println(groupName+"====="+groupNameOld);
      if(orderNos.trim().equals("")){
        orderNo = 0;
      }else{
        orderNo = Integer.parseInt(request.getParameter("orderNo"));
      }
      Map m =new HashMap();
      m.put("seqId", seqId);
      m.put("orderNo", orderNo);
      m.put("groupName", groupName);
      T9ORM t = new T9ORM();
      T9AddressLogic cwLogic = new T9AddressLogic();
      if(groupNameOld.trim().equals(groupName.trim())){
        t.updateSingle(dbConn, "addressGroup", m);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功");
      }else{
        if(cwLogic.existsGroupName(dbConn, groupName)){
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
          return "/core/inc/rtjson.jsp";
        }else{
          t.updateSingle(dbConn, "addressGroup", m);
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
          request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功");
        }
      }
      //t.saveSingle(dbConn, "addressGroup", m);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"添加成功");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 查询结果 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getSearchGroup(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String userName = "";
    ArrayList<T9Address> addressList = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int seqId = person.getSeqId();
      userName = person.getUserName();
      String psnName = request.getParameter("psnName");
      psnName = T9DBUtility.escapeLike(psnName);
      String groupId = request.getParameter("groupId");
      String sex = request.getParameter("sex");
      String nickName = request.getParameter("nickName");
      nickName = T9DBUtility.escapeLike(nickName);
      String deptName = request.getParameter("deptName");
      deptName = T9DBUtility.escapeLike(deptName);
      String telNoDept = request.getParameter("telNoDept");
      telNoDept = T9DBUtility.escapeLike(telNoDept);
      String beginDate = request.getParameter("beginDate");
      String endDate = request.getParameter("endDate");
      String addDept = request.getParameter("addDept");
      addDept = T9DBUtility.escapeLike(addDept);
      String telNoHome = request.getParameter("telNoHome");
      telNoHome = T9DBUtility.escapeLike(telNoHome);
      String addHome = request.getParameter("addHome");
      addHome = T9DBUtility.escapeLike(addHome);
      String mobileNo = request.getParameter("mobileNo");
      mobileNo = T9DBUtility.escapeLike(mobileNo);
      String notes = request.getParameter("notes");
      notes = T9DBUtility.escapeLike(notes);
      T9AddressLogic addl = new T9AddressLogic();
      String data = "";
      data = addl.getAddressSearchJson(dbConn, request.getParameterMap(), seqId, psnName, sex, nickName, deptName, telNoDept, addDept, telNoHome, addHome, notes, groupId, beginDate, endDate, mobileNo);
     
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  
  /**
   * 查询结果列表详情
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getDetail(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String userName = "";
    ArrayList<T9Address> addressList = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int seqIdStr = person.getSeqId();
      userName = person.getUserName();
      
      List<Map> list = new ArrayList();
      T9ORM orm = new T9ORM();
      HashMap map = null;
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      
      String data = null;
      Object obj = orm.loadObjSingle(dbConn, T9Address.class, seqId);
      data = T9FOM.toJson(obj).toString();
      
      
      StringBuffer sb = new StringBuffer("[");
      String[] filters = new String[]{"SEQ_ID="+seqId};
      List funcList = new ArrayList();
      funcList.add("address");
      map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters);
      list.addAll((List<Map>) map.get("ADDRESS"));
      for(Map ms : list){
        sb.append("{");
        sb.append("sex:\"" + (ms.get("sex") == null ? "" : ms.get("sex")) + "\"");
        sb.append("},");
      }
      sb.deleteCharAt(sb.length() - 1); 
      sb.append("]");
      //System.out.println(data+">>>>>>>>>>");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      //request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 编辑联系人：提取数据
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getEditContactPerson(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String userName = "";
    ArrayList<T9Address> addressList = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int seqIdStr = person.getSeqId();
      userName = person.getUserName();
      
      List<Map> list = new ArrayList();
      T9ORM orm = new T9ORM();
      HashMap map = null;
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      
      String data = null;
      Object obj = orm.loadObjSingle(dbConn, T9Address.class, seqId);
      data = T9FOM.toJson(obj).toString();
      
      //System.out.println(data+">>>>>>>>>>");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 编辑联系人：修改数据
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String updateEditContactPerson(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String deptNo = request.getParameter("deptNo");
      String deptNoOld = request.getParameter("deptNoOld");
      String psnName = request.getParameter("psnName");
      String groupId = request.getParameter("groupId");
      String userId = request.getParameter("userId");
      String sex = request.getParameter("sex");
      String nickName = request.getParameter("nickName");
      String birthday = request.getParameter("birthday");
      String ministration = request.getParameter("ministration");
      String mate = request.getParameter("mate");
      String child = request.getParameter("child");
      String deptName = request.getParameter("deptName");
      
      String addDept = request.getParameter("addDept");
      String postNoDept = request.getParameter("postNoDept");
      String telNoDept = request.getParameter("telNoDept");
      String faxNoDept = request.getParameter("faxNoDept");
      String addHome = request.getParameter("addHome");
      String postNoHome = request.getParameter("postNoHome");
      String telNoHome = request.getParameter("telNoHome");
      String mobilNo = request.getParameter("mobilNo");
      String bpNo = request.getParameter("bpNo");
      String email = request.getParameter("email");
      String oicqNo = request.getParameter("oicqNo");
      String icqNo = request.getParameter("icqNo");
      String notes = request.getParameter("notes");
      String psnNo = request.getParameter("psnNo");
      Map m =new HashMap();
      T9Address dl = new T9Address();
      //T9Address dpt = (T9Address) T9FOM.build(request.getParameterMap());
      T9ORM orm = new T9ORM();
      m.put("seqId", seqId);
      m.put("groupId", groupId);
      m.put("psnName", psnName);
      m.put("sex", sex);
      m.put("nickName", nickName);
      m.put("birthday", birthday);
      m.put("ministration", ministration);
      m.put("mate", mate);
      m.put("child", child);
      m.put("deptName", deptName);
      m.put("addDept", addDept);
      m.put("postNoDept", postNoDept);
      m.put("telNoDept", telNoDept);
      m.put("faxNoDept", faxNoDept);
      m.put("addHome", addHome);
      m.put("postNoHome", postNoHome);
      m.put("telNoHome", telNoHome);
      m.put("mobilNo", mobilNo);
      m.put("bpNo", bpNo);
      m.put("email", email);
      m.put("oicqNo", oicqNo);
      m.put("icqNo", icqNo);
      m.put("notes", notes);
      m.put("psnNo", psnNo);
      orm.updateSingle(dbConn, "address", m);
//      orm.updateSingle(dbConn, dpt);
//      if((deptNoOld).equals(deptNo)){
//        orm.updateSingle(dbConn, dpt);
//        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
//        request.setAttribute(T9ActionKeys.RET_MSRG, "成功更改数据库的数据");
//      }else{
//        if(dl.existsTableNo(dbConn, deptNo)){
//          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
//          request.setAttribute(T9ActionKeys.RET_MSRG, "部门排序号以存在，请重新填写！");
//          return "/core/inc/rtjson.jsp";
//        }else{
//          orm.updateSingle(dbConn, dpt);
//          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
//          request.setAttribute(T9ActionKeys.RET_MSRG, "成功更改数据库的数据");
//        }
//      }
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 查询联系人分组数据
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getContactPersonGroup(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int loginSeqId = person.getSeqId();
      int loginDeptId = person.getDeptId();
      String loginUserPriv = person.getUserPriv();
      String loginUserId = person.getUserId(); 
      T9ORM orm = new T9ORM();
      HashMap map = null;
      List<Map> list = new ArrayList();
      StringBuffer sb = new StringBuffer();
      ArrayList<T9AddressGroup> addressGroup = null;
      boolean flag = false;
      T9AddressLogic logic = new T9AddressLogic();
      List<T9AddressGroup> addressList = logic.getAddressInfo(dbConn, loginSeqId);
      List<T9AddressGroup> addressListPresence = logic.getAddressPresenceInfo(dbConn, loginSeqId);
     // List<T9AddressGroup> addressListIsNull = new ArrayList<T9AddressGroup>();
      
      for (int i = 0; i < addressList.size(); i++) {
        boolean users = conditionUser(T9Utility.null2Empty(addressList.get(i).getPrivUser()).split(","), loginSeqId);
        boolean depts = conditionDept(T9Utility.null2Empty(addressList.get(i).getPrivDept()).split(","), loginDeptId);
        boolean roles = conditionRole(T9Utility.null2Empty(addressList.get(i).getPrivRole()).split(","), loginUserPriv);
        flag = (users || depts || roles);
        if (flag) {
          addressListPresence.add(addressList.get(i));
        }
      }
      
      
      if (addressListPresence.size() > 0) {
        sb.append("[");
        for (T9AddressGroup address : addressListPresence) {
          String groupName = (String) address.getGroupName();
          if(!T9Utility.isNullorEmpty(groupName)){
            groupName = groupName.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
          }
          
          sb.append("{");
          sb.append("seqId:\"" + address.getSeqId() + "\"");
          sb.append(",userId:\""+ (address.getUserId() == null ? "" : address.getUserId())+ "\"");
          sb.append(",groupName:\""+ (address.getGroupName() == null ? "" : groupName)+ "\"");
          sb.append("},");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
      } else {
        sb.append("[]");
      }
      //System.out.println(sb+"sdsdsd");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  } 
  
  public boolean conditionUser(String[] userStr, int loginSeqId){
    boolean flagUser = false;
      for(int user = 0; user < userStr.length; user++){
        if(userStr[user].trim().equals(String.valueOf(loginSeqId))){
          return true;
        }else{
          return false;
        }
      }
    return flagUser;
  }
  
  public boolean conditionDept(String[] deptStr, int loginDeptId){
    boolean flagDept = false;
      for(int dept = 0; dept < deptStr.length; dept++){
        if(deptStr[dept].trim().equals(String.valueOf(loginDeptId))){
          return true;
        }else{
          return false;
        }
      }
    return flagDept;
  }
  
  public boolean conditionRole(String[] roleStr, String loginUserPriv){
    boolean flagRole = false;
      for(int role = 0; role < roleStr.length; role++){
        if(roleStr[role].trim().equals(String.valueOf(loginUserPriv))){
          return true;
        }else{
          return false;
        }
      }
    return flagRole;
  }
  
  /**
   * 新建联系人
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String insertContactPerson(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int seqId = person.getSeqId();
      String seqIdStr = String.valueOf(seqId);
      dbConn = requestDbConn.getSysDbConn();
      T9Address dpt = (T9Address) T9FOM.build(request.getParameterMap());
      T9ORM orm = new T9ORM();
      dpt.setUserId(seqIdStr);
      orm.saveSingle(dbConn, dpt);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getManageContact(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String userName = "";
    ArrayList<T9Address> addressList = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      String userId = String.valueOf(person.getSeqId());
      userName = person.getUserName();
      StringBuffer sb = new StringBuffer("[");
      String groupId = request.getParameter("seqId");
      String userIdStr = request.getParameter("userId");
      if(userIdStr.equals("0")){
        userIdStr = userId;
      }
      T9AddressLogic addl = new T9AddressLogic();
      String data = addl.getManageContactJson(dbConn,request.getParameterMap(), groupId, userIdStr);
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  
  public String getManageContact1(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String userName = "";
    ArrayList<T9Address> addressList = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = person.getSeqId();
      userName = person.getUserName();
      StringBuffer sb = new StringBuffer("[");
      String groupId = request.getParameter("seqId");
      String userIdStr = request.getParameter("userId");
      T9AddressLogic addl = new T9AddressLogic();
      addressList = addl.getManageContactJson1(dbConn, groupId, userIdStr);
      for(int i = 0; i < addressList.size(); i++){
        T9Address address = addressList.get(i);
        //System.out.println(address.getBirthday()+"YOOIOP");
        sb.append("{");
        sb.append("seqId:\"" + address.getSeqId() + "\"");
        sb.append(",userId:\"" + (address.getUserId() == null ? "" : address.getUserId()) + "\"");
        sb.append(",groupId:\"" + address.getGroupId() + "\"");
        sb.append(",psnName:\"" + (address.getPsnName() == null ? "" : address.getPsnName()) + "\"");
        sb.append(",sex:\"" + (address.getSex() == null ? "" : address.getSex()) + "\"");
        sb.append(",nickName:\"" + (address.getNickName() == null ? "" : address.getNickName()) + "\"");
        sb.append(",birthday:\"" + (address.getBirthday() == null ? "" : address.getBirthday()) + "\"");
        sb.append(",ministration:\"" + (address.getMinistration() == null ? "" : address.getMinistration()) + "\"");
        sb.append(",mate:\"" + (address.getMate() == null ? "" : address.getMate()) + "\"");
        sb.append(",child:\"" + (address.getChild() == null ? "" : address.getChild()) + "\"");
        sb.append(",deptName:\"" + (address.getDeptName() == null ? "" : address.getDeptName()) + "\"");
        sb.append(",addDept:\"" + (address.getAddDept() == null ? "" : address.getAddDept()) + "\"");
        sb.append(",postNoDept:\"" + (address.getPostNoDept() == null ? "" : address.getPostNoDept()) + "\"");
        sb.append(",telNoDept:\"" + (address.getTelNoDept() == null ? "" : address.getTelNoDept()) + "\"");
        sb.append(",faxNoDept:\"" + (address.getFaxNoDept() == null ? "" : address.getFaxNoDept()) + "\"");
        sb.append(",addHome:\"" + (address.getAddHome() == null ? "" : address.getAddHome()) + "\"");
        sb.append(",postNoHome:\"" + (address.getPostNoHome() == null ? "" : address.getPostNoHome()) + "\"");
        sb.append(",telNoHome:\"" + (address.getTelNoHome() == null ? "" : address.getTelNoHome()) + "\"");
        sb.append(",mobilNo:\"" + (address.getMobilNo() == null ? "" : address.getMobilNo()) + "\"");
        sb.append(",bpNo:\"" + (address.getBpNo() == null ? "" : address.getBpNo()) + "\"");
        sb.append(",email:\"" + (address.getEmail() == null ? "" : address.getEmail()) + "\"");
        sb.append(",oicqNo:\"" + (address.getOicqNo() == null ? "" : address.getOicqNo()) + "\"");
        sb.append(",icqNo:\"" + (address.getIcqNo() == null ? "" : address.getIcqNo()) + "\"");
        sb.append(",notes:\"" + (address.getNotes() == null ? "" : address.getNotes().replace("\"", "\\\"").replace("\r", "").replace("\n", "")) + "\"");
        sb.append(",psnNo:\"" + (address.getPsnNo() == 0 ? "" : address.getPsnNo()) + "\"");
        sb.append(",smsFlag:\"" + (address.getSmsFlag() == null ? "" : address.getSmsFlag()) + "\"");
        sb.append("},");
      }
      sb.deleteCharAt(sb.length() - 1);
      if (addressList.size() == 0) {
        sb = new StringBuffer("[");
      }
      sb.append("]");
      //System.out.println(sb+">>>>>>>>>>");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获取AddressGroup中的groupName字段
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getGroupName(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int seqIdStr = person.getSeqId();
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      List<Map> list = new ArrayList();
      T9ORM orm = new T9ORM();
      HashMap map = null;
      StringBuffer sb = new StringBuffer("[");
      String[] filters = new String[]{"SEQ_ID=" + seqId};
      List funcList = new ArrayList();
      funcList.add("addressGroup");
      map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters);
      list.addAll((List<Map>) map.get("ADDRESS_GROUP"));
      for(Map ms : list){
        
        String groupName = (String) ms.get("groupName");
        if(!T9Utility.isNullorEmpty(groupName)){
          groupName = groupName.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
        }
        sb.append("{");
        sb.append("seqId:\"" + ms.get("seqId") + "\"");
        sb.append(",groupName:\"" + (ms.get("groupName") == null ? "" : groupName) + "\"");
        sb.append("},");
      }
      sb.deleteCharAt(sb.length() - 1); 
      sb.append("]");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 管理联系人：删除
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String deleteContact(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int seqId = person.getSeqId();
    
      String sumStrs = request.getParameter("sumStrs");
      //System.out.println(sumStrs);
      T9AddressLogic ccl = new T9AddressLogic();
      ccl.deleteAll(dbConn, sumStrs);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 管理分组：删除（1、先把联系人update到默认组中，2、然后删除选择的分组名称） 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String deleteManageGroup(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      //int seqId = person.getSeqId();
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      T9AddressLogic addl = new T9AddressLogic();
      addl.updateManageGroup(dbConn, seqId);          
      T9ORM t = new T9ORM();
      t.deleteSingle(dbConn,T9AddressGroup.class, seqId);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 管理分组：清空联系人
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String clearPrivateManageGroup(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int loginUserId = person.getSeqId();
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      T9AddressLogic addl = new T9AddressLogic();
      addl.deletePrivateClearContact(dbConn, seqId, loginUserId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  //*****以下是公共事务中的公共通讯簿方法 public（公共的）*****
  
  /** 
   * 判段id是不是在str里面 
   * @param str 
   * @param id 
   * @return 
   */ 
   public static boolean findId(String str, String id) {
     if (str == null || id == null || "".equals(str) || "".equals(id)) {
       return false;
     }
     String[] aStr = str.split(",");
     for (String tmp : aStr) {
       if (tmp.equals(id)) {
         return true;
       }
     }
     return false;
   }
   
   /**
    * 公共通讯簿 转到（读取列表）
    * @param request
    * @param response
    * @return
    * @throws Exception
    */
  
  public String getPublicContactPersonGroup(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int loginSeqId = person.getSeqId();
      int loginDeptId = person.getDeptId();
      String loginSeqIdStr = String.valueOf(person.getDeptId());
      String loginDeptIdStr = String.valueOf(person.getDeptId());
      int loginUserPriv = Integer.parseInt(person.getUserPriv());
      String loginUserId = person.getUserId(); 
      T9ORM orm = new T9ORM();
      HashMap map = null;
      List<Map> list = new ArrayList();
      StringBuffer sb = new StringBuffer("[");
      
      String[] filters = new String[]{"(USER_ID is null and ("
          + findInSet(String.valueOf(loginSeqId),"PRIV_USER")
          + " or "+ findInSet(String.valueOf(loginDeptId),"PRIV_DEPT")
          + " or "+ findInSet(String.valueOf(loginUserPriv),"PRIV_ROLE")
           + " or "+ findInSet("0","PRIV_DEPT")
            + " or "+ findInSet("ALL_DEPT","PRIV_DEPT")
          + ")) order by USER_ID asc, ORDER_NO asc, GROUP_NAME asc"};
      
      //and (" + T9DBUtility.findInSet(loginDeptIdStr, "SUPPORT_DEPT") +" or (SUPPORT_DEPT like 0 or SUPPORT_DEPT like 'ALL_DEPT') or " + T9DBUtility.findInSet(loginSeqIdStr, "SUPPORT_USER") +" )
      String[] filters2 = new String[]{"USER_ID is null order by ORDER_NO asc, GROUP_NAME asc"};
      List funcList = new ArrayList();
      funcList.add("addressGroup");
      map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters2);
      list.addAll((List<Map>) map.get("ADDRESS_GROUP"));
      int flag = 0;
      for(Map ms : list){
        if(!"0".equals(String.valueOf(ms.get("privDept")))){
          if(!findId(String.valueOf(ms.get("privDept")), String.valueOf(loginDeptId)) && !findId(String.valueOf(ms.get("privRole")), String.valueOf(loginUserPriv)) && !findId(String.valueOf(ms.get("privUser")), String.valueOf(loginSeqId))){
            flag++;
            continue;
          }
        }
        
        String groupName = (String) ms.get("groupName");
        if(!T9Utility.isNullorEmpty(groupName)){
          groupName = groupName.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
        }
        sb.append("{");
        sb.append("seqId:\"" + ms.get("seqId") + "\"");
        sb.append(",userId:\"" + (ms.get("userId") == null ? "" : ms.get("userId")) + "\"");
        sb.append(",groupName:\"" + (ms.get("groupName") == null ? "" : groupName) + "\"");
        sb.append("},");
      }
      sb.deleteCharAt(sb.length() - 1); 
      
      if(list.size() == 0 || flag == list.size()){
        sb.append("[");
      }
      sb.append("]");
      //System.out.println(sb+"NNNNMssss");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  } 
  
  /**
   * 公共通讯簿 转到（读取列表）
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
 
  public String getPublicChangePersonGroup(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int loginSeqId = person.getSeqId();
      int loginDeptId = person.getDeptId();
      String loginSeqIdStr = String.valueOf(person.getSeqId());
      String loginDeptIdStr = String.valueOf(person.getDeptId());
      int loginUserPriv = Integer.parseInt(person.getUserPriv());
      String loginUserId = person.getUserId(); 
      T9ORM orm = new T9ORM();
      HashMap map = null;
      List<Map> list = new ArrayList();
      StringBuffer sb = new StringBuffer("[");
      
      String[] filters = new String[]{"(USER_ID is null and ("
          + findInSet(String.valueOf(loginSeqId),"PRIV_USER")
          + " or "+ findInSet(String.valueOf(loginDeptId),"PRIV_DEPT")
          + " or "+ findInSet(String.valueOf(loginUserPriv),"PRIV_ROLE")
          + ")) order by USER_ID asc, ORDER_NO asc, GROUP_NAME asc"};
      
      //and (" + T9DBUtility.findInSet(loginDeptIdStr, "SUPPORT_DEPT") +" or (SUPPORT_DEPT like 0 or SUPPORT_DEPT like 'ALL_DEPT') or " + T9DBUtility.findInSet(loginSeqIdStr, "SUPPORT_USER") +" )
      String[] filters2 = null;
      if(person.isAdminRole()){
        filters2 = new String[]{"USER_ID is null order by ORDER_NO asc, GROUP_NAME asc"};
      }else{
        filters2 = new String[]{"USER_ID is null and (" + T9DBUtility.findInSet(loginDeptIdStr, "SUPPORT_DEPT") +" or (SUPPORT_DEPT like 0 or SUPPORT_DEPT like 'ALL_DEPT') or " + T9DBUtility.findInSet(loginSeqIdStr, "SUPPORT_USER") +" ) order by ORDER_NO asc, GROUP_NAME asc"};
      }
      
      List funcList = new ArrayList();
      funcList.add("addressGroup");
      map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters2);
      list.addAll((List<Map>) map.get("ADDRESS_GROUP"));
      int flag = 0;
      for(Map ms : list){
        if(!"0".equals(String.valueOf(ms.get("privDept")))){
          if(!findId(String.valueOf(ms.get("privDept")), String.valueOf(loginDeptId)) && !findId(String.valueOf(ms.get("privRole")), String.valueOf(loginUserPriv)) && !findId(String.valueOf(ms.get("privUser")), String.valueOf(loginSeqId))){
            flag++;
            continue;
          }
        }
        String groupName = (String) ms.get("groupName");
        if(!T9Utility.isNullorEmpty(groupName)){
          groupName = groupName.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
        }
        sb.append("{");
        sb.append("seqId:\"" + ms.get("seqId") + "\"");
        sb.append(",userId:\"" + (ms.get("userId") == null ? "" : ms.get("userId")) + "\"");
        sb.append(",groupName:\"" + (ms.get("groupName") == null ? "" : groupName) + "\"");
        sb.append("},");
      }
      sb.deleteCharAt(sb.length() - 1); 
      
      if(list.size() == 0 || flag == list.size()){
        sb.append("[");
      }
      sb.append("]");
      //System.out.println(sb+"NNNNMssss");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  } 
  
  /**
   * 个人通讯簿 转到（读取列表）
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getPrivateContactPersonGroup(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int loginSeqId = person.getSeqId();
      int loginDeptId = person.getDeptId();
      int loginUserPriv = Integer.parseInt(person.getUserPriv());
      String loginUserId = person.getUserId(); 
      T9ORM orm = new T9ORM();
      HashMap map = null;
      List<Map> list = new ArrayList();
      StringBuffer sb = new StringBuffer("[");
      
      String[] filters2 = new String[]{"USER_ID = '" + loginSeqId + "' order by ORDER_NO asc, GROUP_NAME asc"};
      List funcList = new ArrayList();
      funcList.add("addressGroup");
      map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters2);
      list.addAll((List<Map>) map.get("ADDRESS_GROUP"));
      int flag = 0;
      for(Map ms : list){
        
        String groupName = (String) ms.get("groupName");
        if(!T9Utility.isNullorEmpty(groupName)){
          groupName = groupName.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
        }
        sb.append("{");
        sb.append("seqId:\"" + ms.get("seqId") + "\"");
        sb.append(",userId:\"" + (ms.get("userId") == null ? "" : ms.get("userId")) + "\"");
        sb.append(",groupName:\"" + (ms.get("groupName") == null ? "" : groupName) + "\"");
        sb.append("},");
      }
      sb.deleteCharAt(sb.length() - 1); 
      
      if(list.size() == 0 || flag == list.size()){
        sb.append("[");
      }
      sb.append("]");
      //System.out.println(sb+"NNNNMssss");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  } 
  /**
   * 改变公共分组
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String changePublicGroup(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int seqId = person.getSeqId();
      int groupId = Integer.parseInt(request.getParameter("groupId"));
      String seqStr = request.getParameter("seqIds");
      String groupIdOld= request.getParameter("groupIdOld");
      T9AddressLogic al = new T9AddressLogic();
      al.changePublicGroupLogic(dbConn, groupId, seqStr, groupIdOld);
      
      
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  
  /**
   * 改变个人通讯簿分组
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String changePrivateGroup(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int seqId = person.getSeqId();
      int groupId = Integer.parseInt(request.getParameter("groupId"));
      String seqStr = request.getParameter("seqIds");
      String groupIdOld= request.getParameter("groupIdOld");
      T9AddressLogic al = new T9AddressLogic();
      al.changePrivateGroupLogic(dbConn, groupId, seqStr, groupIdOld);
      
      
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 把人员复制到其它公共分组
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String changePublicCopyGroup(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ORM orm = new T9ORM();
      Map m =new HashMap();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int seqId = person.getSeqId();
      int groupId = Integer.parseInt(request.getParameter("groupId"));
      String seqStr = request.getParameter("seqIds");
      String groupIdOld= request.getParameter("groupIdOld");
      T9AddressLogic al = new T9AddressLogic();
      String[] filters = new String[]{"SEQ_ID IN (" + seqStr +")"};
      //al.changePublicCopyGroupLogic(dbConn, groupId, seqStr, groupIdOld);
      List<T9Address> addressList = orm.loadListSingle(dbConn, T9Address.class, filters);
      for(int i = 0; i < addressList.size(); i++){
        String bir = String.valueOf(addressList.get(i).getBirthday());
        m.put("userId", addressList.get(i).getUserId());
        m.put("groupId", groupId);
        m.put("psnName", addressList.get(i).getPsnName());
        m.put("sex", addressList.get(i).getSex());
        m.put("nickName", addressList.get(i).getNickName());
        if("null".equals(bir)){
          m.put("birthday", null);
        }else{
          m.put("birthday", bir.substring(0, 19));
        }
        m.put("ministration", addressList.get(i).getMinistration());
        m.put("mate", addressList.get(i).getMate());
        m.put("child", addressList.get(i).getChild());
        m.put("deptName", addressList.get(i).getDeptName());
        m.put("addDept", addressList.get(i).getAddDept());
        m.put("postNoDept", addressList.get(i).getPostNoDept());
        m.put("telNoDept", addressList.get(i).getTelNoDept());
        m.put("faxNoDept", addressList.get(i).getFaxNoDept());
        m.put("addHome", addressList.get(i).getAddHome());
        m.put("postNoHome", addressList.get(i).getPostNoHome());
        m.put("telNoHome", addressList.get(i).getTelNoHome());
        m.put("mobilNo", addressList.get(i).getMobilNo());
        m.put("bpNo", addressList.get(i).getBpNo());
        m.put("email", addressList.get(i).getEmail());
        m.put("oicqNo", addressList.get(i).getOicqNo());
        m.put("icqNo", addressList.get(i).getIcqNo());
        m.put("notes", addressList.get(i).getNotes());
        m.put("psnNo", addressList.get(i).getPsnNo());
        m.put("smsFlag", addressList.get(i).getSmsFlag());
        orm.saveSingle(dbConn, "address", m);
      }
      
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  
  /**
   * 获取管理分组的列表数据
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getPublicManageGroup(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int seqId = person.getSeqId();
      int loginSeqId = person.getSeqId();
      int loginDeptId = person.getDeptId();
      int loginUserPriv = Integer.parseInt(person.getUserPriv());
      int privFlag = 0;
      List<Map> list = new ArrayList();
      T9ORM orm = new T9ORM();
      HashMap map = null;
      StringBuffer sb = new StringBuffer("[");
      String[] filters = new String[]{"USER_ID is null order by ORDER_NO asc,GROUP_NAME asc"};
      List funcList = new ArrayList();
      funcList.add("addressGroup");
      map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters);
      list.addAll((List<Map>) map.get("ADDRESS_GROUP"));
      int flag = 0;
      for(Map ms : list){
        if(!"0".equals(String.valueOf(ms.get("privDept")))){
          if(!findId(String.valueOf(ms.get("privDept")), String.valueOf(loginDeptId)) && !findId(String.valueOf(ms.get("privRole")), String.valueOf(loginUserPriv)) && !findId(String.valueOf(ms.get("privUser")), String.valueOf(loginSeqId))){
            flag++;
            continue;
          }
        }
        if(!person.isAdmin()){
          String[] filterPriv = {"SEQ_ID = " 
              + ms.get("seqId") 
              +" and (" + T9DBUtility.findInSet(String.valueOf(loginDeptId),"SUPPORT_DEPT")
              +" or SUPPORT_DEPT like '0' or " 
              + T9DBUtility.findInSet(String.valueOf(loginSeqId),"SUPPORT_USER")+")"};
          ArrayList<T9AddressGroup> addGroup = (ArrayList<T9AddressGroup>) orm.loadListSingle(dbConn, T9AddressGroup.class, filterPriv);
          if(addGroup.size() > 0){
            privFlag = 1;
          }else{
            privFlag = 0;
          }
        }else{
          privFlag = 1;
        }
        String groupName = (String) ms.get("groupName");
        groupName = T9Utility.encodeSpecial(groupName);
        sb.append("{");
        sb.append("seqId:\"" + ms.get("seqId") + "\"");
        sb.append(",groupName:\"" + (ms.get("groupName") == null ? "" : groupName) + "\"");
        sb.append(",privFlag:\"" + privFlag + "\"");
        sb.append("},");
      }
      if(list.size() == 0 || flag == list.size()){
        sb.append("[");
      }
      sb.deleteCharAt(sb.length() - 1); 
      sb.append("]");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 新建分组
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addPublicGroup(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int seqId = person.getSeqId();
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int orderNo = 0;
      String orderNos = request.getParameter("orderNo");
      String groupName = request.getParameter("groupName");
      if(orderNos.trim().equals("")){
        orderNo = 0;
      }else{
        orderNo = Integer.parseInt(request.getParameter("orderNo"));
      }
      Map m =new HashMap();
      m.put("orderNo", orderNo);
      m.put("groupName", groupName);
      T9ORM t = new T9ORM();
      T9AddressLogic cwLogic = new T9AddressLogic();
      if(cwLogic.existsGroupName(dbConn, groupName)){
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        return "/core/inc/rtjson.jsp";
      }else{
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功");
      }
      t.saveSingle(dbConn, "addressGroup", m);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"添加成功");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 根据SEQ_ID获取编辑数据
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getPublicEditGroup(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int seqIdUser = person.getSeqId();
      List<Map> list = new ArrayList();
      T9ORM orm = new T9ORM();
      HashMap map = null;
      String seqId = request.getParameter("seqId");
      StringBuffer sb = new StringBuffer("[");
      String[] filters = new String[]{"SEQ_ID="+seqId};
      List funcList = new ArrayList();
      funcList.add("addressGroup");
      map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters);
      list.addAll((List<Map>) map.get("ADDRESS_GROUP"));
      for(Map ms : list){
        sb.append("{");
        sb.append("groupName:\"" + (ms.get("groupName") == null ? "" : ms.get("groupName")) + "\"");
        sb.append(",orderNo:\"" + ms.get("orderNo") + "\"");
        sb.append("},");
      }
      sb.deleteCharAt(sb.length() - 1); 
      sb.append("]");
      //System.out.println(sb);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 管理分组列表编辑
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String updatePublicGroup(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int seqIdUser = person.getSeqId();
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int orderNo = 0;
      String seqId = request.getParameter("seqId");
      String orderNos = request.getParameter("orderNo");
      String groupName = request.getParameter("groupName");
      String groupNameOld = request.getParameter("groupNameOld");
      //System.out.println(groupName+"====="+groupNameOld);
      if(orderNos.trim().equals("")){
        orderNo = 0;
      }else{
        orderNo = Integer.parseInt(request.getParameter("orderNo"));
      }
      Map m =new HashMap();
      m.put("seqId", seqId);
      m.put("orderNo", orderNo);
      m.put("groupName", groupName);
      T9ORM t = new T9ORM();
      T9AddressLogic cwLogic = new T9AddressLogic();
      if(groupNameOld.trim().equals(groupName.trim())){
        t.updateSingle(dbConn, "addressGroup", m);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功");
      }else{
        if(cwLogic.existsGroupName(dbConn, groupName)){
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
          return "/core/inc/rtjson.jsp";
        }else{
          t.updateSingle(dbConn, "addressGroup", m);
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
          request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功");
        }
      }
      //t.saveSingle(dbConn, "addressGroup", m);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"添加成功");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 管理分组：清空联系人
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String clearPublicManageGroup(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      //int loginSeqId = person.getSeqId();
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      T9AddressLogic addl = new T9AddressLogic();
      addl.deletePublicClearContact(dbConn, seqId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 管理联系人列表
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getPublicManageContact(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    ArrayList<T9Address> addressList = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int seqId = person.getSeqId();
      String groupId = request.getParameter("seqId");
      String userId = request.getParameter("userId");
      T9AddressLogic addl = new T9AddressLogic();
      String data = addl.getPublicManageContactJson(dbConn,request.getParameterMap(), groupId, userId);
      
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  
  public String getPublicSupportPriv(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      String groupId = request.getParameter("groupId");
      String loginDeptId = String.valueOf(person.getDeptId());
      String loginSeqId = String.valueOf(person.getSeqId());
      T9AddressLogic logic = new T9AddressLogic();
      String data = "0";
      int count = logic.getSupportPriv(dbConn, groupId, loginDeptId, loginSeqId , person.getUserPriv());
      if(count > 0 || person.isAdminRole()){
        data = "1";
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA,"\"" + data + "\"");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getPublicManageContact1(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String userName = "";
    ArrayList<T9Address> addressList = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int seqId = person.getSeqId();
      userName = person.getUserName();
      StringBuffer sb = new StringBuffer("[");
      String groupId = request.getParameter("seqId");
      String userId = request.getParameter("userId");
      T9AddressLogic addl = new T9AddressLogic();
      addressList = addl.getPublicManageContactJson1(dbConn, groupId, userId);
      for(int i = 0; i < addressList.size(); i++){
        T9Address address = addressList.get(i);
        sb.append("{");
        sb.append("seqId:\"" + address.getSeqId() + "\"");
        sb.append(",userId:\"" + (address.getUserId() == null ? "" : address.getUserId()) + "\"");
        sb.append(",groupId:\"" + address.getGroupId() + "\"");
        sb.append(",psnName:\"" + (address.getPsnName() == null ? "" : address.getPsnName()) + "\"");
        sb.append(",sex:\"" + (address.getSex() == null ? "" : address.getSex()) + "\"");
        sb.append(",nickName:\"" + (address.getNickName() == null ? "" : address.getNickName()) + "\"");
        sb.append(",birthday:\"" + (address.getBirthday() == null ? "" : address.getBirthday()) + "\"");
        sb.append(",ministration:\"" + (address.getMinistration() == null ? "" : address.getMinistration()) + "\"");
        sb.append(",mate:\"" + (address.getMate() == null ? "" : address.getMate()) + "\"");
        sb.append(",child:\"" + (address.getChild() == null ? "" : address.getChild()) + "\"");
        sb.append(",deptName:\"" + (address.getDeptName() == null ? "" : address.getDeptName()) + "\"");
        sb.append(",addDept:\"" + (address.getAddDept() == null ? "" : address.getAddDept()) + "\"");
        sb.append(",postNoDept:\"" + (address.getPostNoDept() == null ? "" : address.getPostNoDept()) + "\"");
        sb.append(",telNoDept:\"" + (address.getTelNoDept() == null ? "" : address.getTelNoDept()) + "\"");
        sb.append(",faxNoDept:\"" + (address.getFaxNoDept() == null ? "" : address.getFaxNoDept()) + "\"");
        sb.append(",addHome:\"" + (address.getAddHome() == null ? "" : address.getAddHome()) + "\"");
        sb.append(",postNoHome:\"" + (address.getPostNoHome() == null ? "" : address.getPostNoHome()) + "\"");
        sb.append(",telNoHome:\"" + (address.getTelNoHome() == null ? "" : address.getTelNoHome()) + "\"");
        sb.append(",mobilNo:\"" + (address.getMobilNo() == null ? "" : address.getMobilNo()) + "\"");
        sb.append(",bpNo:\"" + (address.getBpNo() == null ? "" : address.getBpNo()) + "\"");
        sb.append(",email:\"" + (address.getEmail() == null ? "" : address.getEmail()) + "\"");
        sb.append(",oicqNo:\"" + (address.getOicqNo() == null ? "" : address.getOicqNo()) + "\"");
        sb.append(",icqNo:\"" + (address.getIcqNo() == null ? "" : address.getIcqNo()) + "\"");
        sb.append(",notes:\"" + (address.getNotes() == null ? "" : address.getNotes()) + "\"");
        sb.append(",psnNo:\"" + (address.getPsnNo() == 0 ? "" : address.getPsnNo()) + "\"");
        sb.append(",smsFlag:\"" + (address.getSmsFlag() == null ? "" : address.getSmsFlag()) + "\"");
        sb.append("},");
      }
      sb.deleteCharAt(sb.length() - 1);
      if (addressList.size() == 0) {
        sb = new StringBuffer("[");
      }
      sb.append("]");
      //System.out.println(sb+">>>>>>>>>>");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 新建联系人
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String insertPublicContactPerson(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int seqId = person.getSeqId();
      String seqIdStr = String.valueOf(seqId);
      dbConn = requestDbConn.getSysDbConn();
      T9Address dpt = (T9Address) T9FOM.build(request.getParameterMap());
      T9ORM orm = new T9ORM();
      orm.saveSingle(dbConn, dpt);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 编辑联系人：修改数据(公共事务中的公共通讯簿)
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String updatePublicEditContactPerson(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      String psnName = request.getParameter("psnName");
      String groupId = request.getParameter("groupId");
      String userId = request.getParameter("userId");
      String sex = request.getParameter("sex");
      String nickName = request.getParameter("nickName");
      String birthday = request.getParameter("birthday");
      String ministration = request.getParameter("ministration");
      String mate = request.getParameter("mate");
      String child = request.getParameter("child");
      String deptName = request.getParameter("deptName");
      String addDept = request.getParameter("addDept");
      String postNoDept = request.getParameter("postNoDept");
      String telNoDept = request.getParameter("telNoDept");
      String faxNoDept = request.getParameter("faxNoDept");
      String addHome = request.getParameter("addHome");
      String postNoHome = request.getParameter("postNoHome");
      String telNoHome = request.getParameter("telNoHome");
      String mobilNo = request.getParameter("mobilNo");
      String bpNo = request.getParameter("bpNo");
      String email = request.getParameter("email");
      String oicqNo = request.getParameter("oicqNo");
      String icqNo = request.getParameter("icqNo");
      String notes = request.getParameter("notes");
      String psnNo = request.getParameter("psnNo");
      Map m =new HashMap();
      m.put("seqId", seqId);
      m.put("groupId", groupId);
      m.put("psnName", psnName);
      m.put("sex", sex);
      m.put("nickName", nickName);
      m.put("birthday", birthday);
      m.put("ministration", ministration);
      m.put("mate", mate);
      m.put("child", child);
      m.put("deptName", deptName);
      m.put("addDept", addDept);
      m.put("postNoDept", postNoDept);
      m.put("telNoDept", telNoDept);
      m.put("faxNoDept", faxNoDept);
      m.put("addHome", addHome);
      m.put("postNoHome", postNoHome);
      m.put("telNoHome", telNoHome);
      m.put("mobilNo", mobilNo);
      m.put("bpNo", bpNo);
      m.put("email", email);
      m.put("oicqNo", oicqNo);
      m.put("icqNo", icqNo);
      m.put("notes", notes);
      m.put("psnNo", psnNo);
      T9ORM orm = new T9ORM();
      orm.updateSingle(dbConn, "address", m);
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 管理联系人：删除
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String deletePublicContact(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int seqId = person.getSeqId();
    
      String sumStrs = request.getParameter("sumStrs");
      //System.out.println(sumStrs);
      T9AddressLogic ccl = new T9AddressLogic();
      ccl.deleteAll(dbConn, sumStrs);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 公共事务通讯簿：查询结果 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getPublicSearchGroup(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String userName = "";
    ArrayList<T9Address> addressList = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int seqId = person.getSeqId();
      userName = person.getUserName();
      StringBuffer sb = new StringBuffer("[");
      String psnName = request.getParameter("psnName");
      psnName = T9DBUtility.escapeLike(psnName);
      String groupId = request.getParameter("groupId");
      String sex = request.getParameter("sex");
      String nickName = request.getParameter("nickName");
      nickName = T9DBUtility.escapeLike(nickName);
      String deptName = request.getParameter("deptName");
      deptName = T9DBUtility.escapeLike(deptName);
      String beginDate = request.getParameter("beginDate");
      String endDate = request.getParameter("endDate");
      String telNoDept = request.getParameter("telNoDept");
      telNoDept = T9DBUtility.escapeLike(telNoDept);
      String addDept = request.getParameter("addDept");
      addDept = T9DBUtility.escapeLike(addDept);
      String telNoHome = request.getParameter("telNoHome");
      telNoHome = T9DBUtility.escapeLike(telNoHome);
      String addHome = request.getParameter("addHome");
      addHome = T9DBUtility.escapeLike(addHome);
      String notes = request.getParameter("notes");
      notes = T9DBUtility.escapeLike(notes);
      String mobileNo = request.getParameter("mobileNo");
      mobileNo = T9DBUtility.escapeLike(mobileNo);
      T9AddressLogic addl = new T9AddressLogic();
      String data = addl.getPublicAddressSearchJson(dbConn,request.getParameterMap(), person, psnName, sex, nickName, deptName, telNoDept, addDept, telNoHome, addHome, notes, groupId, beginDate, endDate,mobileNo);
      //System.out.println(data+"=========");
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  
  public String getPublicSearchGroup1(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String userName = "";
    ArrayList<T9Address> addressList = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int seqId = person.getSeqId();
      userName = person.getUserName();
      StringBuffer sb = new StringBuffer("[");
      String psnName = request.getParameter("psnName");
      String groupId = request.getParameter("groupId");
      String sex = request.getParameter("sex");
      String nickName = request.getParameter("nickName");
      String deptName = request.getParameter("deptName");
      String beginDate = request.getParameter("beginDate");
      String endDate = request.getParameter("endDate");
      String telNoDept = request.getParameter("telNoDept");
      String addDept = request.getParameter("addDept");
      String telNoHome = request.getParameter("telNoHome");
      String addHome = request.getParameter("addHome");
      String notes = request.getParameter("notes");
      T9AddressLogic addl = new T9AddressLogic();
      addressList = addl.getPublicAddressSearchJson1(dbConn, person, psnName, sex, nickName, deptName, telNoDept, addDept, telNoHome, addHome, notes, groupId, beginDate, endDate);
      for(int i = 0; i < addressList.size(); i++){
        T9Address address = addressList.get(i);
        sb.append("{");
        sb.append("seqId:\"" + address.getSeqId() + "\"");
        sb.append(",userId:\"" + (address.getUserId() == null ? "" : address.getUserId()) + "\"");
        sb.append(",groupId:\"" + address.getGroupId() + "\"");
        sb.append(",psnName:\"" + (address.getPsnName() == null ? "" : address.getPsnName()) + "\"");
        sb.append(",sex:\"" + (address.getSex() == null ? "" : address.getSex()) + "\"");
        sb.append(",nickName:\"" + (address.getNickName() == null ? "" : address.getNickName()) + "\"");
        sb.append(",birthday:\"" + (address.getBirthday() == null ? "" : address.getBirthday()) + "\"");
        sb.append(",ministration:\"" + (address.getMinistration() == null ? "" : address.getMinistration()) + "\"");
        sb.append(",mate:\"" + (address.getMate() == null ? "" : address.getMate()) + "\"");
        sb.append(",child:\"" + (address.getChild() == null ? "" : address.getChild()) + "\"");
        sb.append(",deptName:\"" + (address.getDeptName() == null ? "" : address.getDeptName()) + "\"");
        sb.append(",addDept:\"" + (address.getAddDept() == null ? "" : address.getAddDept()) + "\"");
        sb.append(",postNoDept:\"" + (address.getPostNoDept() == null ? "" : address.getPostNoDept()) + "\"");
        sb.append(",telNoDept:\"" + (address.getTelNoDept() == null ? "" : address.getTelNoDept()) + "\"");
        sb.append(",faxNoDept:\"" + (address.getFaxNoDept() == null ? "" : address.getFaxNoDept()) + "\"");
        sb.append(",addHome:\"" + (address.getAddHome() == null ? "" : address.getAddHome()) + "\"");
        sb.append(",postNoHome:\"" + (address.getPostNoHome() == null ? "" : address.getPostNoHome()) + "\"");
        sb.append(",telNoHome:\"" + (address.getTelNoHome() == null ? "" : address.getTelNoHome()) + "\"");
        sb.append(",mobilNo:\"" + (address.getMobilNo() == null ? "" : address.getMobilNo()) + "\"");
        sb.append(",bpNo:\"" + (address.getBpNo() == null ? "" : address.getBpNo()) + "\"");
        sb.append(",email:\"" + (address.getEmail() == null ? "" : address.getEmail()) + "\"");
        sb.append(",oicqNo:\"" + (address.getOicqNo() == null ? "" : address.getOicqNo()) + "\"");
        sb.append(",icqNo:\"" + (address.getIcqNo() == null ? "" : address.getIcqNo()) + "\"");
        sb.append(",notes:\"" + (address.getNotes() == null ? "" : address.getNotes()) + "\"");
        sb.append(",psnNo:\"" + (address.getPsnNo() == 0 ? "" : address.getPsnNo()) + "\"");
        sb.append(",smsFlag:\"" + (address.getSmsFlag() == null ? "" : address.getSmsFlag()) + "\"");
        sb.append("},");
      }
      sb.deleteCharAt(sb.length() - 1);
      if (addressList.size() == 0) {
        sb = new StringBuffer("[");
      }
      sb.append("]");
      //System.out.println(sb+">>>>>>>>>>");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 导出到EXCEL表格中

   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String exportCsvPrivateAddress(HttpServletRequest request, HttpServletResponse response) throws Exception{
    response.setCharacterEncoding(T9Const.CSV_FILE_CODE);
//    OutputStream ops = null;
    Connection conn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      String groupName = request.getParameter("groupName");
      String seqId = request.getParameter("seqId");
      String fileName = URLEncoder.encode(groupName + ".csv","UTF-8");
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control","private");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Accept-Ranges","bytes");
      response.setHeader("Content-disposition","attachment; filename=\"" + fileName + "\"");
      T9AddressLogic ieml = new T9AddressLogic();
      ArrayList<T9DbRecord > dbL = ieml.toExportPersonData(conn, Integer.parseInt(seqId)  , person.getSeqId());
      T9CSVUtil.CVSWrite(response.getWriter(), dbL);
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    }
    return null;
  }
  
  /**
   * 导出到EXCEL表格中

   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String exportCsvPublicAddress(HttpServletRequest request,HttpServletResponse response) throws Exception{
    response.setCharacterEncoding(T9Const.CSV_FILE_CODE);
    Connection conn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      int loginSeqId = person.getSeqId();
      String groupName = request.getParameter("groupName");
      String seqId = request.getParameter("seqId");
      String fileName = URLEncoder.encode(groupName + ".csv","UTF-8");
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control","private");
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Accept-Ranges","bytes");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setHeader("Content-disposition","attachment; filename=\"" + fileName + "\"");
      T9AddressLogic ieml = new T9AddressLogic();
      ArrayList<T9DbRecord > dbL = ieml.toExportPublicAdressData(conn, Integer.parseInt(seqId), loginSeqId);
      T9CSVUtil.CVSWrite(response.getWriter(), dbL);
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    }
    return null;
  }
  
  public String importAddressPrivate(HttpServletRequest request,HttpServletResponse response) throws Exception{
    InputStream is = null;
    Connection dbConn = null;
    String data = null;
    int isCount = 0;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      T9FileUploadForm fileForm = new T9FileUploadForm();
      fileForm.parseUploadRequest(request);
      is = fileForm.getInputStream();
      ArrayList<T9DbRecord> drl = T9CSVUtil.CVSReader(is);
      String groupIdStr = fileForm.getParameter("groupId");
      int groupId = 0;
      if(T9Utility.isNullorEmpty(groupIdStr)){
        groupId = 0;
      }else{
        groupId = Integer.parseInt(groupIdStr);
      }
      
      T9AddressLogic dl = new T9AddressLogic();
      String psnName = "";
      String ministration = "";
      String nickName = "";
      String email = "";
      String mobilNo = "";
      String bpNo = "";
      String oicqNo = "";
      String icqNo = "";
      String sex = "";
      String birthday = "";
      String mate = "";
      String child = "";
      String postNoHome = "";
      String addHome = "";
      String telNoHome = "";
      String deptName = "";
      String postNoDept = "";
      String addDept = "";
      String telNoDept = "";
      String faxNoDept = "";
      String notes = "";
      Map map = new HashMap();
      for(int i = 0; i < drl.size(); i++){
        psnName = (String) drl.get(i).getValueByName("姓名");
        if(T9Utility.isNullorEmpty(psnName)){
          continue;
        }
        isCount++;
        ministration = (String) drl.get(i).getValueByName("职位");
        nickName = (String) drl.get(i).getValueByName("昵称");
        email = (String) drl.get(i).getValueByName("电子邮件地址");
        mobilNo = (String) drl.get(i).getValueByName("手机");
        bpNo = (String) drl.get(i).getValueByName("传呼机");
        oicqNo = (String) drl.get(i).getValueByName("QQ");
        icqNo = (String) drl.get(i).getValueByName("MSN");
        sex = (String) drl.get(i).getValueByName("性别");
        birthday = (String) drl.get(i).getValueByName("生日");
        mate = (String) drl.get(i).getValueByName("配偶");
        child = (String) drl.get(i).getValueByName("子女");
        postNoHome = (String) drl.get(i).getValueByName("家庭所在地邮政编码");
        addHome = (String) drl.get(i).getValueByName("家庭所在街道");
        telNoHome = (String) drl.get(i).getValueByName("家庭电话1");
        deptName = (String) drl.get(i).getValueByName("公司");
        postNoDept = (String) drl.get(i).getValueByName("公司所在地邮政编码");
        addDept = (String) drl.get(i).getValueByName("公司所在街道");
        telNoDept = (String) drl.get(i).getValueByName("办公电话1");
        faxNoDept = (String) drl.get(i).getValueByName("公司传真");
        notes = (String) drl.get(i).getValueByName("附注");
          
        if("女".equals(sex)){
          sex = "1";
        }else if("男".equals(sex)){
          sex = "0";
        }else{
          sex = "";
        }
        map.put("psnName" , psnName);
        map.put("ministration" , ministration);
        map.put("nickName" , nickName);
        map.put("email" , email);
        map.put("mobilNo" , mobilNo);
        map.put("bpNo" , bpNo);
        map.put("oicqNo" , oicqNo);
        map.put("icqNo" , icqNo);
        map.put("sex" , sex);
        map.put("birthday" , birthday);
        map.put("mate" , mate);
        map.put("child" , child);
        map.put("postNoHome" , postNoHome);
        map.put("addHome" , addHome);
        map.put("telNoHome" , telNoHome);
        map.put("deptName" , deptName);
        map.put("postNoDept" , postNoDept);
        map.put("addDept" , addDept);
        map.put("telNoDept" , telNoDept);
        map.put("faxNoDept" , faxNoDept);
        map.put("notes" , notes);
          
        T9ORM orm = new T9ORM();
        if(dl.existsGroupId(dbConn, groupId, psnName)){
          int seqId = dl.getGroupSeqIdLogic(dbConn, groupId, psnName);
          map.put("seqId" , seqId);
          orm.updateSingle(dbConn , "address" , map);
        }else{
          map.put("userId" , person.getSeqId());
          map.put("groupId" , groupId);
          orm.saveSingle(dbConn , "address" , map);
        }
      }

      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (SizeLimitExceededException ex) {
        return "/core/funcs/address/private/group/importPrivate.jsp?maxSize=1";
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    } 
    return "/core/funcs/address/private/group/importPrivate.jsp?isCount="+isCount;
  }
  
  public String importAddressPublic(HttpServletRequest request,HttpServletResponse response) throws Exception{
    InputStream is = null;
    OutputStream ops = null;
    Connection dbConn = null;
    String data = null;
    int isCount = 0;
    int updateCount = 0;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      T9FileUploadForm fileForm = new T9FileUploadForm();
      fileForm.parseUploadRequest(request);
      String fileName = fileForm.getFileName();
      is = fileForm.getInputStream();
      ArrayList<T9DbRecord> drl = T9CSVUtil.CVSReader(is);
      String groupIdStr = fileForm.getParameter("groupId");
      int groupId = 0;
      if(T9Utility.isNullorEmpty(groupIdStr)){
        groupId = 0;
      }else{
        groupId = Integer.parseInt(groupIdStr);
      }
      
      T9AddressLogic dl = new T9AddressLogic();
      String psnName = "";
      String ministration = "";
      String nickName = "";
      String email = "";
      String mobilNo = "";
      String bpNo = "";
      String oicqNo = "";
      String icqNo = "";
      String sex = "";
      String birthday = "";
      String mate = "";
      String child = "";
      String postNoHome = "";
      String addHome = "";
      String telNoHome = "";
      String deptName = "";
      String postNoDept = "";
      String addDept = "";
      String telNoDept = "";
      String faxNoDept = "";
      String notes = "";
      String color = "red";
      Map map = new HashMap();
      for(int i = 0; i < drl.size(); i++){
        psnName = (String) drl.get(i).getValueByName("姓名");
        if(T9Utility.isNullorEmpty(psnName)){
          continue;
        }
        isCount++;
        ministration = (String) drl.get(i).getValueByName("职位");
        nickName = (String) drl.get(i).getValueByName("昵称");
        email = (String) drl.get(i).getValueByName("电子邮件地址");
        mobilNo = (String) drl.get(i).getValueByName("手机");
        bpNo = (String) drl.get(i).getValueByName("传呼机");
        oicqNo = (String) drl.get(i).getValueByName("QQ");
        icqNo = (String) drl.get(i).getValueByName("MSN");
        sex = (String) drl.get(i).getValueByName("性别");
        birthday = (String) drl.get(i).getValueByName("生日");
        mate = (String) drl.get(i).getValueByName("配偶");
        child = (String) drl.get(i).getValueByName("子女");
        postNoHome = (String) drl.get(i).getValueByName("家庭所在地邮政编码");
        addHome = (String) drl.get(i).getValueByName("家庭所在街道");
        telNoHome = (String) drl.get(i).getValueByName("家庭电话1");
        deptName = (String) drl.get(i).getValueByName("公司");
        postNoDept = (String) drl.get(i).getValueByName("公司所在地邮政编码");
        addDept = (String) drl.get(i).getValueByName("公司所在街道");
        telNoDept = (String) drl.get(i).getValueByName("办公电话1");
        faxNoDept = (String) drl.get(i).getValueByName("公司传真");
        notes = (String) drl.get(i).getValueByName("附注");
          
        if("女".equals(sex)){
          sex = "1";
        }else if("男".equals(sex)){
          sex = "0";
        }else{
          sex = "";
        }
        map.put("psnName" , psnName);
        map.put("ministration" , ministration);
        map.put("nickName" , nickName);
        map.put("email" , email);
        map.put("mobilNo" , mobilNo);
        map.put("bpNo" , bpNo);
        map.put("oicqNo" , oicqNo);
        map.put("icqNo" , icqNo);
        map.put("sex" , sex);
        map.put("birthday" , birthday);
        map.put("mate" , mate);
        map.put("child" , child);
        map.put("postNoHome" , postNoHome);
        map.put("addHome" , addHome);
        map.put("telNoHome" , telNoHome);
        map.put("deptName" , deptName);
        map.put("postNoDept" , postNoDept);
        map.put("addDept" , addDept);
        map.put("telNoDept" , telNoDept);
        map.put("faxNoDept" , faxNoDept);
        map.put("notes" , notes);
          
        T9ORM orm = new T9ORM();
        if(dl.existsGroupId(dbConn, groupId, psnName)){
          int seqId = dl.getGroupSeqIdLogic(dbConn, groupId, psnName);
          map.put("seqId" , seqId);
          orm.updateSingle(dbConn , "address" , map);
        }else{
          map.put("userId" , null);
          map.put("groupId" , groupId);
          orm.saveSingle(dbConn , "address" , map);
        }
      }

      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (SizeLimitExceededException ex) {
      return "/core/funcs/address/public/group/importPublic.jsp?maxSize=1";
    }  catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      //ex.printStackTrace();
      throw ex;
    } 
    return "/core/funcs/address/public/group/importPublic.jsp?isCount="+isCount;
  }
  
  public String getPrintPrivateList(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int seqId = person.getSeqId();
      int groupId = Integer.parseInt(request.getParameter("groupId"));
      List<Map> list = new ArrayList();
      T9ORM orm = new T9ORM();
      HashMap map = null;
      StringBuffer sb = new StringBuffer("[");
      String[] filters = new String[]{"USER_ID='" + seqId + "' AND GROUP_ID = " + groupId + " order by PSN_NAME"};
      List funcList = new ArrayList();
      funcList.add("address");
      map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters);
      list.addAll((List<Map>) map.get("ADDRESS"));
      
      for(Map ms : list){
        sb.append("{");
        sb.append("seqId:\"" + ms.get("seqId") + "\"");
        sb.append(",psnName:\"" + (ms.get("psnName") == null ? "" : ms.get("psnName")) + "\"");
        sb.append(",addDept:\"" + (ms.get("addDept") == null ? "" : ms.get("addDept")) + "\"");
        sb.append(",postNoDept:\"" + (ms.get("postNoDept") == null ? "" : ms.get("postNoDept")) + "\"");
        sb.append(",telNoDept:\"" + (ms.get("telNoDept") == null ? "" : ms.get("telNoDept")) + "\"");
        sb.append(",telNoHome:\"" + (ms.get("telNoHome") == null ? "" : ms.get("telNoHome")) + "\"");
        sb.append(",mobilNo:\"" + (ms.get("mobilNo") == null ? "" : ms.get("mobilNo")) + "\"");
        sb.append(",faxNoDept:\"" + (ms.get("faxNoDept") == null ? "" : ms.get("faxNoDept")) + "\"");
        sb.append(",email:\"" + (ms.get("email") == null ? "" : ms.get("email")) + "\"");
        
        sb.append("},");
      }
      sb.deleteCharAt(sb.length() - 1); 
      if (list.size() == 0) {
        sb = new StringBuffer("[");
      }
      sb.append("]");
      //System.out.println(sb);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getPrintPublicList(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int seqId = person.getSeqId();
      int groupId = Integer.parseInt(request.getParameter("groupId"));
      List<Map> list = new ArrayList();
      T9ORM orm = new T9ORM();
      HashMap map = null;
      StringBuffer sb = new StringBuffer("[");
      String[] filters = new String[]{"USER_ID is null AND GROUP_ID = " + groupId + " order by PSN_NAME"};
      List funcList = new ArrayList();
      funcList.add("address");
      map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters);
      list.addAll((List<Map>) map.get("ADDRESS"));
      
      for(Map ms : list){
        sb.append("{");
        sb.append("seqId:\"" + ms.get("seqId") + "\"");
        sb.append(",psnName:\"" + (ms.get("psnName") == null ? "" : ms.get("psnName")) + "\"");
        sb.append(",addDept:\"" + (ms.get("addDept") == null ? "" : ms.get("addDept")) + "\"");
        sb.append(",postNoDept:\"" + (ms.get("postNoDept") == null ? "" : ms.get("postNoDept")) + "\"");
        sb.append(",telNoDept:\"" + (ms.get("telNoDept") == null ? "" : ms.get("telNoDept")) + "\"");
        sb.append(",telNoHome:\"" + (ms.get("telNoHome") == null ? "" : ms.get("telNoHome")) + "\"");
        sb.append(",mobilNo:\"" + (ms.get("mobilNo") == null ? "" : ms.get("mobilNo")) + "\"");
        sb.append(",faxNoDept:\"" + (ms.get("faxNoDept") == null ? "" : ms.get("faxNoDept")) + "\"");
        sb.append(",email:\"" + (ms.get("email") == null ? "" : ms.get("email")) + "\"");
        
        sb.append("},");
      }
      sb.deleteCharAt(sb.length() - 1); 
      if (list.size() == 0) {
        sb = new StringBuffer("[");
      }
      sb.append("]");
      //System.out.println(sb);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getSelectGroup(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      String groupId = request.getParameter("groupId");
      String loginDeptId = String.valueOf(person.getDeptId());
      String loginSeqId = String.valueOf(person.getSeqId());
      String data = "0";
      T9ORM orm = new T9ORM();
      HashMap map = null;
      StringBuffer sb = new StringBuffer("[");
      ArrayList<T9Address> perList = null;
      List<Map> list = new ArrayList();
      T9AddressLogic logic = new T9AddressLogic();
      ArrayList<T9AddressGroup> groupList = logic.getSelectGroup(dbConn);
      for(int i = 0; i < groupList.size(); i++){
        //groupList.get(i).getSeqId();
        int count = logic.getSupportPriv(dbConn, String.valueOf(groupList.get(i).getSeqId()), loginDeptId, loginSeqId , person.getUserPriv());
        if(count > 0 || person.isAdminRole()){
          String[] filterPer = new String[]{"SEQ_ID="+groupList.get(i).getSeqId()};
          List funcList = new ArrayList();
          funcList.add("addressGroup");
          map = (HashMap)orm.loadDataSingle(dbConn, funcList, filterPer);
          list.addAll((List<Map>) map.get("ADDRESS_GROUP"));
        }else{
          continue;
        }
      }
      if(list.size() > 1){
        for(Map m : list){
          sb.append("{");
          sb.append("seqId:\"" + m.get("seqId") + "\"");
          sb.append(",groupName:\"" + m.get("groupName") + "\"");
          sb.append("},");
        }
        sb.deleteCharAt(sb.length() - 1); 
      }else{
        for(Map m : list){
          sb.append("{");
          sb.append("seqId:\"" + m.get("seqId") + "\"");
          sb.append(",groupName:\"" + m.get("groupName") + "\"");
          sb.append("}");
        }
      }
      sb.append("]");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getSelectGroup1(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      String groupId = request.getParameter("groupId");
      String loginDeptId = String.valueOf(person.getDeptId());
      String loginSeqId = String.valueOf(person.getSeqId());
      String data = "0";
      T9ORM orm = new T9ORM();
      HashMap map = null;
      StringBuffer sb = new StringBuffer("[");
      ArrayList<T9Address> perList = null;
      List<Map> list = new ArrayList();
      T9AddressLogic logic = new T9AddressLogic();
      ArrayList<T9AddressGroup> groupList = logic.getSelectGroup(dbConn);
      for(int i = 0; i < groupList.size(); i++){
        //groupList.get(i).getSeqId();
        int count = logic.getSupportPriv1(dbConn, String.valueOf(groupList.get(i).getSeqId()), loginDeptId, loginSeqId , person.getUserPriv());
        if(count > 0 || person.isAdminRole()){
          String[] filterPer = new String[]{"SEQ_ID="+groupList.get(i).getSeqId()};
          List funcList = new ArrayList();
          funcList.add("addressGroup");
          map = (HashMap)orm.loadDataSingle(dbConn, funcList, filterPer);
          list.addAll((List<Map>) map.get("ADDRESS_GROUP"));
        }else{
          continue;
        }
      }
      if(list.size() > 1){
        for(Map m : list){
          sb.append("{");
          sb.append("seqId:\"" + m.get("seqId") + "\"");
          sb.append(",groupName:\"" + m.get("groupName") + "\"");
          sb.append("},");
        }
        sb.deleteCharAt(sb.length() - 1); 
      }else{
        for(Map m : list){
          sb.append("{");
          sb.append("seqId:\"" + m.get("seqId") + "\"");
          sb.append(",groupName:\"" + m.get("groupName") + "\"");
          sb.append("}");
        }
      }
      sb.append("]");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getPrivateGroup(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      
      StringBuffer sb = new StringBuffer();
      
      String query = "SELECT * from ADDRESS_GROUP where USER_ID='"+person.getSeqId()+"' order by GROUP_NAME";
      Statement stmt = null;
      ResultSet rs = null;
      String groupIdStr = "";
      int count = 0 ;
      try {
        stmt = dbConn.createStatement();
        rs = stmt.executeQuery(query);
        while (rs.next()) {
          String groupId = rs.getString("SEQ_ID");
          String groupName = T9Utility.encodeSpecial(T9Utility.null2Empty(rs.getString("GROUP_NAME")));
          sb.append("{");
          sb.append("seqId:\"" + groupId + "\"");
          sb.append(",groupName:\""+ groupName+ "\"");
          sb.append("},");
          count++ ;
        }
      } catch (Exception ex) {
         throw ex;
      } finally {
        T9DBUtility.close(stmt, rs, log);
      }
      if (count > 0) {
        sb.deleteCharAt(sb.length() - 1);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  } 
  public String getDefaultGroup(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int loginSeqId = person.getSeqId();
      int loginDeptId = person.getDeptId();
      String loginSeqIdStr = String.valueOf(person.getDeptId());
      String loginDeptIdStr = String.valueOf(person.getDeptId());
      int loginUserPriv = Integer.parseInt(person.getUserPriv());
      String loginUserId = person.getUserId(); 
      T9ORM orm = new T9ORM();
      HashMap map = null;
      List<Map> list = new ArrayList();
      StringBuffer sb = new StringBuffer("[");
      
      String[] filters = new String[]{"(USER_ID is null and ("
          + findInSet(String.valueOf(loginSeqId),"PRIV_USER")
          + " or "+ findInSet(String.valueOf(loginDeptId),"PRIV_DEPT")
          + " or "+ findInSet(String.valueOf(loginUserPriv),"PRIV_ROLE")
           + " or "+ findInSet("0","PRIV_DEPT")
            + " or "+ findInSet("ALL_DEPT","PRIV_DEPT")
          + ")) order by USER_ID asc, ORDER_NO asc, GROUP_NAME asc"};
      
      //and (" + T9DBUtility.findInSet(loginDeptIdStr, "SUPPORT_DEPT") +" or (SUPPORT_DEPT like 0 or SUPPORT_DEPT like 'ALL_DEPT') or " + T9DBUtility.findInSet(loginSeqIdStr, "SUPPORT_USER") +" )
      String[] filters2 = new String[]{"USER_ID is null order by ORDER_NO asc, GROUP_NAME asc"};
      List funcList = new ArrayList();
      funcList.add("addressGroup");
      map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters2);
      list.addAll((List<Map>) map.get("ADDRESS_GROUP"));
      int flag = 0;
      for(Map ms : list){
        if(!"0".equals(String.valueOf(ms.get("privDept")))){
          if(!findId(String.valueOf(ms.get("privDept")), String.valueOf(loginDeptId)) && !findId(String.valueOf(ms.get("privRole")), String.valueOf(loginUserPriv)) && !findId(String.valueOf(ms.get("privUser")), String.valueOf(loginSeqId))){
            flag++;
            continue;
          }
        }
        
        String groupName = (String) ms.get("groupName");
        if(!T9Utility.isNullorEmpty(groupName)){
          groupName = groupName.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
        }
        sb.append("{");
        sb.append("seqId:\"" + ms.get("seqId") + "\"");
        sb.append(",userId:\"" + (ms.get("userId") == null ? "" : ms.get("userId")) + "\"");
        sb.append(",groupName:\"" + (ms.get("groupName") == null ? "" : groupName) + "\"");
        sb.append("},");
      }
      sb.deleteCharAt(sb.length() - 1); 
      
      if(list.size() == 0 || flag == list.size()){
        sb.append("[");
      }
      sb.append("]");
      //System.out.println(sb+"NNNNMssss");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
