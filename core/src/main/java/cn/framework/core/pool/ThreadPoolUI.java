/**
 * @项目名称: core
 * @文件名称: ThreadPoolUI.java
 * @Date: 2016年1月28日
 * @author: wenlai
 * @type: ThreadPoolUI
 */
package cn.framework.core.pool;

import cn.framework.core.container.FrameworkContainer;
import cn.framework.core.event.ThreadPoolMonitor;
import cn.framework.core.utils.Files;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * @author wenlai
 */
public class ThreadPoolUI extends HttpServlet {

    //    public final static String TR = "<tr><th scope=\"row\">%1$s</th><td>%2$s</td><td>%3$s</td><td>%4$s</td><td>%5$s</td><td>%6$s</td><td>%7$s</td><td>%8$s</td><td>%9$s</td></tr>";
    public final static String TR = "<tr><th scope=\"row\">%1$s</th><td>%2$s</td></tr>";

    public final static String THREAD_POOL_HTML_TEMPLATE = FrameworkContainer.buildUI("cn/framework/core/pool/thread-pool.html", ThreadPoolUI.class.getClassLoader());
    /**
     * long
     */
    private static final long serialVersionUID = -1159964741711171321L;

    /*
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!FrameworkContainer.basicAuth(req, resp)) {
            return;
        }
        resp.setContentType("text/html;charset=UTF-8;pageEncoding=UTF-8");
        PrintWriter outPrinter = resp.getWriter();
        StringBuilder html = new StringBuilder();
        int flag = 1;
        List<ThreadPoolMonitor.ExecutorData> dataList = ThreadPoolMonitor.collectMonitorData();
        for (ThreadPoolMonitor.ExecutorData data : dataList) {
            //            html.append(String.format(TR, flag++, data.description, data.activeCount, data.corePoolSize, data.keepAliveMilliSeconds, data.largestPoolSize, data.maximumPoolSize, data.poolSize, data.taskCount));
            html.append(String.format(TR, flag++, data.description));
        }
        outPrinter.append(THREAD_POOL_HTML_TEMPLATE.replace("<content></content>", html.toString()));
    }
}
