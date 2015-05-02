package thahn.java.xmlchecker.parser.standard;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import thahn.java.xmlchecker.parser.Handler;
import thahn.java.xmlchecker.parser.Parser;
import thahn.java.xmlchecker.parser.DescriptorTag;

/**
 *
 * @author th0720.ahn
 *
 */
public class StandardTreeHandler extends Handler {
	
	private TrieMap<DescriptorTag> 										myTree;
	
	public StandardTreeHandler(Parser parser) {
		super(parser);
	}

	public TrieMap<DescriptorTag> getTree() {
		return myTree;
	}
	
	@Override
	public void startElement(String namespaceURI, String lName, String qName, Attributes attrs) throws SAXException {
		String eName = getElementName(lName, qName);
		
		currentTag = DescriptorTag.builder().tagName(eName).attributes(attrs).build();
		if (myTree == null) {
			myTree = new TrieMap<DescriptorTag>();
			myTree.setRoot(eName, currentTag);
		} else {
			myTree.addCurrentDepth(currentTag, eName);
			myTree.pushDepth(eName);
		}
		
		super.startElement(namespaceURI, lName, qName, attrs);
	}

	@Override
	public void endElement(String namespaceURI, String sName, String qName) throws SAXException {
		myTree.popDepth();
	}

	public void clean() {
		myTree = null;
	}
}