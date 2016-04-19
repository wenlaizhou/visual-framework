/**
 * @项目名称: framework2
 * @文件名称: Description.java
 * @Date: 2015年10月12日
 * @author: wenlai
 * @type: Description
 */
package cn.framework.rest.tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口说明注释
 * @author wenlai
 */
@Target(
{ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Description
{
    public String value();
}
