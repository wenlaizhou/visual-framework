/**
 * @项目名称: framework
 * @文件名称: Example.java
 * @Date: 2015年10月15日
 * @author: wenlai
 * @type: Example
 */
package cn.framework.rest.tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口示例注解
 * @author wenlai
 */
@Target(
{ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Example
{
    String value();
}
