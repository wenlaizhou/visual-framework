package cn.framework.cache.resource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * project code
 * package cn.framework.cache.resource
 * create at 16/4/6 下午3:20
 *
 * @author wenlai
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Pattern {
    String value();
}
