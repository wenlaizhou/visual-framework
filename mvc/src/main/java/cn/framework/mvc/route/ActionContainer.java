/**
 * @项目名称: framework
 * @文件名称: ActionContainer.java
 * @Date: 2015年10月26日
 * @author: wenlai
 * @type: ActionContainer
 */
package cn.framework.mvc.route;

import java.util.Map;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import cn.framework.mvc.resource.HttpContext;

/**
 * action容器
 * 
 * @author wenlai
 */
public class ActionContainer {
    
    /**
     * action对应的controller类名
     */
    public String actionClassName;
    
    /**
     * 是否具有@Resource标识<br>
     * 该标识可以将原始{@link HttpContext}当成参数赋值给action
     */
    public boolean hasContextResource = false;
    
    /**
     * 映射的jsp文件路径
     */
    public String viewPath;
    
    /**
     * controller instance
     */
    @Deprecated
    public Object obj;
    
    /**
     * 是否具有jsp页面资源
     */
    public boolean hasView = false;
    
    /**
     * 可以接受的访问类型<br>
     * 默认为all
     */
    public METHOD method = METHOD.ALL;
    
    /**
     * 访问类型
     * 
     * @author wenlai
     */
    public static enum METHOD {
        /**
         * 只允许get访问
         */
        GET,
        
        /**
         * 只允许post访问
         */
        POST,
        
        /**
         * 可接受全部访问类型
         */
        ALL
    }
    
    /**
     * action对应的方法名称
     */
    public String methodName;
    
    /**
     * action容器，内部使用<br>
     * 只读，不需要考虑线程安全
     */
    static Map<String, ActionContainer> instance;
    
    /**
     * 获取action，内部使用
     * 
     * @param req
     * @return
     */
    static ActionContainer findAction(ServletRequest req) {
        return ActionContainer.instance.get(((HttpServletRequest) req).getRequestURI());
    }
}
