package perfanalyzer.agent.bytebuddy;

import net.bytebuddy.matcher.ElementMatcher;
import perfanalyzer.agent.config.PerfAgentAspectConfig;

/** 根据配置信息匹配方法名 */
public class MethodNameMatcher implements ElementMatcher<String> {

	protected PerfAgentAspectConfig aspectConfig;

	public MethodNameMatcher(PerfAgentAspectConfig aspectConfig) {
		this.aspectConfig = aspectConfig;
	}

	@Override
	public boolean matches(String target) {
		return aspectConfig.matchMethod(target);
	}

}
