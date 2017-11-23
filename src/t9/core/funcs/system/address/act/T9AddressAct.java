package t9.core.funcs.system.address.act;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
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
import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.jexcel.util.T9CSVUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.funcs.person.logic.T9UserPrivLogic;
import t9.core.funcs.system.address.logic.T9AddressLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;
public class T9AddressAct {
  private static Logger log = Logger.getLogger("t9.core.funcs.system.address.T9AddressAct");

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
      String privUser = request.getParameter("privUser");
      String privDept = request.getParameter("privDept");
      String privRole = request.getParameter("privRole");
      String orderNos = request.getParameter("orderNo");
      String groupName = request.getParameter("groupName");
      if(orderNos.equals("")){
        orderNo = 0;
      }else{
        orderNo = Integer.parseInt(request.getParameter("orderNo"));
      }
      Map m =new HashMap();
      m.put("privUser", privUser);
      m.put("privDept", privDept);
      m.put("privRole", privRole);
      m.put("orderNo", orderNo);
      m.put("groupName", groupName);
      T9ORM t = new T9ORM();
      T9AddressLogic cwLogic = new T9AddressLogic();
      groupName = T9DBUtility.escapeLike(groupName);
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
      String[] filters = new String[]{"USER_ID is null order by ORDER_NO asc,GROUP_NAME asc"};
      List funcList = new ArrayList();
      funcList.add("addressGroup");
      map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters);
      list.addAll((List<Map>) map.get("ADDRESS_GROUP"));
      
      T9PersonLogic logic = new T9PersonLogic();
      T9DeptLogic logic1 = new T9DeptLogic();
      T9UserPrivLogic logic3 =new T9UserPrivLogic();
      for(Map ms : list){
        String groupName = (String) ms.get("groupName");
        if(!T9Utility.isNullorEmpty(groupName)){
          groupName = groupName.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
        }
        String dept =  (String)(ms.get("privDept") == null ? "" : ms.get("privDept"));
        String user = (String)(ms.get("privUser") == null ? "" : ms.get("privUser"));
        String role =  (String)(ms.get("privRole") == null ? "" : ms.get("privRole"));
        String userName= T9Utility.encodeSpecial(logic.getNameBySeqIdStr(user, dbConn));
        String deptName  = T9Utility.encodeSpecial(logic1.getNameByIdStr(dept, dbConn));
        String roleName = T9Utility.encodeSpecial(logic3.getNameByIdStr(role, dbConn));
        sb.append("{");
        sb.append("seqId:\"" + ms.get("seqId") + "\"");
        sb.append(",groupName:\"" + (ms.get("groupName") == null ? "" : groupName) + "\"");
        sb.append(",privDept:\"" + dept+ "\"");
        sb.append(",privRole:\"" + role + "\"");
        sb.append(",privUser:\"" + user + "\"");
        sb.append(",deptName:\"" + deptName+ "\"");
        sb.append(",roleName:\"" + roleName + "\"");
        sb.append(",userName:\"" + userName + "\"");
        sb.append("},");
      }
      sb.deleteCharAt(sb.length() - 1); 
      if(list.size() == 0){
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
      
      T9PersonLogic logic = new T9PersonLogic();
      T9DeptLogic logic1 = new T9DeptLogic();
      T9UserPrivLogic logic3 =new T9UserPrivLogic();
      for(Map ms : list){
        String groupName = (String) ms.get("groupName");
        if(!T9Utility.isNullorEmpty(groupName)){
          groupName = groupName.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
        }
        String dept =  (String)(ms.get("privDept") == null ? "" : ms.get("privDept"));
        String user = (String)(ms.get("privUser") == null ? "" : ms.get("privUser"));
        String role =  (String)(ms.get("privRole") == null ? "" : ms.get("privRole"));
        String userName= T9Utility.encodeSpecial(logic.getNameBySeqIdStr(user, dbConn));
        String deptName  = T9Utility.encodeSpecial(logic1.getNameByIdStr(dept, dbConn));
        String roleName = T9Utility.encodeSpecial(logic3.getNameByIdStr(role, dbConn));
        sb.append("{");
        sb.append("groupName:\"" + (ms.get("groupName") == null ? "" : groupName) + "\"");
        sb.append(",orderNo:\"" + ms.get("orderNo") + "\"");
        sb.append(",privDept:\"" + dept + "\"");
        sb.append(",privRole:\"" + role + "\"");
        sb.append(",privUser:\"" + user + "\"");
        sb.append(",deptName:\"" + deptName+ "\"");
        sb.append(",roleName:\"" + roleName + "\"");
        sb.append(",userName:\"" + userName + "\"");
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
   * 读取维护权限信息
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getEditPrivGroup(HttpServletRequest request,
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
      T9PersonLogic logic = new T9PersonLogic();
      T9DeptLogic logic1 = new T9DeptLogic();
      for(Map ms : list){
        String groupName = (String) ms.get("groupName");
        if(!T9Utility.isNullorEmpty(groupName)){
          groupName = groupName.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
        }
        String dept =  (String)(ms.get("supportDept") == null ? "" : ms.get("supportDept"));
        String user = (String)(ms.get("supportUser") == null ? "" : ms.get("supportUser"));
        String userName= logic.getNameBySeqIdStr(user, dbConn);
        String deptName  = logic1.getNameByIdStr(dept, dbConn);
        sb.append("{");
        sb.append("groupName:\"" + (ms.get("groupName") == null ? "" : groupName) + "\"");
        sb.append(",supportDept:\"" + dept + "\"");
        sb.append(",supportUser:\"" + user + "\"");
        sb.append(",supportUserName:\"" +T9Utility.encodeSpecial(userName)  + "\"");
        sb.append(",supportDeptName:\"" + T9Utility.encodeSpecial(deptName) + "\"");
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
      String privUser = request.getParameter("privUser");
      String privDept = request.getParameter("privDept");
      String privRole = request.getParameter("privRole");
      String orderNos = request.getParameter("orderNo");
      String groupName = request.getParameter("groupName");
      String groupNameOld = request.getParameter("groupNameOld");
      //System.out.println(groupName+"====="+groupNameOld);
      if(orderNos.equals("")){
        orderNo = 0;
      }else{
        orderNo = Integer.parseInt(request.getParameter("orderNo"));
      }
      Map m =new HashMap();
      m.put("seqId", seqId);
      m.put("privUser", privUser);
      m.put("privDept", privDept);
      m.put("privRole", privRole);
      m.put("orderNo", orderNo);
      m.put("groupName", groupName);
      T9ORM t = new T9ORM();
      T9AddressLogic cwLogic = new T9AddressLogic();
      if(groupNameOld.equals(groupName)){
        t.updateSingle(dbConn, "addressGroup", m);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功");
      }else{
        groupName = T9DBUtility.escapeLike(groupName);
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
   * 编辑维护权限
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String updatePrivGroup(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int seqIdUser = person.getSeqId();
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int orderNo = 0;
      String seqId = request.getParameter("seqId");
      String supportUser = request.getParameter("supportUser");
      String supportDept = request.getParameter("supportDept");
      String groupName = request.getParameter("groupName");
      String groupNameOld = request.getParameter("groupNameOld");
      //System.out.println(groupName+"====="+groupNameOld);
   
      Map m =new HashMap();
      m.put("seqId", seqId);
      m.put("supportUser", supportUser);
      m.put("supportDept", supportDept);
      m.put("groupName", groupName);
      T9ORM t = new T9ORM();
      T9AddressLogic cwLogic = new T9AddressLogic();
      if(groupNameOld.equals(groupName)){
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
  
  public String importAddressPublic(HttpServletRequest request,HttpServletResponse response) throws Exception{
    InputStream is = null;
    Connection dbConn = null;
    String data = null;
    int isCount = 0;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
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
          map.put("userId" , null);
          map.put("groupId" , groupId);
          orm.saveSingle(dbConn , "address" , map);
        }
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }  catch (SizeLimitExceededException ex) {
      
      return "/core/funcs/system/address/manage/importManage.jsp?maxSize=1";
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      //ex.printStackTrace();
      throw ex;
    } 
    return "/core/funcs/system/address/manage/importManage.jsp?isCount="+isCount;
  }
  
  public String getPrintList(HttpServletRequest request,
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
}
