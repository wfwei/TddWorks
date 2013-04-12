package cn.edu.zju.plex.tdd.lucene;

import java.io.IOException;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.Version;

/**
 * TODO DIVIDE_VERSION前后不兼容的地方在于读取词性的方式上，目前没有彻底弄清楚，不处理词性
 * 
 * @author WangFengwei
 * 
 */
@SuppressWarnings({ "deprecation", "unused" })
public class LuceneUtil {
	// lucene29之后改动了解析tokenStream的方式
	// @see http://issues.apache.org/jira/browse/LUCENE-1422
	static final Version DIVIDE_VERSION = Version.LUCENE_29;

	public static StringBuilder parseTokenStream(TokenStream tokenStream,
			String seprator, Version version) throws IOException {
		StringBuilder parsedRes = new StringBuilder();

		// boolean isNewVersion = version.compareTo(DIVIDE_VERSION) > 0 ? true
		// : false;
		CharTermAttribute charTerm = tokenStream
				.getAttribute(CharTermAttribute.class);
		while (tokenStream.incrementToken()) {
			parsedRes.append(charTerm.toString()).append(seprator);
		}
		// old way
		// TermAttribute termAttribute = tokenStream
		// .getAttribute(TermAttribute.class);
		// while (tokenStream.incrementToken()) {
		// parsedRes.append(termAttribute.term()).append(seprator);
		// }
		return parsedRes;
	}

	public static void displayTokenStream(TokenStream tokenStream,
			String seprator, Version version) throws IOException {
		CharTermAttribute charTerm = tokenStream
				.getAttribute(CharTermAttribute.class);
		while (tokenStream.incrementToken()) {
			System.out.print(charTerm.toString() + seprator);
		}
		System.out.println("\n");
	}

}
