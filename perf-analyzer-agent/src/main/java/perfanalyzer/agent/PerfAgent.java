package perfanalyzer.agent;

import static net.bytebuddy.matcher.ElementMatchers.hasSuperType;
import static net.bytebuddy.matcher.ElementMatchers.isInterface;
import static net.bytebuddy.matcher.ElementMatchers.isSubTypeOf;
import static net.bytebuddy.matcher.ElementMatchers.not;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.sql.Connection;
import java.sql.Statement;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.matcher.NameMatcher;
import perfanalyzer.agent.bytebuddy.ClassNameMatcher;
import perfanalyzer.agent.bytebuddy.ClassTransformer;
import perfanalyzer.agent.bytebuddy.ExcludeClassNameMatcher;
import perfanalyzer.agent.bytebuddy.jdbc.ConnectionTransformer;
import perfanalyzer.agent.bytebuddy.jdbc.StatementTransformer;
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
		AgentBuilder agentBuilder = AgentBuilder.Default.of().with(byteBuddy);
		if (config.getVerbose() != null && config.getVerbose().booleanValue()) {
			// 类增强的调试信息输出到System.out
			agentBuilder = agentBuilder
					.with(new AgentBuilder.Listener.StreamWriting(System.out).withTransformationsOnly());
		}

		// 根据逐个切面配置设置增强的类与方法
		for (PerfAgentAspectConfig aspectConfig : config.getAspects()) {
			ClassNameMatcher classNameMatcher = new ClassNameMatcher(aspectConfig);
			ExcludeClassNameMatcher excludeClassNameMatcher = new ExcludeClassNameMatcher(aspectConfig);
			NameMatcher<NamedElement> nameMatcher = new NameMatcher<NamedElement>(classNameMatcher);
			NameMatcher<NamedElement> excludeNameMatcher = new NameMatcher<NamedElement>(excludeClassNameMatcher);
			// 类名的匹配规则：类名本身符合匹配规则的，或者某个类有父类或实现接口是符合匹配规则的，且这个类名本身不属于排除列表的
			// 后面那种匹配情况主要是想拦截到一些如MyBatis/spring-data之类自动生成的DAO动态实现类
			agentBuilder = agentBuilder.type((nameMatcher.or(hasSuperType(nameMatcher))).and(not(excludeNameMatcher)))
					.transform(new ClassTransformer(aspectConfig));
		}

		// SQL拦截配置
		if (config.getRecordSql() != null && config.getRecordSql().booleanValue()) {
			agentBuilder = agentBuilder.type(isSubTypeOf(Connection.class).and(not(isInterface())))
					.transform(new ConnectionTransformer()).type(isSubTypeOf(Statement.class).and(not(isInterface())))
					.transform(new StatementTransformer());
		}

		// 设置到Instrumentation上
		agentBuilder.installOn(inst);
	}

}
