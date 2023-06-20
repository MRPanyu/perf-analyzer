package perfanalyzer.agent.bytebuddy.jdbc;

import net.bytebuddy.asm.Advice.Argument;
import net.bytebuddy.asm.Advice.OnMethodExit;
import net.bytebuddy.asm.Advice.This;
import perfanalyzer.agent.jdbc.JdbcRecorder;

/**
 * Advice for {@link java.sql.Statement#addBatch(String)}
 * 
 * @author panyu
 *
 */
public class StatementAddBatchSqlAdvice {

	@OnMethodExit
	public static void onMethodExit(@This Object thisObj, @Argument(0) String sql) {
		JdbcRecorder.of(thisObj).sql(sql).addBatchCount();
	}

}
