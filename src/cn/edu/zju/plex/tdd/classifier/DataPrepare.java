package cn.edu.zju.plex.tdd.classifier;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.zju.plex.tdd.dao.RssNewsDao;
import cn.edu.zju.plex.tdd.entity.RssNews;
import cn.edu.zju.plex.tdd.seg.MyICTCLAS;

public class DataPrepare {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("unchecked")
	public void readTrainingSet() throws IOException, FileNotFoundException {
		int offset = 0, count = 200;

		ObjectOutputStream articleWriter = new ObjectOutputStream(
				new FileOutputStream("Articles"));
		ObjectInputStream articleReader = new ObjectInputStream(
				new FileInputStream("Articles"));

		Writer recordWriter = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream("record"), "UTF-8"));
		Writer mapWriter = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream("Word2ID"), "UTF-8"));

		HashMap<String, Integer> wordCount = new HashMap<String, Integer>();
		HashMap<String, Integer> wordMapper = new HashMap<String, Integer>();
		HashMap<String, Integer> docCount = new HashMap<String, Integer>();
		int docTotal = 0;

		while (true) {
			List<RssNews> results = RssNewsDao.getRssNewsToTrainClassifier(
					offset, count);
			if (results.size() > 0) {
				for (RssNews rssnews : results) {
					docTotal++;
					long docId = rssnews.getId();
					long wdcnt = 0;
					String words = MyICTCLAS.fenci(rssnews.getTitle()
							+ rssnews.getContent());
					wordCount.clear();
					for (String word : words.split(" ")) {
						// 单个文件中的词频信息
						if (wordCount.containsKey(word)) {
							wordCount.put(word, wordCount.get(word) + 1);
						} else {
							wordCount.put(word, 1);
							// 文档词频信息
							if (docCount.containsKey(word)) {
								docCount.put(word, wordCount.get(word) + 1);
							} else
								docCount.put(word, 1);
						}
						wdcnt++;
					}
					articleWriter.writeLong(docId); // 文档id
					articleWriter.writeLong(wdcnt); // 文档中词语总数
					articleWriter.writeUnshared(wordCount); // 每个词及其个数
				}
			} else
				break;
		}
		int id = 0;
		for (String word : docCount.keySet()) {
			mapWriter.append(word).append("\t" + id + "\n");
			wordMapper.put(word, id);
			id++;
		}
		mapWriter.flush();
		while (true) {
			try {
				Long docId = articleReader.readLong();
				Long wdcnt = articleReader.readLong();
				wordCount = (HashMap<String, Integer>) articleReader
						.readObject();
				// 计算tf-idf
				for (Map.Entry<String, Integer> ent : wordCount.entrySet()) {
					String word = ent.getKey();
					int docNum = docCount.get(word);
					double tf = 1.0d * ent.getValue() / wdcnt;
					double df = 1.0d * docNum / docTotal;
					double tfidf = tf / df; // TODO

					recordWriter.append(docId + "\t"
							+ wordMapper.get(ent.getKey()) + "\t"
							+ String.valueOf(tfidf) + "\n");
				}
				recordWriter.flush();
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}

	}

}
