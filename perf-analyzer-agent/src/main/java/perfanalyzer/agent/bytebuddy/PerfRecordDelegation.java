package perfanalyzer.agent.bytebuddy;

import java.util.concurrent.Callable;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.IgnoreForBinding;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import perfanalyzer.core.recorder.PerfRecorder;

public class PerfRecordDelegation {

	@RuntimeType
	public static Object intercept(@SuperCall Callable<Object> superCall, @AllArguments Object[] args,
			@Origin String name) throws Exception {
		boolean isError = false;
		try {
			PerfRecorder.start(simpleName(name));
			Object returnValue = superCall.call();
			return returnValue;
		} catch (Exception e) {
			isError = true;
			throw e;
		} finally {
			PerfRecorder.end(isError);
		}
	}

	@IgnoreForBinding
	private static String simpleName(String name) {
		int beginIndex = 0;
		int endIndex = 0;
		boolean foundBracket = false;
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if (c == ' ') {
				if (foundBracket) {
					endIndex = i;
					break;
				} else {
					beginIndex = i + 1;
				}
			} else if (c == '(') {
				foundBracket = true;
			}
		}
		if (foundBracket) {
			if (endIndex == 0) {
				endIndex = name.length();
			}
			return name.substring(beginIndex, endIndex);
		} else {
			return name;
		}
	}

}
