package t9.core.funcs.doc.util;

import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import t9.core.funcs.doc.data.T9DocFlowFormItem;
import t9.core.funcs.doc.data.T9DocFlowFormReglex;
import t9.core.funcs.doc.data.T9DocFlowFormType;
import t9.core.funcs.doc.praser.T9FormPraser;
import t9.core.util.T9Out;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.rad.dbexputil.transplant.logic.core.glob.T9DBCont;
import t9.rad.dbexputil.transplant.logic.core.util.db.T9TransplantUtil;

public class T9FlowFormUtility {
  /**
   * $RESULT形如：array($ELEMENT_ARRAY, $PRINT_MODEL_SHORT, $PRINT_MODEL_NEW,
   * $ITEM_ID_MAX); 其中：$ELEMENT_ARRAY：表单缓存数组 $PRINT_MODEL_SHORT：短格式的表单HTML文本
   * $PRINT_MODEL_NEW：新的、补充调整过的、完整的表单HTML文本 $ITEM_ID_MAX：控件的最大编号
   */
 
  public  String htmlElement(String printModel) {
    HashMap hm = (HashMap) T9FormPraser.praserHTML2Dom(printModel);
    Map<String, Map> m1 = T9FormPraser.praserHTML2Arr(hm);
    String data = T9FormPraser.toJson(m1).toString();
    String printModelNew = T9FormPraser.toShortString(m1, printModel, T9DocFlowFormReglex.CONTENT);
   
    return printModelNew;
  }
  public static void main(String[] args) throws Exception {
    Connection dbConn = T9TransplantUtil.getDBConn(false, T9DBCont.newDb);
    try {
      T9FlowFormUtility fu =new T9FlowFormUtility();
      fu.cacheForm(dbConn);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      dbConn.close();
    }
    T9Out.print("ok");
  }
  public static void getCacheForm() {
    
  }
  public  void cacheForm(Connection conn) throws Exception {
    T9ORM orm = new T9ORM();
    List<T9DocFlowFormType> list = orm.loadListSingle(conn,T9DocFlowFormType.class , new HashMap());
    for (T9DocFlowFormType flowType :list) {
      this.cacheForm(flowType, conn);
    }
  }
  /**
   * 缓存表单
   * @param fft
   * @param conn
   * @throws Exception 
   */
  public void cacheForm(T9DocFlowFormType fft , Connection conn) throws Exception {
    T9ORM orm = new T9ORM();
    long date1 = System.currentTimeMillis();
    HashMap hm = (HashMap) T9FormPraser.praserHTML2Dom(fft.getPrintModel());
    long date2 = System.currentTimeMillis();
    long date3 = date2 - date1;
    Map<String, Map> m1 = T9FormPraser.praserHTML2Arr(hm);
    
    String delSql = "delete from "+ T9WorkFlowConst.FLOW_FORM_ITEM +" where FORM_ID = " + fft.getSeqId();
    Statement stm = null;
    try {
      stm = conn.createStatement();
      stm.executeUpdate(delSql);
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, null, null);
    }
    
