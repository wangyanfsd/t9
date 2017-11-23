var selectedColor = "rgb(0, 51, 255)";
var moveColor = "#ccc";
var User,UserDesc;
var selectRole = '0';
var selectGroup = '0';
var menu = null;
var tree =  null;
var deptIndex = "";
var roleIndex = "";
var defaultIndex = "";

var requestUrl = contextPath + "/t9/core/module/org_select/act/T9OrgSelect2Act";
function getAccord(){
  var data = {
  panel:'left',
  data:[{title:'已选人员', action:getSelectedUser},
       {title:'按部门选择', action:getTree},
       {title:'按角色选择', action:getRole},
       {title:'自定义组', action:getDefaultGroup },
       {title:'在线人员', action:getOnline}]
   };
  menu = new MenuList(data);
  deptIndex = menu.getContainerId(2);
  roleIndex = menu.getContainerId(3);
  defaultIndex = menu.getContainerId(4);
  menu.showItem(this,{},2);
  getTree();
}
function getTree(){
  $(deptIndex).update("");
  var url = contextPath + "/t9/core/funcs/orgselect/act/T9DeptSelectAct/getDeptTree.act?";
  if (moduleId) {
    url += "moduleId=" + moduleId + "&privNoFlag="+ privNoFlag +"&id=" ;
  } else {
    url += "id=";
  }
  var config = {bindToContainerId:deptIndex
      , requestUrl:url
      , isOnceLoad:false
      , linkPara:{clickFunc:treeNodeClick}
      , isUserModule:true
    }
  if (!isSingle && !moduleId) {
    config.checkboxPara = {isHaveCheckbox:true, checkedFun : checkedHandler , disCheckedFun:disCheckedHander ,expandEvent:false};
  }
  tree = new DTree(config);
  tree.show(); 
}
function checkedHandler(id) {
  getDeptUser (id);
  //选中所有
  selectedAll();
}
function disCheckedHander(id){
  getDeptUser (id);
  disSelectedAll();
}
function getDeptUser (id) {
  $('right').update("");
  var url = requestUrl + "/getUserByDept.act?deptId=" + id;
  if (moduleId) {
    url += "&moduleId=" + moduleId + "&privNoFlag="+ privNoFlag ;
  }
  var json = getJsonRs(url);
  if (json.rtState == '0') {
    var dept = json.rtData;
    for (var i = 0 ;i < dept.length ;i ++) {
      var name = dept[i].deptName;
      var us = dept[i].user;
      
      if (i == 0) {
        if (us.length > 0) {
          getDeptRightPal(name , true , 0 , true);
          addUserDiv(us , 0);
          setSelected(User.value.split(','));
        } else {
          getDeptRightPal(name  , false , 0 , true);
        }
      } else {
        if (us.length > 0) {
          getDeptRightPal(name , true , i , false);
          addUserDiv(us , i);
          setSelected(User.value.split(','));
        } else {
          getDeptRightPal(name  , false , i , false);
        }
      }
    }
  }
}
function getDeptRightPal(name  , hasData , i , isParent) {
  var right = $('right');
  if (i == 0) {
    tDiv = new Element("table", {
      "class": "TableTop",
      "width": "100%"
    });
    
    var tr = tDiv.insertRow(0);
    var tdl = tr.insertCell(0);
    var td = tr.insertCell(1);
    var tdr = tr.insertCell(2);
    tdl.className = "left";
    td.className = "center";
    tdr.className = "right";
    td.innerHTML = name;
  }
  else {
    tDiv = new Element("div" , {'class' : 'header'}).update(name);
  }
  
  if (!isParent) {
    tDiv.align = 'left';
  }
  tDiv.id = "title-" + i;
  tDiv.isTitle = true;
  right.appendChild(tDiv);

  if (hasData) {
    var hasData = new Element("div", {"class": "list"});
    hasData.id = "hasData-" + i;  
    right.appendChild(hasData);
    if (isParent) {
      var addAll = new Element("input" , {type: "button", 'class' : 'BigButtonB', value: "全部添加"});
      addAll.onclick = function () {
        selectedAll(i);
      }
      var disAll = new Element("input" , {type: "button", 'class' : 'BigButtonB', value: "全部删除"});
      disAll.onclick = function () {
        disSelectedAll(i);
      }
      var d = new Element("div", {"class": "op"});
      hasData.appendChild(d.insert(addAll).insert(disAll));
    }
    var list = new Element("div");
    list.id = "list-" + i;
    hasData.appendChild(list);
  } else {
    if (isParent)  {
      var noData = new Element("div" , {'color' : 'red'}).update("未定义用户");
      noData.style.fontSize = "10pt";
      noData.style.paddingTop = "5px";
      right.appendChild(noData);
    }
  }
}

