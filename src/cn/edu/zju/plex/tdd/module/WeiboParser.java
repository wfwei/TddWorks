package cn.edu.zju.plex.tdd.module;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import weibo4j.org.json.JSONObject;
import cn.edu.zju.plex.tdd.entity.ParsedStatus;
import cn.edu.zju.plex.tdd.entity.TvShows;
import cn.edu.zju.plex.tdd.tools.VideoThumbnail;

/**
 * weibo parser
 * 
 * @author plex
 */
public class WeiboParser {

	private static final Logger LOG = Logger.getLogger(WeiboParser.class);

	private static final Pattern WeiboTopicPatt = Pattern
			.compile("[#【](.*?)[#】]");

	private static final Pattern WeiboUrlPatt = Pattern
			.compile("(http://t.cn/[0-9a-zA-Z]+)");
	private static final Pattern AtPatt = Pattern.compile("(@[^\\s】.。，]+)");

	private String extractTopic(String content) {
		Matcher mat = WeiboTopicPatt.matcher(content);
		StringBuffer topics = new StringBuffer();
		while (mat.find()) {
			topics.append(mat.group(1) + ";");
		}
		return topics.toString();
	}

	public void parse(ParsedStatus status) {
		LOG.info("start parsing weibo status:" + status.getId());

		String wtext = status.getText();
		String content = wtext;
		StringBuffer sb;

		// extract & set topic
		status.setTopic(extractTopic(content));

		// extract & set urls
		Matcher mat = WeiboUrlPatt.matcher(content);
		sb = new StringBuffer();
		StringBuffer urls = new StringBuffer();
		while (mat.find()) {
			urls.append(mat.group() + ";");
			mat.appendReplacement(sb, "");
		}
		mat.appendTail(sb);
		content = sb.toString();
		status.setUrl(urls.toString());

		// check if url is video
		if (status.getUrl() != null && status.getUrl().length() > 0) {
			String[] us = status.getUrl().split(";");
			for (String u : us) {
				// TODO 封装一个微博操作类
				weibo4j.ShortUrl su = new weibo4j.ShortUrl();
				su.client.setToken("2.00l9nr_DfUKrWDf655d3279arZgVvD");
				try {
					JSONObject jo = su.shortToLongUrl(u);
					String vurl = jo.getJSONArray("urls").getJSONObject(0)
							.getString("url_long");
					// TODO how to speed up?
					if (vurl.indexOf("tudou.com") != -1
							|| vurl.indexOf("56.com") != -1
							|| vurl.indexOf("v.youku.com") != -1
							|| vurl.indexOf("video.sina.com") != -1
							|| vurl.indexOf("tv.letv.com") != -1
							|| vurl.indexOf("v.ku6.com") != -1
							|| vurl.indexOf("tv.sohu.com") != -1
							|| vurl.indexOf("v.163.com") != -1
							|| vurl.indexOf("v.ifeng.com") != -1
							|| vurl.indexOf("v.qq.com") != -1
							|| vurl.indexOf("iqiyi.com") != -1
							|| vurl.indexOf("6.cn") != -1) {
						status.setVideo(vurl + ","
								+ VideoThumbnail.getVideoThumbnail(vurl));

						break;
					}
				} catch (Exception e) {
					LOG.warn(e);
				} finally {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

		// extract & set ats
		mat = AtPatt.matcher(content);
		sb = new StringBuffer();
		StringBuffer ats = new StringBuffer();
		while (mat.find()) {
			ats.append(mat.group(1) + ";");
			mat.appendReplacement(sb, "");
		}
		mat.appendTail(sb);
		content = sb.toString();
		status.setAt_unames(ats.toString());

		// set content
		status.setContent(content);

		// parse tvids
		TvShows tvShow = MeijuTvAnalyzer.guessTv(content + ats.toString());
		status.setTvShow(tvShow);

		// set status
		status.setStatus(ParsedStatus.ST_FINISHED);
	}

}
