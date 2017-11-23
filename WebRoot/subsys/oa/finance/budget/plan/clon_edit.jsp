<%@ page language="java" import="java.util.*,java.sql.*,t9.core.funcs.attendance.manage.logic.*,t9.core.data.T9RequestDbConn,t9.core.global.T9BeanKeys" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<%
  Calendar cl =Calendar.getInstance();
  int curYear = cl.get(Calendar.YEAR);
  int nextYear = curYear - 1;
  T9ManageOutLogic tmol = new T9ManageOutLogic();
  T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
  Connection dbConn = requestDbConn.getSysDbConn();
  T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
  T9PersonLogic pl = new T9PersonLogic();
  int userId = user.getSeqId();
  int deptId = user.getDeptId();
  String deptName = tmol.selectByUserIdDept(dbConn,String.valueOf(user.getSeqId()));
  String type = (String)request.getParameter("type");
  if(type==null){
    type = "";
  }
  T9DeptLogic dl = new T9DeptLogic();
  T9BudgetApply ba =(T9BudgetApply)request.getAttribute("budgetApply");
  String clon_edit = (String)request.getParameter("clon_edit");
  int year = curYear;
  String baTemp = "0";
  if(ba!=null){
    baTemp = "1";
    year = ba.getBudgetYear();
    if( ba.getDeptId()!=null&&!ba.getDeptId().equals("")){
      deptId = Integer.parseInt(ba.getDeptId());
    }
  }
  String titleDesc = "克隆预算信息(" + year + "年)";
  if(clon_edit==null){
    clon_edit = "";
    titleDesc = "";
  }else{
    if(clon_edit.equals("3")){
      titleDesc = "编辑预算信息(" + year + "年)";
    }
  }
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="t9.core.funcs.person.logic.T9PersonLogic"%>
<%@page import="t9.core.funcs.dept.logic.T9DeptLogic"%>
<%@page import="t9.subsys.oa.finance.data.T9BudgetApply"%><html>
<head>
<title ><%=titleDesc%></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="<%=cssPath%>/style.css">
<link rel="stylesheet" href="<%=cssPath%>/cmp/Calendar.css">
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/tab.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/Calendarfy.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/Menu.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/orgselect.js" ></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/page.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/attach.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/attachMenu.js"></script>
<script type="text/javascript">
var upload_limit=1,limit_type="php,php3,php4,php5,";
var deptId = '<%=deptId%>';
var year = '<%=year%>';
var type = '<%=type%>';
var baTemp = '<%=baTemp%>';
var clon_edit = '<%=clon_edit%>';
var  selfdefMenu = {
  	office:["downFile","dump","read","deleteFile"], 
    img:["downFile","dump","play","deleteFile"],  
    music:["downFile","dump","play","deleteFile"],  
    video:["downFile","dump","play","deleteFile"], 
    others:["downFile","dump","deleteFile"]
	}

function doOnloadFile(){
  var attr = $("attr");
  var seqId  = $("seqId").value;
  attachMenuSelfUtil(attr,"finance",$('attachmentName').value ,$('attachmentId').value, '','',seqId,selfdefMenu);
}
//浮动菜单文件的删除

function deleteAttachBackHand(attachName,attachId,attrchIndex){
	var url= "<%=contextPath%>/t9/subsys/oa/finance/act/T9BudgetApplyAct/deleleFile.act?attachId=" + attachId +"&attachName=" + encodeURIComponent(attachName) + "&seqId=" + attrchIndex;
	var rtJson = getJsonRs(url); 
	  if(rtJson.rtState == "1"){
	    alert(rtJson.rtMsrg); 
	    return ;
	  }else{
	   prcsJson=rtJson.rtData;
	   var updateFlag=prcsJson.updateFlag;
	   if(updateFlag=='1'){
	     return true;
	   }else{
		  return false;
	   }
	}
}
function upload_attach(){
  if (document.form1.ATTACHMENT.value=="")
      return;
  if(CheckForm())
  {
   document.form1.OP.value="1";
   document.form1.submit();
  }
}
 
function checkForm(){
  if(document.form1.budgetProposer.value == ""){
     alert("预算申请人不能为空!");
     document.form1.budgetProposerName.focus();
     document.form1.budgetProposerName.select();
     return false;
  }
  if(document.form1.budgetItem.value == ""){
     alert("项目名称不能为空!");
     document.form1.budgetItem.focus();
     document.form1.budgetItem.select();
     return false;
  }
  if(document.form1.deptId.value == ""){
     alert("处室名称不能为空!");
     return false;
  }
  if($("budgetAvailabein").value!=''&&!isValidDateStr($("budgetAvailabein").value)){
    alert("有效日期格式不正确，形如：2010-07-01");
    document.form1.budgetAvailabein.focus();
    document.form1.budgetAvailabein.select();
    return false;
  }
  if(document.form1.budgetMoney.value == "") {
     alert("支出金额不能为空!");
     document.form1.budgetMoney.focus();
     document.form1.budgetMoney.select();
     return false;
  }
//去掉千位符号
  var re1 = /\,/gi;
  var budgetMoneyTemp = document.form1.budgetMoney.value.replace(re1,'');
  if(!isNumber(budgetMoneyTemp)){
   alert("支出金额应为数字!");
   document.form1.budgetMoney.focus();
   document.form1.budgetMoney.select();
    return false;
  }
  //判断支出金额是否过大
  if(parseFloat(budgetMoneyTemp,10)>=1000000000000000){
     alert("你输入的支出金额过大！");
     document.form1.budgetMoney.focus();
     document.form1.budgetMoney.select();
      return false;
  }
  //去掉千位符号
  var re1 = /\,/gi;
  var budgetInMoneyTemp = document.form1.budgetInMoney.value.replace(re1,'');
  if(document.form1.budgetInMoney.value!=''&&!isNumber(budgetInMoneyTemp)){
    alert("预收入金额应为数字!");
    document.form1.budgetInMoney.focus();
    document.form1.budgetInMoney.select();
     return false;
   }
  //判断预收入金额是否过大
  if(parseFloat(budgetInMoneyTemp,10)>=1000000000000000){
     alert("你输入的预收入金额过大！");
     document.form1.budgetInMoney.focus();
     document.form1.budgetInMoney.select();
      return false;
  }
  var available = getAvailable(year);
  var type = available.type;
  var availableMoney = available.availableMoney;
  //初始金额
  var budgetMoneyOld= 0;
  if(clon_edit=='3'){
    if($("budgetMoneyOld")){
      budgetMoneyOld =  $("budgetMoneyOld").value ;
    }
    availableMoney = parseFloat(availableMoney) + parseFloat(budgetMoneyOld); 
  }
  if(type == "2"){
	  if(Number(budgetMoneyTemp) > Number(availableMoney)  ){
		 alert("支出金额不能大于可用金额!\n可用金额为"+availableMoney);
		 document.form1.budgetMoney.focus();
		 document.form1.budgetMoney.select();
		 return false;
	  }
  }
 
  if(document.form1.deptAuditDirectorName.value == ""){
     alert("部门主管审批人不能为空!");
     document.form1.deptAuditDirectorName.focus();
     document.form1.deptAuditDirectorName.select();
     return false;
  }
	
  tb_output();
  return true;
}
function CheckMoney(moneyStr){
  var money  =  $(moneyStr).value;
  var re1 = /\,/gi;
  money = money.replace(re1,"");
  if(!isNumber(money)){
    alert("支出金额应为数字!");
    $(moneyStr).focus();
    $(moneyStr).select();
     return ;
   }
  money = insertKiloSplit(money,2);
 $("budgetMoney").value=money;
}
 