function doInit(){
  getAccord();
 // var obj = tree.getFirstNode();
  //tree.open(obj.nodeId); 
  
  var parentWindowObj;
  var userRetNameArray;
  var userCntrl;
  var userDescCntrl;
  // IE浏览器
  if(!!window.ActiveXObject || "ActiveXObject" in window){
	  parentWindowObj = window.dialogArguments;
	  userRetNameArray = parentWindowObj["userRetNameArray"];
	  if (userRetNameArray && userRetNameArray.length == 2) {
		    userCntrl = userRetNameArray[0];
		    userDescCntrl = userRetNameArray[1];
		    User = parentWindowObj.$(userCntrl);
		    UserDesc = parentWindowObj.$(userDescCntrl);
		  }else {
		    User = parentWindowObj.$("user");
		    UserDesc = parentWindowObj.$("userDesc");
		  }
  }else{ // 其他浏览器
		  //var url = window.location.search; //获取url中"?"符后的字串 
		  parentWindowObj = window.opener;
		  userRetNameArray = parentWindowObj["userRetNameArray"];
		  if (userRetNameArray && userRetNameArray.length == 2) {
			    userCntrl = userRetNameArray[0];
			    userDescCntrl = userRetNameArray[1];
			    User = parentWindowObj.$(userCntrl);
			    UserDesc = parentWindowObj.$(userDescCntrl);
			  }else {
			    User = parentWindowObj.$("user");
			    UserDesc = parentWindowObj.$("userDesc");
		  }
		  //var key = new Array();
		  //var val = new Array();
		  //alert(url);
		  //if (url.indexOf("?") != -1) { 
		    //var str = url.substr(1); 
		    //strs = str.split("&"); 
		    //for(var i = 0; i < strs.length; i ++) { 
		      //theRequest[strs[i].split("=")[0]]=unescape(strs[i].split("=")[1]); 
		    	//key[i] = unescape(strs[i].split("=")[1]);
		    	//val[i] = userRetNameArray[i] = strs[i].split("=")[0];
		    //} 
		  //} 
		 /* User = new Object();
		  var k1 = key[3];
		  var k2 = key[4];
		  var v1 = val[3];
		  var v2 = val[4];
		  User.u = function(){
			  User.value = v1;
	        }
		  User.u();
		  UserDesc = new Object();
		  UserDesc.d function(){
			  UserDesc.value = v2;
		  }
		  UserDesc = v2;*/
		  //User = userRetNameArray[0];
		  //User = $('User', window.parent.document);
		  //alert('User=' + User);
		  //UserDesc = userRetNameArray[1];
	      //UserDesc = $('UserDesc', window.parent.document);
	      //alert('UserDesc=' + UserDesc);
		  //userRetNameArray = $("#wordRetNameArray",window.parent.document);
      }
  
  //存在已经选中的人员  if (!User.value) {
	  //alert('getDefaultUser 没有中');
    getDefaultUser();
  }else {
	 //alert('getSelectedUser 默认选中');
    getSelectedUser();
  }
}


