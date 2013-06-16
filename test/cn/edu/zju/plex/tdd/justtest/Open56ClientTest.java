package cn.edu.zju.plex.tdd.justtest;

import open56.Open56Client;

import org.junit.Test;

public class Open56ClientTest {

	@Test
	public void testGetVideoInfoApp() {
		String APPKEY = "3000001134";
		String APPSECRET = "b56e4a2f0f6e59d7";
		String domain = "http://oapi.56.com";
		String interfaceUrl = "/video/getVideoInfo.json";
		Open56Client client = new Open56Client(APPKEY, APPSECRET, domain,
				interfaceUrl);
		String info = client.getVideoInfoApp("NjU4NzkwODU");
		int startIdx = info.indexOf("\"img\":\"");
		String imgUrl = null;
		if (startIdx > 0) {
			startIdx += 7;
			imgUrl = info.substring(startIdx, info.indexOf("\"", startIdx));
		}
		System.out.println(info);
		System.out.println(imgUrl.replace("\\/", "/"));
	}
}
