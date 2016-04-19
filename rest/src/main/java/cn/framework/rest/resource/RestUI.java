/**
 * @项目名称: framework
 * @文件名称: RestUI.java
 * @Date: 2015年10月15日
 * @author: wenlai
 * @type: RestUI
 */
package cn.framework.rest.resource;

import cn.framework.core.container.FrameworkContainer;
import cn.framework.core.utils.Arrays;
import cn.framework.rest.utils.Packages;
import cn.framework.rest.utils.Packages.ServicePathContainer;
import com.alibaba.fastjson.JSON;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wenlai
 */
public class RestUI extends HttpServlet {

    /**
     * api列表html页面
     */
    public final static String API_LIST_HTML_TEMPLATE = FrameworkContainer.buildUI("cn/framework/rest/resource/api.html", RestUI.class.getClassLoader());
    /**
     * api详情html页面
     */
    public final static String API_DETAIL_HTML_TEMPLATE = FrameworkContainer.buildUI("cn/framework/rest/resource/detail.html", RestUI.class.getClassLoader());
    /**
     * long
     */
    private static final long serialVersionUID = 8356353968533868498L;

    private static String API_LIST_HTML;

    private static String API_HTML;

    @SuppressWarnings("unused")
    private static String[] PACKAGES;

    /*
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!FrameworkContainer.basicAuth(req, resp)) {
            return;
        }
        synchronized (RestUI.class) {
            if (API_LIST_HTML == null) {
                Map<String, ServicePathContainer> servicePathContainers = new HashMap<String, ServicePathContainer>();
                if (Arrays.isNotNullOrEmpty(PACKAGES)) {
                    for (String packageName : PACKAGES)
                        servicePathContainers.putAll(Packages.scanServicePackage(packageName));
                }
                API_LIST_HTML = API_LIST_HTML_TEMPLATE.replace("{data}", JSON.toJSONString(servicePathContainers));
                API_HTML = API_DETAIL_HTML_TEMPLATE.replace("{data}", JSON.toJSONString(servicePathContainers));
            }
            resp.addHeader("Content-Type", "text/html;charset=utf-8");
            resp.getWriter().append(req.getRequestURI().endsWith("api") ? API_HTML : API_LIST_HTML);
        }

    }
}
