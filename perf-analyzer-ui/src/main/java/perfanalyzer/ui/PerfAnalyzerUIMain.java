package perfanalyzer.ui;

/**
 * 启动类
 * <p>
 * 目前整个工程中perf-analyzer-core想要支持到java6，因此不能整体用java11模块化实现。UI层用了javafx，在java8下面打个fatjar就能正常运行了，但java11的模块化会造成无法使用。
 * <p>
 * 参考了一下 <br/>
 * https://stackoverflow.com/questions/52653836/maven-shade-javafx-runtime-components-are-missing/52654791#52654791
 * <br/>
 * 里面有一种方法是另写一个非Application的main类调用Application的main方法，绕过java11运行javafx程序时的检查。
 * 
 * @author panyu
 *
 */
public class PerfAnalyzerUIMain {

	public static void main(String[] args) {
		PerfAnalyzerUIApplication.main(args);
	}

}
