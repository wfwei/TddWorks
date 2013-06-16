package open56;

import org.apache.log4j.Logger;

public class Open56PicFecher extends Open56Client {

	private Open56PicFecher(String appkey, String secret, String domain,
			String interfaceURL) {
		super(appkey, secret, domain, interfaceURL);
	}

	private static final Logger LOG = Logger.getLogger(Open56PicFecher.class);

	private static String APPKEY = "3000001134";
	private static String APPSECRET = "b56e4a2f0f6e59d7";
	private static String DOMAIN = "http://oapi.56.com";
	private static String INTERFACEURL = "/video/getVideoInfo.json";

	private static Open56PicFecher fetcher = new Open56PicFecher(APPKEY,
			APPSECRET, DOMAIN, INTERFACEURL);

	public static String getPic(String vurl) {
		String imgUrl = null;

		String vid = parseVid(vurl);
		String vinfo = fetcher.getVideoInfoApp(vid);
		int startIdx = vinfo.indexOf("\"img\":\"");
		if (startIdx > 0) {
			startIdx += 7;
			imgUrl = vinfo.substring(startIdx, vinfo.indexOf("\"", startIdx))
					.replace("\\/", "/");
			System.out.println(vinfo);
			System.out.println(imgUrl);
			LOG.debug(vinfo);
			LOG.debug(imgUrl);
		} else {
			LOG.warn("Fail to get video thumbnail for url:" + vurl);
		}

		return imgUrl;
	}

	private static String parseVid(String vurl) {
		// http://www.56.com/u86/v_OTI1ODg1NzE.html
		String vid = null; // OTI1ODg1NzE
		int startIdx = vurl.lastIndexOf('/') + 1;
		if (startIdx > 0) {
			vid = vurl.substring(startIdx, vurl.lastIndexOf('.'));
			if (vid.startsWith("v_")) {
				vid = vid.substring(2);
			}
		}
		return vid;
	}

}
