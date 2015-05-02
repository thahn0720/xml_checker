package thahn.java.xmlchecker.parser;

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.SAXParseException;

import thahn.java.xmlchecker.editors.XmlEditor;
import thahn.java.xmlchecker.marker.MarkerHelper;
import thahn.java.xmlchecker.marker.MarkerType;
import thahn.java.xmlchecker.util.MyStrings;

/**
 *
 * @author th0720.ahn
 *
 */
public abstract class Parser {
	
	protected String											mContents;
	protected int												mRootTagOffset;
	
	public boolean parse(String path, OnDescriptorParseListener listener) {
		mContents = MyStrings.getText(path);
		mRootTagOffset = getRootTagOffset(mContents);
		return true;
	}
	
	protected void processParsingError(SAXParseException e1) {
		String message = e1.getMessage();
		int startTagIndex = message.indexOf('<'); 
		int endTagIndex = message.indexOf('>', startTagIndex);
		int lineNumber = e1.getLineNumber();
		int charStart = MyStrings.getCharStart(mContents, lineNumber, e1.getColumnNumber());
		int charEnd = startTagIndex == -1 || endTagIndex == -1?charStart+10:charStart+endTagIndex-startTagIndex;
		MarkerHelper.getInstance().problem(XmlEditor.getResource(), MarkerType.GRAMMAR_PROBLEM
				, e1.getMessage(), lineNumber, charStart, charEnd);
	}
	
	public String getContents() {
		return mContents;
	}

	public int getRootTagOffset() {
		return mRootTagOffset;
	}
	
	private int getRootTagOffset(String contents) {
		int ret = 0;
		for (int i = 0; i < contents.length(); i++) {
			char c = contents.charAt(i);
			if (c == '<' && i+1 < contents.length() 
					&& contents.charAt(i+1) != '?' && contents.charAt(i+1) != '!') {
				ret = i;
				break;
			}
		}
		return ret; 
	}
}
