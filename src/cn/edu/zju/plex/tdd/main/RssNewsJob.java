package cn.edu.zju.plex.tdd.main;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import cn.edu.zju.plex.tdd.dao.RssFeedsDao;
import cn.edu.zju.plex.tdd.dao.RssNewsDao;
import cn.edu.zju.plex.tdd.dao.TvepisodesDao;
import cn.edu.zju.plex.tdd.entity.RssFeed;
import cn.edu.zju.plex.tdd.entity.RssNews;
import cn.edu.zju.plex.tdd.module.RssNewsCrawler;
import cn.edu.zju.plex.tdd.module.RssNewsParser;
import cn.edu.zju.plex.tdd.module.RssNewsRmDup;
import cn.edu.zju.plex.tdd.module.TvfantasySpliter;
import cn.edu.zju.plex.tdd.tools.ImageFetcher;
import cn.edu.zju.plex.tdd.tools.UrlUtil;

/**
 * crawl & parse rssNews
 * 
 * @author plex
 */
public class RssNewsJob implements Runnable {
    private static final Logger LOG = Logger.getLogger(RssNewsJob.class);
    private final int FOUR_HOUR = 4 * 60 * 60 * 1000;
    private RssNewsCrawler crawler = new RssNewsCrawler();
    private RssNewsParser parser = new RssNewsParser();
    private static final Pattern ImagePatt = Pattern.compile(
	    ".*(\\.(bmp|gif|jpe?g|png|tiff?|ico))$", Pattern.CASE_INSENSITIVE);
    private static final Pattern SEPATT = Pattern
	    .compile(".*?S(\\d\\d)E(\\d\\d).*");

    private void fetchRssUpdates() {
	ArrayList<RssFeed> rssFeeds = RssFeedsDao.listAll();
	for (RssFeed rf : rssFeeds) {
	    Date latestUpdate = null;
	    for (RssNews rssnews : crawler.fetchUpdate(rf)) {
		if (latestUpdate == null) {
		    latestUpdate = rssnews.getPubDate();
		}
		RssNewsDao.insert(rssnews);
	    }
	    if (latestUpdate != null) {
		rf.setLastUpdate(latestUpdate);
		RssFeedsDao.updateLastUpdateTime(rf);
	    }
	}
    }

    private void parseRssNews() {
	while (true) {
	    List<RssNews> rssNewsToParse = RssNewsDao.getRssNewsToParse(30);
	    LOG.info("Get " + rssNewsToParse.size() + " rss items to parse...");

	    if (rssNewsToParse.size() == 0) {
		LOG.info("RssNews parse work temporally done");
		break;
	    } else {
		for (RssNews rssNews : rssNewsToParse) {
		    if (rssNews.getPage() == null
			    || rssNews.getPage().length() < 10) {
			String link = rssNews.getLink();
			rssNews.setLink(link);
			String page = crawler.fetchPage(link);
			rssNews.setPage(page);
		    }
		    if (TvfantasySpliter.needSplit(rssNews)) {
			TvfantasySpliter.splite(rssNews);
			continue;
		    }
		    rssNews = parser.parse(rssNews);
		    RssNewsDao.updateParsedRes(rssNews);

		    // 更新tvepisodes表中的rss_news_id字段
		    if (rssNews.getLink().startsWith("http://tvfantasy.net")) {
			Matcher match = SEPATT.matcher(rssNews.getTitle());
			if (match.find()) {
			    int season = Integer.parseInt(match.group(1));
			    int episode = Integer.parseInt(match.group(2));
			    String tvdbid = rssNews.getTvShows().getTvdbid();
			    TvepisodesDao.updateRssNewsId(rssNews.getId(),
				    tvdbid, season, episode);
			    LOG.info("update tvepisodes with:"
				    + rssNews.getTitle());
			}
		    }
		}
	    }
	}

    }

    private void rmDump() {
	int timeLen = 172800000;// two days
	while (true) {
	    List<RssNews> list = RssNewsDao.getRssNewsToMerge(timeLen);
	    RssNews[] rssNewsToMerge = new RssNews[list.size()];
	    list.toArray(rssNewsToMerge);

	    if (rssNewsToMerge.length < 2) {
		LOG.info("RssNews merge work temply done");
		break;
	    } else {
		RssNewsRmDup.deals(rssNewsToMerge);
		for (RssNews rssNews : rssNewsToMerge)
		    if (rssNews.getDelegate() != 0)
			RssNewsDao.updateDelegate(rssNews);
		LOG.info("merge rss news:" + rssNewsToMerge.length);
	    }

	}
    }

    private void downloadImages(String rootPath) {
	while (true) {
	    List<RssNews> list = RssNewsDao.getRssNewsToDownloadImages();
	    LOG.info("Loop for downloading images, RssNews count:"
		    + list.size());

	    if (list.size() <= 0) {
		LOG.info("download RssNews images work temply done");
		break;
	    } else {
		for (RssNews rssNews : list) {

		    String[] images = rssNews.getImages().split(";");
		    StringBuffer imageSizes = new StringBuffer();
		    int count = 0;
		    for (int i = 0; i < images.length && count < 5; i++) {
			String imageUrl = images[i];
			Matcher m = ImagePatt.matcher(imageUrl);
			if (m.find()) {
			    if (imageUrl.startsWith(".")
				    || imageUrl.startsWith("/")) {
				imageUrl = UrlUtil.convertToAbsolute(imageUrl,
					rssNews.getLink());
				if (imageUrl.startsWith("."))
				    continue; // this is not solved
			    }
			    String imageSize = ImageFetcher.saveimage(imageUrl,
				    rootPath + "news-" + rssNews.getId() + "-"
					    + count + m.group(1));
			    if (imageSize != null) {
				imageSizes.append(imageSize).append(";");
				count++;
			    } else
				LOG.warn("fail downloading:" + imageUrl);
			} else
			    LOG.debug("invalid image url" + imageUrl);
		    }
		    if (count > 0) {
			RssNewsDao.updateImageCountAndSize(rssNews.getId(),
				count, imageSizes.toString());
		    } else
			RssNewsDao.updateImageCountAndSize(rssNews.getId(), 0,
				"");
		}
		LOG.info("download images for rss news:" + list.size());
	    }

	}
    }

    @Override
    public void run() {
	while (true) {
	    LOG.info("Loop start for RssNewsJob");
	    try {
		 LOG.info("开始下载rss更新");
		 fetchRssUpdates();

		LOG.info("开始解析rss_news");
		parseRssNews();

//		 LOG.info("开始去重");
//		 rmDump();

		 LOG.info("下載圖片");
		 downloadImages("d:/tmp/images/");
	    } catch (Exception t) {
		LOG.error(t);
	    } finally {
		LOG.info("Loop over for RssNewsJob");
		try {
		    Thread.sleep(FOUR_HOUR);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
	    }
	}
    }

    public static void main(String[] args) {
	new Thread(new RssNewsJob(), "RssNewsJob").start();
    }
}
