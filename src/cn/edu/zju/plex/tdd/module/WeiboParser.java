package cn.edu.zju.plex.tdd.module;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import cn.edu.zju.plex.tdd.entity.ParsedStatus;

import cn.edu.zju.plex.tdd.seg.MyICTCLAS;
import cn.edu.zju.plex.tdd.tools.MeijuTvUtil;

/**
 * weibo parser
 *  
 * @author plex
 */
public class WeiboParser {

	private static final Logger LOG = Logger.getLogger(WeiboParser.class);

	private static final Pattern WeiboTopicPatt = Pattern.compile("#(.*?)#");
	private static final Pattern WeiboUrlPatt = Pattern
			.compile("(http://t.cn/[^\\s]+)");
	private static final Pattern AtPatt = Pattern.compile("(@[^\\s]+)");

	// TODO 直接在参数上修改是不是不太好
	public void parse(ParsedStatus status) {
		LOG.info("start parsing weibo status:"+status.getId());
		
		String wtext = status.getText();
		String content = wtext;
		StringBuffer sb;

		// extract & set topic
		Matcher mat = WeiboTopicPatt.matcher(content);
		StringBuffer topics = new StringBuffer();
		while (mat.find()) {
			topics.append(mat.group(1) + ";");
		}
		status.setTopic(topics.toString());

		// extract & set urls
		mat = WeiboUrlPatt.matcher(content);
		sb = new StringBuffer();
		StringBuffer urls = new StringBuffer();
		while (mat.find()) {
			urls.append(mat.group() + ";");
			mat.appendReplacement(sb, "");
		}
		mat.appendTail(sb);
		content = sb.toString();
		status.setUrl(urls.toString());

		// extract & set ats
		mat = AtPatt.matcher(content);
		sb = new StringBuffer();
		StringBuffer ats = new StringBuffer();
		while (mat.find()) {
			ats.append(mat.group(1) + ";");
			mat.appendReplacement(sb, "");
		}
		mat.appendTail(sb);
		content = sb.toString();
		status.setAt_unames(ats.toString());

		// set content
		status.setContent(content);

		// seg & set words
		String words = MyICTCLAS.fenci(content);
		status.setWords(words);

		// parse tvids
		String meijuIds = MeijuTvUtil.guessTv(content);
		status.setMeiju_ids(meijuIds);

		// set status
		status.setStatus(ParsedStatus.ST_FINISHED);
	}

}
