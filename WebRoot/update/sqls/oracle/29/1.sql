ALTER TABLE cms_content ADD (image_url CLOB);

alter trigger TRG_cms_column disable;
alter trigger TRG_cms_station disable;
alter trigger TRG_cms_template disable;

INSERT INTO cms_column (SEQ_ID,COLUMN_NAME,COLUMN_TITLE,STATION_ID,PARENT_ID,COLUMN_PATH,ARCHIVE,TEMPLATE_INDEX_ID,TEMPLATE_ARTICLE_ID,CREATE_ID,CREATE_TIME,COLUMN_INDEX,PAGING,MAX_INDEX_PAGE,PAGING_NUMBER,URL,SHOW_MAIN,VISIT_USER,EDIT_USER,NEW_USER,DEL_USER,REL_USER,EDIT_USER_CONTENT,APPROVAL_USER_CONTENT,RELEASE_USER_CONTENT,RECEVIE_USER_CONTENT,ORDER_CONTENT) VALUES 
 (63,'信息公开','',13,0,'xxgk',0,34,35,0,to_date('2012-06-04 15:46:25','yyyy-mm-dd hh24:mi:ss'),11,0,0,0,NULL,1,'0||','0||','0||','0||','0||','0||','0||','0||','0||','0||');
INSERT INTO cms_column (SEQ_ID,COLUMN_NAME,COLUMN_TITLE,STATION_ID,PARENT_ID,COLUMN_PATH,ARCHIVE,TEMPLATE_INDEX_ID,TEMPLATE_ARTICLE_ID,CREATE_ID,CREATE_TIME,COLUMN_INDEX,PAGING,MAX_INDEX_PAGE,PAGING_NUMBER,URL,SHOW_MAIN,VISIT_USER,EDIT_USER,NEW_USER,DEL_USER,REL_USER,EDIT_USER_CONTENT,APPROVAL_USER_CONTENT,RELEASE_USER_CONTENT,RECEVIE_USER_CONTENT,ORDER_CONTENT) VALUES 
 (64,'会议之窗','',13,0,'hyzc',0,34,35,0,to_date('2012-06-04 15:46:25','yyyy-mm-dd hh24:mi:ss'),10,0,0,0,NULL,1,'0||','0||','0||','0||','0||','0||','0||','0||','0||','0||');
INSERT INTO cms_column (SEQ_ID,COLUMN_NAME,COLUMN_TITLE,STATION_ID,PARENT_ID,COLUMN_PATH,ARCHIVE,TEMPLATE_INDEX_ID,TEMPLATE_ARTICLE_ID,CREATE_ID,CREATE_TIME,COLUMN_INDEX,PAGING,MAX_INDEX_PAGE,PAGING_NUMBER,URL,SHOW_MAIN,VISIT_USER,EDIT_USER,NEW_USER,DEL_USER,REL_USER,EDIT_USER_CONTENT,APPROVAL_USER_CONTENT,RELEASE_USER_CONTENT,RECEVIE_USER_CONTENT,ORDER_CONTENT) VALUES
 (65,'民政互动','',13,0,'mzhd',0,34,35,0,to_date('2012-06-04 15:46:25','yyyy-mm-dd hh24:mi:ss'),9,0,0,0,NULL,1,'0||','0||','0||','0||','0||','0||','0||','0||','0||','0||');
INSERT INTO cms_column (SEQ_ID,COLUMN_NAME,COLUMN_TITLE,STATION_ID,PARENT_ID,COLUMN_PATH,ARCHIVE,TEMPLATE_INDEX_ID,TEMPLATE_ARTICLE_ID,CREATE_ID,CREATE_TIME,COLUMN_INDEX,PAGING,MAX_INDEX_PAGE,PAGING_NUMBER,URL,SHOW_MAIN,VISIT_USER,EDIT_USER,NEW_USER,DEL_USER,REL_USER,EDIT_USER_CONTENT,APPROVAL_USER_CONTENT,RELEASE_USER_CONTENT,RECEVIE_USER_CONTENT,ORDER_CONTENT) VALUES
 (66,'公共服务','',13,0,'ggfw',0,34,35,0,to_date('2012-06-04 15:46:25','yyyy-mm-dd hh24:mi:ss'),8,0,0,0,NULL,1,'0||','0||','0||','0||','0||','0||','0||','0||','0||','0||');
