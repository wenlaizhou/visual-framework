/**
 * @项目名称: framework2
 * @文件名称: InitProvider.java
 * @Date: 2015年10月14日
 * @author: wenlai
 * @type: InitProvider
 */
package cn.framework.core.container;

/**
 * 初始化父类，定义初始化接口使用
 * 
 * @author wenlai
 */
public interface InitProvider {
    
    /**
     * 初始化
     * 
     * @param context 配置上下文
     * @throws Exception
     */
    void init(final Context context) throws Exception;
}