/**
 * 解析已经选中的人员 * @userIdStr       人员ID字符串 * @userNameStr     人员名称字符串 */
function parseSelectedUsers(userIdStr, userNameStr) {
  var rtArray = [];
  var sUserState = "";
  var stateArray = [];
  if (!userIdStr || !userNameStr) {
    return rtArray;
  }
  var idArray = userIdStr.split(",");
  var nameArray = userNameStr.split(",");
  if (idArray.length != nameArray.length) {
   // return rtArray;
  }
  var url = requestUrl + "/getUserState.act" ;
  var json = getJsonRs(url , "ids=" + userIdStr);
  if (json.rtState == '0') {
    rtArray = json.rtData;
  } 
  return rtArray; 
}
function treeNodeClick(id){
  var url = requestUrl  + "/getPersonsByDept.act"
  if (moduleId) {
    url += "?moduleId=" + moduleId + "&privNoFlag="+ privNoFlag ;
  }
  var node = tree.getNode(id);
  //有这部门的权限  if (node && node.extData) {
    var json = getJsonRs(url , 'deptId=' + id + "&notLoginIn=" + notLoginIn);
    if(json.rtState == '0'){
      $("right").update("");
      var users = json.rtData;
      var name = json.rtMsrg;
      if (users.length > 0) {
        getRightPal(name , true , true , 0 );
        addUserDiv(users , 0);
        setSelected(User.value.split(','));
      } else {
        getRightPal(name , true , false , 0 );
      }
    }
  }
}

function setSelected(selectedUser){
  for(var i = 0 ;i < selectedUser.length ;i++){
    var selectedDiv = $("Div-" + selectedUser[i]);
    if(selectedDiv){
      selectedDiv.isSelected = true;
      selectedDiv.className = "item select";
    }
  }
}
function addUserDiv(users , i){
  var divs = $("list-" + i);
  if(users.length > 0){
    for(var i = 0 ; i < users.length ; i++){
      var user = users[i];
      var div = createDiv(user);
      divs.appendChild(div);
    }
  }
}

