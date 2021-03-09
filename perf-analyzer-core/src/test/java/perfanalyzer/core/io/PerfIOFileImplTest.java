package perfanalyzer.core.io;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import org.junit.Test;

import perfanalyzer.core.model.PerfStatisticsGroup;
import perfanalyzer.core.model.PerfStatisticsTimedGroup;

public class PerfIOFileImplTest {

	@Test
	public void test() throws Exception {
		File f = File.createTempFile("perf_io_", ".prec");
		PerfIOFileImpl io = new PerfIOFileImpl(f);
		PerfStatisticsTimedGroup group1 = new PerfStatisticsTimedGroup(0, 1);
		io.saveItem(group1);
		PerfStatisticsGroup group2 = new PerfStatisticsTimedGroup(1, 2);
		io.saveItem(group2);
		PerfStatisticsGroup group3 = new PerfStatisticsTimedGroup(2, 3);
		io.saveItem(group3);
		List<Serializable> list = io.loadAll();
		assertEquals(3, list.size());
	}

}
