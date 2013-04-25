package cn.edu.zju.plex.tdd.parser;

import org.jsoup.nodes.Element;

import cn.edu.zju.plex.tdd.entity.RssNews;

/**
 * 天涯小筑(http://tvfantasy.net) parser
 * 
 * @author WangFengwei
 */
public class TvfantasyParser extends AbstractParser {

	public TvfantasyParser(RssNews data) {
		super(data);
		Element e = doc.getElementById("comments");
		if (e != null)
			e.remove();
	}

	public void setTargetElements() {
		// String targetId = "entry_main";
		String targetTag = "article";

		try {
			targetElements = doc.getElementsByTag(targetTag);
		} catch (Exception e) {
			LOG.error("天涯小筑 TvfantasyParse not parse well:" + rssNews);
			targetElements = doc.getAllElements();
		}

	}

}
