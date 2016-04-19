/**
 * @项目名称: framework
 * @文件名称: View.java
 * @Date: 2015年10月26日
 * @author: wenlai
 * @type: View
 */
package cn.framework.mvc.tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 如果action返回视图，必须标注此标签
 * 
 * @author wenlai
 *
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface View {
    
    /**
     * view的访问路径
     * 
     * @return
     */
    String value();
}
