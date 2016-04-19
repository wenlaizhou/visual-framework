/**
 * @项目名称: core
 * @文件名称: Async.java
 * @Date: 2016年2月1日
 * @author: wenlai
 * @type: Async
 */
package cn.framework.core.plug;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import cn.framework.core.utils.Strings;

/**
 * 异步执行
 * 
 * @author wenlai
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Async {
    
    /**
     * 延迟
     * 
     * @return
     */
    int delaySeconds() default 0;
    
    /**
     * 回调
     * 
     * @return
     */
    String callback() default Strings.EMPTY;
}
