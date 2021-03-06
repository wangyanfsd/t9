<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<%
String seqId = request.getParameter("seqId");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>编辑员工复职信息</title>
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<link rel="stylesheet" href="<%=cssPath%>/cmp/Calendar.css" type="text/css" />
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/Calendarfy.js"></script>
<link rel="stylesheet" href="<%=cssPath%>/cmp/Calendar.css" type="text/css" />
<script type="text/Javascript" src="<%=contextPath%>/core/js/cmp/select.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/orgselect.js" ></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/fck/fckeditor/fckeditor.js"></script> 
<script type="text/javascript"  src="<%=contextPath%>/core/js/cmp/Menu.js"></script>
<script type="text/javascript"  src="<%=contextPath%>/core/js/cmp/attachMenu.js"></script>
<!-- 文件上传 -->
<link href="<%=cssPath%>/cmp/swfupload.css" rel="stylesheet"  type="text/css" />
<script type="text/javascript"  src="<%=contextPath%>/core/js/cmp/swfupload/swfupload.js"></script>
<script type="text/javascript"  src="<%=contextPath%>/core/js/cmp/swfupload/swfupload.queue.js"></script>
<script type="text/javascript"  src="<%=contextPath%>/core/js/cmp/swfupload/fileprogress.js"></script>
<script type="text/javascript"  src="<%=contextPath%>/core/js/cmp/swfupload/handlers.js"></script>
<script type="text/javascript"  src="<%=contextPath%>/core/js/cmp/attach.js"></script>
<script type="text/javascript"  src="<%=contextPath%>/subsys/oa/hr/manage/staffReinstatement/js/staffReinstatementLogic.js"></script>
<script type="text/javascript">
var fckContentStr = "";
//附件上传
var upload_limit=1,limit_type=limitUploadFiles;
var oa_upload_limit=limitUploadFiles;
var swfupload;

function doInit(){
  getSecretFlag("HR_STAFF_REINSTATEMENT1","reappointmentType");
  deptFunc1();
  setDate();
  var url = "<%=contextPath%>/t9/subsys/oa/hr/manage/reinstatement/act/T9HrStaffReinstatementAct/getReinstatementDetail.act?seqId=<%=seqId%>";
  var rtJson = getJsonRs(url);
  if(rtJson.rtState == "0"){
    var data = rtJson.rtData;
    bindJson2Cntrl(rtJson.rtData);
    if(data.reinstatementPerson){
      bindDesc([{cntrlId:"reinstatementPerson", dsDef:"PERSON,SEQ_ID,USER_NAME"}]);
    }
    if(data.applicationDate){
      $("applicationDate").value = data.applicationDate.substr(0,10);
    }
    if(data.reappointmentTimePlan){
      $("reappointmentTimePlan").value = data.reappointmentTimePlan.substr(0,10);
    }
    if(data.reappointmentTimeFact){
      $("reappointmentTimeFact").value = data.reappointmentTimeFact.substr(0,10);
    }
    if(data.firstSalaryTime){
      $("firstSalaryTime").value = data.firstSalaryTime.substr(0,10);
    }
    fckContentStr = data.reappointmentState;
    if(data.attachmentId){
      $("returnAttId").value = data.attachmentId;
      $("returnAttName").value = data.attachmentName;
      var selfdefMenu = {
          office:["downFile","dump","read","edit","deleteFile"], 
          img:["downFile","dump","play","deleteFile"],  
          music:["downFile","play","deleteFile"],  
          video:["downFile","play","deleteFile"], 
          others:["downFile","deleteFile"]
      }
      attachMenuSelfUtil("attr","hr",$('returnAttName').value ,$('returnAttId').value, '','','<%=seqId%>',selfdefMenu);
    }else{
      $('attr').innerHTML = "无附件";
    }
  }else{
    alert(rtJson.rtMsrg); 
  }
}

