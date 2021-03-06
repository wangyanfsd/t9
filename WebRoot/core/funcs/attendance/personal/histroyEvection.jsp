<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>出差历史记录</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="<%=cssPath%>/style.css">
<link rel="stylesheet" href="<%=cssPath%>/cmp/Calendar.css">
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/tab.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/Calendarfy.js"></script>
<script type="text/javascript">
function returnBefore(){
  window.location.href = "<%=contextPath%>/core/funcs/attendance/personal/evection.jsp";
}
function updateEvection(seqId){
  window.location.href = "<%=contextPath%>/core/funcs/attendance/personal/editEvection.jsp?seqId=" + seqId;
}
function doOnload(){
  var requestURL = "<%=contextPath%>/t9/core/funcs/attendance/personal/act/T9AttendEvectionAct/selectHistroyEvection.act";
  var rtJson = getJsonRs(requestURL);
  if(rtJson.rtState == "1"){
    alert(rtJson.rtMsrg); 
    return ;
    }
  var prcsJson = rtJson.rtData;
  if(prcsJson.length>0){
    var table = new Element('table',{"class":"TableList" ,"width":"95%"}).update("<tbody id = 'tboday'><tr class='TableHeader'><td nowrap align='center'>出差地区</td>"
      + "<td nowrap align='center'>出差原因</td>"
      + "<td nowrap align='center'>开始时间</td>"
      + "<td nowrap align='center'>结束时间</td>"
      + "<td nowrap align='center'>审批人员</td>"
      + "<td nowrap align='center'>状态</td>"
      + "<td nowrap align='center'>操作</td></tr></tbody>");
    $('listDiv').appendChild(table); 
    for(var i =0;i< prcsJson.length;i++){
      var tr = new Element('tr',{"class":"TableData"});
      $('tboday').appendChild(tr);
      var prcs = prcsJson[i];
      var seqId = prcs.seqId;
      var userId = prcs.userId;
      var leaderId = prcs.leaderId;
      var leaderName = prcs.leaderName;
      var evectionDate1 = prcs.evectionDate1;
      var evectionDate2 = prcs.evectionDate2;
      var evectionDest = prcs.evectionDest;
      var allow =prcs.allow;
      var status = prcs.status;
      var reason = prcs.reason;


      var isHookRun = prcs.isHookRun;
      var flowId = prcs.flowId;

      var opts= "<a href='#' title='提醒：修改提交后，需要重新审批' onclick='updateEvection(" + seqId + ")'>修改</a>&nbsp;&nbsp; "
                 + "<a href='#' onclick = 'deteleEvection(" + seqId + ")'>删除</a>";
      if(isHookRun!=0){
        leaderName = "<a href='javascript:void(0)' onclick='formView("+isHookRun+" , "+ flowId +")'>查看流程</a>";
        opts="";
            }

      
      tr.update("<td align='center'>" + evectionDest + "</td>"
        + "<td align='center'>" + reason + "</td>"
        + "<td nowrap align='center'>" + evectionDate1.substr(0,10) + "</td>"
        + "<td nowrap align='center'>" + evectionDate2.substr(0,10) + "</td>"
        + "<td nowrap align='center'>" + leaderName + "</td>"
        + "<td nowrap align='center'>已归来 </td>"
        + "<td nowrap align='center'>" 
        + "<a href='#' title='提醒：修改提交后，需要重新审批' onclick='updateEvection(" + seqId + ")'>修改</a> "
        + "</td>"
       );
    }
  }else{
    var table = new Element('table',{ "class":"MessageBox" ,"align":"center" ,"width":"240"}).update("<tr>"
        + "<td class='msg info'>"
        + "<div class='content' style='font-size:12pt'>无历史记录</td></tr>");
      $('nullTable').appendChild(table);
  }   
}
</script>
</head>
<body class="" topmargin="5" onload="doOnload();">
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="Big"><img src="<%=imgPath%>/views/attendance.gif" WIDTH="22" HEIGHT="20" align="absmiddle"><span class="big3">  &nbsp;出差登记</span><br>
    </td>
  </tr>
</table>
<br>
<div align="center" id="listDiv">
</div>
<div align="center" id="nullTable">
</div><br>
<div align="center">
  <input type="button"  value="返回上页" class="BigButton" onClick="returnBefore();">
</div>
</body>
</html>