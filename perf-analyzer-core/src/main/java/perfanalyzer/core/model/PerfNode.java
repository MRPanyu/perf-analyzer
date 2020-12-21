package perfanalyzer.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 性能数据节点，表示某个代码块（如方法执行等）的单次执行信息。
 * 
 * @author panyu
 *
 */
public class PerfNode implements Serializable {

	private static final long serialVersionUID = -1580462591761505567L;

	public static final String PATH_SEPARATOR = "/";

	private String name;
	private String path;
	private boolean error;
	private long startTimeNano;
	private long endTimeNano;

	private PerfNode parent;
	private List<PerfNode> children = new ArrayList<PerfNode>();

	protected PerfNode() {
	}

	public PerfNode(String name, long startTimeNano, PerfNode parent) {
		this.name = name;
		this.startTimeNano = startTimeNano;
		this.parent = parent;
		if (parent != null) {
			parent.children.add(this);
		}
	}

	public String getPath() {
		if (path == null) {
			if (parent == null) {
				path = name;
			} else {
				path = parent.getPath() + PATH_SEPARATOR + name;
			}
		}
		return path;
	}

	public long getUseTimeNano() {
		return endTimeNano - startTimeNano;
	}

	public long getUseTimeNanoExcludeChildren() {
		long useTime = getUseTimeNano();
		long childrenUseTime = 0;
		for (PerfNode child : children) {
			childrenUseTime += child.getUseTimeNano();
		}
		return useTime - childrenUseTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public long getStartTimeNano() {
		return startTimeNano;
	}

	public void setStartTimeNano(long startTimeNano) {
		this.startTimeNano = startTimeNano;
	}

	public long getEndTimeNano() {
		return endTimeNano;
	}

	public void setEndTimeNano(long endTimeNano) {
		this.endTimeNano = endTimeNano;
	}

	public PerfNode getParent() {
		return parent;
	}

	public void setParent(PerfNode parent) {
		this.parent = parent;
	}

	public List<PerfNode> getChildren() {
		return children;
	}

	public void setChildren(List<PerfNode> children) {
		this.children = children;
	}

}