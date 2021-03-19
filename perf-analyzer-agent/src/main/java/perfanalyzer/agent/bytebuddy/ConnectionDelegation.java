package perfanalyzer.agent.bytebuddy;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.Callable;

import net.bytebuddy.implementation.bind.annotation.Argument;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;
import perfanalyzer.agent.support.SupportObject;

/**
 * {@link java.sql.Connection} 拦截代理类
 * 
 * @author panyu
 *
 */
public class ConnectionDelegation {

	public static PreparedStatement prepareStatement(@Argument(0) String sql,
			@SuperCall Callable<PreparedStatement> superCall, @This Object thisObj) throws SQLException {
		try {
			PreparedStatement ps = superCall.call();
			if (SupportObject.hasSupportObject(thisObj)) {
				SupportObject s = SupportObject.createSupportObject(ps);
				s.setSql(sql);
			}
			return ps;
		} catch (Throwable e) {
			return rethrowException(e);
		}
	}

	public static CallableStatement prepareCall(@Argument(0) String sql,
			@SuperCall Callable<CallableStatement> superCall, @This Object thisObj) throws SQLException {
		try {
			CallableStatement ps = superCall.call();
			if (SupportObject.hasSupportObject(thisObj)) {
				SupportObject s = SupportObject.createSupportObject(ps);
				s.setSql(sql);
			}
			return ps;
		} catch (Throwable e) {
			return rethrowException(e);
		}
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
