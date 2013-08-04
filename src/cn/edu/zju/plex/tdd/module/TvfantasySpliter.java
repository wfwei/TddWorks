package cn.edu.zju.plex.tdd.module;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cn.edu.zju.plex.tdd.dao.RssNewsDao;
import cn.edu.zju.plex.tdd.entity.RssNews;

/**
 * 天涯小筑中简讯的拆分类
 * <p>
 * url-pattern: http://tvfantasy.net/2013/04/12/newsletter[^#]+
 * <p>
 * 提取页面中讲不同剧集的段落，分开存储,分开后的页面会在link末尾加上#和数字编码
 * 
 * @author WangFengwei
 */
public class TvfantasySpliter {
    private static final Logger LOG = Logger.getLogger(TvfantasySpliter.class);

    /**
     * split page, remove origin, save spliters
     * 
     * @param rssNews
     */
    public static void splite(RssNews rssNews) {
	LOG.info("split rssNews" + rssNews);

	ArrayList<RssNews> res = new ArrayList<RssNews>();
	Document doc;
	if (rssNews.getPage() == null || rssNews.getPage().length() == 0)
	    try {
		doc = Jsoup.parse(new URL(rssNews.getLink()), 30000);
	    } catch (Exception e) {
		e.printStackTrace();
		doc = Jsoup.parse("");
	    }
	else
	    doc = Jsoup.parse(rssNews.getPage());
	Element article = doc.getElementsByTag("article").get(0);

	ArrayList<Element> eles = new ArrayList<Element>();

	for (Element e : article.children()) {
	    eles.add(e);
	    e.remove();
	}
	int splitId = 1;
	for (int i = 0; i < eles.size(); i++) {
	    // 因为要复用doc所以每次都要清空article
	    article = doc.getElementsByTag("article").get(0);
	    for (Element e : article.children()) {
		e.remove();
	    }

	    while (i < eles.size() && !eles.get(i).text().contains("●"))
		i++;
	    if (i >= eles.size())
		break;
	    article.appendChild(eles.get(i));
	    while (i + 1 < eles.size() && !eles.get(i + 1).text().contains("●")) {
		i++;
		article.appendChild(eles.get(i));
	    }
	    RssNews newbee = rssNews.clone();
	    newbee.setTitle("");
	    newbee.setPage(doc.html());
	    newbee.setSplitId(splitId++);
	    res.add(newbee);
	}

	if (res.size() > 0) {
	    for (RssNews rn : res) {
		LOG.info("insert rssNews" + rn);
		RssNewsDao.insert(rn);
	    }
	}
	rssNews.setSplitId(-1); // 已经分裂过
	RssNewsDao.updateSplitId(rssNews.getId(), rssNews.getSplitId());
    }

    public static void splitAll(String linkReg) {
	while (true) {
	    List<RssNews> rssNewsToSplit = RssNewsDao.getRssNewsToSplit(30,
		    linkReg);
	    LOG.info("Get " + rssNewsToSplit.size() + " rss items to split...");

	    if (rssNewsToSplit.size() == 0) {
		LOG.info("All done");
		break;
	    } else {
		for (RssNews rssNews : rssNewsToSplit) {
		    splite(rssNews);
		}
	    }
	}
    }

    public static boolean needSplit(RssNews rssNews) {
	String link = rssNews.getLink();
	return rssNews.getSplitId() == 0
		&& link.startsWith("http://tvfantasy.net/")
		&& (link.contains("/newsletter") || link
			.contains("/shows-in-development"));
    }

    public static void main(String args[]) {
	String linkReg = "http://tvfantasy.net/2013/03/29/shows-in-development-newsletter-2013-03-29/";
	splitAll(linkReg);
    }

}
