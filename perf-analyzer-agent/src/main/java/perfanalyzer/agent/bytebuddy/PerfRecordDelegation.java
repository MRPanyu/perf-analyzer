package perfanalyzer.agent.bytebuddy;

import java.util.concurrent.Callable;

import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import perfanalyzer.core.recorder.PerfRecorder;

public class PerfRecordDelegation {

	@RuntimeType
	public static Object intercept(@SuperCall Callable<Object> superCall, @Origin String name) throws Exception {
		boolean isError = false;
		try {
			PerfRecorder.start(name);
			Object returnValue = superCall.call();
			return returnValue;
		} catch (Exception e) {
			isError = true;
			throw e;
		} finally {
			PerfRecorder.end(isError);
		}
	}

}