function createDiv(user){
  var userName = user.userName;
  var deptName = user.deptName;
  var isOnline = user.isOnline;
  var userId = user.userId;
  
  tmp = userName;
  if (deptName) {
    tmp = "[" + deptName + "]"  + tmp ;
  }
  if (isOnline) {
    if (isOnline != '0') {
      tmp += "<font color=red>(在线)<font>";
    }
//    } else if (isOnline == '2') {
//      tmp += "<font color=red>(忙碌)<font>";
//    } else if (isOnline == '3') {
//      tmp += "<font color=red>(离开)<font>";
//    }
  }
  var userDiv = new Element('div',{'class':'item'}).update(tmp);
  userDiv.id = "Div-" + userId ;
  userDiv.isSelected = false;
  userDiv.userName = userName;
  userDiv.onmouseout = function(){
    if(!this.isSelected){
      this.className = "item";
    }else {
      this.className = "item select";
    }
  }
  userDiv.onmouseover = function(){
    if(!this.isSelected){
      this.className = "item hover";
    }else {
      this.className = "item select";
    }
  }
  userDiv.onclick = function(){
    //选 中到不选 中
    if(this.isSelected){
      if (!isSingle) { 
        selectUser(userId ,  userName);
      } else {
        User.value = "";
        UserDesc.value = "";
      }
      this.isSelected = false;
      this.className = "item";
    }else{
      if (!isSingle) { 
        disSelectUser(userId , userName);
        this.isSelected = true;
        this.className = "item select";
      } else {
        User.value = userId;
        UserDesc.value = userName;
        close();
      }
    }
  }
  return userDiv;
}
function selectUser(id , userName) {
  var userStr = User.value;
  var userDescStr = "";
  if (UserDesc.tagName == "INPUT") {
    userDescStr = UserDesc.value;
    UserDesc.value = getOutofStr(userDescStr , userName);
  } else {
    userDescStr = UserDesc.value;
    if (userDescStr && userDescStr.trim) {
      userDescStr = userDescStr.trim();
    }
    UserDesc.value = getOutofStr(userDescStr , userName);
  }
  User.value = getOutofStr(userStr , id);
}
function disSelectUser(id , userName) {
  
  var userStr = User.value;
  var userDescStr = "";
  
  if (UserDesc.tagName == "INPUT") {
    userDescStr = UserDesc.value;
  } else {
    userDescStr = UserDesc.value;
    if (userDescStr && userDescStr.trim) {
      userDescStr = userDescStr.trim();
    }
  }
  if (User.value.length > 0) {
    User.value += "," + id;
  }else {
    User.value = id ;
  }
  if (UserDesc.tagName == "INPUT") {
    if (UserDesc.value) {
      UserDesc.value += "," + userName;
    } else {
      UserDesc.value =  userName;
    }
  } else {
    if(UserDesc.value){
      UserDesc.value += "," + userName;
    }else{
      UserDesc.value = userName;
    }
  }
}
function selectedAll(i){
  var divs = new Array();
  if (i != undefined) {
    divs = $('list-' + i).getElementsByTagName('div');
  } else {
    var divs1 = $('right').getElementsByTagName('div');
    for (var i = 0 ;i < divs1.length ;i ++) {
      var tmp = divs1[i];
      if ( tmp.id 
          && tmp.id.indexOf('list-') != -1) {
        var div2 = tmp.getElementsByTagName('div');
        for(var j = 0 ; j < div2.length ;j ++) {
          divs.push(div2[j]);
        }
      }
    }
  }
  var userStr = User.value;
  if (UserDesc.tagName == "INPUT") {
    if(UserDesc.value){
      var userDescStr = UserDesc.value;
    }else{
      var userDescStr = "";
    }
  } else {
    if(UserDesc.value){
      var userDescStr = UserDesc.value;
      if (userDescStr && userDescStr.trim) {
        userDescStr = userDescStr.trim();
      }
    }else{
      var userDescStr = "";
    }
  }
  
  if (userDescStr) {
    userDescStr += ",";
  }
  if (userStr) {
    userStr += ",";
  }
  for(var i = 0;i < divs.length ;i++){
    var div = divs[i];
    if(!div.isSelected){
      var userId = div.id.substr(4);
      var userName = div.userName;
      div.isSelected = true;
      div.className = "item select";
      userStr += userId + ',';
      userDescStr += userName + ',';
    }
  }
  if (userStr && userStr.lastIndexOf(",") == userStr.length - 1) {
    userStr = userStr.substring(0, userStr.length - 1);
  }
  if (userDescStr && userDescStr.lastIndexOf(",") == userDescStr.length - 1) {
    userDescStr = userDescStr.substring(0, userDescStr.length - 1);
  }
  User.value = userStr;
  if (UserDesc.tagName == 'INPUT') {
    UserDesc.value = userDescStr;
  } else {
    UserDesc.value = userDescStr;
  }
}

function disSelectedAll(i){
  var divs = new Array();
  if (i != undefined) {
    divs = $('list-' + i).getElementsByTagName('div');
  } else {
    var divs1 = $('right').getElementsByTagName('div');
    for (var i = 0 ;i < divs1.length ;i ++) {
      var tmp = divs1[i];
      if ( tmp.id 
          && tmp.id.indexOf('list-') != -1) {
        var div2 = tmp.getElementsByTagName('div');
        for(var j = 0 ; j < div2.length ;j ++) {
          divs.push(div2[j]);
        }
      }
    }
  }
  var userStr = User.value;
  if (UserDesc.tagName == 'INPUT') {
    if(!UserDesc.value){
      return ;
    }
  } else {
    if(!UserDesc.value){
      return ;
    }
  }
  
  if (UserDesc.tagName == "INPUT") {
    if(UserDesc.value){
      var userDescStr = UserDesc.value;
    }else{
      var userDescStr = "";
    }
  } else {
    if(UserDesc.value){
      var userDescStr = UserDesc.value;
      if (userDescStr && userDescStr.trim) {
        userDescStr = userDescStr.trim();
      }
    }else{
      var userDescStr = "";
    }
  }
  for(var i = 0 ; i< divs.length ;i++){
    var div = divs[i];
    if(div.isSelected){
      var userId = div.id.substr(4);
      var userName = div.userName;
      userStr = getOutofStr(userStr , userId);
      
      userDescStr = getOutofStr(userDescStr , userName);
      div.isSelected = false;
      div.className = "item";
    }
  }
  User.value = userStr;
  if (UserDesc.tagName == 'INPUT') {
    UserDesc.value = userDescStr;
  } else {
    UserDesc.value = userDescStr;
  }
}

