<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>项目问题列表</title>
<link rel="stylesheet" href="<%=cssPath%>/cmp/tab.css" type="text/css" />
<link rel="stylesheet" href="<%=cssPath%>/style.css">
<link rel="stylesheet" href="<%=cssPath%>/cmp/Calendar.css">
<link rel="stylesheet" href="<%=contextPath%>/project/css/dialog.css">
<script type="text/Javascript" src="<%=contextPath%>/core/js/datastructs.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/sys.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/prototype.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/Calendarfy.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/cmp/select.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/tab.js"></script>
<script type="text/javascript"	src="<%=contextPath%>/core/js/cmp/Menu.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/attach.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/attachMenu.js"></script>
<script type="text/javascript" src="<%=contextPath %>/project/js/jquery.min.js"></script>
<script type="text/javascript" src="<%=contextPath %>/project/js/dialog.js"></script>
<script type="text/javascript" src="<%=contextPath %>/project/js/util.js"></script>
<script type="text/javascript">
jQuery.noConflict();
function doInit(){
	 var url= contextPath + "/t9/project/bug/act/T9ProjBugAct/getBugInfoListByUserId.act?flag="+1;
	 var rtJson = getJsonRs(url);
	 if(rtJson.rtState == "0"){
		  var data=rtJson.rtData;
		  if(data.size()>0){
	    var str="<table class=\"TableList\" width=\"100%\">"
				   	 + "<tr class=\"TableHeader\">"
				     + "<td nowrap align=\"center\">项目名称</td>"
				     + "<td nowrap align=\"center\">问题名称</td>"
				     + "<td nowrap align=\"center\">提交人</td>"
				     + "<td nowrap align=\"center\" width=\"120px\">处理底线</td>"
				     + "<td nowrap align=\"center\" width=\"120px\">优先级</td>"
				     + "<td nowrap align=\"center\" width=\"120px\">状态</td>"
				     + "<td nowrap align=\"center\" width=\"120px\">操作</td>"
				     + "</tr>";
	    var bugList="";
				for(var i=0;i<data.size();i++){
					bugList +="<tr class=\"TableLine1\">"
								 // + "<td nowrap align=\"center\"><a href=\"../proj/basicInfo/index.jsp?projId="+data[i].projId+"\">"+data[i].projName+"</a></td>"
								  + "<td nowrap align=\"center\"><a href=\"#this\" onclick=\"showOpen("+data[i].projId+")\">"+data[i].projName+"</a></td>"

								  + "<td nowrap align=\"center\"><a href=\"#\" onclick=\"getDetailInfo("+data[i].seqId+")\">"+data[i].bugName+"</a></td>"
								  + "<td nowrap align=\"center\">"+data[i].beginUser+"</td>"
								  + "<td nowrap align=\"center\">"+data[i].deadLine.substring(0,10)+"</td>"
								  + "<td nowrap align=\"center\"><span>"+getLevel(data[i].level)+"</span></td>"
								  + "<td nowrap align=\"center\">"+getStatus(data[i].status)+"</td>"
								  + "<td nowrap align=\"center\">"+opts(data[i].seqId)+"</td></tr>";
				}
	    $("bugList").innerHTML=str+bugList+"</table>";
		  }else{
			  WarningMsrg('无相关项目问题', 'msrg');
		  }
	 }else{
		 alert(rtJson.rtMrsg);
	 }  
}

function getStatus(status){
	if(status=="0"){
		return "未提交";
	}else if(status=="1"){
		return "处理中";
	}else if(status=="2"){
		return "已反馈";
	}else{
		return "已超时";
	}
}
function opts(bugId){
	str="<span><a href=\"javascript:getDetailInfo("+bugId+")\">详情</a></span><span style=\"margin-left:20px;\"><a href=\"javascript:solveBug("+bugId+")\">办理</a></span>";
	return str;
}
function getLevel(level){
	if(level=="0"){
		return "<font color=gray>低</font>"
	}else if(level=="1"){
		return "<font color=green>普通</font>"
	}else if(level=="2"){
		return "<font color=#ff9933>高</font>"
	}else{
		return "<font color=red>非常高</font>"
	}
}

function solveBug(bugId){
	$("bugId").value=bugId;
	var myDate = new Date();
	myDate.getYear();        //获取当前年份(2位)
	myDate.getFullYear();    //获取完整的年份(4位,1970-????)
	myDate.getMonth();       //获取当前月份(0-11,0代表1月)
	myDate.getDate();        //获取当前日(1-31)
	myDate.getDay();         //获取当前星期X(0-6,0代表星期天)
	myDate.getTime();        //获取当前时间(从1970.1.1开始的毫秒数)
	myDate.getHours();       //获取当前小时数(0-23)
	myDate.getMinutes();     //获取当前分钟数(0-59)
	myDate.getSeconds();     //获取当前秒数(0-59)
	myDate.getMilliseconds();    //获取当前毫秒数(0-999)
	myDate.toLocaleDateString();     //获取当前日期
	var mytime=myDate.toLocaleTimeString();     //获取当前时间
	myDate.toLocaleString( );        //获取日期与时间
	$("curTime").innerHTML=myDate;
	ShowDialog('do');
}

function subResult(){
	if(check_form()){
		var url= contextPath + "/t9/project/bug/act/T9ProjBugAct/subSolveResult.act";
		 var rtJson = getJsonRs(url,mergeQueryString($("form1")));
		 if(rtJson.rtState == "0"){
			 location.reload();
		 }else{
			 alert(rtJson.rtMsrg);
		 }
	}
}

