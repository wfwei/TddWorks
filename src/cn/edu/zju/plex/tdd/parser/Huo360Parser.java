package cn.edu.zju.plex.tdd.parser;

import cn.edu.zju.plex.tdd.entity.RssNews;

/**
 * 火星360（http://huo360.com/） parser
 * 
 * @author WangFengwei
 */
public class Huo360Parser extends AbstractParser {

	public Huo360Parser(RssNews data) {
		super(data);
	}
	
	public void setTargetElements() {
		String targetId = "article"; //这个比较明显，只有一种类型的新闻
		targetElements = doc.getElementById(targetId).getAllElements();
		if(targetElements == null){
			LOG.error("Huo360Parser not parse well");
		}
	}

}