function getOutofStr(str, s){
  var aStr = str.split(',');
  var strTmp = "";
  var j = 0 ;//控制重名
  for(var i = 0 ;i < aStr.length ; i++){
    if(aStr[i] && (aStr[i] != s || j != 0)){
        strTmp += aStr[i] + ',';
    }else{
      if(aStr[i] == s){
        j = 1;
      }  
    }
  }
  if (strTmp && strTmp.lastIndexOf(",") == strTmp.length - 1) {
    strTmp = strTmp.substring(0, strTmp.length - 1);
  }
  return strTmp;
}
function getRightPal(title , isHaveAll , hasData , i , tip){
  var right = $('right');
  var tDiv;
  if (i == 0) {
    tDiv = new Element("table", {
      "class": "TableTop",
      "width": "100%"
    });
    
    var tr = tDiv.insertRow(0);
    var tdl = tr.insertCell(0);
    var td = tr.insertCell(1);
    var tdr = tr.insertCell(2);
    tdl.className = "left";
    td.className = "center";
    tdr.className = "right";
    td.innerHTML = title;
  }
  else {
    tDiv = new Element("div" , {'class' : 'header'}).update(title);
  }
  tDiv.id = "title" + i;
  right.appendChild(tDiv);
  if (hasData) {
    var hasData = new Element("div", {"class": "list"});
    hasData.id = "hasData-" + i;  
    right.appendChild(hasData);
    if (isHaveAll && !isSingle) {
      var addAll = new Element("input" , {type: "button", 'class' : 'BigButtonB', value: "全部添加"});
      addAll.onclick = function () {
        selectedAll(i);
      }
      var disAll = new Element("input" , {type: "button", 'class' : 'BigButtonB', value: "全部删除"});
      disAll.onclick = function () {
        disSelectedAll(i);
      }
      var d = new Element("div", {"class": "op"});
      hasData.appendChild(d.insert(addAll).insert(disAll));
    }
    var list = new Element("div");
    list.id = "list-" + i;
    hasData.appendChild(list);
  } else {
     if (!tip) {
       tip = "未定义用户";
     }
     var noData = new Element("div" , {'color' : 'red'}).update(tip);
     noData.style.fontSize = "10pt";
     noData.style.paddingTop = "5px";
     right.appendChild(noData);
  }
}