INSERT INTO cms_column (SEQ_ID,COLUMN_NAME,COLUMN_TITLE,STATION_ID,PARENT_ID,COLUMN_PATH,ARCHIVE,TEMPLATE_INDEX_ID,TEMPLATE_ARTICLE_ID,CREATE_ID,CREATE_TIME,COLUMN_INDEX,PAGING,MAX_INDEX_PAGE,PAGING_NUMBER,URL,SHOW_MAIN,VISIT_USER,EDIT_USER,NEW_USER,DEL_USER,REL_USER,EDIT_USER_CONTENT,APPROVAL_USER_CONTENT,RELEASE_USER_CONTENT,RECEVIE_USER_CONTENT,ORDER_CONTENT) VALUES
 (67,'政务督查','',13,0,'zwdc',0,34,35,0,to_date('2012-06-04 15:46:25','yyyy-mm-dd hh24:mi:ss'),7,0,0,0,NULL,1,'0||','0||','0||','0||','0||','0||','0||','0||','0||','0||');
INSERT INTO cms_column (SEQ_ID,COLUMN_NAME,COLUMN_TITLE,STATION_ID,PARENT_ID,COLUMN_PATH,ARCHIVE,TEMPLATE_INDEX_ID,TEMPLATE_ARTICLE_ID,CREATE_ID,CREATE_TIME,COLUMN_INDEX,PAGING,MAX_INDEX_PAGE,PAGING_NUMBER,URL,SHOW_MAIN,VISIT_USER,EDIT_USER,NEW_USER,DEL_USER,REL_USER,EDIT_USER_CONTENT,APPROVAL_USER_CONTENT,RELEASE_USER_CONTENT,RECEVIE_USER_CONTENT,ORDER_CONTENT) VALUES
 (68,'应急处理','',13,0,'yjcl',0,34,35,0,to_date('2012-06-04 15:46:25','yyyy-mm-dd hh24:mi:ss'),6,0,0,0,NULL,1,'0||','0||','0||','0||','0||','0||','0||','0||','0||','0||');
INSERT INTO cms_column (SEQ_ID,COLUMN_NAME,COLUMN_TITLE,STATION_ID,PARENT_ID,COLUMN_PATH,ARCHIVE,TEMPLATE_INDEX_ID,TEMPLATE_ARTICLE_ID,CREATE_ID,CREATE_TIME,COLUMN_INDEX,PAGING,MAX_INDEX_PAGE,PAGING_NUMBER,URL,SHOW_MAIN,VISIT_USER,EDIT_USER,NEW_USER,DEL_USER,REL_USER,EDIT_USER_CONTENT,APPROVAL_USER_CONTENT,RELEASE_USER_CONTENT,RECEVIE_USER_CONTENT,ORDER_CONTENT) VALUES
 (70,'今日要闻','',13,0,'jryw',0,34,35,1,to_date('2012-06-04 15:46:25','yyyy-mm-dd hh24:mi:ss'),12,0,0,0,NULL,0,'0||','0||','0||','0||','0||','0||','0||','0||','0||','0||');
INSERT INTO cms_column (SEQ_ID,COLUMN_NAME,COLUMN_TITLE,STATION_ID,PARENT_ID,COLUMN_PATH,ARCHIVE,TEMPLATE_INDEX_ID,TEMPLATE_ARTICLE_ID,CREATE_ID,CREATE_TIME,COLUMN_INDEX,PAGING,MAX_INDEX_PAGE,PAGING_NUMBER,URL,SHOW_MAIN,VISIT_USER,EDIT_USER,NEW_USER,DEL_USER,REL_USER,EDIT_USER_CONTENT,APPROVAL_USER_CONTENT,RELEASE_USER_CONTENT,RECEVIE_USER_CONTENT,ORDER_CONTENT) VALUES
 (71,'政府文件','',13,0,'zfwj',0,34,35,1,to_date('2012-06-04 15:46:25','yyyy-mm-dd hh24:mi:ss'),13,0,0,0,NULL,0,'0||','0||','0||','0||','0||','0||','0||','0||','0||','0||');
