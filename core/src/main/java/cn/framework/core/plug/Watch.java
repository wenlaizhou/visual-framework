/**
 * @项目名称: core
 * @文件名称: Watch.java
 * @Date: 2016年1月27日
 * @author: wenlai
 * @type: Watch
 */
package cn.framework.core.plug;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 监控
 * 
 * @author wenlai
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Watch {
    
    /**
     * 标记
     * 
     * @return
     */
    String value();
}