function getSelectedUser() {
  $('right').update("");
  tip = "暂未选择人员";
  
 
  var us = parseSelectedUsers(User.value, UserDesc.value);
  if (us.length > 0) {
    getRightPal('已选人员' , true , true , 0 , tip);
    addUserDiv(us , 0);
    setSelected(User.value.split(','));
  } else {
    getRightPal('已选人员' , true , false , 0 , tip);
  }
}
function getRole(){
  $(roleIndex).update("");
  var url = contextPath + "/t9/core/module/org_select/act/T9RoleSelectAct/getRoles.act";
  if (moduleId) {
    url += "?moduleId=" + moduleId + "&privNoFlag="+ privNoFlag ;
  }
  var json = getJsonRs(url);
  if(json.rtState == "0"){
    roleList = json.rtData;
    addRole(roleList);
  }
}
function addRole(roles) {
  var divs = $(roleIndex);
  if(roles.length > 0 ){
    for(var i = 0 ; i < roles.length ; i++){
      var role = roles[i];
      var div = createRoleDiv(role);
      divs.appendChild(div);
    }
  }
}
function createRoleDiv(role) {
  var div = new Element('div' , {'class' : 'role'}).update(role.privName);
  div.align = 'center';
  if (selectRole == role.privNo) {
    div.isChecked = true;
    div.style.backgroundColor = moveColor;
  }
  div.onmouseout = function(){
    if (!div.isChecked){
      div.style.backgroundColor = '';
    }
  }
  div.onmouseover = function(){
    if (!div.isChecked){
      div.style.backgroundColor = moveColor;
    }
  }
  div.onclick = function(){
    selectRole = role.privNo;
    var divs = $(roleIndex).getElementsByTagName("div");
    for(var i = 0 ; i < divs.length ; i++){
      var tmp = divs[i];
      tmp.isChecked = false;
      tmp.style.backgroundColor = "";
    }
    div.isChecked = true;
    div.style.backgroundColor = moveColor;
    getUserByRoleId(role.privNo , role.privName);
  }
  return div;
}

