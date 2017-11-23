<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<%@ page import="t9.core.funcs.person.data.T9Person" %>
<%
  String deptId = request.getParameter("deptId");
  if (deptId == null) {
    deptId = "";
  }
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
<script type="text/javascript">
var deptId = "<%=deptId%>";

var requestUrl = contextPath + "/t9/core/esb/client/act/T9DeptTreeAct";
function deptFunc(parentId){
  var url = requestUrl + "/selectDept.act";
  var rtJson = getJsonRs(url , "deptId=" + deptId);
  if(rtJson.rtState == "1"){
    alert(rtJson.rtMsrg); 
    return ;
  }
  var prcs = rtJson.rtData;
  var selects = document.getElementById("DEPT_PARENT");
  for(var i = 0; i < prcs.length; i++){
    var prc = prcs[i];
    var option = document.createElement("option"); 
    option.value = prc.value; 
    option.innerHTML = prc.text; 
    if(prc.value == parentId){
      option.selected = true;
    }
    selects.appendChild(option);
  }
}
function doInit(){
  var parentId = '0';
  if (deptId) {
    var url = requestUrl + "/dept.act";
    var rtJson = getJsonRs(url , "deptId=" + deptId);
    if(rtJson.rtState == "0"){
      var data = rtJson.rtData;
      $('DEPT_NO').value = data.deptNo;
      $('DEPT_NAME').value = data.deptName;
      $('ESB_USER').value = data.esbUser;
      $('DEPT_DESC').value = data.deptDesc;
      parentId = data.deptParent;
      $('info').update("编辑外部组织机构 - ["+data.deptName+"]");
    }
  } else {
    $('info').update("新建外部组织机构");
  }
  deptFunc(parentId)
}
function isDigit(s)   
{   
   var patrn=/^[0-9]{1,20}$/;   
   if (!patrn.exec(s)) return false  
   return true  
}     
  
function CheckForm()
{
   if(document.form1.DEPT_NO.value=="")
   { alert("排序号不能为空！");
     return (false);
   }
   
   if( document.form1.DEPT_NO.value.length!=3 || !isDigit(document.form1.DEPT_NO.value) )
   { alert("排序号应为3位数字！");
     return (false);
   }

   if(document.form1.DEPT_NAME.value=="")
   { alert("组织结构名称不能为空！");
     return (false);
   }
   
   if(document.form1.ESB_USER.value=="")
   { alert("数据交换平台标识不能为空！");
     return (false);
   }
   if(document.form1.DEPT_PARENT.value==document.form1.DEPT_ID.value)
   { alert("上级部门不能是本部门！");
     return (false);
   }
   return true;
}

function delete_dept(DEPT_ID)
{
  msg='确认要删除该外部组织机构吗？这将同时删除所有下级部门和部门中的用户！';
  if(window.confirm(msg))
  {
    var url = requestUrl + "/delDept.act";
    var rtJson = getJsonRs(url , "deptId=" + DEPT_ID);
    if(rtJson.rtState == "0"){
      alert(rtJson.rtMsrg);
      parent.deptListTree.location.reload(); 
      parent.deptinput.location.href='deptinput.jsp';
    }
  }
}
function updateDept(){
  if (!CheckForm()) {
    return ;
  }
  var url = requestUrl + "/updateDept.act";
  var rtJson = getJsonRs(url , $('form1').serialize());
  if(rtJson.rtState == "0"){
    alert(rtJson.rtMsrg); 
    if (!deptId) {
      $("form1").reset();
     //  parent.deptListTree.location.reload();
    } 
    parent.deptListTree.location.reload(); 
  }
}
</script>
</head>

<body class="bodycolor" topmargin="5" onload="doInit();">

<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="Big"><img src="<%=imgPath %>/edit.gif" align="absmiddle">
        <span class="big3" id="info"></span>
    </td>
      </tr>
</table>

<form   method="post" name="form1" id="form1">
<table class="TableBlock" width=500" align="center">
   <tr>
    <td nowrap class="TableData">排序号：</td>
    <td nowrap class="TableData">
       <input type="text" name="DEPT_NO" id="DEPT_NO" class="BigInput" size="10" maxlength="3" value="">&nbsp;3位数字，用于同一级次部门排序，不能重复    </td>
   </tr>
   <tr>
    <td nowrap class="TableData">组织机构名称：</td>
    <td nowrap class="TableData">
        <input type="text" name="DEPT_NAME" id="DEPT_NAME" class="BigInput" size="25" maxlength="25" value="">&nbsp;
    </td>
   </tr>
   <tr>
    <td nowrap class="TableData">数据交换平台标识：</td>
    <td nowrap class="TableData">
        <input type="text" name="ESB_USER" id="ESB_USER" class="BigInput" size="25" maxlength="25" value="">&nbsp;
    </td>
   </tr>
   <tr>
    <td nowrap class="TableData">上级组织机构：</td>
      <td class="TableData">
        <select name="DEPT_PARENT" id="DEPT_PARENT" class="BigSelect">
           <option value="0">无</option>
        </select>
      </td>
   </tr>
   <tr>
    <td nowrap class="TableData">描述信息：</td>
    <td nowrap class="TableData">
        <textarea name="DEPT_DESC" id="DEPT_DESC" class="SmallInput" cols="60" rows="5"></textarea>
    </td>
   </tr>
   <tr>
    <td nowrap  class="TableControl" colspan="2" align="center">
        <input type="hidden" name="DEPT_ID" id="DEPT_ID" value="<%=deptId %>">
        <% if (!T9Utility.isNullorEmpty(deptId)) { %>
        <input type="button" value="保存修改" class="BigButton" title="保存修改" name="button" onclick="updateDept()">
   <input type="button" value="删除"  class="BigButton"  onClick="delete_dept('<%=deptId %>')" title="删除">
   <% } else { %>
   <input type="button" value="添加" class="BigButton" title="添加" name="button" onclick="updateDept()">
   <% } %>
    </td>
</table>
  </form>

</body>
</html>