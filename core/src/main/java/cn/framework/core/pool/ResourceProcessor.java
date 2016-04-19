/**
 * @项目名称: core
 * @文件名称: ResourceProcessor.java
 * @Date: 2016年2月18日
 * @author: wenlai
 * @type: ResourceProcessor
 */
package cn.framework.core.pool;

import cn.framework.core.utils.Exceptions;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author wenlai
 */
public class ResourceProcessor {

    /**
     * 资源库
     */
    private volatile static ConcurrentLinkedQueue<AutoCloseable> resourceQueue = new ConcurrentLinkedQueue<>();

    /**
     * 创建资源回收处理器
     */
    static {
        ThreadPool.addScheduledTaskAndWaitForDone(Task.wrap("resource-recycle", () -> {
            AutoCloseable resource = null;
            while ((resource = resourceQueue.poll()) != null) {
                try {
                    resource.close();
                    Thread.sleep(1000);
                }
                catch (Exception x) {
                    Exceptions.processException(x);
                }
                finally {
                    resource = null;
                }
            }
        }), 30);
    }

    /**
     * 将需要关闭的资源放到资源关闭队列中<br>
     * 关闭需要关闭的资源<br>
     * 处理器会每30秒对待关闭资源进行关闭，每关闭一个资源会等待1秒钟
     *
     * @param resource 待关闭的资源
     */
    public static void closeResource(AutoCloseable resource) {
        ThreadPool.addTask(Task.wrap("add-resource", () -> {
            try {
                if (resource != null) {
                    resourceQueue.add(resource);
                }
            }
            catch (Exception x) {
                Exceptions.processException(x);
            }
        }));
    }
}
