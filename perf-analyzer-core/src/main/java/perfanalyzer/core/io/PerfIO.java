package perfanalyzer.core.io;

import java.io.Serializable;
import java.util.List;

/**
 * 性能统计信息读写接口
 * 
 * @author panyu
 *
 */
public interface PerfIO {

	/** 追加写入信息 */
	public void saveItem(Serializable item);

	/** 读取所有对象 */
	public List<Serializable> loadAll();

}
