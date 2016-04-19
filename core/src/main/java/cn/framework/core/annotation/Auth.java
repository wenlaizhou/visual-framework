package cn.framework.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * project code
 * package cn.framework.core.annotation
 * create at 16/3/30 下午6:32 <br />
 * servlet use auth
 *
 * @author wenlai
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auth {
}
