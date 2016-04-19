/**
 * @项目名称: core
 * @文件名称: Plugins.java
 * @Date: 2016年1月27日
 * @author: wenlai
 * @type: Plugins
 */
package cn.framework.core.plug;

import static cn.framework.core.utils.Exceptions.processException;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;

/**
 * 插件
 * 
 * @author wenlai
 */
public class Plugin {
    
    /**
     * 创建新代理
     * 
     * @param className 类名
     * @param wrapper 包装
     * @return
     */
    @SuppressWarnings("unchecked")
    static <T> T newInstance(String className, Callback wrapper) {
        try {
            Enhancer creator = new Enhancer();
            if (wrapper != null)
                creator.setCallback(wrapper);
            creator.setSuperclass(Class.forName(className));
            return (T) creator.create();
        }
        catch (Exception e) {
            processException(e);
        }
        return null;
    }
    
    /**
     * 获取插件实例
     * 
     * @param name
     * @return
     */
    public final static <T> T get(String name) {
        return PlugInitProvider.PLUG_CONTAINER.get(name);
    }
}
