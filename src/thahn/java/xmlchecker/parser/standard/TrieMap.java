package thahn.java.xmlchecker.parser.standard;

import java.util.Stack;

import thahn.java.xmlchecker.parser.exception.NotSupportedTagMapException;


/**
 *
 * @author th0720.ahn
 *
 */
public class TrieMap<R> {
	
	private String rootKey;
	private NodeMap<R> children;
	private Stack<String> depth;
	
	public TrieMap() {
		depth = new Stack<String>();
	}

	public R getRoot() {
		return children.getData();
	}
	
	public void setRoot(String rootKey, R data) {
		this.rootKey = rootKey;
		depth.push(rootKey);
		children = new NodeMap<R>(data);
		children.setParent(null);
	}
	
	public NodeMap<R> getChildren() {
		return children;
	}
	
	public NodeMap<R> getNode(String... keys) throws NotSupportedTagMapException {
		NodeMap<R> node = null;
		if (keys.length > 0 && rootKey.equals(keys[0])) {
			node = children;
			for (int i = 1; i < keys.length; i++) {
				node = getChildNode(node, keys[i]);
			}
			return node;
		}
		node = new NodeMap<R>(children.getData());
		node.add(rootKey, children.getData());
		return node;
	}
	
	public NodeMap<R> getNodeInCurrentDepth() throws NotSupportedTagMapException {
		if (rootKey.equals(depth.get(0))) {
			NodeMap<R> node = children;
			for (int i = 1; i < depth.size(); i++) {
				node = getChildNode(node, depth.get(i));
			}
			return node;
		}
		return null;
	}
	
	public NodeMap<R> getNodeInCurrentDepth(String key) throws NotSupportedTagMapException {
		NodeMap<R> node = getNodeInCurrentDepth();
		node = getChildNode(node, key);
		return node;
	}
	
	private NodeMap<R> getChildNode(NodeMap<R> node, String key) throws NotSupportedTagMapException {
		if (node.contains(key)) {
			node = node.getChild(key);
		} else {
			throw new NotSupportedTagMapException("Not Supported Tag : " + key, node, key);
		}
		return node;
	}
	
	public R get(String... keys) throws NotSupportedTagMapException {
		return getNode(keys).getData();
	}
	
	public Stack<String> getCurrentDepth() {
		return depth;
	}
	
	public boolean containsInCurrentDepth(String key) {
		try {
			NodeMap<R> node = getNodeInCurrentDepth();
			return node.contains(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void add(R data, String... keys) throws NotSupportedTagMapException {
		getNode(keys).add(keys[keys.length-1], data);
	}
	
	public void addCurrentDepth(R data, String key) {
		if (depth.isEmpty()) {
			children.add(key, data);
		} else {
			try {
				getNode(depth.toArray(new String[depth.size()])).add(key, data);
			} catch (NotSupportedTagMapException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void pushDepth(String key) {
		depth.push(key);
	}
	
	public String popDepth() {
		if (!depth.isEmpty()) {
			return depth.pop();
		}
		return null;
	}
	
	public void clearDepth() {
		depth.clear();
		if (rootKey != null) {
			depth.push(rootKey);
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(children.getData());
		printChild(builder, children, 0);
		return builder.toString();
	}
	
	private void printChild(StringBuilder builder, NodeMap<R> child, int depth) {
		for (String key : child.getChildrenKeySet()) {
			builder.append("\r\n");
			for (int i = 0; i < depth; i++) {
				builder.append("  ");
			}
			builder.append(key);
			printChild(builder, child.getChild(key), depth+1);
		}
	}
}
