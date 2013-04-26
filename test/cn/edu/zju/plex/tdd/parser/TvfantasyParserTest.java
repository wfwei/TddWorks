package cn.edu.zju.plex.tdd.parser;

import java.util.List;

import org.junit.Test;

import cn.edu.zju.plex.tdd.dao.DB4Tdd;
import cn.edu.zju.plex.tdd.entity.RssNews;
import cn.edu.zju.plex.tdd.module.RssNewsCrawler;

public class TvfantasyParserTest {

	int feed = 1;
	int count = 10;

	List<RssNews> rssNewsList = DB4Tdd.getRssNewsForTest(feed, count);
	private RssNewsCrawler crawler = new RssNewsCrawler();
	@Test
	public void testParseContent() {
		for(RssNews rssNews:rssNewsList){
			if (rssNews.getPage() == null
					|| rssNews.getPage().length() < 10) {
				String link = rssNews.getLink();
				if(link.contains("#"))
					link = link.substring(0, link.indexOf('#'));
				String page = crawler.fetchPage(link);
				rssNews.setPage(page);
			}
			
			TvfantasyParser parser = new TvfantasyParser(rssNews);
			parser.parseContent();
		}
	}
}
