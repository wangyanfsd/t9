<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<%
  String beginTime = request.getParameter("beginTime");
  if (beginTime == null) {
    beginTime = "";
  }
  String endTime = request.getParameter("endTime");
  if (endTime == null) {
    endTime = "";
  }
  String userId = request.getParameter("seqId");
  if (userId == null) {
    userId = "";
  }
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>加班登记</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="<%=cssPath%>/style.css">
<link rel="stylesheet" href = "<%=cssPath%>/cmp/Calendar.css">
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/Calendarfy.js"></script>
<script type="text/javascript" src="<%=contextPath %>/subsys/oa/overtimeStat/js/overtimeStatLogic.js"></script>
<script type="text/javascript">
var userId = <%=userId%>;
function doInit(){
  $("seqId").value = userId;
  getUserName(userId);
  $("beginDate").value = '<%=beginTime%>';
  $("endDate").value = '<%=endTime%>';
  var beginParameters = {
      inputId:'beginTime',
      property:{isHaveTime:false}
      ,bindToBtn:'beginTimeImg'
  };
  new Calendar(beginParameters);
  var endParameters = {
      inputId:'endTime',
      property:{isHaveTime:false}
      ,bindToBtn:'endTimeImg'
  };
  new Calendar(endParameters);
  normal(userId);
  festival(userId);
  week(userId);
  getNormalAdd(userId);
  getWeekAdd(userId);
  getFestivalAdd(userId);
}

function searchCommit(){
  var query = "";
  query = $("form1").serialize();
  location = "<%=contextPath%>/subsys/oa/overtimeStat/showOvertime.jsp?"+query;
}

</script>
</head>
 
<body topmargin="5" onload="doInit();">
 
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="Big"><img src="<%=imgPath%>/views/attendance.gif" WIDTH="22" HEIGHT="20" align="absmiddle"><span class="big3">&nbsp;个人加班时长统计(<span id="userName"></span>)</span><br>
    </td>
  </tr>
</table>
<br>
<form method="post" name="form1" id="form1">
<table width="400px" class="TableList" align="center" >
      <td nowrap class="TableContent"> 加班统计时间查询：</td>
      <td nowrap class="TableData" colspan=3>
        从 <input type="text" name="beginTime" id="beginTime" size="10" maxlength="10" class="BigInput" value="">
        <img id="beginTimeImg" src="<%=imgPath%>/calendar.gif" align="absMiddle" border="0" style="cursor:pointer">
        至 <input type="text" name="endTime" id="endTime" size="10" maxlength="10" class="BigInput" value="">
        <img id="endTimeImg" src="<%=imgPath%>/calendar.gif" align="absMiddle" border="0" style="cursor:pointer">
      </td>
  </tr>
  <tr class="TableFooter" >
      <td nowrap align="center" colspan=4>
      <input type="hidden" name="beginDate" id="beginDate" value="">
      <input type="hidden" name="endDate" id="endDate" value="">
      <input type="hidden" name="seqId" id="seqId" value="">
       <input type="button" class="BigButton" value="查询" onClick="searchCommit();">&nbsp;
      </td>
  </tr>
</table>
</form>
<br>
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="Big"><img src="/t9/core/styles/style3/img/views/attendance.gif" WIDTH="22" HEIGHT="20" align="absmiddle"><span class="big3"> 平时<span id="normalSpan"></span></span><br>
    </td>
  </tr>
</table>
<div id="normalDiv"></div>

<br>
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="Big"><img src="/t9/core/styles/style3/img/views/attendance.gif" WIDTH="22" HEIGHT="20" align="absmiddle"><span class="big3"> 周末<span id="weekSpan"></span></span><br>
    </td>
  </tr>
</table>
<div id="weekDiv"></div>
<br>

<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="Big"><img src="/t9/core/styles/style3/img/views/attendance.gif" WIDTH="22" HEIGHT="20" align="absmiddle"><span class="big3"> 节假日<span id="festivalSpan"></span></span><br>
    </td>
  </tr>
</table>

<div id="festivalDiv"></div>
</body>
</html>