function check_form()
{
   if(document.form1.result.value.trim()=="")
   { alert("请填写处理结果！");
     return (false);
   }

   return (true);
}


function getDetailInfo(bugId){
	 var str="<table width=\"80%\" align=\"center\" class=\"TableList\" border=\"0\">";
	 var url= contextPath + "/t9/project/bug/act/T9ProjBugAct/getBugInfo.act?bugId="+bugId;
	 var rtJson = getJsonRs(url);
	 if(rtJson.rtState == "0"){
		 var data=rtJson.rtData;
			str +="<tr><td  nowrap class=\"TableContent\">问题名称：</td><td class=\"TableData\" >"+data.bugName+"</td></tr>"
			 		 + "<tr><td nowrap class=\"TableContent\">提交人：</td><td class=\"TableData\" >"+data.beginUser+"</td></tr>"
			 		 + "<tr><td nowrap class=\"TableContent\">问题描述：</td><td class=\"TableData\" >"+data.bugDesc+"</td></tr>"
			 		 + "<tr><td nowrap class=\"TableContent\">处理期限：</td><td class=\"TableData\" >"+data.deadLine+"</td></tr>"
			 		 + "<tr><td nowrap class=\"TableContent\">附件文档：</td><td class=\"TableData\" >"
			 		 + "<div id=\"projAttach\">"
			     + "<input type=\"hidden\" id=\"attachmentId1\" name=\"attachmentId\">"
			     + "<input type=\"hidden\" id=\"attachmentName1\" name=\"attachmentName\">"
			     + "<input type=\"hidden\" id=\"moduel\" name=\"moduel\" value=\"project\">"
			     + "<span id=\"showAtt1\">无附件 </span></div></td></tr>"
			 		 + "<tr><td nowrap class=\"TableContent\">处理记录：</td><td class=\"TableData\" >"+showResult(data.result)+"</td></tr>"
		}
		$("detail_body").innerHTML=str+"</table>";
		$('attachmentId1').value=data.attachmentId ;
		$('attachmentName1').value=data.attachmentName;
		var  selfdefMenu2 = {
			    office:["downFile","dump","read"], 
			    img:["downFile","dump","play"],
			    music:["downFile","dump","play"],  
			    video:["downFile","dump","play"], 
			    others:["downFile","dump"]
			  }

	if( $("attachmentId1").value){
		  attachMenuSelfUtil("showAtt1","project",$('attachmentName1').value ,$('attachmentId1').value, '','','',selfdefMenu2);  
		  }
		ShowDialog('detail');
		//$("detail").style.display="block";
} 

function showResult(result){
	if(result=="null"){
		return "";
	}else{
		var results=result.split("|*|");
		var str="";
		for(var i=0;i<results.size();i++){
			str+="<span>"+results[i]+"</span><br/>";
		}
		return str;
	}
}
function showOpen(seqId) {    
	var url = "../proj/basicInfo/index.jsp?projId="+seqId;
	var iWidth=800; //窗口宽度       
	var iHeight=800;//窗口高度          
	var iTop=(window.screen.height-iHeight)/2;          
	var iLeft=(window.screen.width-iWidth)/2;          
	window.open(url,"Detail","Scrollbars=no,Toolbar=no,Location=no,Direction=no,Resizeable=no,     Width="+iWidth+" ,Height="+iHeight+",top="+iTop+",left="+iLeft);        
	}
</script>
</head>
<body class="bodycolor" topmargin="5" onload="doInit();">
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
	<tr>
	   <td>
    <img src="../images/bug.gif" align="absmiddle"/>
    <span class="big3"> 项目问题记录</span>
	<td></tr>
</table>
<div id="bugList">
			<div style="margin:0 auto;text-align:center;font-size:18px;font-color:blue"></div>
</div>
<div id="msrg">
</div>
<div id="overlay"></div>
<div id="detail" class="ModalDialog" style="width:550px;">
  <div class="header"><span id="title" class="title">项目问题详情</span><a class="operation" href="javascript:HideDialog('detail');"><img src="../images/close.png"/></a></div>
  <div id="detail_body" class="body">
  </div>
  <div id="footer" class="footer">
    <input class="BigButton" onclick="HideDialog('detail')" type="button" value="关闭"/>
  </div>
</div>

<div id="do" class="ModalDialog" style="width:550px;">
  <div class="header"><span id="title" class="title">项目问题处理</span><a class="operation" href="javascript:HideDialog('do');"><img src="../images/close.png"/></a></div>
  <form action="" method="post" name="form1" id="form1">
  <div id="do_body" class="body">
  		<table class="TableList" border=0 align="center">
  		  <tr>
  		    <td class="TableContent">汇报时间：</td>
  		    <td class="TableData"><div id="curTime"></div></td>
  		  </tr>
  		  <tr>
  		  	<td class="TableContent">处理结果汇报：</td>
  		    <td class="TableData"><textarea class="BigInput" rows=5 cols=50 name="result" id="result"></textarea></td>
  		  </tr>
  		</table>
  </div>
  <div id="footer" class="footer">
    <input type="hidden" name="bugId" id="bugId"/>
    <input class="BigButton" type="button" value="确定" onclick="subResult()"/>
    <input class="BigButton" onclick="HideDialog('do')" type="button" value="关闭"/>
  </div>
  </form>
</div>

</body>
</html>