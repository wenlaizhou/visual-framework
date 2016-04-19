/**
 * @项目名称: mvc
 * @文件名称: DefaultAction.java
 * @Date: 2016年2月5日
 * @author: wenlai
 * @type: DefaultAction
 */
package cn.framework.mvc.tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * controller默认的action
 * 
 * @author wenlai
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultAction {
    
}
