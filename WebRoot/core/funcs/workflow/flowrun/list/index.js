var baseContentUrl = contextPath + "/core/funcs/workflow/flowrun/list";

function doInit(){
  loadFlowType();
  loadData(1, 5 , "");
}

function loadFlowType(){
  var url = contextPath+'/t9/core/funcs/workflow/act/T9FlowTypeAct/getFlowTypeJson.act';
  var json = getJsonRs(url);
  var rtData = json.rtData;   
  for(var i=0;i<rtData.length;i++) {      
    var opt=document.createElement("option");      
	opt.value=rtData[i].seqId;      
	opt.innerText=rtData[i].flowName;      
	$('flowList').appendChild(opt);    
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

function loadData(pageIndex , showLength , flowId){
  //$('freshLoad').className = "pgBtn pgLoad";
  $('pageIndex').value = pageIndex;
  $('pgSearchInfo').innerHTML = "加载数据中,请稍后.....";
  var par = "pageIndex="+pageIndex+"&showLength=" +showLength + "&flowId=" + flowId+"&typeStr=" + type;
  var url =contextPath+'/t9/core/funcs/workflow/act/T9MyWorkAct/getMyWorkList.act';
  var json = getJsonRs(url , par);
  
  if(json.rtState == "0"){
    var rtData = json.rtData;
    
    var pageData = rtData.pageData;
    var listData = rtData.listData;
    var pageCount = pageData.pageCount;
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
        addRow(data, i);
      }
    }
    
    $('freshLoad').className = "pgBtn pgRefresh"; 
  }else{
    document.body.innerHTML = json.rtMsrg;
  } 
}
function addEvent(pageIndex,pageCount){
  var pageLen = $F('pageLen');
  if(pageIndex == pageCount){
    $('pgNext').onclick = function(){};
    $('pgLast').onclick = function(){};
    $('pgPrev').onclick = function(){loadData(pageIndex - 1 , pageLen,$F('flowList'));};
    $('pgFirst').onclick = function(){loadData(1 , pageLen,$F('flowList'));};
  }else if(pageIndex == 1){
    $('pgPrev').onclick = function(){};
    $('pgFirst').onclick = function(){};
    $('pgNext').onclick = function(){loadData(pageIndex + 1 , pageLen,$F('flowList'));};
    $('pgLast').onclick = function(){loadData(pageCount , pageLen,$F('flowList'));};
  }else{
    $('pgNext').onclick = function(){loadData(pageIndex + 1 , pageLen,$F('flowList'));};
    $('pgLast').onclick = function(){loadData(pageCount , pageLen,$F('flowList'));};
    $('pgPrev').onclick = function(){loadData(pageIndex - 1 , pageLen,$F('flowList'));};
    $('pgFirst').onclick = function(){loadData(1 , pageLen,$F('flowList'));};
  }
}


function addRow(data , i){
  var par = "runId=" + data.runId + "&flowId=" + data.flowId + "&prcsId=" + data.prcsId + "&flowPrcs=" + data.flowPrcs;
  var flowName = "<a href='javascript:;' onclick='view_graph("+data.flowId+")'>" + data.flowName + "</a>";
  var runName = "<a href='javascript:;' onclick='form_view("+ data.runId +"," + data.flowId +")'>" + data.runName + "</a>";
  var prcsName = "<a href='javascript:;' onclick='flow_view("+ data.runId +"," + data.flowId +")'>第" + data.prcsId  + "步:" + data.runName + "</a>";
  var operate = "<a href='inputform/index.jsp?" + par + "'>"
                + "<img src='" + imgPath +"/edit.gif'>主办</a>"
                + "<a href='turn/turnnext.jsp?" + par + "'>"
                + "<img src='" + imgPath +"/flow_next.gif'>转交</a>"
                + "<a href='#' id=more_" + data.seqId + ">更多</a>"; 
  
  var td = "<td>" + data.runId +"</td>"
            + "<td>" + flowName +"</td>"
            + "<td>" + runName +"</td>"
            + "<td>" + data.userName  +"</td>"
            + "<td>" + prcsName +"</td>"
            + "<td>"+ operate  +"</td>";
  var className = "TableLine2" ;    
  if(i%2 == 0){
    className = "TableLine1" ;
  }
  var tr = new Element("tr" , {"class" : className}).update(td);
  $('dataBody').appendChild(tr);  
  
//  addMoreEvent($('more_' + data.seqId) , data.seqId); 
}
function addMoreEvent(el , seqId){
  var menu = [{name:'委托',action:trustAction , extData:seqId},{name:'导出',action:exportAction , extData:seqId},{name:'删除',action:delAction , extData:seqId}];
  el.onmouseover = function(event){
    var menu = new Menu({bindTo:el , menuData:menu , attachCtrl:true});
    menu.show(event);
  }
}

function trustAction(){
  var seqId = arguments[1]; 
}
function exportAction(){
  var seqId = arguments[1]; 
}
function delActionAction(){
  var seqId = arguments[1]; 
}
function form_view(RUN_ID,FLOW_ID)
{
  window.open(contextPath + "/t9/core/funcs/workflow/act/T9FlowFormPrintAct/getFormPrintPage.act?runId="+RUN_ID+"&flowId="+FLOW_ID,"","status=0,toolbar=no,menubar=no,width="+(screen.availWidth-15)+",height="+(screen.availHeight-30)+",location=no,scrollbars=yes,resizable=yes,left=0,top=0");
}


function flow_view(RUN_ID,FLOW_ID)
{
  myleft=(screen.availWidth-600)/2;
  window.open("../list/flow_view/index.jsp?RUN_ID="+RUN_ID+"&FLOW_ID="+FLOW_ID,RUN_ID,"status=0,toolbar=no,menubar=no,location=no,scrollbars=yes,resizable=yes,width=600,height=400,left="+myleft+",top=100");
}

function view_graph(FLOW_ID)
{
  myleft=(screen.availWidth-800)/2;
  window.open("../list/viewgraph/index.jsp?flowId="+FLOW_ID,"","status=0,toolbar=no,menubar=no,location=no,scrollbars=yes,resizable=yes,width=800,height=500,left="+myleft+",top=50");
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
  loadData(1, showLen , selectValue);
}
function selectLen(selectValue){
  $("pageIndex").value = "1";
  var flowId = $F("flowList");
  loadData(1, selectValue , flowId);
}


