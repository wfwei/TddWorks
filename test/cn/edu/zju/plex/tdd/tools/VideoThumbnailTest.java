package cn.edu.zju.plex.tdd.tools;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class VideoThumbnailTest {
	
	@Test
	public void testGetVideoThumbnail() {
		System.out.println("Testing on testGetVideoThumbnail\n");
		String[][] testCases = {
				{ "http://www.tudou.com/programs/view/IRN6EHC7ilQ/",
						"http://i1.tdimg.com/170/565/344/p.jpg" },
				{
						"http://www.tudou.com/oplay/YUZT28ph9-g/uANj3vmmerM.html",
						"http://g2.ykimg.com/0100641F4651889DCEAED405903CCC2CBBA63D-E207-A972-9BB8-A0E2303C5D84" },
				{
						"http://v.youku.com/v_show/id_XNTcxMDQxMTYw.html?f=19377931",
						"http://g1.ykimg.com/01270F1F4651BBF938852F0123193CAD495B21-2ED6-418F-470F-A6FB6E496E5E" },
				{
						"http://www.56.com/u96/v_NjU4NzkwODU.html",
						"http://v155.56img.com/images/24/29/raiseryoungi56olo56i56.com_sc_mp4_132660314916hd.jpg" },
				{ "http://www.letv.com/ptv/vplay/2026636.html",
						"http://i3.letvimg.com/yunzhuanma/201306/15/621d844e1b599d6c35e4d9fd064041ab/thumb/2.jpg" },
				{
						"http://v.qq.com/cover/a/a6aflrbgxqhtvv9.html?vid=y0012336fca",
						"http://vpic.video.qq.com/82105634/y0012336fca_160_90_3.jpg" },
				{ "http://v.qq.com/cover/8/8wonxzy5pvo8ao3.html",
						"http://i.gtimg.cn/qqlive/img/jpgcache/files/qqvideo/8/8wonxzy5pvo8ao3_h.jpg" } };
		for (int i = 0; i < testCases.length; i++) {
			System.out.println("testing on:\t" + testCases[i][0]);
			String thumbnail = VideoThumbnail
					.getVideoThumbnail(testCases[i][0]);
			System.out.println("result:\t" + thumbnail);
			assertEquals(testCases[i][1], thumbnail);
			System.out.println("PASS\n");
		}
	}

	@Ignore
	@Test
	public void testGetTudou() throws IOException {
		System.out.println("Testing on testGetTudou");
		String[][] testCases = {
				{ "http://www.tudou.com/programs/view/IRN6EHC7ilQ/",
						"http://i1.tdimg.com/170/565/344/p.jpg" },
				{
						"http://www.tudou.com/oplay/YUZT28ph9-g/uANj3vmmerM.html",
						"http://g2.ykimg.com/0100641F4651889DCEAED405903CCC2CBBA63D-E207-A972-9BB8-A0E2303C5D84" } };
		for (int i = 0; i < testCases.length; i++) {
			String thumbnail = VideoThumbnail.getTudou(testCases[i][0]);
			assertEquals(testCases[i][1], thumbnail);
		}
	}

	@Ignore
	@Test
	public void testGetYouku() throws IOException {
		System.out.println("Testing on testGetYouku");
		String[][] testCases = { {
				"http://v.youku.com/v_show/id_XNTcxMDQxMTYw.html?f=19377931",
				"http://g1.ykimg.com/01270F1F4651BBF938852F0123193CAD495B21-2ED6-418F-470F-A6FB6E496E5E" } };
		for (int i = 0; i < testCases.length; i++) {
			assertEquals(testCases[i][1],
					VideoThumbnail.getYouku(testCases[i][0]));
		}
	}

	@Ignore
	@Test
	public void testGet56() throws IOException {
		System.out.println("Testing on testGet56");
		String[][] testCases = { {
				"http://www.56.com/u96/v_NjU4NzkwODU.html",
				"http://v155.56img.com/images/24/29/raiseryoungi56olo56i56.com_sc_mp4_132660314916hd.jpg" } };
		for (int i = 0; i < testCases.length; i++) {
			assertEquals(testCases[i][1], VideoThumbnail.get56(testCases[i][0]));
		}
	}

	@Ignore
	@Test
	public void testGetSina() throws IOException {
		System.out.println("Testing on testGetSina");
		String[][] testCases = { {
				"http://video.sina.com.cn/v/b/71181293-2128167317.html",
				"http://p1.v.iask.com/3/859/71181293_2.jpg" } };
		for (int i = 0; i < testCases.length; i++) {
			assertEquals(testCases[i][1].substring(13),
					VideoThumbnail.getSina(testCases[i][0]).substring(13));
		}
	}

	@Ignore
	@Test
	public void testGetLetv() throws IOException {
		System.out.println("Testing on testGetLetv");
		String[][] testCases = { {
				"http://www.letv.com/ptv/vplay/2026636.html",
				"http://i3.letvimg.com/yunzhuanma/201306/15/621d844e1b599d6c35e4d9fd064041ab/thumb/2.jpg" } };
		for (int i = 0; i < testCases.length; i++) {
			assertEquals(testCases[i][1],
					VideoThumbnail.getLetv(testCases[i][0]));
		}
	}

	@Ignore
	@Test
	public void testGetQQ() throws IOException {
		// * 1. http://v.qq.com/cover/a/a6aflrbgxqhtvv9.html?vid=y0012336fca
		// 通过vid解析
		// * 2. http://v.qq.com/cover/8/8wonxzy5pvo8ao3.html 通过pic :
		System.out.println("Testing on testGetQQ");
		String[][] testCases = {
				{
						"http://v.qq.com/cover/a/a6aflrbgxqhtvv9.html?vid=y0012336fca",
						"http://vpic.video.qq.com/82105634/y0012336fca_160_90_3.jpg" },
				{ "http://v.qq.com/cover/8/8wonxzy5pvo8ao3.html",
						"http://i.gtimg.cn/qqlive/img/jpgcache/files/qqvideo/8/8wonxzy5pvo8ao3_h.jpg" } };
		for (int i = 1; i < testCases.length; i++) {
			assertEquals(testCases[i][1], VideoThumbnail.getQQ(testCases[i][0]));
		}

	}

}