//检查数字类型
function check_value(obj){
   if(obj.value==""){
      return;
   }
   val=parseFloat(obj.value);
   if(isNaN(val)){
      alert("非法的数字");
      obj.focus();
      return;
   }
}
 

//支出预算函数
function tb_addnew(){
  if(checkFrom1()){
    var budgetMoney = document.getElementById('budgetMoney');//得到支出金额
    //去掉千位符号
    var re1 = /\,/gi;
    var budgetMoneyTemp = budgetMoney.value.replace(re1,'');
    if(budgetMoneyTemp==''){
      budgetMoneyTemp = 0;
    }
    //getNumber();
    if(document.form1.MODIFY_ROWINDEX.value!="-1"){//编辑
  	currRowIndex=Number(document.form1.MODIFY_ROWINDEX.value);
  	lv_tb_id="myTable";
  	if(document.form1._ITEM1.value==""){
  	  document.form1._ITEM1.value=document.form1._ITEM.value;
  	}
      $(lv_tb_id).rows[currRowIndex].cells[0].innerHTML=document.form1._ITEM1.value;
  	  $(lv_tb_id).rows[currRowIndex].cells[1].innerHTML=document.form1._NUMBER.value;
      $(lv_tb_id).rows[currRowIndex].cells[2].innerHTML=document.form1._PDATE.value;
      $(lv_tb_id).rows[currRowIndex].cells[3].innerHTML=document.form1._PRICE.value;
      $(lv_tb_id).rows[currRowIndex].cells[4].innerHTML=document.form1._MONEY.value;
      $(lv_tb_id).rows[currRowIndex].cells[5].innerHTML=document.form1._MEMO.value;
      var now_money = budgetMoneyTemp;
      if(document.form1._MONEY_.value!=""){
        now_money = parseFloat(budgetMoneyTemp) - parseFloat(changeMoney(document.form1._MONEY_.value));
      }
  	if(document.form1._MONEY.value!=""){
  	  budgetMoney.value = insertKiloSplit(parseFloat(now_money) + parseFloat(changeMoney(document.form1._MONEY.value)),2); //转换为带千位符号的
  	}
  	document.form1.MODIFY_ROWINDEX.value="-1";
  	document.form1._ITEM.value="";
  	document.form1._ITEM1.value="";
  	document.form1._NUMBER.value="";
  	document.form1._PDATE.value="";
  	document.form1._PRICE.value="";
  	document.form1._MONEY.value="";
  	document.form1._MEMO.value="";
  	document.form1._MONEY_.value="";
     }else{//新增
  	 if(document.form1._ITEM1.value==""){
  	   document.form1._ITEM1.value=document.form1._ITEM.value;
  	 }
   
       var mytable=$("myTable");
       rownum=mytable.rows.length;//得到行数
       //alert(mytable.rows.length);
       mynewrow = mytable.insertRow(rownum);
       //alert(mytable.rows.length);
       mynewrow.className="TableControl";

       var cellnum = mynewrow.cells.length;//得到列数
       mynewcell=mynewrow.insertCell(cellnum);
       mynewcell.align="center";
       cell_html=document.form1._ITEM1.value;
       mynewcell.innerHTML=cell_html;
       document.form1._ITEM1.value="";
       
       var cellnum = mynewrow.cells.length;
       mynewcell=mynewrow.insertCell(cellnum);
       mynewcell.align="center";
       cell_html=document.form1._NUMBER.value;
       mynewcell.innerHTML=cell_html;
       document.form1._NUMBER.value="";
       
       var cellnum = mynewrow.cells.length;
       mynewcell=mynewrow.insertCell(cellnum);
       mynewcell.align="center";
       cell_html=document.form1._PDATE.value;
       mynewcell.innerHTML=cell_html;
       document.form1._PDATE.value="";

       var cellnum = mynewrow.cells.length;
       mynewcell=mynewrow.insertCell(cellnum);
       mynewcell.align="center";
       cell_html=document.form1._PRICE.value;
       mynewcell.innerHTML=cell_html;
       document.form1._PRICE.value="";
    
       if(document.form1._MONEY.value!=""){//更改支出总额
         budgetMoney.value = insertKiloSplit(parseFloat(budgetMoneyTemp) + parseFloat(changeMoney(document.form1._MONEY.value)),2);
       }
       var cellnum = mynewrow.cells.length;
       mynewcell=mynewrow.insertCell(cellnum);
       mynewcell.align="center";
       cell_html=document.form1._MONEY.value;
       mynewcell.innerHTML=cell_html;
       document.form1._MONEY.value="";
       document.form1._MONEY_.value="";

       var cellnum = mynewrow.cells.length;
       mynewcell=mynewrow.insertCell(cellnum);
       mynewcell.align="center";
       cell_html=document.form1._MEMO.value;
       mynewcell.innerHTML=cell_html;
       document.form1._MEMO.value="";

       var cellnum = mynewrow.cells.length;
       mynewcell=mynewrow.insertCell(cellnum);
       mynewcell.align="center";
       cell_html="<a href=#this onclick=\"tb_edit(this);\">编辑</a> <a href=#this onclick=\"ke_long(this);\">克隆</a> <a href=#this onclick=\"tb_delete(this);\">清除</a>";
       mynewcell.innerHTML=cell_html;
    }
   }
}
function tb_delete(objra){
   var objTD =objra.parentNode;
   var objTR =objTD.parentNode;
   var money = objTR.cells[4].innerHTML;
   var budgetMoney = document.getElementById('budgetMoney');//得到支出金额
   document.form1.MODIFY_ROWINDEX.value ="-1";//删除后将参数为增加
      //去掉千位符号
   var re1 = /\,/gi;
   var budgetMoneyTemp = budgetMoney.value.replace(re1,'');
   if(budgetMoneyTemp==''){
     budgetMoneyTemp = 0;
   }
   if(money!=""){
     budgetMoney.value = insertKiloSplit(parseFloat(budgetMoneyTemp) - parseFloat(changeMoney(money,10)),2);//用支出金额-删除的这一行的支出金额
   }
   var objTable = objTR.parentNode;
   currRowIndex = objTR.rowIndex;
   var mytable=$("myTable");
   mytable.deleteRow(currRowIndex);//删除这一行

}
function ke_long(objra){
  var objTD =objra.parentNode;
  var objTR =objTD.parentNode;
  var objTable = objTR.parentNode;
  currRowIndex = objTR.rowIndex;
  document.form1.MODIFY_ROWINDEX.value=currRowIndex;
  var mytable=$("myTable");
   
  var length = mytable.rows.length;
  
  lv_tb_id="myTable";
  rownum=mytable.rows.length;
  mynewrow = mytable.insertRow(rownum);//新建一行
  mynewrow.className="TableControl";

  var cellnum = mynewrow.cells.length;
  mynewcell=mynewrow.insertCell(cellnum);

  mynewcell.align="center";
  cell_html=$(lv_tb_id).rows[currRowIndex].cells[0].innerHTML;//从这一行的第一列项目值

  mynewcell.innerHTML=cell_html;

  var cellnum = mynewrow.cells.length;
  mynewcell=mynewrow.insertCell(cellnum);
  mynewcell.align="center";
  cell_html=$(lv_tb_id).rows[currRowIndex].cells[1].innerHTML;//从这一行的第二列人数值

  mynewcell.innerHTML=cell_html;

  var cellnum = mynewrow.cells.length;
  mynewcell=mynewrow.insertCell(cellnum);
  mynewcell.align="center";
  cell_html=$(lv_tb_id).rows[currRowIndex].cells[2].innerHTML;//从这一行的第三列天数值

  mynewcell.innerHTML=cell_html;

  var cellnum = mynewrow.cells.length;
  mynewcell=mynewrow.insertCell(cellnum);
  mynewcell.align="center";
  cell_html=$(lv_tb_id).rows[currRowIndex].cells[3].innerHTML;//从这一行的第四列单价目值

  mynewcell.innerHTML=cell_html;
  
  var cellnum = mynewrow.cells.length;
  mynewcell=mynewrow.insertCell(cellnum);
  mynewcell.id = 'money_'  + (length - 1);
   
  
  mynewcell.align="center";
  cell_html=$(lv_tb_id).rows[currRowIndex].cells[4].innerHTML;//从这一行的第五列金额值

  var budgetMoney = $("budgetMoney");
  //去掉千位符号
  var re1 = /\,/gi;
  var budgetMoneyTemp = budgetMoney.value.replace(re1,'');
  if(budgetMoneyTemp==''){
    budgetMoneyTemp = 0;
  }
  
  if(cell_html!=""){
    budgetMoney.value = insertKiloSplit(parseFloat(budgetMoneyTemp) + parseFloat(changeMoney(cell_html)),2);
  }
  mynewcell.innerHTML=cell_html;

  var cellnum = mynewrow.cells.length;
  mynewcell=mynewrow.insertCell(cellnum);
  mynewcell.align="center";
  cell_html=$(lv_tb_id).rows[currRowIndex].cells[5].innerHTML;//从这一行的第六列备注值

  mynewcell.innerHTML=cell_html;

  var cellnum = mynewrow.cells.length;
  mynewcell=mynewrow.insertCell(cellnum);
  mynewcell.align="center";
  cell_html="<a href=#this onclick=\"tb_edit(this);\">编辑</a> <a href=#this onclick=\"ke_long(this);\">克隆</a> <a href=#this onclick=\"tb_delete(this);\">清除</a>";
  mynewcell.innerHTML=cell_html;
}
function CheckMoney(moneyStr){
  var money  =  $(moneyStr).value;
  var re1 = /\,/gi;
  money = money.replace(re1,"");
  if($(moneyStr).value!=''&&!isNumber(money)){
    alert("支出金额应为数字!");
    $(moneyStr).focus();
    $(moneyStr).select();
     return ;
   }
  money = insertKiloSplit(money,2);
 $(moneyStr).value=money;
}
function tb_edit(objra){
   var objTD =objra.parentNode;//和parentNode 差不多 
   //alert(objTD.innerHTML);//得到td 里面的所有（包括HTML文本）
   //objTD.innerText;//得到文本  如编辑 克隆 删除
   var objTR =objTD.parentNode;

   var objTable = objTR.parentNode;
   document.form1._MONEY_.value="";
  
   currRowIndex = objTR.rowIndex;
   document.form1.MODIFY_ROWINDEX.value=currRowIndex;
   var mytable=$("myTable");
   lv_tb_id="myTable";
   var data_str="";
   //项目 SELEC下拉框选中
   var temp = 2;
   for(i=0;i<document.form1._ITEM.options.length;i++){
     if(document.form1._ITEM.options[i].text==$(lv_tb_id).rows[currRowIndex].cells[0].innerHTML){
       document.form1._ITEM.selectedIndex=i;
       $("_ITEM1").style.display = "none";//填写项目隐藏
       temp = 1;
       break;
     }    
   }
   if(document.form1._ITEM.value == "" || document.form1._ITEM.value == '其他'||temp != 1)	{//填写项目显示
     $("_ITEM1").style.display = "";
     document.form1._ITEM.selectedIndex= 0 ;
     document.form1._ITEM1.value=$(lv_tb_id).rows[currRowIndex].cells[0].innerHTML;
   }
   //document.form1._ITEM1.value=$(lv_tb_id).rows(currRowIndex).cells(0).innerHTML;
   document.form1._NUMBER.value=$(lv_tb_id).rows[currRowIndex].cells[1].innerHTML;//人数
   document.form1._PDATE.value=$(lv_tb_id).rows[currRowIndex].cells[2].innerHTML;//天数 
   document.form1._PRICE.value=$(lv_tb_id).rows[currRowIndex].cells[3].innerHTML;//价格
   document.form1._MONEY.value=$(lv_tb_id).rows[currRowIndex].cells[4].innerHTML;//总额
   document.form1._MEMO.value=$(lv_tb_id).rows[currRowIndex].cells[5].innerHTML;//备注
   document.form1._MONEY_.value=$(lv_tb_id).rows[currRowIndex].cells[4].innerHTML;//隐藏总额
   
}
function tb_output(){
  lv_tb_id="myTable";
  var data_str="";
  for (i=1; i < $(lv_tb_id).rows.length; i++){
    for (j=0; j < $(lv_tb_id).rows[i].cells.length-1; j++){
       data_str+=$(lv_tb_id).rows[i].cells[j].innerHTML+"`~";
    }
    data_str+="\n";
  }
  document.form1.detailContent.value=data_str;
  
  lv_tb_id="MYTABLE_IN";
  var data_str="";
  for (i=1; i < $(lv_tb_id).rows.length; i++){
    for (j=0; j < $(lv_tb_id).rows[i].cells.length-1; j++){
      data_str+=$(lv_tb_id).rows[i].cells[j].innerHTML+"`~";
    }
    data_str+="\n";
  }
  document.form1.detailContentIn.value=data_str;
}
function getNumber(){
  if(isPositivInteger(document.form1._NUMBER.value) && isPositivInteger(document.form1._PDATE.value) && isNumber(changeMoney(document.form1._PRICE.value))){
	document.form1._MONEY.value=Number(document.form1._NUMBER.value)*Number(document.form1._PDATE.value)*Number(changeMoney(document.form1._PRICE.value));
  }
}
 
 
//收入预算方法相关

