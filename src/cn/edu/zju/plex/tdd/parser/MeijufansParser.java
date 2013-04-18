package cn.edu.zju.plex.tdd.parser;

import cn.edu.zju.plex.tdd.entity.RssNews;

/**
 * 美剧贩(http://meijufans.com/) parser
 * 
 * @author WangFengwei
 */
public class MeijufansParser extends AbstractParser {

	public MeijufansParser(RssNews data) {
		super(data);
	}

	public void setTargetElements() {
		String targetId = "entries";// 范围太大，包含了相关咨询，评论等模块
		String targetClass = "entry"; // 缩小范围，但还是包括了相关咨询

		try{
			targetElements = doc.getElementById(targetId).getElementsByClass(
					targetClass);
		} catch (Exception e) {
			LOG.error("美剧贩 MeijufansParser not parse well:" + rssNews);
			targetElements = doc.getAllElements();
		}

	}

}
