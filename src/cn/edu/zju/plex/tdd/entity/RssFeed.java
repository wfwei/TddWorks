package cn.edu.zju.plex.tdd.entity;

import java.util.Date;

public class RssFeed {
	private long id;
	private String title;
	private String feed;
	private String link;
	private int count;
	private Date firstUpdate;
	private Date lastUpdate;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFeed() {
		return feed;
	}

	public void setFeed(String feed) {
		this.feed = feed;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public Date getFirstUpdate() {
		return firstUpdate;
	}

	public void setFirstUpdate(Date firstUpdate) {
		this.firstUpdate = firstUpdate;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
