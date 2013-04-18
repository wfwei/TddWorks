package cn.edu.zju.plex.tdd.main;

import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.zju.plex.tdd.dao.DB4Tdd;
import cn.edu.zju.plex.tdd.entity.ParsedStatus;
import cn.edu.zju.plex.tdd.module.WeiboCrawler;
import cn.edu.zju.plex.tdd.module.WeiboParser;

/**
 * crawl & parse weibo
 * 
 * @author WangFengwei
 */
public class WeiboJob implements Runnable {

	private final Logger LOG = Logger.getLogger(WeiboJob.class);
	private WeiboCrawler crawler = new WeiboCrawler();
	private WeiboParser parser = new WeiboParser();
	
	@Override
	public void run() {
		LOG.info("Loop start for WeiboJob");
		
		// fetch weibo update
		crawler.fetchAndStoreUpdate();
		
		// parse
		while (true) {
			List<ParsedStatus> weiboToParse = DB4Tdd.getWeiboToParse(30);
			LOG.info("Get " + weiboToParse.size() + " weibo status to parse...");

			if (weiboToParse.size() == 0) {
				LOG.info("Weibo parsing work temporally done");
				break;
			} else {
				for (ParsedStatus status : weiboToParse) {
					parser.parse(status);
					DB4Tdd.updateParsedStatus(status);
				}
			}
		}
		
		// 去重
		// TODO
	}

}
