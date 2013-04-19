import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.edu.zju.plex.tdd.entity.RssNews;

public class Test {
	private static final Logger LOG = Logger.getLogger(Test.class);

	static void JsoupTest() throws Exception {
		ArrayList<RssNews> res = new ArrayList<RssNews>();
		Document doc = Jsoup
				.parse(new URL(
						"http://tvfantasy.net/2013/04/12/newsletter-2013-04-12-hannibal-supernatural-revenge-homeland-tbw-how-and-more/#1"),
						1000);
		Element article = doc.getElementsByTag("article").get(0);

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

	public static void main(String args[]) throws MalformedURLException,
			IOException {
		for(Long a: new ArrayList<Long>()){
			System.out.println(a);
		}
	}

}
