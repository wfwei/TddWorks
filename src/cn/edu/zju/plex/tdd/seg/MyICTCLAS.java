package cn.edu.zju.plex.tdd.seg;

import java.nio.charset.Charset;

import ICTCLAS.I3S.AC.ICTCLAS50;

// TODO implement mutli-thread version
public class MyICTCLAS {
	private static final ICTCLAS50 ICTCLAS50 = new ICTCLAS50();

	static {
		if (ICTCLAS50.ICTCLAS_Init(".".getBytes(Charset.forName("UTF-8"))) == false) {
			System.out.println("Init Fail!");
			System.exit(1);
		}
		// 设置词性标注集(0 计算所二级标注集，1 计算所一级标注集，2 北大二级标注集，3 北大一级标注集)
		ICTCLAS50.ICTCLAS_SetPOSmap(2);
	}

	/** 设置词性标注集(0 计算所二级标注集，1 计算所一级标注集，2 北大二级标注集，3 北大一级标注集) */
	public static synchronized void setPOSmap(int pos) {
		ICTCLAS50.ICTCLAS_SetPOSmap(pos);
	}

	/**
	 * 第一个参数为用户字典路径，第二个参数为用户字典的编码类型(0:type
	 * unknown;1:ASCII码;2:GB2312,GBK,GB10380;3:UTF-8;4:BIG5)
	 */
	public static void importUserDic(String filePath, int incode) {
		// 导入用户字典
		int nCount = 0;
		String usrdir = filePath; // 用户字典路径
		byte[] usrdirb = usrdir.getBytes(Charset.forName("UTF-8"));// 将string转化为byte类型
		// 导入用户字典,返回导入用户词语个数第一个参数为用户字典路径，第二个参数为用户字典的编码类型
		nCount = ICTCLAS50.ICTCLAS_ImportUserDictFile(usrdirb, incode);
		System.out.println("导入用户词个数" + nCount);
		nCount = 0;
	}

	public static void saveUserDic() {
		ICTCLAS50.ICTCLAS_SaveTheUsrDic();
	}

	public static synchronized String fenci(String input) {
		byte nativeBytes[] = ICTCLAS50.ICTCLAS_ParagraphProcess(
				input.getBytes(Charset.forName("UTF-8")), 0, 1);
		return new String(nativeBytes, Charset.forName("UTF-8"));
	}

	public static void main(String[] args) {
		// importUserDic("meijuDict.txt", 3);
		// saveUserDic();
		// 字符串分词
		String sInput = "《南国医恋》（Hart of Dixie）S02E18《Why Don’t We Get Drunk?》美女上错身不简单oh sit!666 park avenue逝者之证间谍亚契 第一季联盟it’s always sunny in philadelphia厨房噩梦";
		MyICTCLAS.setPOSmap(3);
		String res = MyICTCLAS.fenci(sInput.toLowerCase());
		for(String s:res.split(" ")) 
			System.out.println(s);
	}

	private MyICTCLAS() {
	}
}
