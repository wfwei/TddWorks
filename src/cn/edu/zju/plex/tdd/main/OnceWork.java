package cn.edu.zju.plex.tdd.main;

import java.util.List;

import org.apache.log4j.Logger;

import weibo4j.org.json.JSONObject;
import cn.edu.zju.plex.tdd.dao.WeiboDao;
import cn.edu.zju.plex.tdd.entity.ParsedStatus;

public class OnceWork {

	private static final Logger LOG = Logger.getLogger(OnceWork.class);

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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// System.out.println("".split(";")[0].length());
		updateWeiboVideo();
	}

}
