package cn.framework.cache.resource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * project visual-framework
 * package cn.framework.cache.resource
 * create at 16/4/22 下午5:49
 *
 * @author wenlai
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Name {
    String value();
}
