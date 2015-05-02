package thahn.java.xmlchecker.editor;

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
public class EditorXmlTreeHandler extends Handler {
	
	private TrieList<DescriptorTag> 									myTree;
	private NodeList<DescriptorTag>										currentNode;

	public EditorXmlTreeHandler(Parser parser) {
		super(parser);
	}

	public TrieList<DescriptorTag> getTree() {
		return myTree;
	}
	
	@Override
	public void startElement(String namespaceURI, String lName, String qName, Attributes attrs) throws SAXException {
		String eName = getElementName(lName, qName);
		
		currentTag = DescriptorTag.builder().tagName(eName).attributes(attrs).build();
		if (myTree == null) {
			myTree = new TrieList<DescriptorTag>();
			myTree.setRoot(eName, currentTag);
			currentNode = myTree.getChildren();
		} else {
			NodeList<DescriptorTag> node = new NodeList<DescriptorTag>(currentTag);
			currentNode.add(eName, node);
			currentNode = node;
			myTree.pushDepth(eName);
		}
		
		super.startElement(namespaceURI, lName, qName, attrs);
	}

	@Override
	public void endElement(String namespaceURI, String sName, String qName) throws SAXException {
		if (currentNode.getParent() != null) { // root child
			currentNode = currentNode.getParent();
		}
		myTree.popDepth();
	}

	public void clean() {
		myTree = null;
	}
}