package cn.edu.zju.plex.tdd.entity;

import java.util.Date;

public class RssNews implements Cloneable {

	private long id;
	private String title;
	private String link;
	private String author;
	private String category;
	private String description;
	private Date pubDate;
	private long feed;
	private String page;
	private String content;
	private String summary;
	private String words;
	private String images;
	private String videos;
	private TvShows tvShows;
	private long delegate;
	private int status;

	public static final int ST_ERROR = -1;
	public static final int ST_READY = 0;
	public static final int ST_PARSING = 1;
	public static final int ST_FINISHED = 2;

	@Override
	public RssNews clone() {
		RssNews rssNews = null;
		try {
			rssNews = (RssNews) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return rssNews;
	}

	@Override
	public String toString() {
		return "rssnews:{id:" + id + ", title:" + title + ", link:" + link
				+ ", status:" + status + "}";
	}

	/**
	 * init rss news with rss parts(title, author, link, category, description, pubDate,
	 * content, feed) and set status = ST_READY
	 */
	public void setFirstPart(String title, String link, String author,
			String category, String description, Date pubDate, String page,
			long feed) {
		this.title = title;
		this.link = link;
		this.author = author;
		this.category = category;
		this.description = description;
		this.pubDate = pubDate;
		this.feed = feed;
		this.page = page;
		this.status = ST_READY;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getPubDate() {
		return pubDate;
	}

	public void setPubDate(Date pubDate) {
		this.pubDate = pubDate;
	}

	public long getFeed() {
		return feed;
	}

	public void setFeed(long feed) {
		this.feed = feed;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getWords() {
		return words;
	}

	public void setWords(String words) {
		this.words = words;
	}

	public String getImages() {
		return images;
	}

	public void setImages(String images) {
		this.images = images;
	}

	public String getVideos() {
		return videos;
	}

	public void setVideos(String videos) {
		this.videos = videos;
	}

	public TvShows getTvShows() {
		return tvShows;
	}

	public void setTvShows(TvShows tvShows) {
		this.tvShows = tvShows;
	}

	public long getDelegate() {
		return delegate;
	}

	public void setDelegate(long delegate) {
		this.delegate = delegate;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public static int getStError() {
		return ST_ERROR;
	}

	public static int getStReady() {
		return ST_READY;
	}

	public static int getStParsing() {
		return ST_PARSING;
	}

	public static int getStFinished() {
		return ST_FINISHED;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

}