//获取全体部门列表
function deptFunc1(){
  var url = "/t9/t9/subsys/oa/rollmanage/act/T9RmsRollRoomAct/selectDeptToAttendance.act";
  var rtJson = getJsonRs(url);
  if(rtJson.rtState == "1"){
    alert(rtJson.rtMsrg); 
    return ;
  }
  var userId = rtJson.rtMsrg;
  var prcs = rtJson.rtData;
  var selects = document.getElementById("reappointmentDept");
  for(var i=0;i<prcs.length;i++){
    var prc = prcs[i];
    var option = document.createElement("option"); 
    option.value = prc.value; 
    option.innerHTML = prc.text; 
    selects.appendChild(option);
  }
  return userId;
}

function FCKeditor_OnComplete( editorInstance ) {
  editorInstance.SetData( fckContentStr ) ;
}

//日期
function setDate(){
  var date1Parameters = {
    inputId:'applicationDate',
    property:{isHaveTime:false}
    ,bindToBtn:'date1'
  };
  new Calendar(date1Parameters);

  var date2Parameters = {
    inputId:'reappointmentTimePlan',
    property:{isHaveTime:false}
    ,bindToBtn:'date2'
  };
  new Calendar(date2Parameters);

  var date3Parameters = {
    inputId:'reappointmentTimeFact',
    property:{isHaveTime:false}
    ,bindToBtn:'date3'
  };
  new Calendar(date3Parameters);

  var date4Parameters = {
    inputId:'firstSalaryTime',
    property:{isHaveTime:false}
    ,bindToBtn:'date4'
  };
  new Calendar(date4Parameters);
}

function doSubmit(){
  var oEditor = FCKeditorAPI.GetInstance('fileFolder');
  $("reappointmentState").value = oEditor.GetXHTML();
  if(checkForm()){
    $("form1").submit();
    //alert("通过。。。。");
  }
}

function checkForm(){
  if($("reinstatementPersonDesc").value == ""){
    alert("调动员工不能为空！");
    $("reinstatementPersonDesc").focus();
    return (false);
  }

  if($("reappointmentState").value == ""){
    alert("调动前部门不能为空！");
    return (false);
  }

  var applicationDate = $("applicationDate").value;
  if(applicationDate){
    if(!isValidDateStr(applicationDate)){
      alert("申请日期格式不对，应形如 2010-01-02");
      $("applicationDate").focus();
      $("applicationDate").select();
      return false;
    }
  }

  var reappointmentTimePlan = $("reappointmentTimePlan").value;
  if(reappointmentTimePlan){
    if(!isValidDateStr(reappointmentTimePlan)){
      alert("拟复职日期格式不对，应形如 2010-01-02");
      $("reappointmentTimePlan").focus();
      $("reappointmentTimePlan").select();
      return false;
    }
  }

  var reappointmentTimeFact = $("reappointmentTimeFact").value;
  if(reappointmentTimeFact){
    if(!isValidDateStr(reappointmentTimeFact)){
      alert("实际复职日期格式不对，应形如 2010-01-02");
      $("reappointmentTimeFact").focus();
      $("reappointmentTimeFact").select();
      return false;
    }
  }

  var firstSalaryTime = $("firstSalaryTime").value;
  if(firstSalaryTime){
    if(!isValidDateStr(firstSalaryTime)){
      alert("工资恢复日期格式不对，应形如 2010-01-02");
      $("firstSalaryTime").focus();
      $("firstSalaryTime").select();
      return false;
    }
  }
  
  if(firstSalaryTime && reappointmentTimeFact){
    if(firstSalaryTime > reappointmentTimeFact){
      alert(" 工资恢复日期不能小于实际复职日期！");
      $("reappointmentTimeFact").focus(); 
      $("reappointmentTimeFact").select(); 
      return false;
    }
  }
  if($("reappointmentDept").value == ""){
    alert("复职部门不能为空！");
    return (false);
  }
  return true;
}

//插入图片
function InsertImage(src){
  var oEditor = FCKeditorAPI.GetInstance('fileFolder') ;
  if ( oEditor.EditMode == FCK_EDITMODE_WYSIWYG )   {
    oEditor.InsertHtml( "<img src='"+ src  + "'/>") ;
  }
}

