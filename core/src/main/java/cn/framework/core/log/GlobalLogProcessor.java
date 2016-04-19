package cn.framework.core.log;

import org.apache.logging.log4j.Logger;

/**
 * project code
 * package cn.framework.core.log
 * create at 16/3/15 上午11:34
 *
 * @author wenlai
 */
public interface GlobalLogProcessor {

    /**
     * 异常处理
     *
     * @param x 异常
     */
    void processException(Throwable x);

    /**
     * 异常处理
     *
     * @param message 异常消息
     * @param x       异常
     */
    void processException(String message, Throwable x);

    /**
     * 获取framework - error - logger
     *
     * @return
     */
    Logger logger();

    /**
     * 获取特定名称的logger
     *
     * @param name logger-name
     *
     * @return
     */
    Logger logger(String name);

}
