package perfanalyzer.agent.config;

import java.io.Serializable;

public class PerfAgentOutputConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 输出文件路径，如果是相对路径对应的是程序工作目录 */
	private String file = "./perf_record.prec";

	/** 是否滚动输出文件，即每小时产生一个新文件，避免单个文件太大无法打开分析 */
	private boolean rolling = true;

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public boolean isRolling() {
		return rolling;
	}

	public void setRolling(boolean rolling) {
		this.rolling = rolling;
	}

}
