package thahn.java.xmlchecker.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 *
 * @author th0720.ahn
 *
 */
public class NodeList<R> {
	
	private NodeList<R> parent;
	private R data;
	private HashMap<String, List<NodeList<R>>> children = new HashMap<>();
	
	public NodeList(R data) {
		this.data = data;
	}

	/**
	 * if root, null
	 * @return
	 */
	public NodeList<R> getParent() {
		return parent;
	}
	
	public void setParent(NodeList<R> parent) {
		this.parent = parent;
	}
	
	public R getData() {
		return data;
	}

	public boolean contains(String key) {
		return children.containsKey(key);
	}
	
	public List<NodeList<R>> getChild(String key) {
		return children.get(key);
	}
	
	/**
	 * if exists, replace only data
	 * @param key
	 * @param data
	 */
	public void add(String key, R data) {
		NodeList<R> node = new NodeList<R>(data);
		node.setParent(this);
		if (children.containsKey(key)) {
			children.get(key).add(node);
		} else {
			ArrayList<NodeList<R>> lists = new ArrayList<>();
			lists.add(node);
			children.put(key, lists);
		}
	}
	
	public void add(String key, NodeList<R> node) {
		node.setParent(this);
		if (children.containsKey(key)) {
			children.get(key).add(node);
		} else {
			ArrayList<NodeList<R>> lists = new ArrayList<>();
			lists.add(node);
			children.put(key, lists);
		}
	}
	
	public Set<String> keySet() {
		return children.keySet();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NodeList [data=").append(data).append(", parent=")
				.append(parent).append(", children=").append(children)
				.append("]");
		return builder.toString();
	}
}
