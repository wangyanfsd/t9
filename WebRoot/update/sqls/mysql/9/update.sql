INSERT INTO hr_code (CODE_NO,CODE_NAME,CODE_ORDER,PARENT_NO,CODE_FLAG) VALUES  
 ('HR_RECRUIT_PLAN1','招聘方式','1','','0'),
 ('1','网络招聘','1','HR_RECRUIT_PLAN1','0'),
 ('2','现场招聘','2','HR_RECRUIT_PLAN1','0');
 
update  hr_code set code_NAME = '籍贯' WHERE CODE_NO = 'AREA';