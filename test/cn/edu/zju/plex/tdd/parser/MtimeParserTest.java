package cn.edu.zju.plex.tdd.parser;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import cn.edu.zju.plex.tdd.dao.DB4Tdd;
import cn.edu.zju.plex.tdd.entity.RssNews;
import cn.edu.zju.plex.tdd.module.RssNewsCrawler;

public class MtimeParserTest {

	int feed = 2;
	int count = 100;

	List<RssNews> rssNewsList = DB4Tdd.getRssNewsForTest(feed, count);
	private RssNewsCrawler crawler = new RssNewsCrawler();
	@Ignore
	@Test
	public void testSetTargetElements() {
		for (RssNews item : rssNewsList) {
			MtimeParser parser = new MtimeParser(item);
			parser.setTargetElements();
		}
	}

	@Ignore
	@Test
	public void testParseImages() {
		for (RssNews item : rssNewsList) {
			MtimeParser parser = new MtimeParser(item);
			parser.parseImages();
		}
	}

	@Test
	public void testContent() {
		for (RssNews rssNews : rssNewsList) {
			if (rssNews.getPage() == null || rssNews.getPage().length() < 10) {
				String link = rssNews.getLink();
				if (link.contains("#"))
					link = link.substring(0, link.indexOf('#'));
				String page = crawler.fetchPage(link);
				rssNews.setPage(page);
			}
			MtimeParser parser = new MtimeParser(rssNews);
			parser.parseContent();
		}
	}
}
