package cn.edu.zju.plex.tdd.main;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import cn.edu.zju.plex.tdd.dao.DB4Tdd;
import cn.edu.zju.plex.tdd.entity.ParsedStatus;
import cn.edu.zju.plex.tdd.entity.RssNews;
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

	private void fetchWeiboUpdate() {
		crawler.fetchAndStoreUpdate();
	}

	private void parseWeibo() {
		while (true) {
			List<ParsedStatus> weiboToParse = DB4Tdd.getWeiboToParse(100);
			LOG.info("Get " + weiboToParse.size() + " weibo status to parse...");

			if (weiboToParse.size() == 0) {
				LOG.info("Weibo parsing work temporally done");
				break;
			} else {
				for (ParsedStatus status : weiboToParse) {
					parser.parse(status);
					DB4Tdd.updateParsedStatus(status);
				}
			}
		}
	}

	private void downloadImages(String rootPath) {
		while (true) {
			List<ParsedStatus> list = DB4Tdd.getParsedStatusToDownloadImages();
			LOG.info("Loop for downloading images, ParsedStatus count:"
					+ list.size());

			if (list.size() <= 0) {
				LOG.info("download ParsedStatus images work temply done");
				break;
			} else {
				for (ParsedStatus status : list) {

					String[] images = { status.getThumbnailPic(),
							status.getBmiddlePic(), status.getOriginalPic() };
					int idx = 0;
					for (int i = 0; i < images.length; i++) {
						Matcher m = ImagePatt.matcher(images[i]);
						if (m.find()) {
							boolean success = ImageFetcher.saveimage(images[i],
									rootPath + "weibo-" + status.getId() + "-"
											+ idx + m.group(1));
							if (success)
								idx++;
							else
								LOG.warn("fail downloading:" + images[i]);
						} else
							LOG.debug("invalid image url" + images[i]);
					}
					LOG.info("get image count:" + idx);
					if (idx > 0)
						DB4Tdd.updateParsedStatusImageCount(status.getId(),
								idx + 1);
					else
						DB4Tdd.updateParsedStatusImageCount(status.getId(), 0);
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
				fetchWeiboUpdate();

				LOG.info("开始解析微博");
				parseWeibo();

				// 去重
				// TODO if needed

				LOG.info("下载图片");
				downloadImages("d:/tmp/images2/");
			} catch (Throwable t) {
				LOG.error(t.getCause().getMessage());
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
