/**
 * @项目名称: core
 * @文件名称: Callback.java
 * @Date: 2016年2月1日
 * @author: wenlai
 * @type: Callback
 */
package cn.framework.core.plug;

/**
 * 回调
 * 
 * @author wenlai
 */
public interface Callback {
    
    /**
     * 回调方法
     * 
     * @param result
     */
    void done(Object result);
    
}
