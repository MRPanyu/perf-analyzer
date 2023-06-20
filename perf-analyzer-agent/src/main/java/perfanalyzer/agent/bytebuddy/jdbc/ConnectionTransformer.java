package perfanalyzer.agent.bytebuddy.jdbc;

import static net.bytebuddy.matcher.ElementMatchers.isAbstract;
import static net.bytebuddy.matcher.ElementMatchers.namedOneOf;
import static net.bytebuddy.matcher.ElementMatchers.not;

import java.security.ProtectionDomain;

import net.bytebuddy.agent.builder.AgentBuilder.Transformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.utility.JavaModule;

public class ConnectionTransformer implements Transformer {

	@Override
	public Builder<?> transform(Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader,
			JavaModule module, ProtectionDomain protectionDomain) {
		String[] names = new String[] { "prepareStatement", "prepareCall" };
		return builder.method(namedOneOf(names).and(not(isAbstract()))).intercept(Advice.to(ConnectionPrepareAdvice.class));
	}

}
