/**
 * @项目名称: core
 * @文件名称: ThreadUI.java
 * @Date: 2016年1月29日
 * @author: wenlai
 * @type: ThreadUI
 */
package cn.framework.core.pool;

import cn.framework.core.container.FrameworkContainer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

/**
 * @author wenlai
 */
public class ThreadUI extends HttpServlet {

    public final static String TR = "<tr><th scope=\"row\">%1$s</th><td>%2$s</td><td>%3$s</td><td>%4$s</td><td>%5$s</td></tr>";
    public final static String TR_CHILD = "<tr flag='%2$s' style='display:none;'><th scope=\"row\"></th><td></td><td></td><td></td><td>%1$s</td></tr>";
    public final static String DOWN = "<span class='glyphicon glyphicon-chevron-down' aria-hidden='true' onclick='trClick(this, %1$s);'></span>";
    /**
     * long
     */
    private static final long serialVersionUID = 8973099073110089931L;
    public static String THREAD_HTML = FrameworkContainer.buildUI("cn/framework/core/pool/thread.html", ThreadUI.class.getClassLoader());

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
        Set<Thread> threads = Thread.getAllStackTraces().keySet();
        if (threads != null && threads.size() > 0) {
            int id = 1;
            for (Thread thread : threads) {
                html.append(String.format(TR, id++, thread.getId(), thread.getName(), thread.getState(), String.format(DOWN, thread.getId())));
                StackTraceElement[] traceInfos = thread.getStackTrace();
                if (traceInfos != null && traceInfos.length > 0) {
                    for (StackTraceElement traceInfo : traceInfos) {
                        html.append(String.format(TR_CHILD, traceInfo, thread.getId()));
                    }
                }
            }
        }
        outPrinter.append(THREAD_HTML.replace("<content></content>", html.toString()));
    }
}
