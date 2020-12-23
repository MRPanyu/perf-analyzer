package perfanalyzer.agent.bytebuddy;

import net.bytebuddy.matcher.ElementMatcher;
import perfanalyzer.agent.config.PerfAgentAspectConfig;

/** 匹配所有要排除的类 */
public class ExcludeClassNameMatcher implements ElementMatcher<String> {

	protected PerfAgentAspectConfig aspectConfig;

	public ExcludeClassNameMatcher(PerfAgentAspectConfig aspectConfig) {
		this.aspectConfig = aspectConfig;
	}

	@Override
	public boolean matches(String target) {
		return aspectConfig.isExcludeClass(target);
	}

}
