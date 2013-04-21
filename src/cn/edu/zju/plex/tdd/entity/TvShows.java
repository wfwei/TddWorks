package cn.edu.zju.plex.tdd.entity;

public class TvShows {
	private String tvdbid;
	private String cname;
	private String ename;
	private String doubanid;
	private String aka;
	private String aka_original;

	public TvShows(String sid, String cname, String ename) {
		this.tvdbid = sid;
		this.cname = cname;
		this.ename = ename;
	}

	public TvShows() {
		this.tvdbid = "";
		this.cname = "";
		this.ename = "";
	}

	public String toString() {
		return "{" + tvdbid + "," + cname + "," + ename + "}";
	}

	public String getTvdbid() {
		return tvdbid;
	}

	public void setTvdbid(String sid) {
		this.tvdbid = sid;
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
		if (tvdbid != "")
			return true;
		return false;
	}

	public String getDoubanid() {
		return doubanid;
	}

	public void setDoubanid(String doubanid) {
		this.doubanid = doubanid;
	}

	public String getAka() {
		return aka;
	}

	public void setAka(String akas) {
		this.aka = akas;
	}

	public String getAka_original() {
		return aka_original;
	}

	public void setAka_original(String aka_original) {
		this.aka_original = aka_original;
	}

	public boolean equals2(TvShows show) {
		if (this.tvdbid == show.tvdbid)
			return true;
		return false;
	}

}
