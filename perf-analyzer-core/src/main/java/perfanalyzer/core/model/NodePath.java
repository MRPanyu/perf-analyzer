package perfanalyzer.core.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 表示执行节点路径的模型结构
 * <p>
 * 包含本层节点的名称以及上层节点对象两个属性，不可变（Immutable），支持hashcode/equals，且尽可能保持相同节点单实例化（反序列化后可能非单例，但应该只有UI展示的时候才用到）。
 * 
 * @author panyu
 *
 */
public class NodePath implements Serializable {

	private static final long serialVersionUID = -4458084428890672515L;

	private static Map<NodePath, Map<String, NodePath>> instanceMap = new HashMap<NodePath, Map<String, NodePath>>();

	/**
	 * 获取实例
	 * 
	 * @param name       当前节点名称
	 * @param parentPath 上层节点路径，为null时表示本层节点是顶层节点
	 * @return 节点路径实例
	 */
	public static NodePath getInstance(String name, NodePath parentPath) {
		Map<String, NodePath> map = instanceMap.get(parentPath);
		if (map == null) {
			synchronized (instanceMap) {
				map = instanceMap.get(parentPath);
				if (map == null) {
					map = new HashMap<String, NodePath>();
					instanceMap.put(parentPath, map);
				}
			}
		}
		NodePath path = map.get(name);
		if (path == null) {
			synchronized (map) {
				path = map.get(name);
				if (path == null) {
					path = new NodePath(name, parentPath);
					map.put(name, path);
				}
			}
		}
		return path;
	}

	/** 当前节点名称 */
	private String name;
	/** 上层节点路径 */
	private NodePath parentPath;

	/** Immutable对象且会比较频繁使用到hashCode，进行缓存 */
	private int _hashCode;

	private NodePath(String name, NodePath parentPath) {
		this.name = name;
		this.parentPath = parentPath;
		// 计算hashCode值
		calcHashCode();
	}

	public String getName() {
		return name;
	}

	public NodePath getParentPath() {
		return parentPath;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("/").append(name);
		NodePath p = parentPath;
		while (p != null) {
			sb.insert(0, "/" + p.name);
			p = p.parentPath;
		}
		return sb.toString();
	}

	@Override
	public int hashCode() {
		return this._hashCode;
	}

	private void calcHashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((parentPath == null) ? 0 : parentPath.hashCode());
		this._hashCode = result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NodePath other = (NodePath) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (parentPath == null) {
			if (other.parentPath != null)
				return false;
		} else if (!parentPath.equals(other.parentPath))
			return false;
		return true;
	}

}
