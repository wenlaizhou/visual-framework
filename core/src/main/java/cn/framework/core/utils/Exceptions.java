/**
 * @项目名称: core
 * @文件名称: Exceptions.java
 * @Date: 2016年2月24日
 * @author: wenlai
 * @type: Exceptions
 */
package cn.framework.core.utils;

import cn.framework.core.log.FrameworkLogger;
import cn.framework.core.log.GlobalLogProcessor;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 统一framework异常处理<br>
 * 可以自定义日志处理器bean-name is globalLogFilter
 *
 * @author wenlai
 */
public final class Exceptions {

    /**
     * runtime count
     */
    public final static AtomicInteger RUNTIME_EXCEPTION_COUNT = new AtomicInteger(0);
    /**
     * error count
     */
    public final static AtomicInteger ERROR_COUNT = new AtomicInteger(0);

    /**
     * framework normal count
     */
    public final static AtomicInteger NORMAL_EXCEPTION_COUNT = new AtomicInteger(0);

    /**
     * global log
     */
    private final static GlobalLogProcessor FRAMEWORK_LOG_HANDLER = Springs.getContext() != null && Springs.get("globalLogProcessor") != null ? Springs.get("globalLogProcessor") : new FrameworkLogger();

    /**
     * 对异常进行处理
     *
     * @param exception exe
     */
    public static void processException(Throwable exception) {
        exceptionCounter(exception);
        FRAMEWORK_LOG_HANDLER.processException(exception);
    }

    private static void exceptionCounter(Throwable exception) {
        if (exception instanceof Error) {
            ERROR_COUNT.addAndGet(1);
        }
        else if (exception instanceof RuntimeException) {
            RUNTIME_EXCEPTION_COUNT.addAndGet(1);
        }
        else {
            NORMAL_EXCEPTION_COUNT.addAndGet(1);
        }
    }

    /**
     * 针对异常做处理
     *
     * @param message   异常消息
     * @param exception 异常obj
     */
    public static void processException(String message, Throwable exception) {
        exceptionCounter(exception);
        FRAMEWORK_LOG_HANDLER.processException(message, exception);
    }

    /**
     * 获取日志处理器
     *
     * @return
     */
    public static GlobalLogProcessor logProcessor() {
        return FRAMEWORK_LOG_HANDLER;
    }

}
