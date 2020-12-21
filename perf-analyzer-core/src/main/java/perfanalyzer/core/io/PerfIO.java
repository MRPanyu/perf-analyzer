package perfanalyzer.core.io;

import java.util.List;

import perfanalyzer.core.model.PerfStatisticsGroup;

public interface PerfIO {

	/** 追加写入统计信息 */
	public void savePerfStatisticsGroup(PerfStatisticsGroup group);

	/** 读取所有统计信息 */
	public List<PerfStatisticsGroup> loadPerfStatisticsGroups();

}
