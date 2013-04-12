package cn.edu.zju.plex.tdd.seg;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;

import org.apache.commons.collections.CollectionUtils;

/**
 * This class defines the WordFilter class.
 * 
 * @author plex
 */

public class WordFilter {

	private static HashSet<String> stopWords = new LinkedHashSet<String>(); // Stop
																			// words
	private static boolean initialized = false;
	public static final String DEFAULT_FILE_ENCODING = "UTF-8";

	// 可以在此扩展English stop words和Chinese stop words
	public static final String[] DEFAULT_STOP_WORDS = { "a", "an", "and",
			"are", "as", "at", "be", "but", "by", "for", "if", "in", "into",
			"is", "it", "no", "not", "of", "on", "or", "s", "such", "t",
			"that", "the", "their", "then", "there", "these", "they", "this",
			"to", "was", "will", "with", "我", "我们" };

	/**
	 * stopWordFile的格式要求每个词使用空格或回车间隔(默认文件编码为utf-8)
	 * 
	 * @param stopWordFile
	 */
	public static void initFromFile(String fileName, String fileEncoding) {
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
			initialized = true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			System.out.format("Total(%d) words imported!\n", line);
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
	}

	/**
	 * 过滤掉words中的停用词
	 * <p>
	 * words中的单词可能是含有用斜杠分割的词型标注的,如“体育运动/n　不由自主/dl 地/ude2 你/rr“ 考虑这种情况
	 * 
	 * @param words
	 *            　单词序列字符串
	 * @param sep
	 *            words的单词分隔符
	 * @return
	 */
	public static String filterStopWords(String words, String sep) {
		if (!initialized) {
			System.out.println("WARN:\tUse default stopwords");
			CollectionUtils.addAll(stopWords, DEFAULT_STOP_WORDS);
		}
		StringBuilder sbwords = new StringBuilder(words.length());
		for (String word : Arrays.asList(words.split(sep))) {
			if (word.contains("/")) {
				word = word.substring(0, word.indexOf('/'));
			}
			if (!stopWords.contains(word)) {
				sbwords.append(word + sep);
			}
		}
		return sbwords.toString();
	}

	public static void main(String args[]) {
		// 自定义stopwords
		WordFilter.initFromFile("stopwords.txt", "utf-8");
		System.out
				.println(filterStopWords(
						"总的说来/c 经过/p 我/rr 从小/d 就/d 不由自主/dl 地/ude2 你/rr 喜欢/vi 修/v 东西/n 吗/y ？/ww 你/rr 喜欢/vi 体育运动/n 吗/y ？",
						" "));
	}
}