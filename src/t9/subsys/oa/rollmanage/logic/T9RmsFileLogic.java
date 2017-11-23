package t9.subsys.oa.rollmanage.logic;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import t9.core.data.T9DbRecord;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.person.data.T9Person;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.file.T9FileUtility;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.rollmanage.data.T9RmsFile;

public class T9RmsFileLogic {
	private static Logger log = Logger.getLogger("t9.subsys.oa.rollmanage.logic.T9RmsFileLogic.java");

	/**
	 * 获取下拉列表值
	 * @param dbConn
	 * @param parentNo
	 * @return
	 * @throws Exception
	 */
	public String getSelectOption(Connection dbConn, String classNo) throws Exception {
		String data = "";
		StringBuffer sb = new StringBuffer("[");
		classNo = T9Utility.null2Empty(classNo);
		String query = "select SEQ_ID,CLASS_CODE,CLASS_DESC from CODE_ITEM where CLASS_NO='" + classNo + "'";
		Statement stm1 = null;
		ResultSet rs1 = null;
		try {
			boolean isHave = false;
			stm1 = dbConn.createStatement();
			rs1 = stm1.executeQuery(query);
			while (rs1.next()) {
				int seqId = rs1.getInt("SEQ_ID");
				String codeNo = T9Utility.null2Empty(rs1.getString("CLASS_CODE"));
				String codeName = T9Utility.null2Empty(rs1.getString("CLASS_DESC"));
				sb.append("{");
				sb.append("seqId:\"" + seqId + "\"");
				sb.append(",value:\"" + T9Utility.encodeSpecial(codeNo) + "\"");
				sb.append(",text:\"" + T9Utility.encodeSpecial(codeName) + "\"");
				sb.append("},");
				isHave = true;
			}
			if (isHave) {
				sb.deleteCharAt(sb.length() - 1);
			}
			sb.append("]");
		} catch (Exception ex) {
			throw ex;
		} finally {
			T9DBUtility.close(stm1, rs1, null);
		}
		data = sb.toString();
		return data;
	}

	/**
	 * 添加信息
	 * 
	 * @param dbConn
	 * @param rmsFile
	 * @throws Exception
	 */
	public void addRmsFileInfo(Connection dbConn, T9RmsFile rmsFile) throws Exception {
		try {
			T9ORM orm = new T9ORM();
			orm.saveSingle(dbConn, rmsFile);
		} catch (Exception ex) {
			throw ex;
		}
	}

	/**
	 * 根据id取出信息
	 * 
	 * @param dbConn
	 * @param seqId
	 * @return
	 * @throws Exception
	 */
	public T9RmsFile getRmsFileDetailById(Connection dbConn, int seqId) throws Exception {
		try {
			T9ORM orm = new T9ORM();
			return (T9RmsFile) orm.loadObjSingle(dbConn, T9RmsFile.class, seqId);
		} catch (Exception ex) {
			throw ex;
		}
	}

	/**
	 * 根据对象更新rmsFile表信息
	 * 
	 * @param dbConn
	 * @param rmsFile
	 * @throws Exception
	 */
	public void updateRmsFileByObj(Connection dbConn, T9RmsFile rmsFile) throws Exception {
		try {
			T9ORM orm = new T9ORM();
			orm.updateSingle(dbConn, rmsFile);
		} catch (Exception ex) {
			throw ex;
		}

	}

	public void updateRmsFileById(Connection dbConn, String loginUserSqlId, String seqIdStr) throws Exception {
		String sql = "update RMS_FILE set DEL_USER=" + loginUserSqlId + ", DEL_TIME=? WHERE SEQ_ID IN (" + seqIdStr + ")";
		PreparedStatement stmt = null;
		try {
			stmt = dbConn.prepareStatement(sql);
			stmt.setTimestamp(1, T9Utility.parseTimeStamp());
			stmt.executeUpdate();

		} catch (Exception ex) {
			throw ex;
		} finally {
			T9DBUtility.close(stmt, null, log);
		}

	}