function getNumber_in(){
  if(isPositivInteger(document.form1._NUMBER_IN.value) && isPositivInteger(document.form1._PDATE_IN.value) && isNumber(changeMoney(document.form1._PRICE_IN.value))){
	document.form1._MONEY_IN.value=Number(document.form1._NUMBER_IN.value)*Number(document.form1._PDATE_IN.value)*Number(changeMoney(document.form1._PRICE_IN.value));
  }
}
 
function tb_addnew_in(){
  if(checkFrom2()){
    var budget_money = document.getElementById('budgetInMoney');
    var re1 = /\,/gi;
    var budgetMoneyTemp =budget_money.value.replace(re1,"");
    if(budgetMoneyTemp==''){
      budgetMoneyTemp = 0;
    }
    //总金额计算

    //getNumber_in();
    if(document.form1.MODIFY_ROWINDEX_IN.value!="-1"){//编辑
  	currRowIndex=Number(document.form1.MODIFY_ROWINDEX_IN.value);
  	lv_tb_id="MYTABLE_IN";
  	
  	if(document.form1._ITEM1_IN.value==""){
  	  document.form1._ITEM1_IN.value=document.form1._ITEM_IN.value;
  	}
  	$(lv_tb_id).rows[currRowIndex].cells[0].innerHTML=document.form1._ITEM1_IN.value;
  	$(lv_tb_id).rows[currRowIndex].cells[1].innerHTML=document.form1._NUMBER_IN.value;
    $(lv_tb_id).rows[currRowIndex].cells[2].innerHTML=document.form1._PDATE_IN.value;
    $(lv_tb_id).rows[currRowIndex].cells[3].innerHTML=document.form1._PRICE_IN.value;
    $(lv_tb_id).rows[currRowIndex].cells[4].innerHTML=document.form1._MONEY_IN.value;
    $(lv_tb_id).rows[currRowIndex].cells[5].innerHTML=document.form1._MEMO_IN.value;

    	//预收入总金额-本次编辑的（之前的金额）_MONEY__IN.value  隐藏字段 
    	
    var now_money = parseFloat(budgetMoneyTemp);
    if(document.form1._MONEY__IN.value!=''){
    	var now_money  = parseFloat(budgetMoneyTemp) - parseFloat(changeMoney(document.form1._MONEY__IN.value));
    }
  
  	if(document.form1._MONEY_IN.value!=""){//预收入总金额+本次编辑之后的
      budget_money.value = insertKiloSplit(parseFloat(now_money) + parseFloat(changeMoney(document.form1._MONEY_IN.value)),2); 
  	}
      //所有的值都清空
  	document.form1.MODIFY_ROWINDEX_IN.value="-1";
  	document.form1._ITEM_IN.value="";
  	document.form1._ITEM1_IN.value="";
  	document.form1._NUMBER_IN.value="";
  	document.form1._PDATE_IN.value="";
  	document.form1._PRICE_IN.value="";
  	document.form1._MONEY_IN.value="";
  	document.form1._MEMO_IN.value="";
      document.form1._MONEY__IN.value="";
    }else{//新建
  	if(document.form1._ITEM1_IN.value==""){
  	  document.form1._ITEM1_IN.value=document.form1._ITEM_IN.value;
  	}
   
      var mytable=$("MYTABLE_IN");
      rownum=mytable.rows.length;
      mynewrow = mytable.insertRow(rownum);//新建一行

      mynewrow.className="TableControl";

      var cellnum = mynewrow.cells.length;//新建一列
      mynewcell=mynewrow.insertCell(cellnum);
      mynewcell.align="center";
      cell_html=document.form1._ITEM1_IN.value;
      mynewcell.innerHTML=cell_html;
      document.form1._ITEM1_IN.value="";

      var cellnum = mynewrow.cells.length;//新建一列
      mynewcell=mynewrow.insertCell(cellnum);
      mynewcell.align="center";
      cell_html=document.form1._NUMBER_IN.value;
      mynewcell.innerHTML=cell_html;
      document.form1._NUMBER_IN.value="";

      var cellnum = mynewrow.cells.length;//新建一列
      mynewcell=mynewrow.insertCell(cellnum);
      mynewcell.align="center";
      cell_html=document.form1._PDATE_IN.value;
      mynewcell.innerHTML=cell_html;
      document.form1._PDATE_IN.value="";

      var cellnum = mynewrow.cells.length;//新建一列
      mynewcell=mynewrow.insertCell(cellnum);
      mynewcell.align="center";
      cell_html=document.form1._PRICE_IN.value;
      mynewcell.innerHTML=cell_html;
      document.form1._PRICE_IN.value="";
    
     if(document.form1._MONEY_IN.value!=""){
  	 budget_money.value = insertKiloSplit(parseFloat(budgetMoneyTemp) + parseFloat(changeMoney(document.form1._MONEY_IN.value)),2);
     }
     var cellnum = mynewrow.cells.length;//新建一列
     mynewcell=mynewrow.insertCell(cellnum);
     mynewcell.align="center";
     cell_html=document.form1._MONEY_IN.value;
     mynewcell.innerHTML=cell_html;
     document.form1._MONEY_IN.value="";
     document.form1._MONEY__IN.value="";
   
     var cellnum = mynewrow.cells.length;//新建一列
     mynewcell=mynewrow.insertCell(cellnum);
     mynewcell.align="center";
     cell_html=document.form1._MEMO_IN.value;
     mynewcell.innerHTML=cell_html;
     document.form1._MEMO_IN.value="";
     
     var cellnum = mynewrow.cells.length;//新建一列
     mynewcell=mynewrow.insertCell(cellnum);
     mynewcell.align="center";
     cell_html="<a href=#this onclick=\"tb_edit_in(this);\">编辑</a> <a href=#this onclick=\"ke_long_in(this);\">克隆</a> <a href=#this onclick=\"tb_delete_in(this);\">清除</a>";
     mynewcell.innerHTML=cell_html;
    }
  }
 
}
function tb_delete_in(objra){
 
   var objTD =objra.parentNode;
   var objTR =objTD.parentNode;
   var money = objTR.cells[4].innerHTML;
   var budget_money = document.getElementById('budgetInMoney');
   document.form1.MODIFY_ROWINDEX_IN.value = "-1"; //删除后将参数更改为添加;
   //去掉千位符号
   var re1 = /\,/gi;
   var budgetMoneyTemp =budget_money.value.replace(re1,"");
   if(budgetMoneyTemp==''){
     budgetMoneyTemp = 0;
   }
   if(money!=""){
	  budget_money.value = 	insertKiloSplit(parseFloat(budgetMoneyTemp) - parseFloat(changeMoney(money)),2);
   }
   var objTable = objTR.parentNode;
   currRowIndex = objTR.rowIndex;
   var mytable=$("MYTABLE_IN");
   mytable.deleteRow(currRowIndex);
}
function ke_long_in(objra){
   var objTD =objra.parentNode;
   var objTR =objTD.parentNode;
   var objTable = objTR.Node;
   currRowIndex = objTR.rowIndex;
   document.form1.MODIFY_ROWINDEX_IN.value=currRowIndex;
   var mytable=$("MYTABLE_IN");
   
  var length = mytable.rows.length;
  lv_tb_id="MYTABLE_IN";
  rownum=mytable.rows.length;
  mynewrow = mytable.insertRow(rownum);
  mynewrow.className="TableControl";

  var cellnum = mynewrow.cells.length;//新建一列
  mynewcell=mynewrow.insertCell(cellnum);
  mynewcell.align="center";
  cell_html=$(lv_tb_id).rows[currRowIndex].cells[0].innerHTML;
  mynewcell.innerHTML=cell_html;

  var cellnum = mynewrow.cells.length;//新建一列
  mynewcell=mynewrow.insertCell(cellnum);
  mynewcell.align="center";
  cell_html=$(lv_tb_id).rows[currRowIndex].cells[1].innerHTML;
  mynewcell.innerHTML=cell_html;

  var cellnum = mynewrow.cells.length;//新建一列
  mynewcell=mynewrow.insertCell(cellnum);
  mynewcell.align="center";
  cell_html=$(lv_tb_id).rows[currRowIndex].cells[2].innerHTML;
  mynewcell.innerHTML=cell_html;

  var cellnum = mynewrow.cells.length;//新建一列
  mynewcell=mynewrow.insertCell(cellnum);
  mynewcell.align="center";
  cell_html=$(lv_tb_id).rows[currRowIndex].cells[3].innerHTML;
  mynewcell.innerHTML=cell_html;
  

  var cellnum = mynewrow.cells.length;//新建一列
  mynewcell=mynewrow.insertCell(cellnum);
  mynewcell.id = 'money_'  + (length - 1)+'_in';
   
  
  mynewcell.align="center";
  cell_html=$(lv_tb_id).rows[currRowIndex].cells[4].innerHTML;
  
  var budget_money = document.getElementById('budgetInMoney');
  //去掉千位符号
  var re1 = /\,/gi;
  var budgetMoneyTemp =budget_money.value.replace(re1,"");
  if(budgetMoneyTemp==''){
    budgetMoneyTemp = 0;
  }
  if(cell_html!=""){
	budget_money.value =  insertKiloSplit(parseFloat(budgetMoneyTemp) + parseFloat(changeMoney(cell_html)),2);
  }
  mynewcell.innerHTML=cell_html;
  

  var cellnum = mynewrow.cells.length;//新建一列
  mynewcell=mynewrow.insertCell(cellnum);
  mynewcell.align="center";
  cell_html=$(lv_tb_id).rows[currRowIndex].cells[5].innerHTML;
  mynewcell.innerHTML=cell_html;

  var cellnum = mynewrow.cells.length;//新建一列
  mynewcell=mynewrow.insertCell(cellnum);
  mynewcell.align="center";
  cell_html="<a href=#this onclick=\"tb_edit_in(this);\">编辑</a> <a href=#this onclick=\"ke_long_in(this);\">克隆</a> <a href=#this onclick=\"tb_delete_in(this);\">清除</a>";
  mynewcell.innerHTML=cell_html;
  
}
function tb_edit_in(objra){
   var objTD =objra.parentNode;
   var objTR =objTD.parentNode;
   var objTable = objTR.parentNode;
   document.form1._MONEY__IN.value="";
   
   currRowIndex = objTR.rowIndex;
   document.form1.MODIFY_ROWINDEX_IN.value=currRowIndex;
   var mytable=$("MYTABLE_IN");
   lv_tb_id="MYTABLE_IN";
   var data_str="";
   var temp = 2;
  	for(i=0;i<document.form1._ITEM_IN.options.length;i++){
     if(document.form1._ITEM_IN.options[i].text==$(lv_tb_id).rows[currRowIndex].cells[0].innerHTML){
     	 document.form1._ITEM_IN.selectedIndex=i;
        $("_ITEM1_IN").style.display = "none";
     	 temp = 1;
     	 break;
     }  
   }
	if(document.form1._ITEM_IN.value == "" ||document.form1._ITEM_IN.value=='其他' || temp != 1){
     $("_ITEM1_IN").style.display = "";
     document.form1._ITEM_IN.selectedIndex = 0;
     document.form1._ITEM1_IN.value=$(lv_tb_id).rows[currRowIndex].cells[0].innerHTML;
	}
    //sel_change1($(lv_tb_id).rows(currRowIndex).cells(0).innerHTML);
 
   //document.form1._ITEM1.value=$(lv_tb_id).rows(currRowIndex).cells(0).innerHTML;
   document.form1._NUMBER_IN.value=$(lv_tb_id).rows[currRowIndex].cells[1].innerHTML;
   document.form1._PDATE_IN.value=$(lv_tb_id).rows[currRowIndex].cells[2].innerHTML;
   document.form1._PRICE_IN.value=$(lv_tb_id).rows[currRowIndex].cells[3].innerHTML;
   document.form1._MONEY_IN.value=$(lv_tb_id).rows[currRowIndex].cells[4].innerHTML;
   document.form1._MEMO_IN.value=$(lv_tb_id).rows[currRowIndex].cells[5].innerHTML;
   document.form1._MONEY__IN.value=$(lv_tb_id).rows[currRowIndex].cells[4].innerHTML;

}
function doOnload(){
  if(baTemp=='1'){
    var date1Parameters = {
        inputId:'budgetAvailabein',
        property:{isHaveTime:false}
        ,bindToBtn:'date1'
    };
    new Calendar(date1Parameters);
    var date1Parameters = {
        inputId:'deptAuditDate',
        property:{isHaveTime:false}
        ,bindToBtn:'date2'
    };
    new Calendar(date1Parameters);
    giftType("_ITEM");//得到预算支出项目类型 
    giftType("_ITEM_IN");//得到预算收入项目类型 
    if(type=='1'){
      alert("操作成功！");
      parent.opener.location.reload();
      window.close();
    }
    doOnloadFile();
  }
  bindKiloSplitPrcBatch(["budgetMoney", "budgetInMoney","_PRICE","_MONEY","_PRICE_IN","_MONEY_IN"], "form1");
}
function getAvailable(year){
  var requestUrl = "<%=contextPath%>/t9/subsys/oa/finance/act/T9BudgetApplyAct/selectAvailable.act?deptId="+deptId+"&year="+year;
  var rtJson = getJsonRs(requestUrl);
  if(rtJson.rtState == "1"){
    alert(rtJson.rtMsrg); 
    return ;
  }
  var prc = rtJson.rtData;
  return prc;
}
function doOnchange(){
  var notAffair = $("notAffair").value;
  if(notAffair=='1'){
     $("useAreaTR").style.display = "";
  }else{
    $("useArea").value = "";
    $("useAreaTR").style.display = "none";
  }

}
function checkFrom1(){
  var IsInt = "^[0-9]*[1-9][0-9]*$";//正整数　
  var IsInt2 =   "^-?\\d+$";//整数
  var re2 = new RegExp(IsInt2);
  if($("_NUMBER").value!=''&& $("_NUMBER").value.match(re2)==null){
     alert("人数应为整数！");
     $("_NUMBER").focus();
     $("_NUMBER").select();
     return false ;      
  }
  if($("_PDATE").value!=''&& $("_PDATE").value.match(re2)==null){
    alert("天数应为整数！");
    $("_PDATE").focus();
    $("_PDATE").select();
    return false ;       
  }
  if($("_MONEY").value==''){
    alert("金额是必填项！");
    $("_MONEY").focus();
    $("_MONEY").select();
    return false ;      
  }
  return true ;   
}
function checkFrom2(){
  var IsInt = "^[0-9]*[1-9][0-9]*$";//正整数　
  var IsInt2 =   "^-?\\d+$";//整数
  var re2 = new RegExp(IsInt2);
  if($("_NUMBER_IN").value!=''&& $("_NUMBER_IN").value.match(re2)==null){
     alert("人数应为整数！");
     $("_NUMBER_IN").focus();
     $("_NUMBER_IN").select();
     return false ;      
  }
  if($("_PDATE_IN").value!=''&& $("_PDATE_IN").value.match(re2)==null){
    alert("天数应为整数！");
    $("_PDATE_IN").focus();
    $("_PDATE_IN").select();
    return false ;       
  }
  if($("_MONEY_IN").value==''){
    alert("金额是必填项！");
    $("_MONEY_IN").focus();
    $("_MONEY_IN").select();
    return false ;    
  }
  return true ;   
}
function changeMoney(money){
  var re1 = /\,/gi;
  var moneyTemp =money.replace(re1,"");
  return moneyTemp; 
}
function giftType(_ITEM){
  var requestURL = "<%=contextPath%>/t9/subsys/oa/finance/act/T9BudgetApplyAct/getCodeItem.act?GIFT_PROTYPE=_ITEM_TYPE"; 
  var json = getJsonRs(requestURL); 
  //alert(rsText);
  if(json.rtState == '1'){ 
    alert(json.rtMsrg); 
    return ; 
  }
  var prcsJson = json.rtData;
  var selectObj = document.getElementById(_ITEM);
  for(var i = 0;i<prcsJson.length;i++){
    var prc = prcsJson[i];
    var seqId = prc.sqlId;
    var classNO = prc.classNo;
    var calssDesc = prc.classDesc;
    var myOption = document.createElement("option");
    myOption.value = calssDesc;
    myOption.text = calssDesc;
    selectObj.options.add(myOption, selectObj.options ? selectObj.options.length : 0);
  }
}
function toApplyBudgerInfo(){
  myleft = (screen.availWidth-800)/2;
  window.open("<%=contextPath %>/subsys/oa/finance/budget/plan/description/index.jsp","","status=0,toolbar=no,menubar=no,location=no,scrollbars=yes,resizable=yes,width=820,height=650,left="+myleft+",top=50");
} 

