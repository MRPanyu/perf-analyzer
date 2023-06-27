package perfanalyzer.core.io;

import java.io.File;
import java.text.SimpleDateFormat;

import org.junit.Test;

import perfanalyzer.core.model.PerfStatisticsTimedGroup;

public class RollingFilePerfOutputTest {

	@Test
	public void test() throws Exception {
		File file = new File("target/test/perf_record.prec");
		file.getParentFile().mkdirs();
		RollingFilePerfOutput out = new RollingFilePerfOutput(file);
		out.write(buildGroup("2023-06-01 21:00:00"));
		out.write(buildGroup("2023-06-01 21:01:00"));
	}
	
	private PerfStatisticsTimedGroup buildGroup(String dateTime) throws Exception {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long startTime = fmt.parse(dateTime).getTime();
		long endTime = startTime + 60000L;
		PerfStatisticsTimedGroup grp = new PerfStatisticsTimedGroup(startTime, endTime);
		return grp;
	}

}
