/**
 * @项目名称: framework
 * @文件名称: Route.java
 * @Date: 2015年10月26日
 * @author: wenlai
 * @type: Route
 */
package cn.framework.mvc.route;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import cn.framework.core.log.LogProvider;
import cn.framework.core.utils.KVMap;
import cn.framework.core.utils.Reflects;
import cn.framework.core.utils.Strings;
import cn.framework.mvc.model.ActionResult;
import cn.framework.mvc.model.Model;
import cn.framework.mvc.resource.HttpContext;
import cn.framework.mvc.route.ActionContainer.METHOD;
import com.alibaba.fastjson.JSON;
import static cn.framework.mvc.route.ActionContainer.*;

/**
 * 路由器<br>
 * 增加对多文件上传支持
 * <multipart-config>
 * <max-file-size>52428800</max-file-size>
 * <max-request-size>52428800</max-request-size>
 * <file-size-threshold>0</file-size-threshold>
 * </multipart-config>
 * TODO 增加404 500页面配置<br>
 * TODO 配置日志是否显示到界面<br>
 * 
 * @author wenlai
 */
@MultipartConfig
public class Route implements Servlet {
    
    /*
     * @see javax.servlet.Servlet#init(javax.servlet.ServletConfig)
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        this.config = config;
    }
    
    /*
     * 路由入口
     * 
     * @see javax.servlet.Servlet#service(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
     */
    @Override
    public void service(ServletRequest r, ServletResponse rp) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) r;
        HttpServletResponse response = (HttpServletResponse) rp;
        response.setCharacterEncoding("utf-8");
        request.setCharacterEncoding("utf-8");
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET,POST");
        boolean async = false;
        try {
            ActionContainer a = findAction(r);
            if (a != null) {
                // check method
                switch (a.method) {
                    case GET :
                        if (!request.getMethod().toUpperCase().equals(METHOD.GET.toString())) {
                            response.sendError(405, "Method Not Allowed");
                            return;
                        }
                        break;
                    case POST :
                        if (!request.getMethod().toUpperCase().equals(METHOD.POST.toString())) {
                            response.sendError(405, "Method Not Allowed");
                            return;
                        }
                        break;
                    case ALL:
                        break;
                    default :
                        break;
                }
                // execute action
                // ActionResult result = a.hasContextResource ? (ActionResult) Reflects.invoke(a.actionClassName, a.methodName, new Class<?>[]{HttpContext.class}, null, HttpContext.wrap(request, response)) : (ActionResult) Reflects.invoke(a.actionClassName, a.methodName, null);
                ActionResult result;
                if (a.hasContextResource) {
                    result = (ActionResult) Reflects.invoke(a.actionClassName, a.methodName, new Class<?>[]{HttpContext.class}, null, HttpContext.wrap(request, response));
                }
                else {
                    result = (ActionResult) Reflects.invoke(a.actionClassName, a.methodName, null);
                }
                // set model
                Model.set(r, result);
                switch (result.action) {
                    case VIEW :
                        if (a.hasView) {
                            String ajaxHeader = request.getHeader("x-requested-with");
                            String accept = request.getHeader("Accept");
                            if (Strings.isNotNullOrEmpty(ajaxHeader) || Strings.isNullOrEmpty(accept) || accept.contains("application/json")) {
                                response.setContentType(CONTENT_TYPE_JSON);
                                rp.getWriter().write(JSON.toJSONString(Model.get(r)));
                            }
                            else {
                                response.setContentType(CONTENT_TYPE_HTML);
                                r.getRequestDispatcher(a.viewPath).forward(request, response);
                            }
                        }
                        else {
                            response.setContentType(CONTENT_TYPE_JSON);
                            rp.getWriter().write(JSON.toJSONString(Model.get(r)));
                        }
                        return;
                    case JSON :
                        response.setContentType(CONTENT_TYPE_JSON);
                        rp.getWriter().write(JSON.toJSONString(Model.get(r)));
                        return;
                    case REDIRECT :
                        response.sendRedirect(result.model.toString());
                        return;
                    case DISPATCHER :
                        r.getRequestDispatcher(result.model.toString()).forward(r, rp); // TODO 带model的dispatch
                        return;
                    case ERROR :
                        int code = 404;
                        String message = "NOT FOUND";
                        if (result.model != null && result.model instanceof KVMap) {
                            KVMap mod = (KVMap) result.model;
                            code = mod.getInt(ActionResult.CODE_KEY, 404);
                            message = mod.getString(ActionResult.MESSAGE_KEY, "NOT FOUND");
                        }
                        response.sendError(code, message);
                        return;
                    case ASYNC :
                        async = true;
                        break;
                    case PLAIN :
                        response.setContentType(CONTENT_TYPE_PLAIN);
                        Object model = Model.get(r);
                        if (model != null)
                            rp.getWriter().write(model.toString());
                        return;
                    default :
                        break;
                }
            }
        }
        catch (Exception x) {
            LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
            response.getWriter().append(x.getMessage());
            response.sendError(500);
            return;
        }
        if (!async)
            response.sendError(404, "NOT FOUND");
    }
    
    /*
     * @see javax.servlet.Servlet#getServletInfo()
     */
    @Override
    public String getServletInfo() {
        return "this is a framework by wenlai_zhou@126.com";
    }
    
    /*
     * @see javax.servlet.Servlet#destroy()
     */
    @Override
    public void destroy() {
        
    }
    
    /*
     * @see javax.servlet.Servlet#getServletConfig()
     */
    @Override
    public ServletConfig getServletConfig() {
        return config;
    }
    
    /**
     * 配置项
     */
    private ServletConfig config;
    
    /**
     * utf8-html的content type
     */
    public static final String CONTENT_TYPE_HTML = "text/html;charset=UTF-8";
    
    /**
     * utf8-json的content type
     */
    public static final String CONTENT_TYPE_JSON = "text/json;charset=UTF-8";
    
    /**
     * utf8-text的content type
     */
    public static final String CONTENT_TYPE_PLAIN = "text/plain;charset=UTF-8";
    
}
