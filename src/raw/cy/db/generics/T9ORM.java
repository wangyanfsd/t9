package raw.cy.db.generics;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import raw.cy.db.T9StringFormat;

/**
 * 2.0版本
 * 
 * @author TTlang
 * 
 */
public class T9ORM {
  /**
   * 新增加单个对象到数据库表
   * 
   * @param dbConn
   * @param obj
   * @throws Exception
   */
  public void save(Connection dbConn, Object obj) throws Exception {

    Map<String, Object> fieldInfo = null;
    T9ORMReflect or = new T9ORMReflect();
    fieldInfo = or.getFieldInfo(obj);
    //System.out.println("fieldInfo >> " + fieldInfo);
    T9ORMInsert.doInsert(dbConn, fieldInfo);
  }

  /**
   * 更新单个数据表
   * 
   * @param dbConn
   * @param obj
   * @throws Exception
   */
  public void update(Connection dbConn, Object obj) throws Exception {

    Map<String, Object> fieldInfo = null;
    T9ORMReflect or = new T9ORMReflect();
    fieldInfo = or.getFieldInfo(obj);
    T9ORMUpdate.doUpdate(dbConn, fieldInfo);
  }

  /**
   * 删除单个数据表数据
   * 
   * @param dbConn
   * @param obj
   * @throws Exception
   */

  public void delete(Connection dbConn, int objId) throws Exception {
  }

  /**
   * 删除单个数据表数据
   * 
   * @param dbConn
   * @param obj
   * @throws Exception
   */
  public void delete(Connection dbConn, Object obj) throws Exception {

    Map<String, Object> fieldInfo = null;
    T9ORMReflect or = new T9ORMReflect();
    fieldInfo = or.getFieldInfo(obj);
    T9ORMDelete.doDelete(dbConn, fieldInfo);
  }

  /**
   * 加载数据
   * 
   * @param dbConn
   * @param cls
   * @return
   * @throws Exception
   */
  public Object loadObj(Connection dbConn, Class cls, int objId)
      throws Exception {
    Map<String, Object> fieldInfo = null;
    T9ORMReflect or = new T9ORMReflect();
    fieldInfo = or.getFieldInfo(cls);
    Map<String, Object> filter = new HashMap<String, Object>();
    System.out.println("field : >>> " + fieldInfo);
    filter.put("SEQ_ID", objId);
    Object pojo = T9ORMSelect.doSelect(cls, dbConn, fieldInfo, filter);
    return pojo;
  }

  /**
   * 加载数据
   * 
   * @param dbConn
   * @param filters
   * @return
   * @throws Exception
   */
  /*
   * public Object loadObj(Connection dbConn, Class cls, String[] filters)
   * throws Exception { T9ORMReflect or = new T9ORMReflect(); PreparedStatement
   * pstmt = null; ResultSet rs = null; Object result = null;
   * 
   * List<String> fields = or.getTableProl(cls); StringBuffer sql = new
   * StringBuffer("select "); Iterator<String> iter = fields.iterator();
   * while(iter.hasNext()){ sql.append(iter.next()); if(iter.hasNext()){
   * sql.append(" , "); } } sql.append(" from ") .append(or.getTableName());
   * sql.append(" where "); for(int i = 0; i < filters.length; i++){
   * sql.append(filters[i]); if(i < filters.length - 1){ sql.append(" and "); }
   * } pstmt = dbConn.prepareStatement(sql.toString()); rs =
   * pstmt.executeQuery(); result = or.getObject(cls, rs); return result; }
   */
  /**
   * 加载数据
   * 
   * @param dbConndd
   * @param cls
   * @return
   * @throws Exception
   */

  public List loadList(Connection dbConn, Class cls, Map filters)
      throws Exception {
    Map<String, Object> fieldInfo = null;
    T9ORMReflect or = new T9ORMReflect();
    fieldInfo = or.getFieldInfo(cls);
    Map<String, Object> filterMap = new HashMap<String, Object>();
    Set<String> keys = filters.keySet();
    for (String key : keys) {
      Object value = filters.get(key);
      key = T9StringFormat.format(key);
      filterMap.put(key, value);
    }
    return T9ORMSelect.doSelectList(cls, dbConn, fieldInfo, filterMap);
  }

  /**
   * 加载数据
   * 
   * @param dbConn
   * @param cls
   * @return
   * @throws Exception
   */
  // public List loadList(Connection dbConn, Class cls, List filters) throws
  // Exception {
  // return null;
  // }

  /**
   * 加载数据
   * 
   * @param dbConn
   * @param cls
   * @return
   * @throws Exception
   */
  /*
   * public List loadList(Connection dbConn, Class cls, String[] filters) throws
   * Exception { T9ORMReflect or = new T9ORMReflect(); PreparedStatement pstmt =
   * null; ResultSet rs = null; Object obj = null; Iterator<String> fieldIter=
   * null; List list = null;
   * 
   * Map<String, String> fieldMap = or.getTableProm(cls); StringBuffer sql = new
   * StringBuffer("select "); fieldIter = fieldMap.values().iterator();
   * 
   * while(fieldIter.hasNext()){ sql.append(fieldIter.next());
   * if(fieldIter.hasNext()){ sql.append(" , "); } } sql.append(" from ")
   * .append(or.getTableName()) .append(" where "); //fieldIter =
   * filters.keySet().iterator(); for(int i = 0; i < filters.length; i++){
   * sql.append(filters[i]); if(i < filters.length - 1){ sql.append(" and "); }
   * } pstmt = dbConn.prepareStatement(sql.toString()); rs =
   * pstmt.executeQuery(); // System.out.println(sql.toString());
   * //System.out.println(rs); list = or.getObjectList(cls, rs); //
   * System.out.println(list.size()); if(rs!=null) rs.close(); //
   * dbConn.close(); return list; }
   */
}
