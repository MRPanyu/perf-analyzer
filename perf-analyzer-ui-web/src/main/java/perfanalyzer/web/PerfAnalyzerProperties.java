package perfanalyzer.web;

import java.util.ArrayList;
import java.util.List;

public class PerfAnalyzerProperties {

	private List<String> recordFiles = new ArrayList<>();

	public List<String> getRecordFiles() {
		return recordFiles;
	}

	public void setRecordFiles(List<String> recordFiles) {
		this.recordFiles = recordFiles;
	}

}
