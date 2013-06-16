package cn.edu.zju.plex.tdd.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import cn.edu.zju.plex.tdd.entity.TvShows;

public class TvShowsDao extends BaseDao {

	public static List<TvShows> getTvShowList() {
		String sql = "select tvdbid, cname, ename, doubanid, aka_original, aka from tvshows";
		List<TvShows> res = new ArrayList<TvShows>();
		Connection con = CM.getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String tvdbid = rs.getString(1);
				String cname = rs.getString(2);
				String ename = rs.getString(3);
				TvShows tvShow = new TvShows(tvdbid, cname, ename);
				tvShow.setDoubanid(rs.getString(4));
				tvShow.setAka(rs.getString(5));
				tvShow.setAka_original(rs.getString(6));
				res.add(tvShow);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
			LOG.debug("sql is:\t" + sql);
		} finally {
			release(con, stmt, rs);
		}
		return res;
	}

	public static void updateDoubanId(String tvdbid, String doubanid) {
		String sql = String.format(
				"update tvshows set doubanid='%s' where tvdbid='%s';",
				doubanid, tvdbid);
		Connection con = CM.getConnection();
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
			LOG.debug("sql is:\t" + sql);
		} finally {
			release(con, stmt, null);
		}

	}

	public static void updateTvShowAka(String tvdbid, String aka) {
		String sql = String.format(
				"update tvshows set aka='%s' where tvdbid='%s';", aka, tvdbid);
		Connection con = CM.getConnection();
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			LOG.warn(e.getMessage());
			LOG.debug("sql is:\t" + sql);
		} finally {
			release(con, stmt, null);
		}

	}

}
