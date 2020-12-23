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

	/** 代码块的标识名称，如方法签名等 */
	private String name;
	/** 从根节点到这个节点的全部名称路径，用"/"分隔，用于在记录统计信息的时候分类统计（即同一个方法在不同的地方被调用统计信息分别计算） */
	private String path;
	/** 标识这次执行是否错误或异常，在统计时成功和错误的数据会分开统计 */
	private boolean error;
	/** 执行开始时间，通过{@link System#nanoTime()}获取 */
	private long startTimeNano;
	/** 执行结束时间，通过{@link System#nanoTime()}获取 */
	private long endTimeNano;

	/** 父节点，表示调用这个代码块的代码块 */
	private PerfNode parent;
	/** 子节点，表示这个代码块调用的所有需记录的代码块 */
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

	/** 获取执行耗时纳秒数 */
	public long getUseTimeNano() {
		return endTimeNano - startTimeNano;
	}

	/** 获取去除所有子节点执行时间后的本节点自身执行时间纳秒数 */
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