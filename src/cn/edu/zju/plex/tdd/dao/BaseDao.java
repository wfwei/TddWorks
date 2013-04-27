package cn.edu.zju.plex.tdd.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;

/**
 * @author WangFengwei
 */
public class BaseDao {

	protected static final ConnectionManager CM = ConnectionManager
			.getInstance();

	protected static final Logger LOG = Logger.getLogger(BaseDao.class);

	/**
	 * 替换不合法的字符，将'''替换成'’'.
	 */
	protected static String rmInvalidChar(String input) {
		String output = null;
		if (input != null) {
			output = input.trim().replaceAll("'", "’");
		}
		return output;
	}
	
	protected static void release(Connection con, Statement stmt, ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (Exception e) {
			}
		}

		if (stmt != null) {
			try {
				stmt.close();
			} catch (Exception e) {
			}
		}
		if (con != null) {
			try {
				con.close();
			} catch (Exception e) {
			}
		}
	}

}
