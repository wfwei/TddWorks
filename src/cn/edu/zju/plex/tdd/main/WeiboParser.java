package cn.edu.zju.plex.tdd.main;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import cn.edu.zju.plex.tdd.dao.DB4Tdd;
import cn.edu.zju.plex.tdd.entity.ParsedStatus;

import cn.edu.zju.plex.tdd.seg.MyICTCLAS;

public class WeiboParser implements Runnable {
	static {
		PropertyConfigurator.configure("resources/log4j.properties");
	}
	private static final Logger LOG = Logger.getLogger(WeiboParser.class);
	private static final Long ONE_HOUR = 3600000L;

	private static final Pattern WeiboTopicPatt = Pattern.compile("#(.*?)#");
	private static final Pattern WeiboUrlPatt = Pattern
			.compile("(http://t.cn/[^\\s]+)");
	private static final Pattern AtPatt = Pattern.compile("(@[^\\s]+)");

	void parse(ParsedStatus status) {
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

	@Override
	public void run() {
		while (true) {
			List<ParsedStatus> weiboToParse = DB4Tdd.getWeiboToParse(30);
			LOG.info("Get " + weiboToParse.size() + " weibo status to parse...");

			if (weiboToParse.size() == 0) {
				LOG.info("Temporally done, going to sleep a while");
				try {
					Thread.sleep(ONE_HOUR * 10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				for (ParsedStatus status : weiboToParse) {
					parse(status);
					DB4Tdd.updateParsedStatus(status);
				}
			}
		}
	}

	public static void main(String args[]) {
		for (int i = 0; i < 1; i++) {
			new Thread(new WeiboParser(), "WeiboParser-" + i).start();
			LOG.info("WeiboParser-" + i + " started");
		}
	}

}
