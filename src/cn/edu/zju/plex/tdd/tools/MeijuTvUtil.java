package cn.edu.zju.plex.tdd.tools;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import cn.edu.zju.plex.tdd.dao.DB4Tdd;
import cn.edu.zju.plex.tdd.entity.TvShows;
import cn.edu.zju.plex.tdd.module.WeiboParser;

public class MeijuTvUtil {

	private static final HashMap<String, TvShows> Meijus = DB4Tdd.getMeijuTvs();
	private static final Logger LOG = Logger.getLogger(WeiboParser.class);

	public static TvShows guessTv(String content) {

		HashMap<TvShows, Double> guessList = new HashMap<TvShows, Double>();
		content = content.toLowerCase().replace(" ", "");
		for (String mjName : Meijus.keySet()) {
			if (content.contains(mjName)) {
				double w = 1;
				// 长度较小的名字进行降权
				if (mjName.length() < 8)
					w = 0.6;
				TvShows tvShow = Meijus.get(mjName);
				Double weight = guessList.get(tvShow);
				if (weight == null)
					weight = w;
				else
					weight += w;
				guessList.put(tvShow, weight);
			}
		}
		TvShows result = new TvShows();
		double maxW = 0;
		for (Entry<TvShows, Double> entry : guessList.entrySet()) {
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
					if (CharsetUtil.containChinese(name)) {
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
		for (Entry<String, TvShows> en : Meijus.entrySet()) {
			LOG.info(en);
		}

		// outputMeijuDict("meijuDict.txt");
	}
}
