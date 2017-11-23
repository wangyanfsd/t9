package t9.cms.column.logic;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import t9.cms.area.data.T9CmsArea;
import t9.cms.area.logic.T9AreaLogic;
import t9.cms.column.data.T9CmsColumn;
import t9.cms.common.logic.T9CmsCommonLogic;
import t9.cms.content.data.T9CmsContent;
import t9.cms.content.logic.T9ContentLogic;
import t9.cms.permissions.logic.T9PermissionsLogic;
import t9.cms.station.data.T9CmsStation;
import t9.cms.station.logic.T9StationLogic;
import t9.cms.template.data.T9CmsTemplate;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9SysProps;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.cms.velocity.T9velocityUtil;

public class T9ColumnLogic {
  public static final String attachmentFolder = "cms";

  /**
   * CMS 文章模板获取
   * 
   * @param dbConn
   * @return
   * @throws Exception
   */
  public String getTemplateArticle(Connection dbConn, String stationId) throws Exception {
    try {
      StringBuffer data = new StringBuffer("[");
      String sql = " select c1.SEQ_ID, c1.TEMPLATE_NAME "
                 + " from cms_template c1 "
                 + " where c1.template_type = 2 "
                 + " and c1.station_id =" + stationId
                 + " ORDER BY c1.SEQ_ID asc ";
      PreparedStatement ps = null;
      ResultSet rs = null;
      boolean flag = false;
      try {
        ps = dbConn.prepareStatement(sql);
        rs = ps.executeQuery();
        while (rs.next()) {
          data.append("{seqId:"+rs.getInt("SEQ_ID")+","+"templateName:\""+rs.getString("TEMPLATE_NAME")+"\"},");
          flag = true;
        }
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        T9DBUtility.close(ps, rs, null);
      }
      if(flag){
        data = data.deleteCharAt(data.length() - 1);
      }
      data.append("]");
      return data.toString();
    } catch (Exception e) {
      throw e;
    }
  }
  
  /**
   * 获取详情
   * 
   * @param conn
   * @param seqId
   * @return
   * @throws Exception
   */
  public StringBuffer getInfomation(Connection conn, int stationId, int parentId) throws Exception {
    try {
      T9ORM orm = new T9ORM();
      StringBuffer data = new StringBuffer();
      if(parentId == 0){
        T9CmsStation station = (T9CmsStation) orm.loadObjSingle(conn, T9CmsStation.class, stationId);
        data = T9FOM.toJson(station);
      }
      else{
        T9CmsColumn column = (T9CmsColumn) orm.loadObjSingle(conn, T9CmsColumn.class, parentId);
        T9CmsStation station = (T9CmsStation) orm.loadObjSingle(conn, T9CmsStation.class, column.getStationId());
        data = T9FOM.toJson(column);
        data = data.deleteCharAt(data.length()-1);
        data.append(",stationName:\""+station.getStationName()+"\"}");
      }
      return data;
    } catch (Exception ex) {
      throw ex;
    }
  }
  
  /**
   * CMS栏目 添加
   * 
   */
  public int addColumn(Connection dbConn, T9CmsColumn column, T9Person person) throws Exception{
    
    int columnIndex = 1;
    PreparedStatement ps = null;
    ResultSet rs = null;
    String sql = "  SELECT max(COLUMN_INDEX) COLUMN_INDEX FROM cms_column c WHERE c.STATION_ID ="+column.getStationId()+" and c.PARENT_ID ="+column.getParentId();
    try {
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){
        columnIndex = rs.getInt("COLUMN_INDEX") + 1;
      }
    } catch (Exception ex) {
      throw ex;
    }
    finally{
      T9DBUtility.close(ps, null, null);
    }
    
    T9ORM orm = new T9ORM();
    column.setCreateId(person.getSeqId());
    column.setCreateTime(T9Utility.parseTimeStamp());
    column.setColumnIndex(columnIndex);
    
    //初始化权限
    column.setVisitUser("0||");
    column.setEditUser("0||");
    column.setNewUser("0||");
    column.setDelUser("0||");
    column.setRelUser("0||");
    column.setEditUserContent("0||");
    column.setApprovalUserContent("0||");
    column.setReleaseUserContent("0||");
    column.setRecevieUserContent("0||");
    column.setOrderContent("0||");
    
    orm.saveSingle(dbConn,column);
    
