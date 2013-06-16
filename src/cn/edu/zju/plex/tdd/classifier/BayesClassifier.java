package cn.edu.zju.plex.tdd.classifier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import org.apache.log4j.Logger;

import cn.edu.zju.plex.tdd.dao.RssNewsDao;
import cn.edu.zju.plex.tdd.entity.RssNews;
import cn.edu.zju.plex.tdd.entity.TvShows;
import cn.edu.zju.plex.tdd.seg.MyICTCLAS;
import cn.edu.zju.plex.tdd.seg.WordFilter;

/**
 * 1. 分词后没有过滤停用词，而是使用了词性信息(n,aka)
 * <p >
 * TODO 1. 使用tf-idf; 2. 使用自己写的分词器进行分析，做一个美剧词库 3. 每个类的大小差距太大
 * 
 * @author WangFengwei
 * 
 */
public class BayesClassifier {
	final String Dir = "resources/bayes/";
	final String Word2IdFile = Dir + "Word2ID";
	final String modelFile = Dir + "model";
	static final String SEP = "\t";
	static final String KONGGE = " ";
	static final Charset UTF8 = Charset.forName("UTF-8");
	private static final Logger LOG = Logger.getLogger(BayesClassifier.class);

	HashMap<String, Long> word2ID = new HashMap<String, Long>();
	HashMap<String, HashMap<Long, Double>> classDict = new HashMap<String, HashMap<Long, Double>>();
	HashMap<String, Double> classPrior = new HashMap<String, Double>();

	private double laplace_smooth = Math.E;

	public static void main(String[] args) {
		BayesClassifier classifier = new BayesClassifier();
		// classifier.buildModel();
		classifier.loadModel();
		classifier.calcTrainError();
	}

	public String predict(String doc) {
		String words = MyICTCLAS.fenci(doc.replaceAll("(<.*?>)|(\\s)", "")
				.toLowerCase());
		HashSet<String> wordSet = new HashSet<String>();
		for (String word : WordFilter.filterWithCixing(words, KONGGE, false)
				.split(KONGGE))
			wordSet.add(word);

		String tarClass = "NULL";
		double maxSim = Double.MIN_VALUE;
		for (String className : classDict.keySet()) {
			double sim = 1d;
			// double sim = Math.log(classPrior.get(className));
			HashMap<Long, Double> classWords = classDict.get(className);
			laplace_smooth = 1.0d / wordSet.size();
			for (String word : wordSet) {
				Double freq = classWords.get(word2ID.get(word));
				if (freq != null) {
					// System.out.println(word);
					sim += Math.log(freq + laplace_smooth);
				} else
					sim += Math.log(laplace_smooth);
			}
			sim = Math.exp(sim);
			if (sim > maxSim) {
				maxSim = sim;
				tarClass = className;
			}
		}
		return tarClass;
	}

	public void loadModel() {
		word2ID.clear();
		classDict.clear();
		LOG.info("Loading Model");
		try {
			/* <word, id> */
			BufferedReader word2IDReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(Word2IdFile),
							UTF8));

