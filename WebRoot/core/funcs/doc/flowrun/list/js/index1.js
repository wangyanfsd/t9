var baseContentUrl = contextPath + moduleContextPath + "/flowrun/list";
var pageCount = 1 ;
var isIE=!!window.ActiveXObject;
var isIE6=isIE&&!window.XMLHttpRequest;
var tooltipMsg1 = "";
var tooltipMsg2 = "";
var tooltipMsg3 = "";
var tooltipMsg4 = "";

function doInit(){
  skinObjectToSpan(flowrun_list_index1);
  //并发处理
  //loadFlowType();
  //setTimeout(this.loadFlowType.bind(this),1);
  if(type != 3){
    loadData($F('pageIndex') , $F('pageLen') , '');
  }else{
    loadData($F('pageIndex') , $F('pageLen') , '');
  }
}
/**
 * 
 * @return
 */
function loadFlowType(){
  var url = contextPath+moduleSrcPath+'/act/T9FlowTypeAct/getFlowTypeJson.act?sortId=' + sortId;
  var json = getJsonRsAsyn(url , "", doFlowTypeJson);
  //$('flowList').value = flowType;
}
function doFlowTypeJson(json) {
  var rtData = json.rtData;   
  for(var i=0;i<rtData.length;i++) {      
    var opt=document.createElement("option");      
    opt.value=rtData[i].seqId;      
    opt.innerHTML = rtData[i].flowName;      
    if (flowType == opt.value) {
      opt.selected = true;
    }
//    $('flowList').appendChild(opt);    
  }   
}
function removeHeader() {
  var tr = $('flowTableHeader');
  var tds = tr.getElementsByTagName("td");
  for (var i = tds.length - 1 ;i >= 0 ;i--) {
    var td = tds[i];
    var name = td.getAttribute("name");
    if (name == "formHeader") {
      tr.removeChild(td);
    }
  }
}
function addHeader(headers) {
  var tr = $('flowTableHeader');
  var td2 = $('operate');
  var tds = headers.split(",");
  for (var i = 0 ;i < tds.length ;i++) {
    var tmp = tds[i];
    var td = new Element("td" , {"class":"TableHeader"});
    td.setAttribute("name","formHeader");
    td.align = 'center';
    td.update(tmp);
    if (td2) {
      tr.insertBefore(td,td2);
    }else {
      tr.appendChild(td);
    }
  }
}
/**
 * 返加数据格式:{pageData:{pageCount:20 , recordCount : 100, pgStartRecord : 1 , pgEndRecord:40}
 * ,listData:[{runId:1, flowId:2, flowName :'111' , runName :'ddd' , userId:'ddd' , userName :'sss' , prcsId:1 , prcsName:'aaa' ,flowPrcs:4}
 * ,{runId:1, flowId:2, flowName :'111' , runName :'ddd' , userId:'ddd' , userName :'sss' , prcsId:1 , prcsName:'aaa' ,flowPrcs:4}
 * ,{runId:1, flowId:2, flowName :'111' , runName :'ddd' , userId:'ddd' , userName :'sss' , prcsId:1 , prcsName:'aaa' ,flowPrcs:4}
 * ]}
 * @param pageIndex
 * @param showLength
 * @param flowId
 * @return
 */

