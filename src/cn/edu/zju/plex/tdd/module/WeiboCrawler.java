package cn.edu.zju.plex.tdd.module;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.zju.plex.tdd.dao.WeiboDao;
import cn.edu.zju.plex.tdd.entity.ParsedStatus;

import weibo4j.Timeline;
import weibo4j.model.Paging;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;
import weibo4j.model.WeiboException;

/**
 * weibo crawler
 * 
 * @author WangFengwei
 */
public class WeiboCrawler {

	private final Logger LOG = Logger.getLogger(WeiboCrawler.class);
	private final long INTERVAL = 2000L; // 2 seconds
	private String accessToken = "2.00l9nr_DfUKrWDf655d3279arZgVvD";
	private HashMap<String, String> targetUsers = WeiboDao.getWeiboTargets();
	private final SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public WeiboCrawler(String accessToken, HashMap<String, String> targetUsers) {
		this.accessToken = accessToken;
		this.targetUsers = targetUsers;
	}

	public WeiboCrawler() {
	};

	/**
	 * 获取目标用户新发布的微博
	 */
	public ArrayList<Status> fetchAndStoreNew() {
		ArrayList<Status> res = new ArrayList<Status>();

		for (String wuid : targetUsers.keySet()) {
			String lastUpdateWeibo = targetUsers.get(wuid);
			LOG.info("start fetching new weibos for user:" + wuid
					+ " lastUpdateWeibo:" + lastUpdateWeibo);
			if (lastUpdateWeibo == null)
				lastUpdateWeibo = "1";
			Timeline tm = new Timeline();
			Paging paging = new Paging();
			int perNum = 100;
			paging.setCount(perNum);
			boolean done = false; // if caught @lastUpdateWeibo
			String latestWeibo = null;
			int count = 0;
			boolean theFirst = true; // 去掉可能的置顶微博
			// 最多爬取最近的前1000条微博
			for (int p = 1; p < 11 && !done; p++) {
				paging.setPage(p);
				try {
					tm.client.setToken(accessToken);
					try {
						StatusWapper status = tm.getUserTimelineByUid(wuid,
								paging, 0, 0);
						for (Status s : status.getStatuses()) {
							if (theFirst
									&& Long.valueOf(s.getId()) <= Long
											.valueOf(lastUpdateWeibo)) {
								theFirst = false;
								continue;
							}
							if (latestWeibo == null)
								latestWeibo = s.getId();

							if (Long.valueOf(s.getId()) <= Long
									.valueOf(lastUpdateWeibo)) {
								done = true;
								break;
							}
							count++;
							WeiboDao.insertWeibo(s);
						}
					} catch (WeiboException e) {
						e.printStackTrace();
						LOG.error(e.getMessage());
					}

					Thread.sleep(INTERVAL);

				} catch (InterruptedException e) {
					e.printStackTrace();
					LOG.error(e.getMessage());
				}
			}
			LOG.info("Fetching weibo updates for user:" + wuid
					+ " over, total:" + count);

			if (latestWeibo != null) {
				lastUpdateWeibo = latestWeibo;
				WeiboDao.updateWeiboTargets(wuid, lastUpdateWeibo);
			}
			if (!done)
				LOG.warn("not succeed in fetching weibo for user:" + wuid);
		}

		return res;
	}

	/**
	 * 更新已经爬取的微博的评论和转发数目
	 */
	public void fetchAndStoreUpdate() {
		int offset = 0, count = 500, dayLimit = 2;
		List<ParsedStatus> weibos = null;
		Timeline tm = new Timeline();
		tm.client.setToken(accessToken);
		Calendar date = Calendar.getInstance();
		date.set(Calendar.DATE, date.get(Calendar.DATE) - dayLimit);
		String beginTime = sdf.format(date.getTime());
		LOG.info("Start fetching weibo updates");
		do {
			weibos = WeiboDao.getWeiboToUpdate(beginTime, offset, count);
			offset += weibos.size();
			LOG.info("Get " + weibos.size()
					+ " weibo to update repost&comment count");
			for (ParsedStatus weibo : weibos) {
				try {
					Status status = tm.showStatus(weibo.getId());
					boolean needUpdate = false;
					int commentCount = status.getCommentsCount();
					int repostCount = status.getRepostsCount();
					if (commentCount > weibo.getCommentsCount()
							|| repostCount > weibo.getRepostsCount())
						needUpdate = true;
					if (needUpdate) {
						WeiboDao.updateCommentAndRepostCount(weibo.getId(),
								commentCount, repostCount);
					}

					Thread.sleep(INTERVAL);
				} catch (WeiboException e) {
					if (e.getStatusCode() == 400) {
						LOG.info("Status was removed already:" + weibo.getId());
					} else {
						LOG.warn("Fail to fetch weibo:" + weibo.getId());
						LOG.warn(e.getMessage());
						e.printStackTrace();
					}
				} catch (InterruptedException e) {
				}
			}
			LOG.info("Updated " + weibos.size() + " status");
		} while (weibos.size() > 0);
		LOG.info("Update loop over");
	}
}
