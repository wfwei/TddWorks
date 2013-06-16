package cn.edu.zju.plex.tdd.justtest;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.horrabin.horrorss.RssChannelBean;
import org.horrabin.horrorss.RssItemBean;
import org.horrabin.horrorss.RssParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.edu.zju.plex.tdd.entity.RssNews;
import cn.edu.zju.plex.tdd.tools.HttpUtil;

public class JsoupTest {
	private static final Logger LOG = Logger.getLogger(JsoupTest.class);

	static void JsoupTest() throws Exception {
		ArrayList<RssNews> res = new ArrayList<RssNews>();
		Document doc = Jsoup
				.parse(new URL(
						"http://tvfantasy.net/2013/04/12/newsletter-2013-04-12-hannibal-supernatural-revenge-homeland-tbw-how-and-more/#1"),
						1000);
		Element article = doc.getElementsByTag("article").get(0);
		System.out.println(article.text());
		System.out.println(article.html().replaceAll("<img.*?>", ""));

		ArrayList<Element> eles = new ArrayList<Element>();
		for (Element e : article.children()) {
			eles.add(e);
			e.remove();
		}
		System.out.println("eles.get(0):\n" + eles.get(0));
		System.out.println("article:\n" + article);
		article.insertChildren(0, eles.get(0).getAllElements());

		System.out.println("article:\n" + article);

		Elements watche = doc.getElementsByClass("entry_main");

		System.out.println("watche:\n" + watche);
	}

	static void JsoupTest2() throws Exception {
		Document doc = Jsoup.parse(new URL("http://zc.qq.com/chs/index.html"),
				1000);
		StringBuffer sb = new StringBuffer();
		Elements elements = doc.getElementsByTag("img");
		for (Element ele : elements) {
			String val = ele.attr("abc");
			System.out.println(val);
			if (ele.attr("src").matches(".*\\.(bmp|gif|jpe?g|png|tiff?|ico)"))
				continue;
			Element target = null;
			int parentLimit = 4;
			while (target == null && parentLimit > 0
					&& !ele.tagName().equals("html")) {
				String html = ele.toString();
				if (html.contains("验证码") || html.contains("校验码"))
					target = ele;
				else {
					ele = ele.parent();
					parentLimit--;
				}
			}
			if (target != null) {
				sb.append(target.toString()).append("#F#G#F#");
			}
		}
		System.out.println(sb);
	}

	static void horrorssTest() throws Exception {
		HttpUtil httpUtil = new HttpUtil();
		// http://www.tvjike.com/feed http://feed.mtime.com/news.rss
		String page = httpUtil.fetchPage("http://www.tvjike.com/feed");
		org.horrabin.horrorss.RssFeed feed = new RssParser().loadString(page);

		RssChannelBean channel = feed.getChannel();
		LOG.debug("Get feed update: " + channel.getTitle());

		List<RssItemBean> items = feed.getItems();
		for (RssItemBean item : items) {
			LOG.info("author:" + item.getAuthor());
			LOG.info("" + item.getCategory());
		}
	}

	public static void main(String args[]) throws Exception {
		JsoupTest2();
	}

}