function loadData(pageIndex , showLength , flowId , opFlag){
  if(pageCount < pageIndex){
    alert('页码超过了总页数!');
    //重置页码为1;
    pageIndex = 1;
    $('pageIndex').value = pageIndex;
  }
  if (!pageIndex) {
    pageIndex = 1;
    $('pageIndex').value = pageIndex;
  }
  $('freshLoad').className = "pgBtn pgLoad";
  $('pgSearchInfo').innerHTML = "加载数据中,请稍后.....";
  var par = "";
  if(type != 3){
    par = "pageIndex="+pageIndex+"&showLength=" +showLength + "&flowId=" + flowId+"&typeStr=" + type;
  }else{
    par = "pageIndex="+pageIndex+"&showLength=" +showLength + "&flowId=" + flowId+"&typeStr=" + type + "&opFlag=" + opFlag;
  }
  var date1 = new Date();
  var url =contextPath+moduleSrcPath+'/act/T9MyWorkAct/getMyWorkList.act?sortId=' + sortId;
  var json = getJsonRs(url , par);
  var date2 = new Date();
  $('endTime').update(date2);
  $('useTime').update((date2.getTime() - date1.getTime())/1000 + "秒");
  
  if(json.rtState == "0"){
    var rtData = json.rtData;
    
    var title =  json.rtMsrg;
    //移出标题
    removeHeader();
    if (title) {
      addHeader(title);
    } 
    var pageData = rtData.pageData;
    var listData = rtData.listData;
    pageCount = pageData.pageCount;
    var recordCount = pageData.recordCount;
    var pgStartRecord = pageData.pgStartRecord;
    var pgEndRecord = pageData.pgEndRecord;
    var pgSearchInfo = "共&nbsp;"+ recordCount +"&nbsp;条记录，显示第&nbsp;<span class=\"pgStartRecord\">"+pgStartRecord+"</span>&nbsp;条&nbsp;-&nbsp;第&nbsp;<span class=\"pgEndRecord\">"+pgEndRecord+"</span>&nbsp;条记录";
    $('pgSearchInfo').innerHTML = pgSearchInfo;
    $('pageCount').innerHTML = pageCount;
    if(pageIndex == pageCount){
      $('pgNext').className = "pgBtn pgNext pgNextDisabled";
      $('pgLast').className = "pgBtn pgLast pgLastDisabled";
    }else{
      $('pgNext').className = "pgBtn pgNext pgNext";
      $('pgLast').className = "pgBtn pgLast pgLast";
    }
    if(pageIndex == 1){
      $('pgPrev').className = "pgBtn pgPrev pgPrevDisabled";
      $('pgFirst').className = "pgBtn pgFirst pgFirstDisabled";
    }else{
      $('pgPrev').className = "pgBtn  pgPrev pgPrev";
      $('pgFirst').className = "pgBtn pgFirst pgFirst";
    }
 
    addEvent( pageIndex , pageCount);
    removeAllChildren($('dataBody'));
    
    if(listData.length > 0){
      for(var i = 0 ;i < listData.length ;i ++){
        var data = listData[i];
        var isLast = false;
        if ((listData.length - 1)  == i) {
          isLast = true;
        }
        addRow(data, i , isLast , title);
      }
    }else{
      $('hasData').hide();
      $('noData').show();
//      if($F('flowList') != '0'){
//        $('msgInfo').update(tooltipMsg1);
//      }else{
        $('msgInfo').update(tooltipMsg2);
//      }
      //如果是ie6的话，select隐藏不掉问题
      if (isIE6) {
//        var select = $('flowList');
//        var parentNode = select.parentNode;
//        if (parentNode) {
//          parentNode.removeChild(select);
//        }
        var select2 = $("opFlag");
        if (select2) {
          var parentNode = select2.parentNode;
          if (parentNode) {
            parentNode.removeChild(select2);
          }
        }
      }
    }
    $('freshLoad').className = "pgBtn pgRefresh"; 
  }else{
    document.body.innerHTML = json.rtMsrg;
  } 
}
function addEvent(index,pageCount){
  var pageLen = $F('pageLen');
  var pageIndex = parseInt(index);
  if(pageIndex == pageCount){
    $('pgNext').onclick = function(){};
    $('pgLast').onclick = function(){};
    $('pgPrev').onclick = function(){
        $('pageIndex').value = pageIndex - 1;
        if(type != 3){
          loadData(pageIndex - 1 , pageLen,'');
        }else{
          loadData(pageIndex - 1 , pageLen,'' ,$F('opFlag'));
        }
      };
    $('pgFirst').onclick = function(){
      $('pageIndex').value = 1;
      if(type != 3){
        loadData(1 , pageLen,'');
      }else{
        loadData(1 , pageLen,'',$F('opFlag'));
      }
    };
  }else if(pageIndex == 1){
    $('pgPrev').onclick = function(){};
    $('pgFirst').onclick = function(){};
    $('pgNext').onclick = function(){
      $('pageIndex').value = pageIndex + 1;
      if(type != 3){
        loadData(pageIndex + 1 , pageLen,'');
      }else{
        loadData(pageIndex + 1 , pageLen,'',$F('opFlag'));
      }
    };
    $('pgLast').onclick = function(){
      $('pageIndex').value = pageCount;
      if(type != 3){
        loadData(pageCount , pageLen,'');
      }else{
        loadData(pageCount , pageLen,'',$F('opFlag'));
      }
    };
  }else{
    $('pgNext').onclick = function(){
      $('pageIndex').value = pageIndex + 1;
      if(type != 3){
        loadData(pageIndex + 1 , pageLen,'');
      }else{
        loadData(pageIndex + 1 , pageLen,'',$F('opFlag'));
      }
    };
    $('pgLast').onclick = function(){
      $('pageIndex').value = pageCount;
      if(type != 3){
        loadData(pageCount , pageLen,'');
      }else{
        loadData(pageCount , pageLen,'',$F('opFlag'));
      }
    };
    $('pgPrev').onclick = function(){
      $('pageIndex').value = pageIndex - 1;
      if(type != 3){
        loadData(pageIndex - 1 , pageLen,'');
      }else{
        loadData(pageIndex - 1 , pageLen,'' ,$F('opFlag'));
      }
    };
    $('pgFirst').onclick = function(){
      $('pageIndex').value = 1;
      if(type != 3){
        loadData(1 , pageLen,'');
      }else{
        loadData(1 , pageLen,'',$F('opFlag'));
      }
    };
  }
}
function getTextValue(data) {
  if (data.opFlag == 1) {
    return data.prcsName;
  } else {
    return "会签";
  }
}
function addRow(data , i , isLast , title){
  var prcsNameTemp = data.prcsName;
  var par = "runId=" + data.runId + "&flowId=" + data.flowId + "&prcsId=" + data.prcsId + "&flowPrcs=" + data.flowPrcs + "&sortId=" + sortId + "&skin=" + skin;
  var flowName = "";
  if (data.flowType == 1) {
    flowName = "<a href='javascript:;' onclick='viewGraph("+data.flowId+")'>" + data.flowName + "</a>";
  } else {
    flowName = data.flowName; 
  }
  var runName = data.title;
  if (runName.length > 25) {
    runName = runName.substring(0 , 25) + "...";
  }
  runName = "<a href='javascript:;' onclick='formView("+ data.runId +"," + data.flowId +")'>" + runName + "</a>";
  var prcsName = "";
  if(type == 3){
    var str = (data.opFlag == 1 ? "(主办)" : "(经办)");
    tmp =  "<font color=red>" + str + "</font>";
    prcsName = "<a href='javascript:;' title='第" + data.prcsId  + "步:"+ data.prcsName +"' onclick='"
    + "flowView("+ data.runId +"," + data.flowId +",\"\",\""+ sortId +"\",\""+skin+"\")"
    +"'>"+ tmp +"第" + data.prcsId  + "步:" + data.prcsName + "</a>";
  } else {
    prcsName = "<a href='javascript:;' title='第" + data.prcsId  + "步:"+ data.prcsName +"' onclick='"
    + "flowView("+ data.runId +"," + data.flowId +",\"\",\""+ sortId +"\",\""+skin+"\")"
    +"'>第" + data.prcsId  + "步:" + data.prcsName + "</a>";
  }
  var operate = "";
  if(type != 3){
    var isOp = getTextValue(data);
    var turn = "";
    if (data.opFlag == 1 && type == 2) {
      //如果是固定流程
      if (data.flowType == 1) {
        //turn = "<a id='turnnext-"+ data.runId +"' href='javascript:void(0)'><img src='" + imgPath +"/flow_next.gif'>下一步</a>&nbsp;&nbsp;"; 
      } else {
       // turn = "<a id='turnnext-"+ data.runId +"' href='javascript:void(0)'>"
        //  + "<img src='" + imgPath +"/flow_next.gif'>下一步"
        //  + "</a>";
        turn = "&nbsp;&nbsp;<a href='javascript:' onclick='stop(\""+ par +"\")'/>结束</a>&nbsp;&nbsp;";
      }
    }
    var hasEntrust = false ;
    //如果是主办并且不禁止委托
    if ((data.opFlag == 1 
        && data.freeOther == 1) || data.freeOther == 2 || data.freeOther == 3){
      hasEntrust = true;
    }
    
    var hasDelete = data.isHaveDelPriv;
    operate = "<a href='javascript:;' onclick='formView("+ data.runId +"," + data.flowId +")'>查看</a>&nbsp;&nbsp;<a target='_parent' href='inputform/index.jsp?" + par + "'>"
                + "<img src='" + imgPath +"/edit.gif'>"+ isOp +"</a>&nbsp;&nbsp;"
                + turn
                + "<a href='javascript:;' id='more-" + data.seqId + "' onmouseover='createDiv(event,\""+ data.seqId +"\" , "+ data.flowType +" ,"+ data.runId +" , "+ hasEntrust +" ,"+ hasDelete +",\""+ data.sendFlag +"\", \""+ par +"\")'>更多<img align='absMiddle' src='"+ imgPath +"/menu_arrow_down.gif'/></a>&nbsp;</td>";
  }else{
    var url = contextPath +moduleSrcPath+"/act/T9FlowExportAct/exportFlowZip.act?runIdStr=" + data.runId;
    operate = "<a href='javascript:' onclick='window.open(\""+ url +"\")'>导出</a>";
    if (data.calBackPriv && data.state != "已结束") {
      operate = "<a href='javascript:' onclick='callBack(\""+ par +"\")'>收回</a>&nbsp;&nbsp;" + operate;
    }
    if (data.isHaveDelPriv) {
      operate += "&nbsp;&nbsp;<a href='javascript:' onclick='deleteRun("+ data.runId +")'>作废</a> " ;
    }
  }
  var td = "";
  var tip = "流程类型：" ;
  if (data.flowType == '1') {
    tip += "固定流程";
  } else {
    tip += "自由流程";
  }
  tip += "&#10;流程名：" + data.flowName;
  var fields = getFieldsValue(title , data);
  
  if (!data.sendNo || data.sendNo == "null"
    || data.sendNo == "undefined") {
    data.sendNo = "";
  }
  if (!data.writtenTime|| data.writtenTime == "null"|| data.writtenTime == "undefined") {
    data.writtenTime = "";
  }
  if (!data.miji|| data.miji == "null"|| data.miji == "undefined") {
    data.miji = "";
  }
  
  if (!data.jinji|| data.jinji == "null"|| data.jinji == "undefined") {
    data.jinji   = "";
  }
  if(type != 3){
    td = 
//    	"<td align=center>" + data.runId +"</td>"
//            + "<td class=auto title='" + tip + "'>" + flowName +"</td>"
             "<td  class=auto title='" + data.title + "'>" + runName +"</td>"
            + "<td align=center>" + data.userName  +"</td>"
            + "<td align=center>" + data.sendNo + "</td>"
            + "<td align=center>" + data.writtenTime + "</td>"
            + "<td align=center>" + data.miji + "</td>"
            + "<td align=center>" + data.jinji + "</td>"
//            + "<td align=center>" + data.prcsName + "</td>"
//            + "<td  class=auto title='" + prcsNameTemp + "'>" + prcsName +"</td>"
            + fields
            + "<td>"+ operate  +"</td>";
  }else{
    var state =  "";
    if (data.state != "已结束") {
      state = "<font color=green>" + data.state + "</font>";
    } else {
      state =  "<font color=red>" + data.state + "</font>";
    }
    var recvInfo = "<a href='javascript:;' onclick='formView("+ data.runId +"," + data.flowId +")'>查看</a>&nbsp;&nbsp;";
    if(data.sendFlag == "1"){
      recvInfo += "<a href='javascript:;' onclick='recvInfo("+data.runId+")'>收文情况</a>";
    }
    var str = "";
    if (data.docId) {
      var imgStr = getAttachImage("doc");
      var sss =data.docName;
      if (sss.length > 10) {
        sss = sss.substring(0 , 10) + "...";
      }
       str = "<a href='javascript:void(0)' onclick='return false;' onmouseover='createDiv2(event  ,this , \""+ data.docId +"\" , \""+ data.docName +"\" , \"doc\")'><img src='" + imgStr + "'/>&nbsp;" +sss  + "</a>";
    } 
    td = "<td align=center title='" + data.title + "'>" + runName +"</td>"
            + "<td align=center>" + data.userName +"</td>"
           // + "<td align=center>" + data.fileWord +"</td>"
            + "<td align=center>" + data.sendNo  +"</td>"
            + "<td align=center>" + data.writtenTime +"</td>"
            + "<td align=center>" + data.jinji  +"</td>"
            + "<td align=center>" + str  +"</td>"
           // + "<td align=center>" + data.nowUser +"</td>"
            //+ "<td align=center>" + data.prcsNameNext +"</td>"
            + "<td align=center>" + recvInfo +"</td>"
//            + "<td  align=center>" + state +"</td>"+ fields;
    
//    if (!noOperate) {
//      td += "<td >&nbsp;&nbsp;"+ operate  +"</td>";
//    }
  }
  var className = "TableLine2" ;    
  if(i%2 == 0){
    className = "TableLine1" ;
  }
  
  var tr = new Element("tr" , {"class" : className});
  $('dataBody').appendChild(tr);  
  tr.update(td);
  var a = $('turnnext-' + data.runId);
  if (a) {
    a.onclick = function() {
      flowNext(par , data);
    }
  }
}
function office(attachmentName,attachmentId,moudle,op,signKey,print){
  //print = print ? "" : "1";
  var param = "attachmentName=" + encodeURIComponent(attachmentName)
     + "&attachmentId=" + attachmentId 
     + "&moudle=" + moudle 
     + "&op=" + op 
     + "&signKey=" + signKey 
     + "&print=" + print ; 
  var url = contextPath + "/core/funcs/office/ntko/indexNtko.jsp?" + param;
  //url = encodeURIComponent(url);
  openWindow(url,'在线编辑',900,600);
}

