package cn.edu.zju.plex.tdd.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.zju.plex.tdd.entity.RssFeed;
import cn.edu.zju.plex.tdd.entity.RssNews;

/**
 * TDD的数据库操作类.
 * 
 * @author WangFengwei
 */
public final class DB4Tdd {

	/** 数据库链接. */
	private static Connection con = MySqlDB.getCon();

	/** 日志. */
	private static final Logger LOG = Logger.getLogger(DB4Tdd.class);

	/**
	 */
	public static synchronized List<RssNews> getRssNewsToParse(int count) {
		String sql = "select id, title, link, feed, page from rss_news where status = "
				+ RssNews.ST_READY + " limit 0," + count;
		List<RssNews> res = new ArrayList<RssNews>();
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				RssNews rssnews = new RssNews();
				rssnews.setId(rs.getLong(1));
				rssnews.setTitle(rs.getString(2));
				rssnews.setLink(rs.getString(3));
				rssnews.setFeed(rs.getLong(4));
				rssnews.setPage(rs.getString(5));
				res.add(rssnews);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * 从rssnews表中得到要索引的rssnews条目. TODO table has changed!!!
	 * 
	 * @return 成功则返回得到的rssnews(id, title, description, summary, status)，失败返回null
	 */
	public static synchronized List<RssNews> getItemToIndex(int startIdx,
			int count) {
		String sql = String
				.format("select id, title, summary, status, description from rssnews where status=%d order by id limit %d,%d",
						RssNews.ST_FINISHED, startIdx, count);
		List<RssNews> res = new ArrayList<RssNews>();
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				RssNews ritem = new RssNews();
				ritem.setId(rs.getLong(1));
				ritem.setTitle(rs.getString(2));
				ritem.setDescription(rs.getString(3).replaceAll("<[^>]+>", "")
						.replaceAll("[\\s\\t\\r\\n]+", ""));
				ritem.setSummary(rs.getString(4).replaceAll("<[^>]+>", "")
						.replaceAll("[\\s\\t\\r\\n]+", ""));
				ritem.setStatus((int) rs.getLong(5));
				res.add(ritem);
			}
		} catch (SQLException e) {
			LOG.warn(e.toString());
		}
		return res;
	}

	/**
	 * 替换不合法的字符，将'''替换成'’'.
	 * 
	 * @param input
	 *            输入字符串
	 * @return output 替换后的字符串
	 */
	private static String rmInvalidChar(final String input) {
		String output = null;
		if (input != null) {
			output = input.trim().replaceAll("'", "’");
		}
		return output;
	}

	/**
	 * @return
	 */
	public static ArrayList<RssFeed> getRssFeedList() {
		String sql = "select id, title, feed, link, count, first_update, last_update from rss_feeds";
		ArrayList<RssFeed> res = new ArrayList<RssFeed>();
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				RssFeed rf = new RssFeed();
				rf.setId(rs.getLong(1));
				rf.setTitle(rs.getString(2));
				rf.setFeed(rs.getString(3));
				rf.setLink(rs.getString(4));
				rf.setCount(rs.getInt(5));
				rf.setFirstUpdate(rs.getDate(6));
				rf.setLastUpdate(rs.getDate(7));
				res.add(rf);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			LOG.warn(e.toString());
		}
		return res;
	}

	/**
	 * 插入rssnews，目前直插入了rss和news部分字段
	 * <p>
	 * (title, link, category, description, pubDate, feed, page, status)
	 * <p>
	 * TODO insert sql will be too long???
	 */
	public static void insertRssNews(RssNews rssnews) {

		String sql = String
				.format("insert into rss_news(title, link, category, description, pubDate, feed, page, status) values('%s', '%s', '%s', '%s', '%s', %d, '%s', %d);",
						rmInvalidChar(rssnews.getTitle()),
						rmInvalidChar(rssnews.getLink()), rmInvalidChar(rssnews
								.getCategory()), rmInvalidChar(rssnews
								.getDescription()), new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss").format(rssnews
								.getPubDate()), rssnews.getFeed(),
						rmInvalidChar(rssnews.getPage()), rssnews.getStatus());
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			// ignore duplicated entry warning
			if(e.getMessage().toLowerCase().contains("duplicated")){
				e.printStackTrace();
				LOG.warn("sql is:\t" + sql);
			}
		} 
	}

	/**
	 * update RssNews(content, images, videos, words, status)
	 * <p>
	 * 
	 * @param rssNews
	 */
	public static void updateParsedData(RssNews rssNews) {

		String sql = String
				.format("update rss_news set content='%s', images='%s', videos='%s', words='%s', status=%d where id=%d",
						rssNews.getContent(), rssNews.getImages(),
						rssNews.getVideos(), rssNews.getWords(),
						rssNews.getStatus(), rssNews.getId());
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static synchronized List<RssNews> getRssNewsForTest(int feed, int count) {
		String sql = "select id, title, link, feed, page from rss_news where feed = "
				+ feed + " limit 0," + count;
		List<RssNews> res = new ArrayList<RssNews>();
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				RssNews rssnews = new RssNews();
				rssnews.setId(rs.getLong(1));
				rssnews.setTitle(rs.getString(2));
				rssnews.setLink(rs.getString(3));
				rssnews.setFeed(rs.getLong(4));
				rssnews.setPage(rs.getString(5));
				res.add(rssnews);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

}
