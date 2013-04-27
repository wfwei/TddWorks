package cn.edu.zju.plex.tdd.parser;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import cn.edu.zju.plex.tdd.dao.RssNewsDao;
import cn.edu.zju.plex.tdd.entity.RssNews;
import cn.edu.zju.plex.tdd.module.RssNewsCrawler;

public class TvjikeParserTest {
	
	int feed=4; int count=10;
	
	List<RssNews> rssNewsList = RssNewsDao.getRssNewsForTest(feed, count);
	private RssNewsCrawler crawler = new RssNewsCrawler();
	
	@Ignore
	@Test
	public void testSetTargetElements(){
		for(RssNews rssNews:rssNewsList){
			if (rssNews.getPage() == null
					|| rssNews.getPage().length() < 10) {
				String link = rssNews.getLink();
				if(link.contains("#"))
					link = link.substring(0, link.indexOf('#'));
				String page = crawler.fetchPage(link);
				rssNews.setPage(page);
			}
			
			TvjikeParser parser = new TvjikeParser(rssNews);
			parser.setTargetElements();
		}
	}
	
	@Test
	public void testParseImages(){
		for(RssNews rssNews:rssNewsList){
			if (rssNews.getPage() == null
					|| rssNews.getPage().length() < 10) {
				String link = rssNews.getLink();
				if(link.contains("#"))
					link = link.substring(0, link.indexOf('#'));
				String page = crawler.fetchPage(link);
				rssNews.setPage(page);
			}
			TvjikeParser parser = new TvjikeParser(rssNews);
			parser.parseImages();
		}
	}
}
