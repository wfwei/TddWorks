package cn.edu.zju.plex.tdd.parser;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import cn.edu.zju.plex.tdd.dao.RssNewsDao;
import cn.edu.zju.plex.tdd.entity.RssNews;
import cn.edu.zju.plex.tdd.module.RssNewsCrawler;

public class Huo360ParserTest {

	int feed = 3;
	int count = 1000;

	List<RssNews> rssNewsList = RssNewsDao.getRssNewsForTest(feed, count);
	private RssNewsCrawler crawler = new RssNewsCrawler();
	
	@Ignore
	@Test
	public void testSetTargetElements() {
		for (RssNews item : rssNewsList) {
			Huo360Parser parser = new Huo360Parser(item);
			parser.setTargetElements();
		}
	}

	@Ignore
	@Test
	public void testParseImages() {
		for (RssNews item : rssNewsList) {
			Huo360Parser parser = new Huo360Parser(item);
			parser.parseImages();
		}
	}

	@Test
	public void testParseContent() {
		for (RssNews rssNews : rssNewsList) {
			if (rssNews.getPage() == null || rssNews.getPage().length() < 10) {
				String link = rssNews.getLink();
				if (link.contains("#"))
					link = link.substring(0, link.indexOf('#'));
				String page = crawler.fetchPage(link);
				rssNews.setPage(page);
			}

			Huo360Parser parser = new Huo360Parser(rssNews);
			parser.parseContent();
		}

	}
}
