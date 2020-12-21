package perfanalyzer.core.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import perfanalyzer.core.model.PerfStatisticsGroup;

/**
 * 指定文件存储性能日志信息。
 * <p>
 * 存储的文件是每分钟写入的时候逐段追加的模式，每次写入的时候短暂地打开文件进行追加后关闭，因此一般可以随时提取文件进行分析。
 * 
 * @author panyu
 *
 */
public class PerfIOFileImpl implements PerfIO {

	private File file;

	public PerfIOFileImpl(File file) {
		this.file = file;
	}

	@Override
	public void savePerfStatisticsGroup(PerfStatisticsGroup group) {
		try {
			FileOutputStream out = new FileOutputStream(file, true);
			try {
				PerfIOSupport.writePerfStatisticsGroup(out, group);
			} finally {
				out.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<PerfStatisticsGroup> loadPerfStatisticsGroups() {
		try {
			List<PerfStatisticsGroup> list = new ArrayList<PerfStatisticsGroup>();
			FileInputStream in = new FileInputStream(file);
			try {
				while (true) {
					PerfStatisticsGroup group = PerfIOSupport.readPerfStatisticsGroup(in);
					if (group == null) {
						break;
					}
					list.add(group);
				}
			} finally {
				in.close();
			}
			return list;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
