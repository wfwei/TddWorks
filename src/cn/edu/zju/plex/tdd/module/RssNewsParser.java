package cn.edu.zju.plex.tdd.module;

import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.zju.plex.tdd.dao.DB4Tdd;
import cn.edu.zju.plex.tdd.entity.RssNews;
import cn.edu.zju.plex.tdd.parser.ParserFactory;
import cn.edu.zju.plex.tdd.seg.MyICTCLAS;
import cn.edu.zju.plex.tdd.tools.MeijuTvUtil;
import cn.edu.zju.plex.tdd.tools.TvfantasySplitUtil;


public class RssNewsParser implements Runnable {

	private static final Logger LOG = Logger.getLogger(RssNewsParser.class);

	@Override
	public void run() {
		while (true) {
			List<RssNews> rssNewsToParse = DB4Tdd.getRssNewsToParse(30);
			LOG.info("Get " + rssNewsToParse.size() + " rss items to parse...");

			if (rssNewsToParse.size() == 0) {
				LOG.info("Temporally done");
				break;
			} else {
				for (RssNews rssNews : rssNewsToParse) {
					if(rssNews.getLink().matches("http://tvfantasy.net/.{10}/newsletter[^#]+")){
						TvfantasySplitUtil.splite(rssNews);
						continue;
					}
					rssNews = parse(rssNews);
					DB4Tdd.updateParsedRssNews(rssNews);
				}
			}
		}

	}

	private RssNews parse(RssNews rssNews) {

		ParserFactory.getParser(rssNews).parse();

		// words segmentation
		String words = MyICTCLAS.fenci(rssNews.getContent());
		rssNews.setWords(words);
		
		// parse tvids
		String meijuIds = MeijuTvUtil.guessTv(rssNews.getContent());
		rssNews.setMeiju_ids(meijuIds);

		rssNews.setStatus(RssNews.ST_FINISHED);
		return rssNews;
	}

	public static void main(String args[]) {
//		new Thread(new RssNewsParser(), "RssParser").start();
	}

}
