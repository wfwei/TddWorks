package cn.edu.zju.plex.tdd.main;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import cn.edu.zju.plex.tdd.dao.WeiboDao;
import cn.edu.zju.plex.tdd.entity.ParsedStatus;
import cn.edu.zju.plex.tdd.module.WeiboCrawler;
import cn.edu.zju.plex.tdd.module.WeiboParser;
import cn.edu.zju.plex.tdd.tools.ImageFetcher;

/**
 * crawl & parse weibo
 * 
 * @author WangFengwei
 */
public class WeiboJob implements Runnable {

	private final Logger LOG = Logger.getLogger(WeiboJob.class);
	private final int HALF_HOUR = 1800000;
	private WeiboCrawler crawler = new WeiboCrawler();
	private WeiboParser parser = new WeiboParser();
	private static final Pattern ImagePatt = Pattern.compile(
			".*(\\.(bmp|gif|jpe?g|png|tiff?|ico))$", Pattern.CASE_INSENSITIVE);

	private void updateWeibo() {
		crawler.fetchAndStoreNew();
		crawler.fetchAndStoreUpdate();
	}

	private void parseWeibo() {
		while (true) {
			List<ParsedStatus> weiboToParse = WeiboDao.getWeiboToParse(100);
			LOG.info("Get " + weiboToParse.size() + " weibo status to parse...");

			if (weiboToParse.size() == 0) {
				LOG.info("Weibo parsing work temporally done");
				break;
			} else {
				for (ParsedStatus status : weiboToParse) {
					parser.parse(status);
					WeiboDao.updateParsedStatus(status);
				}
			}
		}
	}

	private void downloadImages(String rootPath) {
		while (true) {
			List<ParsedStatus> list = WeiboDao.getParsedStatusToDownloadImages();
			LOG.info("Loop for downloading images, ParsedStatus count:"
					+ list.size());

			if (list.size() <= 0) {
				LOG.info("download ParsedStatus images work temply done");
				break;
			} else {
				for (ParsedStatus status : list) {

					StringBuffer imageSizes = new StringBuffer();
					String[] images = { status.getThumbnailPic(),
							status.getBmiddlePic(), status.getOriginalPic() };
					int count = 0;
					for (int i = 0; i < images.length; i++) {
						if (i != 1)
							continue; // 只保存middlesize的图片
						String imageUrl = images[i];
						Matcher m = ImagePatt.matcher(imageUrl);
						if (m.find()) {
							String imageSize = ImageFetcher.saveimage(imageUrl,
									rootPath + "weibo-" + status.getId() + "-"
											+ i + m.group(1));
							if (imageSize != null) {
								imageSizes.append(imageSize).append(";");
								count++;
							} else
								LOG.warn("fail downloading:" + imageUrl);
						} else
							LOG.debug("invalid image url" + imageUrl);
					}
					if (count > 0)
						WeiboDao.updateImageCountAndSize(
								status.getId(), count, imageSizes.toString());
					else
						WeiboDao.updateImageCountAndSize(
								status.getId(), 0, "");
				}
				LOG.info("download images for weibo count:" + list.size());
			}
		}
	}

	@Override
	public void run() {
		while (true) {
			LOG.info("Loop start for WeiboJob");
			try {
				LOG.info("开始下载微博更新");
				updateWeibo();

				LOG.info("开始解析微博");
				parseWeibo();

				// 去重
				// TODO if needed

				LOG.info("下载图片");
				downloadImages("d:/tmp/images2/");
			} catch (Exception t) {
				t.printStackTrace();
				LOG.error(t.getMessage());
			} finally {
				LOG.info("Loog over for WeiboJob");
				try {
					Thread.sleep(HALF_HOUR);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) {
		new Thread(new WeiboJob(), "WeiboJob").start();
	}

}
