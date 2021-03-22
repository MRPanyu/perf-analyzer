package perfanalyzer.core.recorder;

import java.io.File;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import perfanalyzer.core.io.PerfIO;
import perfanalyzer.core.io.PerfIOFileImpl;
import perfanalyzer.core.model.NodeType;
import perfanalyzer.core.model.PerfNode;
import perfanalyzer.core.model.PerfStatisticsGroup;
import perfanalyzer.core.model.PerfStatisticsNode;
import perfanalyzer.core.model.PerfStatisticsTimedGroup;

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
	private static PerfStatisticsTimedGroup perfStatisticsTimedGroup = null;
	/** 当前线程所处的代码块对应执行节点信息 */
	private static ThreadLocal<PerfNode> nodeStorage = new ThreadLocal<PerfNode>();
	/** 汇总当前线程中执行信息的临时统计组，在根节点执行完成后再汇总到当前一分钟的统计组中 */
	private static ThreadLocal<PerfStatisticsGroup> statisticsGroupStorage = new ThreadLocal<PerfStatisticsGroup>();

	/**
	 * 某个程序块执行前调用
	 * 
	 * @param name 程序块的名称
	 */
	public static void start(String name) {
		start(name, NodeType.METHOD);
	}

	/**
	 * 某个程序块执行前调用
	 * 
	 * @param name 程序块的名称
	 * @param type 程序块类型
	 */
	public static void start(String name, NodeType type) {
		// 类似于堆栈中压入当前执行块的操作
		PerfNode parent = nodeStorage.get();
		PerfNode node = new PerfNode(name, type, System.nanoTime(), parent);
		nodeStorage.set(node);
		if (parent == null) {
			PerfStatisticsGroup statisticsGroup = new PerfStatisticsGroup();
			statisticsGroupStorage.set(statisticsGroup);
		}
	}

	/**
	 * 某个程序块执行完成后调用，要与前面的start一一对应
	 * 
	 * @param isError 执行结果是否为错误/异常
	 */
	public static void end(boolean isError) {
		// 记录完成时间，然后堆栈中弹出当前执行块回到上一层
		final PerfNode node = nodeStorage.get();
		final PerfStatisticsGroup perfStatisticsGroup = statisticsGroupStorage.get();
		node.setEndTimeNano(System.nanoTime());
		node.setError(isError);

		// 获取父节点
		PerfNode parent = node.getParent();
		if (parent != null && parent.getType() == NodeType.METHOD) {
			// 累加父节点中的子节点耗时信息
			// 如果父节点不是METHOD类型，子节点本身不会记录，因此也不累加子节点耗时
			parent.addChildrenUseTime(node.getUseTimeNano());
		}
		// 当前节点设置为父节点
		nodeStorage.set(parent);

		// 异步执行（避免影响性能）合并到统计信息里面
		if ((parent == null && node.getType() == NodeType.METHOD)
				|| (parent != null && parent.getType() == NodeType.METHOD)) {
			// 注：根节点仅METHOD类型的做记录，非根节点的，如果父节点不是METHOD类型也不记录
			// 避免与要拦截方法无关的其他SQL被记录下来，另外避免SQL节点嵌套SQL节点。
			executorService.submit(new Runnable() {
				@Override
				public void run() {
					// 本节点本身要计入当前统计信息
					PerfStatisticsNode perfStatisticsNode = perfStatisticsGroup.getOrCreateNode(node.getPath());
					perfStatisticsNode.mergeNode(node);
					// 如果已经回到顶层节点，则汇总到当前一分钟的组里面
					if (node.getParent() == null) {
						mergeStatisticsTimedGroup(perfStatisticsGroup);
					}
				}
			});
		}
	}

	/** 将当前线程已完成的根节点信息汇总到统计信息当中 */
	private static void mergeStatisticsTimedGroup(PerfStatisticsGroup statisticsGroup) {
		// 判断是否需要滚动统计信息，将上一分钟的统计信息写入文件，然后开启一个新的
		long now = System.currentTimeMillis();
		if (perfStatisticsTimedGroup == null || now > perfStatisticsTimedGroup.getStatisticsEndTime()) {
			rollStatisticsTimedGroup(now);
		}
		// 合并节点信息到统计信息中
		perfStatisticsTimedGroup.mergeStatisticsGroup(statisticsGroup);
	}

	/**
	 * 滚动统计组，将上一分钟的统计组写入文件，然后新建一个最新的当前统计组
	 * 
	 * @param now 滚动时的当前时间
	 */
	private synchronized static void rollStatisticsTimedGroup(long now) {
		if (perfStatisticsTimedGroup == null || now > perfStatisticsTimedGroup.getStatisticsEndTime()) {
			// 启动写入文件的线程
			writeStatisticsTimedGroup(perfStatisticsTimedGroup);
			// 新建一个当前统计组
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(now);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			long statisticsStartTime = cal.getTimeInMillis();
			long statisticsEndTime = statisticsStartTime + 60000L;
			perfStatisticsTimedGroup = new PerfStatisticsTimedGroup(statisticsStartTime, statisticsEndTime);
		}
	}

	/** 统计组信息写入文件 */
	private static void writeStatisticsTimedGroup(final PerfStatisticsGroup group) {
		executorService.submit(new Runnable() {
			@Override
			public void run() {
				try {
					// 休眠1秒，尽量保证切换间隔mergeStatistics都完成
					Thread.sleep(1000);
					synchronized (group) {
						perfIO.saveItem(group);
					}
				} catch (Exception e) {
				}
			}
		});
	}

}
