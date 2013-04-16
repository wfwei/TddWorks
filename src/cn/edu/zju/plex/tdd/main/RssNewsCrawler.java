package cn.edu.zju.plex.tdd.main;

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
	private static final Long ONE_HOUR = 3600000L;
	// private static Pattern refreshPtn = Pattern
	// .compile("<HTML>[^<]*<HEAD>[^<]*<META HTTP-EQUIV=\"Refresh\" CONTENT=\"0;URL=(http://[^\"]+)\">[^<]*</HEAD>[^<]*</HTML>");

	private RssFeed rssFeed;
	private RssParser rssParser = new RssParser();
	private HttpUtil httpUtil = new HttpUtil();

	public RssNewsCrawler(RssFeed rssFeed) {
		this.rssFeed = rssFeed;
	}

	private void checkUpdate() {
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOG.error(e.toString());
		}

	}

	@Override
	public void run() {
		while (true) {
			checkUpdate();
			try {
				// 从google reader上查看各个rss的更新频率后决定8小时轮循
				long sleepTime = 8 * ONE_HOUR;
				LOG.info("Sleep for " + sleepTime / ONE_HOUR);
				Thread.currentThread().sleep(sleepTime);
			} catch (InterruptedException e) {
				LOG.error("Thread Interrupted!\t" + e.getMessage());
				// LOG.trace("", e);// TODO
			}
		}
	}

	public static void main(String[] args) {
		ArrayList<RssFeed> rssFeeds = DB4Tdd.getRssFeedList();
		for (RssFeed rf : rssFeeds) {
			new Thread(new RssNewsCrawler(rf), "RssCrawler-" + rf.getTitle())
					.start();
			LOG.info("RssCrawler-" + rf.getTitle() + " started");
		}
	}

}