    T9CmsStation station = (T9CmsStation) orm.loadObjSingle(dbConn, T9CmsStation.class, column.getStationId());
    String parent = getParentPath(dbConn, column);
    //System.out.println(parent);
    File file = new File(T9SysProps.getWebPath() + File.separator + station.getStationPath() + File.separator + parent + column.getColumnPath());
    file.mkdir();
    
    int seqId = this.getMaSeqId(dbConn, "CMS_COLUMN");
    return seqId;
  }
  
  /**
   * 递归获取目录结构
   * 
   * @param dbConn
   * @return
   * @throws Exception
   */
  public String getParentPath(Connection dbConn, T9CmsColumn column) throws Exception{
    String parentPath = "";
    if(column.getParentId() > 0){
      T9ORM orm = new T9ORM();
      T9CmsColumn parentColumn = (T9CmsColumn) orm.loadObjSingle(dbConn, T9CmsColumn.class, column.getParentId());
      if(parentColumn == null){
        return "";
      }
      parentPath = parentColumn.getColumnPath() + File.separator;
      parentPath = getParentPath(dbConn, parentColumn) + parentPath;
    }
    return parentPath;
  }
  
  /**
   * CMS 获取站点树
   * 
   * @param dbConn
   * @return
   * @throws Exception
   */
  public String getStationTree(Connection dbConn, T9Person person) throws Exception {
    
    T9ORM orm = new T9ORM();
    StringBuffer sb = new StringBuffer("[");
    boolean isHave = false;
    String filters[] = {" 1=1 order by SEQ_ID asc "};
    List<T9CmsStation> stations = orm.loadListSingle(dbConn, T9CmsStation.class, filters);
    if (stations != null && stations.size() > 0) {
      for (T9CmsStation station : stations) {
        
        //访问权限
        T9PermissionsLogic logic = new T9PermissionsLogic();
        if(logic.isPermissions(dbConn, station.getVisitUser(), person)){
          int dbSeqId = station.getSeqId();
          String stationName = T9Utility.null2Empty(station.getStationName());
          boolean flag = this.isHaveChild(dbConn, dbSeqId);
          if (flag) {
            sb.append("{");
            sb.append("nodeId:\"" + dbSeqId + ",station\"");
            sb.append(",name:\"" + T9Utility.encodeSpecial(stationName) + "\"");
            sb.append(",isHaveChild:" + 1 + "");
            sb.append(",extData:{visitUser:\""+station.getVisitUser()+"\",editUser:\""+station.getEditUser()+"\",newUser:\""+station.getNewUser()+"\",delUser:\""+station.getDelUser()+"\",relUser:\""+station.getRelUser()+"\"}");
            sb.append("},");
            isHave = true;
          } else {
            sb.append("{");
            sb.append("nodeId:\"" + dbSeqId + ",station\"");
            sb.append(",name:\"" + T9Utility.encodeSpecial(stationName) + "\"");
            sb.append(",isHaveChild:" + 0 + "");
            sb.append(",extData:{visitUser:\""+station.getVisitUser()+"\",editUser:\""+station.getEditUser()+"\",newUser:\""+station.getNewUser()+"\",delUser:\""+station.getDelUser()+"\",relUser:\""+station.getRelUser()+"\"}");
            sb.append("},");
            isHave = true;
          }
        }
      }
      if (isHave) {
        sb = sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("]");
    } else {
      sb.append("]");
    }
    return sb.toString();
  }
  
  /**
   * 站点是否存在子节点
   * 
   */
  public boolean isHaveChild(Connection dbConn, int dbSeqId){
    
    String sql = " select 1 from cms_column where STATION_ID ="+dbSeqId;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      if (rs.next()) {
        return true;
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    return false;
  }
  
  /**
   * 获取栏目树

   * 
   * 
   * @param dbConn
   * @param id
   * @return
   * @throws Exception
   */
  public String getColumnTree(Connection dbConn, String id, String type, T9Person person) throws Exception {
    T9ORM orm = new T9ORM();
    StringBuffer sb = new StringBuffer("[");
    boolean isHave = false;
    try {
      int seqId = 0;
      if (T9Utility.isNumber(id)) {
        seqId = Integer.parseInt(id);
      }
      String filters[];
      if("station".equals(type)){
        String filtersTemp[] = {" STATION_ID =" + seqId + " and PARENT_ID =" + 0 + " order by COLUMN_INDEX asc "};
        filters = filtersTemp;
      }
      else{
        String filtersTemp[] = {" PARENT_ID =" + seqId + " order by COLUMN_INDEX asc "};
        filters = filtersTemp;
      }
      List<T9CmsColumn> columns = orm.loadListSingle(dbConn, T9CmsColumn.class, filters);
      if (columns != null && columns.size() > 0) {
        for (T9CmsColumn column : columns) {
          
          //访问权限
          T9PermissionsLogic logic = new T9PermissionsLogic();
          if(logic.isPermissions(dbConn, column.getVisitUser(), person)){
            int dbSeqId = column.getSeqId();
            String columnName = T9Utility.null2Empty(column.getColumnName());
            boolean counter = isHaveChildColumn(dbConn, dbSeqId);
            if (counter) {
              sb.append("{");
              sb.append("nodeId:\"" + dbSeqId + ",column," + column.getStationId() + "\"");
              sb.append(",name:\"" + T9Utility.encodeSpecial(columnName) + "\"");
              sb.append(",isHaveChild:" + 1 + "");
              sb.append(",extData:{" +
              		"visitUser:\""+column.getVisitUser()+"\""
              	+ ",editUser:\""+column.getEditUser()+"\""
              	+	",newUser:\""+column.getNewUser()+"\""
              	+ ",delUser:\""+column.getDelUser()+"\""
              	+ ",relUser:\""+column.getRelUser()+"\""
              	+ ",editUserContent:\""+column.getEditUserContent()+"\""
              	+ ",approvalUserContent:\""+column.getApprovalUserContent()+"\""
              	+ ",releaseUserContent:\""+column.getReleaseUserContent()+"\""
              	+ ",recevieUserContent:\""+column.getRecevieUserContent()+"\""
              	+ ",orderContent:\""+column.getOrderContent()+"\""
              	+ "}");
              sb.append("},");
              isHave = true;
            } else {
              sb.append("{");
              sb.append("nodeId:\"" + dbSeqId + ",column," + column.getStationId() + "\"");
              sb.append(",name:\"" + T9Utility.encodeSpecial(columnName) + "\"");
              sb.append(",isHaveChild:" + 0 + "");
              sb.append(",extData:{" +
                  "visitUser:\""+column.getVisitUser()+"\""
                + ",editUser:\""+column.getEditUser()+"\""
                + ",newUser:\""+column.getNewUser()+"\""
                + ",delUser:\""+column.getDelUser()+"\""
                + ",relUser:\""+column.getRelUser()+"\""
                + ",editUserContent:\""+column.getEditUserContent()+"\""
                + ",approvalUserContent:\""+column.getApprovalUserContent()+"\""
                + ",releaseUserContent:\""+column.getReleaseUserContent()+"\""
                + ",recevieUserContent:\""+column.getRecevieUserContent()+"\""
                + ",orderContent:\""+column.getOrderContent()+"\""
                + "}");
              sb.append("},");
              isHave = true;
            }
          }
        }
        if (isHave) {
          sb = sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("]");
      } else {
        sb.append("]");
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
    return sb.toString();
  }

  /**
   * 栏目是否存在子节点
   * 
   */
  public boolean isHaveChildColumn(Connection dbConn, int dbSeqId){
    
    String sql = " select 1 from cms_column where PARENT_ID ="+dbSeqId;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      if (rs.next()) {
        return true;
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    return false;
  }
  
  /**
   * 获取详情
   * 
   * @param conn
   * @param seqId
   * @return
   * @throws Exception
   */
  public StringBuffer getColumnDetailLogic(Connection conn, int seqId) throws Exception {
    try {
      T9ORM orm = new T9ORM();
      T9CmsColumn column = (T9CmsColumn) orm.loadObjSingle(conn, T9CmsColumn.class, seqId);
      StringBuffer data = T9FOM.toJson(column);
      return data;
    } catch (Exception ex) {
      throw ex;
    }
  }
  
  /**
   * CMS站点 修改
   * 
   */
  public void updateColumn(Connection dbConn, T9CmsColumn column) throws Exception{
    
    T9ORM orm = new T9ORM();
    T9CmsColumn columnOld = (T9CmsColumn) orm.loadObjSingle(dbConn, T9CmsColumn.class, column.getSeqId());
    orm.updateSingle(dbConn, column);
    
    String parent = getParentPath(dbConn, column);
    
    T9CmsStation station = (T9CmsStation) orm.loadObjSingle(dbConn, T9CmsStation.class, column.getStationId());
    File fileOld = new File(T9SysProps.getWebPath() + File.separator + station.getStationPath() + File.separator + parent + columnOld.getColumnPath());
    File fileNew = new File(T9SysProps.getWebPath() + File.separator + station.getStationPath() + File.separator + parent + column.getColumnPath());
    fileOld.renameTo(fileNew);
  }
  
  
  
  
  /**
   * 返回最大的SEQ_Id
   * 
   * @param dbConn
   * @param tableName
   * @return
   * @throws Exception
   */
  public int getMaSeqId(Connection dbConn, String tableName) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    int maxSeqId = 0;
    String sql = "select max(SEQ_ID) as SEQ_ID from " + tableName;
    try {
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
      if (rs.next()) {
        maxSeqId = rs.getInt("SEQ_ID");
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
    return maxSeqId;
  }
  
  /**
   * CMS下级栏目 通用列表
   * 
   * @param dbConn
   * @param request
   * @param person
   * @return
   * @throws Exception
   */
  public String getColumnList(Connection dbConn, Map request, T9Person person, int seqId, String flag) throws Exception {
    
    String sql = "";
    if("1".equals(flag)){
      sql = " SELECT c.SEQ_ID, c.COLUMN_NAME, c.CREATE_TIME, c.COLUMN_INDEX, c.VISIT_USER, c.EDIT_USER, c.NEW_USER, c.DEL_USER, c.REL_USER FROM cms_column c where c.STATION_ID =" + seqId + " and c.PARENT_ID = 0 ";
    }
    else{
      sql = " SELECT c.SEQ_ID, c.COLUMN_NAME, c.CREATE_TIME, c.COLUMN_INDEX, c.VISIT_USER, c.EDIT_USER, c.NEW_USER, c.DEL_USER, c.REL_USER FROM cms_column c where c.PARENT_ID =" + seqId;
    }
    sql = sql + " ORDER BY c.COLUMN_INDEX asc ";
    try {
      T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
      T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
      
      //判断列表访问权限
      T9PermissionsLogic pLogic = new T9PermissionsLogic();
      pageDataList = pLogic.visitControl(pageDataList, person);
      
      return pageDataList.toJson();
    } catch (Exception e) {
      throw e;
    }
  }
  
  /**
   * 删除栏目
   * 
   * @param dbConn
   * @param seqIdStr
   * @throws Exception
   */
  public int deleteColumn(Connection dbConn, String seqIdStr) throws Exception {
    int data = 0;
    T9ORM orm = new T9ORM();
    if (T9Utility.isNullorEmpty(seqIdStr)) {
      seqIdStr = "";
    }
    try {
      String seqIdArry[] = seqIdStr.split(",");
      if (!"".equals(seqIdArry) && seqIdArry.length > 0) {
        for (String seqId : seqIdArry) {
          T9CmsColumn column = (T9CmsColumn) orm.loadObjSingle(dbConn, T9CmsColumn.class, Integer.parseInt(seqId));
          
          //判断是否存在子栏目
          Map map = new HashMap();
          map.put("PARENT_ID", column.getSeqId());
          List<T9CmsColumn> list = orm.loadListSingle(dbConn, T9CmsColumn.class, map);
          if(list != null && list.size() > 0){
            data = 1;
            continue;
          }
          String parent = getParentPath(dbConn, column);
          T9CmsStation station = (T9CmsStation) orm.loadObjSingle(dbConn, T9CmsStation.class, column.getStationId());
          File file = new File(T9SysProps.getWebPath() + File.separator + station.getStationPath() + File.separator + parent + column.getColumnPath());
          if(file.list() != null){
            deleteFiles(file, file.list());
          }
          file.delete();
          
          orm.deleteSingle(dbConn, column);
        }
      }
    } catch (Exception e) {
      throw e;
    }
    return data;
  }
  
  /**
   * 删除该文件夹下所有文件
   * 
   * @param dbConn
   * @param seqIdStr
   * @throws Exception
   */
  public void deleteFiles(File file, String[] files){
    int len = files.length;
    for(int i = 0 ; i < len ; i ++){
      File fileDir = new File(file, files[i]);
//      System.out.println(fileDir.getAbsolutePath());
      if(fileDir.isFile() || fileDir.list() != null){
        fileDir.delete();
      }
      else{
        deleteFiles(fileDir, fileDir.list());
      }
    }
  }
  
  
  public int toReleaseStart(Connection conn, int seqId, boolean fullRelease) throws Exception{
    T9ORM orm = new T9ORM();
    
    try{
//      if(releaseTotal > -1){
//        return 2;
//      }
      
//      //此次发布的总文章数
//      String filters[] = {" STATION_ID =" + seqId + " and CONTENT_STATUS = 5 order by CONTENT_TOP desc, CONTENT_INDEX desc "};
//      List<T9CmsContent> contentListTotal = orm.loadListSingle(conn, T9CmsContent.class, filters);
//      nowReleaseTotal = 0;
//      releaseTotal = contentListTotal.size();
      
      //初始化全局变量
      T9CmsCommonLogic commonLogic = new T9CmsCommonLogic();
      T9CmsColumn column = (T9CmsColumn) orm.loadObjSingle(conn, T9CmsColumn.class, seqId);
      T9StationLogic.stationPublic = commonLogic.getStationInfo(conn, column.getStationId());
      
      int returnInt = toRelease(conn, seqId, fullRelease);
      
//      //全部发送完毕后，发布文章总数归-1（初始值）
//      releaseTotal = -1;
//      nowReleaseTotal = -1;
      return returnInt;
    }
    catch(Exception ex){
//      //全部发送完毕后，发布文章总数归-1（初始值）
//      releaseTotal = -1;
//      nowReleaseTotal = -1;
      throw ex;
    }
  }
  
  
  /**
   * 发布
   * 
   * @param conn
   * @param seqId
   * @return
   * @throws Exception
   */
  public int toRelease(Connection conn, int seqId, boolean fullRelease) throws Exception {
    T9ORM orm = new T9ORM();
    T9CmsColumn column = (T9CmsColumn) orm.loadObjSingle(conn, T9CmsColumn.class, seqId);
    T9CmsStation station = (T9CmsStation) orm.loadObjSingle(conn, T9CmsStation.class, column.getStationId());
    T9CmsTemplate template = (T9CmsTemplate) orm.loadObjSingle(conn, T9CmsTemplate.class, column.getTemplateIndexId());
    try {
      if(template != null){
        
        //获取出来的list 是有排序的
        String filters[] = {" COLUMN_ID =" + seqId + " and CONTENT_STATUS = 5 order by CONTENT_TOP desc, CONTENT_INDEX desc "};
        List<T9CmsContent> contentListTotal = orm.loadListSingle(conn, T9CmsContent.class, filters);
        
        //读取模板文件
        T9CmsCommonLogic commonLogic = new T9CmsCommonLogic();
        
        //获取栏目树形结构目录
        T9ColumnLogic logic = new T9ColumnLogic();
        String parent = logic.getParentPath(conn, column);
        
        int total = contentListTotal.size();
        //是否需要分页
        if(column.getPaging() == 1 && total > 0){
          int maxIndexPage = column.getMaxIndexPage();
          int pagingNumber = column.getPagingNumber(); 
          
          List<T9CmsContent> contentListTemp = new ArrayList<T9CmsContent>();
          for(int i = 0; i < maxIndexPage && total > 0; i++){
            contentListTemp.clear();
            for(int j = 0; j < pagingNumber && total > 0; j++){
              total--;
              contentListTemp.add(contentListTotal.get(i*pagingNumber + j));
            }
            
            //获取生成索引的文件名&扩展名，如个为空则用seqId代替
            String fileName = template.getTemplateFileName();
            if(i != 0){
              fileName = fileName + i;
            }
            
            //velocity拼map
            Map<String,Object> request = new HashMap<String,Object>();
            //文件输出路径的文件名
            request.put("fileName", fileName + "." + station.getExtendName());
            //当前位置
            request.put("location", commonLogic.getLocation(conn, column, "../") + " > " + column.getColumnName());
            //栏目及栏目url
            request.put("column", column);
            
            //获取站点所有信息
//            station = commonLogic.getStationInfo(conn, station.getSeqId());
            request.put("station", T9StationLogic.stationPublic);
            
            //当前栏目下的文章，并设置文章url
            //获取出来的list 是有排序的
            for(T9CmsContent content : contentListTemp){
              String contentFileName = content.getContentFileName();
              if(T9Utility.isNullorEmpty(content.getContentFileName())){
                contentFileName = content.getSeqId()+"";
              }
              String path = commonLogic.getColumnPath(conn, content.getColumnId());
              contentFileName = "/t9/" + T9StationLogic.stationPublic.getStationPath() + "/" + path + "/" + contentFileName + "." + T9StationLogic.stationPublic.getExtendName();
              content.setUrl(contentFileName);
            }
            request.put("contentList", contentListTemp);
            
            //当前站点下的最新文章
            //获取出来的list 是有排序的
            T9CmsColumn columnNew = new T9CmsColumn();
            columnNew.setColumnName("最新更新");
            String filtersContentNew[] = {" STATION_ID =" + T9StationLogic.stationPublic.getSeqId() + " and CONTENT_STATUS = 5 order by CONTENT_DATE desc "};
            List<T9CmsContent> contentListNew = orm.loadListSingle(conn, T9CmsContent.class, filtersContentNew);
            for(T9CmsContent content : contentListNew){
              String contentFileName = content.getContentFileName();
              if(T9Utility.isNullorEmpty(content.getContentFileName())){
                contentFileName = content.getSeqId()+"";
              }
              String path = commonLogic.getColumnPath(conn, content.getColumnId());
              contentFileName = "/t9/" + T9StationLogic.stationPublic.getStationPath() + "/" + path + "/" + contentFileName + "." + T9StationLogic.stationPublic.getExtendName();
              content.setUrl(contentFileName);
            }
            columnNew.setContentList(contentListNew);   
            request.put("columnNew", columnNew);
            
            //当前栏目模板
            request.put("template", template);
            
            //当前栏目模板
            request.put("contentSize", contentListTotal.size());
            request.put("pageNow", i);
            
            //文件输出路径、模板名、模板路径
            String pageOutPath = T9SysProps.getWebPath() + File.separator + T9StationLogic.stationPublic.getStationPath() + File.separator + parent + column.getColumnPath() ;
            String indexTemplateName = template.getAttachmentName();
            String pageTemlateUrl = T9SysProps.getAttachPath() + File.separator + attachmentFolder+ File.separator+T9StationLogic.stationPublic.getStationName();
            
            T9velocityUtil.velocity(request, pageOutPath, indexTemplateName, pageTemlateUrl);
          }
        }
        else{
        
          //获取生成索引的文件名&扩展名，如个为空则用seqId代替
          String fileName = template.getTemplateFileName();
          
          //velocity拼map
          Map<String,Object> request = new HashMap<String,Object>();
          //文件输出路径的文件名
          request.put("fileName", fileName + "." + T9StationLogic.stationPublic.getExtendName());
          //当前位置
          request.put("location", commonLogic.getLocation(conn, column, "../") + " > " + column.getColumnName());
          //栏目及栏目url
          request.put("column", column);
          
          //获取站点所有信息
//          station = commonLogic.getStationInfo(conn, station.getSeqId());
          request.put("station", T9StationLogic.stationPublic);
          
          //当前栏目下的文章，并设置文章url
          //获取出来的list 是有排序的
          String filtersContent[] = {" COLUMN_ID =" + seqId + " and CONTENT_STATUS = 5 order by CONTENT_TOP desc, CONTENT_INDEX desc "};
          List<T9CmsContent> contentList = orm.loadListSingle(conn, T9CmsContent.class, filtersContent);
          for(T9CmsContent content : contentList){
            String contentFileName = content.getContentFileName();
            if(T9Utility.isNullorEmpty(content.getContentFileName())){
              contentFileName = content.getSeqId()+"";
            }
            String path = commonLogic.getColumnPath(conn, content.getColumnId());
            contentFileName = "/t9/" + T9StationLogic.stationPublic.getStationPath() + "/" + path + "/" + contentFileName + "." + T9StationLogic.stationPublic.getExtendName();
            content.setUrl(contentFileName);
          }
          request.put("contentList", contentList);
          
          //当前站点下的最新文章
          //获取出来的list 是有排序的
          T9CmsColumn columnNew = new T9CmsColumn();
          columnNew.setColumnName("最新更新");
          String filtersContentNew[] = {" STATION_ID =" + T9StationLogic.stationPublic.getSeqId() + " and CONTENT_STATUS = 5 order by CONTENT_DATE desc "};
          List<T9CmsContent> contentListNew = orm.loadListSingle(conn, T9CmsContent.class, filtersContentNew);
          for(T9CmsContent content : contentListNew){
            String contentFileName = content.getContentFileName();
            if(T9Utility.isNullorEmpty(content.getContentFileName())){
              contentFileName = content.getSeqId()+"";
            }
            String path = commonLogic.getColumnPath(conn, content.getColumnId());
            contentFileName = "/t9/" + T9StationLogic.stationPublic.getStationPath() + "/" + path + "/" + contentFileName + "." + T9StationLogic.stationPublic.getExtendName();
            content.setUrl(contentFileName);
          }
          columnNew.setContentList(contentListNew);   
          request.put("columnNew", columnNew);
          
          //当前栏目模板
          request.put("template", template);
          
          //当前栏目模板
          request.put("contentSize", contentListTotal.size());
          request.put("pageNow", 0);
          
          //文件输出路径、模板名、模板路径
          String pageOutPath = T9SysProps.getWebPath() + File.separator + T9StationLogic.stationPublic.getStationPath() + File.separator + parent + column.getColumnPath() ;
          String indexTemplateName = template.getAttachmentName();
          String pageTemlateUrl = T9SysProps.getAttachPath() + File.separator + attachmentFolder+ File.separator+T9StationLogic.stationPublic.getStationName();

          
          T9velocityUtil.velocity(request, pageOutPath, indexTemplateName, pageTemlateUrl);
        }
          
        //发布栏目所在的站点
        T9StationLogic logicStation = new T9StationLogic();
        logicStation.toRelease(conn, column.getStationId(), false);
        
        //发布栏目所在的父栏目
        if(column.getParentId() > 0 && !fullRelease){
          toRelease(conn, column.getParentId(), false);
        }
        
        //发布栏目下的所有文章
        if(fullRelease){
          T9ContentLogic logicContent = new T9ContentLogic();
          for(T9CmsContent content : contentListTotal){
            logicContent.toRelease(conn, content.getSeqId(), false);
          }
        }
        
        if(T9AreaLogic.isHaveArea(conn, column.getStationId(), seqId)){
          T9AreaLogic areaLogic = new T9AreaLogic();
          List<T9CmsArea> areaList = areaLogic.getListArea(conn, column.getStationId(), seqId);
          for(T9CmsArea area : areaList){
            areaLogic.toRelease(conn, area.getSeqId(), seqId);
          }
        }
        
        return 1;
      }
    } catch (Exception ex) {
      throw ex;
    }
    return 0;
  }
  
  /**
   * 调序、排序
   * 
   * @param conn
   * @param seqId
   * @return
   * @throws Exception
   */
  public void toSort(Connection conn, int seqId, int toSeqId, int flag) throws Exception {
    if((flag == 1 || flag == 2) && toSeqId != 0){
      int toColumnIndex = getColumnIndex(conn,toSeqId);
      int columnIndex = getColumnIndex(conn,seqId);
      updateColumnIndex(conn, seqId,toColumnIndex);
      updateColumnIndex(conn, toSeqId,columnIndex);
    }
    if(flag == 3 || flag == 4){
      PreparedStatement ps = null;
      String sql1 = "";
      T9ORM orm = new T9ORM();
      T9CmsColumn column = (T9CmsColumn) orm.loadObjSingle(conn, T9CmsColumn.class, seqId);
      int columnIndex = getMaxOrMinColumnIndex(conn, column.getStationId(), column.getParentId(), flag);
      if(flag == 3){
        columnIndex = columnIndex + 1;
        sql1 = " update CMS_COLUMN set COLUMN_INDEX ="+columnIndex+" where SEQ_ID ="+seqId;
      }
      else if(flag == 4){
        columnIndex = columnIndex - 1;
        sql1 = " update CMS_COLUMN set COLUMN_INDEX ="+columnIndex+" where SEQ_ID ="+seqId;
      }
      try {
        ps = conn.prepareStatement(sql1);
        ps.executeUpdate();
      } catch (Exception ex) {
        throw ex;
      }
      finally{
        T9DBUtility.close(ps, null, null);
      }
    }
  }
  
  /**
   * 获取排序索引 contentIndex
   * 
   * @param conn
   * @param seqId
   * @return
   * @throws Exception
   */
  public int getColumnIndex(Connection conn,int seqId) throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null;
    String sql1 = " select COLUMN_INDEX from CMS_COLUMN where SEQ_ID ="+seqId;
    try {
      ps = conn.prepareStatement(sql1);
      rs = ps.executeQuery();
      if(rs.next()){
        return rs.getInt("COLUMN_INDEX");
      }
    } catch (Exception ex) {
      throw ex;
    }
    finally{
      T9DBUtility.close(ps, rs, null);
    }
    return 0;
  }
  
  /**
   * 更新排序索引
   * 
   * @param conn
   * @param seqId
   * @return
   * @throws Exception
   */
  public int updateColumnIndex(Connection conn,int seqId,int columnIndex) throws Exception{
    PreparedStatement ps = null;
    String sql1 = " update CMS_COLUMN set COLUMN_INDEX ="+columnIndex+" where SEQ_ID ="+seqId;
    try {
      ps = conn.prepareStatement(sql1);
      ps.executeUpdate();
    } catch (Exception ex) {
      throw ex;
    }
    finally{
      T9DBUtility.close(ps, null, null);
    }
    return 0;
  }
  
  /**
   * 更新排序索引
   * 
   * @param conn
   * @param seqId
   * @return
   * @throws Exception
   */
  public int getMaxOrMinColumnIndex(Connection conn,int stationId, int parentId, int flag) throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null;
    String sql = "";
    if(flag ==3){
      sql = " select MAX(COLUMN_INDEX) COLUMN_INDEX from CMS_COLUMN where STATION_ID ="+stationId+" and PARENT_ID ="+parentId;
    }
    else if(flag == 4){
      sql = " select MIN(COLUMN_INDEX) COLUMN_INDEX from CMS_COLUMN where STATION_ID ="+stationId+" and PARENT_ID ="+parentId;
    }
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){
        return rs.getInt("COLUMN_INDEX");
      }
    } catch (Exception ex) {
      throw ex;
    }
    finally{
      T9DBUtility.close(ps, rs, null);
    }
    return 0;
  }
  
  /**
   * 验证路径是否存在
   * 
   * @param conn
   * @param stationId
   * @param parentId
   * @param seqId
   * @param columnPath
   * @return
   * @throws Exception
   */
  public int checkPath(Connection conn, int stationId, int parentId, int seqId, String columnPath) throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null;
    String sql = " SELECT 1 FROM cms_column c where c.STATION_ID = " + stationId + " and c.PARENT_ID = " + parentId + " and COLUMN_PATH = '" + columnPath + "' and SEQ_ID != " + seqId;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){
        return 1;
      }
    } catch (Exception ex) {
      throw ex;
    }
    finally{
      T9DBUtility.close(ps, rs, null);
    }
    return 0;
  }
  
  public String getPath(Connection conn, int seqId) throws Exception{
    T9ORM orm = new T9ORM();
    T9CmsContent content = (T9CmsContent) orm.loadObjSingle(conn, T9CmsContent.class, seqId);
    T9CmsStation station = (T9CmsStation) orm.loadObjSingle(conn, T9CmsStation.class, content.getStationId());
    String contentFileName = content.getContentFileName();
    if(T9Utility.isNullorEmpty(content.getContentFileName())){
      contentFileName = content.getSeqId()+"";
    }
    T9CmsCommonLogic commonLogic = new T9CmsCommonLogic();
    String pathContent = commonLogic.getColumnPath(conn, content.getColumnId());
    
    return T9Utility.encodeSpecial("/t9/" + station.getStationPath() + "/" + pathContent + "/" + contentFileName + "." + station.getExtendName());
  }
  
  
  public String getColumnList(Connection conn,int stationId){
	  PreparedStatement ps=null;
	  ResultSet rs=null;
	  String sql="";
	  StringBuffer data=new StringBuffer("{");
	  try{
		  sql="select seq_id,column_name from cms_column where station_id="+stationId;
		  ps=conn.prepareStatement(sql);
		  rs=ps.executeQuery();
		  while(rs.next()){
			  data.append("\"columnId\":"+rs.getInt(1)+",");
			  data.append("\"columnName\":"+rs.getString(2));
		  }
		  if(data.length()>3){
			  data.deleteCharAt(data.length()-1);
		  }
		  data.append("}");
	  }catch(Exception ex){
		  ex.printStackTrace();
	  }
	  return data.toString();
  }
}
