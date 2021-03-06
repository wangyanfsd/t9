<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ include file="/core/inc/header.jsp" %>
<!DOCTYPE  html  PUBLIC  "-//W3C//DTD  HTML  4.01  Transitional//EN"  "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta  http-equiv="Content-Type"  content="text/html;  charset=UTF-8">
<title>Insert  title  here</title>
<link  rel="stylesheet"  href  =  "<%=cssPath%>/cmp/ExchangeSelect.css">
<link  rel="stylesheet"  href  ="<%=cssPath%>/style.css">
<script  type="text/Javascript"  src="<%=contextPath%>/core/js/datastructs.js"  ></script>
<script  type="text/Javascript"  src="<%=contextPath%>/core/js/sys.js"  ></script>
<script  type="text/Javascript"  src="<%=contextPath%>/core/js/prototype.js"  ></script>
<script  type="text/Javascript"  src="<%=contextPath%>/core/js/smartclient.js"  ></script>
<script  type="text/Javascript"  src="<%=contextPath%>/core/js/cmp/ExchangeSelect1.0.js"  ></script>
<script  type="text/javascript">

var selectbox;
var selectValue = "cancel";

function  doInit(){
  var url = "<%=contextPath%>/t9/core/funcs/setdescktop/shortcut/act/T9ShortcutAct/getFuncs.act";
  var rtJson = getJsonRs(url);
  if (rtJson.rtState == "0") {
    var  selected  =  rtJson.rtData.selected;
    var  disselected  =  rtJson.rtData.disselected;
    selectbox = new  ExchangeSelectbox({
      containerId:'div',
      selectedArray:selected,
      disSelectedArray:disselected,
      isSort:true,
      isOneLevel:false,
      title:'菜单快捷组定义',
      selectName:'nextProcess',
      selectedChange:exchangeHandler
    });
  }
}

function exchangeHandler(ids){
  selectValue = ids;
}

function submitForm(){
  var url = "<%=contextPath%>/t9/core/funcs/setdescktop/shortcut/act/T9ShortcutAct/modifyShortCut.act?sortCut=" + selectValue;
  var rtJson = getJsonRs(url);
  if (rtJson.rtState == "0") {
    top.reloadShortcut && top.reloadShortcut();
    window.location.href = "<%=contextPath%>/core/funcs/setdescktop/shortcut/submit.jsp";
  }
  else{
    alert("修改未成功");
  }
}
</script>
</head>
<body  onload="doInit()">
<div class="PageHeader"></div>
<div id="div"></div>
<div class="exchange-info">
	<p>点击条目时，可以组合CTRL或SHIFT键进行多选</p>
	<input type="button" value="保存设置" class="BigButton" onclick="submitForm()">
</div>
</body>
</html>
