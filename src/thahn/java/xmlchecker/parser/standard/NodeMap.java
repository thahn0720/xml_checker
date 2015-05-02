package thahn.java.xmlchecker.parser.standard;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author th0720.ahn
 *
 */
public class NodeMap<R> {
	
	private NodeMap<R> parent;
	private R data;
	private HashMap<String, NodeMap<R>> children = new HashMap<>();
	
	public NodeMap(R data) {
		this.data = data;
	}

	/**
	 * if root, null
	 * @return
	 */
	public NodeMap<R> getParent() {
		return parent;
	}
	
	public void setParent(NodeMap<R> parent) {
		this.parent = parent;
	}
	
	public R getData() {
		return data;
	}

	public boolean contains(String key) {
		return children.containsKey(key);
	}
	
	public NodeMap<R> getChild(String key) {
		return children.get(key);
	}
	
	public Collection<NodeMap<R>> getAllChildren() {
		return children.values();
	}

	public Set<String> getChildrenKeySet() {
		return children.keySet();
	}
	
	/**
	 * if exists, replace only data
	 * @param key
	 * @param data
	 */
	public void add(String key, R data) {
		if (children.containsKey(key)) {
			children.get(key).data = data;
		} else {
			NodeMap<R> node = new NodeMap<R>(data);
			node.setParent(this);
			children.put(key, node);
		}
	}
	
	public NodeMap<R> remove(String key, R data) {
		return children.remove(key);
	}
}
