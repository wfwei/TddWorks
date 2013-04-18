package cn.edu.zju.plex.tdd.main;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import cn.edu.zju.plex.tdd.dao.DB4Tdd;
import cn.edu.zju.plex.tdd.entity.RssFeed;
import cn.edu.zju.plex.tdd.entity.RssNews;
import cn.edu.zju.plex.tdd.module.RssNewsCrawler;
import cn.edu.zju.plex.tdd.module.RssNewsParser;
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


	@Override
	public void run() {
		LOG.info("Loop start for RssNewsJob");
		
		//下载rss更新
		ArrayList<RssFeed> rssFeeds = DB4Tdd.getRssFeedList();
		for (RssFeed rf : rssFeeds) {
			for(RssNews rssnews:crawler.fetchUpdate(rf))
				DB4Tdd.insertRssNews(rssnews);
		}
		
		// 解析rss_news
		while (true) {
			List<RssNews> rssNewsToParse = DB4Tdd.getRssNewsToParse(30);
			LOG.info("Get " + rssNewsToParse.size() + " rss items to parse...");

			if (rssNewsToParse.size() == 0) {
				LOG.info("Temporally done");
				break;
			} else {
				for (RssNews rssNews : rssNewsToParse) {
					if(rssNews.getLink().matches("http://tvfantasy.net/.{10}/newsletter[^#]+")){
						TvfantasySplitUtil.splite(rssNews);
						continue;
					}
					rssNews = parser.parse(rssNews);
					DB4Tdd.updateParsedRssNews(rssNews);
				}
			}
		}
		
		// 去重
		// TODO
	}
}
