package perfanalyzer.agent.bytebuddy.jdbc;

import net.bytebuddy.asm.Advice.Argument;
import net.bytebuddy.asm.Advice.OnMethodExit;
import net.bytebuddy.asm.Advice.Return;
import perfanalyzer.agent.jdbc.JdbcRecorder;

/**
 * Advice for {@link java.sql.Connection#prepareStatement(String)} and
 * {@link java.sql.Connection#prepareCall(String)}
 * 
 * @author panyu
 *
 */
public class ConnectionPrepareAdvice {

	@OnMethodExit
	public static void onMethodExit(@Argument(0) String sql, @Return Object statement) {
		JdbcRecorder.of(statement).sql(sql);
	}

}
