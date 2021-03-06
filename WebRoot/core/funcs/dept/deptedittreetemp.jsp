<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<%
String deptId = request.getParameter("deptId");

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>部门管理</title>
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<script type="text/Javascript" src="<%=contextPath%>/core/js/datastructs.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/sys.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/prototype.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/smartclient.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/cmp/select.js" ></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/DTree1.0.js"></script>
<script type="text/javascript">
var deptId = '<%=deptId%>';
function doInit(){
  var url =  contextPath + "/t9/core/funcs/dept/act/T9DeptTreeAct/getTree.act?id=";
  tree = new DTree({bindToContainerId:'deplist'
		            ,requestUrl:url
		            ,isOnceLoad:false
		            ,checkboxPara:{isHaveCheckbox:false}
		            ,linkPara:{clickFunc:clickLink,linkAddress:'index.jsp?id=',target:'user'}
  });
  tree.show();
  var obj = tree.getFirstNode();
  tree.open(obj.nodeId);
  //var nowDeptNode = tree.getNode(deptId);
 // if(nowDeptNode){
   // tree.removeNode(nowDeptNode.nodeId);
  //}
}
function clickLink(id){
  var obj = tree.getNode(id);
  //while(childId == deptId){
    //tree.removeNode(deptId);
   // break; 
  //}
  var parentId = obj.parentId;
  if(parentId != '0'){
  	deptParent = tree.getNode(parentId).name;
  }
  var parentWindowObj = window.dialogArguments;
  parentWindowObj.document.getElementById('deptApplyDesc').value = obj.name;
  parentWindowObj.document.getElementById('deptApply').value = id;
  //var deptParent = $('a-' + id).innerHTML;
}
</script>
</head>
<body class="" onload="doInit()">
<div id="deplist"></div>
<table border="0" cellspacing="1" width="100%" class="small"  cellpadding="2" align="center">
   <tr>
     <td nowrap align="right">
     	<input type="button" class="BigButton" value="关 闭" onclick="top.close();">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     </td>
   </tr>
</table>
</body>
</html>