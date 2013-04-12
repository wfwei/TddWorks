package cn.edu.zju.plex.tdd.seg;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.util.Version;

import cn.edu.zju.plex.tdd.lucene.LuceneUtil;
import cn.edu.zju.plex.tdd.lucene.analyzer.MyICTCLASAnalyzer;

@SuppressWarnings("unused")
// ʹ���˷�����ƣ�һЩ��������ʱ���ܲŻ��õ���������ǰ����
public class LuceneSeg {

	// �������Ͳ���Ҫ�����ˣ�Ϊ����ϰʹ��Java������ƣ�����֮
	// private static HashMap<String, Class<?>> reflectMap = new HashMap<String,
	// Class<?>>();
	// static {
	// reflectMap.put("SmartChineseAnalyzer", SmartChineseAnalyzer.class);
	// reflectMap.put("StandardAnalyzer", StandardAnalyzer.class);
	// }

	private static HashMap<String, String[]> reflectMap = new HashMap<String, String[]>();

	static {
		reflectMap.put("SmartChineseAnalyzer", new String[] {
				"org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer",
				"LUCENE_36" });
		reflectMap.put("StandardAnalyzer", new String[] {
				"org.apache.lucene.analysis.standard.StandardAnalyzer",
				"LUCENE_36" });
		reflectMap.put("StopAnalyzer", new String[] {
				"org.apache.lucene.analysis.StopAnalyzer", "LUCENE_36" });
		reflectMap.put("WhitespaceAnalyzer", new String[] {
				"org.apache.lucene.analysis.WhitespaceAnalyzer", "LUCENE_29" });
		reflectMap.put("ICTCLASAnalyzer", new String[] {
				"cn.edu.zju.plex.tdd.lucene.analyzer.MyICTCLASAnalyzer",
				"LUCENE_29" });
	}

	public static String getWords(String content, String analyzerName,
			String analyzerVersion) {
		try {
			Class<?> CurAnalyzer = Class.forName(analyzerName);
			Version CurVersion = Version.valueOf(analyzerVersion);
			Analyzer analyzer = (Analyzer) CurAnalyzer.getConstructor(
					Version.class).newInstance(CurVersion);
			TokenStream tokenStream = analyzer.tokenStream("splex",
					new StringReader(content));
			return LuceneUtil.parseTokenStream(tokenStream, " ", CurVersion)
					.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void showWords(String content, String analyzerName,
			String analyzerVersion) {
		try {
			Class<?> CurAnalyzer = Class.forName(analyzerName);
			Version CurVersion = Version.valueOf(analyzerVersion);
			Analyzer analyzer = (Analyzer) CurAnalyzer.getConstructor(
					Version.class).newInstance(CurVersion);
			TokenStream tokenStream = analyzer.tokenStream("splex",
					new StringReader(content));

			LuceneUtil.displayTokenStream(tokenStream, " ", CurVersion);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String content = "a ������ ���� ���� ��򵥲� ���� ���� ==��";

		// String analyzerName = reflectMap.get("StopAnalyzer")[0];
		// String analyzerVersion = reflectMap.get("StopAnalyzer")[1];

		// String analyzerName = reflectMap.get("SmartChineseAnalyzer")[0];
		// String analyzerVersion = reflectMap.get("SmartChineseAnalyzer")[1];

		// String analyzerName = reflectMap.get("WhitespaceAnalyzer")[0];
		// String analyzerVersion = reflectMap.get("WhitespaceAnalyzer")[1];

		// String analyzerName = reflectMap.get("StandardAnalyzer")[0];
		// String analyzerVersion = reflectMap.get("StandardAnalyzer")[1];

		String analyzerName = reflectMap.get("ICTCLASAnalyzer")[0];
		String analyzerVersion = reflectMap.get("ICTCLASAnalyzer")[1];

		if (analyzerName == null) {
			System.err.println("analyzer not found!");
			return;
		}
		// test LuceneSeg.showWords
		LuceneSeg.showWords(content, analyzerName, analyzerVersion);
		// test LuceneSeg.getWords
		System.out.println(LuceneSeg.getWords(content, analyzerName,
				analyzerVersion));
	}
}
