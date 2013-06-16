package cn.edu.zju.plex.tdd.seg;

import java.nio.charset.Charset;

import ICTCLAS.I3S.AC.ICTCLAS50;

/**
 * 1. 使用前需要把源字符串处理成小写并且去除空格
 * <p>
 * sInput.toLowerCase().replaceAll(" ", "") TODO 词性标注有问题，有时候是aka有时候是un，这个挺奇怪的
 * 
 * @author WangFengwei
 */
public class MyICTCLAS {
	private static final ICTCLAS50 ICTCLAS50 = new ICTCLAS50();
	public static final String SEP = " ";

	static {
		if (ICTCLAS50.ICTCLAS_Init(".".getBytes(Charset.forName("UTF-8"))) == false) {
			System.out.println("Init Fail!");
			System.exit(1);
		}
		// 设置词性标注集(0 计算所二级标注集，1 计算所一级标注集，2 北大二级标注集，3 北大一级标注集)
		ICTCLAS50.ICTCLAS_SetPOSmap(3);
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
				input.getBytes(Charset.forName("UTF-8")), 0, 1); // 显示词性则设为1
		return new String(nativeBytes, Charset.forName("UTF-8"));
	}

	public static void main(String[] args) {
		importUserDic("meijuDict.txt", 3);
		saveUserDic();
		// 字符串分词
		String sInput = "生活大爆炸神秘因素生活大爆炸 神秘因素the big bang theory 权利的游戏南国医恋》"
				+ "（Hart of Dixie）<tset>a b《权力的游戏》（Game of Thrones）S03E02《Dark Wings, Dark Words》 ";
		String res = MyICTCLAS.fenci(sInput.toLowerCase().replaceAll(
				"(<.*?>)|(\\s)", ""));
		// res = WordFilter.filterStopWords(res, " ");
		for (String s : res.split(" ")) {
			if (s.length() > 0)
				System.out.println(s);
		}
	}

	private MyICTCLAS() {
	}
}
