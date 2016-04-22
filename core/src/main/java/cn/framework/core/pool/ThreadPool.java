/**
 * @项目名称: framework
 * @文件名称: ThreadPool.java
 * @Date: 2015年11月9日
 * @author: wenlai
 * @type: ThreadPool
 */
package cn.framework.core.pool;

import cn.framework.core.event.ThreadPoolMonitor;
import cn.framework.core.log.LogProvider;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author wenlai
 */
public final class ThreadPool {

    /**
     * 日志句柄
     */
    public final static Logger THREAD_POOL_LOGGER = LogProvider.getLogger("pool.framework.info");

    /**
     * 基础线程池大小
     */
    private static int commonPoolSize = 30;

    /**
     * 默认定时任务线程池大小
     */
    private static int schedulePoolSize = 15;

    //    /**
    //     * 监控：<br>
    //     * 1、剩余任务数量<br>
    //     * 2、当前线程数量
    //     *
    //     * @return
    //     */
    //    public static Watcher getWatch() {
    //        Watcher result = new Watcher();
    //        result.taskCount = PoolContainer.COMMON_POOL.getQueue().size();
    //        result.activeCount = PoolContainer.COMMON_POOL.getActiveCount();
    //        result.scheduleTaskCount = PoolContainer.SCHEDULED_POOL.getQueue().size();
    //        result.scheduledActiveCount = PoolContainer.SCHEDULED_POOL.getActiveCount();
    //        result.threadCount = PoolContainer.COMMON_POOL.getPoolSize();
    //        result.scheduledThreadCount = PoolContainer.SCHEDULED_POOL.getPoolSize();
    //        result.poolSize = commonPoolSize > 0 ? commonPoolSize : -1;
    //        result.scheduledPoolSize = commonPoolSize > 0 ? commonPoolSize : 15;
    //        return result;
    //
    //    }

    /**
     * 添加任务
     *
     * @param task task
     */
    public static void addTask(Task task) {
        THREAD_POOL_LOGGER.info(String.format("添加立即执行任务：%1$s", task.getName()));
        PoolContainer.COMMON_POOL.execute(task);
    }

    /**
     * 添加任务
     *
     * @param task        task
     * @param delaySecond 延迟时间 单位 秒
     */
    public static void addTask(Task task, int delaySecond) {
        THREAD_POOL_LOGGER.info(String.format("添加延迟执行任务：%1$s，延迟时间：%2$s秒", task.getName(), delaySecond));
        if (delaySecond > 0) {
            PoolContainer.SCHEDULED_POOL.schedule(task, delaySecond, TimeUnit.SECONDS);
        }
        else {
            PoolContainer.COMMON_POOL.execute(task);
        }
    }

    /**
     * 添加定时任务
     *
     * @param task         task
     * @param periodSecond 周期执行间隔 单位 秒
     */
    public static void addScheduledTask(Task task, long periodSecond) {
        THREAD_POOL_LOGGER.info(String.format("添加定时执行任务：%1$s，间隔时间：%2$s秒", task.getName(), periodSecond));
        PoolContainer.SCHEDULED_POOL.scheduleAtFixedRate(task, 0, periodSecond, TimeUnit.SECONDS);
    }

    /**
     * 添加定时任务
     *
     * @param task        task
     * @param delaySecond 循环等待执行完毕后X秒执行
     */
    public static void addScheduledTaskAndWaitForDone(Task task, long delaySecond) {
        THREAD_POOL_LOGGER.info(String.format("添加序列间隔时间执行任务：%1$s，间隔时间时间：%2$s秒", task.getName(), delaySecond));
        PoolContainer.SCHEDULED_POOL.scheduleWithFixedDelay(task, delaySecond, delaySecond, TimeUnit.SECONDS);
    }

    /**
     * 添加延迟任务
     *
     * @param task         task
     * @param delaySeconds 延迟秒数
     */
    public static void addDelayTask(Task task, int delaySeconds) {
        THREAD_POOL_LOGGER.info(String.format("添加延迟任务：%1$s，间隔时间时间：%2$s秒", task.getName(), delaySeconds));
        PoolContainer.SCHEDULED_POOL.schedule(task, delaySeconds, TimeUnit.SECONDS);
    }

    //    /**
    //     * 线程池监控类
    //     *
    //     * @author wenlai
    //     */
    //    public static class Watcher {
    //
    //        /**
    //         * 剩余任务数量
    //         */
    //        public int taskCount;
    //
    //        /**
    //         * 当前线程数
    //         */
    //        public int activeCount;
    //
    //        /**
    //         * 剩余定时任务数量
    //         */
    //        public int scheduleTaskCount;
    //
    //        /**
    //         * 剩余定时线程数
    //         */
    //        public int scheduledActiveCount;
    //
    //        /**
    //         * 线程池中现在线程数量
    //         */
    //        public int threadCount;
    //
    //        /**
    //         * 线程池中现在线程数量
    //         */
    //        public int scheduledThreadCount;
    //
    //        /**
    //         * 池总大小
    //         */
    //        public int poolSize;
    //
    //        /**
    //         * 池总大小
    //         */
    //        public int scheduledPoolSize;
    //    }

    /**
     * 线程池容器
     *
     * @author wenlai
     */
    private static class PoolContainer {

        static final ThreadPoolExecutor COMMON_POOL = (ThreadPoolExecutor) (commonPoolSize > 0 ? Executors.newFixedThreadPool(commonPoolSize) : Executors.newCachedThreadPool());

        static final ScheduledThreadPoolExecutor SCHEDULED_POOL = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(schedulePoolSize > 0 ? schedulePoolSize : 10);

        static {
            ThreadPoolMonitor.add(COMMON_POOL);
            ThreadPoolMonitor.add(SCHEDULED_POOL);
        }
    }
}
