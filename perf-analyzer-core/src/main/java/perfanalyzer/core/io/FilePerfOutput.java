package perfanalyzer.core.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import perfanalyzer.core.model.PerfStatisticsTimedGroup;

public class FilePerfOutput implements PerfOutput {

	private File file;

	public FilePerfOutput(File file) {
		this.file = file;
	}

	public FilePerfOutput(String path) {
		this.file = new File(path);
	}

	@Override
	public void write(PerfStatisticsTimedGroup group) {
		try (FileOutputStream fout = new FileOutputStream(file, true)) {
			PerfIOUtils.write(fout, group);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
