package cn.edu.zju.plex.tdd.main;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import cn.edu.zju.plex.tdd.dao.DB4Tdd;
import cn.edu.zju.plex.tdd.entity.RssNews;
import cn.edu.zju.plex.tdd.parser.ParserFactory;
//import cn.edu.zju.plex.tdd.seg.IctclasSeg;
import cn.edu.zju.plex.tdd.seg.MyICTCLAS;

public class RssNewsParser implements Runnable {
	static {
		PropertyConfigurator.configure("resources/log4j.properties");
	}
	private static final Logger LOG = Logger.getLogger(RssNewsParser.class);
	private static final Long ONE_HOUR = 3600000L;

	void parse(RssNews rssNews) {

		ParserFactory.getParser(rssNews).parse();
		
		 String words = MyICTCLAS.fenci(rssNews.getContent());
		 rssNews.setWords(words);

		rssNews.setStatus(RssNews.ST_FINISHED);
	}

	@Override
	public void run() {
		while (true) {
			List<RssNews> rssNewsToParse = DB4Tdd.getRssNewsToParse(30);
			LOG.info("Get " + rssNewsToParse.size() + " rss items to parse...");

			if (rssNewsToParse.size() == 0) {
				LOG.info("Temporally done, going to sleep a while");
				try {
					Thread.sleep(ONE_HOUR*10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				for (RssNews rssNews : rssNewsToParse) {
					parse(rssNews);
					DB4Tdd.updateParsedData(rssNews);
				}
			}
		}

	}

	public static void main(String args[]) {
		for (int i = 0; i < 1; i++) {
			new Thread(new RssNewsParser(), "RssParser-" + i).start();
			LOG.info("RssParser-" + i + " started");
		}
	}

}
