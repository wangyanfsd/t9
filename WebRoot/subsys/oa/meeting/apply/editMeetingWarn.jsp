<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ include file="/core/inc/header.jsp" %>
<%
	String seqId = request.getParameter("seqId");
	if(T9Utility.isNullorEmpty(seqId)){
		seqId = "0";
	}

%>
<!DOCTYPE  html  PUBLIC  "-//W3C//DTD  XHTML  1.0  Transitional//EN"  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html  xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta  http-equiv="Content-Type"  content="text/html;  charset=utf-8">
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<title>操作结果</title>
</head>

<body topmargin="5">

<table class="MessageBox" align="center" width="320">
  <tr>
    <td class="msg info">
      <h4 class="title">提示</h4>
      <div class="content" style="font-size:12pt">会议申请被保存！</div>
    </td>
  </tr>
</table>
<br><center><input type="button" class="BigButton" value="返回" onclick="window.location.href='<%=contextPath %>/subsys/oa/meeting/apply/modifyMeeting.jsp?seqId=<%=seqId %>';"></center></body>
</html>