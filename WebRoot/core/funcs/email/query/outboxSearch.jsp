<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ include file="/core/inc/header.jsp" %>
    <%@ page import="java.util.*,java.text.*" %>
    <%@ page import="t9.core.funcs.email.data.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>内部邮件</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<link rel="stylesheet" href="<%=cssPath %>/page.css">
<link rel="stylesheet" href="<%=cssPath %>/cmp/tab.css" type="text/css" />
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/tab.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/funcs/email/js/util.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/funcs/email/js/emaillogic.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/page.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/Menu.js"></script>
<script type="text/javascript">
var pageMgr;
function doInit() {
  var param = "boxId=0";
  param = encodeURI(param);
  var url =  contextPath + "/t9/core/funcs/email/act/T9InnerEMailAct/listOutBox.act?" + param;
  var cfgs = {
    dataAction: url,
    container: "listContainer",
    moduleName:"email",
    colums: [
             {type:"selfdef", text:"选择", width: "8%",render:checkBoxRender},
             {type:"hidden", name:"emailBodyId"},
             {type:"data", name:"subject", text:"主题", width: "32%",render:subjectRender},
             {type:"hidden", name:"attachId"},
             {type:"data", name:"attach", text:"附件", width: "20%", dataType:"attach"},
             {type:"data", name:"sendTime", text:"修改日期", width:  "18%", dataType:"dateTime",format:'yyyy-MM-dd HH:mm'},    
             {type:"data", name:"ensize", text:"大小", width: "12%",dataType:"int",render:mailSizeRender},
             {type:"hidden", name:"important"}
             //,{type:"opts", width: 200, opts:[{clickFunc:reader, text:"再次发送"},{clickFunc:doEdit, text:"编辑 "}]}
             ]
  };
  pageMgr = new T9JsPage(cfgs);
  pageMgr.show();
  var total = pageMgr.pageInfo.totalRecord;
  if(total){
    $('total').innerHTML = total;
    showCntrl('listContainer');
    showCntrl('optM');
    showCntrl('head');
  }else{
    WarningMsrg('已删除邮件箱中无邮件！', 'msrg');
  }
}
function subjectRender(cellData, recordIndex, columIndex){
  var emailBodyId = this.getCellData(recordIndex,"emailBodyId");
 return "<a href=\"" + contextPath + "/core/funcs/email/new/index.jsp?resend=2&seqId=" + emailBodyId + "\">" + cellData + "</a>";
}
</script>
</head>
<body topmargin="5" onload="doInit()">
<table border="0" width="98%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="Big" height="40px"><img src="<%=imgPath%>/cmp/email/outbox.gif" WIDTH="20" HEIGHT="20" align="absmiddle"><span class="big3">草稿箱 --- 查询结果 </span></td>
    <td valign="bottom" id="head" style="display:none"><span class="small1">共&nbsp;<span class="big4" id="total"></span>&nbsp;封邮件</td>
    </tr>
    </table>
    <div id="listContainer" style="display:none;width:98%"></div>
<div id="optM"  style="display:none;width:98%">

<TABLE class=TableBlock width="98%" align=center>
  <TBODY>
    <TR>
      <TD class=TableControl noWrap>
         <div style="float:left;">
           <input type="checkbox" name="allbox" id="allbox" onClick="checkSearchAll();">
           <label for="allbox">全选</label>&nbsp;
           <a href="javascript:sendMailForDbox();" title="发送邮件"><img src="<%=imgPath%>/cmp/email/mailout.gif" align="absMiddle">发送</a>&nbsp;
           <a href="javascript:deleteMail(4);" title="删除所选邮件"><img src="<%=imgPath%>/cmp/email/inbox/delete.gif" align="absMiddle">删除</a>&nbsp;
           <a href="javascript:exportMail();" title="批量导出"><img src="<%=imgPath%>/cmp/email/inbox//xls.gif" align="absMiddle">导出Excel</a>&nbsp;
           <a href="javascript:exportEml();" title="单独导出"><img src="<%=imgPath%>/cmp/email/inbox/eml.gif" align="absMiddle">导出eml</a>&nbsp;
           </div>
	      </TD>
		</TR>
	</TBODY>
</TABLE>
</div>
<div id="msrg"></div>
<div style="height:5px"></div>
<center><input type="button" class="BigButton" value="返回" onclick="javascript:location='<%=contextPath %>/core/funcs/email/query/index.jsp;'"></center>
</body>
</html>