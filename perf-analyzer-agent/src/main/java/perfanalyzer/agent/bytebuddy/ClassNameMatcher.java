package perfanalyzer.agent.bytebuddy;

import net.bytebuddy.matcher.ElementMatcher;
import perfanalyzer.agent.config.PerfAgentAspectConfig;

/** 根据配置信息匹配类名 */
public class ClassNameMatcher implements ElementMatcher<String> {

	protected PerfAgentAspectConfig aspectConfig;

	public ClassNameMatcher(PerfAgentAspectConfig aspectConfig) {
		this.aspectConfig = aspectConfig;
	}

	@Override
	public boolean matches(String target) {
		return aspectConfig.matchClass(target);
	}

}
