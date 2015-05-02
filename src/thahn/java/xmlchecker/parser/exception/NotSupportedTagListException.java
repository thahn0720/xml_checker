package thahn.java.xmlchecker.parser.exception;

import thahn.java.xmlchecker.editor.NodeList;

/**
 *
 * @author th0720.ahn
 *
 */
public class NotSupportedTagListException extends Exception {

	private static final long serialVersionUID = 4333944270273370720L;

	private NodeList<?> 									node;
	private String										wrongKey;
	
	public NotSupportedTagListException(String message, NodeList<?> node, String wrongKey) {
		super(message);
		this.node = node;
		this.wrongKey = wrongKey;
	}

	public NodeList<?> getNode() {
		return node;
	}

	public String getWrongKey() {
		return wrongKey;
	}
}
