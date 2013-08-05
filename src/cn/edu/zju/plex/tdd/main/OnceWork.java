package cn.edu.zju.plex.tdd.main;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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

    /** 爬取天涯小筑的剧集介绍部分 http://tvfantasy.net/category/main-columns/episode-synopsis/ */
    public static void crawlTvfantasy() {
	long feedId = 1;
	String baseUrl = "http://tvfantasy.net/category/main-columns/episode-synopsis/page/";
	int maxTimeout = 30000;
	Document doc = null;
	int page = 213;
	try {
	    for (; page < 400; page++) {
		System.out.println("on page:" + page);
		String url = baseUrl + page;
		doc = Jsoup
			.connect(url)
			.userAgent(
				"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.56 Safari/537.17")
			.timeout(maxTimeout).get();

		Elements articles = doc.getElementsByTag("article");
		for (Element article : articles) {
		    Element content = article.child(1);
		    Element titleAnchor = content.child(0).getElementsByTag("a").first();
		    String title = titleAnchor.text();
		    String link = titleAnchor.attr("href");
		    Elements eles = content.child(1).getElementsByTag("font");
		    String author = eles.get(0).text();
		    String pubDateStr = eles.get(1).text();
		    Date pubDate = new SimpleDateFormat("yyyy年MM月dd日")
			    .parse(pubDateStr);
		    StringBuilder description = new StringBuilder();
		    for (int i = 2; i < content.children().size() - 3; i++) {
			description.append(content.child(i).html());
		    }
		    RssNews rssnews = new RssNews();
		    rssnews.setFirstPart(title, link, author, "",
			    description.toString(), pubDate, "", feedId);
		    RssNewsDao.insert(rssnews);
		}

		Thread.sleep(3000);
	    }

	} catch (IOException e) {
	    e.printStackTrace();
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    System.out.println("current on page:" + page);
	}

    }

    /**
     * @param args
     */
    public static void main(String[] args) {
	// System.out.println("".split(";")[0].length());
	// updateWeiboVideo();
	// updateTvepisodesWithRssnews();
	crawlTvfantasy();
    }

}
