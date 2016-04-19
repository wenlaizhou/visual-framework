package cn.framework.core.event;

import cn.framework.core.utils.Exceptions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

/**
 * project code
 * package cn.framework.core.event
 * create at 16/3/31 下午2:19
 *
 * @author wenlai
 */
public final class ThreadPoolMonitor {

    /**
     * 监控队列
     */
    private static ConcurrentLinkedQueue<Executor> MONITOR_LIST = new ConcurrentLinkedQueue<>();

    /**
     * 增加监控项
     *
     * @param e
     */
    public static Executor add(Executor e) {
        MONITOR_LIST.add(e);
        return e;
    }

    /**
     * 增加监控项
     *
     * @param e
     */
    public static void remove(Executor e) {
        MONITOR_LIST.remove(e);
    }

    /**
     * 获取监控信息
     *
     * @return
     */
    public static List<ExecutorData> collectMonitorData() {
        ArrayList<ExecutorData> result = new ArrayList<>();
        for (Executor executor : MONITOR_LIST) {
            try {
                ExecutorData data = new ExecutorData();
                data.description = executor.toString();
                //                if (executor instanceof ThreadPoolExecutor) {
                //                    ThreadPoolExecutor realExecutor = (ThreadPoolExecutor) executor;
                //                    data.activeCount = realExecutor.getActiveCount();
                //                    data.corePoolSize = realExecutor.getCorePoolSize();
                //                    data.description = realExecutor.toString();
                //                    data.keepAliveMilliSeconds = realExecutor.getKeepAliveTime(TimeUnit.MILLISECONDS);
                //                    data.largestPoolSize = realExecutor.getLargestPoolSize();
                //                    data.maximumPoolSize = realExecutor.getMaximumPoolSize();
                //                    data.taskCount = realExecutor.getTaskCount();
                //                    data.poolSize = realExecutor.getPoolSize();
                //                }
                //                else if (executor instanceof ForkJoinPool) {
                //                    ForkJoinPool realExecutor = (ForkJoinPool) executor;
                //                    data.activeCount = realExecutor.getRunningThreadCount();
                //                    data.corePoolSize = realExecutor.getActiveThreadCount();
                //                    data.description = realExecutor.toString();
                //                    data.keepAliveMilliSeconds = 0;
                //                    data.largestPoolSize = 0;
                //                    data.maximumPoolSize = realExecutor.getQueuedSubmissionCount();
                //                    data.taskCount = realExecutor.getStealCount();
                //                    data.poolSize = realExecutor.getPoolSize();
                //                }
                //                else {
                //                    data.activeCount = 1;
                //                    data.largestPoolSize = 1;
                //                    data.description = executor.toString();
                //                }
                result.add(data);
            }
            catch (Exception x) {
                Exceptions.processException(x);
            }
        }
        return result;
    }

    /**
     * 监控信息
     */
    public static class ExecutorData {

        public int activeCount = -1;

        public int corePoolSize = -1;

        public long keepAliveMilliSeconds = -1;

        public int largestPoolSize = -1;

        public int maximumPoolSize = -1;

        public int poolSize = -1;

        public long taskCount = -1L;

        public String description;

    }

}
