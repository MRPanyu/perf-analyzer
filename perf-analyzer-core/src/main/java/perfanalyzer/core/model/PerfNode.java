package perfanalyzer.core.model;

import java.io.Serializable;

/**
 * 性能数据节点，表示某个代码块（如方法执行等）的单次执行信息。
 * 
 * @author panyu
 *
 */
public class PerfNode implements Serializable {

	private static final long serialVersionUID = 1720531900775676271L;

	public static final String PATH_SEPARATOR = "/";

	/** 执行节点完整路径 */
	private NodePath path;
	/** 标识这次执行是否错误或异常，在统计时成功和错误的数据会分开统计 */
	private boolean error;
	/** 执行开始时间，通过{@link System#nanoTime()}获取 */
	private long startTimeNano;
	/** 执行结束时间，通过{@link System#nanoTime()}获取 */
	private long endTimeNano;
	/** 子节点执行花费的时间 */
	private long childrenUseTimeNano = 0;

	/** 父节点，表示调用这个代码块的代码块 */
	private PerfNode parent;

	protected PerfNode() {
	}

	public PerfNode(String name, NodeType type, long startTimeNano, PerfNode parent) {
		this.path = NodePath.getInstance(name, type, parent == null ? null : parent.path);
		this.startTimeNano = startTimeNano;
		this.parent = parent;
	}

	public NodePath getPath() {
		return path;
	}

	/** 获取执行耗时纳秒数 */
	public long getUseTimeNano() {
		return endTimeNano - startTimeNano;
	}

	/** 获取去除所有子节点执行时间后的本节点自身执行时间纳秒数 */
	public long getUseTimeNanoExcludeChildren() {
		long useTime = getUseTimeNano();
		return useTime - childrenUseTimeNano;
	}

	/** 用于在子节点执行完成后累加耗时纳秒数 */
	public void addChildrenUseTime(long childrenUseTimeNano) {
		this.childrenUseTimeNano += childrenUseTimeNano;
	}

	public String getName() {
		return path.getName();
	}

	public NodeType getType() {
		return path.getType();
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

	public long getChildrenUseTimeNano() {
		return childrenUseTimeNano;
	}

	public void setChildrenUseTimeNano(long childrenUseTimeNano) {
		this.childrenUseTimeNano = childrenUseTimeNano;
	}

	public PerfNode getParent() {
		return parent;
	}

	public void setParent(PerfNode parent) {
		this.parent = parent;
	}

}