/**
* 创建右建菜单
* 
*/
function createDiv2(event , node , attachId , attachName , ext){
  var menuD = [];
 var read = { attachmentId:attachId , attachmentName: attachName ,name:'<div  style="padding-top:5px;margin-left:10px">阅读<div>',action:readAction,extData: ""};
 menuD.push( read );
 var divStyle = {border:'1px solid #69F',width:'100px',position :'absolute',backgroundColor:'#FFFFFF',fontSize:'10pt',display:"block"};
 var menu = new Menu({bindTo:node , menuData:menuD , attachCtrl:true},divStyle);
 menu.show(event);
}
function getFieldsValue(title , data) {
  if (!title) {
    return "";
  }
  var titles = title.split(",");
   var tmp = "";
  for (var i = 0 ;i < titles.length ;i++) {
    var ti = titles[i];
    tmp += "<td align=center>" + data[ti]  +"</td>";
  }
  return tmp;
}
function flowNext(par , data) {
  if (data.flowType == 1) {
    turn(par);
  } else {
    turnFree(par);
  }
}
function turnFree(par) {
  url = contextPath + "/core/funcs/workflow/flowrun/list/turn/turnnextfree.jsp?";
  url +=  par;
  parent.location.href = url;
}
function turn(par) {
  var url = contextPath + "/core/funcs/workflow/flowrun/list/turn/turnnext.jsp?" + par;
  parent.location.href = url;
}