INSERT INTO cms_column (SEQ_ID,COLUMN_NAME,COLUMN_TITLE,STATION_ID,PARENT_ID,COLUMN_PATH,ARCHIVE,TEMPLATE_INDEX_ID,TEMPLATE_ARTICLE_ID,CREATE_ID,CREATE_TIME,COLUMN_INDEX,PAGING,MAX_INDEX_PAGE,PAGING_NUMBER,URL,SHOW_MAIN,VISIT_USER,EDIT_USER,NEW_USER,DEL_USER,REL_USER,EDIT_USER_CONTENT,APPROVAL_USER_CONTENT,RELEASE_USER_CONTENT,RECEVIE_USER_CONTENT,ORDER_CONTENT) VALUES
 (72,'政务公告','',13,0,'zwgg',0,34,35,1,to_date('2012-06-04 15:46:25','yyyy-mm-dd hh24:mi:ss'),14,0,0,0,NULL,0,'0||','0||','0||','0||','0||','0||','0||','0||','0||','0||');
INSERT INTO cms_column (SEQ_ID,COLUMN_NAME,COLUMN_TITLE,STATION_ID,PARENT_ID,COLUMN_PATH,ARCHIVE,TEMPLATE_INDEX_ID,TEMPLATE_ARTICLE_ID,CREATE_ID,CREATE_TIME,COLUMN_INDEX,PAGING,MAX_INDEX_PAGE,PAGING_NUMBER,URL,SHOW_MAIN,VISIT_USER,EDIT_USER,NEW_USER,DEL_USER,REL_USER,EDIT_USER_CONTENT,APPROVAL_USER_CONTENT,RELEASE_USER_CONTENT,RECEVIE_USER_CONTENT,ORDER_CONTENT) VALUES
 (73,'舆情反馈','',13,0,'yqfk',0,34,35,1,to_date('2012-06-04 15:46:25','yyyy-mm-dd hh24:mi:ss'),15,0,0,0,NULL,0,'0||','0||','0||','0||','0||','0||','0||','0||','0||','0||');
INSERT INTO cms_column (SEQ_ID,COLUMN_NAME,COLUMN_TITLE,STATION_ID,PARENT_ID,COLUMN_PATH,ARCHIVE,TEMPLATE_INDEX_ID,TEMPLATE_ARTICLE_ID,CREATE_ID,CREATE_TIME,COLUMN_INDEX,PAGING,MAX_INDEX_PAGE,PAGING_NUMBER,URL,SHOW_MAIN,VISIT_USER,EDIT_USER,NEW_USER,DEL_USER,REL_USER,EDIT_USER_CONTENT,APPROVAL_USER_CONTENT,RELEASE_USER_CONTENT,RECEVIE_USER_CONTENT,ORDER_CONTENT) VALUES
 (74,'建言献策','',13,0,'jyxc',0,34,35,1,to_date('2012-06-04 15:46:25','yyyy-mm-dd hh24:mi:ss'),16,0,0,0,NULL,0,'0||','0||','0||','0||','0||','0||','0||','0||','0||','0||');
INSERT INTO cms_column (SEQ_ID,COLUMN_NAME,COLUMN_TITLE,STATION_ID,PARENT_ID,COLUMN_PATH,ARCHIVE,TEMPLATE_INDEX_ID,TEMPLATE_ARTICLE_ID,CREATE_ID,CREATE_TIME,COLUMN_INDEX,PAGING,MAX_INDEX_PAGE,PAGING_NUMBER,URL,SHOW_MAIN,VISIT_USER,EDIT_USER,NEW_USER,DEL_USER,REL_USER,EDIT_USER_CONTENT,APPROVAL_USER_CONTENT,RELEASE_USER_CONTENT,RECEVIE_USER_CONTENT,ORDER_CONTENT) VALUES
 (76,'公告栏','' , 13,0 ,'ggl',0,34,35,1,to_date('2012-06-04 15:46:25','yyyy-mm-dd hh24:mi:ss'),17,0,0,0,NULL,0,'0||','0||','0||','0||','0||','0||','0||','0||','0||','0||');
