package perfanalyzer.agent.bytebuddy;

import static net.bytebuddy.matcher.ElementMatchers.isAbstract;
import static net.bytebuddy.matcher.ElementMatchers.namedOneOf;
import static net.bytebuddy.matcher.ElementMatchers.not;

import net.bytebuddy.agent.builder.AgentBuilder.Transformer;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.utility.JavaModule;

public class ConnectionTransformer implements Transformer {

	@Override
	public Builder<?> transform(Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader,
			JavaModule module) {
		String[] names = new String[] { "createStatement", "prepareStatement", "prepareCall" };
		return builder.method(namedOneOf(names).and(not(isAbstract())))
				.intercept(MethodDelegation.to(ConnectionDelegation.class));
	}

}
