package thahn.java.xmlchecker.parser;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import thahn.java.xmlchecker.util.MyStrings;
import thahn.java.xmlchecker.util.Xmls;

/**
 *
 * @author th0720.ahn
 *
 */
public abstract class Handler extends DefaultHandler {

	private Parser														descriptorParser;
	protected OnDescriptorParseListener									onDescriptorParsing;
	protected DescriptorTag												currentTag;
	protected int														rootTagOffset;
	private boolean 													tagContinued 			= false;
	private String 														prevStr;
	private Locator 													locator;
	
	public Handler(Parser parser) {
		this.descriptorParser = parser;
	}

	private void init() {
		tagContinued = false;
	}
	
	@Override
	public void setDocumentLocator(Locator locator) {
		super.setDocumentLocator(locator);
		this.locator = locator;
	}

	public void setOnDescriptorParsing(OnDescriptorParseListener mCmsDescriptorParsing) {
		this.onDescriptorParsing = mCmsDescriptorParsing;
	}
	
	public void setRootTagOffset(int rootTagOffset) {
		this.rootTagOffset = rootTagOffset;
	}
	
	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		init();
		onStart();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		onTag();
	}
	
	@Override
	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
		super.ignorableWhitespace(ch, start, length);
	}

	@Override
	public void characters(char buf[], int offset, int len) throws SAXException {
		super.characters(buf, offset, len);
		String value = new String(buf, offset, len).trim();
        if ((value.length() == 0 && currentTag.getValuePosition() != null) || currentTag.getTagName().equals(prevStr)) {
            return; 
        } else {
        	int nextIndex = offset + len;
        	int[] concatRet = Xmls.getNearStartTag(buf, nextIndex, false);
        	if (concatRet[0] > nextIndex) {
        		value = new String(buf, offset, concatRet[0] - offset).trim();
        		if (buf.length != offset + value.length()) {
        			prevStr = new String(currentTag.getTagName());
        		}
        	} else {
        		prevStr = null;
        	}
        }
        
        if (tagContinued && offset == 0) {
        	currentTag.setTagValue(currentTag.getTagValue() + value);
        	currentTag.setValuePosition(currentTag.getValuePosition().getStart()
        			, currentTag.getValuePosition().getEnd() + len);
        	currentTag.setValuePosition(currentTag.getValuePosition().getLine()
        			, currentTag.getValuePosition().getColumn(), currentTag.getValuePosition().getLength() + len);
        	
        	currentTag.setEndTagPosition(currentTag.getEndTagPosition().getStart()
        			, currentTag.getEndTagPosition().getEnd() + len);
        	currentTag.setEndTagPosition(currentTag.getEndTagPosition().getLine()
        			, currentTag.getEndTagPosition().getColumn(), currentTag.getEndTagPosition().getLength() + len);
		} else {
			currentTag.setTagValue(value);
			
			int taglength = currentTag.getTagName().length();
			int line = locator.getLineNumber();
			int column = locator.getColumnNumber() - value.length();
			int startChar = MyStrings.getCharStart(descriptorParser.getContents(), line, column);
			// value position
			currentTag.setValuePosition(startChar, startChar + value.length());
			currentTag.setValuePosition(line, column, value.length());
			// start tag position
			int[] startTagPosition = Xmls.getNearStartTag(buf, offset, true);
			int startTagColumn;
			int startTagCharStart;
			if (startTagPosition[2] == 0) {
				startTagColumn = column - (offset - startTagPosition[0]);
				startTagCharStart = startChar - (offset - startTagPosition[0]);
			} else {
				int temp = MyStrings.getCharStart(descriptorParser.getContents(), line - startTagPosition[2], 0);
				startTagColumn = Xmls.getStartTag(descriptorParser.getContents(), temp) - temp;
				startTagCharStart = temp + startTagColumn;
			}
			currentTag.setStartTagPosition(startTagCharStart, startTagCharStart + taglength);
			currentTag.setStartTagPosition(line - startTagPosition[2], startTagColumn, taglength);
			// end tag position
			int[] endTagPosition = Xmls.getNearEndTag(buf, offset, false);
			int endTagColumn;
			int endTagCharStart; 
			if (endTagPosition[2] == 0) {
				endTagColumn = column + (endTagPosition[0] - offset);
				endTagCharStart = startChar + (offset - endTagPosition[0]);
			} else {
				int temp = MyStrings.getCharStart(descriptorParser.getContents(), line + endTagPosition[2], 0);
				endTagColumn = Xmls.getStartTag(descriptorParser.getContents(), temp) - temp;
				endTagCharStart = temp + endTagColumn;
			}
			currentTag.setEndTagPosition(endTagCharStart, endTagCharStart + taglength);
			currentTag.setEndTagPosition(line + endTagPosition[2], endTagColumn, taglength);
		}
        
        if (buf.length == offset + value.length()) {
			tagContinued = true;
		} else {
			tagContinued = false;
		}
        onValue();
	}
	
	private void onStart() {
		if (onDescriptorParsing != null) {
			onDescriptorParsing.onStart();
		}
	}
	
	private void onTag() {
		if (onDescriptorParsing != null) {
			onDescriptorParsing.onTag(currentTag);
		}
	}
	
	private void onValue() {
		if (onDescriptorParsing != null) {
			onDescriptorParsing.onValue(currentTag);
		}
	}
	
	protected String getElementName(String lName, String qName) {
		String eName = lName; 
		if ("".equals(eName)) {
			eName = qName;
		}
		return eName;
	}
}
