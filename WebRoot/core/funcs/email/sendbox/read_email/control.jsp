<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
        <%@ include file="/core/inc/header.jsp" %>
        <% 
        String seqId = request.getParameter("seqId");
        String isQuery = request.getParameter("isQuery");
        if(isQuery == null){
          isQuery = "";
        }
        String totalstr = request.getParameter("total") == null ? "0" : request.getParameter("total");
        String recordIndexstr = request.getParameter("recordIndex") == null ? "0" : request.getParameter("recordIndex");
        int total = Integer.valueOf(totalstr);
        int recordIndex = Integer.valueOf(recordIndexstr);
        %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<link rel="stylesheet" href="<%=cssPath %>/cmp/tab.css" type="text/css" />
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/tab.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/funcs/email/js/util.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/funcs/email/js/emaillogic.js"></script>
<script>
var isQuery = "<%=isQuery%>";

function reSend(){
  url="<%=contextPath%>/core/funcs/email/new?resend=1&seqId=<%=seqId%>";
  parent.location=url;
}
function loadPage(recordIndex,total){
  var emailBodyId = getEmailBodyId(recordIndex);
  if(!emailBodyId){
    return;
  }
  var url = contextPath + "/core/funcs/email/sendbox/read_email/index.jsp?seqId=" + emailBodyId + "&total=" + total + "&recordIndex=" + recordIndex ;
  parent.location = url;
}
function getEmailBodyId (recordIndex){
  var url = contextPath + "/t9/core/funcs/email/act/T9InnerEMailAct/pageForSendBox.act?pageIndex="+ recordIndex ;
  var rtJson = getJsonRs(url);
  if(rtJson.rtState == "0"){
    return rtJson.rtData.pageData[0].bodyId;
  }else{
    return "";
  }
}
function backHis(){
  if(isQuery == "1"){
    history.back();
   }else{
     parent.location = contextPath + '/core/funcs/email/sendbox/index.jsp?boxId=0&pageNum=0&pageRows=10';
   }
}
</script>
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/cmp/grid.css" />
<title>控制页面</title>
</head>
<body topmargin="0">
<table width="100%" cellpadding="3">
  <tr>
    <td align="center">
    <%if(recordIndex > 0) {%>
     <input type="button" value="上一封" class="BigButton" onclick="loadPage('<%=recordIndex - 1 %>','<%=total %>')" title="上一封邮件">&nbsp;
    <%}%>
    <%if(recordIndex < total - 1) {%>
    <input type="button" value="下一封" class="BigButton" onclick="loadPage('<%=recordIndex + 1 %>','<%=total %>')" title="下一封邮件">&nbsp;
   <%}%> 
   <input type="button" value="删除" class="BigButtonA" onclick="parent.mail_view.deletByShow('<%=seqId%>');" title="删除此邮件">&nbsp;
   <input type="button" value="打印" class="BigButtonA" onclick="parent.mail_view.print2('<%=seqId %>');" title="打印此邮件">&nbsp;
   <input type="button" value="复制全文" class="BigButton" onclick="copy_email();" title="复制全文">&nbsp;
   <input type="button" value="再次发送" class="BigButton" onclick="reSend();" title="再次发送">&nbsp;
   <input type="button" value="返回" class="BigButtonA" onClick="backHis()">
    </td>
  </tr>
</table>
</body>
</html>