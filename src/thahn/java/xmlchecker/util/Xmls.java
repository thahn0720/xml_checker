package thahn.java.xmlchecker.util;

/**
 *
 * @author th0720.ahn
 *
 */
public class Xmls {
	
	public static int getStartTag(String text, int column) {
		int index = -1;
		for (int i = column; i < text.length(); i++) {
			int temp = text.charAt(i);
			if (temp == '<') {
				index = i;
				break;
			}  
		}
		return index;
	}
	
	/**
	 * 
	 * @param buf
	 * @param offset
	 * @param isLeftSide
	 * @return index 0 : startIndex, index 1 : endIndex, index 2 : moved line
	 */
	public static int[] getNearStartTag(char[] buf, int offset, boolean isLeftSide) {
		int[] ret = new int[3];
		if (isLeftSide) {
			for (int i = offset; i >= 0; i--) {
				if (checkTagPosition(buf, i, ret)) {
					break;
				}
			}
		} else {
			for (int i = offset; i < buf.length; i++) {
				if (checkTagPosition(buf, i, ret)) {
					break;
				}
			}
		}
		
		return ret;
	}
	
	public static int[] getNearEndTag(char[] buf, int offset, boolean isLeftSide) {
		int[] ret = new int[3];
		if (isLeftSide) {
			for (int i = offset; i >= 0; i--) {
				if (checkTagPosition(buf, i, ret)) {
					break;
				}
			}
		} else {
			for (int i = offset; i < buf.length; i++) {
				if (checkTagPosition(buf, i, ret)) {
					break;
				}
			}
		}
		
		return ret;
	}
	
	public static boolean checkTagPosition(char[] buf, int index, int[] ret) {
		char c = buf[index];
		if (c == '<') {
			ret[0] = index;
			return true;
		} else if (c == '>') {
			ret[1] = index;
		} else if (c == '\n') {
			++ret[2];
		}
		return false;
	}
}
