package cn.edu.zju.plex.tdd.classifier;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import cn.edu.zju.plex.tdd.dao.RssNewsDao;
import cn.edu.zju.plex.tdd.entity.RssNews;
import cn.edu.zju.plex.tdd.entity.TvShows;
import cn.edu.zju.plex.tdd.seg.MyICTCLAS;

/**
 * TODO 1. 停用词 2. 完成分类
 * 
 * @author WangFengwei
 * 
 */
public class BayesClassifier {
	final String Dir = "resources/bayes/";
	final String Word2IdFile = Dir + "Word2ID";
	final String modelFile = Dir + "model";

	public static void main(String[] args) {
		BayesClassifier classifier = new BayesClassifier();
		classifier.buildModel();

	}

	public void buildModel() {

		HashMap<String, Long> word2ID = new HashMap<String, Long>();
		HashMap<Long, String> ID2Word = new HashMap<Long, String>();

		/* 存放tvShows和属于该tvShow类别的词频 */
		HashMap<TvShows, HashMap<Long, Double>> classDict = new HashMap<TvShows, HashMap<Long, Double>>();
		/* 存放tvShows和属于该tvShow类别的文档个数 */
		HashMap<TvShows, Integer> classCount = new HashMap<TvShows, Integer>();

		int offset = 0, count = 100, totDocs = 0;
		Long wordId = 0L;
		while (true) {
			List<RssNews> results = RssNewsDao.getRssNewsToTrainClassifier(
					offset, count);
			if (results.size() > 0) {
				offset += results.size();
				totDocs += results.size();
				for (RssNews rssnews : results) {
					TvShows tvshow = rssnews.getTvShows();
					if (!classCount.containsKey(tvshow)) {
						classCount.put(tvshow, 1);
						classDict.put(tvshow, new HashMap<Long, Double>());
					} else
						classCount.put(tvshow, classCount.get(tvshow) + 1);
					HashMap<Long, Double> wordList = classDict.get(tvshow);
					String words = MyICTCLAS.fenci(rssnews.getTitle()
							+ rssnews.getContent().replaceAll("<.*?>", ""));
					for (String word : words.split(" ")) {
						word = word.trim();
						if (word.length() < 2)
							continue;
						if (!word2ID.containsKey(word)) {
							word2ID.put(word, wordId);
							ID2Word.put(wordId, word);
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

		try {
			/* <word, id> */
			Writer word2IDWriter = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(Word2IdFile), "UTF-8"));
			/* <class, prior, List<P(word|Class)>> */
			Writer modelWriter = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(modelFile), "UTF-8"));

			// output word2id
			for (Entry<String, Long> ent : word2ID.entrySet()) {
				word2IDWriter.append(ent.getKey()).append("\t")
						.append(String.valueOf(ent.getValue())).append("\n");
			}
			word2IDWriter.flush();
			// output classinfo
			for (TvShows tvshow : classDict.keySet()) {
				// 计算类别的先验概率以及每个类别下每个词的概率
				double classPrior = classCount.get(tvshow) * 1.0 / totDocs;
				modelWriter.append(tvshow.getCname()).append("\t")
						.append(String.valueOf(classPrior));
				HashMap<Long, Double> wordList = classDict.get(tvshow);
				double wordCount = 0d;
				for (Double cnt : wordList.values())
					wordCount += cnt;
				for (Long wid : wordList.keySet()) {
					modelWriter
							.append("\t")
							.append(String.valueOf(wid))
							.append("\t")
							.append(String.valueOf(wordList.get(wid)
									/ wordCount));
				}
				modelWriter.append("\n");
			}
			modelWriter.flush();
			modelWriter.close();
			word2IDWriter.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
