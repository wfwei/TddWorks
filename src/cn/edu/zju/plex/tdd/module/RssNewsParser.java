package cn.edu.zju.plex.tdd.module;

import org.apache.log4j.Logger;

import cn.edu.zju.plex.tdd.entity.RssNews;
import cn.edu.zju.plex.tdd.entity.TvShows;
import cn.edu.zju.plex.tdd.parser.ParserFactory;

/**
 * RssNews parser
 * 
 * @author plex
 */
public class RssNewsParser {

	private static final Logger LOG = Logger.getLogger(RssNewsParser.class);

	public RssNews parse(RssNews rssNews) {
		LOG.info("paring rssNews:" + rssNews);

		ParserFactory.getParser(rssNews).parse();

		LOG.info("parse tvshow");
		TvShows tvShows = MeijuTvAnalyzer.guessTv(rssNews.getContent()
				+ rssNews.getTitle());
		rssNews.setTvShows(tvShows);

		rssNews.setStatus(RssNews.ST_FINISHED);
		return rssNews;
	}

}
