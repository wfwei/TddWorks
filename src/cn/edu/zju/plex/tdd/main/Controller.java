package cn.edu.zju.plex.tdd.main;

import cn.edu.zju.plex.tdd.module.RssNewsCrawler;
import cn.edu.zju.plex.tdd.module.RssNewsParser;
import cn.edu.zju.plex.tdd.module.WeiboCrawler;
import cn.edu.zju.plex.tdd.module.WeiboParser;

public class Controller {
	private static final Long ONE_HOUR = 3600000L;


	public static void main(String[] args) {
		RssNewsCrawler rssNewsCrawler = new RssNewsCrawler();
		WeiboCrawler weiboCrawler = new WeiboCrawler();
		RssNewsParser rssNewsParser = new RssNewsParser();
		WeiboParser weiboParser = new WeiboParser();
		
		//http://marshal.easymorse.com/archives/3136		
	}

}
