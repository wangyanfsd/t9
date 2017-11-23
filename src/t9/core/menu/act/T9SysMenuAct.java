package t9.core.menu.act;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.dept.data.T9Department;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.menu.data.T9SysFunction;
import t9.core.menu.data.T9SysMenu;
import t9.core.menu.logic.T9SysMenuLog;
import t9.core.util.db.T9DTJ;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;

public class T9SysMenuAct {
  private static Logger log = Logger.getLogger("t9.core.menu.act.T9SysMenuAct");
  
  T9SysMenuLog menuLog = new T9SysMenuLog();
  ArrayList<T9SysFunction> functionList = null;
  public String listSysMenu(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      ArrayList<T9SysMenu> menuList = null;
      T9ORM orm = new T9ORM();
      Map m = null;
      menuList = (ArrayList<T9SysMenu>)orm.loadListSingle(dbConn, T9SysMenu.class, m);
      request.setAttribute("menuList", menuList);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/menu/sysmenulist.jsp";
  }
  
  public String listSysFunction(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    Statement stmt = null;
    ResultSet rs = null;
    String menuId = request.getParameter("menuId");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      ArrayList<T9SysMenu> menuList = null;
      T9ORM orm = new T9ORM();
      Map m = null;
      menuList = (ArrayList<T9SysMenu>)orm.loadListSingle(dbConn, T9SysMenu.class, m);

      functionList = menuLog.listFunction(dbConn, menuId);
      request.setAttribute("functionList", functionList);
           
      String sql = "select IMAGE, MENU_NAME from SYS_MENU where MENU_ID = '" + menuId + "'";
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
      while(rs.next()) {
        String image = rs.getString("IMAGE");
        String menuName = rs.getString("MENU_NAME");
        
        request.setAttribute("image", image);
        request.setAttribute("menuName", menuName);
      }
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/menu/sysfunctionlist.jsp?menuId=" + menuId;
  }
  
  public String listMenuAndFunction(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    //String menuId = request.getParameter("menuId");
    String data = "";
    String dataMenu = "";
    String dataFunc = "";
    
    T9DTJ dtj = new T9DTJ();
    Connection dbConn = null;
    T9SysMenu menu = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      ArrayList<T9SysMenu> menuList = null;
      T9ORM orm = new T9ORM();
      Map m = null;
      menuList = (ArrayList<T9SysMenu>)orm.loadListSingle(dbConn, T9SysMenu.class, m);
      data += "[";
      for(int i = 0; i < menuList.size(); i++){
        menu = menuList.get(i);
        
        String menuId = menu.getMenuId();
        String[] filters = new String[]{"T0.MENU_ID = '" + menuId + "'"};
        StringBuffer buffer  = dtj.toJson(dbConn, "11111", filters);
        dataMenu = buffer.toString(); 
        
        filters = new String[]{"T0.MENU_ID like '" + menuId + "%' order by MENU_ID"};
        buffer  = dtj.toJson(dbConn, "11112", filters);
        dataFunc = buffer.toString();
        data += "[" + dataMenu + "," + dataFunc + "]" + ",";
      }
      data = data.substring(0,data.length()-1);
      data += "]";
      //System.out.println("+++++++++++++++++++" + data);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出主菜单和子菜单项的数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }  
  
  public String getSysMenu(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String seqId = request.getParameter("seqId");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();

      T9SysMenu menu = null;
      String data = null;
       
      T9ORM orm = new T9ORM();
      menu = (T9SysMenu)orm.loadObjSingle(dbConn, T9SysMenu.class, Integer.parseInt(seqId));
      if(menu == null) {
        menu = new T9SysMenu();
      }
      data = T9FOM.toJson(menu).toString();
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
  
  public String getSysFunction(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    response.setCharacterEncoding("UTF-8");
    Connection dbConn = null;
    String data = null;
    String seqId = request.getParameter("seqId");
    String tabNo = request.getParameter("tableNo");
    T9DTJ dtj = new T9DTJ();
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      String[] filters = new String[]{"T0.SEQ_ID = " + seqId };
      StringBuffer d  = dtj.toJson(dbConn, tabNo, filters);
      data = d.toString();
    
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
  
  public String addSysMenu(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String menuId = null;
    int num = 0;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);     
      dbConn = requestDbConn.getSysDbConn();
      
      T9SysMenu menu = (T9SysMenu)T9FOM.build(request.getParameterMap());
      
      menuId = menu.getMenuId();
      num = menuLog.selectMenu(dbConn, menuId);
      if(num >= 1) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "主菜单分类代码重复, 主菜单分类代码不能重复");
        return "/core/inc/rtjson.jsp";
      }
      
      T9ORM orm = new T9ORM();
      orm.saveSingle(dbConn, menu);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功添加主菜单分类");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String addSysFunction(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String menuId = null;
    int num = 0;
    String menuSort = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);     
      dbConn = requestDbConn.getSysDbConn();
      
      T9SysFunction function = (T9SysFunction)T9FOM.build(request.getParameterMap());
      menuId = function.getMenuId();
      menuSort = function.getMenuSort();
      
