<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ include file="/core/inc/header.jsp" %>
<%@ page import="java.text.*" %>

<!DOCTYPE  html  PUBLIC  "-//W3C//DTD  XHTML  1.0  Transitional//EN"  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html  xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta  http-equiv="Content-Type"  content="text/html;  charset=utf-8">
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath %>/subsys/oa/hr/score/js/scoreFlowLogic.js"></script>
<script type="text/javascript">
function doInit(){
  //var seqId = ${param.groupId};
  //var flowTitle = getGroupName(seqId);
  //$("flowTitleSpan").innerHTML = flowTitle;
}

</script>
<title>考核数据录入</title>
</head>

<body topmargin="5" onload="doInit();">
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="Big"><img src="<%=imgPath%>/salary.gif" WIDTH="22" HEIGHT="20" align="absmiddle"><span class="big3">&nbsp;考核数据录入</span>
    </td>
  </tr>
</table>
<br>

<table class="MessageBox" align="center" width="300">
  <tr>
    <td class="msg info">
      <div class="content" style="font-size:12pt"><span id="flowTitleSpan">考核数据录入</span></div>
    </td>
  </tr>
</table>

<br><center><input type="button" class="BigButton" value="返回" onclick="parent.location.href='<%=contextPath %>/subsys/oa/hr/score/month/index.jsp';"></center></body>
</html>