/**
 * @项目名称: core
 * @文件名称: HandlerAdapter.java
 * @Date: 2016年1月4日
 * @author: wenlai
 * @type: HandlerAdapter
 */
package cn.framework.core.log;

import java.text.MessageFormat;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * @author wenlai
 *
 */
public class HandlerAdapter extends Handler {
    
    /**
     * 初始化
     */
    public HandlerAdapter() {
        // TODO more config
        // LogManager.getLogManager().getProperty("xxxx");
    }
    
    /*
     * @see java.util.logging.Handler#publish(java.util.logging.LogRecord)
     */
    @Override
    public void publish(LogRecord record) {
        switch (record.getLevel().getName()) {
            case "INFO" :
                LogProvider.getFrameworkInfoLogger().info(MessageFormat.format(record.getMessage(), record.getParameters()));
                break;
            default :
                LogProvider.getFrameworkErrorLogger().error(MessageFormat.format(record.getMessage(), record.getParameters()));
                break;
        }
    }
    
    /*
     * @see java.util.logging.Handler#flush()
     */
    @Override
    public void flush() {
        // TODO flush --->>>
    }
    
    /*
     * @see java.util.logging.Handler#close()
     */
    @Override
    public void close() throws SecurityException {
        
    }
}
