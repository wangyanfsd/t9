	<%@ page language="java" contentType="text/html; charset=UTF-8"
	    pageEncoding="UTF-8"%>
	<%@ include file="/core/inc/header.jsp" %>
	<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
	<html>
	<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link rel="stylesheet" href="<%=cssPath %>/cmp/tab.css" type="text/css" />
	<link rel="stylesheet" href = "<%=cssPath%>/style.css">
	<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
	<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/tab.js"></script>
	<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
	<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
	<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
	<script type="text/javascript" src="<%=contextPath %>/core/funcs/system/syslog/js/sysyearlog.js"></script>
	<title>合同管理</title>
	<script type="text/javascript">
	var jso = [
	  {title:"劳动技能管理", contentUrl:"<%=contextPath %>/t9/subsys/oa/hr/manage/staffLaborSkills/act/T9NewLaborSkillsAct/findLaborSkillsInfo.act",  useIframe:true}
	  ,{title:"新建劳动技能信息", contentUrl:"<%=contextPath %>/subsys/oa/hr/manage/staff_labor_skills/new.jsp", useIframe:true}
	   ,{title:"劳动技能查询", contentUrl:"<%=contextPath %>/subsys/oa/hr/manage/staff_labor_skills/query.jsp",  useIframe:true}	  
	];
	function doInit(){
	  buildTab(jso, 'syslog');
	}
	</script>
	</head>
		<body onload="doInit()">
		 <div id = "syslog"></div>
		</body>
	</html>