package cn.edu.zju.plex.tdd.main;

//import java.util.concurrent.ScheduledThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;

public class Controller {

	public static void main(String[] args) {
		// 使用线程池出错就停了，暂不使用
		// ScheduledThreadPoolExecutor scheduler = new
		// ScheduledThreadPoolExecutor(
		// 2);
		//
		// // RssNews 每隔8小时更新一次
		// scheduler.scheduleAtFixedRate(new RssNewsJob(), 0, 8,
		// TimeUnit.HOURS);
		// // Weibo 每隔20分钟刷新一次
		// scheduler.scheduleAtFixedRate(new WeiboJob(), 10, 20,
		// TimeUnit.MINUTES);

		new Thread(new RssNewsJob(), "RssNewsJob").start();
//		new Thread(new WeiboJob(), "WeiboJob").start();

	}

}
