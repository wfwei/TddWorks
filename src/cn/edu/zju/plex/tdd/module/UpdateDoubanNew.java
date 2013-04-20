package cn.edu.zju.plex.tdd.module;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import weibo4j.org.json.JSONObject;

public class UpdateDoubanNew {
	private final static HttpClient client = new DefaultHttpClient();

	public static void extract(String url, String sid, String showflag)
			throws InstantiationException, IllegalAccessException, Exception {

		try {
			// url="http://api.douban.com/movie/subject/5986497";

			Connection conn;
			Statement stmt, stmtinsert;
			ResultSet res;

			String query = "";

			Class.forName("com.mysql.jdbc.Driver").newInstance();

			// 建立到MySQL的连接
			conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/ikanfou", "root", "4150484");

			// 执行SQL语句
			stmt = conn.createStatement();
			stmtinsert = conn.createStatement();

			String tum = get(url);
			// System.out.println("url:"+tum);
			JSONObject id = new JSONObject(tum);
			String cname = id.optString("title");

			String doubanposterurl = id.getJSONObject("images").optString(
					"large");
			String introduction = id.optString("summary");
			String firstair = id.optString("pubdates");
			String imdbid = id.optString("");
			String tags = id.optString("genres").replaceAll("\\[", "")
					.replaceAll("\\]", "").replaceAll("\"", "");
			String ratingnum = id.optString("ratings_count");

			String rating = id.getJSONObject("rating").optString("average");

			String sql = "update tvshows set showflag='" + showflag
					+ "',finishflag='dfinished',cname='" + cname
					+ "',doubanid='" + url + "',doubanposterurl='"
					+ doubanposterurl + "',introduction='" + introduction
					+ "',firstaidate='" + firstair + "',imbdid='" + imdbid
					+ "',tags='" + tags + "',rating='" + rating
					+ "',ratingnum='" + ratingnum + "' where sid='" + sid
					+ "';";
			System.out.println(sql);
			// stmt.execute(sql);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static String get(String url) throws IOException {
		System.out.println(url);
		HttpGet get = new HttpGet(url);

		HttpResponse response = client.execute(get);

		System.out.println(response.getStatusLine());

		HttpEntity entity = response.getEntity();

		return dump(entity);

	}

	private static String dump(HttpEntity entity) throws IOException {

		BufferedReader br = new BufferedReader(

		new InputStreamReader(entity.getContent(), "utf-8"));

		String content = IOUtils.toString(br);
		System.out.println();
		return content;
	}

	public static void main(String[] args) throws Exception {
		extract("http://api.douban.com/v2/movie/subject/1764796", "", "");
	}
}
