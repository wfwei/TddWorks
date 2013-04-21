package cn.edu.zju.plex.tdd.entity;

/**
 * 视频封装
 * 
 * @author sunlightcs 2011-4-6 http://hi.juziku.com/sunlightcs/
 */
public class Video {
	private String url;
	private String flash;
	private String pic;
	private String time;

	public String toString() {
		return "[" + url + "," + pic + "]";
	}

	public Video(String url) {
		this.url = url;
	}

	public String getFlash() {
		return flash;
	}

	public void setFlash(String flash) {
		this.flash = flash;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
