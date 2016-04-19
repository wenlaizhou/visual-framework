package cn.framework.core.log;

import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * project code
 * package cn.framework.core.log
 * create at 16/3/15 上午11:35
 *
 * @author wenlai
 */
@Service("globalLogProcessor")
public class FrameworkLogger implements GlobalLogProcessor {

    /**
     * 异常处理
     *
     * @param x 异常
     */
    @Override
    public void processException(Throwable x) {

        LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
    }

    /**
     * 异常处理
     *
     * @param message 异常消息
     * @param x       异常
     */
    @Override
    public void processException(String message, Throwable x) {
        LogProvider.getFrameworkErrorLogger().error(message, x);
    }

    /**
     * getFrameworkErrorLogger
     *
     * @return
     */
    @Override
    public Logger logger() {
        return LogProvider.getFrameworkErrorLogger();
    }

    /**
     * getLogger
     *
     * @param name logger-name
     *
     * @return
     */
    @Override
    public Logger logger(String name) {
        return LogProvider.getLogger(name);
    }
}