//删除附件
function deleteAttachBackHand(attachName,attachId,attrchIndex) { 
  var url = "<%=contextPath%>/t9/subsys/oa/hr/manage/reinstatement/act/T9HrStaffReinstatementAct/delFloatFile.act?delAttachId=" + attachId + "&seqId=<%=seqId%>";
  //var json = getJsonRs(url);
  var json=getJsonRs(url);
  if(json.rtState =='1'){
    alert(json.rtMsrg);
    return false;
  }else{
    prcsJson=json.rtData;
    var updateFlag=prcsJson.updateFlag;
    if(updateFlag == "1"){
      return true;
    }else{
      return false;
    }
  }
}

function getDeptId(){
  if(!$('reinstatementPerson').value) return;
  var requestUrl = contextPath + "/t9/subsys/oa/hr/manage/transfer/act/T9HrStaffTransferAct/getDeptId.act?transferPerson=" + $('reinstatementPerson').value; 
  var rtJson = getJsonRs(requestUrl); 
  if(rtJson.rtState == "1"){ 
    alert(rtJson.rtMsrg); 
    return ; 
  } 
  $('reappointmentDept').value = rtJson.rtData.deptId;
}
</script>
</head>
<body onload="doInit();">
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td><img src="<%=imgPath %>/notify_new.gif" align="middle"><span class="big3"> 编辑员工复职信息</span>&nbsp;&nbsp;
    </td>
  </tr>
