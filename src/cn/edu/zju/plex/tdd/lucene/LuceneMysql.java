package cn.edu.zju.plex.tdd.lucene;

import java.io.File;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import cn.edu.zju.plex.tdd.dao.DB4Tdd;
import cn.edu.zju.plex.tdd.entity.RssNews;
import cn.edu.zju.plex.tdd.lucene.analyzer.MyICTCLASAnalyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

/**
 * 
 * @描述 对数据库数据进行搜索
 */
public class LuceneMysql {

	private static final Version LUCENE_VERSION = Version.LUCENE_36;
	private static final String INDEX_DIR = "/home/plex/wksp/tmp/index";
	private static Analyzer analyzer = new MyICTCLASAnalyzer(LUCENE_VERSION);
	private static Directory directory = null;
	private static String[] fieldNames = { "doccontent", "docid" };

	/**
	 * 建立索引
	 * 
	 * @param args
	 */
	public static void index(int count) throws Exception {
		directory = FSDirectory.open(new File(INDEX_DIR));
		File indexFile = new File(INDEX_DIR);
		if (!indexFile.exists()) {
			indexFile.mkdirs();
		}

		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(
				LUCENE_VERSION, analyzer);
		IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
		int startIdx = 0, countPer = 200;
		List<RssNews> rssNews2idx = null;
		do {
			rssNews2idx = DB4Tdd.getItemToIndex(startIdx, countPer);
			for (RssNews rssNews : rssNews2idx) {
				Document document = new Document();
				String validDocContent = rssNews.getTitle()
						+ (rssNews.getSummary().length() > rssNews
								.getDescription().length() ? rssNews
								.getSummary() : rssNews.getDescription());
				String docId = rssNews.getId() + "";
				if (validDocContent.length() < 10) {
					System.out.format("Warn:\tdocId:%s\n\tdocContent:%s\n",
							validDocContent, docId);
				}
				// TODO ?? 目前mahout根据lucene建立索引只能用第一个Field
				document.add(new Field(fieldNames[0], validDocContent,
						Field.Store.YES, Field.Index.ANALYZED,
						Field.TermVector.YES));
				document.add(new Field(fieldNames[1], docId, Field.Store.YES,
						Field.Index.ANALYZED, Field.TermVector.YES));
				indexWriter.addDocument(document);
			}
			startIdx += countPer;
			if (rssNews2idx.size() == countPer && startIdx < count) {
				System.out.println("Another loop from " + startIdx);
			} else {
				System.out.println("Add ducuments over~");
				break;
			}
		} while (true);

		indexWriter.commit();
		indexWriter.close();
	}

	/**
	 * 关键字查询
	 * 
	 * @param str
	 * @throws Exception
	 */
	public static void search(String str) throws Exception {
		IndexReader idxReader = IndexReader.open(FSDirectory.open(new File(
				INDEX_DIR)));
		IndexSearcher indexSearcher = new IndexSearcher(idxReader);

		QueryParser queryParser = new QueryParser(LUCENE_VERSION,
				fieldNames[1], analyzer);
		Query query = queryParser.parse(str);
		TopScoreDocCollector topScoreDocCollector = TopScoreDocCollector
				.create(10, true);
		indexSearcher.search(query, topScoreDocCollector);
		ScoreDoc[] scoreDocs = topScoreDocCollector.topDocs().scoreDocs;
		for (int i = 0; i < scoreDocs.length; i++) {
			Document doc = indexSearcher.doc(scoreDocs[i].doc);
			System.out.println(doc.get(fieldNames[0]));
			System.out.println(doc.get(fieldNames[1]));
			System.out.println("-----------------------------------------");
		}
	}

	public static void main(String[] args) {
		try {
			LuceneMysql.index(20);
			LuceneMysql.search("音乐");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}