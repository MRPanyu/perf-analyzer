package perfanalyzer.agent.support;

import java.io.Serializable;
import java.util.WeakHashMap;

/**
 * 创建PreparedStatement/CallableStatement时保存SQL用的辅助对象
 * 
 * @author panyu
 *
 */
public class SupportObject implements Serializable {

	private static final long serialVersionUID = 1L;

	private static WeakHashMap<Object, SupportObject> supportObjectMap = new WeakHashMap<Object, SupportObject>(256);

	public static SupportObject getSupportObject(Object obj) {
		return supportObjectMap.get(obj);
	}

	public static boolean hasSupportObject(Object obj) {
		return supportObjectMap.containsKey(obj);
	}

	public static SupportObject createSupportObject(Object obj) {
		SupportObject s = supportObjectMap.get(obj);
		if (s == null) {
			synchronized (SupportObject.class) {
				s = supportObjectMap.get(obj);
				if (s == null) {
					s = new SupportObject();
					supportObjectMap.put(obj, s);
				}
			}
		}
		return s;
	}

	private String sql;
	private int batchCount = 0;

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public int getBatchCount() {
		return batchCount;
	}

	public void setBatchCount(int batchCount) {
		this.batchCount = batchCount;
	}

	public void incrementBatchCount() {
		this.batchCount++;
	}

}
