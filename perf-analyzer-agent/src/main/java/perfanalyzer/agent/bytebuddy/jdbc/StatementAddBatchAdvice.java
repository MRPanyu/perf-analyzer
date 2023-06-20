package perfanalyzer.agent.bytebuddy.jdbc;

import net.bytebuddy.asm.Advice.OnMethodExit;
import net.bytebuddy.asm.Advice.This;
import perfanalyzer.agent.jdbc.JdbcRecorder;

/**
 * Advice for {@link java.sql.PreparedStatement#addBatch()}
 * 
 * @author panyu
 *
 */
public class StatementAddBatchAdvice {

	@OnMethodExit
	public static void onMethodExit(@This Object thisObj) {
		JdbcRecorder.of(thisObj).addBatchCount();
	}

}
