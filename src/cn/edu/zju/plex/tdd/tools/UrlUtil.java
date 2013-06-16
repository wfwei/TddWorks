package cn.edu.zju.plex.tdd.tools;

import org.apache.log4j.Logger;

public class UrlUtil {
	private static final Logger LOG = Logger.getLogger(UrlUtil.class);

	public static boolean isValid(String url) {
		throw new RuntimeException("");
	}

	public static String convertToAbsolute(String url, String baseUrl) {
		if (url.startsWith("/")) {
			url = getProtocolAndDomain(baseUrl) + url;
		} else if (url.startsWith("./")) {
			// TODO if necessary
			LOG.warn("relative url:" + url);
		} else
			LOG.warn("url is suitable:" + url);
		return url;
	}

	public static void main(String[] args) {
		System.out.println(convertToAbsolute("/abc/dj.jpg",
				"http://www.o.com/1/2/3"));
	}

	private static String getProtocolAndDomain(String url) {
		int domainStartIdx = url.indexOf("//") + 2;
		int domainEndIdx = url.indexOf('/', domainStartIdx);
		if (domainEndIdx > domainStartIdx)
			return url.substring(0, domainEndIdx);
		else {
			LOG.warn("get protocol and domain failed for url:" + url);
			return url;
		}
	}
}
