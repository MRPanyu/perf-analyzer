package perfanalyzer.core.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import perfanalyzer.core.model.PerfStatisticsTimedGroup;

/**
 * 每小时生成一个新文件，加后缀.yyyyMMddHH
 * <p>
 * 避免单个文件过大无法读取分析
 * 
 * @author panyu
 *
 */
public class RollingFilePerfOutput implements PerfOutput {

	private File file;

	private long lastStartTime = 0;

	public RollingFilePerfOutput(File file) {
		this.file = file;
		if (file.isFile()) {
			PerfStatisticsTimedGroup grp0 = null;
			try (FileInputStream fin = new FileInputStream(file)) {
				grp0 = PerfIOUtils.readHead(fin);
			} catch (IOException e) {
			}
			if (grp0 == null) {
				file.delete();
			} else {
				lastStartTime = grp0.getStatisticsStartTime();
			}
		}
	}

	public RollingFilePerfOutput(String path) {
		this(new File(path));
	}

	@Override
	public void write(PerfStatisticsTimedGroup group) {
		if (lastStartTime > 0) {
			String lastHour = fmtTime(lastStartTime);
			String currentHour = fmtTime(group.getStatisticsStartTime());
			if (!lastHour.equals(currentHour)) {
				rollFile(lastHour);
			}
		}
		lastStartTime = group.getStatisticsStartTime();
		try (FileOutputStream fout = new FileOutputStream(file, true)) {
			PerfIOUtils.write(fout, group);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void rollFile(String hour) {
		File rf = new File(file.getParentFile(), file.getName() + "." + hour);
		file.renameTo(rf);
	}

	private String fmtTime(long time) {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHH");
		return fmt.format(time);
	}

}
