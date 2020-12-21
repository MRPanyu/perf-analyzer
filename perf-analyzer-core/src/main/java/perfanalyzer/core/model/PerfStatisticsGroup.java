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

	private static final long serialVersionUID = 1768780201223480270L;

	private long statisticsStartTime;
	private long statisticsEndTime;

	private List<PerfStatisticsNode> rootNodes = new ArrayList<PerfStatisticsNode>();
	private Map<String, PerfStatisticsNode> allNodeMap = new HashMap<String, PerfStatisticsNode>();

	protected PerfStatisticsGroup() {
	}

	public PerfStatisticsGroup(long statisticsStartTime, long statisticsEndTime) {
		this.statisticsStartTime = statisticsStartTime;
		this.statisticsEndTime = statisticsEndTime;
	}

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
					node = new PerfStatisticsNode(name, parentPath, parent);
					if (parent == null) {
						rootNodes.add(node);
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
