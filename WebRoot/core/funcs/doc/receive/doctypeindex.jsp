<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
     <%@ include file="/core/inc/header.jsp" %>
     <%@ page  import="t9.core.funcs.doc.receive.data.T9DocConst,t9.core.util.T9Utility"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%
String grade = request.getParameter("grade");  //等级
String migrade = request.getParameter("migrade");  //密级
String reDate = request.getParameter("reDate");//收文日期
String toUserName = T9Utility.encodeSpecial(request.getParameter("toUserName"));//承办人
String fromUnits = T9Utility.encodeSpecial(request.getParameter("fromUnits"));  //来文单位
String oppDocNo = T9Utility.encodeSpecial(request.getParameter("oppDocNo")); //原文编号
String title = T9Utility.encodeSpecial(request.getParameter("title"));  //标题
String pishi = T9Utility.encodeSpecial(request.getParameter("pishi")); //领导批示
String seqId = request.getParameter("seqId");//收文ID
String docNo = T9Utility.encodeSpecial(request.getParameter("docNo"));//文号
String useName = T9Utility.encodeSpecial(request.getParameter("useName"));//联系人
String deptName = T9Utility.encodeSpecial(request.getParameter("deptName"));//部门
String runId = request.getParameter("runId");//部门
String webroot = request.getRealPath("/");
String docRecFlowType = T9DocConst.getProp(webroot  , T9DocConst.DOC_RECEIVE_FLOWTYPE) ;
%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>
<link rel="stylesheet" href ="<%=cssPath %>/style.css">
<link rel="stylesheet" href ="<%=cssPath %>/cmp/tab.css">
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/tab.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/funcs/doc/workflowUtility/utility.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/funcs/doc/receive/js/common.js" ></script>
<script type="text/javascript">

var docRecFlowType = "<%=docRecFlowType %>";
var isParent = true;
var grade = "<%=grade%>";
var migrade = "<%=migrade%>";
var reDate = "<%=reDate%>";
var toUserName = "<%=toUserName%>";
var fromUnits = "<%=fromUnits%>";
var oppDocNo = "<%=oppDocNo%>";
var title = "<%=title%>";
var pishi = "<%=pishi%>";
var seqId = "<%=seqId%>";
var docNo = "<%=docNo%>";
var useName = "<%=useName%>";
var deptName = "<%=deptName%>";
var runId = "<%=runId%>";

var param = 'grade='+ grade + "&migrade="+ encodeURIComponent(migrade) +"&reDate="+ reDate +"&toUserName="+encodeURIComponent(toUserName);
param += "&fromUnits="+encodeURIComponent(fromUnits)+ "&oppDocNo="+encodeURIComponent(oppDocNo)+"&title="+encodeURIComponent(title);
param += "&pishi="+encodeURIComponent(pishi)+ "&seqId="+seqId +"&docNo="+ docNo + "&useName="+encodeURIComponent(useName) ;
param += "&deptName="+encodeURIComponent(deptName) +"&runId="+ runId;

function doInit() {
  var jso = [];
  if (docRecFlowType) {
    var docs = docRecFlowType.split(",");
    for (var i = 0 ;i < docs.length ; i++) {
       var do1 = docs[i];
       jso[0] = {title:do1, useTextContent:true,contentUrl:contextPath + "/core/funcs/doc/receive/contentUrl.jsp?flowName="+encodeURIComponent(do1)+"&"+param, useIframe:true};
    }
    buildTab(jso, 'listDiv');//实例化标签页
  }
}

</script>
</head>
<body onload="doInit()">
<div id="listDiv">
</div>
</body>
</html>