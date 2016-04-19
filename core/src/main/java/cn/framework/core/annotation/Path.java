package cn.framework.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * project code
 * package cn.framework.core.annotation
 * create at 16/3/30 下午2:05
 * this is use for servlet <br/>
 * 标示servlet的注册路径
 *
 * @author wenlai
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Path {
    /**
     * 路径值
     *
     * @return
     */
    String value();
}
