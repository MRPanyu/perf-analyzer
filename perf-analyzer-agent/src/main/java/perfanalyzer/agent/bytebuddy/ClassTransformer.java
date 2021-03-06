package perfanalyzer.agent.bytebuddy;

import static net.bytebuddy.matcher.ElementMatchers.isAbstract;
import static net.bytebuddy.matcher.ElementMatchers.isDefaultMethod;
import static net.bytebuddy.matcher.ElementMatchers.not;

import net.bytebuddy.agent.builder.AgentBuilder.Transformer;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.implementation.MethodDelegation;
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
			JavaModule module) {
		return builder.method(
				new NameMatcher<NamedElement>(methodNameMatcher).and(not(isAbstract())).and(not(isDefaultMethod())))
				.intercept(MethodDelegation.to(PerfRecordDelegation.class));
	}
}
