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
	private final int HALF_HOUR = 1800000;
	private WeiboCrawler crawler = new WeiboCrawler();
	private WeiboParser parser = new WeiboParser();

	private void fetchWeiboUpdate() {
		crawler.fetchAndStoreUpdate();
	}

	private void parseWeibo() {
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
	}

	@Override
	public void run() {
		while (true) {
			LOG.info("Loop start for WeiboJob");
			try {
				LOG.info("开始下载微博更新");
				fetchWeiboUpdate();

				LOG.info("开始解析微博");
				parseWeibo();

				// 去重
				// TODO if needed
			} catch (Throwable t) {
				LOG.error(t.getCause().getMessage());
			} finally {
				LOG.info("Loog over for WeiboJob");
				try {
					Thread.sleep(HALF_HOUR);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) {
		new Thread(new WeiboJob(), "WeiboJob").start();
	}

}
