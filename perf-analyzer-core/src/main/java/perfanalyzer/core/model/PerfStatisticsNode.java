package perfanalyzer.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 性能节点统计数据，表示某个代码块（如方法执行等）的一段时间内的执行统计信息。
 * 
 * @author panyu
 *
 */
public class PerfStatisticsNode implements Serializable {

	private static final long serialVersionUID = 7981067356341733462L;

	private String name;
	private String path;
	private long successCount = 0L;
	private long errorCount = 0L;
	private long successMaxUseTime = 0L;
	private long successMaxUseTimeExcludeChildren = 0L;
	private long errorMaxUseTime = 0L;
	private long errorMaxUseTimeExcludeChildren = 0L;
	private long successTotalUseTime = 0L;
	private long successTotalUseTimeExcludeChildren = 0L;
	private long errorTotalUseTime = 0L;
	private long errorTotalUseTimeExcludeChildren = 0L;

	private PerfStatisticsNode parent;
	private List<PerfStatisticsNode> children = new ArrayList<PerfStatisticsNode>();

	protected PerfStatisticsNode() {
	}

	public PerfStatisticsNode(String name, String path, PerfStatisticsNode parent) {
		this.name = name;
		this.path = path;
		this.parent = parent;
		if (parent != null) {
			parent.children.add(this);
		}
	}

	public synchronized void mergeNode(PerfNode node) {
		long useTime = node.getUseTime();
		long useTimeExcludeChildren = node.getUseTimeExcludeChildren();
		if (node.isError()) {
			this.errorCount++;
			this.errorTotalUseTime += useTime;
			this.errorTotalUseTimeExcludeChildren += useTimeExcludeChildren;
			if (useTime > this.errorMaxUseTime) {
				this.errorMaxUseTime = useTime;
			}
			if (useTimeExcludeChildren > this.errorMaxUseTimeExcludeChildren) {
				this.errorMaxUseTimeExcludeChildren = useTimeExcludeChildren;
			}
		} else {
			this.successCount++;
			this.successTotalUseTime += useTime;
			this.successTotalUseTimeExcludeChildren += useTimeExcludeChildren;
			if (useTime > this.successMaxUseTime) {
				this.successMaxUseTime = useTime;
			}
			if (useTimeExcludeChildren > this.successMaxUseTimeExcludeChildren) {
				this.successMaxUseTimeExcludeChildren = useTimeExcludeChildren;
			}
		}
	}

	public long getExecuteCount() {
		return this.successCount + this.errorCount;
	}

	public long getMaxUseTime() {
		return Math.max(this.successMaxUseTime, this.errorMaxUseTime);
	}

	public long getMaxUseTimeExcludeChildren() {
		return Math.max(this.successMaxUseTimeExcludeChildren, this.errorMaxUseTimeExcludeChildren);
	}

	public long getTotalUseTime() {
		return this.successTotalUseTime + this.errorTotalUseTime;
	}

	public long getTotalUseTimeExcludeChildren() {
		return this.successMaxUseTimeExcludeChildren + this.errorTotalUseTimeExcludeChildren;
	}

	public long getAvgUseTime() {
		return this.getTotalUseTime() / this.getExecuteCount();
	}

	public long getAvgUseTimeExcludeChildren() {
		return this.getTotalUseTimeExcludeChildren() / this.getExecuteCount();
	}

	public long getSuccessAvgUseTime() {
		return this.successTotalUseTime / this.successCount;
	}

	public long getSuccessAvgUseTimeExcludeChildren() {
		return this.successTotalUseTimeExcludeChildren / this.successCount;
	}

	public long getErrorAvgUseTime() {
		return this.errorTotalUseTime / this.errorCount;
	}

	public long getErrorAvgUseTimeExcludeChildren() {
		return this.errorTotalUseTimeExcludeChildren / this.errorCount;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(long successCount) {
		this.successCount = successCount;
	}

	public long getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(long errorCount) {
		this.errorCount = errorCount;
	}

	public long getSuccessMaxUseTime() {
		return successMaxUseTime;
	}

	public void setSuccessMaxUseTime(long successMaxUseTime) {
		this.successMaxUseTime = successMaxUseTime;
	}

	public long getSuccessMaxUseTimeExcludeChildren() {
		return successMaxUseTimeExcludeChildren;
	}

	public void setSuccessMaxUseTimeExcludeChildren(long successMaxUseTimeExcludeChildren) {
		this.successMaxUseTimeExcludeChildren = successMaxUseTimeExcludeChildren;
	}

	public long getErrorMaxUseTime() {
		return errorMaxUseTime;
	}

	public void setErrorMaxUseTime(long errorMaxUseTime) {
		this.errorMaxUseTime = errorMaxUseTime;
	}

	public long getErrorMaxUseTimeExcludeChildren() {
		return errorMaxUseTimeExcludeChildren;
	}

	public void setErrorMaxUseTimeExcludeChildren(long errorMaxUseTimeExcludeChildren) {
		this.errorMaxUseTimeExcludeChildren = errorMaxUseTimeExcludeChildren;
	}

	public long getSuccessTotalUseTime() {
		return successTotalUseTime;
	}

	public void setSuccessTotalUseTime(long successTotalUseTime) {
		this.successTotalUseTime = successTotalUseTime;
	}

	public long getSuccessTotalUseTimeExcludeChildren() {
		return successTotalUseTimeExcludeChildren;
	}

	public void setSuccessTotalUseTimeExcludeChildren(long successTotalUseTimeExcludeChildren) {
		this.successTotalUseTimeExcludeChildren = successTotalUseTimeExcludeChildren;
	}

	public long getErrorTotalUseTime() {
		return errorTotalUseTime;
	}

	public void setErrorTotalUseTime(long errorTotalUseTime) {
		this.errorTotalUseTime = errorTotalUseTime;
	}

	public long getErrorTotalUseTimeExcludeChildren() {
		return errorTotalUseTimeExcludeChildren;
	}

	public void setErrorTotalUseTimeExcludeChildren(long errorTotalUseTimeExcludeChildren) {
		this.errorTotalUseTimeExcludeChildren = errorTotalUseTimeExcludeChildren;
	}

	public PerfStatisticsNode getParent() {
		return parent;
	}

	public void setParent(PerfStatisticsNode parent) {
		this.parent = parent;
	}

	public List<PerfStatisticsNode> getChildren() {
		return children;
	}

	public void setChildren(List<PerfStatisticsNode> children) {
		this.children = children;
	}

}
