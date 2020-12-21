package perfanalyzer.agent.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PerfAgentAspectConfig implements Serializable {

	private static final long serialVersionUID = 5468772965071434532L;

	private String includeClasses = "**";
	private String excludeClasses = "";
	private String includeMethods = "**";
	private String excludeMethods = "";

	private transient boolean initialized = false;
	private transient List<Pattern> includeClassPatterns;
	private transient List<Pattern> excludeClassPatterns;
	private transient List<Pattern> includeMethodPatterns;
	private transient List<Pattern> excludeMethodPatterns;

	public boolean matchClass(String className) {
		initializePatterns();
		boolean match = false;
		for (Pattern p : includeClassPatterns) {
			if (p.matcher(className).matches()) {
				match = true;
				break;
			}
		}
		if (match) {
			for (Pattern p : excludeClassPatterns) {
				if (p.matcher(className).matches()) {
					match = false;
					break;
				}
			}
		}
		return match;
	}

	public boolean matchMethod(String methodName) {
		initializePatterns();
		boolean match = false;
		for (Pattern p : includeMethodPatterns) {
			if (p.matcher(methodName).matches()) {
				match = true;
				break;
			}
		}
		if (match) {
			for (Pattern p : excludeMethodPatterns) {
				if (p.matcher(methodName).matches()) {
					match = false;
					break;
				}
			}
		}
		return match;
	}

	private void initializePatterns() {
		if (!initialized) {
			synchronized (this) {
				if (!initialized) {
					includeClassPatterns = toPatterns(includeClasses);
					excludeClassPatterns = toPatterns(excludeClasses);
					includeMethodPatterns = toPatterns(includeMethods);
					excludeMethodPatterns = toPatterns(excludeMethods);
					initialized = true;
				}
			}
		}
	}

	private List<Pattern> toPatterns(String text) {
		List<Pattern> patterns = new ArrayList<Pattern>();
		String[] arr = text.split(";");
		for (String s : arr) {
			s = s.trim();
			if (s.length() > 0) {
				// 点(.)匹配文本的点，单个星号(*)匹配除了点以外的字符，两个星号(**)匹配任意字符
				s = s.replace(".", "\\.").replace("*", "[^.]*").replace("[^.]*[^.]*", ".*");
				Pattern p = Pattern.compile(s);
				patterns.add(p);
			}
		}
		return patterns;
	}

	public String getIncludeClasses() {
		return includeClasses;
	}

	public void setIncludeClasses(String includeClasses) {
		this.includeClasses = includeClasses;
	}

	public String getExcludeClasses() {
		return excludeClasses;
	}

	public void setExcludeClasses(String excludeClasses) {
		this.excludeClasses = excludeClasses;
	}

	public String getIncludeMethods() {
		return includeMethods;
	}

	public void setIncludeMethods(String includeMethods) {
		this.includeMethods = includeMethods;
	}

	public String getExcludeMethods() {
		return excludeMethods;
	}

	public void setExcludeMethods(String excludeMethods) {
		this.excludeMethods = excludeMethods;
	}

}
