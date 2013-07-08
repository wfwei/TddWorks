package cn.edu.zju.plex.tdd.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TvepisodesDao extends BaseDao {

	public static void updateRssNewsId(long rssNewsId, String tvdbid,
			int season, int episode) {
		// update tvepisodes set rss_news_id=72 where tvdbsid='110381' and
		// cast(season as signed)=4 and cast(episode as signed)=13;
		String sql = String
				.format("update tvepisodes set rss_news_id = %d where tvdbsid='%s' "
						+ "and cast(season as signed)=%d and cast(episode as signed)=%d;",
						rssNewsId, tvdbid, season, episode);

		String select = String
				.format("select * from  tvepisodes where tvdbsid='%s' "
						+ "and cast(season as signed)=%d and cast(episode as signed)=%d;",
						tvdbid, season, episode);

		Connection con = CM.getConnection();
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(select);
			if (rs.next()) {
				stmt.executeUpdate(sql);
				// System.out.println("find:" + select.substring(31));
			} else {
				// System.out.println("not find:" + select.substring(31));
			}
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
			LOG.debug("sql is:\t" + sql);
		} finally {
			release(con, stmt, null);
		}

	}

}
