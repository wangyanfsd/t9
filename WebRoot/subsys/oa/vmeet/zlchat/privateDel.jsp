<%@ page language="java" import="t9.subsys.oa.vmeet.act.*,t9.core.global.T9SysProps" contentType="text/html;charset=utf-8"%>
<%@ include file="inc.jsp"%>
<% /*文件作用: 处理删除"私聊"功能中的单个文件, 对方在收完文件之后会删除临时上传目录中的文件*/ %> 
<%
	//上传文件的保存目录
String uploadDir = T9SysProps.getAttachPath()+"\\zlchat\\wb\\";
			
	//zlchat 客户端传过来的文件名
	String fileName = request.getParameter("fileName");
	
	if (StringHelper.isNotEmpty(fileName))
	{
		
		File file = new File(uploadDir + fileName);
		if (file.exists())
		{
			file.delete();
		}
	}
%>
