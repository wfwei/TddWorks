package cn.edu.zju.plex.tdd.lucene.analyzer;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseTokenizer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.util.Version;


/**
 * 封装ICTCLAS的Analyzer，但是只能支持lucene29以前的版本
 * 
 * @author WangFengwei
 */
public final class MyICTCLASAnalyzer extends Analyzer {
	private String readerIncoding = "utf-8"; // default encoding
	@SuppressWarnings("deprecation")
	// have to
	private final Version matchVersion = Version.LUCENE_29; // ICTCLAS只支持到这个版本
	private boolean ignoreCase = true; // if analyzer ignore case
	private Set<Object> stopWords; // stop words

	// 可以在此扩展English stop words和Chinese stop words
	public static final String[] DEFAULT_STOP_WORDS = { "a", "an", "and",
			"are", "as", "at", "be", "but", "by", "for", "if", "in", "into",
			"is", "it", "no", "not", "of", "on", "or", "s", "such", "t",
			"that", "the", "their", "then", "there", "these", "they", "this",
			"to", "was", "will", "with", "我", "我们" };

	public MyICTCLASAnalyzer(Version version) {
		if (this.matchVersion.compareTo(version) < 0) {
			System.out.format(
					"Warn:\tICTCLAS not fully support this version(%s)",
					version.toString());
		}
		this.stopWords = StopFilter.makeStopSet(version, DEFAULT_STOP_WORDS,
				ignoreCase);
	}

	public MyICTCLASAnalyzer(Version ver, String[] stopWords, boolean ignoreCase) {
		if (this.matchVersion != ver) {
			System.err
					.println("ERROR:\tICTCLAS not support this lucene version:"
							+ ver);
		}
		this.stopWords = StopFilter.makeStopSet(ver, stopWords, ignoreCase);
	}

	/** Filters LowerCaseTokenizer with StopFilter. */
	@Override
	public TokenStream tokenStream(String fieldName, Reader reader) {
		try {
			StringWriter writer = new StringWriter();
			IOUtils.copy(reader, writer);
			
			String resultString =  cn.edu.zju.plex.tdd.seg.MyICTCLAS.fenci(writer.toString());
			return new StopFilter(matchVersion, new LowerCaseTokenizer(
					this.matchVersion, new StringReader(resultString)),
					stopWords);
		} catch (IOException e) {
			return null;
		}
	}

	public Version getVer() {
		return matchVersion;
	}

	public String getReaderIncoding() {
		return readerIncoding;
	}

	public void setReaderIncoding(String readerIncoding) {
		this.readerIncoding = readerIncoding;
	}

}
