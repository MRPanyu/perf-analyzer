package perfanalyzer.agent.bytebuddy;

import net.bytebuddy.matcher.ElementMatcher;
import perfanalyzer.agent.config.PerfAgentAspectConfig;

public class MethodNameMatcher implements ElementMatcher<String> {

	private PerfAgentAspectConfig aspectConfig;

	public MethodNameMatcher(PerfAgentAspectConfig aspectConfig) {
		this.aspectConfig = aspectConfig;
	}

	@Override
	public boolean matches(String target) {
		return aspectConfig.matchMethod(target);
	}

}
