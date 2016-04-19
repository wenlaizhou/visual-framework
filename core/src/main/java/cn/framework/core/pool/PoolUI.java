/**
 * @项目名称: framework
 * @文件名称: PoolUI.java
 * @Date: 2015年11月12日
 * @author: wenlai
 * @type: PoolUI
 */
package cn.framework.core.pool;

import cn.framework.core.container.FrameworkContainer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author wenlai
 */
public class PoolUI extends HttpServlet {

    /**
     * long
     */
    private static final long serialVersionUID = -1251174052280000085L;

    /**
     * template
     */
    public static String POOL_HTML_TEMPLATE = FrameworkContainer.buildUI("cn/framework/core/pool/pool.html", PoolUI.class.getClassLoader());

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
        int index = 1;
        for (String poolKey : Pool.watchDog.keySet()) {
            html.append(String.format("<tr><th scope=\"row\">%1$s</th><td>%2$s</td><td>%3$s</td><td>%4$s</td></tr>", index++, poolKey, Pool.watchDog.get(poolKey).getIdleCount(), Pool.watchDog.get(poolKey).size));
        }
        outPrinter.append(POOL_HTML_TEMPLATE.replace("<content></content>", html.toString()));
    }
}
