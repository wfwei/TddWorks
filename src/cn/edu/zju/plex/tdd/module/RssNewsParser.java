package cn.edu.zju.plex.tdd.module;

import org.apache.log4j.Logger;

import cn.edu.zju.plex.tdd.entity.RssNews;
import cn.edu.zju.plex.tdd.entity.TvShows;
import cn.edu.zju.plex.tdd.parser.ParserFactory;
import cn.edu.zju.plex.tdd.seg.MyICTCLAS;
import cn.edu.zju.plex.tdd.tools.MeijuTvUtil;

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

		LOG.info("words segmentation");
		String words = MyICTCLAS.fenci(rssNews.getContent());
		rssNews.setWords(words);
		
		LOG.info("parse tvshow");
		TvShows tvShows = MeijuTvUtil.guessTv(rssNews.getContent());
		rssNews.setTvShows(tvShows);

		rssNews.setStatus(RssNews.ST_FINISHED);
		return rssNews;
	}

	public static void main(String args[]) {
//		new Thread(new RssNewsParser(), "RssParser").start();
	}

}
