var xmlhttp;

var catArray = null;
function buildTab(categories){
	catArray = categories;
	var nevPanel = document.createElement("div");
	nevPanel.setAttribute("id", "navPanel");
	document.body.appendChild(nevPanel);
	
	var div_first = document.createElement("div");
	div_first.setAttribute("id", "title");
	div_first.className = "navPanel";
	nevPanel.appendChild(div_first);
	
	//var firstTitle = response.getElementsByTagName("title")[0].firstChild.nodeValue;
	var firstTitle = categories[0].title;
	for(var i=0; i<categories.length; i++) {
		var a = document.createElement("a");
		a.setAttribute("href","#");
		
		//将选中的标题栏传入方法中

		//var title =categories[i].getElementsByTagName("title")[0].firstChild.nodeValue;
		var title = categories[i].title;
		a.title = title;
		a.id = i;
		a.onclick = doClickTab.bind(window, title, i);
	
		div_first.appendChild(a);
		
		var span = document.createElement("span");
		span.width = 80;			
		//span.innerHTML = "&nbsp;&nbsp;" + categories[i].getElementsByTagName("title")[0].firstChild.nodeValue;
		span.innerHTML = "&nbsp;&nbsp;" + categories[i].title;
		a.appendChild(span);
		
		var img = document.createElement("img");
		//var imgUrl = categories[i].getElementsByTagName("imgUrl")[0].firstChild.nodeValue;
		var imgUrl = categories[i].imgUrl;
		img.setAttribute("src", imgUrl);
		span.appendChild(img);
		span.appendChild(document.createTextNode("  "));
	}
	
  //画出内容栏
  var div_second = document.createElement("div");
   div_second.className = "s_b";
   div_second.setAttribute("id", "content");
   document.body.appendChild(div_second);
   
   var div_inner = document.createElement("div");
   div_inner.id = "content_div";
   div_second.appendChild(div_inner);
   
   var div_inner1 = document.createElement("div");
   div_inner1.style.display = "none";
   div_inner.appendChild(div_inner1);
   
   //默认显示第一个标题栏内容
	doClickTab(firstTitle, 0);
}

function doClickTab(title, id) {
	var title_contentUL = document.getElementById("title");
	var count = title_contentUL.childNodes.length;
	
	for(var i=0;i<count;i++) {
		if(i == id) {
			title_contentUL.childNodes[i].className = "active" ;
		}else {
			title_contentUL.childNodes[i].className = "" ;	
		}                             
	}

	if (catArray[id].content) {
    var div_inner = document.getElementById("content_div");
    div_inner.style.display = "block";
    div_inner.innerHTML = "";
    
    div_inner.innerHTML = catArray[id].content;
    var contentFrame = document.getElementById("contentFrame");
    if (contentFrame) {
      contentFrame.style.display = "none";
    }
    return;
  }
	
	if (!catArray[id].useIframe) {
	  getJsonRs(catArray[id].contentUrl, null, dispContent.bind(window, id));
	  return;
	}
	
	//使用Iframe显示内容
	var iframe = document.getElementById("contentFrame");
  if (!iframe) {
    iframe = document.createElement("iframe");
    iframe.width = "100%";
    iframe.height = "100%";
    //iframe.id = "contentFrame";
  }
  iframe.setAttribute("id", "contentFrame");
  iframe.style.display = "block";
  iframe.setAttribute("src", catArray[id].contentUrl);
  var contentDiv = document.getElementById("content_div");
  if (contentDiv) {
    contentDiv.style.display = "none";
  }
  var divContent = document.getElementById("content");
  divContent.appendChild(iframe);
  return;
}

function dispContent() {
  var id = arguments[0];
  var rtJson = arguments[1];
  //var content = rtXml.getElementsByTagName("content")[0].firstChild.nodeValue;
  
  catArray[id].content = rtJson.content;
  //将原先栏中的内容清空，再将选中的内容加进来
  var div_inner = document.getElementById("content_div");
  div_inner.innerHTML = "";
  div_inner.innerHTML = catArray[id].content;
}