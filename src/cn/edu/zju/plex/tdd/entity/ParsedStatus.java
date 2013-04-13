package cn.edu.zju.plex.tdd.entity;

import weibo4j.model.Status;

public class ParsedStatus extends Status {
	private String uname;
	private String uid;
	private String content; //weibo中的文本内容(去除了video,url)
	private String video; //weibo中嵌入的视频地址
	private String url; //weibo中嵌入的url，分号分割
	private String topic; //weibo中的话题(between#)
	private String words; //对@content分词
	private String meiju_ids; //关于哪些美剧，分号分割
	private String at_unames; //at的用户名，分号分割
	private int status; //状态
	
	public static final int ST_ERROR = -1;
	public static final int ST_READY = 0;
	public static final int ST_PARSING = 1;
	public static final int ST_FINISHED = 2;
	
	public String getUname() {
		return uname;
	}
	public void setUname(String uname) {
		this.uname = uname;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getVideo() {
		return video;
	}
	public void setVideo(String video) {
		this.video = video;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getWords() {
		return words;
	}
	public void setWords(String words) {
		this.words = words;
	}
	public String getMeiju_ids() {
		return meiju_ids;
	}
	public void setMeiju_ids(String meiju_ids) {
		this.meiju_ids = meiju_ids;
	}
	public String getAt_unames() {
		return at_unames;
	}
	public void setAt_unames(String at_unames) {
		this.at_unames = at_unames;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

}
