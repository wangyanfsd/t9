<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*,t9.core.funcs.person.data.T9Person" %>
<%@ include file="/core/inc/header.jsp" %>
<%
  String treeId = request.getParameter("treeId");
  if(treeId == null){
    treeId = "";
  }
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>部门管理</title>
<style type="text/css">


</style>
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<link rel="stylesheet" href = "<%=cssPath%>/cmp/tree.css">
<script type="text/Javascript" src="<%=contextPath%>/core/js/datastructs.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/sys.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/prototype.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/smartclient.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/cmp/select.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/funcs/dept/dept.js" ></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/DTree1.0.js"></script>
<script type="text/javascript">
var treeId = "<%=treeId%>";

var arrayOfRolloverClasses = new Array();
var arrayOfClickClasses = new Array();
var activeRow = false;
var activeRowClickArray = new Array();

function doInit() {  
  var url = "<%=contextPath%>/t9/core/funcs/dept/act/T9DeptPositionAct/getUserPosition.act?deptSeqId=" + treeId;
  var rtJson = getJsonRs(url, null);
  //alert(rsText);
  if (rtJson.rtState == "0") {
    var table = document.getElementById("tbody");
    
    for(var i = 0; i < rtJson.rtData.length; i++) {
      var show1tr = document.createElement("tr");
      var show1td = document.createElement("td");
      //show1tr.className = "TableControl";
      show1tr.onmouseover = function(){
        this.className = "TableControl";
      }
      show1tr.onmouseout = function(){
        this.className = "TableLineSelect";
     }
     // if(show1td.className!='TableLineSelect')
      show1td.className = (i % 2 == 0) ? "TableLine1" : "TableLine2";
     // }
      show1tr.style.cursor = "pointer";
      //show1td.style.backgroundColor = "rgb(0, 255, 255)";
      show1td.align = "center";
      show1td.id = rtJson.rtData[i].seqId;
      show1td.idp = rtJson.rtData[i].positionName;
      show1td.onclick = function(){
        clickPriv(this);
        //if(this.className= (i % 2 == 0) ? "TableLine1" : "TableLine2"){
        //this.className = "TableControl";
        //}else{
          //this.className= (i % 2 == 0) ? "TableLine1" : "TableLine2"
          //}
      }
      show1td.innerHTML = rtJson.rtData[i].positionName;
      show1tr.appendChild(show1td);
      table.appendChild(show1tr);
    }        
  } else {
    var table1 = document.getElementById("tbody");
    var show2tr = document.createElement("tr");
    var show2td = document.createElement("td");
    show2tr.className = "TableControl";
    show2td.className = "menulines1";
    show2tr.style.cursor = "pointer";
    show2td.style.backgroundColor = "rgb(0, 255, 255)";
    show2td.align = "center";
    show2td.innerHTML = "未定义岗位";
    show2tr.appendChild(show2td);
    table1.appendChild(show2tr);
  	//alert(rtJson.rtMsrg); 
  }
}
function clickPriv(field){
  var seqId = field.id;
  var positionName = field.idp;
  var deptSeqId = field.idp;
  var parent = window.parent.positionedit;
  parent.location = '<%=contextPath %>/core/funcs/dept/positionedit.jsp?seqId=' + seqId + '&treeId=' + treeId + '&positionName=' + positionName;
  var url = "<%=contextPath%>/t9/core/funcs/dept/act/T9DeptPositionAct/getDept.act?treeId=" + deptSeqId;
 // var rtJson = getJsonRs(url);
  //if(rtJson.rtState == "0"){
   // bindJson2Cntrl(rtJson.rtData);
    //document.getElementById("positionNoOld").value = document.getElementById("positionNo").value ;
 // }else{
	//alert(rtJson.rtMsrg); 
 //}
  
}

</script>
</head>
<body class="" topmargin="1" leftmargin="0" onload="doInit()">
  <table class="TableBlock" width="100%" name="userTable" id="userTable">
  <tr>
    <td class="big3" align="center" style="background-color:#CCCCCC">
     	岗位管理
    </td>
  </tr>  
  <tbody id="tbody">
  </tbody>

  </table>
  <script type="text/javascript">
</script>
</body>
</html>