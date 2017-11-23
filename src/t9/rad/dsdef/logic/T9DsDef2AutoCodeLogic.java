package t9.rad.dsdef.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import raw.cy.db.generics.T9ORM;
import t9.core.data.T9DsField;
import t9.core.util.T9Utility;
import t9.rad.dbexputil.transplant.logic.core.praser.T9XmlReader;
import t9.rad.dsdef.data.T9GridField;
import t9.rad.velocity.T9Code2DbUtil;
import t9.rad.velocity.T9velocityUtil;

public class T9DsDef2AutoCodeLogic {

  private static final String configUrl = "D:\\project\\t9\\templates";
  private static final String pojectSrcUrl = "D:\\project\\t9\\src";
  private static final String pojectClassPag = "t9";
  private static final String pojectWebUrl = "D:\\project\\t9\\webroot";

  /**
   * 
   * @return
   * @throws Exception
   */
  public String fileTemp2Json() throws Exception{
    String result = "{";
    File rootDir = new File(configUrl);
    result += "uri:\"" +  T9Utility.encodeSpecial(configUrl) + "\",templates:[";

    String templateJson = "";
    File[] templatesArray = rootDir.listFiles();
    for (int i = 0; i < templatesArray.length; i++) {
      File template = templatesArray[i];
      if(!template.isDirectory()){
        continue;
      }
      String name = template.getName();
      String path = template.getPath();
      File descFile = new File(path + "\\desc.txt");
      if(!descFile.exists()){
        continue;
      }
      if(!"".equals(templateJson)){
        templateJson += ",";
      }
      InputStream is = new FileInputStream(descFile);
      StringBuffer desc = new StringBuffer();
      byte[] buff = new byte[8192];
      int byteread = 0;
      while( (byteread = is.read(buff)) != -1){
        desc.append(new String(buff,0,byteread,"utf-8"));
      }
      String descstr = desc.toString();
      if(descstr != null){
        descstr =  descstr.replace("\r\n", "<br/>").replace("\n", "<br/>").replace("\r", "<br/>");
      }else{
        descstr = "";
      }
      templateJson += "{name:\"" + name + "\","
         + "desc:\"" + T9Utility.encodeSpecial(descstr) + "\"}";
    }
    result += templateJson;
    result += "]}";
    return result;
  }
  /**
   * 
   * @return
   * @throws Exception
   */
  public String xmlTemp2Json(String xmlName) throws Exception{
    //{params:[{name:,desc:,id:,type:}]}
    String result = "{";
    File inputXml = new File(configUrl + "\\" + xmlName + "\\" +  xmlName.trim() + ".xml");
    if(!inputXml.exists()){
      return "{params:[]}";
    }
    Document document = getDocument(inputXml);
    Element root = document.getRootElement();
    Element params = root.element("params");
    result += "params:[";

    String templateJson = "";
    List<Element> paramArray = params.elements("param");
    for (int i = 0; i < paramArray.size(); i++) {
      Element param = paramArray.get(i);
      String name = param.attributeValue("name");
      Element desc = param.element("desc");
      String id = param.attributeValue("id");
      String type = param.attributeValue("type");
      
      if(!"".equals(templateJson)){
        templateJson += ",";
      }

      String descstr = desc.getText();
      if(descstr != null){
        descstr =  descstr.replace("\r\n", "<br/>").replace("\n", "<br/>").replace("\r", "<br/>");
      }else{
        descstr = "";
      }
      templateJson += "{name:\"" + name + "\","
         + "id:\"" + id + "\","
         + "type:\"" + type + "\","
         + "desc:\"" + T9Utility.encodeSpecial(descstr) + "\"}";
    }
    result += templateJson;
    result += "]}";
    return result;
  }
  /**
   * 
   * @param xmlFile
   * @return
   * @throws Exception
   */
  public  Document getDocument(File xmlFile)throws Exception {
    T9XmlReader handler = new T9XmlReader();
    SAXReader reader = new SAXReader();
    reader.setDefaultHandler(handler);
    InputStream ins = new FileInputStream(xmlFile);
    return reader.read(ins);
  }
  
  public Map autoCodeByPojo(Connection conn, String tableNo,String pojectName,String tempPoj) throws Exception{
    
    String  dataFilePath = configUrl + "\\" + tempPoj + "\\src\\data";
    String pojectPath = pojectName.replace(".", "\\");
    String pojoPath = pojectSrcUrl + "\\" + pojectPath + "\\data" ;
    File file2 = new File(dataFilePath);
    if(!file2.exists()){
      return null;
    }
    String pojoPagckageName = pojectName + ".data";
    T9Code2DbUtil cd = new T9Code2DbUtil();
    Map result = cd.db2JavaCodefNo(conn, tableNo, pojoPagckageName);
    File dir = new File(dataFilePath);
    File[] vmlist = dir.listFiles();
    for (File file : vmlist) {
      if(file.isDirectory() || !file.getName().endsWith(".vm")){
        continue;
      }
      T9velocityUtil.velocity(result, pojoPath, file.getName(), dataFilePath);
    }
    
    return result;
  }
  
