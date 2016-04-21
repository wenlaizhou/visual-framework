/**
 * @项目名称: framework
 * @文件名称: Request.java
 * @Date: 2015年10月26日
 * @author: wenlai
 * @type: Request
 */
package cn.framework.mvc.tag;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 如果需要将{@link HttpServletRequest} 注入到action中，需要在参数前加上此标签<br>
 * <p/>
 * <pre>
 *
 * <code>@Action("/xxxx/xx.jsp")</code>
 * public static {@link ActionResult} funcName(<strong><b>@Request</b> {@link HttpServletRequest} req</strong>)
 * {
 *     .....
 * }
 * </pre>
 *
 * @author wenlai
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Request {

}
