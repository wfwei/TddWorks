package cn.edu.zju.plex.tdd.parser;

import cn.edu.zju.plex.tdd.entity.RssNews;

public class DefaultParser extends AbstractParser {

	public DefaultParser(RssNews rssNews) {
		super(rssNews);
	}

	@Override
	public void setTargetElements() {
		LOG.warn("use default parser, which is not set up well");
		tEle = doc;
	}

}