/**
 * 创建菜单
 * @param event
 * @param seqId
 * @param flowType
 * @param hasEntrust 是否有委托项
 * @return
 */
function createDiv(event , seqId , flowType , runId , hasEntrust , hasDelete , sendFlag , par){
  var menuData = new Array();
  if (hasEntrust) {
   // menuData.push({ name:'<div  style="padding-top:5px;margin-left:10px">委托<div>',action:trustAction ,extData:flowType + ":" + par});
  }
  menuData.push({ name:'<div style="padding-top:5px;margin-left:10px">导出<div>',action:exportAction,extData:par});
  if (hasDelete) {
    menuData.push({ name:'<div  style="padding-top:5px;margin-left:10px">作废<div>',action:delAction,extData:runId});
  }
  if (sendFlag == '1') {
    menuData.push({ name:'<div  style="padding-top:5px;margin-left:10px">收文情况<div>',action:recvInfoAction,extData:runId});
  }
  var divStyle = {border:'1px solid #69F',width:'90px',position :'absolute',backgroundColor:'#FFFFFF',fontSize:'10pt',display:"block"};
  var menu = new Menu({bindTo:$('more-' + seqId) , menuData:menuData , attachCtrl:true},divStyle);
  menu.show(event);
}
 function recvInfoAction() {
   var runId =  arguments[2]; 
   recvInfo(runId);
 }
