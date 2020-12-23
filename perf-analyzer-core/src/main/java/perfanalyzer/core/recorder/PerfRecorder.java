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

	/** 用于执行异步任务的线程池 */
	private static ExecutorService executorService = Executors.newCachedThreadPool();
	/** 当前一分钟的统计信息组 */
	private static PerfStatisticsGroup perfStatisticsGroup = null;
	/** 当前线程所处的代码块对应执行节点信息 */
	private static ThreadLocal<PerfNode> nodeStorage = new ThreadLocal<PerfNode>();

	/**
	 * 某个程序块执行前调用
	 * 
	 * @param name 程序块的名称
	 */
	public static void start(String name) {
		// 类似于堆栈中压入当前执行块的操作
		PerfNode parent = nodeStorage.get();
		PerfNode node = new PerfNode(name, System.nanoTime(), parent);
		nodeStorage.set(node);
	}

	/**
	 * 某个程序块执行完成后调用，要与前面的start一一对应
	 * 
	 * @param isError 执行结果是否为错误/异常
	 */
	public static void end(boolean isError) {
		// 记录完成时间，然后堆栈中弹出当前执行块回到上一层
		final PerfNode node = nodeStorage.get();
		node.setEndTimeNano(System.nanoTime());
		node.setError(isError);
		PerfNode parent = node.getParent();
		nodeStorage.set(parent);
		// 如果已经回到根节点，则开一个异步线程（避免影响性能）合并到统计信息里面
		if (parent == null) {
			executorService.submit(new Runnable() {
				@Override
				public void run() {
					mergeStatistics(node);
				}
			});
		}
	}

	/** 将当前线程已完成的根节点信息汇总到统计信息当中 */
	private static void mergeStatistics(PerfNode node) {
		// 判断是否需要滚动统计信息，将上一分钟的统计信息写入文件，然后开启一个新的
		long now = System.currentTimeMillis();
		if (perfStatisticsGroup == null || now > perfStatisticsGroup.getStatisticsEndTime()) {
			rollStatisticsGroup(now);
		}
		// 递归合并节点信息到统计信息中
		mergeStatistics(node, perfStatisticsGroup);
	}

	/**
	 * 递归合并某个执行节点到对应的统计信息中
	 * 
	 * @param node  执行信息节点
	 * @param group 记录到的统计组，注意记录时从根节点到叶节点都必须记录到同一个统计组里面，为避免滚动的时候切换统计组，这里用方法入参来传递，而不能用静态变量的当前统计组
	 */
	private static void mergeStatistics(PerfNode node, PerfStatisticsGroup group) {
		PerfNode parentNode = node.getParent();
		String parentPath = parentNode == null ? null : parentNode.getPath();
		PerfStatisticsNode st = group.getOrCreateNode(node.getName(), node.getPath(), parentPath);
		st.mergeNode(node);
		for (PerfNode child : node.getChildren()) {
			mergeStatistics(child, group);
		}
	}

	/**
	 * 滚动统计组，将上一分钟的统计组写入文件，然后新建一个最新的当前统计组
	 * 
	 * @param now 滚动时的当前时间
	 */
	private synchronized static void rollStatisticsGroup(long now) {
		if (perfStatisticsGroup == null || now > perfStatisticsGroup.getStatisticsEndTime()) {
			// 启动写入文件的线程
			writeStatisticsGroup(perfStatisticsGroup);
			// 新建一个当前统计组
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(now);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			long statisticsStartTime = cal.getTimeInMillis();
			long statisticsEndTime = statisticsStartTime + 60000L;
			perfStatisticsGroup = new PerfStatisticsGroup(statisticsStartTime, statisticsEndTime);
		}
	}

	/** 统计组信息写入文件 */
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
