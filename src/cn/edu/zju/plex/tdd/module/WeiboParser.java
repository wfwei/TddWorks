package cn.edu.zju.plex.tdd.module;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import weibo4j.model.WeiboException;
import weibo4j.org.json.JSONObject;
import cn.edu.zju.plex.tdd.entity.ParsedStatus;
import cn.edu.zju.plex.tdd.entity.TvShows;
import cn.edu.zju.plex.tdd.tools.UrlUtil;
import cn.edu.zju.plex.tdd.tools.VideoUtil;

/**
 * weibo parser
 * 
 * @author plex
 */
public class WeiboParser {

	private static final Logger LOG = Logger.getLogger(WeiboParser.class);

	private static final Pattern WeiboTopicPatt = Pattern.compile("#(.*?)#");
	private static final Pattern WeiboUrlPatt = Pattern
			.compile("(http://t.cn/[^\\s]+)");
	private static final Pattern AtPatt = Pattern.compile("(@[^\\s]+)");

	public void parse(ParsedStatus status) {
		LOG.info("start parsing weibo status:" + status.getId());

		String wtext = status.getText();
		String content = wtext;
		StringBuffer sb;

		// extract & set topic
		Matcher mat = WeiboTopicPatt.matcher(content);
		StringBuffer topics = new StringBuffer();
		while (mat.find()) {
			topics.append(mat.group(1) + ";");
		}
		status.setTopic(topics.toString());

		// extract & set urls
		mat = WeiboUrlPatt.matcher(content);
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
			StringBuffer videoUrls = new StringBuffer();
			String[] us = status.getUrl().split(";");
			for (String u : us) {
				weibo4j.ShortUrl su = new weibo4j.ShortUrl();
				su.client.setToken("");
				try {
					JSONObject jo = su.shortToLongUrl(u);
					String vurl = jo.getJSONArray("urls").getJSONObject(0)
							.getString("url_long");
					if (UrlUtil.isVideo(vurl))
						videoUrls.append(vurl);
				} catch (Exception e) {
					e.printStackTrace();
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
