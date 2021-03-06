<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<%
	String userId = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("userId")));
	String staffName = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("staffName")));
	String staffEName = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("staffEName")));
	String staffNo = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("staffNo")));
	String workNo = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("workNo")));
	String deptId = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("deptId")));
	String staffCardNo = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("staffCardNo")));
	String staffBirth = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("staffBirth")));
	String staffSex = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("staffSex")));
	String staffHighestSchool = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("staffHighestSchool")));
	
	String staffHighestDegree = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("staffHighestDegree")));
	String staffMajor = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("staffMajor")));
	String staffNationality = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("staffNationality")));
	String staffNativePlace = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("staffNativePlace")));
	String staffMaritalStatus = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("staffMaritalStatus")));
	String staffOccupation = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("staffOccupation")));
	String staffPoliticalStatus = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("staffPoliticalStatus")));
	String workType = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("workType")));
	String datesEmployed = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("datesEmployed")));
	String joinPartyTime = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("joinPartyTime")));

	String staffPhone = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("staffPhone")));
	String administrationLevel = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("administrationLevel")));
	String staffMobile = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("staffMobile")));
	String staffEmail = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("staffEmail")));
	String staffLittleSmart = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("staffLittleSmart")));
	String staffMsn = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("staffMsn")));
	String staffQq = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("staffQq")));
	String homeAddress = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("homeAddress")));
	String jobPosition = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("jobPosition")));
	String presentPosition = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("presentPosition")));

	String jobBeginning = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("jobBeginning")));
	String graduationDate = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("graduationDate")));
	String jobAge = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("jobAge")));
	String workAge = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("workAge")));
	String staffHealth = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("staffHealth")));
	String staffDomicilePlace = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("staffDomicilePlace")));
	String graduationSchool = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("graduationSchool")));
	String computerLevel = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("computerLevel")));
	String beginSalsryTime = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("beginSalsryTime")));
	String foreignLanguage1 = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("foreignLanguage1")));


	String foreignLanguage2 = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("foreignLanguage2")));
	String foreignLanguage3 = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("foreignLanguage3")));
	String foreignLevel1 = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("foreignLevel1")));
	String foreignLevel2 = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("foreignLevel2")));
	String foreignLevel3 = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("foreignLevel3")));
	String staffSkills = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("staffSkills")));
	
	String insureNum = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("insureNum")));
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>查询结果</title>
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<link rel="stylesheet" href="<%=cssPath%>/page.css">
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/page.js"></script>
<script type="text/javascript" src="<%=contextPath%>/subsys/oa/hr/manage/staffInfo/js/util.js"></script>
<script type="text/javascript" src="<%=contextPath%>/subsys/oa/hr/manage/staffInfo/js/staffInfoListLogic.js"></script>
<script type="text/javascript">
function doInit(){
	var param = "";
	param = "userId=" + encodeURIComponent("<%=userId%>");
	param += "&staffName=" + encodeURIComponent("<%=staffName%>");
	param += "&staffEName=" + encodeURIComponent("<%=staffEName%>");
	param += "&staffNo=" + encodeURIComponent("<%=staffNo%>");
	param += "&workNo=" + encodeURIComponent("<%=workNo%>");
	param += "&deptId=" + encodeURIComponent("<%=deptId%>");
	param += "&staffCardNo=" + encodeURIComponent("<%=staffCardNo%>");
	param += "&staffBirth=" + encodeURIComponent("<%=staffBirth%>");
	param += "&staffSex=" + encodeURIComponent("<%=staffSex%>");
	param += "&staffHighestSchool=" + encodeURIComponent("<%=staffHighestSchool%>");
	
	param += "&staffHighestDegree=" + encodeURIComponent("<%=staffHighestDegree%>");
	param += "&staffMajor=" + encodeURIComponent("<%=staffMajor%>");
	param += "&staffNationality=" + encodeURIComponent("<%=staffNationality%>");
	param += "&staffNativePlace=" + encodeURIComponent("<%=staffNativePlace%>");
	param += "&staffMaritalStatus=" + encodeURIComponent("<%=staffMaritalStatus%>");
	param += "&staffOccupation=" + encodeURIComponent("<%=staffOccupation%>");
	param += "&staffPoliticalStatus=" + encodeURIComponent("<%=staffPoliticalStatus%>");
	param += "&workType=" + encodeURIComponent("<%=workType%>");
	param += "&datesEmployed=" + encodeURIComponent("<%=datesEmployed%>");
	param += "&joinPartyTime=" + encodeURIComponent("<%=joinPartyTime%>");
	
	param += "&staffPhone=" + encodeURIComponent("<%=staffPhone%>");
	param += "&administrationLevel=" + encodeURIComponent("<%=administrationLevel%>");
	param += "&staffMobile=" + encodeURIComponent("<%=staffMobile%>");
	param += "&staffEmail=" + encodeURIComponent("<%=staffEmail%>");
	param += "&staffLittleSmart=" + encodeURIComponent("<%=staffLittleSmart%>");
	param += "&staffMsn=" + encodeURIComponent("<%=staffMsn%>");
	param += "&staffQq=" + encodeURIComponent("<%=staffQq%>");
	param += "&homeAddress=" + encodeURIComponent("<%=homeAddress%>");
	param += "&jobPosition=" + encodeURIComponent("<%=jobPosition%>");
	param += "&presentPosition=" + encodeURIComponent("<%=presentPosition%>");
	
	param += "&jobBeginning=" + encodeURIComponent("<%=jobBeginning%>");
	param += "&graduationDate=" + encodeURIComponent("<%=graduationDate%>");
	param += "&jobAge=" + encodeURIComponent("<%=jobAge%>");
	param += "&workAge=" + encodeURIComponent("<%=workAge%>");
	param += "&staffHealth=" + encodeURIComponent("<%=staffHealth%>");
	param += "&staffDomicilePlace=" + encodeURIComponent("<%=staffDomicilePlace%>");
	param += "&graduationSchool=" + encodeURIComponent("<%=graduationSchool%>");
	param += "&computerLevel=" + encodeURIComponent("<%=computerLevel%>");
	param += "&beginSalsryTime=" + encodeURIComponent("<%=beginSalsryTime%>");
	param += "&foreignLanguage1=" + encodeURIComponent("<%=foreignLanguage1%>");
	
	param += "&foreignLanguage2=" + encodeURIComponent("<%=foreignLanguage2%>");
	param += "&foreignLanguage3=" + encodeURIComponent("<%=foreignLanguage3%>");
	param += "&foreignLevel1=" + encodeURIComponent("<%=foreignLevel1%>");
	param += "&foreignLevel2=" + encodeURIComponent("<%=foreignLevel2%>");
	param += "&foreignLevel3=" + encodeURIComponent("<%=foreignLevel3%>");
	param += "&staffSkills=" + encodeURIComponent("<%=staffSkills%>");
	param += "&insureNum=" + encodeURIComponent("<%=insureNum%>");
	
	
	var url = "<%=contextPath%>/t9/subsys/oa/hr/manage/staffInfo/act/T9HrStaffInfoAct/queryStaffInfoListJson.act?" + param;
	var cfgs = {
	    dataAction: url,
	    container: "listContainer",
	    sortIndex: 1,
	    sortDirect: "desc",
	    colums: [
	       {type:"selfdef", text:"选择", width: '5%', render:checkBoxRender},
	       {type:"hidden", name:"seqId", text:"顺序号", dataType:"int"},
	       {type:"data", name:"userId",  width: '10%', text:"OA用户名", render:infoCenterFunc},
	       {type:"data", name:"userName",  width: '10%', text:"姓名", render:infoCenterFunc},
	       {type:"data", name:"staffNo",  width: '10%', text:"编号", render:infoCenterFunc},
	       {type:"data", name:"workNo",  width: '10%', text:"工号", render:infoCenterFunc},
	       {type:"data", name:"staffBirth",  width: '10%', text:"年龄", render:staffAgeFunc},
	       {type:"data", name:"staffSex",  width: '10%', text:"性别", render:staffSexFunc},
	       {type:"selfdef", text:"操作", width: '10%',render:queryResultOpts}]
	  };
	  pageMgr = new T9JsPage(cfgs);
	  pageMgr.show();
	  var total = pageMgr.pageInfo.totalRecord;
	  if(total){
	    showCntrl('listContainer');
	    var mrs = " 共 " + total + " 条记录 ！";
	    showCntrl('delOpt');
	  }else{
	    WarningMsrg('无符合条件的记录！', 'msrg');
	  }
}


</script>
</head>
<body onload="doInit();">
	<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="Big"><img src="<%=imgPath %>/infofind.gif" align="middle">
    <span class="big3"> 人事档案查询结果</span><br>
   </td>
	</tr>
</table>
<br>
<div id="listContainer" style="display:none;width:100;"></div>
<div id="delOpt" style="display:none">
<table class="TableList" width="100%">
<tr class="TableControl">
      <td colspan="19">
         <input type="checkbox" name="checkAlls" id="checkAlls" onClick="checkAll(this);"><label for="checkAlls">全选</label> &nbsp;
         <a href="javascript:deleteInfo();" title="删除所选人事"><img src="<%=imgPath%>/delete.gif" align="middle">删除所选人事  </a>&nbsp;
      </td>
 </tr>
</table>
</div>

<div id="msrg"></div>
<br>
<div id="backDiv" style="display: " align="center">
<br>
  <input type="button" value="返回" class="BigButton" onclick="window.location.href='<%=contextPath %>/subsys/oa/hr/manage/staffInfo/query.jsp';">&nbsp;&nbsp;
</div>


</body>
</html>