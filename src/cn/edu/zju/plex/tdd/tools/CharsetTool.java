package cn.edu.zju.plex.tdd.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CharsetTool {
	private static final Pattern ChineseCharPatt = Pattern.compile("[\u4e00-\u9fa5]");
	
	public static boolean containChinese(String text){
		Matcher m = ChineseCharPatt.matcher(text); 
		if(m.find()) return true; 
		return false; 
	}
	
	public static int countChineseWords(String text){
		Matcher m = ChineseCharPatt.matcher(text); 
		int count=0;
		while(m.find()) count ++; 
		return count; 
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(countChineseWords("fsfs方fsfs规"));
	}

}
