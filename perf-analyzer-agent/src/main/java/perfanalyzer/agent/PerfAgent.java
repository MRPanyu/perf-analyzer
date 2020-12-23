package perfanalyzer.agent;

import java.io.File;
import java.lang.instrument.Instrumentation;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.matcher.NameMatcher;
import perfanalyzer.agent.bytebuddy.ClassNameMatcher;
import perfanalyzer.agent.bytebuddy.ClassTransformer;
import perfanalyzer.agent.config.PerfAgentAspectConfig;
import perfanalyzer.agent.config.PerfAgentConfig;
import perfanalyzer.core.io.PerfIOFileImpl;
import perfanalyzer.core.recorder.PerfRecorder;

/**
 * 性能记录agent的premain类
 * 
 * @author panyu
 *
 */
public class PerfAgent {

	public static void premain(String options, Instrumentation inst) {
		// 从perf-analyzer-agent.yml文件中读取配置信息
		PerfAgentConfig config = PerfAgentConfig.getInstance();
		// 根据配置信息调整输出文件
		PerfRecorder.perfIO = new PerfIOFileImpl(new File(config.getOutputFile()));

		// 创建默认增强配置
		ByteBuddy byteBuddy = new ByteBuddy().with(TypeValidation.DISABLED);
		AgentBuilder agentBuilder = AgentBuilder.Default.of().with(byteBuddy)
				// 类增强的调试信息输出到System.out，如果不需要可以注释掉
				.with(new AgentBuilder.Listener.StreamWriting(System.out).withTransformationsOnly());

		// 根据逐个切面配置设置增强的类与方法
		for (PerfAgentAspectConfig aspectConfig : config.getAspects()) {
			ClassNameMatcher classNameMatcher = new ClassNameMatcher(aspectConfig);
			NameMatcher<NamedElement> nameMatcher = new NameMatcher<NamedElement>(classNameMatcher);
			agentBuilder = agentBuilder.type(nameMatcher.or(ElementMatchers.hasSuperType(nameMatcher)))
					.transform(new ClassTransformer(aspectConfig));
		}

		// 设置到Instrumentation上
		agentBuilder.installOn(inst);
	}

}
