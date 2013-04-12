package cn.edu.zju.plex.tdd.parser;

import cn.edu.zju.plex.tdd.entity.RssNews;

/**
 * 天涯小筑(http://tvfantasy.net) parser
 * 
 * @author WangFengwei
 */
public class TvfantasyParser extends AbstractParser {

	public TvfantasyParser(RssNews data) {
		super(data);
	}

	public void setTargetElements() {
		String targetId = "entry_main";
		String targetTag = "article";

		targetElements = doc.getElementsByClass(targetId);
		targetElements = doc.getElementsByTag(targetTag);
		if (targetElements == null) {
			LOG.error("天涯小筑 TvfantasyParser not parse well");
		}
	}

}
