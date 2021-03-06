package t9.core.funcs.filefolder.logic;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import t9.core.funcs.email.logic.T9InnerEMailUtilLogic;
import t9.core.funcs.filefolder.data.T9FileContent;
import t9.core.funcs.mobilesms.logic.T9MobileSms2Logic;
import t9.core.funcs.office.ntko.logic.T9NtkoLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.portal.util.T9PortalProducer;
import t9.core.funcs.portal.util.rules.T9ImgRule;
import t9.core.funcs.portal.util.rules.T9LinkRule;
import t9.core.funcs.sms.data.T9SmsBack;
import t9.core.funcs.sms.logic.T9SmsUtil;
import t9.core.funcs.system.filefolder.data.T9FileSort;
import t9.core.funcs.system.filefolder.logic.T9FileSortLogic;
import t9.core.funcs.system.syslog.logic.T9SysLogLogic;
import t9.core.global.T9Const;
import t9.core.global.T9LogConst;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.file.T9FileUtility;

public class T9FileContentLogic {
	private static Logger log = Logger.getLogger("t9.core.funcs.filefolder.logic.T9FileContentLogic");
	public static String COPYPATH = File.separator + "core" + File.separator + "funcs" + File.separator + "filefolder" + File.separator + "fileUtil";

	public List<T9FileContent> getFileContentsInfo(Connection dbConn, Map map) throws Exception {
		T9ORM orm = new T9ORM();
		return orm.loadListSingle(dbConn, T9FileContent.class, map);

	}

	public List<T9FileContent> getFileContentsByFilters(Connection dbConn, String[] filters) throws Exception {
		T9ORM orm = new T9ORM();
		return orm.loadListSingle(dbConn, T9FileContent.class, filters);

	}

	public void saveSingleObj(Connection dbConn, T9FileContent fileContent) throws Exception {
		T9ORM orm = new T9ORM();
		orm.saveSingle(dbConn, fileContent);
	}

	public String createFile(String fileType, String fileName, String webrootPath) throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyMM");
		String currDate = format.format(new Date());
		String separator = File.separator;
		String filePath = T9SysProps.getAttachPath() + separator + "file_folder" + separator + currDate + separator;

		T9InnerEMailUtilLogic emut = new T9InnerEMailUtilLogic();
		String rand = emut.getRandom();
		String newFileName = rand + "_" + fileName + "." + fileType;
		String tmp = filePath + newFileName;

		String type = fileType.trim();
		if ("xls".equals(type)) {
			String srcFile = webrootPath + this.COPYPATH + File.separator + "copy.xls";
			T9FileUtility.copyFile(srcFile, tmp);
		} else if ("ppt".equals(type)) {
			String srcFile = webrootPath + this.COPYPATH + File.separator + "copy.ppt";
			T9FileUtility.copyFile(srcFile, tmp);
		} else if ("doc".equals(type)) {
			String srcFile = webrootPath + this.COPYPATH + File.separator + "copy.doc";
			T9FileUtility.copyFile(srcFile, tmp);
		} else {
			File file = new File(filePath);
			if (!file.exists()) {
				file.mkdirs();
			}
			String createPath = file.getPath().replace("\\", "/");
			File createFile = new File(createPath + "/" + newFileName);
			createFile.createNewFile();
		}

