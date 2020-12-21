package perfanalyzer.core.recorder;

import java.io.File;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import perfanalyzer.core.io.PerfIO;
import perfanalyzer.core.io.PerfIOFileImpl;
import perfanalyzer.core.model.PerfNode;
import perfanalyzer.core.model.PerfStatisticsGroup;
import perfanalyzer.core.model.PerfStatisticsNode;

/**
 * 记录性能数据的主工具类
 * 
 * @author panyu
 *
 */
public class PerfRecorder {

	/** 输出性能日志的默认IO，默认是输出到启动目录下的perf_record.data文件中，可以初始化时改成其他实现 */
	public static PerfIO perfIO = new PerfIOFileImpl(new File("perf_record.prec"));

	private static ExecutorService executorService = Executors.newCachedThreadPool();
	private static PerfStatisticsGroup perfStatisticsGroup = null;
	private static ThreadLocal<PerfNode> nodeStorage = new ThreadLocal<PerfNode>();

	/**
	 * 某个程序块执行前调用
	 * 
	 * @param name 程序块的名称
	 */
	public static void start(String name) {
		PerfNode parent = nodeStorage.get();
		PerfNode node = new PerfNode(name, System.nanoTime(), parent);
		nodeStorage.set(node);
	}

	/**
	 * 某个程序块执行完成后调用，要与前面的start一一对应
	 */
	public static void end(boolean isError) {
		final PerfNode node = nodeStorage.get();
		node.setEndTimeNano(System.nanoTime());
		node.setError(isError);
		PerfNode parent = node.getParent();
		nodeStorage.set(parent);
		if (parent == null) {
			executorService.submit(new Runnable() {
				@Override
				public void run() {
					mergeStatistics(node);
				}
			});
		}
	}

	private static void mergeStatistics(PerfNode node) {
		long now = System.currentTimeMillis();
		if (perfStatisticsGroup == null || now > perfStatisticsGroup.getStatisticsEndTime()) {
			rollStatisticsGroup(now);
		}
		mergeStatistics(node, perfStatisticsGroup);
	}

	private static void mergeStatistics(PerfNode node, PerfStatisticsGroup group) {
		PerfNode parentNode = node.getParent();
		String parentPath = parentNode == null ? null : parentNode.getPath();
		PerfStatisticsNode st = group.getOrCreateNode(node.getName(), node.getPath(), parentPath);
		st.mergeNode(node);
		for (PerfNode child : node.getChildren()) {
			mergeStatistics(child, group);
		}
	}

	private synchronized static void rollStatisticsGroup(long now) {
		if (perfStatisticsGroup == null || now > perfStatisticsGroup.getStatisticsEndTime()) {
			writeStatisticsGroup(perfStatisticsGroup);
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(now);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			long statisticsStartTime = cal.getTimeInMillis();
			long statisticsEndTime = statisticsStartTime + 60000L;
			perfStatisticsGroup = new PerfStatisticsGroup(statisticsStartTime, statisticsEndTime);
		}
	}

	private static void writeStatisticsGroup(final PerfStatisticsGroup group) {
		executorService.submit(new Runnable() {
			@Override
			public void run() {
				try {
					// 休眠1秒，尽量保证切换间隔mergeStatistics都完成
					Thread.sleep(1000);
					synchronized (group) {
						perfIO.savePerfStatisticsGroup(group);
					}
				} catch (Exception e) {
				}
			}
		});
	}

}