 public void autoCodeByAct(Connection conn, String tableNo,String pojectName,String tempPoj,String className,String tableName,List list) throws Exception{
     String classPath =  pojectName + ".data"; //pojo类路径
     Map request = new HashMap();
     String actOutPath =  pojectSrcUrl + "\\" + pojectName.replace(".", "\\") + "\\act" ;
     String actPackageName = pojectName + ".act";
     String actTemlateUrl = configUrl + "\\" + tempPoj + "\\src\\act";
     File file2 = new File(actTemlateUrl);
     if(!file2.exists()){
       return;
     }
     request.put("fileName", className + "Act.java");
     request.put("className", className);//
     request.put("tableName", tableName);//
     request.put("classPath", classPath);//
     request.put("list", list);//
     request.put("packageName", actPackageName);
     File dir = new File(actTemlateUrl);
     File[] vmlist = dir.listFiles();
     for (File file : vmlist) {
       if(file.isDirectory() || !file.getName().endsWith(".vm")){
         continue;
       }
       T9velocityUtil.velocity(request, actOutPath, file.getName(), actTemlateUrl);
     }
  }
 
 public void autoCodeByIndexPage(Connection conn, String tableNo,String pojectName,String tempPoj,String className,String tableName,String pojectUrl,String pojectUrl2) throws Exception{
   String pageOutPath = pojectWebUrl + "\\" + pojectUrl;
   String indexTemplateName = "index.vm";
   String pageTemlateUrl = configUrl + "\\" + tempPoj + "\\page";;
   File file2 = new File(pageTemlateUrl);
   if(!file2.exists()){
     return;
   }
   Map request = new HashMap();
   request.put("url", pojectUrl2);
   request.put("title", className);
   request.put("fileName", "index.jsp");
   T9velocityUtil.velocity(request, pageOutPath, indexTemplateName, pageTemlateUrl);
}
 /**
  * 
  * @param conn
  * @param tableNo
  * @return
 * @throws Exception 
  */
 public List<T9GridField> getGridFields(Connection conn, String tableNo) throws Exception{
   T9ORM orm = new T9ORM();
   ArrayList<T9GridField> gridFields = new ArrayList<T9GridField>();
   Map filters = new HashMap();
   filters.put("tableNo", tableNo);
   List<T9DsField> dsFields = orm.loadList(conn, T9DsField.class, filters); 
   for (int i = 0; i < dsFields.size(); i++) {
     if("seqID".equalsIgnoreCase(dsFields.get(i).getPropName())){
       continue;
     }
     String type = "data";
     String name = dsFields.get(i).getFieldName();
     String dataType = "String";
     String text = dsFields.get(i).getPropName();
     T9GridField gridField = new T9GridField(type, dsFields.get(i).getPropName(), dataType, text,name);
     gridFields.add(gridField);
  }
   return gridFields;
 }
  /**
   * 
   * @param conn
   * @param tableNo
   * @param req
   * @throws Exception
   */
  public  void autoCode(Connection conn, String tableNo, String pojectName,String tempPoj)
  throws Exception{
    //POJO类
    List list = getGridFields(conn, tableNo);
    if(!"".equals(pojectClassPag)){
      pojectName = pojectClassPag + "." + pojectName;
    }
    String pojectUrl = pojectName.replace(".", "\\") ;
    String pagepojectUrl = "/" + pojectName.substring(pojectName.indexOf(".") + 1).replace(".", "/") ;
    Map result = autoCodeByPojo(conn, tableNo, pojectName, tempPoj);
    String className = (String) result.get("className"); //pojo类名称
    String tableName = className.substring(2, 3).toLowerCase() + className.substring(3);
 
    //ACT层
    autoCodeByAct(conn, tableNo, pojectName, tempPoj, className, tableName,list);
    
    //增删改页面index
    autoCodeByIndexPage(conn, tableNo, pojectName, tempPoj, className, tableName, pojectUrl,pagepojectUrl);
    
    
    String inputActUrl = "/" +  (pojectName + ".act").replace(".", "/")+ "/" + className + "Act";
    String pageOutPath = pojectWebUrl + "\\" + pojectUrl;
    String pageTemlateUrl = configUrl + "\\" + tempPoj + "\\page";

    File file = new File(pageTemlateUrl);
    if(file.exists()){
      Map request = new HashMap();
      request.put("list", list);
      request.put("fileName", "list.jsp");
      request.put("url", pagepojectUrl);
      request.put("acturl",inputActUrl);
      String listTemplateName =  "list.vm";
      T9velocityUtil.velocity(request, pageOutPath, listTemplateName, pageTemlateUrl);
      
      //input
      String inputTemplateName = "input.vm";
      //System.out.println("acturl : " + inputActUrl);
      request = new HashMap();
      request.put("fields", list);
      request.put("classPath", pojectName + ".data." +  className);
      request.put("fileName", "input.jsp");
      request.put("acturl",inputActUrl);
      request.put("url", pagepojectUrl);//
      T9velocityUtil.velocity(request, pageOutPath, inputTemplateName, pageTemlateUrl);
    }
 }
}
