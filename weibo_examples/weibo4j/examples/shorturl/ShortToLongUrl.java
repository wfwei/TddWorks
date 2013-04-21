package weibo4j.examples.shorturl;

import weibo4j.model.WeiboException;
import weibo4j.org.json.JSONException;
import weibo4j.org.json.JSONObject;

public class ShortToLongUrl {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String access_token = "2.00l9nr_DfUKrWDf655d3279arZgVvD";
		String url = "http://t.cn/zTJHwCH";
		weibo4j.ShortUrl su = new weibo4j.ShortUrl();
		su.client.setToken(access_token);
		try {
			JSONObject jo = su.shortToLongUrl(url);
			
			System.out.println(jo.getJSONArray("urls").getJSONObject(0).getString("url_long"));
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}

