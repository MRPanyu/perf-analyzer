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

	/** 输出性能数据的文件，注意如果是相对路径格式，相对的是程序启动的路径而不是agent jar包的路径 */
	private String outputFile;
	/** 拦截进行性能记录的切面 */
	private List<PerfAgentAspectConfig> aspects = new ArrayList<PerfAgentAspectConfig>();

	public String getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public List<PerfAgentAspectConfig> getAspects() {
		return aspects;
	}

	public void setAspects(List<PerfAgentAspectConfig> aspects) {
		this.aspects = aspects;
	}

}
