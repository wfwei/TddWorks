package cn.edu.zju.plex.tdd.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.edu.zju.plex.tdd.entity.TvShows;

public class TvShowsDao extends BaseDao {

	/**
	 * TODO 关于美剧名称的逻辑代码不应该出现在这里
	 * 
	 * @return
	 */
	public static HashMap<String, TvShows> getMeijuTvs() {
		HashMap<String, TvShows> tvs = new HashMap<String, TvShows>();
		String sql = "select tvdbid, cname, ename, aka_original, aka from tvshows "
				+ "where tvdbid is not NUll";
		Connection con = CM.getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			String aka, id;
			while (rs.next()) {
				id = rs.getString(1);
				String cname = rs.getString(2);
				String ename = rs.getString(3);
				TvShows tvShow = new TvShows(id, cname, ename);
				for (int i = 2; i < 6; i++) {
					aka = rs.getString(i);
					if (aka == null || aka.length() < 1)
						continue;
					aka = aka.toLowerCase().replaceAll("[:：，-；;]", ",");

					for (String name : aka.split(",")) {
						name = name.trim();
						if (tvs.get(name) != null && tvs.get(name) != tvShow) {
							LOG.info(tvs.get(name) + " and " + id
									+ " conflicted by name:" + name);
						} else {
							if (name.length() > 1) {
								if (name.contains("("))
									name = name.substring(0, name.indexOf('('));
								if (name.contains("（"))
									name = name.substring(0, name.indexOf('（'));
								tvs.put(name, tvShow);
							}
						}
					}
				}
			}
			tvs.remove("");
		} catch (SQLException e) {
			e.printStackTrace();
			tvs = null;
			LOG.warn("fail to get target weibo users");
			LOG.error(e.getMessage());
			LOG.debug("sql is:\t" + sql);
		} finally {
			release(con, stmt, rs);
		}
		return tvs;

	}

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
			LOG.warn("fail to get target weibo users");
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
