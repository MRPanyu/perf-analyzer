package perfanalyzer.agent.bytebuddy;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Callable;

import net.bytebuddy.implementation.bind.annotation.Argument;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;
import perfanalyzer.agent.support.SupportObject;
import perfanalyzer.core.model.NodeType;
import perfanalyzer.core.recorder.PerfRecorder;

/**
 * {@link java.sql.Statement}, {@link java.sql.PreparedStatement} 以及
 * {@link java.sql.CallableStatement} 的拦截代理类
 * 
 * @author panyu
 *
 */
public class StatementDelegation {

	/* java.sql.Statement 相关方法 */

	public static ResultSet executeQuery(@Argument(0) String sql, @SuperCall Callable<ResultSet> superCall,
			@This Object thisObj) throws SQLException {
		String name = sqlToName(sql);
		try {
			PerfRecorder.start(name, NodeType.SQL);
			ResultSet rs = superCall.call();
			PerfRecorder.end(false);
			return rs;
		} catch (Throwable e) {
			PerfRecorder.end(true);
			return rethrowException(e);
		}
	}

	public static int executeUpdate(@Argument(0) String sql, @SuperCall Callable<Integer> superCall,
			@This Object thisObj) throws SQLException {
		String name = sqlToName(sql);
		try {
			PerfRecorder.start(name, NodeType.SQL);
			Integer returnVal = superCall.call();
			PerfRecorder.end(false);
			return returnVal;
		} catch (Throwable e) {
			PerfRecorder.end(true);
			return rethrowException(e);
		}
	}

	public static boolean execute(@Argument(0) String sql, @SuperCall Callable<Boolean> superCall, @This Object thisObj)
			throws SQLException {
		String name = sqlToName(sql);
		try {
			PerfRecorder.start(name, NodeType.SQL);
			Boolean returnVal = superCall.call();
			PerfRecorder.end(false);
			return returnVal;
		} catch (Throwable e) {
			PerfRecorder.end(true);
			return rethrowException(e);
		}
	}

	/* java.sql.PreparedStatement/java.sql.CallableStatement 相关方法 */

	public static ResultSet executeQuery(@SuperCall Callable<ResultSet> superCall, @This Object thisObj)
			throws SQLException {
		SupportObject s = SupportObject.getSupportObject(thisObj);
		String name = s == null ? null : sqlToName(s.getSql());
		try {
			if (name != null) {
				PerfRecorder.start(name, NodeType.SQL);
			}
			ResultSet rs = superCall.call();
			if (name != null) {
				PerfRecorder.end(false);
			}
			return rs;
		} catch (Throwable e) {
			if (name != null) {
				PerfRecorder.end(true);
			}
			return rethrowException(e);
		}
	}

	public static int executeUpdate(@SuperCall Callable<Integer> superCall, @This Object thisObj) throws SQLException {
		SupportObject s = SupportObject.getSupportObject(thisObj);
		String name = s == null ? null : sqlToName(s.getSql());
		try {
			if (name != null) {
				PerfRecorder.start(name, NodeType.SQL);
			}
			Integer returnVal = superCall.call();
			if (name != null) {
				PerfRecorder.end(false);
			}
			return returnVal;
		} catch (Throwable e) {
			if (name != null) {
				PerfRecorder.end(true);
			}
			return rethrowException(e);
		}
	}

	public static boolean execute(@SuperCall Callable<Boolean> superCall, @This Object thisObj) throws SQLException {
		SupportObject s = SupportObject.getSupportObject(thisObj);
		String name = s == null ? null : sqlToName(s.getSql());
		try {
			if (name != null) {
				PerfRecorder.start(name, NodeType.SQL);
			}
			Boolean returnVal = superCall.call();
			if (name != null) {
				PerfRecorder.end(false);
			}
			return returnVal;
		} catch (Throwable e) {
			if (name != null) {
				PerfRecorder.end(true);
			}
			return rethrowException(e);
		}
	}

	private static String sqlToName(String sql) {
		return new StringBuilder("[SQL]{").append(sql).append("}").toString();
	}

	private static <T> T rethrowException(Throwable e) throws SQLException {
		if (e instanceof SQLException) {
			throw (SQLException) e;
		} else if (e instanceof RuntimeException) {
			throw (RuntimeException) e;
		} else if (e instanceof Error) {
			throw (Error) e;
		} else {
			throw new RuntimeException(e);
		}
	}

}
