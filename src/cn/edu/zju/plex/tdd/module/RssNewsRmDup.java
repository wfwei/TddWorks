package cn.edu.zju.plex.tdd.module;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import cn.edu.zju.plex.tdd.dao.DB4Tdd;
import cn.edu.zju.plex.tdd.entity.RssNews;
import cn.edu.zju.plex.tdd.seg.MyICTCLAS;

/**
 * RssNews parser TODO 改成通用类，主要用来计算文本相似度
 * 
 * @author plex
 */
public class RssNewsRmDup {

	private static final Logger LOG = Logger.getLogger(RssNewsRmDup.class);
	private static final int ONE_DAY = 86400000;
	private static final Pattern FenciResPatt = Pattern
			.compile("([^/]+)/([^\\s]+)");

	public static void main(String args[]) {
		int timeLen = 172800000;// two days
		while (true) {
			List<RssNews> list = DB4Tdd.getRssNewsToMerge(timeLen);
			RssNews[] rssNewsToMerge = new RssNews[list.size()];
			list.toArray(rssNewsToMerge);

			if (rssNewsToMerge.length < 2) {
				LOG.info("RssNews merge work temply done");
				break;
			} else {
				RssNewsRmDup.deals(rssNewsToMerge);
				for (RssNews rssNews : rssNewsToMerge)
					if (rssNews.getDelegate() != 0)
						DB4Tdd.updateDelegate(rssNews);
				LOG.info("merge rss news:" + rssNewsToMerge.length);
			}

		}
	}

	/**
	 * 判定是不是重复报道，计算a，b的jaccard距离，小于0.05则认为重复文本 TODO seg words here, remove words
	 * in databases;
	 */
	private static boolean judgeDup(RssNews a, RssNews b) {
		Set<String> as = new HashSet<String>();
		Set<String> bs = new HashSet<String>();
		Set<String> aub = new HashSet<String>();
		Set<String> anb = new HashSet<String>();

		Matcher mat1 = FenciResPatt.matcher(MyICTCLAS.fenci(a.getContent()
				+ a.getTitle()));
		while (mat1.find()) {
			String word = mat1.group(1).trim();
			String cixing = mat1.group(2).trim();
			// 词性中有名词(n)成分或手动倒入的剧集(aka)
			if (cixing.contains("n") || cixing.contains("aka")) {
				as.add(word);
			}
		}

		Matcher mat2 = FenciResPatt.matcher(MyICTCLAS.fenci(a.getContent()
				+ a.getTitle()));
		while (mat2.find()) {
			String word = mat2.group(1).trim();
			String cixing = mat2.group(2).trim();
			// 词性中有名词(n)成分或手动倒入的剧集(aka)
			if (cixing.contains("n") || cixing.contains("aka")) {
				bs.add(word);
			}
		}

		aub.addAll(as);
		aub.addAll(bs);
		anb.addAll(as);
		anb.removeAll(bs);

		double jaccardLen = 1 - anb.size() * 1.0 / aub.size();
		LOG.debug("Jaccard Len:" + jaccardLen + "\t" + a + "\t" + b);
		if (jaccardLen < 0.05)
			return true;
		else
			return false;
	}

	public static void deals(RssNews[] rssnews) {
		RssNews first = rssnews[0];
		int len = rssnews.length;
		int d; // delegate
		for (d = 0; d < len; d++) {
			if (!inOneDay(first, rssnews[d]))
				break;
			if (rssnews[d].getDelegate() <= 0) {
				rssnews[d].setDelegate(rssnews[d].getId());
			}
			for (int j = d + 1; j < len; j++) {

				// 时差超过24小时
				if (!inOneDay(rssnews[d], rssnews[j]))
					break;

				// 不属于一个剧集
				if (rssnews[d].getTvShows().isValid()
						&& rssnews[j].getTvShows().isValid()
						&& !rssnews[d].getTvShows().equals2(
								rssnews[j].getTvShows()))
					continue;

				// 已经设置过
				if (rssnews[j].getDelegate() > 0)
					continue;

				if (judgeDup(rssnews[d], rssnews[j])) {
					if (rssnews[d].getLink().contains("meijufans")
							|| rssnews[d].getContent().length() > rssnews[j]
									.getContent().length()) {
						rssnews[j].setDelegate(rssnews[d].getId());
					} else {
						rssnews[d].setDelegate(rssnews[j].getId());
						d = j;
						if (rssnews[d].getDelegate() <= 0) {
							rssnews[d].setDelegate(rssnews[d].getId());
						}
					}
				}
			}
		}
	}

	/**
	 * 判断两个rssnews是不是24小时内的报道
	 */
	private static boolean inOneDay(RssNews a, RssNews b) {

		double t = Math
				.abs(a.getPubDate().getTime() - b.getPubDate().getTime())
				/ ONE_DAY;
		if (t >= 1)
			return false;
		else
			return true;
	}

}
