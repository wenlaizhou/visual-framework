package cn.framework.db.view;

import cn.framework.core.utils.Strings;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * project code
 * package cn.framework.db.view
 * create at 16/3/24 上午11:13
 *
 * @author wenlai
 */
public class TokenProcessor extends HttpServlet {

    /**
     * token处理
     *
     * @param request  请求
     * @param response 响应
     *
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String sessionId = request.getRequestedSessionId();
            if (Strings.isNotNullOrEmpty(sessionId)) {

                // TODO imp
            }
        }
        catch (Exception x) {
            response.sendError(401);
        }
    }
}
