package cn.edu.zju.plex.tdd.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import cn.edu.zju.plex.tdd.entity.RssFeed;

public class RssFeedsDao extends BaseDao {

	public static void updateLastUpdateTime(RssFeed rf) {
		String sql = String.format(
				"update rss_feeds set last_update='%s' where id=%d",
				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rf
						.getLastUpdate()), rf.getId());
		Connection con = CM.getConnection();
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
			LOG.debug("sql:\t" + sql);
		} finally {
			release(con, stmt, null);
		}
	}
	
	public static ArrayList<RssFeed> listAll() {
		String sql = "select id, title, feed, link, count, first_update, last_update from rss_feeds";
		ArrayList<RssFeed> res = new ArrayList<RssFeed>();
		Connection con = CM.getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				RssFeed rf = new RssFeed();
				rf.setId(rs.getLong(1));
				rf.setTitle(rs.getString(2));
				rf.setFeed(rs.getString(3));
				rf.setLink(rs.getString(4));
				rf.setCount(rs.getInt(5));
				rf.setFirstUpdate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.parse(rs.getString(6)));
				rf.setLastUpdate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.parse(rs.getString(7)));
				res.add(rf);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.warn(e.getMessage());
			LOG.debug("sql is:\t" + sql);
		} finally {
			release(con, stmt, rs);
		}
		return res;
	}

}