	/**
	 * 取得文件列表
	 * 
	 * @param dbConn
	 * @param request
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public String getRmsFileJosn(Connection dbConn, Map request, T9Person person) throws Exception {
		String sql = "";
		if (person.isAdminRole()) {
			sql = "SELECT SEQ_ID,FILE_CODE,FILE_TITLE,SECRET,SEND_UNIT,SEND_DATE,ROLL_ID,HANDLER_TIME,TURN_COUNT,FILE_WORD,FILE_YEAR,ISSUE_NUM from RMS_FILE where (DEL_USER='' or del_user is null) ";
		} else {
			sql = "select f.SEQ_ID, " 
				+ "f.FILE_CODE, " 
				+ "f.FILE_TITLE, " 
				+ "f.SECRET, " 
				+ "f.SEND_UNIT, " 
				+ "f.SEND_DATE, " 
				+ "f.ROLL_ID, "
				+ "f.HANDLER_TIME, "
				+ "f.TURN_COUNT, "
				+ "f.FILE_WORD, "
				+ "f.FILE_YEAR, "
				+ "f.ISSUE_NUM "
				+ "from RMS_FILE f left join RMS_ROLL r on  f.ROLL_ID = r.SEQ_ID " + "where (f.ADD_USER='" + person.getSeqId() + "' or r.ADD_USER='" + person.getSeqId() + "' or r.MANAGER='"
				+ person.getSeqId() + "') and (f.DEL_USER='' or f.DEL_USER is null)";
		}
		T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
		T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
		return pageDataList.toJson();
	}

	/**
	 * 获取案卷下拉列表值
	 * 
	 * @param dbConn
	 * @param parentNo
	 * @return
	 * @throws Exception
	 */
	public String getRmsRollSelectOption(Connection dbConn ) throws Exception {
		T9ORM orm = new T9ORM();

		String data = "";

		List<Map<Object, Object>> list = new ArrayList<Map<Object, Object>>();
		StringBuffer sb = new StringBuffer("[");

		String query = " select SEQ_ID,ROLL_CODE,ROLL_NAME from RMS_ROLL ";
		Statement stm1 = null;
		ResultSet rs1 = null;

		try {
			stm1 = dbConn.createStatement();
			rs1 = stm1.executeQuery(query);
			while (rs1.next()) {
				Map<Object, Object> objMap = new HashMap<Object, Object>();

				objMap.put("seqId", rs1.getInt("SEQ_ID"));
				objMap.put("rollCode", rs1.getString("ROLL_CODE"));
				objMap.put("rollName", rs1.getString("ROLL_NAME"));
				list.add(objMap);

			}

		} catch (Exception ex) {
			throw ex;
		} finally {
			T9DBUtility.close(stm1, rs1, null);
		}

		boolean isHave = false;

		if (list != null && list.size() > 0) {

			for (Map<Object, Object> map : list) {
				int seqId = (Integer) map.get("seqId");
				String rollCode = (String) map.get("rollCode");
				String rollName = (String) map.get("rollName");

				sb.append("{");
				sb.append("rollId:\"" + seqId + "\"");
				sb.append(",rollCode:\"" + rollCode + "\"");
				sb.append(",rollName:\"" + T9Utility.encodeSpecial(rollName) + "\"");
				sb.append("},");
				isHave = true;

			}

			if (isHave) {
				sb.deleteCharAt(sb.length() - 1);
			}
			sb.append("]");

		} else {
			sb.append("]");
		}

		data = sb.toString();
		return data;
	}
	/**
   * 获取案卷下拉列表值

   * 
   * @param dbConn
   * @param parentNo
   * @return
   * @throws Exception
   */
  public String getRmsRollSelectOption2(Connection dbConn  , int deptId) throws Exception {
    T9ORM orm = new T9ORM();

    String data = "";

    List<Map<Object, Object>> list = new ArrayList<Map<Object, Object>>();
    StringBuffer sb = new StringBuffer("[");

    String query = " select SEQ_ID,ROLL_CODE,ROLL_NAME from RMS_ROLL where DEPT_ID =" + deptId;
    Statement stm1 = null;
    ResultSet rs1 = null;

    try {
      stm1 = dbConn.createStatement();
      rs1 = stm1.executeQuery(query);
      while (rs1.next()) {
        Map<Object, Object> objMap = new HashMap<Object, Object>();

        objMap.put("seqId", rs1.getInt("SEQ_ID"));
        objMap.put("rollCode", rs1.getString("ROLL_CODE"));
        objMap.put("rollName", rs1.getString("ROLL_NAME"));
        list.add(objMap);

      }

    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm1, rs1, null);
    }

    boolean isHave = false;

