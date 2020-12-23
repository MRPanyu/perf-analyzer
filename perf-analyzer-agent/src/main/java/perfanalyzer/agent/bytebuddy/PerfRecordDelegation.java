package perfanalyzer.agent.bytebuddy;

import java.util.concurrent.Callable;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.IgnoreForBinding;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import perfanalyzer.core.recorder.PerfRecorder;

/**
 * ByteBuddy代理类，其中intercept方法拦截所有需要增强的方法进行性能节点记录
 * 
 * @author panyu
 *
 */
public class PerfRecordDelegation {

	/** 增强拦截，进行性能记录 */
	@RuntimeType
	public static Object intercept(@SuperCall Callable<Object> superCall, @AllArguments Object[] args,
			@Origin String name) throws Throwable {
		boolean isError = false;
		try {
			PerfRecorder.start(simpleName(name));
			Object returnValue = superCall.call();
			return returnValue;
		} catch (Throwable e) {
			isError = true;
			throw e;
		} finally {
			PerfRecorder.end(isError);
		}
	}

	/**
	 * 对ByteBuddy取到的方法签名进行简化
	 * <p>
	 * ByteBuddy取到的方法签名是带修饰符和throws部分的，如<br/>
	 * <code>public static java.lang.String com.example.SomeClass.someMethod(java.lang.String) throws java.lang.Exception</code>
	 * <p>
	 * 这个方法取括号前到空格为止的类名+方法名，加上括号内的内容，即<br/>
	 * <code>com.example.SomeClass.someMethod(java.lang.String)</code>
	 * <p>
	 * 因为java方法重载是根据参数来区分的，因此去掉修饰符/返回值/异常还是能定位到唯一的方法。
	 * 
	 * @param name 方法签名
	 * @return 简化后的方法名
	 */
	@IgnoreForBinding
	private static String simpleName(String name) {
		int beginIndex = 0;
		int endIndex = 0;
		boolean foundBeginBracket = false;
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if (c == ' ') {
				if (!foundBeginBracket) {
					beginIndex = i + 1;
				}
			} else if (c == '(') {
				foundBeginBracket = true;
			} else if (c == ')') {
				endIndex = i + 1;
				break;
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
