<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp"%>
<%
String seqId = request.getParameter("seqId");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>栏目列表</title>
<link rel="stylesheet" href="<%=cssPath%>/cmp/tab.css" type="text/css" />
<link rel="stylesheet" href="<%=cssPath%>/style.css">
<script type="text/Javascript" src="<%=contextPath%>/core/js/datastructs.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/sys.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/prototype.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/smartclient.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/cmp/select.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/tab.js"></script>
<script type="text/javascript">
var jso = [
           {title:"编辑权限", contentUrl:"<%=contextPath%>/cms/permissions/perContent/setContent.jsp?seqId=<%=seqId %>&userType=EDIT_USER_CONTENT", imgUrl: "<%=imgPath%>/notify_new.gif", useIframe:true}
          ,{title:"审批权限", contentUrl:"<%=contextPath%>/cms/permissions/perContent/setContent.jsp?seqId=<%=seqId %>&userType=APPROVAL_USER_CONTENT", imgUrl: "<%=imgPath%>/notify_new.gif", useIframe:true}
          ,{title:"发布权限", contentUrl:"<%=contextPath%>/cms/permissions/perContent/setContent.jsp?seqId=<%=seqId %>&userType=RELEASE_USER_CONTENT", imgUrl: "<%=imgPath%>/notify_new.gif", useIframe:true}
          ,{title:"回撤权限", contentUrl:"<%=contextPath%>/cms/permissions/perContent/setContent.jsp?seqId=<%=seqId %>&userType=RECEVIE_USER_CONTENT", imgUrl: "<%=imgPath%>/notify_new.gif", useIframe:true}
          ,{title:"排序权限", contentUrl:"<%=contextPath%>/cms/permissions/perContent/setContent.jsp?seqId=<%=seqId %>&userType=ORDER_CONTENT", imgUrl: "<%=imgPath%>/notify_new.gif", useIframe:true}
          ,{title:"批量设置",contentUrl:"<%=contextPath%>/cms/permissions/perContent/setBatch.jsp?seqId=<%=seqId %>",imgUrl: "<%=imgPath%>/notify_new.gif", useIframe:true}
          ];
</script>
</head>
<body class="bodycolor" topmargin="5" onLoad="buildTab(jso, 'smsdiv', 800);">
</body>
</html>