/**
 * 委托
 * @return
 */
function trustAction(){
  var par = arguments[2];
  var aPar =  par.split(":");
  var flowType = aPar[0];
  var sPar = aPar[1];
  //alert(sPar);
  var page = "others";
  if (flowType == '2') {
    page="othersfree";
  }
  var url = contextPath + moduleContextPath + "/flowrun/list/others/"+page+".jsp?"+ sPar;
  myleft=(screen.availWidth-700)/2;
  mytop=(screen.availHeight-450)/2;
  window.open(url,"others","status=0,toolbar=no,menubar=no,width=700,height=450,location=no,scrollbars=yes,resizable=no,left="+myleft+",top="+mytop);
  //showModalWindow(url , "工作委托" , "others" ,800,400);
}
function exportAction(){
  var par = arguments[2]; 
  var url = contextPath +moduleSrcPath+"/act/T9FlowExportAct/exportFlowZip.act?" + par;
  window.open(url);
}
function delAction(){
  var runId = arguments[2]; 
  deleteRun(runId);
}


function removeAllChildren(parentNode){
  parentNode = $(parentNode);
  while(parentNode.firstChild){
    var oldNode = parentNode.removeChild(parentNode.firstChild);
    oldNode = null;
  }
}
function selectFlow(selectValue){
  $("pageIndex").value = "1";
  var showLen = $F("pageLen");
  if(type != 3){
    loadData(1, showLen , selectValue);
  }else{
    var opFlag = $F('opFlag');
    loadData(1, showLen , selectValue , opFlag);
  }
}
function selectLen(selectValue){
  $("pageIndex").value = "1";
  var flowId = '';
  if(type != 3){
    loadData(1, selectValue , flowId);
  }else{
    var opFlag = $F('opFlag');
    loadData(1, selectValue , flowId , opFlag);
  }
}
function selectOpFlag(selectValue){
  $("pageIndex").value = "1";
  var flowId = '';
  var showLen = $F("pageLen");
  loadData(1, showLen , flowId , selectValue); 
}
function deleteRun(runId) {
  if (window.confirm(tooltipMsg5)) {
    var url = contextPath+moduleSrcPath+'/act/T9MyWorkAct/delRun.act';
    var json = getJsonRs(url , "runId=" + runId) ;
    if (json.rtState == '0' ) {
      alert(json.rtMsrg) ;
      location.reload();
    } else {
      alert(json.rtMsrg) ;
    }
  }
}
function callBack(par){
  if(window.confirm(tooltipMsg3)) {
    var url = contextPath+moduleSrcPath+'/act/T9MyWorkAct/callBack.act';
    var json = getJsonRs(url , par);
    if (json.rtState == '0') {
      alert(json.rtMsrg);
      location.reload();
    } else {
      alert(json.rtMsrg);
    }
  }
}
function stop(par){
  if(window.confirm(tooltipMsg4)) {
    var url = contextPath+moduleSrcPath+'/act/T9FreeFlowTypeAct/stop.act';
    var json = getJsonRs(url , par);
    if (json.rtState == '0') {
      alert(json.rtMsrg);
      location.reload();
    } else {
      alert(json.rtMsrg);
    }
  }
}

function recvInfo(runId){
  var URL = contextPath + "/core/funcs/doc/send/send.jsp?runId="+runId;
  newWindow(URL,'800', '500');
}


function newWindow(url,width,height){
  var locX=(screen.width-width)/2;
  var locY=(screen.height-height)/2;
  window.open(url, "meeting", 
      "height=" +height + ",width=" + width +",status=1,toolbar=no,menubar=no,location=no,scrollbars=yes, top=" 
      + locY + ", left=" + locX + ", resizable=yes");
}