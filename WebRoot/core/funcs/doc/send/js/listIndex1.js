window.getTextValue = getTextValue2;
function getTextValue2(data) {
//  var labels = ["拟搞","核搞","发文"];
  var label = "办理";
  if (data.flowType == '1') {
    if (data.opFlag == 1) {
      //if (data.flowPrcs <= labels.length) {
       // var i = data.flowPrcs - 1;
        label = data.prcsName;
          //labels[i];
      //}
      return label;
    } else {
      return  "会签";
    }
  } else {
    if (data.opFlag == 1) {
      return "办理";
    } else {
      return  "会签";
    }
  }
}
var nowPar = null;
window.flowNext = flowNext2;
function flowNext2(par , data) {
  if (data.flowType == 1) {
    var url = contextPath + "/t9/subsys/inforesouce/docmgr/act/T9DocAct/getState.act";
    var json = getJsonRs(url , "runId=" + data.runId + "&flowId=" + data.flowId + "&flowPrcs=" + data.flowPrcs);
    var flag = false;
    if (json.rtState == "0") {
      flag = json.rtData;
    }
    if (flag) {
      var url = contextPath + "/core/funcs/doc/flowrun/list/turn/turnnext.jsp?";
      var url2 = contextPath + "/subsys/inforesource/docmgr/roll/newFile.jsp?runId=" + data.runId  + "&prcsId=" + data.prcsId ;
      nowPar = par;
      if (top && top.T9 && top.T9.Window) {
        top.win = window;
        top.dd = new top.T9.Window({
          'title' : '归档',
          'draggable': false,
          'type': 'iframe',
          'width': 800,
          'height': 400,
          'modal':true,
          'src': url2
        });
        top.dd.show();
      } else {
        var resultValue = window.showModalDialog(url2,null,'dialogWidth=800px;dialogHeight=600px;help:no;status:no;');  
        if (resultValue) {
          closePage();
        }
      }
    } else {
      turn(par);
    }
  } else {
    turnFree(par);
  }
}
function closePage() {
  if (top && top.dd) {
    top.dd.destroy();
  }
  turn(nowPar);
}
