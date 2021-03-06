package cn.edu.zju.plex.tdd.module;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import cn.edu.zju.plex.tdd.dao.TvShowsDao;
import cn.edu.zju.plex.tdd.entity.TvShows;
import cn.edu.zju.plex.tdd.seg.MyICTCLAS;
import cn.edu.zju.plex.tdd.seg.WordFilter;

public class MeijuTvAnalyzer {

	private static final HashMap<String, TvShows> Meijus = new HashMap<String, TvShows>();
	private static final Logger LOG = Logger.getLogger(MeijuTvAnalyzer.class);
	private static final Pattern FenciResPatt = Pattern
			.compile("([^/]+)/([^\\s]+)");

	static {
		buildMeijuIndex();
	}

	public static String normalizeName(String name) {
		if (name.contains("("))
			name = name.substring(0, name.indexOf('('));
		if (name.contains("（"))
			name = name.substring(0, name.indexOf('（'));

		return name.toLowerCase().replace(" ", "");
	}

	private static void buildMeijuIndex() {
		List<TvShows> tvshows = TvShowsDao.getTvShowList();
		HashSet<String> conflictNames = new HashSet<String>();
		for (TvShows tvshow : tvshows) {
			if (tvshow.isValid()) {
				String names = tvshow.getCname() + "," + tvshow.getEname()
						+ "," + tvshow.getAka() + ","
						+ tvshow.getAka_original();
				names = names.replace(" ", "").replaceAll("[:：，-；;]", ",");
				for (String name : names.split(",")) {
					if (name.length() > 0) {
						name = normalizeName(name);
						TvShows show = Meijus.get(name);
						if (show != null && !show.equals2(tvshow)) {
							conflictNames.add(name);
							// TODO deals with conflicted names
						}
						Meijus.put(name, tvshow);
					}
				}
			}
		}
//		for (String name : conflictNames) {
//			LOG.debug("confict meiju names:" + name);
//			Meijus.remove(name);
//		}
		
		Meijus.remove("null");
	}

	public static int countTvNames(String content) {
		// 美女上错身/aka 不/d 简单/a oh sit!/aka 666 park avenue/aka 逝者之证/aka
		Matcher mat = FenciResPatt.matcher(WordFilter.filterWithCixing(
				MyICTCLAS.fenci(content), MyICTCLAS.SEP, true));
		Set<TvShows> tvSet = new HashSet<TvShows>();
		while (mat.find()) {
			String word = mat.group(1).trim();
			String cixing = mat.group(2).trim();
			// 词性中有英文短语(x)，名词(n)，剧集(aka)
			if (cixing.contains("x") || cixing.contains("n")
					|| cixing.contains("aka")) {
				TvShows tvShow = Meijus.get(word);
				if (tvShow != null) {
					tvSet.add(tvShow);
				}
			}
		}

		return tvSet.size();
	}

	/**
	 * 检测文本所属剧集，成功返回预测的TvShows，否则返回空（如果检测到多个剧集，也返回空）
	 * 
	 * @param content
	 * @return
	 */
	public static TvShows guessTv(String content) {
		if (content == null || content.length() < 1)
			content = "没 有 内 容";
		else
			content = content.toLowerCase().replace(" ", "");

		double minWeight = content.length() > 140 ? 1.5 : 1;
		// 根据剧名进行分类
		HashMap<TvShows, Double> guessList = new HashMap<TvShows, Double>();
		// 美女上错身/aka 不/d 简单/a oh sit!/aka 666 park avenue/aka 逝者之证/aka
		Matcher mat = FenciResPatt.matcher(WordFilter.filterWithCixing(
				MyICTCLAS.fenci(content), MyICTCLAS.SEP, true));
		Map<String, Integer> wordMap = new HashMap<String, Integer>();
		while (mat.find()) {
			String word = mat.group(1).trim();
			String cixing = mat.group(2).trim();
			// 词性中有名词(n)成分或手动倒入的剧集(aka)
			if (cixing.contains("n") || cixing.contains("x")
					|| cixing.contains("aka")) {
				// 短于4的名词权重是0.5，其他是1 考慮到：厨房噩梦/aka 會被分成 厨房/n 噩梦/n
				double weight = cixing.contains("n") && word.length() < 4 ? 0.5
						: 1.0;

				TvShows tvShow = Meijus.get(word);
				if (tvShow != null) {
					if (wordMap.containsKey(word))
						wordMap.put(word, wordMap.get(word) + 1);
					else
						wordMap.put(word, 1);

					weight /= wordMap.get(word) * wordMap.get(word);

					if (guessList.containsKey(tvShow))
						weight += guessList.get(tvShow);
					guessList.put(tvShow, weight);
				} else {
					if (cixing.contains("aka"))
						LOG.debug("no tvshow for word:" + word);
				}
			}
		}

		// 选择权重最大的
		TvShows result = new TvShows();
		double maxW = 0;
		int candidateCount = 0;// 可能的剧集，用来判断这个新闻是不是包含了很多剧集的报道
		for (Entry<TvShows, Double> entry : guessList.entrySet()) {
			if (entry.getValue() > maxW) {
				maxW = entry.getValue();
				result = entry.getKey();
			}
			if (entry.getValue() >= minWeight)
				candidateCount++;
		}
		if (candidateCount > 2 || maxW < minWeight) {
			LOG.debug("found " + candidateCount + " different meiju in \n"
					+ content);
			result = new TvShows();
		}
		return result;
	}

	/**
	 * 把中文美剧名导出，统一为aka词性 MARK 厨房噩梦@@aka 会被 厨房/n 噩梦/n 覆盖！！！
	 * 
	 * @param fname
	 */
	public static void outputMeijuDict(String fname) {
		try {
			PrintWriter pw = new PrintWriter(fname);
			for (String name : Meijus.keySet()) {
				pw.println(name + "@@aka");
				LOG.info("write user dic item:" + name);
			}
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			LOG.warn("fail to output user dict:" + e.getMessage());
		}
	}

	public static void main(String args[]) {
		// LOG.warn("会死循环，当content为空的时候");
		String content = "南国医恋》S02E18《Why Don’t We Get Drunk?》 Lavon雄心勃勃地要把蓝铃镇打造成大学生春假（复活节假）旅游休闲的首选目的地，但是遭遇竞争对手（邻镇）的强力挑战。眼看人气就要丧失殆尽，Lavon不得不让Ruby Jeffries帮他策划一场能吸引眼球的「竞赛」。情绪不好的Zoe想要放松心情，于是答应和Jonah一起参加聚会活动。George看到两人在一起亲密的模样不由心生醋意。Wade请求Lemon和他一起参加Lavon的竞赛，赢得巨额奖金来购买Rammer Jammer酒吧（Wade刚刚听说这家酒吧正挂牌出售）。与此同时，Brick的行为变得十分怪异，每个人都注意到了……他们逼他进行治疗！";
		TvShows show = MeijuTvAnalyzer.guessTv(content);
		System.out.println(show);
		// while (true) {
		// List<RssNews> list = RssNewsDao.getRssNewsToGuessTvShows(100);
		// if (list.size() == 0)
		// break;
		// for (RssNews rssNews : list) {
		// if (rssNews.getContent() == null)
		// continue;
		// rssNews.setTvShows(MeijuTvAnalyzer.guessTv(rssNews.getContent()));
		// RssNewsDao.updateTvShows(rssNews);
		// }
		// }
//		 outputMeijuDict("meijuDict2.txt");
//		buildMeijuIndex();
	}
}