		return rand;

	}

	/**
	 * 批量上传文件
	 */
	public String uploadFileLogic(Connection dbConn, T9FileContent content, T9FileUploadForm fileForm,T9Person loginUser,String seqId,String remoteAddr,String filePath) throws Exception {
		T9ORM orm = new T9ORM();
		// 获取登录用户信息
		int loginUserSeqId = loginUser.getSeqId();
		int loginUserDeptId = loginUser.getDeptId();
		String loginUserRoleId = loginUser.getUserPriv();

		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyMM");
		String currDate = format.format(date);
		int sortId = 0;
		if (seqId != null) {
			sortId = Integer.parseInt(seqId);
		}
		String smsPerson = fileForm.getParameter("smsPerson");
		if (smsPerson == null) {
			smsPerson = "";
		}
		String mobileSmsPerson = fileForm.getParameter("mobileSmsPerson");
		if (mobileSmsPerson == null) {
			mobileSmsPerson = "";
		}
		String folderPath = fileForm.getParameter("folderPath");
		if (folderPath == null) {
			folderPath = "";
		}
		String subjectStr = "";
		try {
			Iterator<String> keysIte = fileForm.iterateFileFields();
			while (keysIte.hasNext()) {
				String fieldName = keysIte.next();
				String fileName = fileForm.getFileName(fieldName);
				if (T9Utility.isNullorEmpty(fileName)) {
					continue;
				}
				String nameTitle = "";
				String newSubject = "";
				if (fileName.lastIndexOf(".") != -1) {
					nameTitle = fileName.substring(0, fileName.lastIndexOf("."));
				}
				boolean haveFile = this.isExistFile(dbConn, sortId, nameTitle);
				if (haveFile) {
					StringBuffer buffer = new StringBuffer();
					this.copyExistFile(dbConn, buffer, sortId, nameTitle);
					newSubject = buffer.toString().trim();
				}else {
					newSubject = nameTitle;
				}
				
				T9InnerEMailUtilLogic emut = new T9InnerEMailUtilLogic();
				content.setSortId(sortId);
				content.setSendTime(T9Utility.parseTimeStamp());
				content.setAttachmentName(fileName.trim() + "*");
//				String[] fName = fileName.split("\\.");
				content.setSubject(newSubject);
				subjectStr = newSubject; // J01786看舒

				String rand = emut.getRandom();
				fileName = rand + "_" + fileName;
				fileForm.saveFile(fieldName, filePath + File.separator + fileName);
				
				content.setAttachmentId(currDate + "_" + String.valueOf(rand) + ",");
				content.setCreater(String.valueOf(loginUserSeqId));
			}
			orm.saveSingle(dbConn, content);
			T9FileContent maxContent = this.getMaxSeqId(dbConn);
			int contentId = maxContent.getSeqId();
			// 系统日志
			String remark = "新建文件,名称:" + subjectStr;
			T9SysLogLogic.addSysLog(dbConn, T9LogConst.FILE_FOLDER, remark, loginUserSeqId, remoteAddr);

			// 短信提醒
			// T9SmsUtil sms=new T9SmsUtil();
			T9SmsBack sms = new T9SmsBack();
			String loginName = this.getPersonNamesByIds(dbConn, String.valueOf(loginUserSeqId));
			String smsContent = loginName + " 在公共文件柜" + folderPath + " 下建立新文件:" + subjectStr;
			String remindUrl = "/core/funcs/filefolder/read.jsp?sortId=" + sortId + "&contentId=" + contentId + "&newFileFlag=1&openFlag=1";
			if ("allPrivPerson".equals(smsPerson)) {
				T9FileSortLogic logic = new T9FileSortLogic();
				T9FileSort fileSort = logic.getFileSortInfoById(dbConn, String.valueOf(sortId));
				String deptIdStr = logic.getDeptIds(dbConn, fileSort, "USER_ID");
				String roleIdStr = logic.getRoleIds(dbConn, fileSort, "USER_ID");
				String personIdStr = logic.selectManagerIds(dbConn, fileSort, "USER_ID");

				if (!"".equals(personIdStr)) {
					personIdStr += ",";
				}
				// 判断这个部门是否有权限				// String deptPrivIdStrs = logic.getPrivDeptIdStr(dbConn,
				// loginUserDeptId, deptIdStr);
				// String rolePrivIdStrs = logic.getPrivRoleIdStr(dbConn,
				// Integer.parseInt(loginUserRoleId), roleIdStr);
				// // 如有权限，获取该部门下的所有人员id串				// String deptPersonIdStr = logic.getDeptPersonIdStr(loginUserDeptId,
				// deptPrivIdStrs, dbConn);
				// String rolePersonIdStr =
				// logic.getRolePersonIdStr(Integer.parseInt(loginUserRoleId),
				// rolePrivIdStrs, dbConn);

				String deptPersonIdStr = "";
				String rolePersonIdStr = "";
				if (!T9Utility.isNullorEmpty(deptIdStr)) {
					deptPersonIdStr = logic.getDeptPersonIds(deptIdStr, dbConn);
				}
				if (!T9Utility.isNullorEmpty(roleIdStr)) {
					rolePersonIdStr = logic.getRolePersonIds(roleIdStr, dbConn);
				}

				String allPersonIdStr = personIdStr + deptPersonIdStr + rolePersonIdStr;
				String allpersonStr = "";
				ArrayList al = new ArrayList();
				String[] arr = allPersonIdStr.split(",");
				for (int i = 0; i < arr.length; i++) {
					if (al.contains(arr[i]) == false) {
						al.add(arr[i]);
						allpersonStr += arr[i] + ",";
					}
				}
				if (allpersonStr != null && !"".equals(allpersonStr)) {
					sms.setFromId(loginUserSeqId);
					sms.setToId(allpersonStr.trim());
					sms.setContent(smsContent);
					sms.setSendDate(T9Utility.parseTimeStamp());
					sms.setSmsType(T9LogConst.FILE_FOLDER);
					sms.setRemindUrl(remindUrl);
					T9SmsUtil.smsBack(dbConn, sms);
				}

			} else if (!"".equals(smsPerson)) {
				sms.setFromId(loginUserSeqId);
				sms.setToId(smsPerson);
				sms.setContent(smsContent);
				sms.setSendDate(T9Utility.parseTimeStamp());
				sms.setSmsType(T9LogConst.FILE_FOLDER);
				sms.setRemindUrl(remindUrl);
				T9SmsUtil.smsBack(dbConn, sms);
			}
			// 手机短信提醒
			String mobileSmsContent = loginName + " 在公共文件柜 " + folderPath + " 下建立新文件:" + subjectStr;
			T9MobileSms2Logic mobileSms = new T9MobileSms2Logic();
			if ("allPrivPerson".equals(mobileSmsPerson.trim())) {
				T9FileSortLogic logic = new T9FileSortLogic();
				T9FileSort fileSort = logic.getFileSortInfoById(dbConn, String.valueOf(sortId));
				String deptIdStr = logic.getDeptIds(dbConn, fileSort, "USER_ID");
				String roleIdStr = logic.getRoleIds(dbConn, fileSort, "USER_ID");
				String personIdStr = logic.selectManagerIds(dbConn, fileSort, "USER_ID");
				if (!"".equals(personIdStr)) {
					personIdStr += ",";
				}
				// 判断这个部门是否有权限				String deptPrivIdStrs = logic.getPrivDeptIdStr(dbConn, loginUserDeptId, deptIdStr);
				String rolePrivIdStrs = logic.getPrivRoleIdStr(dbConn, Integer.parseInt(loginUserRoleId), roleIdStr);
				// 如有权限，获取该部门下的所有人员id串				String deptPersonIdStr = logic.getDeptPersonIdStr(loginUserDeptId, deptPrivIdStrs, dbConn);
				String rolePersonIdStr = logic.getRolePersonIdStr(Integer.parseInt(loginUserRoleId), rolePrivIdStrs, dbConn);
				String allPersonIdStr = personIdStr + deptPersonIdStr + rolePersonIdStr;
				String allpersonStr = "";
				ArrayList al = new ArrayList();
				String[] arr = allPersonIdStr.split(",");
				for (int i = 0; i < arr.length; i++) {
					if (al.contains(arr[i]) == false) {
						al.add(arr[i]);
						allpersonStr += arr[i] + ",";
					}
				}
				if (allpersonStr != null && !"".equals(allpersonStr)) {
					mobileSms.remindByMobileSms(dbConn, allpersonStr, loginUserSeqId, mobileSmsContent, null);
				}
			} else if (!"".equals(mobileSmsPerson.trim())) {
				mobileSms.remindByMobileSms(dbConn, mobileSmsPerson, loginUserSeqId, mobileSmsContent, null);
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}

	public T9FileContent getFileContentInfoById(Connection dbConn, int contentId) throws Exception {
		T9ORM orm = new T9ORM();
		return (T9FileContent) orm.loadObjSingle(dbConn, T9FileContent.class, contentId);
	}

	public void updateSingleObj(Connection dbConn, T9FileContent fileContent) throws Exception {
		T9ORM orm = new T9ORM();
		orm.updateSingle(dbConn, fileContent);
	}

	public void updataFileInfoByObj(Connection dbConn, T9FileContent content) throws Exception {
		T9ORM orm = new T9ORM();
		orm.updateSingle(dbConn, content);
	}

	/**
	 * 获取查询文件信息
	 * 
	 * @param dbConn
	 * @param map
	 * @param loginUser
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, String>> queryFileContentInfoById(Connection dbConn, Map map, T9Person loginUser, int currNo, int pageSize)
			throws Exception {
		String filePath = T9SysProps.getAttachPath() + File.separator + "file_folder";
		List list = new ArrayList();
		Statement stmt = null;
		ResultSet rs = null;
		if (currNo == 0) {
			currNo = 1;
		} else {
			currNo = currNo + 1;
		}

		String seqIdStr = (String) map.get("seqId");
		String subject = (String) map.get("subject");
		String contentNo = (String) map.get("contentNo");

		String key1 = (String) map.get("key1");
		String key2 = (String) map.get("key2");
		String key3 = (String) map.get("key3");

		String attachmentDesc = (String) map.get("attachmentDesc");
		String attachmentName = (String) map.get("attachmentName");
		String attachmentData = (String) map.get("attachmentData");
		String sendTimeMin = (String) map.get("sendTimeMin");
		String sendTimeMax = (String) map.get("sendTimeMax");

		T9ORM orm = new T9ORM();

		int loginUserSeqId = loginUser.getSeqId();
		String loginUserSeqIdStr = String.valueOf(loginUserSeqId);

		int seqId = 0;
		if (!T9Utility.isNullorEmpty(seqIdStr)) {
			seqId = Integer.parseInt(seqIdStr);
		}

		if (T9Utility.isNullorEmpty(subject)) {
			subject = "";
		}
		if (T9Utility.isNullorEmpty(contentNo)) {
			contentNo = "";
		}
		if (T9Utility.isNullorEmpty(key1)) {
			key1 = "";
		}
		if (T9Utility.isNullorEmpty(key2)) {
			key2 = "";
		}
		if (T9Utility.isNullorEmpty(key3)) {
			key3 = "";
		}
		if (T9Utility.isNullorEmpty(attachmentDesc)) {
			attachmentDesc = "";
		}
		if (T9Utility.isNullorEmpty(attachmentName)) {
			attachmentName = "";
		}
		if (T9Utility.isNullorEmpty(attachmentData)) {
			attachmentData = "";
		}
		if (T9Utility.isNullorEmpty(sendTimeMin)) {
			sendTimeMin = "";
		}
		if (T9Utility.isNullorEmpty(sendTimeMax)) {
			sendTimeMax = "";
		}

		T9FileSort fileSort = new T9FileSort();
		T9FileSortLogic sysLogic = new T9FileSortLogic();

		boolean accessPriv = false;
		int managePriv = 0;
		int downPriv = 0;
		int newPriv = 0;

		if (seqId != 0) {
			fileSort = (T9FileSort) orm.loadObjSingle(dbConn, T9FileSort.class, seqId);
			if (fileSort != null) {
				String sortUserIds = fileSort.getUserId() == null ? "" : fileSort.getUserId();
				// 访问权限(包括owner权限)
				accessPriv = sysLogic.getAccessPriv(dbConn, loginUser, fileSort);
				if (loginUserSeqIdStr.equals(sortUserIds.trim()) || accessPriv) {
					accessPriv = true;
				}
				boolean manageUserPriv = sysLogic.getManageAccessPriv(dbConn, loginUser, fileSort);
				boolean downUserPriv = sysLogic.getDownAccessPriv(dbConn, loginUser, fileSort);
				boolean newUserPriv = sysLogic.getNewUserAccessPriv(dbConn, loginUser, fileSort);
				if (loginUserSeqIdStr.equals(sortUserIds.trim()) || manageUserPriv) {
					managePriv = 1;
				}
				if (loginUserSeqIdStr.equals(sortUserIds.trim()) || downUserPriv) {
					downPriv = 1;
				}
				if (loginUserSeqIdStr.equals(sortUserIds.trim()) || newUserPriv) {
					newPriv = 1;
				}
			}
		} else {
			accessPriv = true;
			managePriv = 1;
			downPriv = 1;
			newPriv = 1;
		}
		if (!accessPriv) {
			return list;
		}
		String where_str = "";
		if (!T9Utility.isNullorEmpty(subject.trim())) {
			where_str += " and SUBJECT like '%" + T9DBUtility.escapeLike(subject) + "%'" + T9DBUtility.escapeLike();
		}
		if (!T9Utility.isNullorEmpty(contentNo.trim())) {
			where_str += " and CONTENT_NO like '%" + T9DBUtility.escapeLike(contentNo) + "%'" + T9DBUtility.escapeLike();
		}

		if (!T9Utility.isNullorEmpty(attachmentDesc.trim())) {
			where_str += " and ATTACHMENT_DESC like '%" + T9DBUtility.escapeLike(attachmentDesc) + "%'" + T9DBUtility.escapeLike();
		}
		if (!T9Utility.isNullorEmpty(key1.trim())) {
			where_str += " and CONTENT like '%" + T9DBUtility.escapeLike(key1) + "%'" + T9DBUtility.escapeLike();
		}
		if (!T9Utility.isNullorEmpty(key2.trim())) {
			where_str += " and CONTENT like '%" + T9DBUtility.escapeLike(key2) + "%'" + T9DBUtility.escapeLike();
		}

		if (!T9Utility.isNullorEmpty(key3.trim())) {
			where_str += " and CONTENT like '%" + T9DBUtility.escapeLike(key3) + "%'" + T9DBUtility.escapeLike();
		}

		if (!T9Utility.isNullorEmpty(attachmentName.trim())) {
			where_str += " and ATTACHMENT_NAME like '%" + T9DBUtility.escapeLike(attachmentName) + "%'" + T9DBUtility.escapeLike();
		}
		if (!T9Utility.isNullorEmpty(sendTimeMin.trim())) {
			String temp = T9DBUtility.getDateFilter("SEND_TIME", sendTimeMin.trim(), ">=");
			where_str += " and " + temp;
		}
		if (!T9Utility.isNullorEmpty(sendTimeMax.trim())) {
			String temp = T9DBUtility.getDateFilter("SEND_TIME", sendTimeMax.trim(), "<="); // to_char(SEND_TIME,
			where_str += " and " + temp;
		}

		String query = "";
		if (seqId != 0) {
			query = "SELECT * from FILE_CONTENT where SORT_ID=" + seqId + where_str;
		} else {
			query = "SELECT * from FILE_CONTENT where SORT_ID=" + seqId + " and USER_ID='" + loginUserSeqId + "'" + where_str;
		}
		query += " order by CONTENT_NO,SEND_TIME desc ";

		try {
			stmt = dbConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			rs = stmt.executeQuery(query);
			rs.last();
			int maxRow = rs.getRow();
			rs.beforeFirst();
			int count = 0;
			rs.absolute((currNo - 1) * pageSize + 1);
			if (maxRow > 0) {
				do {
					String dbAttachmentIds = T9Utility.encodeSpecial(rs.getString("ATTACHMENT_ID"));
					String dbAttachmentNames = T9Utility.encodeSpecial(rs.getString("ATTACHMENT_NAME"));
					if (!"".equals(attachmentData.trim()) && "".equals(dbAttachmentNames.trim())) {
						continue;
					}

					// 匹配文件里的内容
					if (!T9Utility.isNullorEmpty(attachmentData) && !T9Utility.isNullorEmpty(dbAttachmentNames)) {
						String[] attIdArray = dbAttachmentIds.trim().split(",");
						String[] attNameArray = dbAttachmentNames.trim().split("\\*");
						int contentValue = -1;
						for (int i = 0; i < attIdArray.length; i++) {
							String attId = this.getAttaId(attIdArray[i]);
							String attFolder = this.getFilePathFolder(attIdArray[i]);
							String newAttName = attId + "_" + attNameArray[i];
							String oldAttName = attId + "." + attNameArray[i];

							String newFilePath = filePath + "/" + attFolder + "/" + newAttName;
							String oldFilePath = filePath + "/" + attFolder + "/" + oldAttName;
							File newFile = new File(newFilePath);
							File oldFile = new File(oldFilePath);

							String fileType = "";
							String attName = attNameArray[i];
							if (attName.trim().lastIndexOf(".") != -1) {
								fileType = attName.substring(attName.trim().lastIndexOf(".")); // .doc
							}

							StringBuffer buffer = new StringBuffer();
							if (newFile.exists()) {
								long totalSpace = newFile.length();
								if (".htm".equals(fileType.trim()) || ".html".equals(fileType.trim())) {
									if (totalSpace < 500 * 1024 * 1024) {
										buffer = T9FileUtility.loadLine2Buff(newFile.getAbsolutePath(), "GBK");
										contentValue = buffer.indexOf(attachmentData.trim());
										if (contentValue == -1) {
											buffer = T9FileUtility.loadLine2Buff(newFile.getAbsolutePath(), "UTF-8");
											contentValue = buffer.indexOf(attachmentData.trim());
										}
									} else {
										buffer = T9FileUtility.loadLine2Buff(newFile.getAbsolutePath(), 0, 500, "GBK");
										contentValue = buffer.indexOf(attachmentData.trim());
										if (contentValue == -1) {
											buffer = T9FileUtility.loadLine2Buff(newFile.getAbsolutePath(), 0, 500, "UTF-8");
											contentValue = buffer.indexOf(attachmentData.trim());
										}
									}
								} else if (".txt".equals(fileType.trim())) {
									if (totalSpace < 500 * 1024 * 1024) {
										buffer = T9FileUtility.loadLine2Buff(newFile.getAbsolutePath(), "GBK");
										contentValue = buffer.indexOf(attachmentData.trim());
										if (contentValue == -1) {
											buffer = T9FileUtility.loadLine2Buff(newFile.getAbsolutePath(), "UTF-8");
											contentValue = buffer.indexOf(attachmentData.trim());
										}
									} else {
										buffer = T9FileUtility.loadLine2Buff(newFile.getAbsolutePath(), 0, 500, "GBK");
										contentValue = buffer.indexOf(attachmentData.trim());
										if (contentValue == -1) {
											buffer = T9FileUtility.loadLine2Buff(newFile.getAbsolutePath(), 0, 500, "UTF-8");
											contentValue = buffer.indexOf(attachmentData.trim());
										}
									}
								}
								if (contentValue >= 0) {
									break;
								}
							} else if (oldFile.exists()) {
								long totalSpace = oldFile.length();
								if (".htm".equals(fileType.trim()) || ".html".equals(fileType.trim())) {
									if (totalSpace < 500 * 1024 * 1024) {
										buffer = T9FileUtility.loadLine2Buff(oldFile.getAbsolutePath(), "GBK");
										contentValue = buffer.indexOf(attachmentData.trim());
										if (contentValue == -1) {
											buffer = T9FileUtility.loadLine2Buff(oldFile.getAbsolutePath(), "UTF-8");
											contentValue = buffer.indexOf(attachmentData.trim());
										}
									} else {
										buffer = T9FileUtility.loadLine2Buff(oldFile.getAbsolutePath(), 0, 500, "GBK");
										contentValue = buffer.indexOf(attachmentData.trim());
										if (contentValue == -1) {
											buffer = T9FileUtility.loadLine2Buff(oldFile.getAbsolutePath(), 0, 500, "UTF-8");
											contentValue = buffer.indexOf(attachmentData.trim());
										}
									}
								} else if (".txt".equals(fileType.trim())) {
									if (totalSpace < 500 * 1024 * 1024) {
										buffer = T9FileUtility.loadLine2Buff(oldFile.getAbsolutePath(), "GBK");
										contentValue = buffer.indexOf(attachmentData.trim());
										if (contentValue == -1) {
											buffer = T9FileUtility.loadLine2Buff(oldFile.getAbsolutePath(), "UTF-8");
											contentValue = buffer.indexOf(attachmentData.trim());
										}
									} else {
										buffer = T9FileUtility.loadLine2Buff(oldFile.getAbsolutePath(), 0, 500, "GBK");
										contentValue = buffer.indexOf(attachmentData.trim());
										if (contentValue == -1) {
											buffer = T9FileUtility.loadLine2Buff(oldFile.getAbsolutePath(), 0, 500, "UTF-8");
											contentValue = buffer.indexOf(attachmentData.trim());
										}
									}
								}
								if (contentValue >= 0) {
									break;
								}
							} else {
								break;
							}
						}
						if (contentValue == -1) {
							continue;
						}
					}
					Map<String, String> map2 = new HashMap<String, String>();
					map2.put("contentId", rs.getString("SEQ_ID"));
					map2.put("sortId", rs.getString("SORT_ID"));
					map2.put("subject", rs.getString("SUBJECT"));
					map2.put("sendTime", T9Utility.getDateTimeStr(rs.getTimestamp("SEND_TIME"))); 

					map2.put("attachmentId", rs.getString("ATTACHMENT_ID"));
					map2.put("attachmentName", rs.getString("ATTACHMENT_NAME"));
					map2.put("attachmentDesc", rs.getString("ATTACHMENT_DESC"));
					map2.put("maxRow", String.valueOf(maxRow));
					list.add(map2);
				} while (rs.next() && ++count < pageSize);
			}

		} catch (SQLException e) {
			throw e;
		} finally {
			T9DBUtility.close(stmt, rs, log);
		}

		return list;

	}

	/**
	 * 全局搜索
	 * 
	 * @param dbConn
	 * @param map
	 * @param loginUser
	 * @return
	 * @throws Exception
	 */
	public List<Map<Object, Object>> getGlobalFileContentsByList(Connection dbConn, Map map, T9Person loginUser) throws Exception {
		String filePath = T9SysProps.getAttachPath() + File.separator + "file_folder";
		T9FileSortLogic fileSortLogic = new T9FileSortLogic();
		List list = new ArrayList();

		Statement stmt = null;
		ResultSet rs = null;

		String subject = (String) map.get("subject");
		String contentNo = (String) map.get("contentNo");

		String key1 = (String) map.get("key1");
		String key2 = (String) map.get("key2");
		String key3 = (String) map.get("key3");

		String attachmentDesc = (String) map.get("attachmentDesc");
		String attachmentName = (String) map.get("attachmentName");
		String attachmentData = (String) map.get("attachmentData");
		String sendTimeMin = (String) map.get("sendTimeMin");
		String sendTimeMax = (String) map.get("sendTimeMax");

		if (T9Utility.isNullorEmpty(subject)) {
			subject = "";
		}
		if (T9Utility.isNullorEmpty(contentNo)) {
			contentNo = "";
		}
		if (T9Utility.isNullorEmpty(key1)) {
			key1 = "";
		}
		if (T9Utility.isNullorEmpty(key2)) {
			key2 = "";
		}
		if (T9Utility.isNullorEmpty(key3)) {
			key3 = "";
		}
		if (T9Utility.isNullorEmpty(attachmentDesc)) {
			attachmentDesc = "";
		}
		if (T9Utility.isNullorEmpty(attachmentName)) {
			attachmentName = "";
		}
		if (T9Utility.isNullorEmpty(attachmentData)) {
			attachmentData = "";
		}
		if (T9Utility.isNullorEmpty(sendTimeMin)) {
			sendTimeMin = "";
		}

		if (T9Utility.isNullorEmpty(sendTimeMax)) {
			sendTimeMax = "";
		}

		// 搜索条件
		String where_str = "";
		if (!T9Utility.isNullorEmpty(subject.trim())) {
			where_str += " and SUBJECT like '%" + T9DBUtility.escapeLike(subject) + "%'" + T9DBUtility.escapeLike();
		}
		if (!T9Utility.isNullorEmpty(contentNo.trim())) {
			where_str += " and CONTENT_NO like '%" + T9DBUtility.escapeLike(contentNo) + "%'" + T9DBUtility.escapeLike();
		}

		if (!T9Utility.isNullorEmpty(attachmentDesc.trim())) {
			where_str += " and ATTACHMENT_DESC like '%" + T9DBUtility.escapeLike(attachmentDesc) + "%'" + T9DBUtility.escapeLike();
		}
		if (!T9Utility.isNullorEmpty(key1.trim())) {
			where_str += " and CONTENT like '%" + T9DBUtility.escapeLike(key1) + "%'" + T9DBUtility.escapeLike();
		}
		if (!T9Utility.isNullorEmpty(key2.trim())) {
			where_str += " and CONTENT like '%" + T9DBUtility.escapeLike(key2) + "%'" + T9DBUtility.escapeLike();
		}

		if (!T9Utility.isNullorEmpty(key3.trim())) {
			where_str += " and CONTENT like '%" + T9DBUtility.escapeLike(key3) + "%'" + T9DBUtility.escapeLike();
		}

		if (!T9Utility.isNullorEmpty(attachmentName.trim())) {
			where_str += " and ATTACHMENT_NAME like '%" + T9DBUtility.escapeLike(attachmentName) + "%'" + T9DBUtility.escapeLike();
		}
		if (!T9Utility.isNullorEmpty(sendTimeMin.trim())) {
			String temp = T9DBUtility.getDateFilter("SEND_TIME", sendTimeMin.trim(), ">=");
			where_str += " and " + temp;
		}
		if (!T9Utility.isNullorEmpty(sendTimeMax.trim())) {
			String temp = T9DBUtility.getDateFilter("SEND_TIME", sendTimeMax.trim(), "<="); // to_char(SEND_TIME,
			where_str += " and " + temp;
		}
		String query = "SELECT * from FILE_CONTENT where 1=1 " + where_str;
		query += " order by CONTENT_NO,SEND_TIME desc ";

		try {
			stmt = dbConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			rs = stmt.executeQuery(query);
			rs.last();
			int maxRow = rs.getRow();
			rs.beforeFirst();
			int count = 0;
			while (rs.next()) {
				String dbAttachmentIds = T9Utility.null2Empty(rs.getString("ATTACHMENT_ID"));
				String dbAttachmentNames = T9Utility.null2Empty(T9Utility.encodeSpecial(rs.getString("ATTACHMENT_NAME")));

				// 匹配文件内容信息
				if (!"".equals(attachmentData.trim()) && "".equals(dbAttachmentNames.trim())) {
					continue;
				}
				if (!T9Utility.isNullorEmpty(attachmentData) && !T9Utility.isNullorEmpty(dbAttachmentNames)) {
					String[] attIdArray = dbAttachmentIds.trim().split(",");
					String[] attNameArray = dbAttachmentNames.trim().split("\\*");
					int contentValue = -1;
					for (int i = 0; i < attIdArray.length; i++) {
						String attId = this.getAttaId(attIdArray[i]);
						String attFolder = this.getFilePathFolder(attIdArray[i]);
						String newAttName = attId + "_" + attNameArray[i];
						String oldAttName = attId + "." + attNameArray[i];

						String newFilePath = filePath + "/" + attFolder + "/" + newAttName;
						String oldFilePath = filePath + "/" + attFolder + "/" + oldAttName;
						File newFile = new File(newFilePath);
						File oldFile = new File(oldFilePath);
						String fileType = "";
						String attName = attNameArray[i];
						if (attName.trim().lastIndexOf(".") != -1) {
							fileType = attName.substring(attName.trim().lastIndexOf(".")); // .doc
						}
						StringBuffer buffer = new StringBuffer();
						if (newFile.exists()) {
							long totalSpace = newFile.length();
							if (".htm".equals(fileType.trim()) || ".html".equals(fileType.trim())) {
								if (totalSpace < 500 * 1024 * 1024) {
									buffer = T9FileUtility.loadLine2Buff(newFile.getAbsolutePath(), "GBK");
									contentValue = buffer.indexOf(attachmentData.trim());
									if (contentValue == -1) {
										buffer = T9FileUtility.loadLine2Buff(newFile.getAbsolutePath(), "UTF-8");
										contentValue = buffer.indexOf(attachmentData.trim());
									}
								} else {
									buffer = T9FileUtility.loadLine2Buff(newFile.getAbsolutePath(), 0, 500, "GBK");
									contentValue = buffer.indexOf(attachmentData.trim());
									if (contentValue == -1) {
										buffer = T9FileUtility.loadLine2Buff(newFile.getAbsolutePath(), 0, 500, "UTF-8");
										contentValue = buffer.indexOf(attachmentData.trim());
									}
								}
							} else if (".txt".equals(fileType.trim())) {
								if (totalSpace < 500 * 1024 * 1024) {
									buffer = T9FileUtility.loadLine2Buff(newFile.getAbsolutePath(), "GBK");
									contentValue = buffer.indexOf(attachmentData.trim());
									if (contentValue == -1) {
										buffer = T9FileUtility.loadLine2Buff(newFile.getAbsolutePath(), "UTF-8");
										contentValue = buffer.indexOf(attachmentData.trim());
									}
								} else {
									buffer = T9FileUtility.loadLine2Buff(newFile.getAbsolutePath(), 0, 500, "GBK");
									contentValue = buffer.indexOf(attachmentData.trim());
									if (contentValue == -1) {
										buffer = T9FileUtility.loadLine2Buff(newFile.getAbsolutePath(), 0, 500, "UTF-8");
										contentValue = buffer.indexOf(attachmentData.trim());
									}
								}
							}
							if (contentValue >= 0) {
								break;
							}
						} else if (oldFile.exists()) {
							long totalSpace = oldFile.length();
							if (".htm".equals(fileType.trim()) || ".html".equals(fileType.trim())) {
								if (totalSpace < 500 * 1024 * 1024) {
									buffer = T9FileUtility.loadLine2Buff(oldFile.getAbsolutePath(), "GBK");
									contentValue = buffer.indexOf(attachmentData.trim());
									if (contentValue == -1) {
										buffer = T9FileUtility.loadLine2Buff(oldFile.getAbsolutePath(), "UTF-8");
										contentValue = buffer.indexOf(attachmentData.trim());
									}
								} else {
									buffer = T9FileUtility.loadLine2Buff(oldFile.getAbsolutePath(), 0, 500, "GBK");
									contentValue = buffer.indexOf(attachmentData.trim());
									if (contentValue == -1) {
										buffer = T9FileUtility.loadLine2Buff(oldFile.getAbsolutePath(), 0, 500, "UTF-8");
										contentValue = buffer.indexOf(attachmentData.trim());
									}
								}

							} else if (".txt".equals(fileType.trim())) {
								if (totalSpace < 500 * 1024 * 1024) {
									buffer = T9FileUtility.loadLine2Buff(oldFile.getAbsolutePath(), "GBK");
									contentValue = buffer.indexOf(attachmentData.trim());
									if (contentValue == -1) {
										buffer = T9FileUtility.loadLine2Buff(oldFile.getAbsolutePath(), "UTF-8");
										contentValue = buffer.indexOf(attachmentData.trim());
									}
								} else {
									buffer = T9FileUtility.loadLine2Buff(oldFile.getAbsolutePath(), 0, 500, "GBK");
									contentValue = buffer.indexOf(attachmentData.trim());
									if (contentValue == -1) {
										buffer = T9FileUtility.loadLine2Buff(oldFile.getAbsolutePath(), 0, 500, "UTF-8");
										contentValue = buffer.indexOf(attachmentData.trim());
									}
								}
							}
							if (contentValue >= 0) {
								break;
							}
						} else {
							break;
						}
					}
					if (contentValue == -1) {
						continue;
					}
				}
				int managePriv = 0;
				int downPriv = 0;
				int newPriv = 0;
				int fileSortCur = 1; // 默认为公共文件柜的标识				int dbSortId = rs.getInt("SORT_ID");
				String dbUserId = T9Utility.null2Empty(rs.getString("USER_ID"));

				int loginUserSeqId = loginUser.getSeqId();
				String loginUserSeqIdStr = String.valueOf(loginUserSeqId);

				if (dbSortId != 0) {
					T9FileSort fileSort = fileSortLogic.getFolderInfoById(dbConn, dbSortId);
					if (fileSort != null) {
						String sortUserIds = fileSort.getUserId() == null ? "" : fileSort.getUserId();
						boolean userIdPriv = fileSortLogic.getUserIdAccessPriv(dbConn, loginUser, fileSort);
						if (!loginUserSeqIdStr.equals(sortUserIds.trim()) && !userIdPriv) {
							continue;
						}
						boolean manageUserPriv = fileSortLogic.getManageAccessPriv(dbConn, loginUser, fileSort);
						boolean downUserPriv = fileSortLogic.getDownAccessPriv(dbConn, loginUser, fileSort);
						boolean newUserPriv = fileSortLogic.getNewUserAccessPriv(dbConn, loginUser, fileSort);
						if (loginUserSeqIdStr.equals(sortUserIds.trim()) || manageUserPriv) {
							managePriv = 1;
						}
						if (loginUserSeqIdStr.equals(sortUserIds.trim()) || downUserPriv) {
							downPriv = 1;
						}
						if (loginUserSeqIdStr.equals(sortUserIds.trim()) || newUserPriv) {
							newPriv = 1;
						}
						if (loginUserSeqIdStr.equals(sortUserIds.trim())) {
							fileSortCur = 2; // 个人文件柜的标识
						} else {
							fileSortCur = 1; // 公共文件柜的标识
						}

					}
				} else {
					if (!loginUserSeqIdStr.equals(dbUserId.trim())) {
						continue;
					}
					managePriv = 1;
					downPriv = 1;
					newPriv = 1;
					fileSortCur = 2;
				}

				StringBuffer buffer1 = new StringBuffer();
				fileSortLogic.getSortNamePath(dbConn, dbSortId, buffer1);
				String sortName = buffer1.toString();
				String sortNames[] = sortName.split(",");
				StringBuffer sb = new StringBuffer();
				for (int i = sortNames.length - 1; i >= 0; i--) {
					sb.append(sortNames[i]);
				}
				sb.deleteCharAt(sb.length() - 1);

				String treePath = "/" + sb.toString();

				if ("".equals(treePath) && dbSortId == 0) {
					treePath = "/";
				}

				Map<Object, Object> map2 = new HashMap<Object, Object>();
				map2.put("dbContentId", rs.getInt("SEQ_ID"));
				map2.put("dbSortId", rs.getInt("SORT_ID"));
				map2.put("dbSubject", rs.getString("SUBJECT"));
				map2.put("dbSendTime", T9Utility.getDateTimeStr(rs.getTimestamp("SEND_TIME")));

				map2.put("dbAttachmentId", rs.getString("ATTACHMENT_ID"));
				map2.put("dbAttachmentName", rs.getString("ATTACHMENT_NAME"));
				map2.put("dbAttachmentDesc", rs.getString("ATTACHMENT_DESC"));
				// map2.put("dbUserId", rs.getString("USER_ID"));

				map2.put("treePath", treePath);
				map2.put("managePriv", managePriv);
				map2.put("downPriv", downPriv);
				map2.put("newPriv", newPriv);
				map2.put("fileSortCur", fileSortCur);
				list.add(map2);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(stmt, rs, log);
		}
		return list;
	}

	/**
	 * 根据seqId串删除文件
	 * 
	 * 
	 * @param dbConn
	 * @param seqIdStrs
	 * @param filePath
	 * @throws NumberFormatException
	 * @throws Exception
	 */
	public void delFile(Connection dbConn, String seqIdStrs, String filePath, int loginUserSeqId, String ipStr, String recycle, String recyclePath)
			throws NumberFormatException, Exception {
		T9ORM orm = new T9ORM();

		String[] seqIdStr = seqIdStrs.split(",");
		if (!"".equals(seqIdStrs) && seqIdStrs.split(",").length > 0) {
			// 遍历要选择删除的附件id串
			for (String seqId : seqIdStr) {
				T9FileContent fileContent = this.getFileContentInfoById(dbConn, Integer.parseInt(seqId));
				String attachmentId = T9Utility.null2Empty(fileContent.getAttachmentId());
				String attachmentName = T9Utility.null2Empty(fileContent.getAttachmentName());
				String[] attIdArray = {};
				String[] attNameArray = {};
				if (!"".equals(attachmentId) && attachmentId != null && attachmentName != null) {
					attIdArray = attachmentId.trim().split(",");
					attNameArray = attachmentName.trim().split("\\*");
				}
				for (int i = 0; i < attIdArray.length; i++) {
					Map<String, String> map = this.getFileName(attIdArray[i], attNameArray[i]);
					if (map.size() != 0) {
						Set<String> set = map.keySet();
						// 遍历Set集合
						for (String keySet : set) {
							String key = keySet;
							String keyValue = map.get(keySet);
							String attaIdStr = this.getAttaId(keySet);
							String fileNameValue = attaIdStr + "_" + keyValue;
							String fileFolder = this.getFilePathFolder(key);
							String oldFileNameValue = attaIdStr + "." + keyValue;
							File file = new File(filePath + File.separator + fileFolder + File.separator + fileNameValue);
							File oldFile = new File(filePath + File.separator + fileFolder + File.separator + oldFileNameValue);

							if (file.exists()) {
								if ("1".equals(recycle.trim())) {
									T9FileUtility.xcopyFile(file.getAbsolutePath(), recyclePath + File.separator + fileNameValue);
								} else {
									T9FileUtility.deleteAll(file.getAbsoluteFile());
								}
							} else if (oldFile.exists()) {
								if ("1".equals(recycle.trim())) {
									T9FileUtility.xcopyFile(oldFile.getAbsolutePath(), recyclePath + File.separator + fileNameValue);
								} else {
									T9FileUtility.deleteAll(oldFile.getAbsoluteFile());
								}
							}
						}
					}
				}
				// 删除数据库信息				T9FileContent delContent = new T9FileContent();
				delContent.setSeqId(fileContent.getSeqId());
				orm.deleteSingle(dbConn, delContent);

				// 写入系统日志
				String remark = "删除文件,名称:" + fileContent.getSubject();
				T9SysLogLogic.addSysLog(dbConn, T9LogConst.FILE_FOLDER, remark, loginUserSeqId, ipStr);

			}
		}
	}

	/**
	 * 浮动菜单文件删除
	 * 
	 * @param dbConn
	 * @param attId
	 * @param attName
	 * @param contentId
	 * @throws Exception
	 */
	public boolean delFloatFile(Connection dbConn, String attId, String attName, int contentId) throws Exception {
		boolean updateFlag = false;
		T9FileContent fileContent = this.getFileContentInfoById(dbConn, contentId);
		String[] attIdArray = {};
		String[] attNameArray = {};
		String attachmentId = T9Utility.null2Empty(fileContent.getAttachmentId());
		String attachmentName = T9Utility.null2Empty(fileContent.getAttachmentName());
		if (!"".equals(attachmentId.trim()) && attachmentId != null && attachmentName != null) {
			attIdArray = attachmentId.trim().split(",");
			attNameArray = attachmentName.trim().split("\\*");
		}
		String attaId = "";
		String attaName = "";
		for (int i = 0; i < attIdArray.length; i++) {
			if (attId.equals(attIdArray[i])) {
				continue;
			}
			attaId += attIdArray[i] + ",";
			attaName += attNameArray[i] + "*";
		}

		fileContent.setAttachmentId(attaId.trim());
		fileContent.setAttachmentName(attaName.trim());
		this.updataFileInfoByObj(dbConn, fileContent);
		updateFlag = true;
		return updateFlag;

	}

	/**
	 * 复制文件操作
	 * 
	 * @param dbConn
	 * @param seqIdStrs
	 * @throws Exception
	 * @throws NumberFormatException
	 */
	public void copyFile(Connection dbConn, String seqIdStrs, String sortId, String filePath) throws NumberFormatException, Exception {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyMM");
		String currDate = format.format(date);
		T9InnerEMailUtilLogic emut = new T9InnerEMailUtilLogic();

		T9ORM orm = new T9ORM();
	
		String[] seqIdStr = seqIdStrs.split(",");
		if (!"".equals(seqIdStrs) && seqIdStrs.split(",").length > 0) {
			// 遍历要选择附件id串			for (String seqId : seqIdStr) {
			  String randFlag = "";
		    String newAttName = "";
				boolean isHave = false;
				T9FileContent fileContent = this.getFileContentInfoById(dbConn, Integer.parseInt(seqId));
				if (fileContent != null) {
					String subject = T9Utility.null2Empty(fileContent.getSubject());
					boolean haveFile = this.isExistFile(dbConn, Integer.parseInt(sortId), subject);
					if (haveFile) {
						StringBuffer buffer = new StringBuffer();
						this.copyExistFile(dbConn, buffer, Integer.parseInt(sortId), subject);
						String newSubject = buffer.toString().trim();
						fileContent.setSubject(newSubject);
					}

					String attachmentId = T9Utility.null2Empty(fileContent.getAttachmentId());
					String attachmentName = T9Utility.null2Empty(fileContent.getAttachmentName());
					String[] attIdArray = {};
					String[] attNameArray = {};
					if (attachmentId != null && attachmentName != null) {
						attIdArray = attachmentId.split(",");
						attNameArray = attachmentName.split("\\*");
					}
					for (int i = 0; i < attIdArray.length; i++) {
						Map<String, String> map = this.getFileName(attIdArray[i], attNameArray[i]);
						// 遍历Set集合
						if (map.size() != 0) {
							Set<String> set = map.keySet();
							for (String keySet : set) {
								String rand = emut.getRandom();
								String key = keySet;
								String keyValue = map.get(keySet);
								String attaIdStr = this.getAttaId(keySet);
								String newAttaName = rand + "_" + keyValue;
								String fileNameValue = attaIdStr + "_" + keyValue;
								String fileFolder = this.getFilePathFolder(key);

								File file = new File(filePath + File.separator + fileFolder + File.separator + fileNameValue);
								if (file != null && file.exists()) {
									T9FileUtility.copyFile(filePath + File.separator + fileFolder + File.separator + fileNameValue, filePath + File.separator
											+ currDate + File.separator + newAttaName);
									randFlag += currDate + "_" + rand + ",";
									newAttName += keyValue + "*";
									isHave = true;
									break;
								}
							}
						}
					}
					if (isHave) {
						// 保存到数据库
						// fileContent.setUserId(String.valueOf(loginUserSeqId));
						// fileContent.setCreater(String.valueOf(loginUserSeqId));
						fileContent.setSortId(Integer.parseInt(sortId));
						fileContent.setSendTime(T9Utility.parseTimeStamp());
						fileContent.setAttachmentId(randFlag);
						fileContent.setAttachmentName(newAttName.trim());
						orm.saveSingle(dbConn, fileContent);
					} else {
						fileContent.setSortId(Integer.parseInt(sortId));
						fileContent.setSendTime(T9Utility.parseTimeStamp());
						orm.saveSingle(dbConn, fileContent);
					}

				}
			}
		}
	}

	/**
	 * 得到附件的Id
	 * 
	 * @param keyId
	 * @return
	 */
	public String getAttaId(String keyId) {
		String attaId = "";
		if (keyId != null && !"".equals(keyId)) {
			if (keyId.indexOf('_') != -1) {
				String[] ids = keyId.split("_");
				if (ids.length > 0) {
					attaId = ids[1];
				}

			} else {
				attaId = keyId;
			}
		}
		return attaId;
	}

	/**
	 * 得到附件的Id 兼老数据
	 * 
	 * 
	 * @param keyId
	 * @return
	 */
	public String getOldAttaId(String keyId) {
		String attaId = "";
		if (keyId != null && !"".equals(keyId)) {
			if (keyId.indexOf('_') != -1) {
				String[] ids = keyId.split("_");
				if (ids.length > 0) {
					attaId = ids[1];
				}
			} else {
				attaId = keyId;
			}
		}
		return attaId;
	}

	/**
	 * 根据人员id字符串得到name字符串
	 * 
	 * 
	 * @param dbConn
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public String getPersonNamesByIds(Connection conn, String ids) throws Exception {
		String names = "";
		if (ids != null && !"".equals(ids.trim())) {
			if (ids.endsWith(",")) {
				ids = ids.substring(0, ids.length() - 1);
			}
			String query = "select USER_NAME from PERSON where SEQ_ID in (" + ids + ")";
			Statement stm = null;
			ResultSet rs = null;
			try {
				stm = conn.createStatement();
				rs = stm.executeQuery(query);
				while (rs.next()) {
					names += rs.getString("USER_NAME") + ",";
				}
			} catch (Exception ex) {
				throw ex;
			} finally {
				T9DBUtility.close(stm, rs, null);
			}
		}
		if (names.endsWith(",")) {
			names = names.substring(0, names.length() - 1);
		}
		return names;
	}

	/**
	 * 得到该文件的文件夹名
	 * 
	 * @param key
	 * @return
	 */
	public String getFilePathFolder(String key) {
		String folder = "";

		if (key != null && !"".equals(key)) {

			if (key.indexOf('_') != -1) {
				String[] str = key.split("_");
				for (int i = 0; i < str.length; i++) {
					folder = str[0];
				}
			} else {
				folder = "all";
			}

		}
		return folder;
	}

	/**
	 * 拼接附件Id与附件名
	 * 
	 * @param attachmentId
	 * @param attachmentName
	 * @return
	 */
	public Map<String, String> getFileName(String attachmentId, String attachmentName) {
		Map<String, String> map = new HashMap<String, String>();
		if (attachmentId == null || attachmentName == null) {
			return map;
		}
		if (!"".equals(attachmentId.trim()) && !"".equals(attachmentName.trim())) {

			String attachmentIds[] = attachmentId.split(",");
			String attachmentNames[] = attachmentName.split("\\*");
			if (attachmentIds.length != 0 && attachmentNames.length != 0) {
				for (int i = 0; i < attachmentIds.length; i++) {
					map.put(attachmentIds[i], attachmentNames[i]);
				}

			}
		}
		return map;
	}

	/**
	 * 拼接附件Id与附件名
	 * 
	 * @param attachmentId
	 * @param attachmentName
	 * @return
	 */
	public String getAttachName(String attachmentId, String attachmentName) {
		String fileName = "";
		if (attachmentId == null || attachmentName == null) {
			return fileName;
		}
		if (!"".equals(attachmentId.trim()) && !"".equals(attachmentName.trim())) {

			String attachmentIds[] = attachmentId.split(",");
			String attachmentNames[] = attachmentName.split("\\*");
			if (attachmentIds.length != 0 && attachmentNames.length != 0) {

				for (int i = 0; i < attachmentIds.length; i++) {
					fileName = attachmentIds[i] + attachmentNames[i];
				}

			}
		}
		return fileName;
	}

	/**
	 * 得到该文件的文件夹名 兼老数据
	 * 
	 * 
	 * @param key
	 * @return
	 */
	public String getAttFolderName(String key) {
		String folder = "";
		if (key != null && !"".equals(key)) {

			if (key.indexOf('_') != -1) {
				String[] str = key.split("_");
				for (int i = 0; i < str.length; i++) {
					folder = str[0];
				}
			} else {
				folder = "all";
			}
		}
		return folder;
	}

	/**
	 * 剪切文件操作
	 * 
	 * @param dbConn
	 * @param seqIdStrs
	 * @param sortId
	 * @param filePath
	 * @throws NumberFormatException
	 * @throws Exception
	 */
	public void cutFile(Connection dbConn, String seqIdStrs, String sortId, String filePath) throws NumberFormatException, Exception {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyMM");
		String currDate = format.format(date);
		T9InnerEMailUtilLogic emut = new T9InnerEMailUtilLogic();

		
		boolean isHave = false;

		T9ORM orm = new T9ORM();
		String[] seqIdStr = seqIdStrs.split(",");
		if (!T9Utility.isNullorEmpty(seqIdStrs) && seqIdStrs.split(",").length > 0) {
			// 遍历选择的附件Id串			for (String seqId : seqIdStr) {
			  String randFlag = "";
		    String newAttName = "";
				T9FileContent fileContent = this.getFileContentInfoById(dbConn, Integer.parseInt(seqId));
				if (fileContent != null) {
					String subject = T9Utility.null2Empty(fileContent.getSubject());
					boolean haveFile = this.isExistFile(dbConn, Integer.parseInt(sortId), subject);
					if (haveFile) {
						StringBuffer buffer = new StringBuffer();
						this.copyExistFile(dbConn, buffer, Integer.parseInt(sortId), subject);
						String newSubject = buffer.toString().trim();
						fileContent.setSubject(newSubject);
					}
					String attachmentId = T9Utility.null2Empty(fileContent.getAttachmentId());
					String attachmentName = T9Utility.null2Empty(fileContent.getAttachmentName());
					T9FileContent delContent = new T9FileContent();
					String[] attIdArray = {};
					String[] attNameArray = {};
					if (!T9Utility.isNullorEmpty(attachmentId) && !T9Utility.isNullorEmpty(attachmentName)) {
						attIdArray = attachmentId.split(",");
						attNameArray = attachmentName.split("\\*");
					}
					for (int i = 0; i < attIdArray.length; i++) {
						Map<String, String> map = this.getFileName(attIdArray[i], attNameArray[i]);
						if (map.size() != 0) {
							Set<String> set = map.keySet();
							// 遍历Set集合
							for (String keySet : set) {
								String rand = emut.getRandom();
								String key = keySet;
								String keyValue = map.get(keySet);
								String attaIdStr = this.getAttaId(keySet);
								String fileNameValue = attaIdStr + "_" + keyValue;
								String newAttaName = rand + "_" + keyValue;
								String fileFolder = this.getFilePathFolder(key);

								File file = new File(filePath + File.separator + fileFolder + File.separator + fileNameValue);
								if (file != null && file.exists()) {
									T9FileUtility.xcopyFile(filePath + File.separator + fileFolder + File.separator + fileNameValue, filePath + File.separator
											+ currDate + File.separator + newAttaName);
									randFlag += currDate + "_" + rand + ",";
									newAttName += keyValue + "*";
									isHave = true;
									break;
								}
							}
						}
					}
					if (isHave) {
						delContent.setSeqId(fileContent.getSeqId());
						// 删除旧信息
						orm.deleteSingle(dbConn, delContent);
						// 插入新信息
						fileContent.setSortId(Integer.parseInt(sortId));
						fileContent.setSendTime(T9Utility.parseTimeStamp());
						fileContent.setAttachmentId(randFlag.trim());
						fileContent.setAttachmentName(newAttName.trim());
						orm.saveSingle(dbConn, fileContent);
					} else {
						delContent.setSeqId(fileContent.getSeqId());
						orm.deleteSingle(dbConn, delContent);
						fileContent.setSortId(Integer.parseInt(sortId));
						fileContent.setSendTime(T9Utility.parseTimeStamp());
						orm.saveSingle(dbConn, fileContent);
					}

				}

			}
		}
	}

	/**
	 * 转存文件到公共文件柜
	 * 
	 * @param dbConn
	 * @param seqId
	 * @param attachId
	 * @param attachName
	 * @throws Exception
	 */
	public boolean transferFolder(Connection dbConn, int seqId, String attachId, String attachName, String subject, String filePath, String folderPath)
			throws Exception {
		boolean flag = false;
		T9ORM orm = new T9ORM();
		String fileFolder = filePath + File.separator + this.getAttFolderName(attachId);
		String fileName = this.getOldAttaId(attachId) + "_" + attachName;
		String oldFileName = this.getOldAttaId(attachId) + "." + attachName;

		T9FileContent fileContent = new T9FileContent(); // this.getFileContentInfoById(dbConn,
		// seqId);
//		T9FileContent fileContent1 = this.getFileContentInfoById(dbConn, seqId);
		try {
			Date date = new Date();
			SimpleDateFormat format = new SimpleDateFormat("yyMM");
			String currDate = format.format(date);
			T9InnerEMailUtilLogic emut = new T9InnerEMailUtilLogic();

			String rand = emut.getRandom();
			String newAttaName = rand + "_" + attachName;

			File file = new File(fileFolder + "/" + fileName);
			File oldFile = new File(fileFolder + "/" + oldFileName);
			String subjectStr = "文件转存";
			String newSubject = "";
			if (!T9Utility.isNullorEmpty(subject)) {
				subjectStr = subject;
			}
			boolean haveFile = this.isExistFile(dbConn, seqId, subjectStr);
			if (haveFile) {
				StringBuffer buffer = new StringBuffer();
				this.copyExistFile(dbConn, buffer, seqId, subjectStr);
				newSubject = buffer.toString().trim();
			}
			
			if (file.exists()) {
				T9FileUtility.copyFile(file.getAbsolutePath(), folderPath + File.separator + currDate + File.separator + newAttaName);
				fileContent.setSortId(seqId);
				fileContent.setSubject(newSubject);
				fileContent.setSendTime(T9Utility.parseTimeStamp());
				fileContent.setAttachmentId(currDate + "_" + rand + ",");
				fileContent.setAttachmentName(attachName + "*");
				orm.saveSingle(dbConn, fileContent);
				flag = true;

			} else if (oldFile.exists()) {
				T9FileUtility.copyFile(oldFile.getAbsolutePath(), folderPath + File.separator + currDate + File.separator + newAttaName);
				fileContent.setSortId(seqId);
				fileContent.setSubject(newSubject);
				fileContent.setSendTime(T9Utility.parseTimeStamp());
				fileContent.setAttachmentId(currDate + "_" + rand + ",");
				fileContent.setAttachmentName(attachName + "*");
				orm.saveSingle(dbConn, fileContent);
				flag = true;
			}
			return flag;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 获取最大的SeqId值
	 * 
	 * 
	 * @param dbConn
	 * @return
	 */
	public T9FileContent getMaxSeqId(Connection dbConn) {
		// String sql="select MAX(SEQ_ID) from file_sort";
		String sql = "select SEQ_ID from FILE_CONTENT where SEQ_ID=(select MAX(SEQ_ID) from FILE_CONTENT ) ";
		T9FileContent content = null;
		int seqId = 0;
		// String sortName = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = dbConn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				content = new T9FileContent();
				seqId = rs.getInt("SEQ_ID");
				content.setSeqId(seqId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			T9DBUtility.close(ps, rs, log);
		}
		return content;
	}

	/**
	 * 判断是否已签阅
	 * 
	 * 
	 * @param userSeqId
	 * @param ids
	 * @param dbConn
	 * @return
	 * @throws Exception
	 * @throws Exception
	 */
	public boolean isHaveReaderId(int userSeqId, String ids) throws Exception, Exception {
		boolean flag = false;
		if (ids != null && !"".equals(ids.trim())) {
			String[] aId = ids.split(",");
			for (String tmp : aId) {
				if (!"".equals(tmp.trim())) {
					if (Integer.parseInt(tmp) == userSeqId) {
						flag = true;
					}
				}
			}
		}
		return flag;
	}

	public void updateReader(Connection dbConn, String contentId, int loginUserSeqId) throws NumberFormatException, Exception {
		String[] seqIdStr = contentId.split(",");
		if (!"".equals(contentId) && contentId.split(",").length > 0) {
			for (String seqId : seqIdStr) {
				T9FileContent content = this.getFileContentInfoById(dbConn, Integer.parseInt(seqId));
				String readStr = T9Utility.null2Empty(content.getReaders());
				if (readStr != null && !"".equals(readStr.trim()) && readStr.lastIndexOf(',') == -1) {
					readStr += ",";
				}
				boolean isRead = this.isHaveReaderId(loginUserSeqId, readStr);
				if (!isRead) {
					String readers = readStr.trim() + String.valueOf(loginUserSeqId) + ",";
					content.setReaders(readers.trim());
					this.updateSingleObj(dbConn, content);
				}
			}
		}
	}

	/**
	 * 判断文件名是否已存在
	 * 
	 * @param dbConn
	 * @param subjectName
	 * @return
	 * @throws Exception
	 */
	public String checkSubjectName(Connection dbConn, int seqId, int subContentId, String subjectName) throws Exception {
		String data = "";
		boolean isHave = false;
		int isHaveFlag = 0;

		Map map = new HashMap();
		map.put("SORT_ID", seqId);
		try {
			List<T9FileContent> contentList = this.getFileContentsInfo(dbConn, map);
			if (subContentId != 0) {
				if (contentList != null && contentList.size() > 0) {
					for (T9FileContent content : contentList) {
						String subject = T9Utility.null2Empty(content.getSubject());
						int contentId = content.getSeqId();
						if (subContentId != contentId) {
							if (subjectName.trim().equals(subject.trim())) {
								isHave = true;
								break;
							}
						}
					}
				}
			} else {
				if (contentList != null && contentList.size() > 0) {
					for (T9FileContent content : contentList) {
						String subject = T9Utility.null2Empty(content.getSubject());
						if (subjectName.trim().equals(subject.trim())) {
							isHave = true;
							break;
						}
					}
				}
			}

			if (isHave) {
				isHaveFlag = 1;
			}
			data = "{isHaveFlag:\"" + isHaveFlag + "\" }";

		} catch (Exception e) {
			throw e;
		}
		return data;
	}

	/**
	 * 判断文件名是否已存在
	 * 
	 * @param dbConn
	 * @param subjectName
	 * @return
	 * @throws Exception
	 */
	public String checkEditSubjectName(Connection dbConn, int seqId, int subContentId, String subjectName) throws Exception {
		String data = "";
		boolean isHave = false;
		int isHaveFlag = 0;

		Map map = new HashMap();
		map.put("SORT_ID", seqId);
		try {
			List<T9FileContent> contentList = this.getFileContentsInfo(dbConn, map);
			if (contentList != null && contentList.size() > 0) {
				for (T9FileContent content : contentList) {
					String subject = T9Utility.null2Empty(content.getSubject());
					int contentId = content.getSeqId();
					if (subContentId != contentId) {
						if (subjectName.trim().equals(subject.trim())) {
							isHave = true;
							break;
						}
					}
				}
			}
			if (isHave) {
				isHaveFlag = 1;
			}
			data = "{isHaveFlag:\"" + isHaveFlag + "\" }";

		} catch (Exception e) {
			throw e;
		}
		return data;
	}

	/**
	 * 判断附件是否存在本地硬盘上
	 * 
	 * 
	 * @param attIdStr
	 * @param attNameStr
	 * @return
	 * @throws Exception
	 */
	public String isHaveFileInDisk(String attIdStr, String attNameStr) throws Exception {

		int isHaveFile = 0;
		String filePath = "";
		String oldFilePath = "";
		try {
			if (!"".equals(attIdStr.trim()) && !"".equals(attNameStr.trim())) {
				String currDate = this.getFilePathFolder(attIdStr);
				String fileId = this.getAttaId(attIdStr);
				String fileName = fileId + "_" + attNameStr;
				String oldFileName = fileId + "." + attNameStr;

				filePath = T9SysProps.getAttachPath() + File.separator + "file_folder" + File.separator + currDate + File.separator + fileName; // T9SysProps.getAttachPath()得到
				oldFilePath = T9SysProps.getAttachPath() + File.separator + "file_folder" + File.separator + currDate + File.separator + oldFileName; // T9SysProps.getAttachPath()得到
			}
			File file = new File(filePath);
			File oldFile = new File(oldFilePath);
			if (file.exists()) {
				isHaveFile = 1;
			} else if (oldFile.exists()) {
				isHaveFile = 1;
			}
			String data = "{isHaveFile:\"" + isHaveFile + "\"}";
			return data;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 查询最新文件显示于桌面
	 * 
	 * @param dbConn
	 * @return
	 * @throws Exception
	 */
	public List<Map<Object, Object>> selectNewContent(Connection dbConn) throws Exception {
		List<Map<Object, Object>> list = new ArrayList<Map<Object, Object>>();
		Statement stmt = null;
		ResultSet rs = null;
		// String sql =
		// "select a.SEQ_ID,a.SUBJECT,a.SEND_TIME,a.READERS,b.SEQ_ID as SORT_ID,b.SORT_NAME,b.USER_ID,b.OWNER  from FILE_CONTENT a,FILE_SORT b where a.SORT_ID=b.SEQ_ID and b.SORT_TYPE!='4' and rownum between (1) and (150) order by a.SEND_TIME desc";
		// String sql =
		// "select * from ( select aa.*,rownum row_num from ( select a.SEQ_ID,a.SUBJECT,a.SEND_TIME,a.READERS,b.SEQ_ID as SORT_ID,b.SORT_NAME,b.USER_ID,b.OWNER, b.sort_type  from FILE_CONTENT a,FILE_SORT b where a.SORT_ID = b.SEQ_ID and (b.SORT_TYPE != '4' or b.SORT_TYPE is  NULL) order by a.SEND_TIME desc )aa )bb  where bb.row_num between 1 and 10";

		String query = "select a.SEQ_ID," + "a.SUBJECT," + "a.SEND_TIME," + "a.READERS," + "b.SEQ_ID as SORT_ID," + "b.SORT_NAME," + "b.USER_ID,"
				+ "b.OWNER," + " b.sort_type " + " from FILE_CONTENT a,FILE_SORT b"
				+ " where a.SORT_ID = b.SEQ_ID and (b.SORT_TYPE != '4' or b.SORT_TYPE is  NULL) order by a.SEND_TIME desc ";

		try {
			stmt = dbConn.createStatement();
			rs = stmt.executeQuery(query);
			int count = 0;
			while (rs.next() && count++ < 10) {
				Map<Object, Object> map = new HashMap<Object, Object>();
				T9FileContent content = new T9FileContent();
				content.setSeqId(rs.getInt("SEQ_ID"));
				content.setSubject(rs.getString("SUBJECT"));
				content.setSendTime(rs.getTimestamp("SEND_TIME"));

				map.put("contentId", rs.getInt("SEQ_ID"));
				map.put("subject", rs.getString("SUBJECT"));
				map.put("sendTime", rs.getObject("SEND_TIME"));
				map.put("sortId", rs.getInt("SORT_ID"));
				map.put("sortName", rs.getString("SORT_NAME"));
				map.put("userId", rs.getString("USER_ID"));
				map.put("owner", rs.getString("OWNER"));
				map.put("readers", rs.getString("READERS"));
				list.add(map);
			}

		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(stmt, rs, log);
		}
		return list;
	}

	/**
	 * 取得userId与owner的访问权限
	 * 
	 * 
	 * @param dbConn
	 * @param sortId
	 * @return
	 * @throws Exception
	 */
	public boolean getVisiPriv(Connection dbConn, T9FileSort fileSort, T9Person user) throws Exception {
		boolean flag = false;

		int loginUserSeqId = user.getSeqId();
		int loginUserDeptId = user.getDeptId();
		String loginUserRoleId = user.getUserPriv();

		int visiPrivFlag = 0;
		int ownerPrivFlag = 0;

		T9FileSortLogic logic = new T9FileSortLogic();
		String[] actions = new String[] { "USER_ID", "OWNER" };

		try {
			for (int i = 0; i < actions.length; i++) {
				if ("USER_ID".equals(actions[i])) {
					String userPrivs = logic.selectManagerIds(dbConn, fileSort, "USER_ID");
					String rolePrivs = logic.getRoleIds(dbConn, fileSort, "USER_ID");
					String deptPrivs = logic.getDeptIds(dbConn, fileSort, "USER_ID");
					boolean userFlag = logic.getUserIdStr(loginUserSeqId, userPrivs, dbConn);
					boolean deptFlag = logic.getDeptIdStr(loginUserDeptId, deptPrivs, dbConn);
					boolean roleFlag = logic.getRoleIdStr(loginUserRoleId, rolePrivs, dbConn);
					if (userFlag || deptFlag || roleFlag) {
						visiPrivFlag = 1;
					}
				}

				if ("OWNER".equals(actions[i])) {
					String userPrivs = logic.selectManagerIds(dbConn, fileSort, "OWNER");
					String rolePrivs = logic.getRoleIds(dbConn, fileSort, "OWNER");
					String deptPrivs = logic.getDeptIds(dbConn, fileSort, "OWNER");
					boolean userFlag = logic.getUserIdStr(loginUserSeqId, userPrivs, dbConn);
					boolean deptFlag = logic.getDeptIdStr(loginUserDeptId, deptPrivs, dbConn);
					boolean roleFlag = logic.getRoleIdStr(loginUserRoleId, rolePrivs, dbConn);
					if (userFlag || deptFlag || roleFlag) {
						ownerPrivFlag = 1;
					}
				}
			}
			if (visiPrivFlag == 1 || ownerPrivFlag == 1) {
				flag = true;
			}
		} catch (Exception e) {
			throw e;
		}
		return flag;
	}

	/**
	 * 取得下载权限
	 * 
	 * @param dbConn
	 * @param fileSort
	 * @return
	 * @throws Exception
	 */
	public boolean getDownPriv(Connection dbConn, T9FileSort fileSort, T9Person user) throws Exception {
		boolean flag = false;
		int loginUserSeqId = user.getSeqId();
		int loginUserDeptId = user.getDeptId();
		String loginUserRoleId = user.getUserPriv();
		int downPrivFlag = 0;
		int managePrivFlag = 0;
		T9FileSortLogic logic = new T9FileSortLogic();
		String[] actions = new String[] { "DOWN_USER", "MANAGE_USER" };
		try {
			for (int i = 0; i < actions.length; i++) {
				if ("DOWN_USER".equals(actions[i])) {
					String userPrivs = logic.selectManagerIds(dbConn, fileSort, "DOWN_USER");
					String rolePrivs = logic.getRoleIds(dbConn, fileSort, "DOWN_USER");
					String deptPrivs = logic.getDeptIds(dbConn, fileSort, "DOWN_USER");
					boolean userFlag = logic.getUserIdStr(loginUserSeqId, userPrivs, dbConn);
					boolean deptFlag = logic.getDeptIdStr(loginUserDeptId, deptPrivs, dbConn);
					boolean roleFlag = logic.getRoleIdStr(loginUserRoleId, rolePrivs, dbConn);
					if (userFlag || deptFlag || roleFlag) {
						downPrivFlag = 1;
					}
				}
				if ("MANAGE_USER".equals(actions[i])) {
					String userPrivs = logic.selectManagerIds(dbConn, fileSort, "MANAGE_USER");
					String rolePrivs = logic.getRoleIds(dbConn, fileSort, "MANAGE_USER");
					String deptPrivs = logic.getDeptIds(dbConn, fileSort, "MANAGE_USER");

					boolean userFlag = logic.getUserIdStr(loginUserSeqId, userPrivs, dbConn);
					boolean deptFlag = logic.getDeptIdStr(loginUserDeptId, deptPrivs, dbConn);
					boolean roleFlag = logic.getRoleIdStr(loginUserRoleId, rolePrivs, dbConn);
					if (userFlag || deptFlag || roleFlag) {
						managePrivFlag = 1;
					}
				}
			}

			if (downPrivFlag == 1 || managePrivFlag == 1) {
				flag = true;
			}

		} catch (Exception e) {
			throw e;
		}

		return flag;
	}

	/**
	 * 判断文件是否已阅读
	 * 
	 * 
	 * @param readIdStr
	 * @param loginUser
	 * @return
	 * @throws Exception
	 */
	public boolean isReaders(String readStr, T9Person loginUser) throws Exception {
		boolean returnFlag = false;
		int loginUserSeqId = loginUser.getSeqId();

		try {
			if (readStr != null && !"".equals(readStr.trim()) && readStr.lastIndexOf(',') == -1) {
				readStr += ",";
			}
			returnFlag = this.isHaveReaderId(loginUserSeqId, readStr);

		} catch (Exception e) {
			throw e;
		}

		return returnFlag;
	}

	/**
	 * 获取附件的id和name
	 * 
	 * @param dbConn
	 * @param contentStrs
	 * @return
	 * @throws Exception
	 */
	public String getDownFileInfo(Connection dbConn, String contentStrs) throws Exception {
		String data = "";
		String attIdArry = "";
		String attNamedArry = "";
		int counter = 0;
		StringBuffer buffer = new StringBuffer();
		try {
			String[] seqIdStr = contentStrs.split(",");
			if (!"".equals(contentStrs) && contentStrs.split(",").length > 0) {
				for (String seqId : seqIdStr) {
					T9FileContent fileContent = this.getFileContentInfoById(dbConn, Integer.parseInt(seqId));
					String attachmentId = fileContent.getAttachmentId();
					String attachmentName = fileContent.getAttachmentName();

					if (!"".equals(attachmentId.trim()) && !"".equals(attachmentName.trim())) {
						attIdArry += attachmentId;
						attNamedArry += attachmentName;
					}
				}
			}

			String[] counterArry = attIdArry.split(",");
			if (counterArry != null && counterArry.length != 0) {
				for (int i = 0; i < counterArry.length; i++) {
					counter++;
					if (counter >= 3) {
						break;
					}
				}
			}

			data = "{attIdArry:\"" + attIdArry + "\",attNamedArry:\"" + attNamedArry + "\",counter:" + counter + "}";

		} catch (Exception e) {
			throw e;
		}

		return data;
	}

	/**
	 * 重命名文件
	 * 
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean reNameOndisk(String oldNameStr, String attachId, String renameStr, String folderPath) throws Exception {
		boolean returnFlag = false;

		String fileFolder = this.getFilePathFolder(attachId); // 1006
		String filePath = folderPath + "/" + fileFolder;

		String attIdStr = this.getAttaId(attachId); // f40b24778d538764c902bbd547aa7802

		String oldName = attIdStr + "_" + oldNameStr; // f40b24778d538764c902bbd547aa7802_公告通知状态显示逻辑.docx
		String newName = attIdStr + "_" + renameStr; // f40b24778d538764c902bbd547aa7802_公告cc.docx

		String oldName2 = attIdStr + "." + oldNameStr;
		String newName2 = attIdStr + "." + renameStr;

		try {
			if (filePath != null && !"".equals(filePath.trim())) {

				File oldFile = new File(filePath + "/" + oldName);
				File newFile = new File(filePath + "/" + newName);

				File oldFile2 = new File(filePath + "/" + oldName2);
				File newFile2 = new File(filePath + "/" + newName2);

				if (!newFile.exists()) {
					if (oldFile.renameTo(newFile)) {
						returnFlag = true;
					}
				} else if (!newFile2.exists()) {
					if (oldFile2.renameTo(newFile2)) {
						returnFlag = true;
					}
				}

			}

		} catch (Exception e) {
			throw e;
		}

		return returnFlag;
	}

	public String updateAttachName(Connection dbConn, int contentId, String attachName, String attachId, String renameStr, String folderPath)
			throws Exception {
		T9ORM orm = new T9ORM();
		try {
			T9FileContent content = this.getFileContentInfoById(dbConn, contentId);
			String attIdDb = "";
			String attNameDb = "";
			if (content != null) {
				attIdDb = content.getAttachmentId();
				attNameDb = content.getAttachmentName();
			}

			String[] attIdArry = attIdDb.split(",");
			String[] attNameArry = attNameDb.split("\\*");

			String updateAttId = "";
			String updateAttName = "";

			if (attIdArry != null && attIdArry.length > 0) {
				for (int i = 0; i < attIdArry.length; i++) {
					String attIdTemp = attIdArry[i];
					if (attIdTemp.equals(attachId.trim())) {
						continue;
					}
					updateAttId += attIdTemp + ",";
					updateAttName += attNameArry[i] + "*";
				}
			}

			updateAttId = updateAttId + attachId + ",";
			updateAttName = updateAttName + renameStr + "*";

			content.setAttachmentId(updateAttId.trim());
			content.setAttachmentName(updateAttName.trim());
			orm.updateSingle(dbConn, content);

		} catch (Exception e) {
			throw e;
		}

		return "";

	}

	/**
	 * 
	 * @param attachmentName
	 * @param attachmentId
	 * @param module
	 * @return
	 * @throws Exception
	 */
	public HashMap toZipInfoMapFile(Connection dbConn, int sortId, String seqIds, String module, T9Person loginUser)
			throws Exception {
		HashMap result = new HashMap();

		T9ORM orm = new T9ORM();
		if (seqIds == null || "".equals(seqIds.trim())) {
			return result;
		}
		if (seqIds.trim().endsWith(",")) {
			seqIds = seqIds.trim().substring(0, seqIds.trim().length() - 1);
		}
		String[] filters = { "SEQ_ID IN(" + seqIds + ")" };
		ArrayList<T9FileContent> fileContents = (ArrayList<T9FileContent>) orm.loadListSingle(dbConn, T9FileContent.class, filters);
		HashMap<String, Integer> subjectNames = new HashMap<String, Integer>();
		T9NtkoLogic ntkoLogic = new T9NtkoLogic();
		for (int j = 0; j < fileContents.size(); j++) {
			T9FileContent fileContent = fileContents.get(j);
			String[] attachmentArray = T9Utility.isNullorEmpty(fileContent.getAttachmentName()) ? new String[0] : fileContent.getAttachmentName().split("\\*");
			String[] attachmentIdArray = T9Utility.isNullorEmpty(fileContent.getAttachmentId()) ? new String[0] : fileContent.getAttachmentId().split(",");
			String subject = fileContent.getSubject();
			if (subjectNames.keySet().contains(subject.trim())) {
				int count = subjectNames.get(subject.trim());
				subject = subject + "_" + count;
				subjectNames.put(subject.trim(), count + 1);
			} else {
				subjectNames.put(subject.trim(), 1);
			}
			HashMap<String, Integer> filesName = new HashMap<String, Integer>();

			T9FileSort fileSort = new T9FileSort();
			boolean downPriv = true;
			if (sortId != 0) {
				fileSort = (T9FileSort) orm.loadObjSingle(dbConn, T9FileSort.class, sortId);
				// 取得权限
				downPriv = this.getDownPriv(dbConn, fileSort, loginUser);
			}

			for (int i = 0; i < attachmentIdArray.length; i++) {
				if ("".equals(attachmentIdArray[i].trim()) || "".equals(attachmentArray[i].trim())) {
					continue;
				}

				String fileType = T9FileUtility.getFileExtName(attachmentArray[i].trim());
				// 判断是否为office文件
				boolean isOffice = this.isOfficeFile("." + fileType);
				if (isOffice && !downPriv) {
					continue;
				}

				String attachName = attachmentArray[i].trim();
				String temp = ntkoLogic.getAttachBytes(attachName, attachmentIdArray[i].trim(), module);
				String fileName = "";
				if (temp != null) {
					String preName = attachName.substring(0, attachName.lastIndexOf("."));
					if (filesName.keySet().contains(attachName.trim())) {
						int count = filesName.get(attachName.trim());
						String extName = attachName.substring(attachName.lastIndexOf("."), attachName.length());
						fileName = preName + "_" + count + extName;
						filesName.put(attachName.trim(), count + 1);
					} else {
						filesName.put(attachName.trim(), 1);
						fileName = attachName;
					}
					result.put(subject + "/" + "附件" + "/" + fileName, temp); // 附件内容
				}
				result.put(subject + "/" + "附件" + "/", null); // 标题为文件夹下的附件文件夹
			}

			result.put(subject + "/", null); // 以标题为文件夹名，
			String createName = this.getPersonNamesByIds(dbConn, String.valueOf(fileContent.getCreater()));

			String html = "<html><head><title>" + subject + "</title><meta http-equiv='Content-Type' content='text/html; charset=utf-8'></head>";
			html += "<style>body{font-size:12px;} table{border:1px #000 solid;border-collapse:collapse;} table td{border:1px #000 solid;}</style>";
			html += "<body><table width='70%' align='center'><tr><td align='center' colspan='2'><b><span class='big'>" + subject
					+ "&nbsp;</span></b></td></tr>";
			html += "<tr><td height='250' valign='top' colspan='2'>" + T9Utility.null2Empty(fileContent.getContent()) + "&nbsp;</td></tr>";
			html += "<tr class=small><td width='100'>创建人：</td><td width='400'>" + createName + "&nbsp;</td></tr></table></body></html>";
			/* FileInputStream htmlIn = new FileInputStream( html.getBytes()); */
			InputStream in = new ByteArrayInputStream(html.getBytes("UTF-8"));
			result.put(subject + "/" + subject + ".html", in); // 生成的hmtl页面
		}

		return result;
	}

	/**
	 * 判断是否为office文件
	 * 
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public boolean isOfficeFile(String fileType) throws Exception {
		boolean flag = false;
		try {
			if (fileType != null && !"".equals(fileType.trim())) {
				if (".doc".equals(fileType) || ".xls".equals(fileType) || ".ppt".equals(fileType) || ".pps".equals(fileType) || ".docx".equals(fileType)
						|| ".xlsx".equals(fileType) || ".pptx".equals(fileType) || ".ppsx".equals(fileType) || "wps".equals(fileType) || ".et".equals(fileType)
						|| ".ett".equals(fileType)) {
					flag = true;
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return flag;
	}

	/**
	 * 获取文件柜信息到桌面模块（须分页）
	 * 
	 * 
	 * @param dbConn
	 * @param seqId
	 * @param startCount
	 * @param endCount
	 * @return
	 * @throws Exception
	 */

	public T9PortalProducer getFileFolderInfoToDeskTop(Connection dbConn, int seqId, int startCount, int endCount) throws Exception {
		List<Object> list = new ArrayList<Object>();
		T9PortalProducer rule = new T9PortalProducer();
		T9ORM orm = new T9ORM();
		int k = 0;
		try {// //通过点击发布，传过来的seqId，取出所有文件夹
			String[] condition = { " SORT_PARENT=" + seqId + " AND (SORT_TYPE !='4' or SORT_TYPE is null) order by SORT_NO,SORT_NAME" };
			List<T9FileSort> sortList = orm.loadListSingle(dbConn, T9FileSort.class, condition);
			if (sortList != null || sortList.size() > 0) {
				for (int i = startCount; i < startCount + endCount && k <= endCount && i < sortList.size(); i++) {
					T9FileSort fileSort = sortList.get(i);
					Map<String, String> map = new HashMap<String, String>();
					String sosrtName = T9Utility.null2Empty(fileSort.getSortName());
					map.put("fileName", sosrtName);
					map.put("isDir", "isDir");
					map.put("filePath", "'" + fileSort.getSeqId() + "'");
					list.add(map); // list 保存的是文件夹
				}
			}
			String[] filters = { "SORT_ID=" + seqId };// 通过点击发布，传过来的seqId，取出所有文件
			List<T9FileContent> fileContents = orm.loadListSingle(dbConn, T9FileContent.class, filters);
			if (fileContents != null || fileContents.size() > 0) {
				for (int i = startCount; i < startCount + endCount && k <= endCount && i < fileContents.size(); i++) {
					T9FileContent fileContent = fileContents.get(i);
					String attachmentId = T9Utility.null2Empty(fileContent.getAttachmentId());
					String attachmentName = T9Utility.null2Empty(fileContent.getAttachmentName());
					String attaIdArry[] = attachmentId.split(",");
					String attaNameArry[] = attachmentName.split("\\*");
					if (!T9Utility.isNullorEmpty(attachmentId) && attaIdArry.length > 0) {
						for (int j = 0; j < attaIdArry.length; j++) {
							String fileName = attaNameArry[j];
							Map<String, String> map = new HashMap<String, String>();
							String fileType = "";
							if (fileName.lastIndexOf('.') != -1) {
								fileType = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
							}
							map.put("fileName", fileName);
							map.put("fileType", fileType);
							map.put("isDir", "isFile");
							map.put("filePath", "'" + fileContent.getSortId() + "'");
							list.add(map);// 这个list 保存的所有文件
						}
					}
				}
			}
			rule.setData(list);
			T9LinkRule lr = new T9LinkRule("fileName", "filePath");
			T9ImgRule imag = new T9ImgRule("fileName", "filePath");
			rule.addRule(lr);
			rule.addRule(imag);
			// rule.toJson();
		} catch (Exception e) {
			throw e;
		}
		// System.out.println(list.toString());
		return rule;
	}

	/**
	 * 判断库是否已有文件
	 * 
	 * @param dbConn
	 * @param sortId
	 * @param subject
	 * @return
	 * @throws Exception
	 */
	public boolean isExistFile(Connection dbConn, int sortId, String subject) throws Exception {
		boolean flag = false;
		int counter = 0;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select count(SEQ_ID) from FILE_CONTENT where SORT_ID = ? and SUBJECT=?";
		try {
			stmt = dbConn.prepareStatement(sql);
			stmt.setInt(1, sortId);
			stmt.setString(2, subject);
			rs = stmt.executeQuery();
			if (rs.next()) {
				counter = rs.getInt(1);
			}
			if (counter > 0) {
				flag = true;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(stmt, rs, log);
		}
		return flag;
	}

	/**
	 * 文件夹里文件已存在的处理方法
	 * 
	 * @param dbConn
	 * @param buffer
	 * @param sortId
	 * @param subject
	 * @throws Exception
	 */
	public void copyExistFile(Connection dbConn, StringBuffer buffer, int sortId, String subject) throws Exception {
		try {
			String temp = subject + " - 复件";
			String subjectSuffix = temp;
			int repeat = 1;
			while (this.isExistFile(dbConn, sortId, subjectSuffix)) {
			  repeat++;
			  subjectSuffix = temp + "(" + repeat + ")";
			}
			buffer.append(subjectSuffix);
		} catch (Exception e) {
			throw e;
		}
	}

}
