package perfanalyzer.core.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * 性能节点统计数据，表示某个代码块（如方法执行等）的一段时间内的执行统计信息。
 * 
 * @author panyu
 *
 */
public class PerfStatisticsNode implements Serializable {

	private static final long serialVersionUID = 5654499597403167720L;

	private static long safeDivide(long a, long b) {
		if (b == 0) {
			return 0;
		}
		return a / b;
	}

	private String name;
	private String path;
	private long successCount = 0L;
	private long errorCount = 0L;
	private long successMaxUseTimeNano = 0L;
	private long successMaxUseTimeNanoExcludeChildren = 0L;
	private long errorMaxUseTimeNano = 0L;
	private long errorMaxUseTimeNanoExcludeChildren = 0L;
	private long successTotalUseTimeNano = 0L;
	private long successTotalUseTimeNanoExcludeChildren = 0L;
	private long errorTotalUseTimeNano = 0L;
	private long errorTotalUseTimeNanoExcludeChildren = 0L;

	private transient PerfStatisticsNode parent;
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
		long useTimeNano = node.getUseTimeNano();
		long useTimeNanoExcludeChildren = node.getUseTimeNanoExcludeChildren();
		if (node.isError()) {
			this.errorCount++;
			this.errorTotalUseTimeNano += useTimeNano;
			this.errorTotalUseTimeNanoExcludeChildren += useTimeNanoExcludeChildren;
			if (useTimeNano > this.errorMaxUseTimeNano) {
				this.errorMaxUseTimeNano = useTimeNano;
			}
			if (useTimeNanoExcludeChildren > this.errorMaxUseTimeNanoExcludeChildren) {
				this.errorMaxUseTimeNanoExcludeChildren = useTimeNanoExcludeChildren;
			}
		} else {
			this.successCount++;
			this.successTotalUseTimeNano += useTimeNano;
			this.successTotalUseTimeNanoExcludeChildren += useTimeNanoExcludeChildren;
			if (useTimeNano > this.successMaxUseTimeNano) {
				this.successMaxUseTimeNano = useTimeNano;
			}
			if (useTimeNanoExcludeChildren > this.successMaxUseTimeNanoExcludeChildren) {
				this.successMaxUseTimeNanoExcludeChildren = useTimeNanoExcludeChildren;
			}
		}
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

	public long getSuccessMaxUseTimeNano() {
		return successMaxUseTimeNano;
	}

	public void setSuccessMaxUseTimeNano(long successMaxUseTimeNano) {
		this.successMaxUseTimeNano = successMaxUseTimeNano;
	}

	public long getSuccessMaxUseTimeNanoExcludeChildren() {
		return successMaxUseTimeNanoExcludeChildren;
	}

	public void setSuccessMaxUseTimeNanoExcludeChildren(long successMaxUseTimeNanoExcludeChildren) {
		this.successMaxUseTimeNanoExcludeChildren = successMaxUseTimeNanoExcludeChildren;
	}

	public long getErrorMaxUseTimeNano() {
		return errorMaxUseTimeNano;
	}

	public void setErrorMaxUseTimeNano(long errorMaxUseTimeNano) {
		this.errorMaxUseTimeNano = errorMaxUseTimeNano;
	}

	public long getErrorMaxUseTimeNanoExcludeChildren() {
		return errorMaxUseTimeNanoExcludeChildren;
	}

	public void setErrorMaxUseTimeNanoExcludeChildren(long errorMaxUseTimeNanoExcludeChildren) {
		this.errorMaxUseTimeNanoExcludeChildren = errorMaxUseTimeNanoExcludeChildren;
	}

	public long getSuccessTotalUseTimeNano() {
		return successTotalUseTimeNano;
	}

	public void setSuccessTotalUseTimeNano(long successTotalUseTimeNano) {
		this.successTotalUseTimeNano = successTotalUseTimeNano;
	}

	public long getSuccessTotalUseTimeNanoExcludeChildren() {
		return successTotalUseTimeNanoExcludeChildren;
	}

	public void setSuccessTotalUseTimeNanoExcludeChildren(long successTotalUseTimeNanoExcludeChildren) {
		this.successTotalUseTimeNanoExcludeChildren = successTotalUseTimeNanoExcludeChildren;
	}

	public long getErrorTotalUseTimeNano() {
		return errorTotalUseTimeNano;
	}

	public void setErrorTotalUseTimeNano(long errorTotalUseTimeNano) {
		this.errorTotalUseTimeNano = errorTotalUseTimeNano;
	}

	public long getErrorTotalUseTimeNanoExcludeChildren() {
		return errorTotalUseTimeNanoExcludeChildren;
	}

