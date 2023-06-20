package perfanalyzer.agent.bytebuddy.jdbc;

import net.bytebuddy.asm.Advice.Argument;
import net.bytebuddy.asm.Advice.OnMethodEnter;
import net.bytebuddy.asm.Advice.OnMethodExit;
import net.bytebuddy.asm.Advice.This;
import net.bytebuddy.asm.Advice.Thrown;
import perfanalyzer.agent.jdbc.JdbcRecorder;

/**
 * Advice for {@link java.sql.Statement#execute(String)},
 * {@link java.sql.Statement#executeQuery(String)},
 * {@link java.sql.Statement#executeUpdate(String)}
 * 
 * @author panyu
 *
 */
public class StatementExecuteSqlAdvice {

	@OnMethodEnter
	public static void onMethodEnter(@Argument(0) String sql, @This Object thisObj) {
		JdbcRecorder.of(thisObj).sql(sql).start();
	}

	@OnMethodExit(onThrowable = Throwable.class)
	public static void onMethodExit(@Thrown Throwable t, @This Object thisObj) {
		JdbcRecorder.of(thisObj).end(t != null);
	}

}