      num = menuLog.selectFunction(dbConn, menuId, menuSort);
      if(num >= 1) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "子菜单代码重复, 子菜单代码不能重复");
        return "/core/inc/rtjson.jsp";
      }
      
      Map map = new HashMap();
      map.put("menuId", "" + function.getMenuId() + function.getMenuSort() + "");
      map.put("funcName", function.getFuncName());
      map.put("funcCode", function.getFuncCode());
      
      T9ORM orm = new T9ORM();
      orm.saveSingle(dbConn, "sysFunction", map);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功添加子菜单项");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
 
  public String updateSysMenu(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String menuIdOld = request.getParameter("menuIdOld"); 
    String menuId = null;
    String menuName = null;
    String image = null;
    int num = 0;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);   
      dbConn = requestDbConn.getSysDbConn();
      
      T9SysMenu menu = (T9SysMenu)T9FOM.build(request.getParameterMap());
      menuId = menu.getMenuId();
      menuName = menu.getMenuName();
      image = menu.getImage();
      
      if(menuIdOld.equals(menuId)) {
        
        menuLog.updateMenu(dbConn, menuId, menuName, image);
        
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "修改主分类成功！");  
        return "/core/inc/rtjson.jsp";
      }else {
        String funcMenuId = null;
        num = menuLog.selectMenu(dbConn, menuId);
        if(num >= 1) {
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
          request.setAttribute(T9ActionKeys.RET_MSRG, "主菜单分类代码重复, 主菜单分类代码不能重复");
          return "/core/inc/rtjson.jsp";
        }
        
        menuLog.insertMenu(dbConn, menuId, menuName, image);
        functionList = menuLog.listFunction(dbConn, menuIdOld);
          for(int i = 0; i < functionList.size(); i++) {
            T9SysFunction function  = functionList.get(i);
            funcMenuId = function.getMenuId();
            menuLog.updateFunction(dbConn, menuId, menuIdOld, funcMenuId);
          }
        menuLog.deleteMenu(dbConn, menuIdOld);
        
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG,"成功修改主菜单分类");
      }
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";    
  }
  
  public String updateSysFunction(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    Map map = null;
    String menuSortOld = request.getParameter("menuSortOld");
    
    String menuId = null;
    String menuSort = null;
    int num = 0;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);   
      dbConn = requestDbConn.getSysDbConn();
      
      T9ORM orm = new T9ORM();
      T9SysFunction function = (T9SysFunction)T9FOM.build(request.getParameterMap());
      menuId = function.getMenuId();
      menuSort = function.getMenuSort();
      
      if(menuSortOld.equals(String.valueOf(menuSort))) {
        map = new HashMap();
        map.put("seqId", "" + function.getSeqId());
        map.put("menuId", "" + function.getMenuId() + function.getMenuSort() + "");
        map.put("funcName", function.getFuncName());
        map.put("funcCode", function.getFuncCode());
        
        orm.updateSingle(dbConn, "sysFunction", map);
        
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "修改子菜单项成功！");  
        return "/core/inc/rtjson.jsp";
      }
      
      num = menuLog.selectFunction(dbConn, menuId, menuSort);
      if(num >= 1) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "子菜单代码重复, 子菜单代码不能重复");
        return "/core/inc/rtjson.jsp";
      }
          
      map = new HashMap();
      map.put("seqId", "" + function.getSeqId());
      map.put("menuId", "" + function.getMenuId() + function.getMenuSort() + "");
      map.put("funcName", function.getFuncName());
      map.put("funcCode", function.getFuncCode());
      
      orm.updateSingle(dbConn, "sysFunction", map);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功修改子菜单项");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getNoTree(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    HashMap map = null;
    String num = request.getParameter("num");
    String length = request.getParameter("length");
    
    if(num==null||num.equals("0")){
      num = "";
    }

    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
          
      T9ORM orm = new T9ORM();
      StringBuffer sb = new StringBuffer("[");
      if(Integer.parseInt(length) == 2) {
        ArrayList<T9SysMenu> menuList = null;
        Map m =null;
        menuList = (ArrayList<T9SysMenu>)orm.loadListSingle(dbConn, T9SysMenu.class, m);
               
        for(int i = 0; i < menuList.size(); i++) {
          T9SysMenu menu = menuList.get(i);
          sb.append("{");
          sb.append("nodeId:\"" + menu.getMenuId() + "\"");
          sb.append(",name:\"" + menu.getMenuName() + "\"");
          sb.append(",isHaveChild:" + 1 + "");
          sb.append(",imgAddress:\"" + request.getContextPath() + "/core/styles/imgs/" + menu.getImage() + "\"");
          sb.append("},");
        }       
        sb.deleteCharAt(sb.length() - 1);       
      }else {
        List funcList = new ArrayList();
        funcList.add("sysFunction");
        String[] filters = new String[]{"MENU_ID like '" + num + "%'"," length(MENU_ID) = " + length + "  order by MENU_ID"};
        map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters);
        List<Map> list = (List<Map>) map.get("SYS_FUNCTION");
        for(Map m : list) {
          sb.append("{");
          sb.append("nodeId:\"" + m.get("menuId") + "\"");
          sb.append(",name:\"" + m.get("funcName") + "\"");
          sb.append(",isHaveChild:" + 1 + "");
          sb.append(",imgAddress:\"" + request.getContextPath() + "/core/styles/imgs/" + m.get("funcCode") + "\"");
          sb.append("},");
        } 
        //System.out.println(map);
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
  
  public String deleteSysMenu(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    request.setCharacterEncoding("utf-8");
    String seqId = request.getParameter("seqId");
    String menuId = request.getParameter("menuId");
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);   
      dbConn = requestDbConn.getSysDbConn();
      
      menuLog.deleteSysMenu(dbConn, seqId, menuId);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功删除主菜单分类");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String deleteSysFunction(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    request.setCharacterEncoding("utf-8");
    String seqId = request.getParameter("seqId");
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);   
      dbConn = requestDbConn.getSysDbConn();
      
      Map map = new HashMap();
      map.put("seqId", seqId);
      
      T9ORM orm = new T9ORM();
      orm.deleteSingle(dbConn, "sysFunction", map);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功删除子菜单项");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
