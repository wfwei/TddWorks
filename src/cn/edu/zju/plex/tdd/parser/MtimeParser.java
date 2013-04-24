package cn.edu.zju.plex.tdd.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.edu.zju.plex.tdd.entity.RssNews;

/**
 * 时光网(http://tvfantasy.net) parser
 * 
 * @author WangFengwei
 */
public class MtimeParser extends AbstractParser {

	public MtimeParser(RssNews data) {
		super(data);
	}

	public void setTargetElements() {
		String targetId = "newscont";		
		try{
			targetElements = doc.getElementById(targetId).getAllElements();
		} catch (Exception e) {
			LOG.error("时光网 MtimeParser not parse well:" + rssNews);
			targetElements = doc.getAllElements();
		}
	}

	/**
	 * 时光网图片部分在网页js中，如下，这里特殊解析
	 * <p>
	 * <script type="text/javascript">var imageList = [
	 * {"ID":361198,"Title":"黄裕翔与张榕容"
	 * ,"Content":"","Url":"http://news.mtime.com/pix/2013/04/09/361198.html"
	 * ,"SmallPicUrl":
	 * "http://img31.mtime.cn/CMS/Gallery/2013/04/09/111659.89889241_75X75.jpg"
	 * ,"MiddlePicUrl":
	 * "http://img31.mtime.cn/CMS/Gallery/2013/04/09/111659.89889241_160X160.jpg"
	 * }, {"ID":361204,"Title":"张榕容与黄裕翔","Content":"","Url":
	 * "http://news.mtime.com/pix/2013/04/09/361204.html","SmallPicUrl":
	 * "http://img31.mtime.cn/CMS/Gallery/2013/04/09/111723.25021924_75X75.jpg"
	 * ,"MiddlePicUrl":
	 * "http://img31.mtime.cn/CMS/Gallery/2013/04/09/111723.25021924_160X160.jpg"
	 * },{"ID":361201,"Title":"舞蹈家许芳宜出演小洁老师","Content":"","Url":
	 * "http://news.mtime.com/pix/2013/04/09/361201.html","SmallPicUrl":
	 * "http://img31.mtime.cn/CMS/Gallery/2013/04/09/111710.87486426_75X75.jpg"
	 * ,"MiddlePicUrl":
	 * "http://img31.mtime.cn/CMS/Gallery/2013/04/09/111710.87486426_160X160.jpg"
	 * },{"ID":361199,"Title":"黄裕翔在学校","Content":"","Url":
	 * "http://news.mtime.com/pix/2013/04/09/361199.html","SmallPicUrl":
	 * "http://img31.mtime.cn/CMS/Gallery/2013/04/09/111702.21039830_75X75.jpg"
	 * ,"MiddlePicUrl":
	 * "http://img31.mtime.cn/CMS/Gallery/2013/04/09/111702.21039830_160X160.jpg"
	 * },{"ID":361200,"Title":"李烈出演黄裕翔母亲","Content":"","Url":
	 * "http://news.mtime.com/pix/2013/04/09/361200.html","SmallPicUrl":
	 * "http://img31.mtime.cn/CMS/Gallery/2013/04/09/111705.12095961_75X75.jpg"
	 * ,"MiddlePicUrl":
	 * "http://img31.mtime.cn/CMS/Gallery/2013/04/09/111705.12095961_160X160.jpg"
	 * },{"ID":361196,"Title":"“大嘴巴”怀秋出演小洁男友","Content":"","Url":
	 * "http://news.mtime.com/pix/2013/04/09/361196.html","SmallPicUrl":
	 * "http://img31.mtime.cn/CMS/Gallery/2013/04/09/111650.98027007_75X75.jpg"
	 * ,"MiddlePicUrl":
	 * "http://img31.mtime.cn/CMS/Gallery/2013/04/09/111650.98027007_160X160.jpg"
	 * },{"ID":361202,"Title":"小洁陪裕翔回台南家乡","Content":"","Url":
	 * "http://news.mtime.com/pix/2013/04/09/361202.html","SmallPicUrl":
	 * "http://img31.mtime.cn/CMS/Gallery/2013/04/09/111713.32236517_75X75.jpg"
	 * ,"MiddlePicUrl":
	 * "http://img31.mtime.cn/CMS/Gallery/2013/04/09/111713.32236517_160X160.jpg"
	 * },{"ID":361203,"Title":"裕翔练琴","Content":"","Url":
	 * "http://news.mtime.com/pix/2013/04/09/361203.html","SmallPicUrl":
	 * "http://img31.mtime.cn/CMS/Gallery/2013/04/09/111719.57717730_75X75.jpg"
	 * ,"MiddlePicUrl":
	 * "http://img31.mtime.cn/CMS/Gallery/2013/04/09/111719.57717730_160X160.jpg"
	 * },{"ID":361197,"Title":"黄裕翔","Content":"","Url":
	 * "http://news.mtime.com/pix/2013/04/09/361197.html","SmallPicUrl":
	 * "http://img31.mtime.cn/CMS/Gallery/2013/04/09/111654.38562666_75X75.jpg"
	 * ,"MiddlePicUrl":
	 * "http://img31.mtime.cn/CMS/Gallery/2013/04/09/111654.38562666_160X160.jpg"
	 * }];var previousNews = {"Title":"\"忠烈杨家将\"遭遇偷票房 排片量增长却受暗算","Url":
	 * "http://news.mtime.com/2013/04/09/1509881.html","PicUrl":
	 * "http://img31.mtime.cn/CMS/GalleryCover/2013/04/09/102442.21119121_120X75.jpg"
	 * };var nextNews = {"Title":"暗黑女王另类诱惑 外媒评影史30位最性感女巫","Url":
	 * "http://news.mtime.com/2013/03/11/1508292.html","PicUrl":
	 * "http://img31.mtime.cn/CMS/GalleryCover/2013/03/11/174412.42336599_120X75.jpg"
	 * };</script>
	 * */
	@Override
	public void parseImages() {
		super.parseImages();
		StringBuffer sb = new StringBuffer();
		Matcher imageMatch = imageListPatt.matcher(doc.select("script").html());
		int count = 0;
		while (imageMatch.find()) {
			sb.append(imageMatch.group(1) + ";");
			count++;
		}
		LOG.info("[MtimeParser]get image number:" + count + " in page: "
				+ rssNews.getLink());
		rssNews.setImages(sb.append(rssNews.getImages()).toString());
	}

	private final Pattern imageListPatt = Pattern
			.compile("\\{.*?MiddlePicUrl\":\"(.*?)\".*?\\}"); // ("var imageList[^{]+(\\{.*?MiddlePicUrl\":\"(.*?)\".*?\\},?)+");
}
