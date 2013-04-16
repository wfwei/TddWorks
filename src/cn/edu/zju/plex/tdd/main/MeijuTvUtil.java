package cn.edu.zju.plex.tdd.main;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import cn.edu.zju.plex.tdd.dao.DB4Tdd;
import cn.edu.zju.plex.tdd.tools.CharsetTool;

public class MeijuTvUtil {

	private static final HashMap<String, String> Meijus = DB4Tdd.getMeijuTvs();
	private static final Logger LOG = Logger.getLogger(WeiboParser.class);

	public static String guessTv(String content) {

		HashMap<String, Double> guessList = new HashMap<String, Double>();
		content = content.toLowerCase().replace(" ", "");
		for (String mjName : Meijus.keySet()) {
			if (content.contains(mjName)) {
				double w = 1;
				// 长度较小的名字进行降权
				if (mjName.length() < 8)
					w = 0.6;
				String meijuId = Meijus.get(mjName);
				Double weight = guessList.get(meijuId);
				if (weight == null)
					weight = w;
				else
					weight += w;
				guessList.put(meijuId, weight);
			}
		}
		String result = null;
		double maxW = 0;
		for (Entry<String, Double> entry : guessList.entrySet()) {
			if (entry.getValue() > maxW) {
				maxW = entry.getValue();
				result = entry.getKey();
			}
		}
		return result;

	}

	/**
	 * 把中文美剧名导出，统一为名词词性
	 * 
	 * @param fname
	 */
	public static void outputMeijuDict(String fname) {
		try {
			PrintWriter pw = new PrintWriter(fname);
			for (String names : Meijus.keySet()) {
				for (String name : names.split(" ")) {
					if (CharsetTool.containChinese(name)) {
						pw.println(name + "\tn");
						LOG.info("write user dic item:" + name);
					}
				}
			}
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			LOG.warn("fail to output user dict:" + e.getMessage());
		}
	}

	public static void main(String args[]) {
		for (Entry<String, String> en : Meijus.entrySet()) {
			LOG.info(en);
		}

		// outputMeijuDict("meijuDict.txt");
	}
}
