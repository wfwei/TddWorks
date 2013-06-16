package cn.edu.zju.plex.tdd.module;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.horrabin.horrorss.RssItemBean;
import org.horrabin.horrorss.RssParser;

import cn.edu.zju.plex.tdd.entity.RssFeed;
import cn.edu.zju.plex.tdd.entity.RssNews;
import cn.edu.zju.plex.tdd.tools.HttpUtil;

/**
 * RssNews Crawler
 * 
 * @author plex
 */
public class RssNewsCrawler {

	private static final Logger LOG = Logger.getLogger(RssNewsCrawler.class);

	// private static Pattern refreshPtn = Pattern
	// .compile("<HTML>[^<]*<HEAD>[^<]*<META HTTP-EQUIV=\"Refresh\" CONTENT=\"0;URL=(http://[^\"]+)\">[^<]*</HEAD>[^<]*</HTML>");

	private RssParser rssParser = new RssParser();
	private HttpUtil httpUtil = new HttpUtil();

	public String fetchPage(String url) {
		return httpUtil.fetchPage(url);
	}

	public ArrayList<RssNews> fetchUpdate(RssFeed rssFeed) {
		LOG.info("fetching rss news updates:" + rssFeed.getTitle());

		ArrayList<RssNews> res = new ArrayList<RssNews>();
		try {
			String feedPage = httpUtil.fetchPage(rssFeed.getFeed());
			org.horrabin.horrorss.RssFeed feed = rssParser.loadString(feedPage);
			String author = feed.getChannel().getTitle();
			// TODO category is not parsed
			List<RssItemBean> items = feed.getItems();
			for (RssItemBean item : items) {
				if (item.getPubDate().getTime() <= rssFeed.getLastUpdate()
						.getTime())
					break;
				RssNews rssnews = new RssNews();
				String page = httpUtil.fetchPage(item.getLink());
				rssnews.setFirstPart(item.getTitle(), item.getLink(), author,
						item.getCategory(), item.getDescription(),
						item.getPubDate(), page, rssFeed.getId());
				res.add(rssnews);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.toString());
		}
		LOG.info("get total rss news updates:" + res.size());
		return res;

	}

}
