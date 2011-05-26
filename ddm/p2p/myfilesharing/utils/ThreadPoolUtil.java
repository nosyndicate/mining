package ddm.p2p.myfilesharing.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * @author 刘浩
 * 线程池工具
 */
public class ThreadPoolUtil {
	private static final Logger LOG = Logger.getLogger(ThreadPoolUtil.class.getName());
	private static final int WAIT_EXISTING_TASKS_TERMINATE = 1;
	private static final int WAIT_TASKS_RESPOND_CANCELLED = 1;
	
	public static void shutdownAndAwaitTermination(ExecutorService pool) {
		pool.shutdown(); // Disable new tasks from being submitted
		try {
			// Wait a while for existing tasks to terminate
			if (!pool.awaitTermination(WAIT_EXISTING_TASKS_TERMINATE, TimeUnit.SECONDS)) {
				pool.shutdownNow(); // Cancel currently executing tasks
				// Wait a while for tasks to respond to being cancelled
				if (!pool.awaitTermination(WAIT_TASKS_RESPOND_CANCELLED, TimeUnit.SECONDS))
					LOG.warning("Pool did not terminate");
			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			pool.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}
}
