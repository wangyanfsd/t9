<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<%@ include file="/core/funcs/doc/workflowUtility/workflowheader.jsp" %>
<%@ page import="t9.core.funcs.doc.act.T9FlowSortAct" %>
<%@ page import="t9.core.funcs.doc.data.T9DocFlowSort" %>
<%@ page import="t9.core.funcs.person.data.T9Person" %>
<%@ page import="java.util.*" %>
<%@ page import="t9.core.data.T9RequestDbConn" %>
<%@ page import="t9.core.global.T9BeanKeys" %>
<%@ page import="t9.core.funcs.doc.util.T9WorkFlowUtility" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>列出流程分类</title>
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<script type="text/Javascript" src="<%=contextPath%>/core/js/datastructs.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/sys.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/prototype.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/smartclient.js" ></script>
<script type="text/javascript">
function delFlowSort(seqId, haveChild, sortParent) {
  if(!confirm("确认删除！")) {
    return ;
  }

  var url = "<%=contextPath %><%=moduleSrcPath %>/act/T9FlowSortAct/deleteFlowSort.act";
  var rtJson = getJsonRs(url, "seqId=" + seqId + "&haveChild=" + haveChild + "&sortParent=" + sortParent);
  if (rtJson.rtState == "0") {
    alert(rtJson.rtMsrg); 
    window.location.reload();
  }else {
    alert(rtJson.rtMsrg); 
  }
}
</script>
</head>
<body>
<table border="0" width="100%" cellspacing="0" cellpadding="3">
  <tr>
    <td>
      <img src="<%=imgPath %>/notify_open.gif" />
      <span class="big3"> 管理流程分类</span>
    </td>
  </tr>
</table>

<table cellscpacing="1" cellpadding="3" width="500" class=TableList align="center">
  <tr class="TableHeader">
    <td>序号</td>
    <td>名称</td>
    <td>流程数量</td>
    <td>操作</td>
  </tr>
<%
T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
  ArrayList<T9DocFlowSort> flowSortList = (ArrayList<T9DocFlowSort>)request.getAttribute("flowSortList");
  Map countMap = (Map)request.getAttribute("countMap");
  
  for(int i = 0; i < flowSortList.size(); i++) {
    T9DocFlowSort sort = flowSortList.get(i);
    
    if(sort.getSortParent() == 0) {
%>
  <tr class="<%=(i % 2 == 0) ? "TableLine2" : "TableLine1"%>">
    <td><%=sort.getSortNo() %></td>
    <td><%=sort.getSortName() %></td>
    <td><%=countMap.get(sort.getSeqId()) %></td>
    <td>
      <a href="<%=contextPath %><%=moduleContextPath %>/flowsort/flowsortinput.jsp?seqId=<%=sort.getSeqId() %>&sortParent=<%=sort.getSortParent() %>">编辑</a>&nbsp;&nbsp;
      <% if ((Integer)countMap.get(sort.getSeqId()) == 0 ) { %>
      <a href="javascript:delFlowSort('<%=sort.getSeqId()%>' ,'<%=sort.getHaveChild() %>' ,'<%=sort.getSortParent() %>');">删除</a>
      <%} %>
    </td>
  </tr>
<%
    } else {     
%>
  <tr class="<%=(i % 2 == 0) ? "TableLine2" : "TableLine1"%>">
    <td>&nbsp;&nbsp;&nbsp;&nbsp;<%=sort.getSortNo() %></td>
    <td>&nbsp;&nbsp;&nbsp;&nbsp;<%=sort.getSortName() %></td>
    <td><%='0' %></td>
    <td>
      <a href="<%=contextPath %><%=moduleContextPath %>/flowsort/flowsortinput.jsp?seqId=<%=sort.getSeqId() %>&sortParent=<%=sort.getSortParent() %>">编辑</a>&nbsp;&nbsp;
      <a href="javascript:delFlowSort('<%=sort.getSeqId()%>' ,'<%=sort.getHaveChild() %>' ,'<%=sort.getSortParent() %>');">删除</a>
    </td>
  </tr>

<%
    }   
  }
%>
</table>
</body>
</html>