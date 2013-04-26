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
		String targetId = "article"; 
		String targetClass = "text";
		try {
			tEle = doc.getElementById(targetId).getElementsByClass(targetClass)
					.first();

		} catch (Exception e) {
			LOG.error("Huo360Parser not parse well:" + rssNews);
			tEle = doc;
		}
	}

}
