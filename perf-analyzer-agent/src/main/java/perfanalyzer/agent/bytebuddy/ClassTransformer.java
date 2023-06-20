package perfanalyzer.agent.bytebuddy;

import static net.bytebuddy.matcher.ElementMatchers.isAbstract;
import static net.bytebuddy.matcher.ElementMatchers.isStatic;
import static net.bytebuddy.matcher.ElementMatchers.isDefaultMethod;
import static net.bytebuddy.matcher.ElementMatchers.not;

import java.security.ProtectionDomain;

import net.bytebuddy.agent.builder.AgentBuilder.Transformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.matcher.NameMatcher;
import net.bytebuddy.utility.JavaModule;
import perfanalyzer.agent.config.PerfAgentAspectConfig;

/** 根据配置信息对类进行增强 */
public class ClassTransformer implements Transformer {

	protected PerfAgentAspectConfig aspectConfig;
	protected MethodNameMatcher methodNameMatcher;

	public ClassTransformer(PerfAgentAspectConfig aspectConfig) {
		this.aspectConfig = aspectConfig;
		this.methodNameMatcher = new MethodNameMatcher(aspectConfig);
	}

	@Override
	public Builder<?> transform(Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader,
			JavaModule module, ProtectionDomain protectionDomain) {
		builder = builder
				.method(new NameMatcher<NamedElement>(methodNameMatcher).and(not(isAbstract()))
						.and(not(isDefaultMethod())).and(isStatic()))
				.intercept(Advice.to(PerfRecordStaticMethodAdvice.class));
		builder = builder
				.method(new NameMatcher<NamedElement>(methodNameMatcher).and(not(isAbstract()))
						.and(not(isDefaultMethod())).and(not(isStatic())))
				.intercept(Advice.to(PerfRecordInstanceMethodAdvice.class));
		return builder;
	}
}
