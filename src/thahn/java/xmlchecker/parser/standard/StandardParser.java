package thahn.java.xmlchecker.parser.standard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import thahn.java.xmlchecker.editors.XmlEditor;
import thahn.java.xmlchecker.marker.MarkerHelper;
import thahn.java.xmlchecker.parser.Parser;
import thahn.java.xmlchecker.parser.DescriptorTag;
import thahn.java.xmlchecker.parser.OnDescriptorParseListener;

/**
 *
 * @author th0720.ahn
 *
 */
public class StandardParser extends Parser {

	private StandardTreeHandler handler = new StandardTreeHandler(this);
	
	public TrieMap<DescriptorTag> getTree() {
		return handler.getTree();
	}
	
	@Override
	public boolean parse(String uri, OnDescriptorParseListener listener) {
		super.parse(uri, listener);
		boolean ret = false;
		try {
			handler.clean();
			handler.setRootTagOffset(getRootTagOffset());
			handler.setOnDescriptorParsing(listener);
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
