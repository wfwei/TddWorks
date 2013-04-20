package cn.edu.zju.plex.tdd.entity;

public class TvShows {
	private String sid;
	private String cname;
	private String ename;
	private String doubanid;
	private String akas;

	public TvShows(String sid, String cname, String ename) {
		this.sid = sid;
		this.cname = cname;
		this.ename = ename;
	}

	public TvShows() {
		this.sid = "";
		this.cname = "";
		this.ename = "";
	}

	public String toString() {
		return "{" + sid + "," + cname + "," + ename + "}";
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getCname() {
		return cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
	}

	public String getEname() {
		return ename;
	}

	public void setEname(String ename) {
		this.ename = ename;
	}

	public boolean isValid() {
		if (sid != "")
			return true;
		return false;
	}

	public String getDoubanid() {
		return doubanid;
	}

	public void setDoubanid(String doubanid) {
		this.doubanid = doubanid;
	}

	public String getAkas() {
		return akas;
	}

	public void setAkas(String akas) {
		this.akas = akas;
	}

	public boolean equals2(TvShows show) {
		if (this.sid == show.sid)
			return true;
		return false;
	}

}