INSERT INTO cms_column (SEQ_ID,COLUMN_NAME,COLUMN_TITLE,STATION_ID,PARENT_ID,COLUMN_PATH,ARCHIVE,TEMPLATE_INDEX_ID,TEMPLATE_ARTICLE_ID,CREATE_ID,CREATE_TIME,COLUMN_INDEX,PAGING,MAX_INDEX_PAGE,PAGING_NUMBER,URL,SHOW_MAIN,VISIT_USER,EDIT_USER,NEW_USER,DEL_USER,REL_USER,EDIT_USER_CONTENT,APPROVAL_USER_CONTENT,RELEASE_USER_CONTENT,RECEVIE_USER_CONTENT,ORDER_CONTENT) VALUES
 (77,'法规文件','',13,0,'fgwj',0,34,35,1,to_date('2012-06-04 15:46:25','yyyy-mm-dd hh24:mi:ss'),18,0,0,0,NULL,0,'0||','0||','0||','0||','0||','0||','0||','0||','0||','0||');
INSERT INTO cms_column (SEQ_ID,COLUMN_NAME,COLUMN_TITLE,STATION_ID,PARENT_ID,COLUMN_PATH,ARCHIVE,TEMPLATE_INDEX_ID,TEMPLATE_ARTICLE_ID,CREATE_ID,CREATE_TIME,COLUMN_INDEX,PAGING,MAX_INDEX_PAGE,PAGING_NUMBER,URL,SHOW_MAIN,VISIT_USER,EDIT_USER,NEW_USER,DEL_USER,REL_USER,EDIT_USER_CONTENT,APPROVAL_USER_CONTENT,RELEASE_USER_CONTENT,RECEVIE_USER_CONTENT,ORDER_CONTENT) VALUES
 (78,'行政审批','',13,0,'xzsp',0,34,35,1,to_date('2012-06-04 15:46:25','yyyy-mm-dd hh24:mi:ss'),19,0,0,0,NULL,0,'0||','0||','0||','0||','0||','0||','0||','0||','0||','0||');
INSERT INTO cms_column (SEQ_ID,COLUMN_NAME,COLUMN_TITLE,STATION_ID,PARENT_ID,COLUMN_PATH,ARCHIVE,TEMPLATE_INDEX_ID,TEMPLATE_ARTICLE_ID,CREATE_ID,CREATE_TIME,COLUMN_INDEX,PAGING,MAX_INDEX_PAGE,PAGING_NUMBER,URL,SHOW_MAIN,VISIT_USER,EDIT_USER,NEW_USER,DEL_USER,REL_USER,EDIT_USER_CONTENT,APPROVAL_USER_CONTENT,RELEASE_USER_CONTENT,RECEVIE_USER_CONTENT,ORDER_CONTENT) VALUES
 (79,'新闻中心','',14,0,'xwzx',0,37,38,0,to_date('2012-06-04 15:46:25','yyyy-mm-dd hh24:mi:ss'),1,0,0,0,NULL,1,'0||','0||','0||','0||','0||','0||','0||','0||','0||','0||');
INSERT INTO cms_column (SEQ_ID,COLUMN_NAME,COLUMN_TITLE,STATION_ID,PARENT_ID,COLUMN_PATH,ARCHIVE,TEMPLATE_INDEX_ID,TEMPLATE_ARTICLE_ID,CREATE_ID,CREATE_TIME,COLUMN_INDEX,PAGING,MAX_INDEX_PAGE,PAGING_NUMBER,URL,SHOW_MAIN,VISIT_USER,EDIT_USER,NEW_USER,DEL_USER,REL_USER,EDIT_USER_CONTENT,APPROVAL_USER_CONTENT,RELEASE_USER_CONTENT,RECEVIE_USER_CONTENT,ORDER_CONTENT) VALUES
 (80,'集团动态','',14,0,'jtdt',0,37,38,1,to_date('2012-06-04 15:46:25','yyyy-mm-dd hh24:mi:ss'),2,0,0,0,NULL,0,'0||','0||','0||','0||','0||','0||','0||','0||','0||','0||');
