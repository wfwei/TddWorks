package cn.edu.zju.plex.tdd.main;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import cn.edu.zju.plex.tdd.dao.DB4Tdd;
import cn.edu.zju.plex.tdd.entity.RssFeed;
import cn.edu.zju.plex.tdd.entity.RssNews;
import cn.edu.zju.plex.tdd.module.RssNewsCrawler;
import cn.edu.zju.plex.tdd.module.RssNewsParser;
import cn.edu.zju.plex.tdd.module.RssNewsRmDup;
import cn.edu.zju.plex.tdd.tools.TvfantasySplitUtil;

/**
 * crawl & parse rssNews
 * 
 * @author plex
 */
public class RssNewsJob implements Runnable {

	private static final Logger LOG = Logger.getLogger(RssNewsJob.class);

	private RssNewsCrawler crawler = new RssNewsCrawler();
	private RssNewsParser parser = new RssNewsParser();

	private void fetchRssUpdates() {
		ArrayList<RssFeed> rssFeeds = DB4Tdd.getRssFeedList();
		for (RssFeed rf : rssFeeds) {
			for (RssNews rssnews : crawler.fetchUpdate(rf))
				DB4Tdd.insertRssNews(rssnews);
		}
	}

	private void parseRssNews() {
		while (true) {
			List<RssNews> rssNewsToParse = DB4Tdd.getRssNewsToParse(30);
			LOG.info("Get " + rssNewsToParse.size() + " rss items to parse...");

			if (rssNewsToParse.size() == 0) {
				LOG.info("Temporally done");
				break;
			} else {
				for (RssNews rssNews : rssNewsToParse) {
					if (rssNews.getLink().matches(
							"http://tvfantasy.net/.{10}/newsletter[^#]+")) {
						TvfantasySplitUtil.splite(rssNews);
						continue;
					}
					rssNews = parser.parse(rssNews);
					DB4Tdd.updateParsedRssNews(rssNews);
				}
			}
		}

	}

	private void rmDump() {
		int timeLen = 172800000;// two days
		while (true) {
			List<RssNews> list = DB4Tdd.getRssNewsToMerge(timeLen);
			RssNews[] rssNewsToMerge = new RssNews[list.size()];
			list.toArray(rssNewsToMerge);

			if (rssNewsToMerge.length < 2) {
				LOG.info("get all merge work temply done");
				break;
			} else {
				RssNewsRmDup.deals(rssNewsToMerge);
				for (RssNews rssNews : rssNewsToMerge)
					if (rssNews.getDelegate() != 0)
						DB4Tdd.updateDelegate(rssNews);
				LOG.info("merge rss news:" + rssNewsToMerge.length);
			}

		}
	}

	@Override
	public void run() {
		LOG.info("Loop start for RssNewsJob");

		try {
			LOG.info("开始下载rss更新");
			fetchRssUpdates();

			LOG.info("开始解析rss_news");
			parseRssNews();

			LOG.info("开始去重");
			rmDump();
		} catch (Throwable t) {
			LOG.error(t);
			LOG.error(t.getCause());
		} finally {
			LOG.info("Loop stop for RssNewsJob");
		}
	}

	public static void main(String[] args) {
		new Thread(new RssNewsJob(), "RssNewsJob").start();
	}
}
