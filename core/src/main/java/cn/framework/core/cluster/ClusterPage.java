package cn.framework.core.cluster;

import cn.framework.core.utils.Springs;
import cn.framework.core.utils.Strings;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * project code
 * package cn.framework.core.cluster
 * create at 16/3/25 下午8:00
 *
 * @author wenlai
 */
public class ClusterPage extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String message = req.getParameter("message");
        String id = req.getParameter("id");
        if (Strings.isNotNullOrEmpty(message) && Strings.isNotNullOrEmpty(id)) {
            ClusterHandler handler = Springs.get("clusterHandler");
            if (handler != null) {
                handler.send(Long.parseLong(id), message);
            }
            else {
                System.err.println("spring error");
            }
        }
    }
}
