package t9.subsys.inforesouce.docmgr.data;

import java.util.Date;

import t9.core.util.T9Utility;

/**
 * 收 文管理
 * @author Administrator
 *
 */
public class T9DocReceive{
  private int seq_id;             //主键
  private String docNo;           //外办文号
  private Date resDate;           //已办文号
  private String fromUnits;       //来电单位
  private String oppDocNo;        //对方文号
  private String title;           //标题
  private int copies;             //份数
  private int confLevel;          //保密级别
  private String instruct;        //领导批示
  private String recipient;       //签收人
  private int docType;            //收件类型
  private int status;             //收文状态0未签，1已签
  private int sendStauts;         //发送类型0未发送，1已发送
  private String sponsor;         //承办单位
  private int userId;             //创建人的id
  private String fromUserName;
  private String toUserName;
  private String confLevelName;
  private String docTypeName;
  private String attachNames;     //附件名称
  private String attachIds;       //附件id
  private String runId;
  private String sponsorName;  //承办单位名称
  
  public String getSponsorName(){
    if(T9Utility.isNullorEmpty(sponsorName)){
      sponsorName = "";
    }
    return sponsorName;
  }
  public void setSponsorName(String sponsorName){
    this.sponsorName = sponsorName;
  }

  private T9DocNext next;
  
  public T9DocNext getNext(){
    return next;
  }
  public void setNext(T9DocNext next){
    this.next = next;
  }
  public String getRunId(){
    if(T9Utility.isNullorEmpty(runId)){
      runId = "";
    }
    return runId;
  }
  public void setRunId(String runId){
    this.runId = runId;
  }
  public String getAttachNames(){
    if(T9Utility.isNullorEmpty(attachNames) || "null".equalsIgnoreCase(attachNames) ||"null*".equalsIgnoreCase(attachNames)){
      attachNames = "";
    }
    return attachNames;
  }
  public void setAttachNames(String attachNames){
    this.attachNames = attachNames;
  }
  public String getAttachIds(){
    if(T9Utility.isNullorEmpty(attachIds) || "null".equalsIgnoreCase(attachIds)){
      attachIds = "";
    }
    return attachIds;
  }
  public void setAttachIds(String attachIds){
    this.attachIds = attachIds;
  }
  public String getConfLevelName(){
    int level = getConfLevel();
    String[] tt = this.parseDate(T9DocConst.SECRET_GRADE);
    confLevelName = tt[level-1];
    return confLevelName;
  }
  public void setConfLevelName(String confLevelName){
    this.confLevelName = confLevelName;
  }
  public String getDocTypeName(){
    int  docType = getDocType();
    String[] tt = this.parseDate(T9DocConst.DOC_TYPE);
    docTypeName = tt[docType - 1];
    return docTypeName;
  }
  public void setDocTypeName(String docTypeName){
    this.docTypeName = docTypeName;
  }
  public String getFromUserName(){
    return fromUserName;
  }
  public void setFromUserName(String fromUserName){
    this.fromUserName = fromUserName;
  }
  public String getToUserName(){
    if(T9Utility.isNullorEmpty(toUserName)){
      toUserName = "";
    }
    return toUserName;
  }
  public void setToUserName(String toUserName){
    this.toUserName = toUserName;
  }
  public int getUserId(){
    return userId;
  }
  public void setUserId(int userId){
    this.userId = userId;
  }
  public String getSponsor(){
    if(T9Utility.isNullorEmpty(sponsor)){
      sponsor = "";
    }
    return sponsor;
  }
  public void setSponsor(String sponsor){
    this.sponsor = sponsor;
  }
  public int getSendStauts(){
    return sendStauts;
  }
  public void setSendStauts(int sendStauts){
    this.sendStauts = sendStauts;
  }
  public int getStatus(){
    return status;
  }
  public void setStatus(int status){
    this.status = status;
  }
  public int getSeq_id(){
    return seq_id;
  }
  public void setSeq_id(int seqId){
    seq_id = seqId;
  }
  public String getDocNo(){
    return docNo;
  }
  public void setDocNo(String docNo){
    this.docNo = docNo;
  }
  public Date getResDate(){
    return resDate;
  }
  public void setResDate(Date resDate){
    this.resDate = resDate;
  }
  public String getFromUnits(){
    return fromUnits;
  }
  public void setFromUnits(String fromUnits){
    this.fromUnits = fromUnits;
  }
  public String getOppDocNo(){
    return oppDocNo;
  }
  public void setOppDocNo(String oppDocNo){
    this.oppDocNo = oppDocNo;
  }
  public String getTitle(){
    return title;
  }
  public void setTitle(String title){
    this.title = title;
  }
  public int getCopies(){
    return copies;
  }
  public void setCopies(int copies){
    this.copies = copies;
  }
  public int getConfLevel(){
    return confLevel;
  }
  public void setConfLevel(int confLevel){
    this.confLevel = confLevel;
  }
  public String getInstruct(){
    if(T9Utility.isNullorEmpty(instruct)){
      return "";
    }
    return instruct;
  }
  public void setInstruct(String instruct){
    this.instruct = instruct;
  }
  public String getRecipient(){
    return recipient;
  }
  public void setRecipient(String recipient){
    this.recipient = recipient;
  }
  public int getDocType(){
    return docType;
  }
  public void setDocType(int docType){
    this.docType = docType;
  }
  
  public String[] parseDate(String data){
    if(!T9Utility.isNullorEmpty(data)){
      String[] d = data.replace("[", "").replace("]", "").replaceAll("'", "").split(",");
      return d;
    }
    return null;
  }
  
  public String toJson(){
    StringBuffer sb = new StringBuffer();
    sb.append("{");
       sb.append("seqId:'").append(seq_id).append("',");
       sb.append("docNo:'").append(T9Utility.encodeSpecial(docNo)).append("',");
       sb.append("resDate:'").append(resDate).append("',");
       sb.append("fromUnits:'").append(T9Utility.encodeSpecial(fromUnits)).append("',");
       sb.append("oppDocNo:'").append(T9Utility.encodeSpecial(oppDocNo)).append("',");
       sb.append("title:'").append(T9Utility.encodeSpecial(title)).append("',");
       sb.append("copies:'").append(copies).append("',");
       sb.append("confLevel:'").append(confLevel).append("',");
       sb.append("instruct:'").append(T9Utility.encodeSpecial(getInstruct())).append("',");
       sb.append("recipient:'").append(T9Utility.encodeSpecial(recipient)).append("',");
       sb.append("docType:'").append(docType).append("',");
       sb.append("status:'").append(status).append("',");
       sb.append("sendStauts:'").append(sendStauts).append("',");
       sb.append("sponsor:'").append(T9Utility.encodeSpecial(sponsor)).append("',");
       sb.append("userId:'").append(userId).append("',");
       sb.append("fromUserName:'").append(T9Utility.encodeSpecial(getFromUserName())).append("',");
       sb.append("toUserName:'").append(T9Utility.encodeSpecial(getToUserName())).append("',");
       sb.append("confLevelName:'").append(T9Utility.encodeSpecial(getConfLevelName())).append("',");
       sb.append("docTypeName:'").append(T9Utility.encodeSpecial(getDocTypeName())).append("',");
       sb.append("attachNames:'").append(T9Utility.encodeSpecial(this.getAttachNames())).append("',");
       sb.append("attachIds:'").append(this.getAttachIds()).append("',");
       sb.append("runId:'").append(this.getRunId()).append("',");
       sb.append("nextDoc:").append((this.getNext().toJson())).append("");
    sb.append("}");
    
    return sb.toString();
  }
}