    if (list != null && list.size() > 0) {

      for (Map<Object, Object> map : list) {
        int seqId = (Integer) map.get("seqId");
        String rollCode = (String) map.get("rollCode");
        String rollName = (String) map.get("rollName");

        sb.append("{");
        sb.append("rollId:\"" + seqId + "\"");
        sb.append(",rollCode:\"" + rollCode + "\"");
        sb.append(",rollName:\"" + T9Utility.encodeSpecial(rollName) + "\"");
        sb.append("},");
        isHave = true;

      }

      if (isHave) {
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("]");

    } else {
      sb.append("]");
    }

    data = sb.toString();
    return data;
  }
	/**
	 * 更新浮动菜单附件
	 * 
	 * @param dbConn
	 * @param seqIdStr
	 * @param attachId
	 * @param attachName
	 * @return
	 * @throws Exception
	 */
	public boolean updateFloadFile(Connection dbConn, String seqIdStr, String attachId, String attachName) throws Exception {
		boolean returnFlag = false;
		int seqId = 0;
		if (!T9Utility.isNullorEmpty(seqIdStr)) {
			seqId = Integer.parseInt(seqIdStr);
		}

		try {

			T9RmsFile rmsFile = this.getRmsFileDetailById(dbConn, seqId);
			String attachIdOld = "";
			String attachNameOld = "";

			String docAttachIdOld = "";
			String docAttachNameOld = "";

			if (rmsFile != null) {
				attachIdOld = T9Utility.null2Empty(rmsFile.getAttachmentId());
				attachNameOld = T9Utility.null2Empty(rmsFile.getAttachmentName());
				docAttachIdOld = T9Utility.null2Empty(rmsFile.getDocAttachmentId());
				docAttachNameOld = T9Utility.null2Empty(rmsFile.getDocAttachmentName());
			}
			String[] attachIdArrays = attachIdOld.split(",");
			String[] attachNameArrays = attachNameOld.split("\\*");

			String[] docAttachIdArrays = docAttachIdOld.split(",");
			String[] docAttachNameArrays = docAttachNameOld.split("\\*");

			String attaId = "";
			String attaName = "";

			String docAttaId = "";
			String docAttaName = "";

			// 正文
			if (!T9Utility.isNullorEmpty(docAttachIdOld) && docAttachIdArrays.length > 0) {
				for (int i = 0; i < docAttachIdArrays.length; i++) {
					if (attachId.equals(docAttachIdArrays[i])) {
						continue;
					}

					docAttaId += docAttachIdArrays[i] + ",";
					docAttaName += docAttachNameArrays[i] + "*";

				}

				rmsFile.setDocAttachmentId(docAttaId.trim());
				rmsFile.setDocAttachmentName(docAttaName.trim());

			}
			// 附件
			if (!T9Utility.isNullorEmpty(attachIdOld.trim()) && attachIdArrays.length > 0) {
				for (int i = 0; i < attachIdArrays.length; i++) {
					if (attachId.equals(attachIdArrays[i])) {
						continue;
					}
					attaId += attachIdArrays[i] + ",";
					attaName += attachNameArrays[i] + "*";
				}

				rmsFile.setAttachmentId(attaId.trim());
				rmsFile.setAttachmentName(attaName.trim());

			}

			this.updateRmsFileByObj(dbConn, rmsFile);
			returnFlag = true;

		} catch (Exception e) {
			throw e;
		}

		return returnFlag;
	}

