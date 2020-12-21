package perfanalyzer.core.io;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
			ObjectOutputStream out = new AppendingFileObjectOutputStream(file);
			try {
				out.writeObject(group);
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
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
			try {
				while (true) {
					PerfStatisticsGroup group = (PerfStatisticsGroup) in.readObject();
					list.add(group);
				}
			} catch (EOFException e) {
				// ignore EOF
			} finally {
				in.close();
			}
			return list;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
