package perfanalyzer.core.io;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.Test;

import perfanalyzer.core.model.PerfStatisticsGroup;

public class PerfIOFileImplTest {

	@Test
	public void test() throws Exception {
		File f = File.createTempFile("perf_io_", ".prec");
		PerfIOFileImpl io = new PerfIOFileImpl(f);
		PerfStatisticsGroup group1 = new PerfStatisticsGroup(0, 1);
		io.savePerfStatisticsGroup(group1);
		PerfStatisticsGroup group2 = new PerfStatisticsGroup(1, 2);
		io.savePerfStatisticsGroup(group2);
		PerfStatisticsGroup group3 = new PerfStatisticsGroup(2, 3);
		io.savePerfStatisticsGroup(group3);
		List<PerfStatisticsGroup> list = io.loadPerfStatisticsGroups();
		assertEquals(3, list.size());
	}

}
