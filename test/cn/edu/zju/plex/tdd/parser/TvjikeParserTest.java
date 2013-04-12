package cn.edu.zju.plex.tdd.parser;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import cn.edu.zju.plex.tdd.dao.DB4Tdd;
import cn.edu.zju.plex.tdd.entity.RssNews;

public class TvjikeParserTest {
	
	int feed=4; int count=10;
	
	List<RssNews> rssNews = DB4Tdd.getRssNewsForTest(feed, count);
	
	@Ignore
	@Test
	public void testSetTargetElements(){
		for(RssNews item:rssNews){
			TvjikeParser parser = new TvjikeParser(item);
			parser.setTargetElements();
		}
	}
	
	@Test
	public void testParseImages(){
		for(RssNews item:rssNews){
			TvjikeParser parser = new TvjikeParser(item);
			parser.parseImages();
		}
	}
}
