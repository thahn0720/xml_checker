package thahn.java.xmlchecker.message;

/**
 *
 * @author th0720.ahn
 *
 */
public class ErrorMessage {

	public static String invalidRootTagName(String rootTag) {
		return String.format("root tag name is \"%s\"", rootTag);
	}
	
	public static String invalidTagName(String childTag, String parentTag) {
		return String.format("Child tag \"%s\" of parent tag \"%s\" does not exist in standard descriptor format.", childTag, parentTag);
	}
	
	public static String valueDependencyError(String value) {
		return String.format("the dependency error : \"%s\"", value);
	}
	
	public static String valueRegExpNotMatch(String matcher, String pattern) {
		return String.format("the tag value's regular expression match error : \"%s\"is not matched with \"%s\"", matcher, pattern);
	}
}
