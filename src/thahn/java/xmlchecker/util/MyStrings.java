package thahn.java.xmlchecker.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author th0720.ahn
 *
 */
public class MyStrings {
	
	public static boolean isNullorEmpty(String str) {
		boolean ret = false;
		if (str == null || str.trim().equals("")) {
			ret = true;
		}
		return ret;
	}
	
	public static boolean isEmpty(String str) {
		boolean ret = false;
		if (str.trim().equals("")) {
			ret = true;
		}
		return ret;
	}
	
	public static int getCharStart(String text, int line, int charIndex) {
		int index = -1;
		for (int i = 0; i < line-1; i++) {
			int temp = text.indexOf('\n', index+1);
			if (temp == -1) {
				break;
			}  else {
				index = temp;
			}
		}
		return index + charIndex;
	}
	
	public static String getText(String path) {
		try {
			return getText(new FileInputStream(new File(path)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getText(InputStream is) {
		StringBuilder ret = new StringBuilder();
	    BufferedInputStream bis = new BufferedInputStream(is);
	    byte[] buf = new byte[1024];
	    try {
			int read;
			while ((read = bis.read(buf)) != -1) {
				ret.append(new String(buf, 0, read));
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return ret.toString();
	}
	
	public static String join(List<String> contents, String seperator) {
		StringBuilder builder = new StringBuilder();
		if (contents.size() > 0) {
			builder.append(contents.get(0));
			for (int i = 1; i < contents.size(); i++) {
				builder.append(seperator).append(contents.get(i));
			}
		}
		return builder.toString();
	}
	
	public static List<String> split(String contents, String seperator) {
		List<String> rets = new ArrayList<>();
		String[] splitted = contents.split(seperator);
		for (String item : splitted) {
			rets.add(item.trim());
		}
		return rets;
	}
}
