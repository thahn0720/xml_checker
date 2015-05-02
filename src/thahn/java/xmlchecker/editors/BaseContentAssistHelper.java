package thahn.java.xmlchecker.editors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.CompletionProposal;

import thahn.java.xmlchecker.MyConstants;
import thahn.java.xmlchecker.XmlCheckerPlugin;
import thahn.java.xmlchecker.marker.MarkerHelper;
import thahn.java.xmlchecker.marker.MarkerType;
import thahn.java.xmlchecker.message.ErrorMessage;
import thahn.java.xmlchecker.parser.DescriptorTag;
import thahn.java.xmlchecker.parser.exception.NotSupportedTagMapException;
import thahn.java.xmlchecker.parser.standard.DescriptorContainer;
import thahn.java.xmlchecker.parser.standard.DescriptorStandard;
import thahn.java.xmlchecker.parser.standard.NodeMap;
import thahn.java.xmlchecker.parser.standard.TrieMap;
import thahn.java.xmlchecker.util.MyStrings;

/**
 *
 * @author th0720.ahn
 *
 */
public abstract class BaseContentAssistHelper {

	public static final String									VALUE_SEPARATOR 			= "\\|";
	
	protected String 											mStdDesPath;
	
	public BaseContentAssistHelper(String stdDesPath) {
		mStdDesPath = stdDesPath;
	}

	public DescriptorStandard descriptor() {
		return DescriptorContainer.descriptor(mStdDesPath);
	}
	
	public abstract void computeProposals(List<TagInfo> tagLists, String qualifier, int documentOffset, List<CompletionProposal> propList);
	
	public String[] splitTagOrAttrValue(String value) {
		String[] rets = value.split(VALUE_SEPARATOR);
		for (String ret : rets) {
			ret = ret.trim();
		}
		return rets;
	}
	
	public boolean isTagOrAttrRegExpValue(String value) {
		return value.startsWith(DescriptorStandard.ATTR_MY_PREFIX);
	}
	
	/**
	 * the qualifier should contain '='
	 * @param qualifier
	 * @return
	 */
	public static String getLastAttrName(String qualifier) {
		boolean isEnd = false;
		StringBuilder temp = new StringBuilder();
		for (int i = qualifier.lastIndexOf(AttrValueContentAssistHelper.ATTR_VALUE_START_POINT_PREFIX) - 1; i > 0; i--) {
			char c = qualifier.charAt(i);
			boolean isSpace = Character.isSpaceChar(c);
			if (isEnd && isSpace) {
				break;
			} else if (!isSpace) {
				isEnd = true;
				temp.append(c);
			}
		}
		return temp.reverse().toString();
	}
	
	public static List<TagInfo> computeCurrentLocationOver(IDocument doc, int documentOffset) throws BadLocationException {
		int startIndex = documentOffset;
		int endIndex = -1;
		for (int i = startIndex; i < doc.getLength(); i++) {
			char c = doc.getChar(i);
			if (c == '>') {
				endIndex = i;
				break;
			} 
		}
		return computeCurrentLocation(doc, endIndex);
	}
	
	public static List<TagInfo> computeCurrentLocation(IDocument doc, int documentOffset) {
		Stack<TagInfo> parents = new Stack<>();
		try {
			String str = doc.get(0, documentOffset);
			int size = str.length();
			for (int i = 0; i < size; i++) {
				char c = str.charAt(i);
				if (c == '<') {
					int startIndex = i + 1;
					int endTagIndex1 = str.indexOf(' ', startIndex); 
					int endTagIndex2 = str.indexOf('>', startIndex);
					int endTagIndex3 = str.indexOf("\n", startIndex);
					int endIndex = Math.min(Math.min(
										endTagIndex1 != -1?endTagIndex1:Integer.MAX_VALUE
										, endTagIndex2 != -1?endTagIndex2:Integer.MAX_VALUE)
											, endTagIndex3 != -1?endTagIndex3:Integer.MAX_VALUE
											);
					
					if (endIndex != Integer.MAX_VALUE) {
						i = endIndex;
					} else {
						break;
					}
					
					if (startIndex < size) { 
						char nextChar = str.charAt(startIndex);
						switch (nextChar) {
						case '/':
							// </
							// end tag
							parents.pop();
							break;
						case '?':
							endIndex = str.indexOf("?>", startIndex) + 1;
							break;
						case '!':
							endIndex = str.indexOf("->", startIndex) + 1;
							break;
						default:
							if (endIndex != -1) {
								// <
								String tagName = str.substring(startIndex, endIndex);
								if (tagName.endsWith("/")) {
									// end tag
									endIndex -= 1;
									tagName = tagName.substring(0, tagName.length()-1);
									TagInfo info = parents.pop();
									if (!tagName.equals(info.getTagName())) {
										int lineNumber = doc.computeNumberOfLines(str);//1;
										System.out.println(tagName + " / error / " + lineNumber);
										MarkerHelper.getInstance().problem(XmlEditor.getResource()
												, MarkerType.GRAMMAR_PROBLEM
												, tagName, lineNumber, startIndex, endIndex);
									} 
								} else {
									// start tag
									TagInfo info = new TagInfo(tagName, startIndex, endIndex);
									parents.push(info);
								}
							}
							break;
						}
					}  
				} else if (c == '>') {
					// "/>"
					if (str.charAt(i-1) == '/') {
						parents.pop();
					}
				}
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		return new ArrayList<>(parents);
	}
	
	public static class TagInfo {
		
		private String tagName;
		private int startIndex;
		private int endIndex;
		
		public TagInfo(String tagName, int startIndex, int endIndex) {
			this.tagName = tagName;
			this.startIndex = startIndex;
			this.endIndex = endIndex;
		}

		public String getTagName() {
			return tagName;
		}

		public int getStartIndex() {
			return startIndex;
		}

		public int getEndIndex() {
			return endIndex;
		}
	}
}
