/**
 * @项目名称: core
 * @文件名称: PlugMonitor.java
 * @Date: 2016年1月27日
 * @author: wenlai
 * @type: PlugMonitor
 */
package cn.framework.core.plug;

import static cn.framework.core.utils.Exceptions.processException;
import java.lang.reflect.Method;
import org.apache.logging.log4j.Logger;
import cn.framework.core.log.LogProvider;
import cn.framework.core.pool.Task;
import cn.framework.core.pool.ThreadPool;
import cn.framework.core.utils.StopWatch;
import cn.framework.core.utils.Strings;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * 插件代理
 * 
 * @author wenlai
 */
class PluginWrapper implements MethodInterceptor {
    
    /**
     * String
     */
    private static final String PLUG_IN = "plug-in";
    
    /**
     * 日志
     */
    private Logger logger = LogProvider.getLogger("plugin");
    
    /**
     * 构造
     */
    public PluginWrapper() {
        
    }
    
    /**
     * 代理
     */
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        Object result = null;
        try {
            Watch watchDeclard = method.getDeclaredAnnotation(Watch.class);
            Async asyncDeclared = method.getDeclaredAnnotation(Async.class);
            if (asyncDeclared != null) {
                if (Strings.isNotNullOrEmpty(asyncDeclared.callback())) {
                    try {
                        Callback c = Plugin.get(asyncDeclared.callback());
                        ThreadPool.addTask(Task.wrap(PLUG_IN, () -> {
                            if (c != null) {
                                c.done(execute(obj, args, proxy, watchDeclard));
                            }
                            else {
                                execute(obj, args, proxy, watchDeclard);
                            }
                        }), asyncDeclared.delaySeconds());
                    }
                    catch (Exception ex) {
                        processException(ex);
                    }
                }
                else {
                    try {
                        ThreadPool.addTask(Task.wrap(PLUG_IN, () -> {
                            execute(obj, args, proxy, watchDeclard);
                        }), asyncDeclared.delaySeconds());
                    }
                    catch (Exception ex) {
                        processException(ex);
                    }
                }
            }
            else {
                result = execute(obj, args, proxy, watchDeclard);
            }
        }
        catch (Exception x) {
            processException(x);
        }
        return result;
    }
    
    /**
     * 执行
     * 
     * @param watch
     * @param obj
     * @param args
     * @param proxy
     * @param watchFlag
     * @return
     */
    public Object execute(Object obj, Object[] args, MethodProxy proxy, Watch watch) {
        try {
            if (watch != null) {
                StopWatch stopWatch = StopWatch.newWatch();
                Object result = proxy.invokeSuper(obj, args);
                logger.info(String.format("plug : [%1$s], process time: [%2$s] miliseconds", watch.value(), stopWatch.checkByOriginal()));
                return result;
            }
            else {
                return proxy.invoke(obj, args);
            }
        }
        catch (Throwable x) {
            processException(x);
        }
        return null;
    }
    
}
