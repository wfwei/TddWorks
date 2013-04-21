package cn.edu.zju.plex.tdd.main;

import java.util.List;

import weibo4j.org.json.JSONObject;
import cn.edu.zju.plex.tdd.dao.DB4Tdd;
import cn.edu.zju.plex.tdd.entity.ParsedStatus;
import cn.edu.zju.plex.tdd.entity.Video;
import cn.edu.zju.plex.tdd.tools.VideoUtil;

public class OnceWork {

	private static void updateWeiboVideo() {

		weibo4j.ShortUrl su = new weibo4j.ShortUrl();
		su.client.setToken("2.00l9nr_DfUKrWDf655d3279arZgVvD");
		while (true) {
			List<ParsedStatus> sts = DB4Tdd.getWeiboToUpdateVideos(100);
			if (sts.size() == 0)
				break;
			else {
				for (ParsedStatus ps : sts) {
					Video video = null;
					for (String url : ps.getUrl().split(";")) {
						if (url.length() < 1)
							continue;
						try {
							JSONObject jo = su.shortToLongUrl(url.trim());
							String vurl = jo.getJSONArray("urls")
									.getJSONObject(0).getString("url_long");
							video = VideoUtil.getVideoInfo(vurl);
							if (video != null) {
								break;
							} else {
								try {
									Thread.sleep(10000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						} catch (Exception e) {
							System.err.println(e);
						}
					}
					if (video != null) {
						DB4Tdd.updateWeiboVideo(ps.getId(), video.getUrl()
								+ "," + video.getPic());
					} else {
						DB4Tdd.updateWeiboVideo(ps.getId(), "");
					}
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
