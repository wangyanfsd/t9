<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header2.jsp" %>

<%
String deptName = request.getParameter("deptName");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>国务院发展研究中心办公专栏</title>

<link href="../../style/css/css.css" rel="stylesheet" type="text/css" />
<link href="../../style/css/css-other.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" href="<%=cssPath %>/page.css">
<style type="text/css">
<!--
body {
	background-color: #FFFFFF;
}
.two-a-down-popular {
	height: auto;
	padding-right: 5px;
	padding-left: 5px;
	font-family: "微软雅黑", "宋体";
	font-size: 15px;
	text-indent: 2em;
	color: #333333;
	line-height: 19px;
	padding-top: 18px;
	padding-bottom: 28px;
	clear: both;
}
.popular-title {
	font-family: "微软雅黑", "宋体";
	font-size: 16px;
	color: #0000CC;
	line-height: 27px;
	vertical-align: middle;
	display: block;
	float: left;
	font-weight: bold;
}
-->
</style>
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/tab.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath %>/subsys/portal/guoyan/js/pagepilot.js"></script>
<script type="text/javascript" src="<%=contextPath %>/subsys/portal/guoyan/js/portal.js"></script>
<script type="text/javascript">
var pageSize = 5;
var totalRecord  = 0;
var pageInfoS = null;
var deptName = "<%=deptName%>";
var deptId = "";
function doInit(){
  deptId = getDeptIdByName(deptName);
  totalRecord = loadData(0,pageSize);
  var cfgs = {
      dataAction: "",
      container: "pageInfo",
      pageSize:pageSize,
      loadData:loadDataAction,
      totalRecord:totalRecord
    };
  pageInfoS = new T9JsPagePilot(cfgs);
  if(totalRecord > 0){
    pageInfoS.show();
  }
}

function loadData(pageIndex,pageSize){
  var param = "pageIndex=" + pageIndex +"&pageSize=" + pageSize+ "&ruleName=deptNotify" + "&filter_dept=" + deptId;
  var url =  contextPath + "/t9/subsys/portal/guoyan/module/act/T9PortalGridNormal/loadDataPage.act";
  var rtJson = getJsonRs(url,param);
  //alert(rsText);
  if(rtJson.rtState == "0"){
    var data = rtJson.rtData;
    $('dataContent').innerHTML = "";
    for(var i = 0 ; i < data.pageData.length; i++){
      var news = data.pageData[i];
      var newId = news.newId;
      var subject =  news.subject;
      var newsTime = news.newsTime.substring(0,10);
      var hrefUrl = "deptNotify.jsp?Id=" + newId;
      var liHtml = "<li class=po-li><a href=\"" + hrefUrl + "\" class=\"block-left\"> " + subject + "</a>  <p class=\"block-right\">" + newsTime + "</p> </li>";
      $('dataContent').insert(liHtml,"bottom");
    }
  }
  return data.totalRecord;
}

function loadDataAction(obj){
  var pageNo = obj.pageIndex;
  var pageSize = obj.pageSize;
  totalRecord = loadData(pageNo,pageSize);
}
</script>
</head>

<body onload="doInit()">
<div class="frame-box-two">
   <!--第一块从这里开始-->
     <div class="box-two-a">
	   <div class="two-a-up"><img src="../../images-other/dept-header-tit.jpg" width="131" height="42" border="0"/></div>
		  <div class="two-a-down-popular">
		    <ul id="dataContent">
			</ul>
		  <div class="next-page" id="pageInfo"></div>
		  </div>
	 </div>
	 <div class="h10px"></div>
  </div>
</body>
</html>