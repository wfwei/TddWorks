package cn.edu.zju.plex.tdd.parser;

import cn.edu.zju.plex.tdd.entity.RssNews;

/**
 * 美剧极客(http://www.tvjike.com/) parser
 * 
 * @author WangFengwei
 */
public class TvjikeParser extends AbstractParser {

	public TvjikeParser(RssNews data) {
		super(data);
	}

	public void setTargetElements() {
		String targetId = "content";//范围太大，包含了相关咨询，评论等模块
		String targetClass = "entry"; // 缩小范围，但还是包括了相关咨询
		
		targetElements = doc.getElementById(targetId).getElementsByClass(targetClass);
		if (targetElements == null) 
			LOG.error("美剧极客 TvjikeParser not parse well");
		
	}

}
