package thahn.java.xmlchecker.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


/**
 *
 * @author th0720.ahn
 *
 */
public class TrieList<R> {
	
	private String rootKey;
	private NodeList<R> children;
	private Stack<String> depth;
	
	public TrieList() {
		depth = new Stack<String>();
	}

	public R getRoot() {
		return children.getData();
	}
	
	public void setRoot(String rootKey, R data) {
		this.rootKey = rootKey;
		depth.push(rootKey);
		children = new NodeList<R>(data);
		children.setParent(null);
	}

	public List<NodeList<R>> getChildren(String... keys) {
		List<NodeList<R>> source = new ArrayList<>();
		if (keys[0].equals(rootKey)) {
			source.add(children);
			for (int i = 1; i < keys.length; i++) {
				source = getChildren(source, keys[i]);
			}
		}
		return source;
	}
	
	private List<NodeList<R>> getChildren(List<NodeList<R>> children, String keys) {
		List<NodeList<R>> ret = new ArrayList<>();
		for (NodeList<R> child : children) {
			List<NodeList<R>> candidateList = child.getChild(keys);
			if (candidateList != null) {
				ret.addAll(candidateList);
			}
		}
		return ret;
	}
	
	public NodeList<R> getChildren() {
		return children;
	}
	
	public Stack<String> getCurrentDepth() {
		return depth;
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
	
	private void printChild(StringBuilder builder, NodeList<R> child, int depth) {
		for (String key : child.keySet()) {
			builder.append("\r\n");
			for (int i = 0; i < depth; i++) {
				builder.append("  ");
			}
			builder.append(key);
			for (NodeList<R> node : child.getChild(key)) {
				printChild(builder, node, depth+1);
			}
		}
	}
}
