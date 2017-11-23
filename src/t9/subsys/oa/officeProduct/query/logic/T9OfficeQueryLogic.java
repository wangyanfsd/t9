package t9.subsys.oa.officeProduct.query.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.data.T9DbRecord;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.person.data.T9Person;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.officeProduct.officeType.data.T9OfficeDepository;
import t9.subsys.oa.officeProduct.officeType.data.T9OfficeType;

public class T9OfficeQueryLogic {
	private static Logger log = Logger.getLogger("t9.subsys.oa.officeProduct.query.logic.T9OfficeQueryLogic.java");

	/**
	 * 获取库树(办公用品信息查询)
	 * 
	 * @param dbConn
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public String getQueryTreeLogic(Connection dbConn, T9Person person) throws Exception {
		T9ORM orm = new T9ORM();
		StringBuffer sb = new StringBuffer("[");
		boolean isHave = false;
		try {
			int loginDeptId = person.getDeptId();
			List<T9OfficeDepository> officeQueries = orm.loadListSingle(dbConn, T9OfficeDepository.class, new HashMap());
			if (officeQueries != null && officeQueries.size() > 0) {
				for (T9OfficeDepository officeQuery : officeQueries) {
					int dbSeqId = officeQuery.getSeqId();
					String depositoryName = T9Utility.null2Empty(officeQuery.getDepositoryName());
					String dbDeptIdStr = T9Utility.null2Empty(officeQuery.getDeptId());
					String officeTypeId = T9Utility.null2Empty(officeQuery.getOfficeTypeId());
					boolean loginDeptIdFlag = this.isFindInSet(dbDeptIdStr.split(","), String.valueOf(loginDeptId));
					if (loginDeptIdFlag || "".equals(dbDeptIdStr) || "ALL_DEPT".equals(dbDeptIdStr) || "0".equals(dbDeptIdStr) || person.isAdminRole()) {
						if (T9Utility.isNullorEmpty(officeTypeId)) {
							sb.append("{");
							sb.append("nodeId:\"" + dbSeqId + ",store" + "\"");
							sb.append(",name:\"" + T9Utility.encodeSpecial(depositoryName) + "\"");
							sb.append(",isHaveChild:" + 0 + "");
							sb.append(",extData:\"" + "" + "\"");
							sb.append("},");
							isHave = true;
						} else {
							sb.append("{");
							sb.append("nodeId:\"" + dbSeqId + ",store" + "\"");
							sb.append(",name:\"" + T9Utility.encodeSpecial(depositoryName) + "\"");
							sb.append(",isHaveChild:" + 1 + "");
							sb.append(",extData:\"" + "" + "\"");
							sb.append("},");
							isHave = true;
						}
					}
				}
				if (isHave) {
					sb = sb.deleteCharAt(sb.length() - 1);
				}
				sb.append("]");
			} else {
				sb.append("]");
			}
		} catch (Exception e) {
			throw e;
		}
		return sb.toString();
	}

	/**
	 * 获取库树(办公用品信息管理) 2011-3-29
	 * 
	 * @param dbConn
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public String getManageTreeLogic(Connection dbConn, T9Person person) throws Exception {
		T9ORM orm = new T9ORM();
		StringBuffer sb = new StringBuffer("[");
		boolean isHave = false;
		try {
			int loginDeptId = person.getDeptId();
			List<T9OfficeDepository> officeQueries = orm.loadListSingle(dbConn, T9OfficeDepository.class, new HashMap());
			if (officeQueries != null && officeQueries.size() > 0) {
				for (T9OfficeDepository officeQuery : officeQueries) {
					int dbSeqId = officeQuery.getSeqId();
					String depositoryName = T9Utility.null2Empty(officeQuery.getDepositoryName());
					String dbDeptIdStr = T9Utility.null2Empty(officeQuery.getDeptId());
					String officeTypeId = T9Utility.null2Empty(officeQuery.getOfficeTypeId());
					boolean loginDeptIdFlag = this.isFindInSet(dbDeptIdStr.split(","), String.valueOf(loginDeptId));
					if (loginDeptIdFlag || "".equals(dbDeptIdStr) || "ALL_DEPT".equals(dbDeptIdStr) || "0".equals(dbDeptIdStr) || person.isAdminRole()) {
						if (T9Utility.isNullorEmpty(officeTypeId)) {
							sb.append("{");
							sb.append("nodeId:\"" + dbSeqId + ",store" + "\"");
							sb.append(",name:\"" + T9Utility.encodeSpecial(depositoryName) + "\"");
							sb.append(",isHaveChild:" + 0 + "");
							sb.append(",extData:\"" + "" + "\"");
							sb.append("},");
							isHave = true;
						} else {
							sb.append("{");
							sb.append("nodeId:\"" + dbSeqId + ",store" + "\"");
							sb.append(",name:\"" + T9Utility.encodeSpecial(depositoryName) + "\"");
							sb.append(",isHaveChild:" + 1 + "");
							sb.append(",extData:\"" + "" + "\"");
							sb.append("},");
							isHave = true;
						}
					}
				}

				// this.getUndefindTypePro(dbConn,isHave,sb);
				boolean undefindTypeFlag = this.isUndefindTypePro(dbConn);
				if (undefindTypeFlag) {
					sb.append("{");
					sb.append("nodeId:\"" + "undefindType" + ",store" + "\"");
					sb.append(",name:\"" + "未分类办公用品" + "\"");
					sb.append(",isHaveChild:" + 1 + "");
					sb.append(",extData:\"" + "" + "\"");
					sb.append("},");
					isHave = true;
				}

				if (isHave) {
					sb = sb.deleteCharAt(sb.length() - 1);
				}
				sb.append("]");
			} else {
				sb.append("]");
			}
		} catch (Exception e) {
			throw e;
		}
		return sb.toString();
	}

	/**
	 * 获取类别树
	 * 
	 * 
	 * @param dbConn
	 * @param person
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public String getTypeTreeLogic(Connection dbConn, T9Person person, String id) throws Exception {
		T9ORM orm = new T9ORM();
		StringBuffer sb = new StringBuffer("[");
		boolean isHave = false;
		try {
			int seqId = 0;
			if (T9Utility.isNumber(id)) {
				seqId = Integer.parseInt(id);
			}
			Map map = new HashMap();
			map.put("TYPE_DEPOSITORY", seqId);
			List<T9OfficeType> officeTypes = orm.loadListSingle(dbConn, T9OfficeType.class, map);
			if (officeTypes != null && officeTypes.size() > 0) {
				for (T9OfficeType officeType : officeTypes) {
					int dbSeqId = officeType.getSeqId();
					String typeName = T9Utility.null2Empty(officeType.getTypeName());
					int counter = this.getCountProduce(dbConn, person, String.valueOf(dbSeqId));
					if (counter > 0) {
						sb.append("{");
						sb.append("nodeId:\"" + dbSeqId + ",type,produce" + "\"");
						sb.append(",name:\"" + T9Utility.encodeSpecial(typeName) + "\"");
						sb.append(",isHaveChild:" + 1 + "");
						sb.append(",extData:\"" + seqId + "\"");
						sb.append("},");
						isHave = true;
					} else {
						sb.append("{");
						sb.append("nodeId:\"" + dbSeqId + ",type,produce" + "\"");
						sb.append(",name:\"" + T9Utility.encodeSpecial(typeName) + "\"");
						sb.append(",isHaveChild:" + 0 + "");
						sb.append(",extData:\"" + seqId + "\"");
						sb.append("},");
						isHave = true;
					}
				}
				if (isHave) {
					sb = sb.deleteCharAt(sb.length() - 1);
				}
				sb.append("]");
			} else {
				sb.append("]");
			}

		} catch (Exception e) {
			throw e;
		}
		return sb.toString();
	}

	/**
	 * 获取办公用品树
	 * 
	 * @param dbConn
	 * @param person
	 * @param id为类别seq_id
	 * @return
	 * @throws Exception
	 */
	public String getProductsTreeLogic(Connection dbConn, T9Person person, String id) throws Exception {

		T9ORM orm = new T9ORM();
		StringBuffer sb = new StringBuffer("[");
		boolean isHave = false;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			int seqId = 0;
			if (T9Utility.isNumber(id)) {
				seqId = Integer.parseInt(id);
			}

			T9OfficeType officeType = (T9OfficeType) orm.loadObjSingle(dbConn, T9OfficeType.class, seqId);
			int storeSeqId = 0;
			if (officeType != null) {
				storeSeqId = officeType.getTypeDepository();
			}
			String conditionStr = "";
			if (!person.isAdminRole()) {
				conditionStr = "((" + T9DBUtility.findInSet(String.valueOf(person.getSeqId()), "PRO_MANAGER");
				conditionStr += " or " + T9DBUtility.findInSet(String.valueOf(person.getDeptId()), "PRO_DEPT") + ")";
				conditionStr += " or (PRO_MANAGER='' and PRO_DEPT='') or (PRO_DEPT='ALL_DEPT' or PRO_DEPT='0'))";
			} else {
				conditionStr = " 1=1";
			}
			String sql = "select SEQ_ID,PRO_NAME from OFFICE_PRODUCTS where OFFICE_PROTYPE =? and " + conditionStr;
			stmt = dbConn.prepareStatement(sql);
			stmt.setString(1, String.valueOf(seqId));
			rs = stmt.executeQuery();
			String imgAddress = "/t9/core/styles/style1/img/4[1].gif";
			while (rs.next()) {
				int dbSeqId = rs.getInt("SEQ_ID");
				String proName = T9Utility.null2Empty(rs.getString("PRO_NAME"));
				sb.append("{");
				sb.append("nodeId:\"" + dbSeqId + "\"");
				sb.append(",name:\"" + T9Utility.encodeSpecial(proName) + "\"");
				sb.append(",isHaveChild:" + 0 + "");
				sb.append(",imgAddress:\"" + imgAddress + "\"");
				sb.append(",extData:\"" + storeSeqId + "\"");
				sb.append("},");
				isHave = true;
			}
			if (isHave) {
				sb = sb.deleteCharAt(sb.length() - 1);
			}
			sb.append("]");
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(stmt, rs, log);
		}
		return sb.toString();
	}

	/**
	 * 获取办公用品树(不属于任何类型的办公用品) 2011-3-29
	 * 
	 * @param dbConn
	 * @return
	 * @throws Exception
	 */
	public String getUndefindTypePro(Connection dbConn) throws Exception {
		StringBuffer sb = new StringBuffer("[");
		boolean isHave = false;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT distinct SEQ_ID, PRO_NAME,OFFICE_PROTYPE,PRO_CREATOR FROM OFFICE_PRODUCTS  WHERE OFFICE_PROTYPE not in(SELECT SEQ_ID FROM OFFICE_TYPE )";
		try {
			stmt = dbConn.prepareStatement(sql);
			rs = stmt.executeQuery();
			String imgAddress = "/t9/core/styles/style1/img/4[1].gif";
			while (rs.next()) {
				int dbSeqId = rs.getInt("SEQ_ID");
				String proName = T9Utility.null2Empty(rs.getString("PRO_NAME"));
				sb.append("{");
				sb.append("nodeId:\"" + dbSeqId + "\"");
				sb.append(",name:\"" + T9Utility.encodeSpecial(proName) + "\"");
				sb.append(",isHaveChild:" + 0 + "");
				sb.append(",imgAddress:\"" + imgAddress + "\"");
				sb.append(",extData:\"" + "unType" + "\"");
				sb.append("},");
				isHave = true;
			}
			if (isHave) {
				sb = sb.deleteCharAt(sb.length() - 1);
			}
			sb.append("]");
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(stmt, rs, log);
		}
		return sb.toString();
	}

	public boolean isUndefindTypePro(Connection dbConn) throws Exception {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT count(SEQ_ID) FROM OFFICE_PRODUCTS  WHERE OFFICE_PROTYPE not in(SELECT SEQ_ID FROM OFFICE_TYPE )";
		int counter = 0;
		boolean flag = false;
		try {
			stmt = dbConn.prepareStatement(sql);
			rs = stmt.executeQuery();
			if (rs.next()) {
				counter = rs.getInt(1);
			}
			if (counter > 0) {
				flag = true;
			}
			return flag;
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(stmt, rs, log);
		}
	}

	/**
	 * 查询单个办公用品信息
	 * 
	 * @param dbConn
	 * @param person
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public String getOneOfficeProductInfoLogic(Connection dbConn, T9Person person, String id) throws Exception {

		T9ORM orm = new T9ORM();
		StringBuffer sb = new StringBuffer("[");
		boolean isHave = false;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {

			String sql = "select SEQ_ID,PRO_NAME,PRO_DESC,OFFICE_PROTYPE,PRO_CODE,PRO_UNIT,PRO_PRICE,PRO_SUPPLIER,PRO_LOWSTOCK,PRO_MAXSTOCK"
					+ " ,PRO_STOCK,PRO_DEPT,PRO_MANAGER,PRO_CREATOR,PRO_AUDITER from office_products where SEQ_ID =" + id;
			stmt = dbConn.prepareStatement(sql);
			rs = stmt.executeQuery();
			while (rs.next()) {
				int dbSeqId = rs.getInt("SEQ_ID");
				String officeProtype = T9Utility.null2Empty(rs.getString("OFFICE_PROTYPE"));
				String typeName = getOfficeProductsInfo(dbConn, person, officeProtype);

				sb.append("{");
				sb.append("seqId:\"" + dbSeqId + "\"");
				sb.append(",proName:\"" + T9Utility.encodeSpecial(rs.getString("PRO_NAME")) + "\"");
				sb.append(",proDesc:\"" + T9Utility.encodeSpecial(rs.getString("PRO_DESC")) + "\"");
				sb.append(",typeName:\"" + T9Utility.encodeSpecial(typeName) + "\"");
				sb.append(",proCode:\"" + T9Utility.encodeSpecial(rs.getString("PRO_CODE")) + "\"");
				sb.append(",proUnit:\"" + T9Utility.encodeSpecial(rs.getString("PRO_UNIT")) + "\"");
				sb.append(",proPrice:\"" + rs.getDouble("PRO_PRICE") + "\"");
				sb.append(",proSupplier:\"" + T9Utility.encodeSpecial(rs.getString("PRO_SUPPLIER")) + "\"");
				sb.append(",proLowstock:\"" + rs.getInt("PRO_LOWSTOCK") + "\"");
				sb.append(",proMaxstock:\"" + rs.getInt("PRO_MAXSTOCK") + "\"");
				sb.append(",proStock:\"" + rs.getInt("PRO_STOCK") + "\"");
				// sb.append(",isHaveChild:" + 0 + "");
				sb.append("},");
				isHave = true;
			}
			if (isHave) {
				sb = sb.deleteCharAt(sb.length() - 1);
			}
			sb.append("]");
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(stmt, rs, log);
		}
		return sb.toString();
	}

	/**
	 * 通过办公用品 查找办公用品类别
	 * 
	 * @param conn
	 * @param user
	 * @param officeId
	 * @return
	 * @throws Exception
	 */
	public String getOfficeProductsInfo(Connection conn, T9Person user, String officeId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String findSql = "select TYPE_NAME from OFFICE_TYPE where SEQ_ID =" + officeId + "";
		try {
			ps = conn.prepareStatement(findSql);
			rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getString("TYPE_NAME");
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			T9DBUtility.close(ps, rs, null);
		}
		return "";
	}

	/**
	 * 判断类别下是否有产品
	 * 
	 * @param dbConn
	 * @param person
	 * @param officeProtypeId
	 * @return
	 * @throws Exception
	 */
	public int getCountProduce(Connection dbConn, T9Person person, String officeProtypeId) throws Exception {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String conditionStr = "";
		if (!person.isAdminRole()) {
			conditionStr = "((" + T9DBUtility.findInSet(String.valueOf(person.getSeqId()), "PRO_MANAGER");
			conditionStr += " or " + T9DBUtility.findInSet(String.valueOf(person.getDeptId()), "PRO_DEPT") + ")";
			conditionStr += " or (PRO_MANAGER='' and PRO_DEPT='') or (PRO_DEPT='ALL_DEPT' or PRO_DEPT='0'))";
		} else {
			conditionStr = " 1=1";
		}
		String sql = "select count(*) from OFFICE_PRODUCTS where OFFICE_PROTYPE =? and " + conditionStr;
		int counter = 0;
		try {
			stmt = dbConn.prepareStatement(sql);
			stmt.setString(1, officeProtypeId);
			rs = stmt.executeQuery();
			if (rs.next()) {
				counter = rs.getInt(1);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(stmt, rs, log);
		}
		return counter;
	}

	/**
	 * 判断数组中是否有该值
	 * 
	 * @param str
	 * @param deptIdStr
	 * @return
	 */
	public boolean isFindInSet(String[] str, String deptIdStr) {
		for (int y = 0; y < str.length; y++) {
			if (str[y].equals(deptIdStr)) {
				return true;
			} else {
				continue;
			}
		}
		return false;
	}

	/**
	 * 办公用品信息查询 带分页的
	 * 
	 * @param dbConn
	 * @param request
	 * @param map
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public String queryOfficeProductsJsonLogic(Connection dbConn, Map request, Map map, T9Person person) throws Exception {
		String proName = (String) map.get("proName");
		String proDesc = (String) map.get("proDesc");
		String proCode = (String) map.get("proCode");
		String officeDepository = (String) map.get("officeDepository");
		String officeProtype = (String) map.get("officeProtype");
		String conditionStr = "";
		String sql = "";
		try {
			if (!T9Utility.isNullorEmpty(proName)) {
				conditionStr = " and a.PRO_NAME like '%" + T9DBUtility.escapeLike(proName) + "%'";
			}
			if (!T9Utility.isNullorEmpty(proDesc)) {
				conditionStr += " and a.PRO_DESC like '%" + T9DBUtility.escapeLike(proDesc) + "%'";
			}
			if (!T9Utility.isNullorEmpty(proCode)) {
				conditionStr += " and a.PRO_CODE like '%" + proCode + "%'";
			}
			/*
			 * if
			 * (!T9Utility.isNullorEmpty(officeDepository)&&!officeDepository.equals
			 * ("-1")) { conditionStr += " and b.SEQ_ID in (" +officeDepository+ ")";
			 * } if
			 * (!T9Utility.isNullorEmpty(officeProtype)&&!officeProtype.equals("-1"))
			 * { conditionStr += " and b.SEQ_ID in (" +officeProtype+ ")"; }
			 */
			if (!T9Utility.isNullorEmpty(officeDepository) && !officeDepository.equals("-1")) {
				conditionStr += " and a.OFFICE_PROTYPE in (" + officeDepository + ")";
			}
			if (!T9Utility.isNullorEmpty(officeProtype) && !officeProtype.equals("-1")) {
				conditionStr += " and a.OFFICE_PROTYPE in (" + officeProtype + ")";
			}

			sql = "select a.SEQ_ID" + ", a.PRO_NAME" + ",b.TYPE_NAME" + ",a.PRO_CODE" + ",a.PRO_UNIT" + ",a.PRO_PRICE" + ",a.PRO_SUPPLIER"
					+ ",a.PRO_LOWSTOCK" + ",a.PRO_STOCK" + ",a.PRO_DESC" + ",a.PRO_MAXSTOCK" + ",c.OFFICE_TYPE_ID" + ",b.SEQ_ID as typeId"
					+ ",b.TYPE_DEPOSITORY"
					+ " from office_products a,office_type b,office_depository c where a.office_protype = b.seq_id and b.type_depository=c.seq_id" + ""
					+ conditionStr + " ORDER BY a.PRO_NAME ASC";
			T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
			T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
//			System.out.println(pageDataList.toJson());
			return pageDataList.toJson();
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 办公用品查询 第二个标签页
	 * 
	 * @param dbConn
	 * @param request
	 * @param map
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public String queryOfficeProductsJsonLogic1(Connection dbConn, Map request, Map map, T9Person person) throws Exception {
		String proName = (String) map.get("proName");
		String proDesc = (String) map.get("proDesc");
		String proCode = (String) map.get("proCode");
		String officeDepository = (String) map.get("officeDepository");
		String officeProtype = (String) map.get("officeProtype");
		String conditionStr = "";
		String sql = "";
		try {
			if (!T9Utility.isNullorEmpty(proName)) {
				conditionStr = " and a.PRO_NAME like '%" + T9DBUtility.escapeLike(proName) + "%'";
			}
			if (!T9Utility.isNullorEmpty(proDesc)) {
				conditionStr += " and a.PRO_DESC like '%" + T9DBUtility.escapeLike(proDesc) + "%'";
			}
			if (!T9Utility.isNullorEmpty(proCode)) {
				conditionStr += " and a.PRO_CODE like '%" + proCode + "%'";
			}
			if (!T9Utility.isNullorEmpty(officeDepository) && !officeDepository.equals("-1")) {
				conditionStr += " and a.OFFICE_PROTYPE in (" + officeDepository + ")";
			}
			if (!T9Utility.isNullorEmpty(officeProtype) && !officeProtype.equals("-1")) {
				conditionStr += " and a.OFFICE_PROTYPE in (" + officeProtype + ")";
			}
			sql = "select a.SEQ_ID" + ", a.PRO_NAME" + ",b.TYPE_NAME" + ",a.PRO_UNIT" + ",a.PRO_SUPPLIER" + ",a.PRO_LOWSTOCK" + ",a.PRO_STOCK"
					+ ",a.PRO_CREATOR" + ",c.MANAGER" + ",c.OFFICE_TYPE_ID" + ",c.SEQ_ID as depoId" + ",b.SEQ_ID as typeId" + ",b.TYPE_DEPOSITORY"
					+ " from office_products a,office_type b,office_depository c where a.office_protype = b.seq_id and b.type_depository=c.seq_id" + ""
					+ conditionStr + " ORDER BY a.PRO_NAME ASC";
			T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
			T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
//			System.out.println(pageDataList.toJson());
			return pageDataList.toJson();
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 办公用品导出 第二个标签页
	 * 
	 * @param dbConn
	 * @param request
	 * @param map
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public ArrayList<T9DbRecord> queryOfficeProductsExport(Connection dbConn, Map request, Map map, T9Person person) throws Exception {
		Statement stmt = null;
		ResultSet rs = null;
		String proName = (String) map.get("proName");
		String proDesc = (String) map.get("proDesc");
		String proCode = (String) map.get("proCode");
		String officeDepository = (String) map.get("officeDepository");
		String officeProtype = (String) map.get("officeProtype");
		String conditionStr = "";
		String sql = "";
		try {
			stmt = dbConn.createStatement();
			if (!T9Utility.isNullorEmpty(proName)) {
				conditionStr = " and a.PRO_NAME like '%" + T9DBUtility.escapeLike(proName) + "%'";
			}
			if (!T9Utility.isNullorEmpty(proDesc)) {
				conditionStr += " and a.PRO_DESC like '%" + T9DBUtility.escapeLike(proDesc) + "%'";
			}
			if (!T9Utility.isNullorEmpty(proCode)) {
				conditionStr += " and a.PRO_CODE like '%" + proCode + "%'";
			}
			if (!T9Utility.isNullorEmpty(officeDepository) && !officeDepository.equals("-1")) {
				conditionStr += " and a.OFFICE_PROTYPE in (" + officeDepository + ")";
			}
			if (!T9Utility.isNullorEmpty(officeProtype) && !officeProtype.equals("-1")) {
				conditionStr += " and a.OFFICE_PROTYPE in (" + officeProtype + ")";
			}
			sql = "select a.SEQ_ID" + ", a.PRO_NAME" + ",b.TYPE_NAME" + ",a.PRO_CODE" + ",a.PRO_PRICE" + ",a.PRO_DESC" + ",a.PRO_UNIT" + ",a.PRO_SUPPLIER"
					+ ",a.PRO_LOWSTOCK" + ",a.PRO_MAXSTOCK" + ",a.PRO_STOCK" + ",a.PRO_CREATOR" + ",c.MANAGER" + ",a.PRO_MANAGER" + ",a.PRO_AUDITER"
					+ ",c.OFFICE_TYPE_ID" + ",c.SEQ_ID as depoId" + ",b.SEQ_ID as typeId" + ",b.TYPE_DEPOSITORY"
					+ " from office_products a,office_type b,office_depository c where a.office_protype = b.seq_id and b.type_depository=c.seq_id" + ""
					+ conditionStr + " ORDER BY a.PRO_NAME ASC";
			rs = stmt.executeQuery(sql);
			ArrayList<T9DbRecord> dbl = new ArrayList<T9DbRecord>();
			boolean isHaveFlag = false;
			while (rs.next()) {
				String proNames = rs.getString("PRO_NAME");
				String typeName = rs.getString("TYPE_NAME");
				String proCodes = rs.getString("PRO_CODE");
				String proPrice = rs.getString("PRO_PRICE");
				String proDescs = rs.getString("PRO_DESC");
				String proUnit = rs.getString("PRO_UNIT");
				String proSupplier = rs.getString("PRO_SUPPLIER");
				String proLowstock = rs.getString("PRO_LOWSTOCK");
				String proMaxstock = rs.getString("PRO_MAXSTOCK");
				String proStock = rs.getString("PRO_STOCK");
				String proCreator = rs.getString("PRO_CREATOR");
				if (!T9Utility.isNullorEmpty(proCreator)) {
					proCreator = getUserNameLogic(dbConn, proCreator);
				}
				String proManager = rs.getString("PRO_MANAGER");
				if (!T9Utility.isNullorEmpty(proManager)) {
					proManager = getUserNameLogic(dbConn, proManager);
				}
				String proAuditer = rs.getString("PRO_AUDITER");
				if (!T9Utility.isNullorEmpty(proAuditer)) {
					proAuditer = getUserNameLogic(dbConn, proAuditer);
				}
				T9DbRecord dbr = new T9DbRecord();
				dbr.addField("办公用品名", proNames);
				dbr.addField("办公用品类别", typeName);
				dbr.addField("编码", proCodes);
				dbr.addField("单价", proPrice);
				dbr.addField("办公用品描述", proDescs);
				dbr.addField("计量单位", proUnit);
				dbr.addField("供应商", proSupplier);
				dbr.addField("最低警戒库存", proLowstock);
				dbr.addField("最高警戒库存", proMaxstock);
				dbr.addField("当前库存", proStock);
				dbr.addField("创建人", proCreator);
				dbr.addField("登记权限(用户)", proManager);
				dbr.addField("审批权限", proAuditer);
				dbl.add(dbr);
				isHaveFlag = true;
			}
			if (!isHaveFlag) {
				T9DbRecord dbr = new T9DbRecord();
				dbr.addField("办公用品名", "");
				dbr.addField("办公用品类别", "");
				dbr.addField("编码", "");
				dbr.addField("单价", "");
				dbr.addField("办公用品描述", "");
				dbr.addField("计量单位", "");
				dbr.addField("供应商", "");
				dbr.addField("最低警戒库存", "");
				dbr.addField("最高警戒库存", "");
				dbr.addField("当前库存", "");
				dbr.addField("创建人", "");
				dbr.addField("登记权限(用户)", "");
				dbr.addField("审批权限", "");
				dbl.add(dbr);
			}
			return dbl;
		} catch (Exception ex) {
			throw ex;
		} finally {
			T9DBUtility.close(stmt, rs, null);
		}
	}

	/**
	 * 获取单位员工用户名称
	 * 
	 * @param conn
	 * @param userIdStr
	 * @return
	 * @throws Exception
	 */
	public String getUserNameLogic(Connection conn, String userIdStr) throws Exception {
		if (T9Utility.isNullorEmpty(userIdStr)) {
			userIdStr = "-1";
		}
		if (userIdStr.endsWith(",")) {
			userIdStr = userIdStr.substring(0, userIdStr.length() - 1);
		}
		String result = "";
		String sql = " select USER_NAME from PERSON where SEQ_ID IN (" + userIdStr + ")";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				String toId = rs.getString(1);
				if (!"".equals(result)) {
					result += ",";
				}
				result += toId;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(ps, rs, log);
		}
		return result;
	}

	/**
	 * 删除办公用品信息 先删除office_transhistory这个表对应的pro_id 在删除OFFICE_PRODUCTS表的seq_id
	 * office_transhistory 表的pro_id对应OFFICE_PRODUCTS表的seq_id
	 * 
	 * @param dbConn
	 * @param prodSeqIdStr
	 * @throws Exception
	 */
	public void deleteOfficeProductsLogic(Connection dbConn, String prodSeqIdStr) throws Exception {
		if (T9Utility.isNullorEmpty(prodSeqIdStr)) {
			prodSeqIdStr = "0";
		}
		if (prodSeqIdStr.endsWith(",")) {
			prodSeqIdStr = prodSeqIdStr.substring(0, prodSeqIdStr.length() - 1);
		}
		Statement stmt = null;
		ResultSet rs = null;
		String sql = "delete from office_transhistory where pro_id in(" + prodSeqIdStr + ")";
		try {
			stmt = dbConn.createStatement();
			stmt.executeUpdate(sql);
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(stmt, rs, log);
		}
	}

	/**
	 * 删除办公用品信息 先删除office_transhistory这个表对应的pro_id 在删除OFFICE_PRODUCTS表的seq_id
	 * office_transhistory 表的pro_id对应OFFICE_PRODUCTS表的seq_id
	 * 
	 * @param dbConn
	 * @param prodSeqIdStr
	 * @throws Exception
	 */
	public void delOfficeProductsByIdLogic(Connection dbConn, String proSeqIdStr) throws Exception {
		if (T9Utility.isNullorEmpty(proSeqIdStr)) {
			proSeqIdStr = "0";
		}
		if (proSeqIdStr.endsWith(",")) {
			proSeqIdStr = proSeqIdStr.substring(0, proSeqIdStr.length() - 1);
		}
		Statement stmt = null;
		ResultSet rs = null;
		String sql = "delete from OFFICE_PRODUCTS where SEQ_ID in(" + proSeqIdStr + ")";
		try {
			stmt = dbConn.createStatement();
			stmt.executeUpdate(sql);
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(stmt, rs, log);
		}
	}
}
