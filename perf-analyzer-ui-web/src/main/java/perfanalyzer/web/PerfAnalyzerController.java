package perfanalyzer.web;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import perfanalyzer.core.io.PerfIOFileImpl;
import perfanalyzer.core.model.PerfStatisticsTimedGroup;

@RestController
public class PerfAnalyzerController {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	protected PerfAnalyzerProperties perfAnalyzerProperties;

	@RequestMapping("/getRecordFiles")
	public List<String> getRecordFiles() {
		return perfAnalyzerProperties.getRecordFiles();
	}

	@RequestMapping("/getTimeGroups")
	public List<String> getTimeGroups(@RequestParam("fileIdx") int fileIdx) {
		List<Serializable> allItems = loadAllGroups(fileIdx);
		List<String> groups = new ArrayList<String>();
		for (Serializable item : allItems) {
			if (item instanceof PerfStatisticsTimedGroup) {
				PerfStatisticsTimedGroup g = (PerfStatisticsTimedGroup) item;
				String gs = timeStr(g.getStatisticsStartTime());
				groups.add(gs);
			}
		}
		return groups;
	}

	@RequestMapping("/getGroupData")
	public PerfStatisticsTimedGroup getGroupData(@RequestParam("fileIdx") int fileIdx,
			@RequestParam("groupIdx") int groupIdx) {
		List<Serializable> allItems = loadAllGroups(fileIdx);
		if (groupIdx >= allItems.size()) {
			return null;
		} else {
			PerfStatisticsTimedGroup g = (PerfStatisticsTimedGroup) allItems.get(groupIdx);
			return g;
		}
	}

	private List<Serializable> loadAllGroups(int fileIdx) {
		String file = perfAnalyzerProperties.getRecordFiles().get(fileIdx);
		File f = new File(file);
		if (!f.exists()) {
			return Collections.emptyList();
		}
		PerfIOFileImpl perfIO = new PerfIOFileImpl(f);
		List<Serializable> allItems = perfIO.loadAll();
		return allItems;
	}

	private String timeStr(long d) {
		SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
		return fmt.format(d);
	}

}
