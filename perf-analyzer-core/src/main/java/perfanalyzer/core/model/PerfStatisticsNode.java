package perfanalyzer.core.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * 性能节点统计数据，表示某个代码块（如方法执行等）若干次执行的统计信息。
 * 
 * @author panyu
 *
 */
public class PerfStatisticsNode implements Serializable {

	private static final long serialVersionUID = -7876077102960270051L;

	private static long safeDivide(long a, long b) {
		if (b == 0) {
			return 0;
		}
		return a / b;
	}

	/** 完整的执行节点路径 */
	private NodePath path;
	/** 执行成功的次数 */
	private long successCount = 0L;
	/** 执行错误/异常的次数 */
	private long errorCount = 0L;
	/** 执行成功的单次最大耗时纳秒数 */
	private long successMaxUseTimeNano = 0L;
	/** 执行成功的单次最大除子节点外的耗时纳秒数 */
	private long successMaxUseTimeNanoExcludeChildren = 0L;
	/** 执行错误/异常的单次最大耗时纳秒数 */
	private long errorMaxUseTimeNano = 0L;
	/** 执行错误/异常的单次最大除子节点外的耗时纳秒数 */
	private long errorMaxUseTimeNanoExcludeChildren = 0L;
	/** 执行成功的总耗时纳秒数 */
	private long successTotalUseTimeNano = 0L;
	/** 执行成功的除子节点外总耗时纳秒数 */
	private long successTotalUseTimeNanoExcludeChildren = 0L;
	/** 执行错误/异常的总耗时纳秒数 */
	private long errorTotalUseTimeNano = 0L;
	/** 执行错误/异常的除子节点外总耗时纳秒数 */
	private long errorTotalUseTimeNanoExcludeChildren = 0L;

	/** 统计信息的子节点 */
	private List<PerfStatisticsNode> children = new ArrayList<PerfStatisticsNode>();

	protected PerfStatisticsNode() {
	}

	public PerfStatisticsNode(NodePath path) {
		this.path = path;
	}

	/** 将单次执行结果汇总到本次统计信息中，次数+1，执行时间累加，最大执行时间取最大值等 */
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

	/** 将一个执行汇总信息累加到本地汇总信息当中 */
	public synchronized void mergeStatisticsNode(PerfStatisticsNode stNode) {
		this.successCount += stNode.successCount;
		this.successTotalUseTimeNano += stNode.successTotalUseTimeNano;
		this.successTotalUseTimeNanoExcludeChildren += stNode.successTotalUseTimeNanoExcludeChildren;
		if (stNode.successMaxUseTimeNano > this.successMaxUseTimeNano) {
			this.successMaxUseTimeNano = stNode.successMaxUseTimeNano;
		}
		if (stNode.successMaxUseTimeNanoExcludeChildren > this.successMaxUseTimeNanoExcludeChildren) {
			this.successMaxUseTimeNanoExcludeChildren = stNode.successMaxUseTimeNanoExcludeChildren;
		}
		this.errorCount += stNode.errorCount;
		this.errorTotalUseTimeNano += stNode.errorTotalUseTimeNano;
		this.errorTotalUseTimeNanoExcludeChildren += stNode.errorTotalUseTimeNanoExcludeChildren;
		if (stNode.errorMaxUseTimeNano > this.errorMaxUseTimeNano) {
			this.errorMaxUseTimeNano = stNode.errorMaxUseTimeNano;
		}
		if (stNode.errorMaxUseTimeNanoExcludeChildren > this.errorMaxUseTimeNanoExcludeChildren) {
			this.errorMaxUseTimeNanoExcludeChildren = stNode.errorMaxUseTimeNanoExcludeChildren;
		}
	}

	public String getName() {
		return path.getName();
	}

	public NodePath getPath() {
		return path;
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
