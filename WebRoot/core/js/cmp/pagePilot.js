var T9JsPagePilot=Class.create();T9JsPagePilot.prototype={initialize:function(cfgs){this.config(cfgs)},config:function(cfgs){this.dataAction=cfgs.dataAction;this.container=cfgs.container?$(cfgs.container):$(document.body);this.pageInfo.pageSize=parseInt(cfgs.pageSize);if(this.pageInfo.pageSize){var sizeList=cfgs.sizeList?cfgs.sizeList:[20,10,30,40,2];var arry=new Array();arry[0]=this.pageInfo.pageSize;for(var i=0,j=0;i<sizeList.length;i++){if(this.pageInfo.pageSize==sizeList[i]){continue}arry[j+1]=sizeList[i];j++}this.sizeList=arry}this.pageInfo.totalRecord=cfgs.totalRecord?parseInt(cfgs.totalRecord):0;this.pageInfo.pageIndex=cfgs.pageIndex?parseInt(cfgs.pageIndex):0;this.opt=cfgs.opt;this.loadData=cfgs.loadData;this.isInit=true},show:function(){if(this.isInit){this.buildNevBar();this.bindNevAction();this.setPageInfo(this.pageInfo.pageIndex);this.refreshNevBar(this.pageInfo.pageIndex);this.isInit=false}else{$("pgTopPanel").show()}if($("pgTopPanel")){$("pgMsrgPanel").hide()}},pageInfo:{totalRecord:0,totalPage:0,pageIndex:0,pageSize:0},setPageInfo:function(pageIndex){if(!this.pageInfo.totalRecord){return}var pageCnt=parseInt((parseInt(this.pageInfo.totalRecord)/parseInt(this.pageInfo.pageSize)));if(this.pageInfo.totalRecord%this.pageInfo.pageSize!=0){pageCnt++}this.pageInfo.totalPage=pageCnt;this.pageInfo.pageIndex=pageIndex},buildNevBar:function(){if(!this.isInit){return}var nevBarText=getTextRs(contextPath+"/core/inc/pagenevbar2.jsp");this.container.innerHTML=nevBarText;var selectArray=new Array();for(var i=0;i<this.sizeList.size();i++){var size=this.sizeList.get(i);var record=new CodeRecord();record.code=size;record.desc=size;selectArray.add(record)}var selectSize=$("selectPageSize");loadSelectData(selectSize,selectArray)},changePageSize:function(){var selectSize=$("selectPageSize");if(!selectSize){return}this.pageInfo.pageSize=selectSize.value?parseInt(selectSize.value):20;this.setPageInfo(0);this.showPage(0)},bindNevAction:function(){if(!this.isInit){return}$("selectPageSize").observe("change",this.changePageSize.bind(this));$("btnPgFirst").observe("click",this.firstPage.bind(this));$("btnPgPre").observe("click",this.prePage.bind(this));$("pageIndex").observe("blur",this.gotoPage.bind(this));$("btPgNext").observe("click",this.nextPage.bind(this));$("btnPgLast").observe("click",this.lastPage.bind(this));$("btnRefresh").observe("click",this.refreshPage.bind(this))},showPage:function(pageIndex){if(pageIndex>=this.pageInfo.totalPage){pageIndex=this.pageInfo.totalPage}if(pageIndex<=0){pageIndex=0}this.pageInfo.pageIndex=pageIndex;this.setPageInfo(pageIndex);this.refreshNevBar(pageIndex);if(this.loadData){this.loadData.bind(this,this.pageInfo)()}},firstPage:function(){this.showPage(0)},prePage:function(){this.showPage(this.pageInfo.pageIndex-1)},gotoPage:function(){var pageNo=$("pageIndex").value;if(!pageNo){return}if(!isNumber(pageNo)){alert("页码需要是数值");selectLast($("pageIndex"));return}pageNo=parseInt(pageNo);if(pageNo<1||pageNo>this.pageInfo.totalPage){alert("请输入合理页码");selectLast($("pageIndex"));return}this.showPage(pageNo-1)},nextPage:function(){this.showPage(this.pageInfo.pageIndex+1)},lastPage:function(){this.showPage(this.pageInfo.totalPage-1)},refreshPage:function(dom,index){if(!index&&index!==0){index=this.pageInfo.pageIndex}this.showPage(index)},refreshNevBar:function(){if(this.pageInfo.pageIndex<1){this.switchClass($("btnPgFirst"),"pgFirst","pgFirstDisabled");$("btnPgFirst").disabled=true}else{this.switchClass($("btnPgFirst"),"pgFirstDisabled","pgFirst");$("btnPgFirst").disabled=false}if(this.pageInfo.pageIndex<1){this.switchClass($("btnPgPre"),"pgPrev","pgPrevDisabled");$("btnPgPre").disabled=true}else{this.switchClass($("btnPgPre"),"pgPrevDisabled","pgPrev");$("btnPgPre").disabled=false}if(this.pageInfo.totalPage<1){$("pageIndex").disabled=true;$("pageIndex").value=""}else{$("pageIndex").disabled=false;$("pageIndex").value=this.pageInfo.pageIndex+1}if(this.pageInfo.pageIndex>=this.pageInfo.totalPage-1){this.switchClass($("btPgNext"),"pgNext","pgNextDisabled");$("btPgNext").disabled=true}else{this.switchClass($("btPgNext"),"pgNextDisabled","pgNext");$("btPgNext").disabled=false}if(this.pageInfo.pageIndex>=this.pageInfo.totalPage-1){this.switchClass($("btnPgLast"),"pgLast","pgLastDisabled");$("btnPgLast").disabled=true}else{this.switchClass($("btnPgLast"),"pgLastDisabled","pgLast");$("btnPgLast").disabled=false}$("pageCount").innerHTML=this.pageInfo.totalPage},switchClass:function(cntrl,srcCls,destCls){cntrl.removeClassName(srcCls);cntrl.addClassName(destCls)}};