package perfanalyzer.core.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 用文件存储性能统计信息。
 * <p>
 * 存储的文件是每分钟写入的时候逐段追加的模式，每次写入的时候短暂地打开文件进行追加后关闭，不会长时间打开文件，因此一般可以随时提取文件进行分析。
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
	public void saveItem(Serializable item) {
		try {
			FileOutputStream out = new FileOutputStream(file, true);
			try {
				PerfIOSupport.writeObject(out, item);
			} finally {
				out.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<Serializable> loadAll() {
		try {
			List<Serializable> list = new ArrayList<Serializable>();
			FileInputStream in = new FileInputStream(file);
			try {
				while (true) {
					Serializable item = PerfIOSupport.readObject(in);
					if (item == null) {
						break;
					}
					list.add(item);
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