			/* <class, prior, List<P(word|Class)>> */
			BufferedReader modelReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(modelFile), UTF8));

			String line;
			// read word2id
			while ((line = word2IDReader.readLine()) != null) {
				String[] wdid = line.split(SEP);
				if (wdid.length == 2) {
					word2ID.put(wdid[0], Long.valueOf(wdid[1]));
				}
			}
			LOG.info("\tLoaded " + word2ID.size() + " words");
			// read model
			while ((line = modelReader.readLine()) != null) {
				String[] classInfo = line.split(SEP);
				if (classInfo.length > 2) {
					String name = classInfo[0];
					Double prior = Double.valueOf(classInfo[1]);
					if (!classDict.containsKey(name)) {
						classDict.put(name, new HashMap<Long, Double>());
					}
					classPrior.put(name, prior);
					HashMap<Long, Double> wordFreqs = classDict.get(name);
					for (int i = 3; i < classInfo.length; i += 2) {
						wordFreqs.put(Long.valueOf(classInfo[i - 1]),
								Double.valueOf(classInfo[i]));
					}
				}
			}
			LOG.info("\tLoaded " + classDict.size() + " classes");
			word2IDReader.close();
			modelReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * select meiju_id, meiju_cname, meiju_ename, title, content from rss_news
	 * where meiju_id!='' into outfile 'd:\\data.csv' FIELDS TERMINATED BY
	 * 'F#G#F' LINES TERMINATED BY 'F#G#F';
	 */
	public void buildModel() {
		LOG.info("Building Bayesian Model");
		/* 存放tvShows和属于该tvShow类别的词频 */
		HashMap<String, HashMap<Long, Double>> classDict = new HashMap<String, HashMap<Long, Double>>();
		/* 存放tvShows和属于该tvShow类别的文档个数 */
		HashMap<String, Integer> classCount = new HashMap<String, Integer>();

		int offset = 0, count = 100, totDocs = 0;
		Long wordId = 0L;
		while (true) {
			List<RssNews> results = RssNewsDao.getRssNewsToTrainClassifier(
					offset, count);
			if (results.size() > 0) {
				offset += results.size();
				totDocs += results.size();
				for (RssNews rssnews : results) {
					String tvshow = rssnews.getTvShows().toString();
					if (!classCount.containsKey(tvshow)) {
						classCount.put(tvshow, 1);
						classDict.put(tvshow, new HashMap<Long, Double>());
					} else
						classCount.put(tvshow, classCount.get(tvshow) + 1);
					HashMap<Long, Double> wordList = classDict.get(tvshow);

					String words = MyICTCLAS
							.fenci((rssnews.getTitle() + rssnews.getContent())
									.replaceAll("(<.*?>)|(\\s)", "")
									.toLowerCase());

					for (String word : WordFilter.filterWithCixing(words,
							KONGGE, false).split(KONGGE)) {
						if (!word2ID.containsKey(word)) {
							word2ID.put(word, wordId);
							wordId++;
						}
						long id = word2ID.get(word);
						Double cnt = wordList.get(id);
						if (cnt == null)
							wordList.put(id, 1d);
						else
							wordList.put(id, cnt + 1);
					}
				}
			} else
				break;
		}
		LOG.info("Saving Model");
		try {
			/* <word, id> */
			Writer word2IDWriter = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(Word2IdFile), UTF8));
			/* <class, prior, List<P(word|Class)>> */
			Writer modelWriter = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(modelFile), UTF8));

			// output word2id
			for (Entry<String, Long> ent : word2ID.entrySet()) {
				word2IDWriter.append(ent.getKey()).append(SEP);
				word2IDWriter.append(String.valueOf(ent.getValue())).append(
						"\n");
			}
			word2IDWriter.flush();
			// output classinfo
			for (String tvshow : classDict.keySet()) {
				// 计算类别的先验概率以及每个类别下每个词的概率
				double classPrior = Double.valueOf(classCount.get(tvshow))
						/ totDocs;
				modelWriter.append(tvshow.toString()).append(SEP)
						.append(String.valueOf(classPrior));
				HashMap<Long, Double> wordList = classDict.get(tvshow);
				double wordCount = 0d;
				for (Double cnt : wordList.values())
					wordCount += cnt;
				for (Long wid : wordList.keySet()) {
					modelWriter.append(SEP).append(String.valueOf(wid));
					modelWriter.append(SEP).append(
							String.valueOf(wordList.get(wid) / wordCount));
				}
				modelWriter.append("\n");
			}
			modelWriter.flush();
			modelWriter.close();
			word2IDWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void calcTrainError() {
		LOG.info("Testing Bayesian Model");

		int offset = 0, count = 100, totDocs = 0;
		int err = 0;
		while (true) {
			List<RssNews> results = RssNewsDao.getRssNewsToTrainClassifier(
					offset, count);
			if (results.size() > 0) {
				offset += results.size();
				totDocs += results.size();
				for (RssNews rssnews : results) {
					TvShows tvshow = rssnews.getTvShows();
					String result = this.predict(rssnews.getTitle()
							+ rssnews.getContent());
					if (!result.contains(tvshow.getTvdbid())) {
						err++;
						// System.out.println("WRONG");
						// System.out.println(tvshow);
						// System.out.println(result);
						// System.out.println("id:\t" + rssnews.getId());
						// System.out.println("title:\t" + rssnews.getTitle());
						// System.out.println("content:\n"
						// + rssnews.getContent().replaceAll("<.*?>", ""));
						// System.out.println();
					}
				}
			} else
				break;
		}

		LOG.info("Train Error:" + err + " / " + totDocs);

	}
}