	/**
	 * 文件查询
	 * @param dbConn
	 * @param request
	 * @param person
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public String queryRmsFileLogic(Connection dbConn, Map request, T9Person person, Map<Object, Object> map) throws Exception {
		String fileCode = (String) map.get("fileCode");
		String fileSubject = (String) map.get("fileSubject");
		String fileTitle = (String) map.get("fileTitle");
		String fileTitleo = (String) map.get("fileTitleo");
		String sendUnit = (String) map.get("sendUnit");

		String sendTimeMin = (String) map.get("sendTimeMin");
		String sendTimeMax = (String) map.get("sendTimeMax");
		String secret = (String) map.get("secret");
		String urgency = (String) map.get("urgency");
		String fileType = (String) map.get("fileType");
		String fileKind = (String) map.get("fileKind");

		String filePage1 = (String) map.get("filePage1");
		String filePage2 = (String) map.get("filePage2");
		String printPage1 = (String) map.get("printPage1");
		String printPage2 = (String) map.get("printPage2");
		String remark = (String) map.get("remark");
		String handlerTime = (String) map.get("handlerTime");
		String fileWord = (String) map.get("fileWord");
    String fileYear = (String) map.get("fileYear");
    String  issueNum= (String) map.get("issueNum");
		try {
			String conditionStr = "";

			if (!T9Utility.isNullorEmpty(fileCode)) {
				conditionStr += " and FILE_CODE like '%" + T9DBUtility.escapeLike(fileCode) + "%'" + T9DBUtility.escapeLike();
			}
			if (!T9Utility.isNullorEmpty(fileSubject)) {
				conditionStr += " and FILE_SUBJECT like '%" + T9DBUtility.escapeLike(fileSubject) + "%'" + T9DBUtility.escapeLike();
			}
			if (!T9Utility.isNullorEmpty(fileTitle)) {
				conditionStr += " and FILE_TITLE like '%" + T9DBUtility.escapeLike(fileTitle) + "%'" + T9DBUtility.escapeLike();
			}
			if (!T9Utility.isNullorEmpty(fileTitleo)) {
				conditionStr += " and FILE_TITLEO like '%" + T9DBUtility.escapeLike(fileTitleo) + "%'" + T9DBUtility.escapeLike();
			}
			if (!T9Utility.isNullorEmpty(sendUnit)) {
				conditionStr += " and SEND_UNIT like '%" + T9DBUtility.escapeLike(sendUnit) + "%'" + T9DBUtility.escapeLike();
			}
			if (!T9Utility.isNullorEmpty(remark)) {
				conditionStr += " and RMS_FILE.REMARK like '%" + T9DBUtility.escapeLike(remark) + "%'" + T9DBUtility.escapeLike();
			}
			if (!T9Utility.isNullorEmpty(handlerTime)) {
				conditionStr += " and RMS_FILE.HANDLER_TIME like '%" + T9DBUtility.escapeLike(handlerTime) + "%'" + T9DBUtility.escapeLike();
			}

			if (!T9Utility.isNullorEmpty(secret)) {
				conditionStr += " and SECRET like '%" + T9DBUtility.escapeLike(secret) + "%'" + T9DBUtility.escapeLike();
			}
			if (!T9Utility.isNullorEmpty(urgency)) {
				conditionStr += " and URGENCY like '%" + T9DBUtility.escapeLike(urgency) + "%'" + T9DBUtility.escapeLike();
			}
			if (!T9Utility.isNullorEmpty(fileType)) {
				conditionStr += " and FILE_TYPE like '%" + T9DBUtility.escapeLike(fileType) + "%'" + T9DBUtility.escapeLike();
			}
			if (!T9Utility.isNullorEmpty(fileKind)) {
				conditionStr += " and FILE_KIND like '%" + T9DBUtility.escapeLike(fileKind) + "%'" + T9DBUtility.escapeLike();
			}

			if (!T9Utility.isNullorEmpty(sendTimeMin)) {
				conditionStr += " and " + T9DBUtility.getDateFilter("SEND_DATE", sendTimeMin, ">=");
			}
			if (!T9Utility.isNullorEmpty(sendTimeMax)) {
				conditionStr += " and " + T9DBUtility.getDateFilter("SEND_DATE", sendTimeMax, "<=");
			}
			if (!T9Utility.isNullorEmpty(filePage1)) {
				conditionStr += " and FILE_PAGE >= '" + T9DBUtility.escapeLike(filePage1) + "'";
			}
			if (!T9Utility.isNullorEmpty(filePage2)) {
				conditionStr += " and FILE_PAGE <= '" + T9DBUtility.escapeLike(filePage2) + "'";
			}

			if (!T9Utility.isNullorEmpty(printPage1)) {
				conditionStr += " and PRINT_PAGE >= '" + T9DBUtility.escapeLike(printPage1) + "'";
			}
			if (!T9Utility.isNullorEmpty(printPage2)) {
        conditionStr += " and PRINT_PAGE >= '" + T9DBUtility.escapeLike(printPage1) + "'";
      }
			if (!T9Utility.isNullorEmpty(fileWord)) {
				conditionStr += " and FILE_WORD like  '%" + T9DBUtility.escapeLike(fileWord) + "%'" + T9DBUtility.escapeLike();
			}
			if (!T9Utility.isNullorEmpty(fileYear)) {
        conditionStr += " and FILE_YEAR = '" + T9DBUtility.escapeLike(fileYear) + "'";
      }
			if (!T9Utility.isNullorEmpty(issueNum)) {
        conditionStr += " and ISSUE_NUM =  '" + T9DBUtility.escapeLike(issueNum) + "'" ;
      }
			String query = "";
			if (person.isAdminRole()) {
				query = "SELECT SEQ_ID,FILE_CODE,FILE_TITLE,SECRET,SEND_UNIT,SEND_DATE,ROLL_ID,FILE_WORD,FILE_YEAR,ISSUE_NUM from RMS_FILE where (DEL_USER='' or del_user is null) ";
			} else {
				query = "select f.SEQ_ID, " 
				  + "f.FILE_CODE, "
				  + "f.FILE_TITLE, "
				  + "f.SECRET, " 
				  + "f.SEND_UNIT, "
				  + "f.SEND_DATE, "
				  + "f.ROLL_ID ,"
				  + "f.FILE_WORD, "
	        + "f.FILE_YEAR, "
	        + "f.ISSUE_NUM "
						+ "from RMS_FILE f left join RMS_ROLL r on f.ROLL_ID=r.SEQ_ID "
						+ " where  (f.ADD_USER='" + person.getSeqId() + "' or r.ADD_USER='" + person.getSeqId() + "' or r.MANAGER='"
						+ person.getSeqId() + "') and ( f.DEL_USER is null or f.DEL_USER = '' ) ";
			}
			query += conditionStr + " order by SEND_DATE desc";
			T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
			T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, query);
			return pageDataList.toJson();

		} catch (Exception e) {
			throw e;
		}

	}

	/**
	 * 组卷至
	 * 
	 * @param dbConn
	 * @param seqIdStr
	 * @param rollId
	 * @throws Exception
	 */
	public void changeRollLogic(Connection dbConn, String seqIdStr, int rollId) throws Exception {
		String sql = "update RMS_FILE set ROLL_ID=? WHERE SEQ_ID IN (" + seqIdStr + ")";
		PreparedStatement stmt = null;
		try {
			stmt = dbConn.prepareStatement(sql);
			stmt.setInt(1, rollId);
			stmt.executeUpdate();

		} catch (Exception ex) {
			throw ex;
		} finally {
			T9DBUtility.close(stmt, null, log);
		}
	}

