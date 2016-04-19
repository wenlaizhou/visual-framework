/**
 * @项目名称: framework
 * @文件名称: LogInitor.java
 * @Date: 2015年10月19日
 * @author: wenlai
 * @type: LogInitor
 */
package cn.framework.core.log;

import cn.framework.core.container.Context;
import cn.framework.core.container.InitProvider;
import cn.framework.core.utils.Exceptions;
import cn.framework.core.utils.Projects;
import org.apache.catalina.valves.AccessLogValve;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;

import static cn.framework.core.utils.Xmls.childAttribute;
import static cn.framework.core.utils.Xmls.xpathNode;

/**
 * 日志初始化类
 * 日志设计：
 * db.log
 * core.log
 * access.log
 * mvc.log
 * rest.log
 * cache.log
 * pool.log
 *
 * @author wenlai
 */
@Service("logInit")
public class LogInitProvider implements InitProvider {

    private final static String PATTERN = "%t [%s] [%D] [%a] [%r] ";
    private static volatile boolean INITED = false;

    /**
     * log4j配置通过注入方式加载
     *
     * @see cn.framework.core.container.InitProvider#init(cn.framework.core.container.Context)
     */
    @Override
    public synchronized void init(final Context context) throws Exception {

        try {
            if (INITED) {
                return;
            }

            // Node log4jNode = xpathNode("//log4j", context.getConf());
            // if (log4jNode != null) {
            // LogProvider.init(attr("src", log4jNode));
            // }
            // Node logPropertyNode = xpathNode("log-property", context.getConf());
            // if (logPropertyNode != null)
            // System.setProperty("java.util.logging.config.file", attr("src", logPropertyNode));
            Node containerLogNode = xpathNode("//log", context.getConf());
            AccessLogValve alv = new AccessLogValve();
            alv.setRotatable(true);
            if (containerLogNode != null) {
                alv.setDirectory(childAttribute("path", "value", containerLogNode, Projects.LOG_DIR));
                alv.setPrefix(childAttribute("prefix", "value", containerLogNode, "access_log."));
                alv.setSuffix(childAttribute("suffix", "value", containerLogNode, ".log"));
                alv.setFileDateFormat(childAttribute("file-format", "value", containerLogNode, "yyyy-MM-dd.HH"));
                alv.setPattern(childAttribute("pattern", "value", containerLogNode, PATTERN));
                alv.setRotatable(true);
            }
            else {
                alv.setDirectory(Projects.LOG_DIR);
                alv.setPrefix("access_log.");
                alv.setSuffix(".log");
                alv.setFileDateFormat("yyyy-MM-dd.HH");
                alv.setPattern(PATTERN);
                alv.setRotatable(true);
            }
            context.getTomcat().getHost().getPipeline().addValve(alv);
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
        finally {
            INITED = true;
        }
    }

    // %a - Remote IP address
    // %A - Local IP address
    // %b - Bytes sent, excluding HTTP headers, or '-' if zero
    // %B - Bytes sent, excluding HTTP headers
    // %h - Remote host name (or IP address if enableLookups for the connector is false)
    // %H - Request protocol
    // %l - Remote logical username from identd (always returns '-')
    // %m - Request method (GET, POST, etc.)
    // %p - Local port on which this request was received. See also %{xxx}p below.
    // %q - Query string (prepended with a '?' if it exists)
    // %r - First line of the request (method and request URI)
    // %s - HTTP status code of the response
    // %S - User session ID
    // %t - Date and time, in Common Log Format
    // %u - Remote user that was authenticated (if any), else '-'
    // %U - Requested URL path
    // %v - Local server name
    // %D - Time taken to process the request, in millis
    // %T - Time taken to process the request, in seconds
    // %F - Time taken to commit the response, in millis
    // %I - Current request thread name (can compare later with stacktraces)
    // %{xxx}i write value of incoming header with name xxx
    // %{xxx}o write value of outgoing header with name xxx
    // %{xxx}c write value of cookie with name xxx
    // %{xxx}r write value of ServletRequest attribute with name xxx
    // %{xxx}s write value of HttpSession attribute with name xxx
    // %{xxx}p write local (server) port (xxx==local) or remote (client) port (xxx=remote)
    // %{xxx}t write timestamp at the end of the request formatted using the enhanced SimpleDateFormat pattern xxx

}
