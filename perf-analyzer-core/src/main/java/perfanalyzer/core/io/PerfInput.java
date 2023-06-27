package perfanalyzer.core.io;

import java.util.List;

import perfanalyzer.core.model.PerfStatisticsTimedGroup;

public interface PerfInput {

	/**
	 * 读取所有对象
	 * 
	 * @return
	 */
	public List<PerfStatisticsTimedGroup> readAll();

	/**
	 * 读取所有对象的头信息（仅包含 statisticsStartTime / statisticsEndTime）
	 * 
	 * @return
	 */
	public List<PerfStatisticsTimedGroup> readHeads();

	/**
	 * 读取第N个对象
	 * 
	 * @param index
	 * @return
	 */
	public PerfStatisticsTimedGroup read(int index);

}
