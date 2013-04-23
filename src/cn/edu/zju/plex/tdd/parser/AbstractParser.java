package cn.edu.zju.plex.tdd.parser;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.edu.zju.plex.tdd.entity.RssNews;

public abstract class AbstractParser {
	RssNews rssNews;
	Document doc;
	Elements targetElements;

	protected static final Logger LOG = Logger.getLogger(AbstractParser.class);

	public AbstractParser(RssNews data) {
		rssNews = data;
		doc = Jsoup.parse(data.getPage());
	}

	public void parse() {
		setTargetElements();
		parseContent();
		parseImages();
		parseVideos();
	}

	public abstract void setTargetElements();

	public void parseContent() {
		if (targetElements == null)
			setTargetElements();
		String plainText = targetElements.html();
		LOG.info("[abstract]extract text length:" + plainText.length()
				+ " in page: " + rssNews.getLink());
		rssNews.setContent(plainText.replaceAll("<img.*?>", ""));
	}

	/*
	 * TODO <img style=
	 * "border-bottom: 0px; border-left: 0px; display: block; float: none; margin-left: auto; border-top: 0px; margin-right: auto; border-right: 0px"
	 * title="130408DW" border="0" alt="130408DW"
	 * src="http://img.tvjike.com/1fee003f2e1a_C416/130408DW_thumb.jpg"
	 * width="500" height="281" /> <img
	 * src="’http://yarpp.org/pixels/f9c577860ed4c44340122c80c1353a0d’/" />
	 */
	public void parseImages() {
		if (targetElements == null)
			setTargetElements();
		Elements images = targetElements.select("img[src]");

		LOG.info("[abstract]get image  number:" + images.size() + " in page: "
				+ rssNews.getLink());
		StringBuffer sb = new StringBuffer();
		for (Element link : images) {
			sb.append(link.attr("src") + ";");
		}
		rssNews.setImages(sb.toString());
	}

	public void parseVideos() {
		if (targetElements == null)
			setTargetElements();
		Elements videos = targetElements.select("embed[src]");
		LOG.info("[abstract]get video number:" + videos.size() + " in page: "
				+ rssNews.getLink());
		StringBuffer sb = new StringBuffer();
		for (Element link : videos) {
			sb.append(link.attr("src") + ";");
		}
		rssNews.setVideos(sb.toString());
	}
}
