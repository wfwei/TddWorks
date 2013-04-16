package cn.edu.zju.plex.tdd.main;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.edu.zju.plex.tdd.dao.DB4Tdd;
import cn.edu.zju.plex.tdd.entity.RssNews;
import cn.edu.zju.plex.tdd.parser.ParserFactory;
//import cn.edu.zju.plex.tdd.seg.IctclasSeg;
import cn.edu.zju.plex.tdd.seg.MyICTCLAS;

/**
 * 天涯小筑中简讯的拆分类
 * <p>
 * url-pattern: http://tvfantasy.net/2013/04/12/newsletter*
 * <p>
 * 提取页面中讲不同剧集的段落，分开存储 TODO 数据库操作限制，只能单线程
 * 
 * @author WangFengwei
 */
public class TvfantasySpliter implements Runnable {

	private static final Logger LOG = Logger.getLogger(TvfantasySpliter.class);
	private static final Long ONE_HOUR = 3600000L;

	private String linkReg;

	public TvfantasySpliter(String linkReg) {
		this.linkReg = linkReg;
	}

	List<RssNews> splite(RssNews rssNews) {
		
		ArrayList<RssNews> res = new ArrayList<RssNews>();
		Document doc = Jsoup.parse(rssNews.getPage());
		Element article = doc.getElementsByTag("article").get(0);

		ArrayList<Element> eles = new ArrayList<Element>();
		
		for (Element e : article.children()) {
			eles.add(e);
			e.remove();
		}
	
		for (int i = 0; i < eles.size();i++) {
			article = doc.getElementsByTag("article").get(0);
			for (Element e : article.children()) {
				e.remove();
			}
			while(i<eles.size() && !eles.get(i).text().contains("●")){
				i++;
			}
			article.appendChild(eles.get(i));
			while(i+1<eles.size() && !eles.get(i+1).text().contains("●")){
				i++;
				article.appendChild(eles.get(i));
			}
			RssNews newbee = rssNews.clone();
			newbee.setPage(doc.html());
			newbee.setId(-1);
			newbee.setLink(newbee.getLink()+"#"+i);
			res.add(newbee);
		}
		
		return res;
	}

	@Override
	public void run() {
		while (true) {
			List<RssNews> rssNewsToSplit = DB4Tdd
					.getRssNewsToSplit(30, linkReg);
			LOG.info("Get " + rssNewsToSplit.size() + " rss items to split...");

			if (rssNewsToSplit.size() == 0) {
				LOG.info("Temporally done, going to sleep a while");
				try {
					Thread.sleep(ONE_HOUR * 10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				for (RssNews rssNews : rssNewsToSplit) {
					List<RssNews> moreRssNews = splite(rssNews);
					for (RssNews rn : moreRssNews) {
						DB4Tdd.insertRssNews(rn);
					}
					DB4Tdd.delete(rssNews);
				}
			}
			
			break;
		}
	}

	public static void main(String args[]) {
		for (int i = 0; i < 1; i++) {
			new Thread(new TvfantasySpliter(
					"http://tvfantasy.net/.{10}/newsletter"),
					"TyFantasy-Spliter-" + i).start();
			LOG.info("RssNewsSpiter-" + i + " started");
		}
	}

}
