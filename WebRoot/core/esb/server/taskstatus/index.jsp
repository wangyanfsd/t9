<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>收发文件</title>
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
		   {title:"发送任务", contentUrl:"<%=contextPath%>/core/esb/server/taskstatus/upload.jsp", imgUrl: "<%=imgPath%>/notify_new.gif", useIframe:true}
          ,{title:"接收任务", contentUrl:"<%=contextPath%>/core/esb/server/taskstatus/download.jsp", imgUrl: "<%=imgPath%>/notify_new.gif", useIframe:true}
          ,{title:"发送任务统计", contentUrl:"<%=contextPath%>/core/esb/server/taskstatus/upload2.jsp", imgUrl: "<%=imgPath%>/notify_new.gif", useIframe:true}
          ,{title:"接收任务统计", contentUrl:"<%=contextPath%>/core/esb/server/taskstatus/download2.jsp", imgUrl: "<%=imgPath%>/notify_new.gif", useIframe:true}
          ,{title:"发送队列", contentUrl:"<%=contextPath%>/core/esb/server/taskstatus/task.jsp", imgUrl: "<%=imgPath%>/notify_new.gif", useIframe:true}
          ,{title:"接收队列", contentUrl:"<%=contextPath%>/core/esb/server/taskstatus/task2.jsp", imgUrl: "<%=imgPath%>/notify_new.gif", useIframe:true}
          ];
</script>
</head>
<body class="bodycolor" topmargin="5" onLoad="buildTab(jso, 'smsdiv', 800);">
</body>
</html>