INSERT INTO cms_column (SEQ_ID,COLUMN_NAME,COLUMN_TITLE,STATION_ID,PARENT_ID,COLUMN_PATH,ARCHIVE,TEMPLATE_INDEX_ID,TEMPLATE_ARTICLE_ID,CREATE_ID,CREATE_TIME,COLUMN_INDEX,PAGING,MAX_INDEX_PAGE,PAGING_NUMBER,URL,SHOW_MAIN,VISIT_USER,EDIT_USER,NEW_USER,DEL_USER,REL_USER,EDIT_USER_CONTENT,APPROVAL_USER_CONTENT,RELEASE_USER_CONTENT,RECEVIE_USER_CONTENT,ORDER_CONTENT) VALUES
 (81,'关于集团','',14,0,'gyjt',0,37,38,0,to_date('2012-06-04 15:46:25','yyyy-mm-dd hh24:mi:ss'),3,0,0,0,NULL,1,'0||','0||','0||','0||','0||','0||','0||','0||','0||','0||');
INSERT INTO cms_column (SEQ_ID,COLUMN_NAME,COLUMN_TITLE,STATION_ID,PARENT_ID,COLUMN_PATH,ARCHIVE,TEMPLATE_INDEX_ID,TEMPLATE_ARTICLE_ID,CREATE_ID,CREATE_TIME,COLUMN_INDEX,PAGING,MAX_INDEX_PAGE,PAGING_NUMBER,URL,SHOW_MAIN,VISIT_USER,EDIT_USER,NEW_USER,DEL_USER,REL_USER,EDIT_USER_CONTENT,APPROVAL_USER_CONTENT,RELEASE_USER_CONTENT,RECEVIE_USER_CONTENT,ORDER_CONTENT) VALUES
 (83,'产品信息','',14,0,'cpxx',0,37,38,1,to_date('2012-06-04 15:46:25','yyyy-mm-dd hh24:mi:ss'),4,0,0,0,NULL,1,'0||','0||','0||','0||','0||','0||','0||','0||','0||','0||');
INSERT INTO cms_column (SEQ_ID,COLUMN_NAME,COLUMN_TITLE,STATION_ID,PARENT_ID,COLUMN_PATH,ARCHIVE,TEMPLATE_INDEX_ID,TEMPLATE_ARTICLE_ID,CREATE_ID,CREATE_TIME,COLUMN_INDEX,PAGING,MAX_INDEX_PAGE,PAGING_NUMBER,URL,SHOW_MAIN,VISIT_USER,EDIT_USER,NEW_USER,DEL_USER,REL_USER,EDIT_USER_CONTENT,APPROVAL_USER_CONTENT,RELEASE_USER_CONTENT,RECEVIE_USER_CONTENT,ORDER_CONTENT) VALUES
 (84,'人才中心','',14,0,'rczx',0,37,38,1,to_date('2012-06-04 15:46:25','yyyy-mm-dd hh24:mi:ss'),5,0,0,0,NULL,1,'0||','0||','0||','0||','0||','0||','0||','0||','0||','0||');
INSERT INTO cms_column (SEQ_ID,COLUMN_NAME,COLUMN_TITLE,STATION_ID,PARENT_ID,COLUMN_PATH,ARCHIVE,TEMPLATE_INDEX_ID,TEMPLATE_ARTICLE_ID,CREATE_ID,CREATE_TIME,COLUMN_INDEX,PAGING,MAX_INDEX_PAGE,PAGING_NUMBER,URL,SHOW_MAIN,VISIT_USER,EDIT_USER,NEW_USER,DEL_USER,REL_USER,EDIT_USER_CONTENT,APPROVAL_USER_CONTENT,RELEASE_USER_CONTENT,RECEVIE_USER_CONTENT,ORDER_CONTENT) VALUES
 (85,'客户服务','',14,0,'khfw',0,37,38,1,to_date('2012-06-04 15:46:25','yyyy-mm-dd hh24:mi:ss'),6,0,0,0,NULL,1,'0||','0||','0||','0||','0||','0||','0||','0||','0||','0||');

