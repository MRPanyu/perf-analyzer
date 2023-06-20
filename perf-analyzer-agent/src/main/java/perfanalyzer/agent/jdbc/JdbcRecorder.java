package perfanalyzer.agent.jdbc;

import java.util.WeakHashMap;

import perfanalyzer.core.model.NodeType;
import perfanalyzer.core.recorder.PerfRecorder;

public class JdbcRecorder {

	private static WeakHashMap<Object, JdbcRecorder> recorderMap = new WeakHashMap<Object, JdbcRecorder>(256);

	public static JdbcRecorder of(Object key) {
		JdbcRecorder r = recorderMap.get(key);
		if (r == null) {
			synchronized (JdbcRecorder.class) {
				r = recorderMap.get(key);
				if (r == null) {
					r = new JdbcRecorder();
					recorderMap.put(key, r);
				}
			}
		}
		return r;
	}

	private String sql;
	private int batchCount = 0;

	public JdbcRecorder sql(String sql) {
		this.sql = sql;
		return this;
	}

	public JdbcRecorder addBatchCount() {
		this.batchCount++;
		return this;
	}

	public void start() {
		if (this.sql != null) {
			String name = "[SQL]{" + sql + "}";
			if (batchCount > 0) {
				name = "[SQL](" + batchCount + "){" + sql + "}";
			}
			PerfRecorder.start(name, NodeType.SQL);
		}
		this.batchCount = 0;
	}

	public void end(boolean isError) {
		if (this.sql != null) {
			PerfRecorder.end(isError);
		}
	}

}
