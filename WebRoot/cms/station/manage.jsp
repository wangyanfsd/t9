<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%
  T9Person person = (T9Person) session.getAttribute(T9Const.LOGIN_USER);
  String personDeptId = person.getDeptId() + "";
  String personUserPriv = person.getUserPriv();
  String personId = person.getSeqId()+ "";
%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>管理模板</title>
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<link rel="stylesheet" href="<%=cssPath%>/page.css">
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/page.js"></script>
<script type="text/javascript" src="<%=contextPath %>/cms/station/js/util.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/jquery/jquery-1.4.2.js"></script>
<script type="text/javascript" src="<%=contextPath%>/cms/station/js/jquery.progressbar.js"></script>
<script> 
jQuery.noConflict();
</script>
<script type="text/javascript" src="<%=contextPath %>/cms/station/js/stationLogic.js"></script>
<script> 
var pageMgr = null;
var personDeptId = "<%= personDeptId%>";
var personUserPriv = "<%= personUserPriv%>";
var personId = "<%= personId%>";
function doInit(){
  
  var url = "<%=contextPath%>/t9/cms/station/act/T9StationAct/getStationList.act";
  var cfgs = {
    dataAction: url,
    container: "listContainer",
    sortIndex: 1,
    sortDirect: "desc",
    colums: [
       {type:"selfdef", text:"选择", width: '5%', render:checkBoxRender},
       {type:"hidden", name:"seqId", text:"顺序号", dataType:"int"},
       {type:"data", name:"stationName",  width: '15%', text:"站点名称" ,align: 'center'},
       {type:"data", name:"stationDomainName",  width: '15%', text:"站点域名" ,align: 'center'},
       {type:"hidden", name:"templateId", text:"索引模板" ,dataType:"int"},
       {type:"data", name:"templateName",  width: '10%', text:"索引模板" ,align: 'center' ,render: stringRender},
       {type:"data", name:"stationPath",  width: '15%', text:"存储路径名称" ,align: 'center'},
       {type:"data", name:"extendName",  width: '10%', text:"站点索引扩展名" ,align: 'center'},
       {type:"data", name:"articleExtendName",  width: '10%', text:"站点文章扩展名" ,align: 'center'},
       {type:"hidden", name:"visitUser", text:"顺序号", dataType:"int"},
       {type:"hidden", name:"editUser", text:"顺序号", dataType:"int"},
       {type:"hidden", name:"newUser", text:"顺序号", dataType:"int"},
       {type:"hidden", name:"delUser", text:"顺序号", dataType:"int"},
       {type:"hidden", name:"relUser", text:"顺序号", dataType:"int"},
       {type:"selfdef", text:"操作", width: '15%',render:opts}]
  };
  pageMgr = new T9JsPage(cfgs);
  pageMgr.show();
  var total = pageMgr.pageInfo.totalRecord;
  if(total){
    showCntrl('listContainer');
    var mrs = " 共 " + total + " 条记录 ！";
    showCntrl('delOpt');
  }else{
    WarningMsrg('无站点信息', 'msrg');
  }
}

function stringRender(cellData, recordIndex, columIndex){
  if(!cellData){
    return "暂未选择";
  }
  return cellData;
}
</script>
</head>
<body topmargin="5" onload="doInit()">

<span class="progressBar" id="pb1"></span>

<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
 <tr>
   <td class="Big"><img src="<%=imgPath%>/notify_open.gif" align="absMiddle"><span class="big3">&nbsp;管理站点 </span>
   </td>
 </tr>
</table>
<br>
<div id="listContainer" style="display:none;width:100;">
</div>
<div id="delOpt" style="display:none">
<table class="TableList" width="100%">
<tr class="TableControl">
      <td colspan="19">
         <input type="checkbox" name="checkAlls" id="checkAlls" onClick="checkAll(this);"><label for="checkAlls">全选</label> &nbsp;
         <a href="javascript:deleteAll();" title="删除所选记录"><img src="<%=imgPath%>/delete.gif" align="absMiddle">删除所选记录</a>&nbsp;
      </td>
 </tr>
</table>
</div>

<div id="msrg">
</div>
</body>
</html>