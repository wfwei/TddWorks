package cn.edu.zju.plex.tdd.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.edu.zju.plex.tdd.entity.RssNews;
import cn.edu.zju.plex.tdd.entity.TvShows;

public class RssNewsDao extends BaseDao {

	/**
	 * 插入rssnews，目前直插入了rss和news部分字段
	 * <p>
	 * (title, link, category, description, pubDate, feed, page, status)
	 * <p>
	 * TODO insert sql will be too long???
	 */
	public static void insert(RssNews rssnews) {
		Connection con = CM.getConnection();
		Statement stmt = null;

		String sql = String
				.format("insert into rss_news(title, link, author, category, description, pubDate, feed, page, split_id, status) values('%s', '%s', '%s', '%s', '%s', '%s', %d, '%s', %d, %d);",
						rmInvalidChar(rssnews.getTitle()),
						rmInvalidChar(rssnews.getLink()), rmInvalidChar(rssnews
								.getAuthor()), rmInvalidChar(rssnews
								.getCategory()), rmInvalidChar(rssnews
								.getDescription()), new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss").format(rssnews
								.getPubDate()), rssnews.getFeed(),
						rmInvalidChar(rssnews.getPage()), rssnews.getSplitId(),
						rssnews.getStatus());
		try {
			stmt = con.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			// ignore duplicated entry warning
			if (!e.getMessage().contains("Duplicate")) {
				e.printStackTrace();
				LOG.warn(e.getMessage());
				LOG.debug("sql is:\t" + sql);
			} else
				LOG.info("duplicated entry:" + rssnews);
		} finally {
			release(con, stmt, null);
		}
	}

	/**
	 * update RssNews(content, images, videos, words, status)
	 * <p>
	 * 
	 * @param rssNews
	 */
	public static void updateParsedRes(RssNews rssNews) {
		Connection con = CM.getConnection();
		Statement stmt = null;
		String sql = String
				.format("update rss_news set page='', content='%s', images='%s', videos='%s', meiju_id='%s', meiju_cname='%s', meiju_ename='%s', status=%d where id=%d",
						rssNews.getContent().replace('\'', '’'), rssNews
								.getImages(),
						rssNews.getVideos().replace('\'', '’'), rssNews
								.getTvShows().getTvdbid(), rssNews.getTvShows()
								.getCname().replace('\'', '’'), rssNews
								.getTvShows().getEname().replace('\'', '’'),
						rssNews.getStatus(), rssNews.getId());
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

	/**
	 * @param count
	 *            一次获取的数量
	 * @param linkReg
	 *            link的正则表达式
	 * @return
	 */
	public static List<RssNews> getRssNewsToSplit(int count, String linkReg) {
		String sql = "select id, title, link, category, description,"
				+ " pubDate, feed, page, status from rss_news where link regexp '"
				+ linkReg + "' limit 0," + count;
		List<RssNews> res = new ArrayList<RssNews>();
		Connection con = CM.getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				RssNews rssnews = new RssNews();
				rssnews.setId(rs.getLong(1));
				rssnews.setTitle(rs.getString(2));
				rssnews.setLink(rs.getString(3));
				rssnews.setCategory(rs.getString(4));
				rssnews.setDescription(rs.getString(5));
				rssnews.setPubDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.parse(rs.getString(6)));
				rssnews.setFeed(rs.getLong(7));
				rssnews.setPage(rs.getString(8));
				rssnews.setStatus(rs.getInt(9));
				res.add(rssnews);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
			LOG.debug("sql:\t" + sql);
		} finally {
			release(con, stmt, rs);
		}
		return res;
	}

	public static List<RssNews> getRssNewsToParse(int count) {
		String sql = "select id, title, link, author, category, description, pubDate, feed, page, split_id, status from rss_news where status = "
				+ RssNews.ST_READY + " limit 0," + count;
		List<RssNews> res = new ArrayList<RssNews>();
		Connection con = CM.getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con = CM.getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				RssNews rssnews = new RssNews();
				rssnews.setId(rs.getLong(1));
				rssnews.setTitle(rs.getString(2));
				rssnews.setLink(rs.getString(3));
				rssnews.setAuthor(rs.getString(4));
				rssnews.setCategory(rs.getString(5));
				rssnews.setDescription(rs.getString(6));
				try {
					rssnews.setPubDate(new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss").parse(rs.getString(7)));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				rssnews.setFeed(rs.getLong(8));
				rssnews.setPage(rs.getString(9));
				rssnews.setSplitId(rs.getInt(10));
				rssnews.setStatus(rs.getInt(11));
				res.add(rssnews);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			LOG.warn(e.getMessage());
			LOG.debug("sql is:\t" + sql);
		} finally {
			release(con, stmt, rs);
		}
		return res;
	}

	public static List<RssNews> getRssNewsForTest(int feed, int count) {
		String sql = "select id, title, link, feed, page from rss_news where feed = "
				+ feed + " limit 0," + count;
		List<RssNews> res = new ArrayList<RssNews>();
		Connection con = CM.getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
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
			LOG.warn(e.getMessage());
			LOG.debug("sql is:\t" + sql);
		} finally {
			release(con, stmt, rs);
		}
		return res;
	}

	public static void delete(RssNews rssNews) {
		String sql = "delete from rss_news where id=" + rssNews.getId();
		Connection con = CM.getConnection();
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			LOG.warn("error occured in updateParsedStatus:" + e.getMessage());
			LOG.debug(sql);
		} finally {
			release(con, stmt, null);
		}
	}

	/**
	 * @param timeLen
	 * @return
	 */
	public static List<RssNews> getRssNewsToMerge(int timeLen) {
		String presql = "select pubDate from rss_news where delegate=0 order by pubDate limit 0, 1";
		String sql = "select id, title, link, category, description, pubDate, "
				+ "feed, words, meiju_id, meiju_cname, meiju_ename, delegate, status "
				+ "from rss_news where delegate=0 and pubDate<='%s' order by pubDate";
		List<RssNews> res = new ArrayList<RssNews>();
		Date startDate = null, endDate = null;
		Connection con = CM.getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(presql);
			if (rs.next()) {
				startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.parse(rs.getString(1));
				endDate = new Date(startDate.getTime() + timeLen);
			} else {
				LOG.warn("no more dates...");
				endDate = new Date();
			}
			sql = String
					.format(sql, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
							.format(endDate));
			rs = stmt.executeQuery(sql);

			while (rs.next()) {
				RssNews rssnews = new RssNews();
				rssnews.setId(rs.getLong(1));
				rssnews.setTitle(rs.getString(2));
				rssnews.setLink(rs.getString(3));
				rssnews.setCategory(rs.getString(4));
				rssnews.setDescription(rs.getString(5));
				rssnews.setPubDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.parse(rs.getString(6)));
				rssnews.setFeed(rs.getLong(7));
				rssnews.setWords(rs.getString(8));
				rssnews.setTvShows(new TvShows(rs.getString(9), rs
						.getString(10), rs.getString(11)));
				rssnews.setDelegate(rs.getLong(12));
				rssnews.setStatus(rs.getInt(13));
				res.add(rssnews);
			}
			Date realEndDate = res.get(res.size() - 1).getPubDate();
			if (realEndDate.getTime() - startDate.getTime() <= timeLen / 2 - 3600000)
				res.clear();
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
			LOG.debug("sql:\t" + sql);
		} finally {
			release(con, stmt, rs);
		}
		return res;
	}

	public static void updateDelegate(RssNews rssNews) {
		String sql = String.format(
				"update rss_news set delegate=%d where id=%d",
				rssNews.getDelegate(), rssNews.getId());
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

	// images_count = -1是还没有获取的，没有是0
	public static List<RssNews> getRssNewsToDownloadImages() {
		String sql = "select id, link, images from rss_news where image_count = -1 and status=2 limit 0, 100";
		List<RssNews> res = new ArrayList<RssNews>();
		Connection con = CM.getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				RssNews rssnews = new RssNews();
				rssnews.setId(rs.getLong(1));
				rssnews.setLink(rs.getString(2));
				rssnews.setImages(rs.getString(3));
				res.add(rssnews);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
			LOG.debug("sql:\t" + sql);
		} finally {
			release(con, stmt, rs);
		}
		return res;
	}

	public static void updateImageCountAndSize(long rssNewsId, int count,
			String sizeInfo) {
		String sql = "update rss_news set image_count = " + count
				+ ", image_sizes='" + sizeInfo + "' where id=" + rssNewsId;
		Connection con = CM.getConnection();
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
			LOG.debug("sql:\t" + sql);
		} finally {
			release(con, stmt, null);
		}

	}

	public static void updateSplitId(long rssNewsId, int splitId) {
		String sql = "update rss_news set split_id = " + splitId + " where id="
				+ rssNewsId;
		Connection con = CM.getConnection();
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
			LOG.debug("sql:\t" + sql);
		} finally {
			release(con, stmt, null);
		}
	}

	public static List<RssNews> getRssNewsToGuessTvShows(int count) {
		String sql = "select id, title, content from rss_news where meiju_id is NULL limit 0,"
				+ count;
		List<RssNews> res = new ArrayList<RssNews>();
		Connection con = CM.getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				RssNews rssnews = new RssNews();
				rssnews.setId(rs.getLong(1));
				rssnews.setTitle(rs.getString(2));
				rssnews.setContent(rs.getString(3));
				res.add(rssnews);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
			LOG.debug("sql:\t" + sql);
		} finally {
			release(con, stmt, rs);
		}
		return res;
	}

	public static void updateTvShows(RssNews rssNews) {

		String sql = String
				.format("update rss_news set meiju_id='%s', meiju_cname='%s', meiju_ename='%s' where id=%d",
						rssNews.getTvShows().getTvdbid(), rssNews.getTvShows()
								.getCname(), rssNews.getTvShows().getEname(),
						rssNews.getId());
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

	public static List<RssNews> getRssNewsToTrainClassifier(int offset,
			int count) {
		String sql = "select id, title, content, meiju_id, meiju_cname, "
				+ "meiju_ename from rss_news where meiju_id is not NULL and meiju_id != '' limit "
				+ offset + ", " + count;
		List<RssNews> res = new ArrayList<RssNews>();
		Connection con = CM.getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				RssNews rssnews = new RssNews();
				rssnews.setId(rs.getLong(1));
				rssnews.setTitle(rs.getString(2));
				rssnews.setContent(rs.getString(3));
				TvShows tvShows = new TvShows();
				tvShows.setTvdbid(rs.getString(4));
				tvShows.setCname(rs.getString(5));
				tvShows.setEname(rs.getString(6));
				rssnews.setTvShows(tvShows);
				res.add(rssnews);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			LOG.debug("sql:\t" + sql);
		} finally {
			release(con, stmt, rs);
		}
		return res;
	}

}
