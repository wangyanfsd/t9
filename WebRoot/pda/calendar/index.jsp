<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="t9.core.global.T9SysProps" %>
<%@ page import="t9.core.funcs.person.data.T9Person" %>
<%@ page import="t9.pda.calendar.data.T9PdaCalendar" %>
<%@ page import="t9.pda.calendar.data.T9PdaAffair" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Date" %>
<%
String contextPath = request.getContextPath();
if (contextPath.equals("")) {
  contextPath = "/t9";
}
T9Person loginPerson = (T9Person)session.getAttribute("LOGIN_USER");
List calendars = (List) request.getAttribute("calendars");
List affairs = (List) request.getAttribute("affairs");
%>
<!doctype html>
<html>
<head>
<title><%=  T9SysProps.getString("productName") %></title>
<meta name="viewport" content="width=device-width" />
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<link rel="stylesheet" type="text/css" href="<%=contextPath %>/pda/style/list.css" />
</head>
<body>
<div id="list_top">
  <div class="list_top_left"><a class="ButtonBack" href="<%=contextPath %>/pda/main.jsp?P=<%=loginPerson.getSeqId() %>"></a></div>
  <span class="list_top_center">今日日程</span>
  <div class="list_top_right"><a class="ButtonB" href="<%=contextPath %>/pda/calendar/new.jsp?P=<%=loginPerson.getSeqId() %>">新建日程</a></div>
</div>
<div id="list_main" class="list_main">
<%
int count = calendars.size();
for(int i = 0 ; i < count ; i++){
  T9PdaCalendar calendar = (T9PdaCalendar)calendars.get(i);
  int seqId = calendar.getSeqId();
  Date calTime = calendar.getCalTime();
  Date endTime = calendar.getEndTime();
  String content = calendar.getContent();

%>
    <a class="list_item" hidefocus="hidefocus" >
       <div class="list_item_subject"><%=calTime.toString().substring(11,16) %>-<%=endTime.toString().substring(11,16) %> <%=content %></div>
       <div class="list_item_time"></div>
       <div class="list_item_arrow"></div>
    </a>           
<% 
}
int count1 = affairs.size();
for(int i = 0 ; i < count1 ; i++){
  T9PdaAffair affair = (T9PdaAffair)affairs.get(i);
  int seqId = affair.getSeqId();
  String userId = affair.getUserId();
  int type = affair.getType();
  String remindDate = affair.getRemindDate();
  String remindTime = affair.getRemindTime();
  String content = affair.getContent();
%>
<a class="list_item" hidefocus="hidefocus" >
   <div class="list_item_subject"><%=remindTime.substring(0,5) %> <%=content %></div>
   <div class="list_item_time"></div>
   <div class="list_item_arrow"></div>
</a>    
<%
}
if(count==0 && count1==0)
  out.print("<div id=\"message\" class=\"message\">无符合条件的记录</div>");
%>
</div>
<div id="list_bottom">
  <div class="list_bottom_right"><a class="ButtonHome" href="<%=contextPath %>/pda/main.jsp?P=<%=loginPerson.getSeqId() %>"></a></div>
</div>
</body>
<script type="text/javascript" src="<%=contextPath %>/pda/js/logic.js"></script>
</html>
