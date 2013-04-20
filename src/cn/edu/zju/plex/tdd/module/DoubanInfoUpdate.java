package cn.edu.zju.plex.tdd.module;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.edu.zju.plex.tdd.dao.DB4Tdd;
import cn.edu.zju.plex.tdd.entity.TvShows;
import cn.edu.zju.plex.tdd.tools.HttpUtil;
import weibo4j.org.json.JSONObject;

public class DoubanInfoUpdate {

	private HttpUtil httpUtil = new HttpUtil();
	private static final Pattern AKAJsonPatt = Pattern.compile("\"(.*?)\"");

	public void updateAkas(TvShows tvShow) throws InstantiationException,
			IllegalAccessException, Exception {
		String sid = tvShow.getSid();
		String url = tvShow.getDoubanid();
		String oldAkas = tvShow.getAkas();
		try {
			String tum = httpUtil.fetchPage(url);
			JSONObject jobj = new JSONObject(tum);

			String akas = jobj.optString("aka").toLowerCase();
			System.out.println(akas);
			Matcher mat = AKAJsonPatt.matcher(akas);
			StringBuffer newAkas = new StringBuffer();
			while (mat.find()) {
				String aka = mat.group(1);
				if (!oldAkas.contains(aka))
					newAkas.append(aka).append(",");
			}
			if (newAkas.length() > 0) {
				newAkas.append(oldAkas);
				DB4Tdd.updateTvShowAka(sid, newAkas.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void updateDoubanIdToV2() {
		for (TvShows tvShow : DB4Tdd.getTvShowList()) {
			// "http://api.douban.com/v2/movie/subject/1764796"
			System.out.println(tvShow.getDoubanid());
			if (tvShow.getDoubanid() != null
					&& tvShow.getDoubanid().contains("com/movie")) {
				String doubanid = tvShow.getDoubanid().replace("com/movie",
						"com/v2/movie");
				DB4Tdd.updateDoubanId(tvShow.getSid(), doubanid);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		// updateDoubanIdToV2();
		DoubanInfoUpdate douban = new DoubanInfoUpdate();
		for (TvShows tvShow : DB4Tdd.getTvShowList()) {
			if (tvShow.getDoubanid() == null
					|| tvShow.getDoubanid().length() < 1)
				continue;
			douban.updateAkas(tvShow);
		}
	}
}
