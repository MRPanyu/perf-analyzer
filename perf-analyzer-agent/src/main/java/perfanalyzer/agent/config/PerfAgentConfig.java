package perfanalyzer.agent.config;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

/**
 * 性能记录agent的yml配置类
 * 
 * @author panyu
 *
 */
public class PerfAgentConfig implements Serializable {

	private static final long serialVersionUID = -8050616338891821147L;

	private static PerfAgentConfig instance = null;

	/** 加载配置信息 */
	public synchronized static PerfAgentConfig getInstance() {
		if (instance == null) {
			/* 从perf-analyzer-agent.jar文件同目录下的perf-analyzer-agent.yml文件加载配置 */
			String path = PerfAgentConfig.class.getResource("PerfAgentConfig.class").getPath();
			path = path.substring(0, path.indexOf(".jar"));
			path = path.substring(0, path.lastIndexOf('/'));
			path += "/perf-analyzer-agent.yml";
			Yaml yaml = new Yaml(new Constructor(PerfAgentConfig.class));
			try {
				InputStream in = new URL(path).openStream();
				try {
					instance = yaml.load(in);
				} finally {
					in.close();
				}
			} catch (Exception e) {
				throw new RuntimeException("Cannot load config file for PerfAgent: " + path, e);
			}
		}
		return instance;
	}

	/** 是否输出System.out调试信息 */
	private boolean verbose = false;
	/** 输出文件设置 */
	private PerfAgentOutputConfig output = new PerfAgentOutputConfig();
	/** 是否记录SQL性能 */
	private boolean recordSql = false;
	/** 拦截进行性能记录的切面 */
	private List<PerfAgentAspectConfig> aspects = new ArrayList<PerfAgentAspectConfig>();

	public boolean getVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public PerfAgentOutputConfig getOutput() {
		return output;
	}

	public void setOutput(PerfAgentOutputConfig output) {
		this.output = output;
	}

	public boolean getRecordSql() {
		return recordSql;
	}

	public void setRecordSql(boolean recordSql) {
		this.recordSql = recordSql;
	}

	public List<PerfAgentAspectConfig> getAspects() {
		return aspects;
	}

	public void setAspects(List<PerfAgentAspectConfig> aspects) {
		this.aspects = aspects;
	}

}
