package perfanalyzer.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计组对象
 * <p>
 * 一个统计组包含若干个统计信息根节点，并提供整体节点维护能力。
 * 
 * @author panyu
 *
 */
public class PerfStatisticsGroup implements Serializable {

	private static final long serialVersionUID = 2222197306369221370L;

	/** 组内所有根节点代码块的统计数据 */
	private List<PerfStatisticsNode> rootNodes = new ArrayList<PerfStatisticsNode>();
	/** 根据path快速查询代码块统计节点的map，只在记录的时候用于检索用，因此是transient不写入文件 */
	private transient Map<NodePath, PerfStatisticsNode> allNodeMap = new HashMap<NodePath, PerfStatisticsNode>();

	public PerfStatisticsGroup() {
	}

	/** 根据path找到已有统计信息节点，如果不存在则创建一个 */
	public PerfStatisticsNode getOrCreateNode(NodePath path) {
		PerfStatisticsNode node = allNodeMap.get(path);
		if (node == null) {
			synchronized (this) {
				node = allNodeMap.get(path);
				if (node == null) {
					node = new PerfStatisticsNode(path);
					NodePath parentPath = path.getParentPath();
					if (parentPath == null) {
						rootNodes.add(node);
					} else {
						PerfStatisticsNode parent = getOrCreateNode(parentPath);
						parent.getChildren().add(node);
					}
					allNodeMap.put(path, node);
				}
			}
		}
		return node;
	}

	/** 将另一个统计组中的信息累加到本统计组中 */
	public void mergeStatisticsGroup(PerfStatisticsGroup group) {
		List<PerfStatisticsNode> rootNodes = group.getRootNodes();
		for (PerfStatisticsNode rootNode : rootNodes) {
			mergeStatisticsNode(rootNode);
		}
	}

	/** 递归合并统计节点信息 */
	private void mergeStatisticsNode(PerfStatisticsNode node) {
		PerfStatisticsNode localNode = getOrCreateNode(node.getPath());
		localNode.mergeStatisticsNode(node);
		for (PerfStatisticsNode childNode : node.getChildren()) {
			mergeStatisticsNode(childNode);
		}
	}

	public List<PerfStatisticsNode> getRootNodes() {
		return rootNodes;
	}

	public void setRootNodes(List<PerfStatisticsNode> rootNodes) {
		this.rootNodes = rootNodes;
	}

}