    Set<String> keySet =  m1.keySet();
    for(String tmp : keySet){
      String sItemId = "";
      if (tmp != null) {
        if(tmp.startsWith("DATA_")){
          sItemId = tmp.substring("DATA_".length());
        }else if(tmp.startsWith("OTHER_")){
          sItemId = tmp.substring("OTHER_".length());
        }else{
          continue;//容错的一种方式;
        }
        if (!T9Utility.isInteger(sItemId)) {
          continue;
        }
        int itemId = Integer.parseInt(sItemId);
        Map mapTmp = m1.get(tmp);
        T9DocFlowFormItem ffi = new T9DocFlowFormItem( );
        ffi.setFormId(fft.getSeqId());
        ffi.setItemId(itemId);
        ffi.setContent((String)mapTmp.get("CONTENT"));
        String child =  (String)mapTmp.get("CHILD");
        if(child != null){
          child = child.replaceAll("\\\\\"", "");
        }
        ffi.setChild(child);
        
        String clazz = (String)mapTmp.get("CLASS");
        if (clazz != null && !"".equals(clazz)) {
          clazz = clazz.replaceAll("\\\\\"", "");
        }
        ffi.setClazz(clazz);
        String tag = (String)mapTmp.get("TAG");
        ffi.setTag(tag);
        String value = (String)mapTmp.get("VALUE");
        
        if (tag != null) {
          tag = tag.toUpperCase();
        }
        if("IMG".equals(tag) && value != null){
          value = value.replaceAll("\\\\\"", "");
        }
        ffi.setValue(value);
        String dataControl = (String)mapTmp.get("DATA_CONTROL");
        if (dataControl != null && !"".equals(dataControl)) {
          dataControl = dataControl.replaceAll("\\\\\"", "");
        }
        ffi.setDataControl(dataControl);
        
        String dateFormat = (String)mapTmp.get("DATE_FORMAT");
        if (dateFormat != null && !"".equals(dateFormat)) {
          dateFormat = dateFormat.replaceAll("\\\\\"", "");
        }
        ffi.setDateFormat(dateFormat);
        
        String defFormat = (String)mapTmp.get("DEF_FORMAT");
        if (defFormat != null && !"".equals(defFormat)) {
          defFormat = defFormat.replaceAll("\\\\\"", "");
        }
        if (!T9Utility.isNullorEmpty(defFormat) && T9Utility.isNullorEmpty(dateFormat)) {
          ffi.setDateFormat(defFormat);
        }
        String hidden = (String)mapTmp.get("HIDDEN");
        if (hidden != null && !"".equals(hidden)) {
          hidden = hidden.replaceAll("\\\\\"", "");
        }
        ffi.setHidden(hidden);
        String dataFld = (String)mapTmp.get("DATAFLD");
        if (dataFld != null && !"".equals(dataFld)) {
          dataFld = dataFld.replaceAll("\\\\\"", "");
        }
        ffi.setDatafld(dataFld);
        String dataSrc = (String)mapTmp.get("DATASRC");
        if (dataSrc != null && !"".equals(dataSrc)) {
          dataSrc = dataSrc.replaceAll("\\\\\"", "");
        }
        ffi.setDatasrc(dataSrc);
        String lvSizeStr = (String)mapTmp.get("LV_SIZE");
        if (lvSizeStr != null && !"".equals(lvSizeStr)) {
          lvSizeStr = lvSizeStr.replaceAll("\\\\\"", "");
        }
        
        String lvSumStr = (String)mapTmp.get("LV_SUM");
        if (lvSumStr != null && !"".equals(lvSumStr)) {
          lvSumStr = lvSumStr.replaceAll("\\\\\"", "");
        }
        ffi.setLvSum(lvSumStr);
        String dataType = (String)mapTmp.get("DATA_TYPE");
        if (dataType != null && !"".equals(dataType)) {
          dataType = dataType.replaceAll("\\\\\"", "");
        }
        ffi.setDataType(dataType);
        ffi.setLvSize(lvSizeStr);
        String lvCal = (String)mapTmp.get("LV_CAL");
        if (lvCal != null && !"".equals(lvCal)) {
          lvCal = lvCal.replaceAll("\\\\\"", "");
        }
        ffi.setLvCal(lvCal);
        
        String lvTitle = (String)mapTmp.get("LV_TITLE");
        if (lvTitle != null && !"".equals(lvTitle)) {
          lvTitle = lvTitle.replaceAll("\\\\\"", "");
        }
        ffi.setLvTitle(lvTitle);
        
        String type = (String)mapTmp.get("TYPE");
        if(("IMG".equals(tag) || "BUTTON".equals(tag) ) && type != null){
          type = type.replaceAll("\\\\\"", "");
        }
        ffi.setType(type);
        
        String radioField = (String)mapTmp.get("RADIO_FIELD");
        if(!T9Utility.isNullorEmpty(radioField)){
          radioField = radioField.replaceAll("\\\\\"", "");
        }
        ffi.setRadioField(radioField);
        
        String radioCheck = (String)mapTmp.get("RADIO_CHECK");
        if(!T9Utility.isNullorEmpty(radioCheck)){
          radioCheck = radioCheck.replaceAll("\\\\\"", "");
        }
        ffi.setRadioCheck(radioCheck);
        
        String metadata = (String)mapTmp.get("METADATA");
        if(!T9Utility.isNullorEmpty(metadata)){
          metadata = metadata.replaceAll("\\\\\"", "");
        }
        ffi.setMetadata(metadata);
        String metadataName = (String)mapTmp.get("METADATANAME");
        if(!T9Utility.isNullorEmpty(metadataName)){
          metadataName = metadataName.replaceAll("\\\\\"", "");
        }
        ffi.setMetadataName(metadataName);
        ffi.setQuick((String)mapTmp.get("QUICK"));
        ffi.setName((String)mapTmp.get("NAME"));
        ffi.setTitle((String)mapTmp.get("TITLE"));
        orm.saveSingle(conn, ffi);
      }
    }
  }
  /**
   * 缓存表单
   * @param formId
   * @param conn
   * @throws Exception
   */
  public void cacheForm(int formId , Connection conn) throws Exception{
    T9ORM orm = new T9ORM();
    T9DocFlowFormType fft = (T9DocFlowFormType) orm.loadObjSingle(conn, T9DocFlowFormType.class, formId);
    this.cacheForm(fft, conn);
  }
  public static void cacheWorkFlow() {

  } 
}
