package perfanalyzer.agent.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 性能记录agent单个aop切面的配置信息
 * 
 * @author panyu
 *
 */
public class PerfAgentAspectConfig implements Serializable {

	private static final long serialVersionUID = 5468772965071434532L;

	/**
	 * 包含的类名，可用*通配符，可多个分号隔开。
	 * <p>
	 * 类名中的通配符：一个*匹配单层包名或类名，两个**通配符则包含子包。
	 * <p>
	 * 例：<code>com.example.*</code> 表示 com.example 单个包下的所有类
	 * <p>
	 * 例：<code>com.example.**</code> 表示 com.example 包以及所有子包下的所有类
	 * <p>
	 * 例：<code>com.example.**;com.mycompany.**</code> 表示这两个包及所有子包下的所有类
	 */
	private String includeClasses = "**";

	/**
	 * 排除的类名，在已包含的类名中可指定部分进行排除，可用*通配符，可多个分号隔开。通配符格式同includeClasses
	 */
	private String excludeClasses = "";

	/**
	 * 包含的方法名，可多个分号隔开，可用*通配符
	 */
	private String includeMethods = "**";

	/**
	 * 排除的方法名，可多个分号隔开，可用*通配符
	 */
	private String excludeMethods = "";

	private transient boolean initialized = false;
	private transient List<Pattern> includeClassPatterns;
	private transient List<Pattern> excludeClassPatterns;
	private transient List<Pattern> includeMethodPatterns;
	private transient List<Pattern> excludeMethodPatterns;

	/** 判断类名是否匹配 */
	public boolean matchClass(String className) {
		initializePatterns();
		boolean match = false;
		for (Pattern p : includeClassPatterns) {
			if (p.matcher(className).matches()) {
				match = true;
				break;
			}
		}
		if (match && isExcludeClass(className)) {
			match = false;
		}
		return match;
	}

	/** 判断类名是否属于需排除 */
	public boolean isExcludeClass(String className) {
		boolean isExclude = false;
		for (Pattern p : excludeClassPatterns) {
			if (p.matcher(className).matches()) {
				isExclude = true;
				break;
			}
		}
		return isExclude;
	}

	/** 判断方法名是否匹配 */
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

	/** 初始化通配匹配Pattern */
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

	/**
	 * 将通配符转换成正则表达式，首先按分号分隔开，然后将点(.)匹配文本的点，单个星号(*)匹配除了点以外的字符，两个星号(**)匹配任意字符
	 * 
	 * @param text 通配符
	 * @return 正则表达式
	 */
	private List<Pattern> toPatterns(String text) {
		List<Pattern> patterns = new ArrayList<Pattern>();
		String[] arr = text.split(";");
		for (String s : arr) {
			s = s.trim();
			if (s.length() > 0) {
				// $匹配文本$，点(.)匹配文本的点，单个星号(*)匹配除了点以外的字符，两个星号(**)匹配任意字符
				s = s.replace("$", "\\$").replace(".", "\\.").replace("*", "[^.]*").replace("[^.]*[^.]*", ".*");
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
