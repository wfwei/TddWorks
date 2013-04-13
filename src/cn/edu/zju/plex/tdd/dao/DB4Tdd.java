package cn.edu.zju.plex.tdd.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;

import weibo4j.model.Status;

import cn.edu.zju.plex.tdd.entity.ParsedStatus;
import cn.edu.zju.plex.tdd.entity.RssFeed;
import cn.edu.zju.plex.tdd.entity.RssNews;
import cn.edu.zju.plex.tdd.tools.CharsetTool;

/**
 * TDD的数据库操作类. TODO 以表分割该类
 * 
 * @author WangFengwei
 */
public final class DB4Tdd {

	/** 数据库链接. */
	// TODO 需要加锁么？ createStatement操作能否并发
	private static Connection con = MySqlDB.getCon();

	/** 日志. */
	// TODO 需要加锁么？
	private static final Logger LOG = Logger.getLogger(DB4Tdd.class);

	/**
	 */
	public static List<RssNews> getRssNewsToParse(int count) {
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
	public static List<RssNews> getItemToIndex(int startIdx, int count) {
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
			if (!e.getMessage().toLowerCase().contains("duplicated")) {
				e.printStackTrace();
				LOG.warn(e.getMessage());
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
	public static void updateParsedRssNews(RssNews rssNews) {

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

	public static List<RssNews> getRssNewsForTest(int feed, int count) {
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

	/**
	 * @deprecated 这是老本版的获取方法，现在只需要access token
	 */
	public static String[] getWeiboOAuth(String wuid) {
		String sql = "select accesstoken, accesstokensecret from weibo_auth where wuid like '"
				+ wuid + "'";
		String[] accessTokenSecret = new String[2];
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				accessTokenSecret[0] = rs.getString(1);
				accessTokenSecret[1] = rs.getString(2);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			accessTokenSecret = null;
			LOG.warn("fail to get weibo auth:" + wuid);
			LOG.error(e.getMessage());
			LOG.warn("sql is:\t" + sql);
		}
		return accessTokenSecret;
	}

	/**
	 * 获取目标用户的user iｄ和上次上次收录的weibo id
	 */
	public static HashMap<String, String> getWeiboTargets() {
		HashMap<String, String> targets = new HashMap<String, String>();
		String sql = "select wuid, lastwid from meiju_wusers";
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				targets.put(rs.getString(1), rs.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			targets = null;
			LOG.warn("fail to get target weibo users");
			LOG.error(e.getMessage());
			LOG.warn("sql is:\t" + sql);
		}
		return targets;
	}

	public static void updateWeiboTargets(String wuid, String lastUpdateWeibo) {
		String sql = "update meiju_wusers set lastwid = '" + lastUpdateWeibo
				+ "' where wuid like '" + wuid + "'";
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			LOG.warn("fail to update target weibo users");
			LOG.error(e.getMessage());
			LOG.warn("sql is:\t" + sql);
		}
	}

	/**
	 * 插入新微薄(wid, wmid, wsmallimages, wmiddleimage, woriginalimage, wgeo,
	 * wuname, wuid, wtime, wtext, wreply2wid, wreply2uid, wrepost_count,
	 * wcomment_count, wattitudes_count) TODO java.sql.SQLException: Incorrect
	 * string value: '\xF0\x9F\x94\x83' for column 'wtext' at row 1
	 * 
	 * @param s
	 */
	public static void insertWeibo(Status s) {
		String sql = String
				.format("insert into meiju_weibo(wid, wmid, wsmallimage, wmiddleimage, "
						+ "woriginalimage, wgeo, wuname, wuid, wtime, wtext, wreply2wid, wreply2uid, "
						+ "wrepost_count, wcomment_count, wattitudes_count) values('%s', '%s', '%s', '%s', '%s', '%s',"
						+ " '%s', '%s', '%s', '%s', '%s', '%s', %d, %d, %d)", s
						.getId(), s.getMid(), s.getThumbnailPic(), s
						.getBmiddlePic(), s.getOriginalPic(), s.getGeo(), s
						.getUser().getName().replace('\'', '’'), s.getUser()
						.getId(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(s.getCreatedAt()),
						s.getText().replace('\'', '’'), s
								.getInReplyToStatusId(),
						s.getInReplyToUserId(), s.getRepostsCount(), s
								.getCommentsCount(), 0);
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			// ignore duplicated entry warning
			if (!e.getMessage().toLowerCase().contains("duplicated")) {
				e.printStackTrace();
				LOG.warn(e.getMessage());
				LOG.warn("sql is:\t" + sql);
			}
		}

	}

	public static List<ParsedStatus> getWeiboToParse(int count) {
		String sql = "select wid, wmid, wsmallimage, wmiddleimage, "
				+ "woriginalimage, wgeo, wuname, wuid, wtime, wtext, "
				+ "wreply2wid, wreply2uid, wrepost_count, wcomment_count "
				+ "from meiju_weibo where status = " + RssNews.ST_READY
				+ " limit 0," + count;
		List<ParsedStatus> res = new ArrayList<ParsedStatus>();
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				ParsedStatus ps = new ParsedStatus();
				ps.setId(rs.getString(1));
				ps.setMid(rs.getString(2));
				ps.setThumbnailPic(rs.getString(3));
				ps.setBmiddlePic(rs.getString(4));
				ps.setOriginalPic(rs.getString(5));
				ps.setGeo(rs.getString(6));
				ps.setUname(rs.getString(7));
				ps.setUid(rs.getString(8));
				ps.setCreatedAt(rs.getDate(9));
				ps.setText(rs.getString(10));
				ps.setInReplyToStatusId(rs.getLong(11));
				ps.setInReplyToUserId(rs.getLong(12));
				ps.setRepostsCount(rs.getInt(13));
				ps.setCommentsCount(rs.getInt(14));

				ps.setStatus(RssNews.ST_READY);
				res.add(ps);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			LOG.warn("error occured in getWeiboToParse: " + e.getMessage());
			LOG.warn(sql);
		}
		return res;
	}

	public static void updateParsedStatus(ParsedStatus st) {
		String sql = String.format("update meiju_weibo set "
				+ "content='%s', video='%s', url='%s', topic='%s', words='%s', meiju_ids='%s', "
				+ "at_unames='%s', status=%d where wid='%s'", st.getContent(), st.getVideo(), st.getUrl()
				, st.getTopic(), st.getWords(), st.getMeiju_ids(), st.getUname(), st.getStatus(), st.getId());
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			LOG.warn("error occured in updateParsedStatus:"+e.getMessage());
			LOG.warn(sql);
		}
	}
	
	public static HashMap<String, String> getMeijuTvs(){
		HashMap<String, String> tvs = new HashMap<String, String>();
		String sql = "select sid, cname, ename, aka_original, aka from tvshows";
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			String aka, id;
			while (rs.next()) {
				id = rs.getString(1);
				// TODO 权利的游戏 没有。。。
				if(id.equals("24493")){
					LOG.info("");
				}
				String cname = rs.getString(2);
				tvs.put(cname, id);
				for(int i=3; i<6; i++){
					aka = rs.getString(i);
					if(CharsetTool.containChinese(aka)){
						aka = aka.replaceAll("[\\s:：，-；;]", ",");
					}
					for(String name:aka.split(",")){
						if(tvs.get(name)!=null && tvs.get(name)!=id){
							LOG.info(tvs.get(name)+" and "+id+" conflicted by name:"+name);
						}else{
							name = name.trim();
							if(name.length()>1)
								tvs.put(name, id);
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			tvs = null;
			LOG.warn("fail to get target weibo users");
			LOG.error(e.getMessage());
			LOG.warn("sql is:\t" + sql);
		}
		return tvs;
		
	}

}
