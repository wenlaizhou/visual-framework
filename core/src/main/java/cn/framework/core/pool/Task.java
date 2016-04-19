/**
 * @项目名称: core
 * @文件名称: TackWrapper.java
 * @Date: 2016年1月13日
 * @author: wenlai
 * @type: TackWrapper
 */
package cn.framework.core.pool;

import cn.framework.core.utils.StopWatch;
import cn.framework.core.utils.Strings;
import static cn.framework.core.pool.ThreadPool.*;

/**
 * 执行任务包装类
 * 
 * @author wenlai
 */
public class Task implements Runnable {
    
    /**
     * 构造任务
     * 
     * @param name
     * @param task
     */
    private Task(String name, Runnable task) {
        this.task = task;
        this.name = Strings.isNotNullOrEmpty(name) ? name : "unnamed";
    }
    
    /**
     * 获取任务名称
     * 
     * @return
     */
    public final String getName() {
        return this.name;
    }
    
    /**
     * 构造任务
     * 
     * @param name 任务名称
     * @param task 任务
     * @return
     */
    public static Task wrap(String name, Runnable task) {
        return new Task(name, task);
    }
    
    /*
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        StopWatch watch = StopWatch.newWatch();
        THREAD_POOL_LOGGER.info(String.format("开始执行任务：%1$s", name));
        this.task.run();
        THREAD_POOL_LOGGER.info(String.format("执行任务：%1$s 完毕，执行耗时：%2$s", name, watch.checkByOriginal()));
    }
    
    /**
     * 任务
     */
    private Runnable task;
    
    /**
     * 任务名称
     */
    private String name;
    
}
