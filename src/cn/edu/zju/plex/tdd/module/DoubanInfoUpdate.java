package cn.edu.zju.plex.tdd.module;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.edu.zju.plex.tdd.dao.TvShowsDao;
import cn.edu.zju.plex.tdd.entity.TvShows;
import cn.edu.zju.plex.tdd.tools.HttpUtil;
import weibo4j.org.json.JSONObject;

public class DoubanInfoUpdate {

	private HttpUtil httpUtil = new HttpUtil();
	private static final Pattern AKAJsonPatt = Pattern.compile("\"(.*?)\"");

	public void updateAkas(TvShows tvShow) throws InstantiationException,
			IllegalAccessException, Exception {
		String tvdbid = tvShow.getTvdbid();
		String url = tvShow.getDoubanid();
		String oldAkas = tvShow.getAka() + tvShow.getAka_original();
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
				newAkas.append(tvShow.getAka());
				TvShowsDao.updateTvShowAka(tvdbid, newAkas.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void updateDoubanIdToV2() {
		for (TvShows tvShow : TvShowsDao.getTvShowList()) {
			// "http://api.douban.com/v2/movie/subject/1764796"
			System.out.println(tvShow.getDoubanid());
			if (tvShow.getDoubanid() != null
					&& tvShow.getDoubanid().contains("com/movie")) {
				String doubanid = tvShow.getDoubanid().replace("com/movie",
						"com/v2/movie");
				TvShowsDao.updateDoubanId(tvShow.getTvdbid(), doubanid);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		updateDoubanIdToV2();
		DoubanInfoUpdate douban = new DoubanInfoUpdate();
		for (TvShows tvShow : TvShowsDao.getTvShowList()) {
			if (tvShow.getDoubanid() == null
					|| tvShow.getDoubanid().length() < 1)
				continue;
			douban.updateAkas(tvShow);
		}
	}
}
