<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<%
  String reinstatementPerson = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("reinstatementPerson")));
  String reappointmentType = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("reappointmentType")));
  String applicationDate1 = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("applicationDate1")));
  String applicationDate2 = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("applicationDate2")));
  String reappointmentTimeFact1 = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("reappointmentTimeFact1")));
  String reappointmentTimeFact2 = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("reappointmentTimeFact2")));
  String reappointmentState = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("reappointmentState")));
  String materialsCondition = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("materialsCondition")));
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>员工复职查询 </title>
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<link rel="stylesheet" href="<%=cssPath%>/page.css">
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/page.js"></script>
<script type="text/javascript" src="<%=contextPath %>/subsys/oa/hr/manage/staffReinstatement/js/util.js"></script>
<script type="text/javascript" src="<%=contextPath %>/subsys/oa/hr/manage/staffReinstatement/js/staffReinstatementLogic.js"></script>
<script> 
var pageMgr = null;
function doInit(){
 	var param = "";
  param = "reinstatementPerson=" + encodeURIComponent("<%=reinstatementPerson%>");
  param += "&reappointmentType=" + encodeURIComponent("<%=reappointmentType%>");
  param += "&applicationDate1=" + encodeURIComponent("<%=applicationDate1%>");
  param += "&applicationDate2=" + encodeURIComponent("<%=applicationDate2%>");
  param += "&reappointmentTimeFact1=" + encodeURIComponent("<%=reappointmentTimeFact1%>");
  param += "&reappointmentTimeFact2=" + encodeURIComponent("<%=reappointmentTimeFact2%>");
  param += "&reappointmentState=" + encodeURIComponent("<%=reappointmentState%>");
  param += "&materialsCondition=" + encodeURIComponent("<%=materialsCondition%>");
  var url = "<%=contextPath%>/t9/subsys/oa/hr/manage/reinstatement/act/T9HrStaffReinstatementAct/queryReinstatementListJson.act?" + param;
  var cfgs = {
    dataAction: url,
    container: "listContainer",
    sortIndex: 1,
    sortDirect: "desc",
    colums: [
        {type:"selfdef", text:"选择", width: '5%', render:checkBoxRender},
        {type:"hidden", name:"seqId", text:"顺序号", dataType:"int"},
        {type:"data", name:"reinstatementPerson",  width: '20%', text:"复职人员" ,align: 'center'},
        {type:"data", name:"nowPosition",  width: '20%', text:"担任职务" ,align: 'center'},
        {type:"data", name:"reappointmentType",  width: '20%', text:"复职类型" ,align: 'center' ,render:reinstatementItemFunc},
        {type:"data", name:"reappointmentTimePlan",  width: '10%', text:"拟复职日期" ,align: 'center' ,render:splitDateFunc},
        {type:"data", name:"firstSalaryTime",  width: '10%', text:"工资恢复日期" ,align: 'center' ,render:splitDateFunc},
        {type:"selfdef", text:"操作", width: '15%',render:opts}]
   };
  pageMgr = new T9JsPage(cfgs);
  pageMgr.show();
  var total = pageMgr.pageInfo.totalRecord;
  if(total){
    showCntrl('listContainer');
    var mrs = " 共 " + total + " 条记录 ！";
    showCntrl('delOpt');
    showCntrl('backDiv');
  }else{
    WarningMsrg('无符合条件的员工复职信息', 'msrg');
  }
}

</script>
</head>
<body topmargin="5" onload="doInit()">
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
 <tr>
   <td class="Big"><img src="<%=imgPath%>/infofind.gif" align="absMiddle"><span class="big3">&nbsp;员工复职信息查询结果 </span>
   </td>
 </tr>
</table>
<br>
<div id="listContainer" style="display:none;width:100;">
</div>
<div id="delOpt" style="display:none">
<table class="TableList" width="100%">
<tr class="TableControl">
      <td colspan="19">
         <input type="checkbox" name="checkAlls" id="checkAlls" onClick="checkAll(this);"><label for="checkAlls">全选</label> &nbsp;
         <a href="javascript:deleteAll();" title="删除所选记录"><img src="<%=imgPath%>/delete.gif" align="absMiddle">删除所选记录</a>&nbsp;
      </td>
 </tr>
</table>
</div>
<div id="backDiv" style="display:none" align="center">
<br>
  <input type="button" value="返回" class="BigButton" onclick="window.location.href='<%=contextPath %>/subsys/oa/hr/manage/staffReinstatement/query.jsp';">&nbsp;&nbsp;
</div>

<div id="msrg">
</div>
</body>
</html>