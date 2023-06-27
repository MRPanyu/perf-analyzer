package perfanalyzer.agent.bytebuddy;

import net.bytebuddy.asm.Advice.OnMethodEnter;
import net.bytebuddy.asm.Advice.OnMethodExit;
import net.bytebuddy.asm.Advice.Origin;
import net.bytebuddy.asm.Advice.Thrown;
import net.bytebuddy.implementation.bind.annotation.IgnoreForBinding;
import perfanalyzer.core.recorder.PerfRecorder;

public class PerfRecordStaticMethodAdvice {

	@OnMethodEnter
	public static void onMethodEnter(@Origin String name) {
		PerfRecorder.start(methodName(name));
	}

	@OnMethodExit(onThrowable = Throwable.class)
	public static void onMethodExit(@Thrown Throwable t) {
		PerfRecorder.end(t != null);
	}

	/**
	 * 对ByteBuddy取到的方法签名进行处理
	 * <p>
	 * ByteBuddy取到的方法签名是带修饰符和throws部分的，如<br/>
	 * <code>public static java.lang.String com.example.SomeClass.someMethod(java.lang.String) throws java.lang.Exception</code>
	 * <p>
	 * 这个方法会去掉修饰符（public static）和异常信息部分。
	 */
	@IgnoreForBinding
	public static String methodName(String name) {
		int beginIndex = 0;
		int endIndex = 0;
		boolean foundBeginBracket = false;
		char[] carr = name.toCharArray();
		for (int i = 0; i < carr.length; i++) {
			char c = carr[i];
			if (c == '(') {
				foundBeginBracket = true;
			} else if (c == ')') {
				endIndex = i + 1;
				break;
			} else if (c == ' ') {
				if (!foundBeginBracket) {
					beginIndex = i + 1;
				}
			}
		}
		if (foundBeginBracket) {
			if (endIndex == 0) {
				endIndex = name.length();
			}
			return name.substring(beginIndex, endIndex);
		} else {
			return name;
		}
	}

}
