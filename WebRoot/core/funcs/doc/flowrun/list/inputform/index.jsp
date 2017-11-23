<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<%@ include file="/core/funcs/doc/workflowUtility/workflowheader.jsp" %>
<%@ page import="t9.core.funcs.person.data.T9Person,t9.core.global.T9SysProps" %>
<%
String runId = request.getParameter("runId");
String flowId = request.getParameter("flowId");
String prcsId = request.getParameter("prcsId");
String flowPrcs = request.getParameter("flowPrcs");
String isNewStr = request.getParameter("isNew");
String isWriteLog = request.getParameter("isWriteLog");
if (isWriteLog == null || "".equals(isWriteLog)) {
  isWriteLog = "0";
} 
T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
int userId = loginUser.getSeqId();
if(isNewStr == null){
  isNewStr = "";
}
String sortId = request.getParameter("sortId");
if (sortId == null) {
  sortId = "";
}
String skin = request.getParameter("skin");
String skinJs = "messages";
if (skin != null && !"".equals(skin)) {
  skinJs = "messages_" + skin;
} else {
  skin = "";
}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<title></title>
<script type="text/Javascript" src="<%=contextPath%>/core/js/prototype.js" ></script>
   <script type="text/javascript" src="<%=contextPath %><%=moduleContextPath %>/lables/<%=skinJs %>.js"></script>
<script type="text/javascript" src="<%=contextPath %><%=moduleContextPath %>/workflowUtility/skin.js"></script>
<script type="text/javascript">
function doInit() {
  skinObjectToSpan(flowrun_list_inputform_index );
}
</script>
</head>
<frameset cols="*" rows="*,30" frameborder="no" border="0" framespacing="0" onload="doInit()">
  <frame src="main.jsp?runId=<%=runId %>&flowId=<%=flowId %>&prcsId=<%=prcsId %>&flowPrcs=<%=flowPrcs %>&isNew=<%=isNewStr %>&sortId=<%=sortId %>&skin=<%=skin %>&isWriteLog=<%=isWriteLog %>" name="main" scrolling="auto"  id="main" title="leftFrame" />
  <frame src="operate.jsp?runId=<%=runId %>&flowId=<%=flowId %>&prcsId=<%=prcsId %>&flowPrcs=<%=flowPrcs %>&isNew=<%=isNewStr %>&sortId=<%=sortId %>&skin=<%=skin %>" name="operate"  scrolling="no" id="operate" title="mainFrame"/>
</frameset>
<noframes><body>
</body></noframes>
</html>
