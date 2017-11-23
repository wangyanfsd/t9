<%@ page language="java" import="java.util.*, t9.core.funcs.person.data.T9Person" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<%
  //判断是否为管理员
  //判断是否自己是审批人员

  T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
  int userId = user.getSeqId();
  String userName = user.getUserName();
  String userPriv = user.getUserPriv();
  boolean IsManage= false;
  if(userPriv.equals("1")){
    IsManage = true;
  }
  String curDateStr = T9Utility.getCurDateTimeStr();
  String curDateTime = curDateStr.substring(11,16);
  int time = Integer.parseInt(curDateStr.substring(11,13))+2;
  if(time<24){
    if(time<10){
      curDateTime = "0" + time + curDateStr.substring(13,16);
    }else{
      curDateTime = time + curDateStr.substring(13,16);
    } 
  }
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>新建加班登记</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="<%=cssPath%>/style.css">
<link rel="stylesheet" href="<%=cssPath%>/cmp/Calendar.css">
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/tab.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/Calendarfy.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/orgselect.js" ></script>
<script type="text/javascript" src="<%=contextPath %>/custom/attendance/personal/duty/js/dutyLogic.js"></script>
<script type="text/javascript">

//比较时间段的
function doOnload(){
  var userId = '<%=userId%>';
  var userName = '<%=userName%>';
  var requestURL; 
  var prcsJson; 
  requestURL = "<%=contextPath%>/t9/core/funcs/system/attendance/act/T9AttendManagerAct/selectManagerPerson.act"; 
  var json = getJsonRs(requestURL); 
  if(json.rtState == '1'){ 
    alert(json.rtMsrg); 
    return ; 
  }
  prcsJson = json.rtData;
  var selects = document.getElementById("leaderId"); 
  for(var i = 0; i< prcsJson.length; i++){
    if(userId!=prcsJson[i].seqId){
      var option = document.createElement("option"); 
      option.value = prcsJson[i].seqId; 
      option.innerHTML = prcsJson[i].userName; 
      selects.appendChild(option); 
    }
  }
  var parameters = {
      inputId:'dutyTime',
      property:{isHaveTime:false}
      ,bindToBtn:'date1'
      ,callbackFun:attendDuty
  };
  new Calendar(parameters);
  getSysRemind();//短信
  
//手机
  getMoblieSmsRemind("moblieSmsRemindDiv","moblieSmsRemind");
}

//判断是否要显示短信提醒
function getSysRemind(){
  var requestUrl = "<%=contextPath%>/t9/core/funcs/calendar/act/T9CalendarAct/getSysParaRemind.act?type=6";
  var rtJson = getJsonRs(requestUrl);
  if(rtJson.rtState == "1"){
    alert(rtJson.rtMsrg); 
    return ;
  }
  var prc = rtJson.rtData;
  var allowRemind = prc.allowRemind;
  var defaultRemind = prc.defaultRemind;
  if(allowRemind=='2'){
    $("smsRemindDiv").style.display = 'none';
  }else{
    if(defaultRemind=='1'){
      $("smsRemind").checked = true;
    }
  }
  //return prc;
}
/** 
*js代码 
*是否显示手机短信提醒 
*/ 
function getMoblieSmsRemind(remidDiv,remind){ 
  var requestUrl = contextPath + "/t9/core/funcs/mobilesms/act/T9MobileSelectAct/isShowSmsRmind.act?type=6"; 
  var rtJson = getJsonRs(requestUrl); 
  if(rtJson.rtState == "1"){ 
    alert(rtJson.rtMsrg); 
    return ; 
  } 
  var prc = rtJson.rtData; 
  var moblieRemindFlag = prc.moblieRemindFlag;//手机默认选中 
  if(moblieRemindFlag == '2'){ 
    $(remidDiv).style.display = ''; 
    $(remind).checked = true; 
  }else if(moblieRemindFlag == '1'){ 
    $(remidDiv).style.display = ''; 
    $(remind).checked = false; 
  }else{ 
    $(remidDiv).style.display = 'none'; 
  } 
}
</script>
</head>
<body class="" topmargin="5" onload="doOnload();">
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="Big"><img src="<%=imgPath%>/views/attendance.gif" WIDTH="22" HEIGHT="20" align="absmiddle"><span class="big3">  &nbsp;新建值班登记</span><br>
    </td>
  </tr>
</table>
<br>
<form action="<%=contextPath%>/t9/custom/attendance/act/T9DutyAct/addDuty.act"  method="post" id = "form1" name="form1" class="big1" onsubmit="return checkForm();">
  <table class="TableBlock" width="90%" align="center">
    <tr>
      <td nowrap class="TableData"> 值班原因：<font style='color:red'>*</font></td>
      <td class="TableData">
      	 <textarea id = "dutyDesc" name="dutyDesc" class="BigInput" cols="60" rows="3"></textarea>
      </td>
    </tr>
    <tr>
      <td nowrap class="TableData"> 值班时间：<font style='color:red'>*</font></td>
      <td class="TableData">
          <input type="text" id = "dutyTime" name="dutyTime" size="10" maxlength="10" value="<%=curDateStr.substring(0,10) %>">
         <img id="date1" src="<%=imgPath%>/calendar.gif" align="absMiddle" border="0">
         
         <input type="text" id = "beginDate" name="beginDate" size="6" maxlength="10" value="" onclick="showTimeBegin();">&nbsp;至
         <input type="text" id = "endDate" name="endDate" size="6" maxlength="10" value="" onclick="showTimeEnd();">
         <span id="attendDuty" style="display:none;"></span>
      </td>
    </tr>
    <tr>
      <td nowrap class="TableData"> 审批人：</td>
      <td class="TableData">
        <select name="leaderId" id = "leaderId"  class="">
        </select>
      </td>
    </tr>
    
    <tr>
      <td nowrap class="TableData"> 短信提醒：</td>
      <td class="TableData">   <span id="smsRemindDiv"><input type="checkbox" id="smsRemind" name="smsRemind" ><label for="smsRemind">使用内部短信提醒</label>&nbsp;&nbsp;</span>    <span id="moblieSmsRemindDiv"><input type="checkbox" id="moblieSmsRemind" name="moblieSmsRemind" ><label for="moblieSmsRemind">使用手机短信提醒</label>&nbsp;&nbsp;</span>  </td>
    </tr>
    <tr align="center" class="TableControl">
      <td colspan="2" nowrap>
      <input type="hidden" name="hour" id="hour" value="">
      <input type="hidden" name="dutyType" id="dutyType" value="">
      <input type="hidden" name="festivalAdd" id="festivalAdd" value="">
      <input type="hidden" name="weekAdd" id="weekAdd" value="">
      <input type="hidden" name="dutyMoney" id="dutyMoney" value="">
      <input type="hidden" name="normalAdd" id="normalAdd" value="">
        <input type="submit" value="申请值班" class="BigButton" title="新建申请值班">&nbsp;&nbsp;
        <input type="button" value="返回上页" class="BigButton" onclick="history.go(-1);">&nbsp;&nbsp;
      </td>
    </tr>
  </table>
</form>

</body>
</html>