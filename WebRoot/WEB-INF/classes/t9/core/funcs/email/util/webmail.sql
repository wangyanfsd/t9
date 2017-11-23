--mysql
create table WEBMAIL_BODY(
  SEQ_ID INT auto_increment  PRIMARY KEY ,
  BODY_ID INT UNIQUE NOT NULL,
  REPLY_MAIL VARCHAR(200),
  TO_MAIL VARCHAR(200),
  CC_MAIL VARCHAR(200),
  SUBJECT VARCHAR(500),
  IS_HTML CHAR(1),
  LARGE_ATTACHMENT CHAR(1),
  ATTACHMENT_ID VARCHAR(1000),
  ATTACHMENT_NAME VARCHAR(1000),
  CONTENT_HTML TEXT,
  WEBMAIL_UID VARCHAR(200),
  SEND_DATE DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table WEBMAIL(
  SEQ_ID INT auto_increment  PRIMARY KEY ,
  EMAIL VARCHAR(50) ,
  USER_ID INT,
  POP_SERVER VARCHAR(200),
  SMTP_SERVER VARCHAR(200),
  LOGIN_TYPE VARCHAR(50),
  SMTP_PASS VARCHAR(200),
  EMAIL_USER VARCHAR(200),
  EMAIL_PASS VARCHAR(200),
  POP3_PORT VARCHAR(10),
  SMTP_PORT VARCHAR(10),
  IS_DEFAULT CHAR(1),
  POP3_SSL VARCHAR(10),
  SMTP_SSL VARCHAR(10),
  QUOTA_LIMIT VARCHAR(50),
  EMAIL_UID text,
  CHECK_FLAG CHAR(1),
  PRIORITY VARCHAR(200),
  RECV_DEL CHAR(1)
) ENGINE=InnoDB DEFAULT CHARSET=utf8; 

alter table EMAIL_BODY Add column FROM_WEBMAIL_ID VARCHAR(500); 
alter table EMAIL_BODY Add column WEBMAIL_CONTENT  TEXT;
alter table EMAIL_BODY Add column WEBMAIL_FLAG  VARCHAR(20);
alter table EMAIL_BODY Add column RECV_FROM_NAME VARCHAR(200) ;
alter table EMAIL_BODY Add column RECV_FROM  VARCHAR(500);
alter table EMAIL_BODY Add column RECV_TO_ID  VARCHAR(500);
alter table EMAIL_BODY Add column RECV_TO  VARCHAR(500);
alter table EMAIL_BODY Add column IS_WEBMAIL  CHAR(1);

alter table WEBMAIL_BODY Add column SEND_DATE DATETIME;
alter table WEBMAIL_BODY Add column MAIL_SIZE int(38);
--oracle
create table WEBMAIL(
  SEQ_ID INT PRIMARY KEY ,
  EMAIL VARCHAR(50) ,
  USER_ID INT,
  POP_SERVER VARCHAR(200),
  SMTP_SERVER VARCHAR(200),
  LOGIN_TYPE VARCHAR(50),
  SMTP_PASS VARCHAR(200),
  EMAIL_USER VARCHAR(200),
  EMAIL_PASS VARCHAR(200),
  POP3_PORT VARCHAR(10),
  SMTP_PORT VARCHAR(10),
  IS_DEFAULT CHAR(1),
  POP3_SSL VARCHAR(10),
  SMTP_SSL VARCHAR(10),
  QUOTA_LIMIT VARCHAR(50),
  EMAIL_UID clob,
  CHECK_FLAG CHAR(1),
  PRIORITY VARCHAR(200),
  RECV_DEL CHAR(1)
); 
exec pr_createidentitycolumn('WEBMAIL','SEQ_ID');

create table WEBMAIL_BODY(
  SEQ_ID INT PRIMARY KEY ,
  BODY_ID INT UNIQUE NOT NULL,
  REPLY_MAIL VARCHAR(200),
  
  TO_MAIL VARCHAR(200),
  CC_MAIL VARCHAR(200),
  SUBJECT VARCHAR(500),
  IS_HTML CHAR(1),
  LARGE_ATTACHMENT VARCHAR(1000),
  ATTACHMENT_ID VARCHAR(1000),
  ATTACHMENT_NAME VARCHAR(1000),
  CONTENT_HTML clob,
  WEBMAIL_UID VARCHAR(200),
  SEND_DATE DATE
);
exec pr_createidentitycolumn('WEBMAIL_BODY','SEQ_ID');

alter table EMAIL_BODY Add  FROM_WEBMAIL_ID VARCHAR(500); 
alter table EMAIL_BODY Add  WEBMAIL_CONTENT  CLOB;
alter table EMAIL_BODY Add  WEBMAIL_FLAG  VARCHAR(20);
alter table EMAIL_BODY Add  RECV_FROM_NAME VARCHAR(200) ;
alter table EMAIL_BODY Add  RECV_FROM  VARCHAR(500);
alter table EMAIL_BODY Add  RECV_TO_ID  VARCHAR(500);
alter table EMAIL_BODY Add  RECV_TO  VARCHAR(500);
alter table EMAIL_BODY Add  IS_WEBMAIL  CHAR(1);


