package cn.edu.zju.plex.tdd.main;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import weibo4j.org.json.JSONObject;
import cn.edu.zju.plex.tdd.dao.RssNewsDao;
import cn.edu.zju.plex.tdd.dao.TvepisodesDao;
import cn.edu.zju.plex.tdd.dao.WeiboDao;
import cn.edu.zju.plex.tdd.entity.ParsedStatus;
import cn.edu.zju.plex.tdd.entity.RssNews;

public class OnceWork {

	private static final Logger LOG = Logger.getLogger(OnceWork.class);

	@SuppressWarnings("unused")
	private static void updateWeiboVideo() {

		weibo4j.ShortUrl su = new weibo4j.ShortUrl();
		su.client.setToken("2.00l9nr_DfUKrWDf655d3279arZgVvD");
		while (true) {
			List<ParsedStatus> sts = WeiboDao.getWeiboToUpdateVideos(100);
			if (sts.size() == 0)
				break;
			else {
				for (ParsedStatus ps : sts) {
					boolean found = false;
					LOG.info("-------------");
					for (String url : ps.getUrl().split(";")) {
						if (url.length() < 1)
							continue;
						try {
							LOG.info("short_url:" + url);
							JSONObject jo = su.shortToLongUrl(url.trim());
							String vurl = jo.getJSONArray("urls")
									.getJSONObject(0).getString("url_long");
							LOG.info("long_url:" + vurl);
							if (vurl.indexOf("tudou.com") != -1
									|| vurl.indexOf("video.sina.com") != -1
									|| vurl.indexOf("v.youku.com") != -1
									|| vurl.indexOf("v.ku6.com") != -1
									|| vurl.indexOf("56.com") != -1
									|| vurl.indexOf("6.cn") != -1) {
								WeiboDao.updateWeiboVideo(ps.getId(), vurl);
								found = true;
								break;
							}
						} catch (Exception e) {
							System.err.println(e);
						}
					}
					if (!found)
						WeiboDao.updateWeiboVideo(ps.getId(), "");
				}
			}

		}
	}

	// 星期日：权力的游戏 S03E05
	public static final Pattern SEPATT = Pattern
			.compile(".*?S(\\d\\d)E(\\d\\d).*");

	private static void updateTvepisodesWithRssnews() {
		List<RssNews> rssNewsList = RssNewsDao.getRssNewsTmp();
		for (RssNews rssNews : rssNewsList) {
			String title = rssNews.getTitle();
			Matcher match = SEPATT.matcher(title);
			if (match.find()) {
				int season = Integer.parseInt(match.group(1));
				int episode = Integer.parseInt(match.group(2));
				String tvdbid = rssNews.getTvShows().getTvdbid();
				TvepisodesDao.updateRssNewsId(rssNews.getId(), tvdbid, season,
						episode);
				// System.out.println("Success:\t" + title);
			} // else
			// System.out.println("Fail:\t" + title);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// System.out.println("".split(";")[0].length());
		// updateWeiboVideo();
		updateTvepisodesWithRssnews();
	}

}
