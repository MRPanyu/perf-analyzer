package perfanalyzer.core.model;

/**
 * 表示一个时间段内（目前设定是一分钟）的性能统计信息的统计组
 * 
 * @author panyu
 *
 */
public class PerfStatisticsTimedGroup extends PerfStatisticsGroup {

	private static final long serialVersionUID = -8830960785458101008L;

	/** 统计信息记录区间开始时间，一般时某一分钟的00秒 */
	private long statisticsStartTime;
	/** 统计信息记录区间结束时间（不含），一般是开始时间60秒后 */
	private long statisticsEndTime;

	public PerfStatisticsTimedGroup(long statisticsStartTime, long statisticsEndTime) {
		super();
		this.statisticsStartTime = statisticsStartTime;
		this.statisticsEndTime = statisticsEndTime;
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
}