	public void setErrorTotalUseTimeNanoExcludeChildren(long errorTotalUseTimeNanoExcludeChildren) {
		this.errorTotalUseTimeNanoExcludeChildren = errorTotalUseTimeNanoExcludeChildren;
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

	public long getExecuteCount() {
		return this.successCount + this.errorCount;
	}

	public long getMaxUseTimeNano() {
		return Math.max(this.successMaxUseTimeNano, this.errorMaxUseTimeNano);
	}

	public long getMaxUseTimeNanoExcludeChildren() {
		return Math.max(this.successMaxUseTimeNanoExcludeChildren, this.errorMaxUseTimeNanoExcludeChildren);
	}

	public long getTotalUseTimeNano() {
		return this.successTotalUseTimeNano + this.errorTotalUseTimeNano;
	}

	public long getTotalUseTimeNanoExcludeChildren() {
		return this.successTotalUseTimeNanoExcludeChildren + this.errorTotalUseTimeNanoExcludeChildren;
	}

	public long getAvgUseTimeNano() {
		return safeDivide(this.getTotalUseTimeNano(), this.getExecuteCount());
	}

	public long getAvgUseTimeNanoExcludeChildren() {
		return safeDivide(this.getTotalUseTimeNanoExcludeChildren(), this.getExecuteCount());
	}

	public long getSuccessAvgUseTimeNano() {
		return safeDivide(this.successTotalUseTimeNano, this.successCount);
	}

	public long getSuccessAvgUseTimeNanoExcludeChildren() {
		return safeDivide(this.successTotalUseTimeNanoExcludeChildren, this.successCount);
	}

	public long getErrorAvgUseTimeNano() {
		return safeDivide(this.errorTotalUseTimeNano, this.errorCount);
	}

	public long getErrorAvgUseTimeNanoExcludeChildren() {
		return safeDivide(this.errorTotalUseTimeNanoExcludeChildren, this.errorCount);
	}

	public String getSuccessMaxUseTime() {
		return nanoToMillis(getSuccessMaxUseTimeNano());
	}

	public String getSuccessMaxUseTimeExcludeChildren() {
		return nanoToMillis(getSuccessMaxUseTimeNanoExcludeChildren());
	}

	public String getErrorMaxUseTime() {
		return nanoToMillis(getErrorMaxUseTimeNano());
	}

	public String getErrorMaxUseTimeExcludeChildren() {
		return nanoToMillis(getErrorMaxUseTimeNanoExcludeChildren());
	}

	public String getSuccessTotalUseTime() {
		return nanoToMillis(getSuccessTotalUseTimeNano());
	}

	public String getSuccessTotalUseTimeExcludeChildren() {
		return nanoToMillis(getSuccessTotalUseTimeNanoExcludeChildren());
	}

	public String getErrorTotalUseTime() {
		return nanoToMillis(getErrorTotalUseTimeNano());
	}

	public String getErrorTotalUseTimeExcludeChildren() {
		return nanoToMillis(getErrorTotalUseTimeNanoExcludeChildren());
	}

	public String getMaxUseTime() {
		return nanoToMillis(getMaxUseTimeNano());
	}

	public String getMaxUseTimeExcludeChildren() {
		return nanoToMillis(getMaxUseTimeNanoExcludeChildren());
	}

	public String getTotalUseTime() {
		return nanoToMillis(getTotalUseTimeNano());
	}

	public String getTotalUseTimeExcludeChildren() {
		return nanoToMillis(getTotalUseTimeNanoExcludeChildren());
	}

	public String getAvgUseTime() {
		return nanoToMillis(getAvgUseTimeNano());
	}

	public String getAvgUseTimeExcludeChildren() {
		return nanoToMillis(getAvgUseTimeNanoExcludeChildren());
	}

	public String getSuccessAvgUseTime() {
		return nanoToMillis(getSuccessAvgUseTimeNano());
	}

	public String getSuccessAvgUseTimeExcludeChildren() {
		return nanoToMillis(getSuccessAvgUseTimeNanoExcludeChildren());
	}

	public String getErrorAvgUseTime() {
		return nanoToMillis(getErrorAvgUseTimeNano());
	}

	public String getErrorAvgUseTimeExcludeChildren() {
		return nanoToMillis(getErrorAvgUseTimeNanoExcludeChildren());
	}

	/** 纳秒转换为毫秒显示 */
	private String nanoToMillis(long nano) {
		BigDecimal n = BigDecimal.valueOf(nano).divide(BigDecimal.valueOf(1000000), 2, RoundingMode.HALF_UP);
		return n.toPlainString();
	}

}