INSERT INTO cms_station (SEQ_ID,STATION_NAME,STATION_DOMAIN_NAME,TEMPLATE_ID,STATION_FILE_NAME,EXTEND_NAME,ARTICLE_EXTEND_NAME,CREATE_ID,CREATE_TIME,STATION_PATH,URL,VISIT_USER,EDIT_USER,NEW_USER,DEL_USER,REL_USER) VALUES 
 (13,'政府','',33,NULL,'html','html',0,to_date('2012-06-04 14:27:31','yyyy-mm-dd hh24:mi:ss'),'zf',NULL,'0||','0||','0||','0||','0||');
INSERT INTO cms_station (SEQ_ID,STATION_NAME,STATION_DOMAIN_NAME,TEMPLATE_ID,STATION_FILE_NAME,EXTEND_NAME,ARTICLE_EXTEND_NAME,CREATE_ID,CREATE_TIME,STATION_PATH,URL,VISIT_USER,EDIT_USER,NEW_USER,DEL_USER,REL_USER) VALUES 
 (14,'公司','',36,NULL,'html','html',0,to_date('2012-06-04 14:27:31','yyyy-mm-dd hh24:mi:ss'),'gs',NULL,'0||','0||','0||','0||','0||');
INSERT INTO cms_template (SEQ_ID,TEMPLATE_NAME,TEMPLATE_FILE_NAME,TEMPLATE_TYPE,CREATE_ID,CREATE_TIME,attachment_Id,attachment_Name,station_id) VALUES 
 (33,'首页模板','index',1,1,to_date('2012-06-04 15:21:56','yyyy-mm-dd hh24:mi:ss'),'2b85b8258e5ec06f705c48f9d2b895aa','index.html',13);
INSERT INTO cms_template (SEQ_ID,TEMPLATE_NAME,TEMPLATE_FILE_NAME,TEMPLATE_TYPE,CREATE_ID,CREATE_TIME,attachment_Id,attachment_Name,station_id) VALUES
 (34,'栏目模板','index',1,0,to_date('2012-06-04 15:21:56','yyyy-mm-dd hh24:mi:ss'),'0ebf0657ac0423bbf54aa145943560b8','index.html',13);
INSERT INTO cms_template (SEQ_ID,TEMPLATE_NAME,TEMPLATE_FILE_NAME,TEMPLATE_TYPE,CREATE_ID,CREATE_TIME,attachment_Id,attachment_Name,station_id) VALUES
 (35,'文章模板','index',2,1,to_date('2012-06-04 15:21:56','yyyy-mm-dd hh24:mi:ss'),'57ba1f04a25938a298d25841cba8bc26','index.html',13);
INSERT INTO cms_template (SEQ_ID,TEMPLATE_NAME,TEMPLATE_FILE_NAME,TEMPLATE_TYPE,CREATE_ID,CREATE_TIME,attachment_Id,attachment_Name,station_id) VALUES
 (36,'首页模板','index',1,1,to_date('2012-06-04 15:21:56','yyyy-mm-dd hh24:mi:ss'),'f13231204ae042e8b417c0330b63f1ff','index.html',14);
INSERT INTO cms_template (SEQ_ID,TEMPLATE_NAME,TEMPLATE_FILE_NAME,TEMPLATE_TYPE,CREATE_ID,CREATE_TIME,attachment_Id,attachment_Name,station_id) VALUES
 (37,'栏目模板','index',1,0,to_date('2012-06-04 15:21:56','yyyy-mm-dd hh24:mi:ss'),'dc216a3a82387150318ab06b5544d629','index.html',14);
INSERT INTO cms_template (SEQ_ID,TEMPLATE_NAME,TEMPLATE_FILE_NAME,TEMPLATE_TYPE,CREATE_ID,CREATE_TIME,attachment_Id,attachment_Name,station_id) VALUES
 (38,'文章模板','index',2,1,to_date('2012-06-04 15:21:56','yyyy-mm-dd hh24:mi:ss'),'29f3a56adfe42d3e1856e9dd48629a06','index.html',14);
