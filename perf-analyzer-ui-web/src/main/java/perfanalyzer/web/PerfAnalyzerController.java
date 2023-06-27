package perfanalyzer.web;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import perfanalyzer.core.io.FilePerfInput;
import perfanalyzer.core.io.PerfInput;
import perfanalyzer.core.model.PerfStatisticsTimedGroup;

@RestController
public class PerfAnalyzerController {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	protected PerfAnalyzerProperties perfAnalyzerProperties;

	@RequestMapping("/getRecordFiles")
	public LinkedHashMap<String, String> getRecordFiles() throws Exception {
		List<String> recordFiles = perfAnalyzerProperties.getRecordFiles();
		LinkedHashMap<String, String> map = new LinkedHashMap<>();
		for (String recordFile : recordFiles) {
			File f = new File(recordFile);
			File[] farr = f.getParentFile().listFiles(ff -> {
				return ff.getName().startsWith(f.getName());
			});
			for (File ff : farr) {
				String path = ff.getCanonicalPath();
				String key = DigestUtils.md5DigestAsHex(path.getBytes("UTF-8"));
				map.put(key, path);
			}
		}
		return map;
	}

	@RequestMapping("/getTimeGroups")
	public List<String> getTimeGroups(@RequestParam("fileKey") String fileKey) throws Exception {
		PerfInput input = getInput(fileKey);
		if (input == null) {
			return Collections.emptyList();
		}
		List<PerfStatisticsTimedGroup> groups = input.readHeads();
		List<String> names = new ArrayList<String>();
		for (PerfStatisticsTimedGroup group : groups) {
			String name = timeStr(group.getStatisticsStartTime());
			names.add(name);
		}
		return names;
	}

	@RequestMapping("/getGroupData")
	public PerfStatisticsTimedGroup getGroupData(@RequestParam("fileKey") String fileKey,
			@RequestParam("groupIdx") int groupIdx) throws Exception {
		PerfInput input = getInput(fileKey);
		if (input == null) {
			return null;
		} else {
			return input.read(groupIdx);
		}
	}

	private PerfInput getInput(String fileKey) throws Exception {
		String file = getRecordFiles().get(fileKey);
		if (file == null) {
			return null;
		}
		File f = new File(file);
		if (!f.exists()) {
			return null;
		}
		FilePerfInput input = new FilePerfInput(f);
		return input;
	}

	private String timeStr(long d) {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return fmt.format(d);
	}

}
