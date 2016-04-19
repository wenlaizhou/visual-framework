/**
 * @项目名称: mvc
 * @文件名称: Post.java
 * @Date: 2015年12月9日
 * @author: wenlai
 * @type: Post
 */
package cn.framework.mvc.tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 只接受Post请求
 * 
 * @author wenlai
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Post {
    
}