function getUserByRoleId(roleId , privName){
  $('right').update("");
  var url = requestUrl + "/getUserByRole.act";
  if (moduleId) {
    url += "?moduleId=" + moduleId + "&privNoFlag="+ privNoFlag ;
  }
  var json = getJsonRs(url , "roleId=" + roleId + "&notLoginIn=" + notLoginIn);
  if (json.rtState == '0') {
    var us = json.rtData.principalRole ;
    roleUser = us;
    if (us.length > 0) {
      getRightPal(privName , true , true , 0);
      addUserDiv(us , 0);
      setSelected(User.value.split(','));
    } else {
      getRightPal(privName , true , false , 0  );
    }
    //辅助角色,
    us = json.rtData.supplementRole;
    //判断辅助角色中的人有没有在主角色里,如果在则踢出
    us2 = new Array();
    for (var i = 0 ;i < us.length ; i ++) {
      var tmp = us[i];
      var flag = false;
      for (var j = 0 ;j < roleUser.length ; j ++) {
        var tmp2 = roleUser[j];
        if (tmp2.userId == tmp.userId) {
          flag = true;
        }
      }
      if (!flag) {
        us2.push(tmp);
      }
    }
    if (us2.length > 0) {
      getRightPal("辅助角色" , true , true , 1);
      addUserDiv(us2 , 1);
      setSelected(User.value.split(','));
    }
  }
}
function getDefaultGroup() {
  $(defaultIndex).update("");
  var url = requestUrl + "/getUserGroup.act";
  var singleJson = getJsonRs(url);
  if (singleJson.rtState == '0' ) {
    var singleUser = singleJson.rtData;
    if (singleUser.length > 0) {
      var div = new Element("div").update("个人自定义组");
      div.isTitle = true;
      div.className = 'item TableControl';
      div.style.fontWeight = 'bold';
      div.style.fontSize = '10pt';
      div.align = 'center';
      $(defaultIndex).appendChild(div);
      for(var i = 0 ; i < singleUser.length ; i++){
        var tmp = singleUser[i];
        $(defaultIndex).appendChild(createGroupDiv(tmp));
      }
    }
  }
    
   url = requestUrl + "/getUserGroup.act?isPublic=true";
   var json = getJsonRs(url);
   if (json.rtState == '0' ) {
      var pUser = json.rtData;
      if (pUser.length > 0) {
        var div = new Element("div").update("公共自定义组");
        div.isTitle = true;
        div.className = 'item TableControl';
        div.style.fontWeight = 'bold';
        div.style.fontSize = '10pt';
        div.align = 'center';
        $(defaultIndex).appendChild(div);
        for(var i = 0 ; i < pUser.length ; i++){
          var tmp = pUser[i];
          $(defaultIndex).appendChild(createGroupDiv(tmp));
        }
      }
    }
}
function createGroupDiv(group) {
  var id = group.seqId;
  var name = group.groupName;
  var div = new Element('div' , {'class' : 'role'}).update(name);
  div.align = 'center';
  if (selectGroup == id) {
    div.isChecked = true;
    div.style.backgroundColor = moveColor;
  }
  div.onmouseout = function(){
    if (!div.isChecked){
      div.style.backgroundColor = '';
    }
  }
  div.onmouseover = function(){
    if (!div.isChecked){
      div.style.backgroundColor = moveColor;
    }
  }
  div.onclick = function(){
    selectGroup = id;
    var divs = $(defaultIndex).getElementsByTagName("div");
    for(var i = 0 ; i < divs.length ; i++){
      var tmp = divs[i];
      //不是标题
      if (!tmp.isTitle) {
        tmp.isChecked = false;
        tmp.style.backgroundColor = "";
      }
    }
    div.isChecked = true;
    div.style.backgroundColor = moveColor;
    getUserByGroup(id , name);
  }
  return div;
}
function getUserByGroup(id , name) {
  $('right').update("");
  var url = requestUrl + "/getUserByGroup.act?groupId=" + id;
  if (moduleId) {
    url += "&moduleId=" + moduleId + "&privNoFlag="+ privNoFlag ;
  }
  var json = getJsonRs(url , "notLoginIn=" + notLoginIn);
  if (json.rtState == '0') {
    var us = json.rtData ;
    if (us.length > 0) {
      getRightPal(name , true , true , 0 , "尚未添加用户");
      addUserDiv(us , 0);
      setSelected(User.value.split(','));
    } else {
      getRightPal(name , true , false , 0 , "尚未添加用户");
    }
  }
}
function getOnline() {
  $('right').update("");
  var url = requestUrl + "/getUserByOnline.act";
  if (moduleId) {
    url += "?moduleId=" + moduleId + "&privNoFlag="+ privNoFlag ;
  }
  var json = getJsonRs(url , "notLoginIn=" + notLoginIn);
  if (json.rtState == '0') {
    var us = json.rtData ;
    if (us.length > 0) {
      getRightPal("全部在线人员" , true , true , 0);
      addUserDiv(us , 0);
      setSelected(User.value.split(','));
    } else {
      getRightPal("全部在线人员" , true , false , 0 );
    }
  }
}
function doSearch(value) {
  $('right').update("");
  var url = requestUrl + "/getUserBySearch.act";
  if (moduleId) {
    url += "?moduleId=" + moduleId + "&privNoFlag="+ privNoFlag ;
  }
  var json = getJsonRs(url , "userName=" + encodeURI(value) + "&notLoginIn=" + notLoginIn);
  tip = "未查询到用户";
  if (json.rtState == '0') {
    var us = json.rtData ;
    if (us.length > 0) {
      getRightPal("人员查询" , true , true , 0 , tip);
      addUserDiv(us , 0);
      setSelected(User.value.split(','));
    } else {
      getRightPal("人员查询" , true , false , 0 ,tip );
    }
  }
}
function getDefaultUser() {
  $('right').update("");
  var url = requestUrl + "/getDefaultUser.act";
  if (moduleId) {
    url += "?moduleId=" + moduleId + "&privNoFlag="+ privNoFlag ;
  }
  var json = getJsonRs(url ,  "notLoginIn=" + notLoginIn);
  tip = "未定义用户";
  if (json.rtState == '0') {
    var us = json.rtData ;
    var name = json.rtMsrg;
    if (us.length > 0) {
      getRightPal(name , true , true , 0 , tip);
      addUserDiv(us , 0);
      setSelected(User.value.split(','));
    } else {
      getRightPal(name , true , false , 0 ,tip );
    }
  }
}
