<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp"%>
<%
String tranSeqId = request.getParameter("tranSeqId");
if(T9Utility.isNullorEmpty(tranSeqId)){
	tranSeqId = "0";
	
}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>处理待批申请</title>
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript">
function doInit(){
	var requestURI = "<%=contextPath%>/t9/subsys/oa/officeProduct/manage/act/T9OfficeTranshistoryAct";
	var url = requestURI + "/getTransDetailById.act";
	var rtJson = getJsonRs(url,"tranSeqId=<%=tranSeqId%>");
	//alert(rsText);
	if(rtJson.rtState == "0"){
		var data = rtJson.rtData;
		bindJson2Cntrl(rtJson.rtData);
		var transDate = data.transDate;
		if(transDate){
			$("transDate").innerHTML = transDate.substring(0,10);
		}
		//alert(data.transqty);
		if(data.transqty){
			$("transQtyStr").innerHTML = Math.abs(data.transqty);;
			$("transQty").value = Math.abs(data.transqty);
			//$("factQty").value = Math.abs(data.transqty);;
		}
		var proStock = data.proStock;
		var proMaxstock = data.proMaxstock;
		var proLowstock = data.proLowstock;
		//alert(data.transName);
		var infoStr = "";
		if(proStock*1 > proMaxstock*1){
			infoStr = "<font color=red>&nbsp;大于最高警戒库存</font>";
		}
		if(proStock*1 < proLowstock*1){
			infoStr = "<font color=red>&nbsp;小于最低警戒库存</font>";
		}
		$("proStockStr").innerHTML = data.proStock;
		$("infoStr").innerHTML = infoStr;
	}else{
		alert(rtJson.rtMsrg);
	}
}

function agree(){
	if(document.form1.setPriv[0].checked)   {
		document.getElementById("removePriv").style.display="none";
	}
}
function disagree(){
	if(document.form1.setPriv[1].checked){
		document.getElementById("removePriv").style.display="";
	}
}

function doSubmit(){
	var requestURI = "<%=contextPath%>/t9/subsys/oa/officeProduct/manage/act/T9OfficeTranshistoryAct";
	var pars = Form.serialize($('form1'));
	var url = requestURI + "/transHandle.act";
	var rtJson = getJsonRs(url,pars);
	if(rtJson.rtState == "0"){
		$("showFormDiv").hide();
		$("remindDiv").show();
	}else{
		alert(rtJson.rtMsrg);
	}
}


</script>
</head>
<body onload="doInit();">
<div id="showFormDiv">
<table border="0" width="50%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="Big"><img src="<%=imgPath %>/notify_new.gif" align="middle"><span class="big3">&nbsp;处理待批申请</span>
    </td>
  </tr>
</table>
<br>
<form  action="trans_handle.php"  method="post" name="form1" id="form1">
<table width="70%" align="center" class="TableBlock">
    <tr>
      <td nowrap class="TableData" width="30%">申请类型：</td>
      <td class="TableData">归还</td>
    </tr>
    <tr>
      <td nowrap class="TableData" width="30%">归还人：</td>
      <td class="TableData"><span id="borrowerName"></span></td>
    </tr>
    <tr>
      <td nowrap class="TableData" width="30%">归还时间：</td>
      <td class="TableData"><span id="transDate"></span></td>
    </tr>
    <tr>
      <td nowrap class="TableData">办公用品类别：</td>
      <td class="TableData"><span id="typeName"></span></td>
    </tr>
    <tr>
      <td nowrap class="TableData">办公用品名称：</td>
      <td class="TableData"><span id="proName"></span> (库存:<span id="proStockStr"></span> <span id="infoStr"></span>)</td>
      </tr>
    <tr>
    <tr>
      <td nowrap class="TableData">警戒库存范围：</td>
      <td class="TableData"><span id="proLowstock"></span>  至<span id="proMaxstock"></span> </td>
    </tr>
    <tr>
      <td nowrap class="TableData">单价：</td>
      <td class="TableData"><span id="proPrice"></span>元</td>
      </tr>
    <tr>
      <td nowrap class="TableData">归还数量：</td>
      <td class="TableData">
	      <input type="hidden" id="transQty" name="transQty" value="" >
	      <span id="transQtyStr" ></span>
      </td>
    </tr>
    <tr>
      <td nowrap class="TableData">处理：</td>
      <td class="TableData">
        <input type="radio" name="setPriv" id="radioAgree" value="1" onclick="agree();" checked><label for="radioAgree">同意</label>&nbsp;&nbsp;
        <input type="radio" name="setPriv" id="radioAgree" value="2" onclick="disagree();"><label for="radioAgree">不同意</label>
      </td>
    </tr>
    <tr id="removePriv" style="display:none;">
      <td nowrap class="TableData">不同意理由：</td>
      <td class="TableData"><input type="text" size="30" class="BigInput" name="removeReason" id="removeReason" value=""></td>
    </tr>
     <tr>
      <td nowrap class="TableData">申请备注：</td>
      <td class="TableData"> <span id="remark"></span></td>
    </tr>
    
    <tfoot align="center" class="TableFooter">
      <td colspan="2" nowrap>
		    <input type="hidden" name="OPERATOR_ID" value="<?=$LOGIN_USER_ID?>">
		    <input type="hidden" name="TRANS_ID" value="<?=$TRANS_ID?>">
		    <input type="hidden" name="TRANS_FLAG" value="<?=$TRANS_FLAG?>">
		    <input type="hidden" name="PRO_ID" value="<?=$PRO_ID?>">
		    <input type="hidden" name="TRANS_QTY" value="<?=$TRANS_QTY?>">
		    <input type="hidden" name="PRO_STOCK" value="<?=$PRO_STOCK?>">
		    
		   	<input type="hidden" name="transId" id="transId" value="<%=tranSeqId %>">
       	<input type="hidden" name="proId" id="proId" value="">
		    <input type="hidden" name="transFlag" id="transFlag" value="">
        <input type="button" value="处理"  onClick="doSubmit();" class="BigButton">&nbsp;&nbsp;
        <input type="button" value="返回" class="BigButton" onClick="javascript:history.go(-1)">
      </td>
    </tfoot>
  </table>
</form>
</div>
<div id="remindDiv" style="display: none">
<table class="MessageBox" align="center" width="320">
  <tr>
    <td class="msg info">
      <h4 class="title">提示</h4>
      <div class="content" style="font-size:12pt">待批申请处理成功！</div>
    </td>
  </tr>
</table>
<br><center>
	<input type="button" class="BigButton" value="返回" onclick="window.location.href='<%=contextPath %>/subsys/oa/officeProduct/manage/transInfo.jsp';">
</center>
</div>
</body>
</html>