function toChangeMoney(){
  if($("_ITEM").value!='其他'){
    $("_ITEM1").style.display = "none";
    $("_ITEM1").value = "";
  }else{
    $("_ITEM1").style.display = "";
  }
}

function toChangeInMoney(){
  if($("_ITEM_IN").value!='其他'){
    $("_ITEM1_IN").style.display = "none";
    $("_ITEM1_IN").value = "";
  }else{
    $("_ITEM1_IN").style.display = "";
  }
}
      
</SCRIPT>
</head>
 
<body  style="margin:0;padding:0;" onload="doOnload();">
<div id="edit_body" style="overflow:auto;" SCROLL=auto>
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td><img src="<%=imgPath%>/edit.gif" WIDTH="22" HEIGHT="20" align="absmiddle"><span class="big3" color=blank> <%=titleDesc %>&nbsp;&nbsp;<a href="#"  onclick="toApplyBudgerInfo();" >预算标准说明</a></span>&nbsp;
    </td>
  </tr>
</table>

<%if(ba!=null){
if(clon_edit.equals("3")){ 
%>
<form action="<%=contextPath%>/t9/subsys/oa/finance/act/T9BudgetApplyAct/updateBudgetApply.act" method="post" name="form1" enctype="multipart/form-data" onSubmit="return checkForm();">
<% }else{ 
%>
<form action="<%=contextPath%>/t9/subsys/oa/finance/act/T9BudgetApplyAct/addBudgetApply.act?clon=1" method="post" name="form1" enctype="multipart/form-data" onSubmit="return checkForm();">
<% }
   String applyUser = ba.getBudgetProposer();
   String applyName = "";
   if(applyUser!=null&&!applyUser.equals("")){
     applyName = pl.getNameBySeqIdStr(applyUser,dbConn);
   }
   String dept = ba.getDeptId();
   String deptIdName = "";
   if(dept!=null&&!dept.equals("")){
     deptIdName = dl.getDeptName(dbConn,Integer.parseInt(dept));
   }
   java.util.Date useDate = ba.getBudgetAvailablein();
   String budgetAvailabein = "";
   if(useDate!=null&&!useDate.equals("")){
     budgetAvailabein = T9Utility.getDateTimeStr(useDate).substring(0,10);
   }
   String deptAudit = ba.getDeptAuditDirector();
   String deptAuditName = "";
   if(deptAudit!=null&&!deptAudit.equals("")){
     deptAuditName = pl.getNameBySeqIdStr(deptAudit,dbConn);
   }
   java.util.Date deptAuditDate = ba.getDeptAuditDate();
   String deptDateStr = "";
   if(deptAuditDate!=null&&!deptAuditDate.equals("")){
     deptDateStr =T9Utility.getDateTimeStr(deptAuditDate).substring(0,10);
   }
   String financeDir  = ba.getFinanceDirector();
   String financeDirName = "";
   if(financeDir!=null&&!financeDir.equals("")){
     financeDirName = pl.getNameBySeqIdStr(financeDir,dbConn);
   }
   java.util.Date financeDate = ba.getFinanceAuditDate();
   String financeDateStr = "";
   if(financeDate!=null&&!financeDate.equals("")){
     financeDateStr =T9Utility.getDateTimeStr(financeDate).substring(0,10);
   }
   String notAffair = ba.getNotAffair();
   String styleNotAffair = "";
   if(notAffair==null||(notAffair!=null&&notAffair.equals("0"))){
     notAffair = "0";
     styleNotAffair = "display:none";
   }
   String useArea = ba.getUseArea();
   String useAreaName = "";
   if(useArea!=null&&!useArea.equals("")){
     useAreaName =  pl.getNameBySeqIdStr(useArea,dbConn);
   }
   String attachmentId = "";
   String attachmentName = "";
   if(ba.getAttachmentId()!=null){
     attachmentId = ba.getAttachmentId();
   }
   if(ba.getAttachmentName()!=null){
     attachmentName = T9Utility.encodeSpecial(ba.getAttachmentName());
   }
   %>
<table width="90%" align="center" class="TableBlock">
    <tr>
      <td colspan=2 class="TableHeader"><img src="<%=imgPath%>/green_arrow.gif" align="absmiddle"> 预算信息 </td>
    </tr>
   <tr>
    <td nowrap class="TableContent">预算申请人：</td>
      <td class="TableData">
        <input type="hidden" id="budgetProposer" name="budgetProposer" value="<%=applyUser%>">
        <input type="text" id="budgetProposerName" name="budgetProposerName" size="20"  value="<%=applyName %>" class="BigStatic" maxlength="20"  value="" readonly>
        &nbsp;<input type="button" value="选 择" class="SmallButton" onClick="selectSingleUser(['budgetProposer','budgetProposerName']);" title="选择" name="button">
	  </td>
   </tr>    
   <tr>
      <td nowrap class="TableContent">项目名称：</td>
      <td class="TableData">
        <input type="text" name="budgetItem" id="budgetItem" size="30" maxlength="100" class="BigInput" value="<%=ba.getBudgetItem() %>">
      </td>
    </tr>
    <tr>
      <td nowrap class="TableContent">处室名称：</td>
      <td class="TableData"><%=deptIdName %>
			<input type="hidden" id="deptId" name="deptId" value="<%=dept%>">
      </td>
    </tr>
    <tr>
      <td nowrap class="TableContent">预算有效期：</td>
      <td class="TableData">
		 <input type="text" id="budgetAvailabein" name="budgetAvailabein" size="20" maxlength="19" class="BigInput" value="<%=budgetAvailabein %>" >
        <img id="date1" src="<%=imgPath%>/calendar.gif" align="absMiddle" border="0" align="absMiddle" style="cursor:pointer" >&nbsp;可以为空
      </td>
    </tr>
    <tr>
      <td nowrap class="TableContent">支出金额：</td>
      <td class="TableData" >
	  			<input type="text" name="budgetMoney" id="budgetMoney" value="<%=T9Utility.getFormatedStr(ba.getBudgetMoney(),0)%>" size="20" maxlength="16" />&nbsp;金额可以根据下面的预算详细信息自动生成
			<input type="hidden" id="Available" name="Available" value="0" />
			<input type="hidden" name="budgetMoneyOld" id="budgetMoneyOld" value="<%=ba.getBudgetMoney() %>" size="20" maxlength="23"/>
			
      </td>
    </tr>
    <tr>
      <td nowrap class="TableContent">预收入金额：</td>
      <td class="TableData" >
	  			<input type="text" name="budgetInMoney" id="budgetInMoney" value="<%=T9Utility.getFormatedStr(ba.getBudgetInMoney(),0) %>" size="20" maxlength="16"/>&nbsp;<font color="red">*仅供参考，请仔细核对！(允许为空)</font>
      </td>
    </tr>
    <tr>
      <td nowrap class="TableContent">预算类型：</td>
      <td class="TableData" >
	  <select id="notAffair" name='notAffair'  onchange="doOnchange();">
	  <option  value="0" >部门经费</option>
	  <%if(notAffair.equals("1")&&deptId==13){ %>
	  	<option value="1" selected="selected" >专项经费</option>
	  <%} else{
	  	if(deptId==13){ 
	    %>
	      	<option value="1" >专项经费</option>
	    <%
	  	}
	  }%>
		
	
	  </select>			
      </td>
    </tr>
     <tr style="<%=styleNotAffair %>" id="useAreaTR">
      <td nowrap class="TableContent">使用范围：</td>
      <td class="TableData" >
	    <input type="hidden" name="useArea" id="useArea" value="<%=useArea %>"  />
      <textarea name="useAreaDesc" id="useAreaDesc"  rows="2" cols="30" readonly="readonly" ><%=useAreaName %></textarea>
       <a href="javascript:;" class="orgAdd" onClick="selectUser(['useArea','useAreaDesc']);">添加</a>
       <a href="javascript:;" class="orgClear" onClick="$('useArea').value='';$('useAreaDesc').value='';">清空</a>		
      </td>
    </tr>
    <tr>
      <td nowrap class="TableContent">预算备注：</td>
      <td class="TableData">
        <textarea cols=50  id="memo" name="memo" rows="3" class="BigInput" wrap="yes"><%=ba.getMemo() %></textarea>
      </td>
    </tr>
 
  <tr>
    <td nowrap  class="TableHeader" colspan="4" align="left">
      描述：
    </td>
 </tr>
<tr>
  <td nowrap class="TableContent">资料附件： </td>
  <td nowrap class="TableData" colspan="3">
     <input type = "hidden" id="attachmentName" name="attachmentName" value="<%=attachmentName %>"></input>
       <input type = "hidden" id="attachmentId" name="attachmentId" value="<%=attachmentId %>"></input>
              	<span id="attr"></span>
</td>
  </tr>
  <tr height="25">
      <td nowrap class="TableData">附件选择：</td>
      <td class="TableData">
       <script>ShowAddFile();</script>
       <input type="hidden" id="ATTACHMENT_ID_OLD"  name="ATTACHMENT_ID_OLD" value="">
	   <input type="hidden" id="ATTACHMENT_NAME_OLD"  name="ATTACHMENT_NAME_OLD" value="">	   
      </td>
    </tr>
 
	<tr>
      <td colspan=2 class="TableHeader"><img src="<%=imgPath%>/green_arrow.gif" align="absmiddle"> 预算支出详情 </td>
    </tr>
 
   <tr>
    <td nowrap colspan="2">
    <table id="myTable" width="100%"  class="small">
        <tr  class="TableHeader"  align="center">
		   <td>项目</td>
           <td>人数</td>
           <td>天数</td>
           <td>（单价）标准</td>
           <td>金额</td>
           <td>备注</td>
           <td>操作</td>
        </tr>
        <%
         if(ba.getDetailContent()!=null&&!ba.getDetailContent().equals("")){
           
           String detailContent = ba.getDetailContent();
           String dcs[] = detailContent.split("\n");
           for(int i = 0; i<dcs.length;i++){
             String dc= dcs[i];
             String[] temp = dc.split("`~");
             String item = "";
             String person = "0";
             String days = "0";
             String price ="0.00";
             String money = "0.00";
             String memo = "";
              for(int j = 0; j<temp.length;j++){
                if(j==0){
                  item = temp[0];
                }
                if(j==1){
                  person = temp[1];
                }
                if(j==2){
                  days = temp[2];
                }
                if(j==3){
                  price = temp[3];
                }
                if(j==4){
                  money = temp[4];
                }
                if(j==5){
                  memo = temp[5];
                }
              }
             %> 
	         <tr class="TableControl" align="center">
            <td><%=item %></td>
            <td><%=person %></td>
            <td><%=days %></td>
            <td><%=price %></td>
            <td><%=money %></td>
            <td><%=memo %></td>
                 <td>
            	<a href=#this onclick="tb_edit(this);">编辑</a>
            	<a href=#this onclick="ke_long(this);">克隆</a>
            	<a href=#this onclick="tb_delete(this);">清除</a>
            	</td>
         </tr>
         <%}
         } %>
       </table>
       <br>
       项目：
        <select id="_ITEM" name="_ITEM"  class="small" onchange="toChangeMoney();" >
        <option value="其他">其他</option>
	</select>
		<input name="_ITEM1" id="_ITEM1" class="small" value="" size='10'>
       &nbsp;
       人数：
        <input id="_NUMBER" type="text" name="_NUMBER" value="" size='5'>&nbsp;
       &nbsp;
       天数：
        <input id="_PDATE" type="text" name="_PDATE" value="" size='5'>&nbsp;
       &nbsp;
       （单价）标准：
        <input id="_PRICE" type="text" name="_PRICE" value="" size='10'>&nbsp;
       &nbsp;
	   <br>
       金额：<input  type="hidden"  id="_MONEY_" name="_MONEY_" value="">
        <input id="_MONEY" type="text"  	  id="_MONEY" name="_MONEY" value="" size='25' onfocus="getNumber();">&nbsp;
       &nbsp;
       备注：
        <input type="text" name="_MEMO" value="" size='40'>&nbsp;
       &nbsp;
        <input type="button"  class="SmallButton" value="添加" onclick="tb_addnew();">
        <INPUT type="hidden" id="detailContent" name="detailContent" >
        <INPUT type="hidden"name="MODIFY_ROWINDEX" value="-1">
    </td>
   </tr>	
   
   <tr>
      <td colspan=2 class="TableHeader"><img src="<%=imgPath%>/green_arrow.gif" align="absmiddle"> 预算收入详情 (允许为空)</td>
    </tr>
 
   <tr>
    <td nowrap colspan="2">
    <table  id="MYTABLE_IN" width="100%"  class="small">
        <tr  class="TableHeader"  align="center">
		   <td>项目</td>
           <td>人数</td>
           <td>天数</td>
           <td>（单价）标准</td>
           <td>金额</td>
           <td>备注</td>
           <td>操作</td>
        </tr>
	<%        
   if(ba.getDetailContentIn()!=null&&!ba.getDetailContentIn().equals("")){
     String detailContentIn = ba.getDetailContentIn();
     String dcs[] = detailContentIn.split("\n");
     for(int i = 0; i<dcs.length;i++){
       String dc= dcs[i];
       String[] temp = dc.split("`~");
       String item = "";
       String person = "0";
       String days = "0";
       String price ="0.00";
       String money = "0.00";
       String memo = "";
        for(int j = 0; j<temp.length;j++){
          if(j==0){
            item = temp[0];
          }
          if(j==1){
            person = temp[1];
          }
          if(j==2){
            days = temp[2];
          }
          if(j==3){
            price = temp[3];
          }
          if(j==4){
            money = temp[4];
          }
          if(j==5){
            memo = temp[5];
          }
        }
       %> 
     <tr class="TableControl" align="center">
      <td><%=item %></td>
      <td><%=person %></td>
      <td><%=days %></td>
      <td><%=price %></td>
      <td><%=money %></td>
      <td><%=memo %></td>
            <td>
            	<a href=#this onclick="tb_edit_in(this);">编辑</a>
            	<a href=#this onclick="ke_long_in(this);">克隆</a>
            	<a href=#this onclick="tb_delete_in(this);">清除</a>
            	</td>
      
   </tr>
    <%}
   }
   %>
	       
       </table>
       <br>
       项目：
        <select id="_ITEM_IN" name="_ITEM_IN"  class="small" onchange="toChangeInMoney();">
        <option value="其他">其他</option>
		</select>
		<input name="_ITEM1_IN" class="small" id="_ITEM1_IN" value="" size='10'>
       &nbsp;
       人数：
        <input type="text" id="_NUMBER_IN" name="_NUMBER_IN" value="" size='5'>&nbsp;
       &nbsp;
       天数：
        <input type="text" id="_PDATE_IN" name="_PDATE_IN" value="" size='5'>&nbsp;
       &nbsp;
       （单价）标准：
        <input type="text" id="_PRICE_IN" name="_PRICE_IN" value="" size='10'>&nbsp;
       &nbsp;
	   <br>
       金额：<input type="hidden"  id="_MONEY__IN" name="_MONEY__IN" value="">
        <input type="text"  id="_MONEY_IN" name="_MONEY_IN" value="" size='25' onfocus="getNumber_in();">&nbsp;
       &nbsp;
       备注：
        <input type="text" name="_MEMO_IN" value="" size='40'>&nbsp;
       &nbsp;
        <input type="button"  class="SmallButton" value="添加" onclick="tb_addnew_in();">
        <INPUT type="hidden" id="detailContentIn" name="detailContentIn" >
        <INPUT type="hidden"name="MODIFY_ROWINDEX_IN" value="-1">
    </td>
   </tr>
 
    <tr>
      <td colspan=2 class="TableHeader"><img src="<%=imgPath%>/green_arrow.gif" align="absmiddle"> 部门主管审批 </td>
    </tr>
 
 
   <tr>
    <td nowrap class="TableContent">部门主管审批：</td>
      <td class="TableData">

	        <input type="hidden" id="deptAuditDirector" name="deptAuditDirector" value="<%=deptAudit %>">
        <input type="text" id="deptAuditDirectorName" name="deptAuditDirectorName" size="20" class="BigStatic" maxlength="20"  value="<%=deptAuditName %>" readonly>
        &nbsp;<input type="button" value="选 择" class="SmallButton" onClick="selectSingleUser(['deptAuditDirector','deptAuditDirectorName']);" title="选择" name="button">
	
	  </td>
   </tr>
	<tr>
		  <td nowrap class="TableContent"> 部门主管审批时间：</td>
		  <td class="TableData">
			<input type="text" id="deptAuditDate" name="deptAuditDate" size="20" maxlength="19" class="BigInput" value="<%=deptDateStr %>" readonly>
			<img id="date2" src="<%=imgPath%>/calendar.gif" align="absMiddle" border="0" align="absMiddle" style="cursor:pointer" >
		  </td>
	</tr>
    <tr>
      <td nowrap class="TableContent">  部门主管审批内容：</td>
      <td class="TableData">
        <textarea cols=50 id="deptAuditConent" name="deptAuditConent" rows="3" class="BigInput" wrap="yes"><%=ba.getDeptAuditContent() %></textarea>
      </td>
    </tr>
    <tr align="center" class="TableControl">
      <td colspan="2" nowrap>
	  	   <input type="hidden" name="budgetYear" id="budgetYear"   value="<%=ba.getBudgetYear() %>" >
	   <input type="hidden" name="seqId"  id="seqId" value="<%=ba.getSeqId() %>" >
	   <input type="submit"  value="保存" class="BigButton"  >&nbsp;&nbsp;
      <input type="button" value="关闭" class="BigButton" onclick="javascript:parent.opener.location.reload();  window.close()">&nbsp;
	</td>
	</tr>

   </table>
 		</form>
<%
 }else{
%>
  <table class="MessageBox" align="center" width="350">
  <tr>
    <td class="msg info">
      <div class="content" style="font-size:12pt">保存成功！</div>
    </td>
  </tr>
</table>
 <br>
<div align="center">
 <input type="button"  value="关闭" class="BigButton" onClick="javascript:parent.opener.location.reload(); window.close();">
</div>
  <%} %> 
</div>
</body>
</html>