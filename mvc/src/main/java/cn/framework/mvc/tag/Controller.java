/**
 * @项目名称: framework
 * @文件名称: Controller.java
 * @Date: 2015年10月26日
 * @author: wenlai
 * @type: Controller
 */
package cn.framework.mvc.tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * mvc中的controller必须要打上的标签
 * 
 * @author wenlai
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {
    
    /**
     * controller的路径
     * 
     * @return
     */
    String value();
}
