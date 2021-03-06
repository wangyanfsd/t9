<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<%@ page import="java.text.*" %>
<%@ page import="java.util.*,t9.core.funcs.system.censorwords.data.T9CensorWords" %>
<%@ page import="t9.core.data.T9RequestDbConn, t9.core.global.T9BeanKeys" %>
<%
  String seqId = request.getParameter("seqId");
  if (seqId == null){
    seqId = "";
  }
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>分组管理</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="<%=cssPath %>/cmp/tab.css" type="text/css" />
<link rel="stylesheet" href = "<%=cssPath%>/cmp/Calendar.css">
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<link rel="stylesheet" href = "<%=cssPath%>/cmp/tree.css">
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/tab.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/orgselect.js" ></script>
<script type="text/javascript">
var seqId = "<%=seqId%>";

function doInit(){
  var url = "<%=contextPath%>/t9/core/funcs/address/act/T9AddressAct/getEditGroup.act?seqId="+seqId;
  var rtJson = getJsonRs(url);
  if(rtJson.rtState == "0"){
    for(var i = 0; i < rtJson.rtData.length; i++){
  	  var orderNo = rtJson.rtData[i].orderNo;
  	  var groupName = rtJson.rtData[i].groupName;
  	  document.getElementById("orderNo").value = orderNo;
  	  document.getElementById("groupName").value = groupName;
  	  document.getElementById("groupNameOld").value = groupName;
    }
  }else{
    alert(rtJson.rtMsrg); 
  }
}

function commit(){
  var orderNo = document.getElementById("orderNo");
  var groupName = document.getElementById("groupName");
  var reg = /^[0-9]*$/;
  if(!reg.test(orderNo.value)){
  	alert("排序号只能输入数字！");
  	orderNo.focus();
  	orderNo.select();
  	return false;
    }
  if(groupName.value == ""){ 
    alert("组名不能为空！");
    groupName.focus();
	return false;
  }
  reg = /['"]/g;
  if (groupName.value.match(reg)) {
    alert("组名不能有\"'\"和\"\"\"字符！");
    $('groupName').focus();
    return false;
  }
  var groupName = document.getElementById("groupName").value;
  var groupNameOld = document.getElementById("groupNameOld").value
  var orderNo = document.getElementById("orderNo").value;
  var url = "<%=contextPath%>/t9/core/funcs/address/act/T9AddressAct/updateGroup.act?seqId="+seqId+"&groupNameOld="+encodeURIComponent(groupNameOld);
  var rtJson = getJsonRs(url, "groupName="+encodeURIComponent(groupName)+"&orderNo="+orderNo);
  if(rtJson.rtState == "0"){
    location = "<%=contextPath %>/core/funcs/address/private/group/index.jsp";
  }else{
    location = "<%=contextPath %>/core/funcs/address/private/group/insertGroup.jsp";
  }
}

</script>
</head>
<body topmargin="5" onload="doInit()">
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="Big"><img src="<%=imgPath%>/notify_new.gif" align="absmiddle"><span class="big3">&nbsp;新建分组</span>
    </td>
  </tr>
</table>

<br>

<form name="form1" id="form1" method="post" enctype="multipart/form-data">
  <table class="TableBlock"  width="470" align="center">
    <tr>
      <td nowrap class="TableData">排序号：</td>
      <td class="TableData"><input type="text" name="orderNo" id="orderNo" size="8" class="BigInput"></td>
    </tr>
    <tr>
      <td nowrap class="TableData">分组名称：</td>
      <input type="hidden" name="groupNameOld" id="groupNameOld" value="">
      <td class="TableData"><input type="text" name="groupName" id="groupName" size="25" class="BigInput">&nbsp;&nbsp;<font style='color:red'>*</font></td>
    </tr>
    <tr>
      <td nowrap class="TableControl" colspan="2" align="center">
          <input type="button" value="提交" class="BigButton" title="提交数据" name="button1" OnClick="commit()">&nbsp;&nbsp;&nbsp;&nbsp;
          <input type="button" value="返回" class="BigButton" title="返回" name="button2" OnClick="location='<%=contextPath%>/core/funcs/address/private/group/index.jsp'">
      </td>
    </tr>
    </form>
</table>
</body>
</html>