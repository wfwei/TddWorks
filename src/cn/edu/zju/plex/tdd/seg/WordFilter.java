package cn.edu.zju.plex.tdd.seg;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.LinkedHashSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

/**
 * This class defines the WordFilter class.
 * 
 * @author plex
 */
public class WordFilter {

	public static void main(String args[]) {
		// 自定义stopwords
		String sep = " ";
		// WordFilter.initFromFile("stopwords.txt", "utf-8");
		String simpleText = "hbo 预订  权力的游戏  gameofthrones ";
		String testText = "hbo/x 预订/v 《/w 权力的游戏/aka 》/w （/w gameofthrones/un ）/w 新/a 季/g hbo/x 台/n 预订/v 了/u 《/w 权力的游戏/un 》/w （/w gameofthrones/un ）/w 新/a 季/g 即/v 第四/m 季/q 。/w 其实/d 该剧/r 得到/v 续订/v 也/d 不/d 算/v 是/v 啥/r 新闻/n 了/y …/w 《/w 权力的游戏/un 》/w 第三/m 季/g 收/v 视/g 超越/v 了/u 之前/f 的/u 最/d 好/a 成绩/n ，/w 达/v 440/m 万/m 收/v 视/g 人数/n ，/w 比/p 去年/t 首播/v 高/a 了/u 13%/m 。/w 算/v 上/v 两/m 次/qv 重播/vn ，/w 新/a 季/g 首/m 集总/n 收/v 视/g 人数/n 为/p 670/m 万/m 。/w ";
		System.out.println(simpleFiter(simpleText, sep));
		System.out.println(filterWithCixing(testText, sep, true));
	}

	/**
	 * 过滤掉words中的停用词
	 * 
	 * @param words
	 *            　单词序列字符串
	 * @param sep
	 *            words的单词分隔符
	 */
	public static String simpleFiter(String words, String sep) {
		StringBuilder sbwords = new StringBuilder(words.length());
		for (String word : words.split(sep)) {
			if (word.contains("/")) {
				LOG.warn("word contain abnormal(contain / )");
			}
			if (!stopWords.contains(word) && word.length() < 40
					&& word.length() > 1) {
				sbwords.append(word + sep);
			}
		}
		return sbwords.toString();
	}

	/**
	 * 过滤掉words中的停用词，同时考虑词性
	 * <p >
	 * words中的单词是含有用斜杠分割的词型标注的
	 * <p >
	 * 如“体育运动/n　不由自主/dl 地/ude2 你/rr“
	 * 
	 * @param words
	 *            　单词序列字符串
	 * @param sep
	 *            words的单词分隔符
	 * @param retainCixing
	 *            是否保留词性信息
	 */
	public static String filterWithCixing(String words, String sep,
			boolean retainCixing) {

		StringBuilder sbwords = new StringBuilder(words.length());

		for (String wordAndCixing : words.split(sep)) {
			String word = null, cixing = null;
			int sepIdx = wordAndCixing.indexOf('/');
			if (!wordAndCixing.contains("/")) {
				LOG.warn("word not contain cixing information");
				sepIdx = wordAndCixing.length() - 1; // TODO test
			}
			word = wordAndCixing.substring(0, sepIdx);
			cixing = wordAndCixing.substring(sepIdx + 1);
			// 过滤停用词和长度不符合的词
			if (!stopWords.contains(word) && word.length() < 40
					&& word.length() > 1) {

				// 根据词性过滤，保留名词成分(n)，英文字串(x)，和美剧词(aka)
				if (cixing != null && cixing.length() > 0) {
					if (!cixing.contains("u") && !cixing.contains("x")
							&& !cixing.contains("aka")) {
						continue;
					}
				}

				if (retainCixing)
					sbwords.append(wordAndCixing).append(sep);
				else
					sbwords.append(word).append(sep);
			}
		}
		return sbwords.toString();
	}

	public static boolean contains(String word) {
		return stopWords.contains(word);
	}

	/**
	 * stopWordFile的格式要求每个词使用空格或回车间隔(默认文件编码为utf-8)
	 * 
	 * @param stopWordFile
	 */
	private static void initFromFile(String fileName, String fileEncoding) {
		BufferedReader reader = null;
		int line = 0;
		if (fileEncoding == null) {
			fileEncoding = DEFAULT_FILE_ENCODING;
		}
		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(fileName), fileEncoding));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				for (String word : tempString.split(" ")) {
					stopWords.add(word);
				}
				line++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			LOG.info("Total " + line + " stopwords imported!");
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
	}

	private static HashSet<String> stopWords = new LinkedHashSet<String>();
	private static final String DEFAULT_FILE_ENCODING = "UTF-8";
	private static final Logger LOG = Logger.getLogger(WordFilter.class);
	private static final String[] DEFAULT_STOP_WORDS = { "a", "an", "and",
			"are", "as", "at", "be", "but", "by", "for", "if", "in", "into",
			"is", "it", "no", "not", "of", "on", "or", "s", "such", "t",
			"that", "the", "their", "then", "there", "these", "they", "this",
			"to", "was", "will", "with", "我", "我们" };
	static {
		CollectionUtils.addAll(stopWords, DEFAULT_STOP_WORDS);
		initFromFile("stopwords.txt", "utf-8");
	}

}