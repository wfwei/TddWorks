package cn.edu.zju.plex.tdd.tools;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import open56.Open56PicFecher;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class VideoThumbnail {

	private static final Logger LOG = Logger.getLogger(VideoThumbnail.class);
	private static final LinkedHashMap<String, Method> VideoUrls;
	private static final Pattern JsImgPatt = Pattern
			.compile("pic[:\\s=]+['\"](.*?)['\"]");
	static {
		VideoUrls = new LinkedHashMap<String, Method>();
		try {
			VideoUrls.put("v.youku.com",
					VideoThumbnail.class.getMethod("getYouku", String.class));
			VideoUrls.put("tudou.com",
					VideoThumbnail.class.getMethod("getTudou", String.class));
			VideoUrls.put("56.com",
					VideoThumbnail.class.getMethod("get56", String.class));
			VideoUrls.put("video.sina.com",
					VideoThumbnail.class.getMethod("getSina", String.class));
			VideoUrls.put("letv.com",
					VideoThumbnail.class.getMethod("getLetv", String.class));
			VideoUrls.put("v.qq.com",
					VideoThumbnail.class.getMethod("getQQ", String.class));
			// TODO implement
			// VideoUrls.put("tv.sohu.com",
			// VideoThumbnail.class.getMethod("getSohu", String.class));
			// VideoUrls.put("v.ku6.com",
			// VideoThumbnail.class.getMethod("getKu6", String.class));
			// VideoUrls.put("v.163.com",
			// VideoThumbnail.class.getMethod("get163", String.class));
			// VideoUrls.put("v.ifeng.com",
			// VideoThumbnail.class.getMethod("getIfeng", String.class));
			// VideoUrls.put("iqiyi.com",
			// VideoThumbnail.class.getMethod("getIqiyi", String.class));
			// VideoUrls.put("6.cn",
			// VideoThumbnail.class.getMethod("get6", String.class));
		} catch (Exception e) {
			LOG.warn("Fail to initialize VideoThumbnail");
			LOG.warn(e.getMessage());
			e.printStackTrace();
		}
	}

	public static String getVideoThumbnail(String vurl) {
		String thumbnail = null;
		try {
			for (Entry<String, Method> entry : VideoUrls.entrySet()) {
				if (vurl.indexOf(entry.getKey()) != -1) {
					thumbnail = (String) entry.getValue().invoke(
							VideoThumbnail.class, vurl);
					break;
				}
			}
		} catch (Exception e) {
			LOG.warn("Fail to get video thumbnail for url:" + vurl);
			LOG.warn(e.getMessage());
			thumbnail = null;
		}
		return thumbnail;
	}

	public static String getYouku(String url) throws IOException {
		String picUrl = null;
		try {
			Document doc = Jsoup.connect(url).timeout(50000)
					.data("query", "Java").userAgent("Mozilla")
					.cookie("auth", "token").get();
			String fullUrl = doc.getElementById("s_baidu1").attr("href");
			int startIdx = fullUrl.indexOf("pic=");
			if (startIdx >= 0) {
				startIdx += 4;
				picUrl = fullUrl.substring(startIdx);
			}
		} catch (Exception e) {
			LOG.warn("Fail to get thumbnail for url:" + url);
			picUrl = null;
		}
		return picUrl;
	}

	public static String getTudou(String url) throws IOException {
		String picUrl = null;
		try {
			Document doc = Jsoup.connect(url).timeout(50000)
					.data("query", "Java").userAgent("Mozilla")
					.cookie("auth", "token").get();

			for (Element scriptEle : doc.getElementsByTag("script")) {
				String scriptData = scriptEle.data();
				Matcher match = JsImgPatt.matcher(scriptData);
				if (match.find()) {
					picUrl = match.group(1);
					break;
				}
			}

		} catch (Exception e) {
			LOG.warn("Fail to get thumbnail for url:" + url);
			picUrl = null;
		}
		return picUrl;
	}

	public static String get56(String url) throws IOException {
		return Open56PicFecher.getPic(url);
	}

	public static String getSina(String url) throws IOException {
		String picUrl = null;
		try {
			Document doc = Jsoup.connect(url).timeout(50000)
					.data("query", "Java").userAgent("Mozilla")
					.cookie("auth", "token").get();

			for (Element scriptEle : doc.getElementsByTag("script")) {
				String scriptData = scriptEle.data();
				Matcher match = JsImgPatt.matcher(scriptData);
				if (match.find()) {
					picUrl = match.group(1);
					break;
				}
			}

		} catch (Exception e) {
			LOG.warn("Fail to get thumbnail for url:" + url);
			picUrl = null;
		}
		return picUrl;
	}

	// TODO 电视剧频道不能不能解析 http://tv.letv.com/zt/tangchaohaonanren/index.shtml
	public static String getLetv(String url) throws IOException {
		String picUrl = null;
		try {
			Document doc = Jsoup.connect(url).timeout(50000)
					.data("query", "Java").userAgent("Mozilla")
					.cookie("auth", "token").get();

			for (Element scriptEle : doc.getElementsByTag("script")) {
				String scriptData = scriptEle.data();
				Matcher match = JsImgPatt.matcher(scriptData);
				if (match.find()) {
					picUrl = match.group(1);
					break;
				}

			}

		} catch (Exception e) {
			LOG.warn("Fail to get thumbnail for url:" + url);
			picUrl = null;
		}
		return picUrl;
	}

	/**
	 * 每个模块不一样，目前考虑两种解析方式
	 * <p>
	 * 1. http://v.qq.com/cover/a/a6aflrbgxqhtvv9.html?vid=y0012336fca 通过vid解析
	 * 2. http://v.qq.com/cover/8/8wonxzy5pvo8ao3.html 通过pic :
	 */
	public static String getQQ(String url) throws IOException {
		String picUrl = null;
		try {
			Document doc = Jsoup.connect(url).timeout(50000)
					.data("query", "Java").userAgent("Mozilla")
					.cookie("auth", "token").get();
			if (url.contains("vid=")) {
				String vid = url.substring(url.indexOf("vid") + 4);
				picUrl = doc.getElementById("li_" + vid)
						.getElementsByTag("img").get(0).attr("_src");
			} else {
				for (Element scriptEle : doc.getElementsByTag("script")) {
					String scriptData = scriptEle.data();
					Matcher match = JsImgPatt.matcher(scriptData);
					if (match.find()) {
						picUrl = match.group(1);
						break;
					}
				}
			}
		} catch (Exception e) {
			LOG.warn("Fail to get thumbnail for url:" + url);
			picUrl = null;
		}
		return picUrl;
	}
}