	/**
	 * 取得已销毁文件
	 * 
	 * @param dbConn
	 * @param request
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public String getDestroyFileLogic(Connection dbConn, Map request, T9Person person) throws Exception {
		String sql = "";
		T9PageDataList pageDataList = null;
		try {
			if (person.isAdminRole()) {

				sql = "SELECT SEQ_ID,FILE_CODE,FILE_TITLE,SECRET,SEND_UNIT,SEND_DATE,ROLL_ID from RMS_FILE where (DEL_USER<>'' or DEL_USER is not null)";

			} else {
				sql = "SELECT SEQ_ID,FILE_CODE,FILE_TITLE,SECRET,SEND_UNIT,SEND_DATE,ROLL_ID from RMS_FILE where  ADD_USER='" + person.getSeqId()
						+ "' and ( DEL_USER<>'' or DEL_USER is not null)";

			}

			T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
			pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);

		} catch (Exception e) {
			throw e;
		}
		return pageDataList.toJson();
	}

	/**
	 * 还原文件
	 * 
	 * @param dbConn
	 * @param seqIdStr
	 * @throws Exception
	 */
	public void updateDestroyFileById(Connection dbConn, String seqIdStr) throws Exception {
		String sql = "update RMS_FILE set DEL_USER='' WHERE SEQ_ID IN (" + seqIdStr + ")";
		PreparedStatement stmt = null;
		try {
			stmt = dbConn.prepareStatement(sql);
			stmt.executeUpdate();

		} catch (Exception ex) {
			throw ex;
		} finally {
			T9DBUtility.close(stmt, null, log);
		}
	}

