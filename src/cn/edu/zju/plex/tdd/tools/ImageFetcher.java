package cn.edu.zju.plex.tdd.tools;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

public class ImageFetcher {

	private static final Logger LOG = Logger.getLogger(ImageFetcher.class);

	public static String saveimage(String imageurl, String pathname) {
		String imageSize = null;
		LOG.debug("fetch image:[url:" + imageurl + ", path:" + pathname + "]");
		if (imageurl == null || pathname == null) {
			LOG.warn("imageurl or pathname is null");
			return null;
		}
		try {
			if (new File(pathname).exists()) {
				LOG.info("already exists pass");
				BufferedImage sourceImg = ImageIO.read(new FileInputStream(
						pathname));
				imageSize = sourceImg.getHeight() + "," + sourceImg.getWidth();
			} else {
				URL url = new URL(imageurl);
				HttpURLConnection urlcon = (HttpURLConnection) url
						.openConnection();
				urlcon.setRequestProperty("User-agent",
						"Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

				BufferedImage sourceImg = ImageIO.read(urlcon.getInputStream());
				ImageIO.write(sourceImg,
						pathname.substring(pathname.lastIndexOf('.') + 1),
						new File(pathname));
				imageSize = sourceImg.getHeight() + "," + sourceImg.getWidth()
						+ ";";
			}
			return imageSize;
		} catch (Exception e) {
			LOG.warn(e);
			return null;
		}
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String imageSize = saveimage(
				"http://tongxue.baidu.com/baidu/images/banner-2.png",
				"d:/test.png");
		System.out.println(imageSize);
	}

}
