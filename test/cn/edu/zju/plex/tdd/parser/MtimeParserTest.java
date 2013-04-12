package cn.edu.zju.plex.tdd.parser;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import cn.edu.zju.plex.tdd.dao.DB4Tdd;
import cn.edu.zju.plex.tdd.entity.RssNews;

public class MtimeParserTest {
	
	int feed=2;int count=10;
	
	List<RssNews> rssNews = DB4Tdd.getRssNewsForTest(feed, count);
	
	@Ignore
	@Test
	public void testSetTargetElements(){
		for(RssNews item:rssNews){
			MtimeParser parser = new MtimeParser(item);
			parser.setTargetElements();
		}
	}
	
	@Test
	public void testParseImages(){
		for(RssNews item:rssNews){
			MtimeParser parser = new MtimeParser(item);
			parser.parseImages();
		}
	}
}
