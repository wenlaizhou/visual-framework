/**
 * @项目名称: mvc
 * @文件名称: Context.java
 * @Date: 2015年12月31日
 * @author: wenlai
 * @type: Context
 */
package cn.framework.mvc.tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 所有需要使用http资源，都必须标记
 * 
 * @author wenlai
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Resource {
    
}
