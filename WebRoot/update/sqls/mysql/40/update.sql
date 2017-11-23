CREATE TABLE  proj_bug (
  SEQ_ID int(11) NOT NULL AUTO_INCREMENT,
  PROJ_ID int(11) NOT NULL DEFAULT '0',
  TASK_ID int(11) NOT NULL,
  DEAL_USER varchar(100) DEFAULT NULL,
  BEGIN_USER varchar(100) DEFAULT NULL,
  BUG_NAME varchar(200) DEFAULT NULL,
  DEAD_LINE datetime DEFAULT NULL,
  CREAT_TIME datetime DEFAULT NULL,
  LEVEL int(11) DEFAULT '0',
  BUG_DESC text,
  STATUS int(11) DEFAULT NULL,
  ATTACHMENT_ID text,
  ATTACHMENT_NAME text,
  RESULT text,
  PRIMARY KEY (SEQ_ID),
  KEY PROJ_ID (PROJ_ID)
) ENGINE=MyISAM AUTO_INCREMENT=28 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

CREATE TABLE  proj_comment (
  SEQ_ID int(11) NOT NULL AUTO_INCREMENT,
  PROJ_ID int(11) NOT NULL DEFAULT '0',
  WRITER varchar(40) DEFAULT NULL,
  WRITE_TIME datetime DEFAULT '0000-00-00 00:00:00',
  CONTENT text,
  PRIMARY KEY (SEQ_ID)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

CREATE TABLE  proj_file (
  SEQ_ID int(11) NOT NULL AUTO_INCREMENT,
  PROJ_ID int(11) NOT NULL DEFAULT '0',
  SORT_ID int(11) DEFAULT '0',
  FILE_TYPE int(11) DEFAULT '0',
  SUBJECT varchar(200) DEFAULT NULL,
  ATTACHMENT_ID text,
  ATTACHMENT_NAME text,
  FILE_DESC text,
  UPLOAD_USER varchar(100) DEFAULT NULL,
  VERSION varchar(20) DEFAULT NULL,
  UPDATE_TIME datetime DEFAULT NULL,
  HISTORY varchar(20) DEFAULT NULL,
  ACTIVE varchar(20) DEFAULT NULL,
  PRIMARY KEY (SEQ_ID)
) ENGINE=MyISAM AUTO_INCREMENT=26 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

CREATE TABLE  proj_file_log (
  SEQ_ID int(11) NOT NULL AUTO_INCREMENT,
  FILE_ID int(11) DEFAULT '0',
  ACTION int(11) DEFAULT NULL,
  USER_ID int(11) unsigned DEFAULT NULL,
  ACTION_TIME datetime DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (SEQ_ID)
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

CREATE TABLE  proj_file_sort (
  SEQ_ID int(11) NOT NULL AUTO_INCREMENT,
  SORT_PARENT int(11) DEFAULT '0',
  PROJ_ID int(11) NOT NULL DEFAULT '0',
  SORT_NO varchar(20) DEFAULT NULL,
  SORT_NAME varchar(200) DEFAULT NULL,
  SORT_TYPE varchar(20) DEFAULT NULL,
  VIEW_USER text,
  NEW_USER text,
  MANAGE_USER text,
  MODIFY_USER text,
  PRIMARY KEY (SEQ_ID),
  UNIQUE KEY ITEM_ID (SEQ_ID),
  KEY SORT_PARENT (SORT_PARENT)
) ENGINE=MyISAM AUTO_INCREMENT=14 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

CREATE TABLE  proj_forum (
  SEQ_ID int(11) NOT NULL AUTO_INCREMENT,
  PROJ_ID int(11) NOT NULL DEFAULT '0',
  USER_ID varchar(20) DEFAULT NULL,
  SUBJECT varchar(200) DEFAULT NULL,
  CONTENT mediumtext,
  ATTACHMENT_ID text,
  ATTACHMENT_NAME text,
  SUBMIT_TIME datetime DEFAULT '0000-00-00 00:00:00',
  REPLY_CONT int(11) DEFAULT '0',
  PARENT int(11) DEFAULT '0',
  OLD_SUBMIT_TIME datetime DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (SEQ_ID),
  KEY USER_ID (USER_ID),
  KEY PROJ_ID (PROJ_ID)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

CREATE TABLE  proj_priv (
  SEQ_ID int(11) NOT NULL AUTO_INCREMENT,
  PRIV_CODE varchar(40) DEFAULT NULL,
  PRIV_USER text,
  PRIV_ROLE text,
  PRIV_DEPT text,
  PRIMARY KEY (SEQ_ID)
) ENGINE=MyISAM AUTO_INCREMENT=54 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

CREATE TABLE  proj_project (
  SEQ_ID int(11) NOT NULL AUTO_INCREMENT,
  PROJ_NAME varchar(255) NOT NULL DEFAULT '',
  PROJ_NUM varchar(40) NOT NULL DEFAULT '',
  PROJ_DESCRIPTION text,
  PROJ_TYPE int(11) DEFAULT '0',
  PROJ_DEPT varchar(40) DEFAULT NULL,
  PROJ_UPDATE_TIME datetime DEFAULT NULL,
  PROJ_START_TIME date DEFAULT NULL,
  PROJ_END_TIME date DEFAULT NULL,
  PROJ_ACT_END_TIME date DEFAULT NULL,
  PROJ_OWNER varchar(255) DEFAULT NULL,
  PROJ_LEADER varchar(255) DEFAULT NULL,
  PROJ_VIWER text,
  PROJ_USER text,
  PROJ_PRIV text,
  PROJ_MANAGER varchar(255) DEFAULT NULL,
  PROJ_COMMENT text,
  PROJ_STATUS int(11) DEFAULT NULL,
  PROJ_PERCENT_COMPLETE int(11) DEFAULT '0',
  COST_TYPE text,
  COST_MONEY text,
  APPROVE_LOG text,
  ATTACHMENT_ID text,
  ATTACHMENT_NAME text,
  PRIMARY KEY (SEQ_ID)
) ENGINE=MyISAM AUTO_INCREMENT=137 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

CREATE TABLE  proj_task (
  SEQ_ID int(11) NOT NULL AUTO_INCREMENT,
  PROJ_ID int(11) NOT NULL DEFAULT '0',
  TASK_NO varchar(40) NOT NULL DEFAULT '',
  TASK_NAME varchar(100) DEFAULT NULL,
  TASK_DESCRIPTION text,
  TASK_USER varchar(40) DEFAULT NULL,
  TASK_MILESTONE int(11) DEFAULT NULL,
  TASK_START_TIME date DEFAULT NULL,
  TASK_END_TIME date DEFAULT NULL,
  TASK_ACT_END_TIME date DEFAULT NULL ,
  TASK_TIME int(10) DEFAULT '0',
  TASK_LEVEL varchar(40) DEFAULT '1',
  PRE_TASK int(11) DEFAULT '0',
  TASK_PERCENT_COMPLETE int(11) DEFAULT '0',
  REMARK text,
  FLOW_ID_STR varchar(200) DEFAULT NULL,
  RUN_ID_STR varchar(200) DEFAULT NULL,
  TASK_STATUS int(10) DEFAULT '0',
  TASk_CONSTRAIN int(10) DEFAULT NULL,
  PARENT_TASK int(11) DEFAULT NULL,
  PRIMARY KEY (SEQ_ID),
  KEY PROJ_ID (PROJ_ID)
) ENGINE=MyISAM AUTO_INCREMENT=69 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

CREATE TABLE  proj_task_log (
  SEQ_ID int(11) NOT NULL AUTO_INCREMENT,
  LOG_TYPE int(11) NOT NULL DEFAULT '0',
  TASK_ID int(11) NOT NULL DEFAULT '0',
  LOG_USER varchar(255) DEFAULT NULL,
  LOG_CONTENT text,
  LOG_TIME datetime DEFAULT NULL,
  PERCENT int(11) DEFAULT '0',
  ATTACHMENT_ID text,
  ATTACHMENT_NAME text,
  PRIMARY KEY (SEQ_ID),
  KEY TASK_ID (TASK_ID),
  KEY LOG_USER (LOG_USER)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;


INSERT INTO code_item (CLASS_NO,CLASS_CODE,CLASS_DESC,SORT_NO,CODE) VALUES 
('SMS_REMIND','88','项目管理','88',NULL); 

INSERT INTO sys_function (MENU_ID,FUNC_NAME,FUNC_CODE,ICON,OPEN_FLAG) VALUES 
('3501','我的项目','/project/proj/projectList.jsp','edit.gif','0'); 
INSERT INTO sys_function (MENU_ID,FUNC_NAME,FUNC_CODE,ICON,OPEN_FLAG) VALUES 
('3505','我的任务','/project/task/index.jsp','edit.gif','0'); 
INSERT INTO sys_function (MENU_ID,FUNC_NAME,FUNC_CODE,ICON,OPEN_FLAG) VALUES 
('3510','项目审批','/project/approve/','edit.gif','0'); 
INSERT INTO sys_function (MENU_ID,FUNC_NAME,FUNC_CODE,ICON,OPEN_FLAG) VALUES 
('3515','项目文档','/project/doc/index.jsp','edit.gif','0'); 
INSERT INTO sys_function (MENU_ID,FUNC_NAME,FUNC_CODE,ICON,OPEN_FLAG) VALUES 
('3520','项目问题','/project/bug/','edit.gif','0'); 
INSERT INTO sys_function (MENU_ID,FUNC_NAME,FUNC_CODE,ICON,OPEN_FLAG) VALUES 
('3525','基础参数设置','org','edit.gif','0'); 
INSERT INTO sys_function (MENU_ID,FUNC_NAME,FUNC_CODE,ICON,OPEN_FLAG) VALUES 
('352501','项目权限设置','/project/setting/priv/','edit.gif','0'); 
INSERT INTO sys_function (MENU_ID,FUNC_NAME,FUNC_CODE,ICON,OPEN_FLAG) VALUES 
('352505','项目代码设置','/project/setting/code/','edit.gif','0'); 
INSERT INTO sys_function (MENU_ID,FUNC_NAME,FUNC_CODE,ICON,OPEN_FLAG) VALUES 
('352510','项目模板管理','/project/setting/template/','edit.gif','0'); 
INSERT INTO sys_menu (MENU_ID,MENU_NAME,IMAGE) VALUES 
('35','项目管理','sys.gif');