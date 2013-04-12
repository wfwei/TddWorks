package cn.edu.zju.plex.tdd.parser;

import cn.edu.zju.plex.tdd.entity.RssNews;
import cn.edu.zju.plex.tdd.parser.AbstractParser;

public class ParserFactory {

	// TODO 通过数据库自动加载
	public static AbstractParser getParser(RssNews rssNews) {
		AbstractParser parser = null;
		if (rssNews.getFeed() == 1)
			parser = new TvfantasyParser(rssNews);
		else if (rssNews.getFeed() == 2)
			parser = new MtimeParser(rssNews);
		else if (rssNews.getFeed() == 3)
			parser = new Huo360Parser(rssNews);
		else if (rssNews.getFeed() == 4)
			parser = new TvjikeParser(rssNews);
		else
			parser = new DefaultParser(rssNews);
		return parser;
	}
}
