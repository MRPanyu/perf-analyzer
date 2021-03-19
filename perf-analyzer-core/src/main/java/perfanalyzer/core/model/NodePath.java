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

	private static final long serialVersionUID = -4919739999103858392L;

	private static Map<NodePath, NodePath> instanceMap = new HashMap<NodePath, NodePath>();

	/**
	 * 获取实例
	 * 
	 * @param name       当前节点名称
	 * @param parentPath 上层节点路径，为null时表示本层节点是顶层节点
	 * @return 节点路径实例
	 */
	public static NodePath getInstance(String name, NodeType type, NodePath parentPath) {
		NodePath key = new NodePath(name, type, parentPath);
		NodePath instance = instanceMap.get(key);
		if (instance == null) {
			synchronized (instanceMap) {
				instance = instanceMap.get(key);
				if (instance == null) {
					instance = key;
					instanceMap.put(key, key);
				}
			}
		}
		return instance;
	}

	/** 当前节点名称 */
	private String name;
	/** 节点类型 */
	private NodeType type = NodeType.METHOD;
	/** 上层节点路径 */
	private NodePath parentPath;

	/** Immutable对象且会比较频繁使用到hashCode，进行缓存 */
	private int _hashCode;

	private NodePath(String name, NodeType type, NodePath parentPath) {
		this.name = name;
		this.type = type;
		this.parentPath = parentPath;
		// 计算hashCode值
		calcHashCode();
	}

	public String getName() {
		return name;
	}

	public NodeType getType() {
		return type;
	}

	public NodePath getParentPath() {
		return parentPath;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (parentPath != null) {
			sb.append(parentPath.toString());
		}
		sb.append("/[").append(type.toString()).append("]").append(name);
		return sb.toString();
	}

	@Override
	public int hashCode() {
		return _hashCode;
	}

	private void calcHashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((parentPath == null) ? 0 : parentPath.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		_hashCode = result;
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
		if (type != other.type)
			return false;
		return true;
	}

}
