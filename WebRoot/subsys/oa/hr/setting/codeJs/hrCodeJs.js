function getCodeById(seqId){//根据SeqId
  var url = contextPath + "/t9/subsys/oa/hr/setting/act/T9HrCodeAct/selectCodeById.act?seqId="+seqId;
  var rtJson = getJsonRs(url);
  if (rtJson.rtState != "0") {
    alert(rtJson.rtMsrg);
    return;
  }
  var prc = rtJson.rtData;
  return prc;
}

function getCode(){//得到所有父级code
  var url = contextPath + "/t9/subsys/oa/hr/setting/act/T9HrCodeAct/selectCode.act";
  var rtJson = getJsonRs(url );
  if (rtJson.rtState != "0") {
    alert(rtJson.rtMsrg);
    return;
  }
  var prcs = rtJson.rtData;
  return prcs;
}
function getChildCode(parentNo){//根据父级得到所有下一级code
  var seqId = 0;
  var url = contextPath + "/t9/subsys/oa/hr/setting/act/T9HrCodeAct/selectChildCode.act?parentNo="+encodeURIComponent(parentNo) + "&seqId=" +seqId;
  var rtJson = getJsonRs(url );
  if (rtJson.rtState != "0") {
    alert(rtJson.rtMsrg);
    return;
  }
  var prcs = rtJson.rtData;
  return prcs;
}