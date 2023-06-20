package perfanalyzer.agent.bytebuddy.jdbc;

import net.bytebuddy.asm.Advice.OnMethodEnter;
import net.bytebuddy.asm.Advice.OnMethodExit;
import net.bytebuddy.asm.Advice.This;
import net.bytebuddy.asm.Advice.Thrown;
import perfanalyzer.agent.jdbc.JdbcRecorder;

/**
 * Advice for {@link java.sql.PreparedStatement#execute()},
 * {@link java.sql.PreparedStatement#executeQuery()},
 * {@link java.sql.PreparedStatement#executeUpdate()}
 * 
 * @author panyu
 *
 */
public class StatementExecuteAdvice {

	@OnMethodEnter
	public static void onMethodEnter(@This Object thisObj) {
		JdbcRecorder.of(thisObj).start();
	}

	@OnMethodExit(onThrowable = Throwable.class)
	public static void onMethodExit(@Thrown Throwable t, @This Object thisObj) {
		JdbcRecorder.of(thisObj).end(t != null);
	}

}
