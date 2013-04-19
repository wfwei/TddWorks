package cn.edu.zju.plex.tdd.tools;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;

public class ImageFetcher {

	private static final Logger LOG = Logger.getLogger(ImageFetcher.class);

	public static boolean saveimage(String imageurl, String pathname) {
		boolean success = true;
		LOG.debug("fetch image:[url:" + imageurl + ", path:" + pathname + "]");
		if (imageurl == null || pathname == null) {
			LOG.warn("imageurl or pathname is null");
			return !success;
		}
		if (new File(pathname).exists()) {
			LOG.warn("already exists pass");
		} else {
			try {
				URL url = new URL(imageurl);
				HttpURLConnection urlcon = (HttpURLConnection) url
						.openConnection();
				urlcon.setRequestProperty("User-agent",
						"Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
				// TODO why use DataInputStream??
				DataInputStream in = new DataInputStream(
						urlcon.getInputStream());
				DataOutputStream out = new DataOutputStream(
						new FileOutputStream(pathname));
				byte[] buffer = new byte[4096];
				int count = 0;
				while ((count = in.read(buffer)) > 0) {
					out.write(buffer, 0, count);
				}
				out.close();
				in.close();
			} catch (Exception e) {
				LOG.warn(e);
				return !success;
			}
		}
		return success;
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		saveimage(
				"http://www.iteye.com/upload/logo/user/610968/bfe17df8-da40-3fa3-95de-88f98306ad3a.jpg",
				"d:/test.image");
	}

}
