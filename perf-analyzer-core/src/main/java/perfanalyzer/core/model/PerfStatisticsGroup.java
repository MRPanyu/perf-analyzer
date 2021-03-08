package perfanalyzer.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计组对象，一个统计组包括某一个所述时间段内（一般是1分钟）所有统计信息
 * 
 * @author panyu
 *
 */
public class PerfStatisticsGroup implements Serializable {

	private static final long serialVersionUID = -6427273552511768812L;

	/** 统计信息记录区间开始时间，一般时某一分钟的00秒 */
	private long statisticsStartTime;
	/** 统计信息记录区间结束时间（不含），一般是开始时间60秒后 */
	private long statisticsEndTime;

	/** 这一分钟内所有根节点代码块的统计数据 */
	private List<PerfStatisticsNode> rootNodes = new ArrayList<PerfStatisticsNode>();
	/** 根据path快速查询代码块统计节点的map，只在记录的时候用于检索用，因此是transient不写入文件 */
	private transient Map<String, PerfStatisticsNode> allNodeMap = new HashMap<String, PerfStatisticsNode>();

	protected PerfStatisticsGroup() {
	}

	public PerfStatisticsGroup(long statisticsStartTime, long statisticsEndTime) {
		this.statisticsStartTime = statisticsStartTime;
		this.statisticsEndTime = statisticsEndTime;
	}

	/** 根据path找到已有统计信息节点，如果不存在则创建一个 */
	public PerfStatisticsNode getOrCreateNode(String name, String path, String parentPath) {
		PerfStatisticsNode node = allNodeMap.get(path);
		if (node == null) {
			synchronized (this) {
				node = allNodeMap.get(path);
				if (node == null) {
					PerfStatisticsNode parent = null;
					if (parentPath != null) {
						parent = allNodeMap.get(parentPath);
					}
					node = new PerfStatisticsNode(name, parentPath);
					if (parent == null) {
						rootNodes.add(node);
					} else {
						parent.getChildren().add(node);
					}
					allNodeMap.put(path, node);
				}
			}
		}
		return node;
	}

	public long getStatisticsStartTime() {
		return statisticsStartTime;
	}

	public void setStatisticsStartTime(long statisticsStartTime) {
		this.statisticsStartTime = statisticsStartTime;
	}

	public long getStatisticsEndTime() {
		return statisticsEndTime;
	}

	public void setStatisticsEndTime(long statisticsEndTime) {
		this.statisticsEndTime = statisticsEndTime;
	}

	public List<PerfStatisticsNode> getRootNodes() {
		return rootNodes;
	}

	public void setRootNodes(List<PerfStatisticsNode> rootNodes) {
		this.rootNodes = rootNodes;
	}

}
