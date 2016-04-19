/**
 * @项目名称: framework
 * @文件名称: Path.java
 * @Date: 2015年10月26日
 * @author: wenlai
 * @type: Path
 */
package cn.framework.mvc.tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 作为一个controller中的<strong>action</strong>，必须打上此标签<br>
 * 
 * @author wenlai
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Action {
    
    /**
     * action对应的路径
     * 
     * @return
     */
    String value();
}
