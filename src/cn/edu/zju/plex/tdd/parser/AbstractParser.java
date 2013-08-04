package cn.edu.zju.plex.tdd.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.edu.zju.plex.tdd.entity.RssNews;

public abstract class AbstractParser {
    RssNews rssNews;
    Document doc;
    Element tEle;

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
		if (tEle == null)
			setTargetElements();
		String plainText = tEle.html();
		LOG.info("[abstract]extract text length:" + plainText.length()
				+ " in page: " + rssNews.getLink());
		String tagReg = "(?s)<(/?)(.*?)>";
		Pattern tagPatt = Pattern.compile(tagReg);
		Matcher match = tagPatt.matcher(plainText);
		StringBuffer sb = new StringBuffer();
		while(match.find()){
		    String tag = match.group(2).trim();
		    int blankIdx = tag.indexOf(' ');
		    if(blankIdx > 0)
			tag = tag.substring(0, blankIdx);
		    String replacement = "<" + match.group(1);
		    if(tag.equals("p") || tag.equals("br"))
			replacement += tag + ">";
		    else{
			replacement = "";
		    }
		    match.appendReplacement(sb, Matcher.quoteReplacement(replacement));
		}
		match.appendTail(sb);
		String content = sb.toString().replaceAll("<br>\\s*(?=<br>)", "");
		content = content.replaceAll("<p>\\s*</p>", "");
		content = content.replaceAll("<p><br></p>", "");
		rssNews.setContent(content);
	}

    public void parseImages() {
	if (tEle == null)
	    setTargetElements();
	Elements images = tEle.select("img[src]");

	LOG.info("[abstract]get image  number:" + images.size() + " in page: "
		+ rssNews.getLink());
	StringBuffer sb = new StringBuffer();
	for (Element link : images) {
	    sb.append(link.attr("src") + ";");
	}
	rssNews.setImages(sb.toString());
    }

    public void parseVideos() {
	if (tEle == null)
	    setTargetElements();
	Elements videos = tEle.select("embed[src]");
	LOG.info("[abstract]get video number:" + videos.size() + " in page: "
		+ rssNews.getLink());
	StringBuffer sb = new StringBuffer();
	for (Element link : videos) {
	    sb.append(link.attr("src") + ";");
	}
	rssNews.setVideos(sb.toString());
    }
}
