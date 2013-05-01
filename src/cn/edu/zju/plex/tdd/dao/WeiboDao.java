package cn.edu.zju.plex.tdd.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import weibo4j.model.Status;

import cn.edu.zju.plex.tdd.entity.ParsedStatus;
import cn.edu.zju.plex.tdd.entity.RssNews;

public class WeiboDao extends BaseDao {

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
		Connection con = CM.getConnection();
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			// ignore duplicated entry warning
			if (!e.getMessage().contains("Duplicate")) {
				e.printStackTrace();
				LOG.warn(e.getMessage());
				LOG.debug("sql is:\t" + sql);
			} else
				LOG.debug("duplicated entry:" + s);
		} finally {
			release(con, stmt, null);
		}

	}

	/**
	 * 获取目标用户的user iｄ和上次上次收录的weibo id
	 */
	public static HashMap<String, String> getWeiboTargets() {
		HashMap<String, String> targets = new HashMap<String, String>();
		String sql = "select wuid, lastwid from meiju_wusers";
		Connection con = CM.getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				targets.put(rs.getString(1), rs.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			targets = null;
			LOG.warn("fail to get target weibo users");
			LOG.error(e.getMessage());
			LOG.warn("sql is:\t" + sql);
		} finally {
			release(con, stmt, rs);
		}
		return targets;
	}

	public static void updateWeiboTargets(String wuid, String lastUpdateWeibo) {
		String sql = "update meiju_wusers set lastwid = '" + lastUpdateWeibo
				+ "' where wuid like '" + wuid + "'";
		Connection con = CM.getConnection();
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			LOG.warn("fail to update target weibo users");
			LOG.error(e.getMessage());
			LOG.debug("sql is:\t" + sql);
		} finally {
			release(con, stmt, null);
		}
	}

	public static List<ParsedStatus> getWeiboToParse(int count) {
		String sql = "select wid, wmid, wsmallimage, wmiddleimage, "
				+ "woriginalimage, wgeo, wuname, wuid, wtime, wtext, "
				+ "wreply2wid, wreply2uid, wrepost_count, wcomment_count "
				+ "from meiju_weibo where status = " + RssNews.ST_READY
				+ "order by wtime desc limit 0," + count;
		List<ParsedStatus> res = new ArrayList<ParsedStatus>();
		Connection con = CM.getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
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
				ps.setCreatedAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.parse(rs.getString(9)));
				ps.setText(rs.getString(10));
				ps.setInReplyToStatusId(rs.getLong(11));
				ps.setInReplyToUserId(rs.getLong(12));
				ps.setRepostsCount(rs.getInt(13));
				ps.setCommentsCount(rs.getInt(14));

				ps.setStatus(RssNews.ST_READY);
				res.add(ps);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.warn("error occured in getWeiboToParse: " + e.getMessage());
			LOG.debug(sql);
		} finally {
			release(con, stmt, rs);
		}
		return res;
	}

	public static void updateParsedStatus(ParsedStatus st) {
		String sql = String
				.format("update meiju_weibo set "
						+ "content='%s', video='%s', url='%s', topic='%s', meiju_id='%s', meiju_cname='%s', meiju_ename='%s', "
						+ "at_unames='%s', status=%d where wid='%s'", st
						.getContent(), st.getVideo(), st.getUrl(), st
						.getTopic().replace('\'', '’'), st.getTvShow()
						.getTvdbid(),
						st.getTvShow().getCname().replace('\'', '’'), st
								.getTvShow().getEname().replace('\'', '’'), st
								.getUname().replace('\'', '’'), st.getStatus(),
						st.getId());
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

	public static List<ParsedStatus> getParsedStatusToDownloadImages() {
		String sql = "select wid, wsmallimage, wmiddleimage, woriginalimage from meiju_weibo where image_count=-1 and status=2 limit 0, 100";
		List<ParsedStatus> res = new ArrayList<ParsedStatus>();
		Connection con = CM.getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				ParsedStatus st = new ParsedStatus();
				st.setId(rs.getString(1));
				st.setThumbnailPic(rs.getString(2));
				st.setBmiddlePic(rs.getString(3));
				st.setOriginalPic(rs.getString(4));
				res.add(st);
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

	public static void updateParsedStatusImageCountAndSize(String statusId,
			int count, String imageSizes) {
		String sql = "update meiju_weibo set image_count = " + count
				+ ", image_sizes='" + imageSizes + "' where wid='" + statusId
				+ "'";
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

	public static List<ParsedStatus> getWeiboToUpdateVideos(int count) {
		String sql = "select wid, url from meiju_weibo where video is NULL and url!='' limit 0,"
				+ count;
		List<ParsedStatus> res = new ArrayList<ParsedStatus>();
		Connection con = CM.getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				ParsedStatus ps = new ParsedStatus();
				ps.setId(rs.getString(1));
				ps.setUrl(rs.getString(2));
				res.add(ps);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.warn("error occured in getWeiboToParse: " + e.getMessage());
			LOG.debug("sql:" + sql);
		} finally {
			release(con, stmt, rs);
		}
		return res;
	}

	public static void updateWeiboVideo(String wid, String video) {
		String sql = "update meiju_weibo set video='" + video + "' where wid='"
				+ wid + "'";
		Connection con = CM.getConnection();
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			LOG.warn("error occured in updateParsedStatus:" + e.getMessage());
			LOG.debug("sql:" + sql);
		} finally {
			release(con, stmt, null);
		}

	}

}
