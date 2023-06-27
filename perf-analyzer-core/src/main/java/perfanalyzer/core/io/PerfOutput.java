package perfanalyzer.core.io;

import perfanalyzer.core.model.PerfStatisticsTimedGroup;

public interface PerfOutput {

	public void write(PerfStatisticsTimedGroup group);

}
