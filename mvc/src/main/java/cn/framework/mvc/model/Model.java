/**
 * @项目名称: framework
 * @文件名称: Model.java
 * @Date: 2015年10月26日
 * @author: wenlai
 * @type: Model
 */
package cn.framework.mvc.model;

import javax.servlet.ServletRequest;
import cn.framework.core.log.LogProvider;

/**
 * mvc中的model帮助类
 * 
 * @author wenlai
 */
public class Model {
    
    /**
     * 获取Model
     * 
     * @param request
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(ServletRequest request) {
        try {
            Object model = request.getAttribute(MODEL_KEY);
            return model != null ? (T) model : null;
        }
        catch (Exception x) {
            LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
        }
        return null;
    }
    
    /**
     * 设置Model
     * 
     * @param request
     * @param result
     */
    public static <T> void set(ServletRequest request, ActionResult result) {
        if (result != null && request != null)
            request.setAttribute(MODEL_KEY, result.model);
    }
    
    /**
     * model对应key值
     */
    final static String MODEL_KEY = "framework-model";
}
