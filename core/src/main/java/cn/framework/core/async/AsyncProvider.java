/**
 * @项目名称: core
 * @文件名称: AsyncProvider.java
 * @Date: 2016年1月11日
 * @author: wenlai
 * @type: AsyncProvider
 */
package cn.framework.core.async;

import cn.framework.core.log.LogProvider;
import cn.framework.core.utils.Pair;
import cn.framework.core.utils.StopWatch;
import cn.framework.core.utils.Strings;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static cn.framework.core.utils.Exceptions.processException;

/**
 * 异步执行句柄
 *
 * @author wenlai
 */
public class AsyncProvider {

    //    /**
    //     * 监控
    //     *
    //     * @return
    //     */
    //    public static Watcher getWatch() {
    //        Watcher result = new Watcher();
    //        result.taskCount = TASK_CONTAINER.getQueue().size();
    //        result.activeCount = TASK_CONTAINER.getActiveCount();
    //        result.threadCount = TASK_CONTAINER.getPoolSize();
    //        result.poolSize = SIZE;
    //        return result;
    //    }

    /**
     * 异步执行池大小
     */
    private static volatile int SIZE = 20;
    /**
     * 容器
     */
    private static Map<String, AsyncCallback> CONTAINER = null;
    /**
     * 执行器
     */
    private static ThreadPoolExecutor TASK_CONTAINER = (ThreadPoolExecutor) Executors.newFixedThreadPool(SIZE);

    /**
     * 提交异步执行请求
     *
     * @param task     要未来执行的任务
     * @param attach   附加对象
     * @param callback callbackId
     */
    public static <T> void async(Callable<T> task, Object attach, String callback) {
        TASK_CONTAINER.submit(new AsyncHandler<T>(task, attach, callback));
    }
    ;

    /**
     * 内部类
     *
     * @param <T>
     *
     * @author wenlai
     */
    private static class AsyncHandler<T> implements Runnable {

        /**
         * 任务
         */
        private Callable<T> task;

        /**
         * callbackId
         */
        private String callback;

        /**
         * 附加对象
         */
        private Object attach;

        /**
         * 构造
         *
         * @param task
         * @param attach
         * @param callback
         */
        public AsyncHandler(Callable<T> task, Object attach, String callback) {
            this.task = task;
            this.callback = callback;
            this.attach = attach;
        }

        /**
         * 获取callback
         *
         * @param callback
         *
         * @return
         */
        private AsyncCallback findCallback(String callback) {
            if (CONTAINER != null && CONTAINER.containsKey(callback)) {
                return CONTAINER.get(callback);
            }
            LogProvider.getFrameworkErrorLogger().error(Strings.format("${callback}不存在", new Pair("callback", callback)));
            return null;
        }

        /*
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            boolean success = false;
            T result = null;
            StopWatch watch = StopWatch.newWatch();
            try {
                result = task.call();
                success = true;
            }
            catch (Exception e) {
                processException(e);
            }
            try {
                AsyncCallback callback = findCallback(this.callback);
                if (callback != null) {
                    callback.done(AsyncCallback.Context.build(success, (int) watch.checkByOriginal(), result, this.attach));
                }
            }
            catch (Exception e) {
                processException(e);
            }
        }
    }
}
