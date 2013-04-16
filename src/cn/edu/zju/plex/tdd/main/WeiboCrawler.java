package cn.edu.zju.plex.tdd.main;

import java.util.HashMap;
import org.apache.log4j.Logger;

import cn.edu.zju.plex.tdd.dao.DB4Tdd;

import weibo4j.Timeline;
import weibo4j.model.Paging;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;
import weibo4j.model.WeiboException;

public class WeiboCrawler implements Runnable {

	private final Logger LOG = Logger.getLogger(DB4Tdd.class);
	private final long INTERVAL = 30000L; // 30 seconds?
	private String accessToken;
	private HashMap<String, String> targetUsers;

	public WeiboCrawler(String accessToken, HashMap<String, String> targetUsers) {
		this.accessToken = accessToken;
		this.targetUsers = targetUsers;
	}

	@Override
	public void run() {
		for (String wuid : targetUsers.keySet()) {
			LOG.info("start fetching weibo updates for user:" + wuid);
			String lastUpdateWeibo = targetUsers.get(wuid);
			Timeline tm = new Timeline();
			Paging paging = new Paging();
			int perNum = 200;
			paging.setCount(perNum);
			boolean flag = false; // if caught @lastUpdateWeibo
			String latestWeibo = null;
			// 最多爬取最近的前1000条微博
			for (int p = 1; p < 6; p++) {
				paging.setPage(p);
				try {
					tm.client.setToken(accessToken);
					try {
						StatusWapper status = tm.getUserTimelineByUid(wuid,
								paging, 0, 0);
						for (Status s : status.getStatuses()) {
							if (latestWeibo == null) {
								latestWeibo = s.getId();
							}
							s.getIdstr();
							// s.getId().compareTo(lastUpdateWeibo)<=0
							if (Long.valueOf(s.getId()) <= Long
									.valueOf(lastUpdateWeibo)) {
								flag = true;
								break;
							}
							DB4Tdd.insertWeibo(s);
						}
					} catch (WeiboException e) {
						e.printStackTrace();
					}
					Thread.currentThread().sleep(INTERVAL);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				LOG.info("\tget weibo update:" + p * perNum);
			}
			if (flag && latestWeibo != null) {
				lastUpdateWeibo = latestWeibo;
				DB4Tdd.updateWeiboTargets(wuid, lastUpdateWeibo);
			} else
				LOG.warn("not succeed in fetching weibo for user:" + wuid);
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String accessToken = "2.00l9nr_DfUKrWDf655d3279arZgVvD";
		HashMap<String, String> targetUsers = DB4Tdd.getWeiboTargets();

		new Thread(new WeiboCrawler(accessToken, targetUsers), "WeiboCrawler")
				.start();
	}

}
