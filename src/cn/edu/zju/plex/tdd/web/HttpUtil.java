package cn.edu.zju.plex.tdd.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

/**
 * optimize this code...
 * http://stackoverflow.com/questions/14866362/invalid-use-of-basicclientconnmanager-connection-still-allocated
 * http://hc.apache.org/httpcomponents-client-ga/tutorial/html/connmgmt.html#d5e639
 * @author WangFengwei
 *
 */
public class HttpUtil {

	private HttpParams httpParams;
	private HttpClient httpClient;
	private int timeout = 100000;

	private static final Logger LOG = Logger.getLogger(HttpUtil.class);

	public HttpUtil() {
		httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
		HttpConnectionParams.setSoTimeout(httpParams, timeout);
		httpClient = new DefaultHttpClient(httpParams);
	}

	/**
	 * 使用httpclient获取指定url的网页内容
	 * 
	 * @author WangFengwei
	 * @time 2012-8-29
	 */
	public String fetchPage(String url) {
		String html = null;
		HttpGet httpget = new HttpGet(url);

		httpget.setHeader("User-Agent",
				"Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.1.2)");
		httpget.setHeader("Accept-Charset", "GB2312,utf-8;q=0.7,*;q=0.7");
		HttpResponse response = null;
		try {
			response = httpClient.execute(httpget);

			/* 检查http状态 */
			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode != HttpStatus.SC_OK) {
				/* 不正常状态 */
				if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY
						|| statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
					/* 跳转 */
					Header header = response.getFirstHeader("Location");
					if (header != null) {
						LOG.warn("Redirect to url:" + header.getValue());
						return fetchPage(header.getValue());
					}
				}
				/* 错误，返回null */
				LOG.error("Failed to fetch url: " + url + "\t HttpStatus:"
						+ response.getStatusLine().toString());
				return null;
			}

			/* 成功获取 */
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				byte[] bytes = EntityUtils.toByteArray(entity);

				/* 获取头部Content-Type中包含了编码信息 */
				String charSet = EntityUtils.getContentCharSet(entity);
				/* 如果头部中没有，那么我们需要 查看页面源码，这个方法虽然不能说完全正确，因为有些粗糙的网页编码者没有在页面中写头部编码信息 */
				if (charSet == null) {
					String regEx = "<meta.*charset=['|\"]?([[a-z]|[A-Z]|[0-9]|-]*)['|\"]?";
					Pattern p = Pattern
							.compile(regEx, Pattern.CASE_INSENSITIVE);
					Matcher m = p.matcher(new String(bytes)); // 默认编码转成字符串，因为我们的匹配中无中文，所以串中可能的乱码对我们没有影响
					if (m.find()) {
						charSet = m.group(1);
					} else {
						/* 如果还没能知道编码，默认使用gb2312 */
						charSet = "gb2312";
					}
				}
				/* 构建网页字符串 */
				html = new String(bytes, charSet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				EntityUtils.consume(response.getEntity());
				httpget.abort();
			} catch (IOException e) {
			}
		}
		return html;
	}

	public boolean login(String loginPostUrl, String parasStr, String encoding) {
		try {
			HttpPost post = new HttpPost(loginPostUrl);
			// 设置需要提交的参数
			List<NameValuePair> paras = new ArrayList<NameValuePair>();
			String[] para_array = parasStr.split(";");
			for (String pair : para_array) {
				if (pair != null && pair.contains(":")) {
					String[] key_value = pair.split(":");
					paras.add(new BasicNameValuePair(key_value[0].trim(),
							key_value[1].trim()));
				}
			}
			post.setEntity(new UrlEncodedFormEntity(paras, encoding));
			httpClient.execute(post);
			post.abort();
		} catch (Exception e) {
			LOG.warn("login error:\t" + e.toString());
			return false;
		}
		LOG.warn("Login Success!!!\t");
		return true;
		
	}

	/**
	 * When HttpClient instance is no longer needed, shut down the connection
	 * manager to ensure immediate deallocation of all system resources
	 */
	public void closeHttpClient() {
		try {
			httpClient.getConnectionManager().shutdown();
		} catch (Exception e) {
		}
	}

	public static void main(String args[]) {
		try {
			// System.out
			// .println(login(
			// "https://www.google.com/accounts/ServiceLoginAuth?service=lh2",
			// "username:cf.wfwei@gmail.com;password:wfwei@google",
			// "utf-8"));
			HttpUtil http = new HttpUtil();
			System.out.println(http.fetchPage("http://www.baidu.com"));
		} catch (Exception e) {
			LOG.error(e.toString());
		}
	}
}
