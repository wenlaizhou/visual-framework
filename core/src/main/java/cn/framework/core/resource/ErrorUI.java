package cn.framework.core.resource;

import cn.framework.core.container.FrameworkContainer;
import cn.framework.core.utils.Strings;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static cn.framework.core.utils.Pair.newPair;

/**
 * project code
 * package cn.framework.core.resource
 * create at 16/3/18 下午7:04
 * 404 page
 *
 * @author wenlai
 */
public class ErrorUI extends HttpServlet {

    /**
     * 页面资源
     */
    private static final String PAGE = FrameworkContainer.buildUI("cn/framework/core/resource/Error.html", ErrorUI.class.getClassLoader());

    /**
     * 替换文本
     */
    private static final String TEMP = "<h4 class=\"text-center\">${code} ${message}</h4>";

    /**
     * httpCode转成消息
     *
     * @param httpCode code
     *
     * @return
     */
    private String getMsgFromCode(int httpCode) {

        switch (httpCode) {
            case 404:
                return "NOT FOUND";
            case 405:
                return "METHOD NOT ALLOWED";
            case 450:
                return "ILLEGAL CALL";
            case 401:
                return "UNAUTHORIZED";
            case 500:
                return "INTERNAL SERVER ERROR";
            default:
                return Strings.EMPTY;
        }
    }

    /**
     * get请求资源
     *
     * @param req  request
     * @param resp response
     *
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().append(buildHtml(resp.getStatus(), getMsgFromCode(resp.getStatus())));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().append(buildHtml(resp.getStatus(), getMsgFromCode(resp.getStatus())));
    }

    /**
     * 构建html文件
     *
     * @param code    code
     * @param message message
     *
     * @return
     */
    private String buildHtml(int code, String message) {
        return Strings.format(PAGE, newPair("placeHolder", Strings.format(TEMP, newPair("code", code), newPair("message", message))));
    }
}
