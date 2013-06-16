package open56;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import org.apache.commons.codec.digest.DigestUtils;

//import com.test.video.config.Config;

public class Open56Client {

	private final String appkey;
	private final String secret;
	private final String domain;
	private final String interfaceURL;

	// private String access_token;
	public Open56Client(String appkey, String secret, String domain,
			String interfaceURL) {
		// TODO Auto-generated constructor stub
		if ("".equals(appkey) || "".equals(secret)) {
			System.out.println("appkey 或 secret均不能为空");
		}
		// 初始化appkey和secret
		this.appkey = appkey;
		this.secret = secret;
		this.interfaceURL = interfaceURL;
		this.domain = domain;
	}

	// /**
	// *
	// * 功能：获取视频上传组件的url地址
	// * @param category(第三方视频分类)
	// * @param css (自定义视频组件的样式)
	// * @param ourl
	// (上传成功后回调你的地址，必须是外网可访问的，如:http://www.wlotx.com/test/success.jsp)
	// * @param rurl (上传失败后的回调地址，如:http://www.wlotx.com/test/fail.jsp)
	// * @param isPublic(是否公开视频(公开-y,不公开-n))
	// * @return
	// */
	// public String getVideoComponenUrl(String category,String css,String
	// ourl,String rurl ,String isPublic){
	// String url=domain+interfaceURL;
	// String sid=Config.sid;
	// try {
	// category = URLEncoder.encode(category,"utf-8");
	// //css = URLEncoder.encode(css,"utf-8");
	// ourl = URLEncoder.encode(ourl,"utf-8");
	// rurl = URLEncoder.encode(rurl,"utf-8");
	// //isPublic = URLEncoder.encode(isPublic,"utf-8");
	// //sid = URLEncoder.encode(sid,"utf-8");
	// } catch (UnsupportedEncodingException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// HashMap<String, String> params = new HashMap<String, String>();
	//
	// //params.put("category", category);
	// params.put("css", css);
	// params.put("ourl", ourl);
	// params.put("rurl", rurl);
	// params.put("sid",sid);
	// //params.put("public", isPublic);
	//
	// url = url+"?"+signRequest(params);
	// return url;
	// }

	// 获取视频信息
	public String getVideoInfoApp(String vid) {
		String url = domain + interfaceURL;
		try {
			vid = URLEncoder.encode(vid, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		HashMap<String, String> params = new HashMap<String, String>();

		params.put("vid", vid);

		url = url + "?" + signRequest(params);

		return httpCall(url);
	}

	// 统一的http请求
	private String httpCall(String url) {

		HttpClient client = new HttpClient(); // 实例化httpClient
		HttpMethod method = new GetMethod(url); //
		String responseContent = "";
		try {
			client.executeMethod(method); // 执行

			InputStream jsonStr;

			jsonStr = method.getResponseBodyAsStream();

			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			int i = -1;
			while ((i = jsonStr.read()) != -1) {
				baos.write(i);
			}

			responseContent = baos.toString();

			jsonStr.close();
			baos.close();
			method.releaseConnection();

		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return responseContent;
	}

	// 签名方法实现，并构造一个参数串
	private String signRequest(HashMap<String, String> params) {

		String req = DigestUtils.md5Hex(mapToString(params)); // 第一轮次计算 MD5加密

		// 获取时间戳
		Date date = new Date();
		long time = date.getTime();
		// mysq 时间戳只有10位 要做处理
		String ts = time + "";
		ts = ts.substring(0, 10);

		params.put(
				"sign",
				DigestUtils.md5Hex(req + "#" + this.appkey + "#" + this.secret
						+ "#" + ts)); // 第二轮次计算 MD5加密
		params.put("appkey", this.appkey);
		params.put("ts", ts);

		return mapToString(params);
	}

	// 将 map 中的参数及对应值转换为字符串
	private String mapToString(HashMap<String, String> params) {
		Object[] array = params.keySet().toArray();

		java.util.Arrays.sort(array);
		String str = "";
		for (int i = 0; i <= array.length - 1; i++) {
			String key = array[i].toString();
			if (i != array.length - 1) {
				str += key + "=" + params.get(key) + "&";
			} else {
				str += key + "=" + params.get(key);
			}
		}
		return str;
	}

}
