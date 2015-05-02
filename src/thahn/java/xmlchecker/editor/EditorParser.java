package thahn.java.xmlchecker.editor;

import java.io.InputStream;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXParseException;

import thahn.java.xmlchecker.parser.Parser;
import thahn.java.xmlchecker.parser.DescriptorTag;
import thahn.java.xmlchecker.parser.OnDescriptorParseListener;

/**
 *
 * @author th0720.ahn
 *
 */
public class EditorParser extends Parser {

	private EditorXmlTreeHandler handler = new EditorXmlTreeHandler(this);
	
	public TrieList<DescriptorTag> getTree() {
		return handler.getTree();
	}
	
	public boolean parse(String uri) {
		return parse(uri, null);
	}
	
	@Override
	public boolean parse(String uri, OnDescriptorParseListener listener) {
		super.parse(uri, listener);
		boolean ret = false;
		try {
			handler.clean();
			handler.setRootTagOffset(getRootTagOffset());
			SAXParserFactory.newInstance().newSAXParser().parse(uri, handler);
			ret = true;
		} catch (SAXParseException e1) {
			processParsingError(e1);
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		return ret;
	}
}
