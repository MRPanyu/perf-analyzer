package perfanalyzer.core.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import perfanalyzer.core.model.PerfStatisticsTimedGroup;

public class FilePerfInput implements PerfInput {

	private File file;

	public FilePerfInput(File file) {
		this.file = file;
	}

	@Override
	public List<PerfStatisticsTimedGroup> readAll() {
		try (FileInputStream fin = new FileInputStream(file)) {
			List<PerfStatisticsTimedGroup> groups = new ArrayList<>();
			PerfStatisticsTimedGroup group = PerfIOUtils.read(fin);
			while (group != null) {
				groups.add(group);
				group = PerfIOUtils.read(fin);
			}
			return groups;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<PerfStatisticsTimedGroup> readHeads() {
		try (FileInputStream fin = new FileInputStream(file)) {
			List<PerfStatisticsTimedGroup> groups = new ArrayList<>();
			PerfStatisticsTimedGroup group = PerfIOUtils.readHead(fin);
			while (group != null) {
				groups.add(group);
				group = PerfIOUtils.readHead(fin);
			}
			return groups;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public PerfStatisticsTimedGroup read(int index) {
		try (FileInputStream fin = new FileInputStream(file)) {
			for (int i = 0; i < index; i++) {
				PerfIOUtils.readHead(fin);
			}
			PerfStatisticsTimedGroup group = PerfIOUtils.read(fin);
			return group;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

}
