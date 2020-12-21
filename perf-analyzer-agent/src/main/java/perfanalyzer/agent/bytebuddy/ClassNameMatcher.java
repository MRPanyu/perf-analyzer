package perfanalyzer.agent.bytebuddy;

import net.bytebuddy.matcher.ElementMatcher;
import perfanalyzer.agent.config.PerfAgentAspectConfig;

public class ClassNameMatcher implements ElementMatcher<String> {

	private PerfAgentAspectConfig aspectConfig;

	public ClassNameMatcher(PerfAgentAspectConfig aspectConfig) {
		this.aspectConfig = aspectConfig;
	}

	@Override
	public boolean matches(String target) {
		return aspectConfig.matchClass(target);
	}

}
