package thahn.java.xmlchecker.parser.exception;

import thahn.java.xmlchecker.parser.standard.NodeMap;

/**
 *
 * @author th0720.ahn
 *
 */
public class NotSupportedTagMapException extends Exception {

	private static final long serialVersionUID = 4333944270273370720L;

	private NodeMap<?> 									node;
	private String										wrongKey;
	
	public NotSupportedTagMapException(String message, NodeMap<?> node, String wrongKey) {
		super(message);
		this.node = node;
		this.wrongKey = wrongKey;
	}

	public NodeMap<?> getNode() {
		return node;
	}

	public String getWrongKey() {
		return wrongKey;
	}
}