</table>
<br>
<form enctype="multipart/form-data" action="<%=contextPath%>/t9/subsys/oa/hr/manage/reinstatement/act/T9HrStaffReinstatementAct/updateReinstatementInfo.act"  method="post" name="form1" id="form1" onsubmit="">
  <table class="TableBlock" width="80%" align="center">
    <tr>
      <td nowrap class="TableData">复职人员：<font color="red">*</font> </td>
      <td class="TableData">
        <input type="hidden" name="reinstatementPerson" id="reinstatementPerson" value="">
        <input type="text" name="reinstatementPersonDesc" id="reinstatementPersonDesc" class="BigStatic" readonly size="15">
        <a href="javascript:;" class="orgAdd" onClick="selectSingleUser(['reinstatementPerson', 'reinstatementPersonDesc'],null,null,1);getDeptId();">添加</a>
      </td>
      <td nowrap class="TableData">复职类型： </td>
      <td class="TableData" >
        <select name="reappointmentType" id="reappointmentType"  title="复职类型可在“人力资源设置”->“HRMS代码设置”模块设置。">
          <option value="">复职类型&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</option>
        </select>
      </td>
    </tr>
    <tr>
      <td nowrap class="TableData">申请日期：</td>
      <td class="TableData">
        <input type="text" name="applicationDate" id="applicationDate" size="11" maxlength="10"  class="BigInput" value="" readonly>
        <img id="date1" align="middle" src="<%=imgPath %>/calendar.gif" align="middle" border="0" style="cursor:pointer" >
      </td>
      <td nowrap class="TableData">担任职务：</td>
      <td class="TableData">
        <input type="text" name="nowPosition" id="nowPosition" class="BigInput" size="15">
      </td>
    </tr>
    <tr>
      <td nowrap class="TableData">拟复职日期：</td>
      <td class="TableData">
        <input type="text" name="reappointmentTimePlan" id="reappointmentTimePlan" size="11" maxlength="10"  class="BigInput" value="" readonly>
        <img id="date2" align="middle" src="<%=imgPath %>/calendar.gif" align="middle" border="0" style="cursor:pointer" >
      </td>
      <td nowrap class="TableData">实际复职日期：</td>
      <td class="TableData">
        <input type="text" name="reappointmentTimeFact" id="reappointmentTimeFact" size="11" maxlength="10"  class="BigInput" value="" readonly>
        <img id="date3" align="middle" src="<%=imgPath %>/calendar.gif" align="middle" border="0" style="cursor:pointer" >
      </td>
    </tr>
    <tr>
      <td nowrap class="TableData">工资恢复日期：</td>
      <td class="TableData">
        <input type="text" name="firstSalaryTime" id="firstSalaryTime" size="11" maxlength="10"  class="BigInput" value="" readonly>
        <img id="date4" align="middle" src="<%=imgPath %>/calendar.gif" align="middle" border="0" style="cursor:pointer" >
      </td>
      <td nowrap class="TableData">复职部门：<font color="red">*</font> </td>
      <td class="TableData">
        <select name="reappointmentDept" id="reappointmentDept" class="inputSelect">
          <option value="" >请选择</option>
        </select>               
      </td>
    </tr>
    <tr>
      <td nowrap class="TableData">复职手续办理： </td>
      <td class="TableData" colspan=3>
        <textarea name="materialsCondition" id="materialsCondition" cols="78" rows="2" class="BigInput" value=""></textarea>
      </td>
    </tr> 
    <tr>
      <td nowrap class="TableData">备注：</td>
      <td class="TableData" colspan=3>
        <textarea name="remark" id="remark" cols="78" rows="2" class="BigInput" value=""></textarea>
      </td>
    </tr> 
    <tr class="TableData" id="attachment2">
      <td nowrap>附件文档：</td>
      <td nowrap colspan=3>
      <input type = "hidden" id="returnAttId" name="returnAttId"></input>
      <input type = "hidden" id="returnAttName" name="returnAttName"></input>
      <span id="attr"></span> 
      </td>
   </tr>  
   <tr height="25" id="attachment1">
      <td nowrap class="TableData"><span id="ATTACH_LABEL">附件上传：</span></td>
       <td class="TableData" colspan="3">
      <script>ShowAddFile();</script>
      <script></script>
      <script></script> 
      <input type="hidden" name="ATTACHMENT_ID_OLD" id="ATTACHMENT_ID_OLD" value="">
      <input type="hidden" name="ATTACHMENT_NAME_OLD" id="ATTACHMENT_NAME_OLD" value="">
      <%--插入图片 --%>
      <input type="hidden" id="moduel" name="moduel" value="">
      <input type="hidden" id="imgattachmentId" name="imgattachmentId">
      <input type="hidden" id="imgattachmentName" name="imgattachmentName">
     </td>
   </tr>
    <tr id="EDITOR">
      <td class="TableData" colspan="4"> 复职说明：<font color="red">*</font>
        <div>
         <script language=JavaScript>    
          var sBasePath = contextPath+'/core/js/cmp/fck/fckeditor/';
          var oFCKeditor = new FCKeditor( 'fileFolder' ) ;
          oFCKeditor.BasePath = sBasePath ;
          oFCKeditor.Height = 200;
          var sSkinPath = sBasePath + 'editor/skins/office2003/';
          oFCKeditor.Config['SkinPath'] = sSkinPath ;
          oFCKeditor.Config['PreloadImages'] =
                          sSkinPath + 'images/toolbar.start.gif' + ';' +
                          sSkinPath + 'images/toolbar.end.gif' + ';' +
                          sSkinPath + 'images/toolbar.buttonbg.gif' + ';' +
                          sSkinPath + 'images/toolbar.buttonarrow.gif' ;
          //oFCKeditor.Config['FullPage'] = true ;
           oFCKeditor.ToolbarSet = "fileFolder";
          oFCKeditor.Value = '' ;
          oFCKeditor.Create();
         </script>
        </div>
      </td>
    </tr>
    <tr align="center" class="TableControl">
      <td colspan=4 nowrap>
        <input type="hidden" name="reappointmentState" id="reappointmentState" value="">
        <input type="hidden" name="seqId" id="seqId" value="<%=seqId %>">
        <input type="button" value="保存" onclick="doSubmit();" class="BigButton">
        <input type="button" value="返回" class="BigButton" onClick="location='<%=contextPath %>/subsys/oa/hr/manage/staffReinstatement/manage.jsp'">
      </td>
    </tr>
  </table>
</form>

</body>
</html>