	/**
	 * 删除文件
	 * @param dbConn
	 * @param seqIdStr
	 * @throws Exception
	 */
	public void deleteFileLogic(Connection dbConn, String seqIdStr, String filePath) throws Exception {
		T9ORM orm = new T9ORM();
		if (T9Utility.isNullorEmpty(seqIdStr)) {
			seqIdStr = "";
		}
		try {
			String seqIdArry[] = seqIdStr.split(",");
			if (!"".equals(seqIdArry) && seqIdArry.length > 0) {
				for (String seqId : seqIdArry) {
					StringBuffer attIdBuffer = new StringBuffer();
					StringBuffer attNameBuffer = new StringBuffer();
					T9RmsFile rmsFile = this.getRmsFileDetailById(dbConn, Integer.parseInt(seqId));
					String attachmentId = T9Utility.null2Empty(rmsFile.getAttachmentId());
					String attachmentName = T9Utility.null2Empty(rmsFile.getAttachmentName());
					String docAttachmentId = T9Utility.null2Empty(rmsFile.getDocAttachmentId());
					String docAttachmentName = T9Utility.null2Empty(rmsFile.getDocAttachmentName());
					attIdBuffer.append(attachmentId.trim() + docAttachmentId.trim());
					attNameBuffer.append(attachmentName.trim() + docAttachmentName.trim());
					String[] attIdArray = {};
					String[] attNameArray = {};
					if (!T9Utility.isNullorEmpty(attIdBuffer.toString()) && !T9Utility.isNullorEmpty(attNameBuffer.toString()) && attIdBuffer.length() > 0) {
						attIdArray = attIdBuffer.toString().trim().split(",");
						attNameArray = attNameBuffer.toString().trim().split("\\*");
					}
					if (attIdArray != null && attIdArray.length > 0) {
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
										T9FileUtility.deleteAll(file.getAbsoluteFile());
									} else if (oldFile.exists()) {
										T9FileUtility.deleteAll(oldFile.getAbsoluteFile());
									}
								}
							}
						}
					}
					// 删除数据库信息
					orm.deleteSingle(dbConn, rmsFile);
				}
			}
		} catch (Exception e) {
			throw e;
		}
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
	 * 得到附件的Id 兼老数据
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
	 * 文件档案导出
	 * 
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public ArrayList<T9DbRecord> toExportRmsFileData(Connection conn, String seqIdStr) throws Exception {
		ArrayList<T9DbRecord> result = new ArrayList<T9DbRecord>();
		String sql = "SELECT " + " FILE_CODE " + ",FILE_SUBJECT " + ",FILE_TITLE " + ",FILE_TITLEO " + ",SEND_UNIT" + ",SEND_DATE" + ",SECRET"
				+ ",URGENCY" + ",FILE_TYPE" + ",FILE_KIND" + ",FILE_PAGE" + ",PRINT_PAGE" + ",REMARK" + " from RMS_FILE where SEQ_ID IN (" + seqIdStr
				+ ") and (DEL_USER = '' or DEL_USER is null)";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			T9RmsRollLogic rollLogic = new T9RmsRollLogic();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				T9DbRecord record = new T9DbRecord();
				String fileCode = rs.getString(1);
				String fileSubject = rs.getString(2);
				String fileTitle = rs.getString(3);
				String fileTitleo = rs.getString(4);
				String sendUnit = rs.getString(5);
				Date sendDate = rs.getTimestamp(6);
				String secret = rs.getString(7);
				String urgency = rs.getString(8);
				String fileType = rs.getString(9);
				String fileKind = rs.getString(10);
				String filePage = rs.getString(11);
				String printPage = rs.getString(12);
				String remark = rs.getString(13);

				record.addField("文件号", fileCode);
				record.addField("文件主题词", fileSubject);
				record.addField("文件标题", fileTitle);
				record.addField("文件副标题", fileTitleo);
				record.addField("发文单位", sendUnit);
				record.addField("发文日期", T9Utility.getDateTimeStrCn(sendDate));
				record.addField("密级", rollLogic.getRmsSecret(conn, secret).toString());
				record.addField("紧急等级", rollLogic.getUrgency(conn, urgency).toString());
				record.addField("文件分类", rollLogic.getFileType(conn, fileType).toString());
				record.addField("公文类别", rollLogic.getFileKind(conn, fileKind).toString());
				record.addField("文件页数", filePage);
				record.addField("打印页数", printPage);
				record.addField("备注", remark);
				result.add(record);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(ps, rs, null);
		}
		return result;
	}

	/**
	 * 增加值
	 * 
	 * @param dbConn
	 * @param fileForm
	 * @param person
	 * @param map
	 * @throws Exception
	 */
	public void setRmsFileValue(Connection dbConn, T9FileUploadForm fileForm, T9Person person, Map<Object, Object> map) throws Exception {
		boolean fromFolderFlag = (Boolean) map.get("fromFolderFlag");
		boolean uploadFlag = (Boolean) map.get("uploadFlag");
		String newAttchId = (String) map.get("newAttchId");

		String attachmentId = (String) map.get("attachmentId");
		String newAttchName = (String) map.get("newAttchName");
		String attachmentName = (String) map.get("attachmentName");

		boolean docAttachmentFlag = (Boolean) map.get("docAttachmentFlag");
		String docAttachmentId = (String) map.get("docAttachmentId");
		String docAttachmentName = (String) map.get("docAttachmentName");

		String fileCode = (String) fileForm.getParameter("fileCode");
		String fileSubject = (String) fileForm.getParameter("fileSubject");
		String fileTitle = (String) fileForm.getParameter("fileTitle");

		String fileTitleo = (String) fileForm.getParameter("fileTitleo");
		String sendUnit = (String) fileForm.getParameter("sendUnit");
		String sendDate = (String) fileForm.getParameter("sendDate");
		String secret = (String) fileForm.getParameter("secret");
		String urgency = (String) fileForm.getParameter("urgency");
		String fileType = (String) fileForm.getParameter("fileType");
		String fileKind = (String) fileForm.getParameter("fileKind");
		String filePage = (String) fileForm.getParameter("filePage");
		String printPage = (String) fileForm.getParameter("printPage");
		String remark = (String) fileForm.getParameter("remark");
		String rollIdStr = (String) fileForm.getParameter("rollId");
		String downloadYnStr = (String) fileForm.getParameter("downloadYn");
		String fileWord  = (String) fileForm.getParameter("fileWord");
		String fileYear  = (String) fileForm.getParameter("fileYear");
		String issueNum  = (String) fileForm.getParameter("issueNum");
		
		String privUser = T9Utility.null2Empty((String) fileForm.getParameter("privUser"));
		String privDept =T9Utility.null2Empty((String) fileForm.getParameter("privDept"));
		String privRole =T9Utility.null2Empty((String) fileForm.getParameter("role"));
    
		int rollId = 0;
		int downloadYn = 0;
		if (!T9Utility.isNullorEmpty(rollIdStr)) {
			rollId = Integer.parseInt(rollIdStr);
		}
		if (!T9Utility.isNullorEmpty(downloadYnStr)) {
			downloadYn = Integer.parseInt(downloadYnStr);
		}

		try {

			T9RmsFile rmsFile = new T9RmsFile();

			if (fromFolderFlag && uploadFlag) {

				rmsFile.setAttachmentId(newAttchId.trim() + attachmentId.trim());
				rmsFile.setAttachmentName(newAttchName.trim() + attachmentName.trim());

			} else if (fromFolderFlag) {
				rmsFile.setAttachmentId(newAttchId.trim());
				rmsFile.setAttachmentName(newAttchName.trim());
			} else if (uploadFlag) {
				rmsFile.setAttachmentId(attachmentId.trim());
				rmsFile.setAttachmentName(attachmentName.trim());

			}

			if (docAttachmentFlag) {
				rmsFile.setDocAttachmentId(docAttachmentId.trim());
				rmsFile.setDocAttachmentName(docAttachmentName.trim());
			}
			rmsFile.setAddUser(String.valueOf(person.getSeqId()));
			rmsFile.setAddTime(T9Utility.parseTimeStamp());
			rmsFile.setFileCode(fileCode);

			rmsFile.setFileTitle(fileTitle);
			rmsFile.setFileTitleo(fileTitleo);
			rmsFile.setFileSubject(fileSubject);

			rmsFile.setSendUnit(sendUnit);
			rmsFile.setSendDate(T9Utility.parseDate(sendDate));
			rmsFile.setSecret(secret);
			rmsFile.setUrgency(urgency);
			rmsFile.setFileKind(fileKind);

			rmsFile.setFileType(fileType);
			rmsFile.setFilePage(filePage);
			rmsFile.setPrintPage(printPage);
			rmsFile.setRemark(remark);
			rmsFile.setRollId(rollId);
			rmsFile.setDownloadYn(downloadYn);
			rmsFile.setFileWord(fileWord);
			rmsFile.setFileYear(fileYear);
			rmsFile.setIssueNum(issueNum);
			rmsFile.setPrivRole(privRole);
			rmsFile.setPrivUser(privUser);
			rmsFile.setPrivDept(privDept);
			this.addRmsFileInfo(dbConn, rmsFile);

		} catch (Exception e) {
			throw e;
		}

	}

}
