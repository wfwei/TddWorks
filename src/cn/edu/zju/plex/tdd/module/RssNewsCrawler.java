package cn.edu.zju.plex.tdd.module;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.horrabin.horrorss.RssChannelBean;
import org.horrabin.horrorss.RssItemBean;
import org.horrabin.horrorss.RssParser;

import cn.edu.zju.plex.tdd.dao.DB4Tdd;
import cn.edu.zju.plex.tdd.entity.RssFeed;
import cn.edu.zju.plex.tdd.entity.RssNews;
import cn.edu.zju.plex.tdd.tools.HttpUtil;

public class RssNewsCrawler implements Runnable {

	private static final Logger LOG = Logger.getLogger(RssNewsCrawler.class);
	
	// private static Pattern refreshPtn = Pattern
	// .compile("<HTML>[^<]*<HEAD>[^<]*<META HTTP-EQUIV=\"Refresh\" CONTENT=\"0;URL=(http://[^\"]+)\">[^<]*</HEAD>[^<]*</HTML>");

	private RssParser rssParser = new RssParser();
	private HttpUtil httpUtil = new HttpUtil();

	private void checkUpdate(RssFeed rssFeed) {
		try {
			String feedPage = httpUtil.fetchPage(rssFeed.getFeed());
			org.horrabin.horrorss.RssFeed feed = rssParser.loadString(feedPage);

			// Gets the channel information of the feed and
			// display its title
			RssChannelBean channel = feed.getChannel();
			LOG.info("Get feed update: " + channel.getTitle());

			// TODO category is not parsed???
			List<RssItemBean> items = feed.getItems();
			for (RssItemBean item : items) {
				RssNews rssnews = new RssNews();
				String page = httpUtil.fetchPage(item.getLink());
				rssnews.setFirstPart(item.getTitle(), item.getLink(),
						item.getCategory(), item.getDescription(),
						item.getPubDate(), page, rssFeed.getId());
				DB4Tdd.insertRssNews(rssnews);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.toString());
		}

	}

	@Override
	public void run() {
		ArrayList<RssFeed> rssFeeds = DB4Tdd.getRssFeedList();
		for (RssFeed rf : rssFeeds) {
			checkUpdate(rf);
		}
	}

	public static void main(String[] args) {
//		new Thread(new RssNewsCrawler(), "RssCrawler").start();
	}

}
