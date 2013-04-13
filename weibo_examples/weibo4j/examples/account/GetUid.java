package weibo4j.examples.account;

import weibo4j.Account;
import weibo4j.examples.oauth2.Log;
import weibo4j.model.WeiboException;
import weibo4j.org.json.JSONObject;

public class GetUid {

	public static void main(String[] args) {
		String access_token ="2.00l9nr_DfUKrWDf655d3279arZgVvD";
		Account am = new Account();
		am.client.setToken(access_token);
		try {
			JSONObject uid = am.getUid();
			System.out.println(uid);
			Log.logInfo(uid.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
