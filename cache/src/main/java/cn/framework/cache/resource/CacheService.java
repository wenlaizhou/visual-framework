package cn.framework.cache.resource;

import cn.framework.cache.init.FrameworkCache;
import cn.framework.core.annotation.Auth;
import cn.framework.core.annotation.Path;
import cn.framework.core.utils.Springs;
import cn.framework.core.utils.Strings;
import com.alibaba.fastjson.JSON;
import net.sf.ehcache.Element;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * project code
 * package cn.framework.cache.resource
 * create at 16/3/30 下午6:51
 *
 * @author wenlai
 */
@Path("/cache/service")
@Auth
public class CacheService implements Servlet {

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {

    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        servletResponse.setContentType("text/html;charset=UTF-8;pageEncoding=UTF-8");
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        FrameworkCache handler = Springs.get(FrameworkCache.BEAN_NAME);
        if (handler == null) {
            response.sendError(500, "cache is not init success!");
            return;
        }
        String action = request.getParameter("action");
        if (Strings.isNullOrEmpty(action)) {
            response.sendError(405, "param error");
            return;
        }
        ACTION act = ACTION.valueOf(action.toUpperCase());
        if (act == null) {
            response.sendError(405, "param error");
            return;
        }
        String cacheName = request.getParameter("cache");
        if (Strings.isNotNullOrEmpty(cacheName)) {
            net.sf.ehcache.Cache cache = handler.getCache(cacheName);
            if (cache == null) {
                response.sendError(500, "no this cache!");
                return;
            }
            switch (act) {
                case PUT:
                    cache.put(new Element(request.getParameter("key"), request.getParameter("value")));
                    response.getWriter().append(Strings.append("success"));
                    break;
                case GET:
                    response.getWriter().append(JSON.toJSONString(cache.get(request.getParameter("key"))));
                    break;
                case REMOVE:
                    response.getWriter().append(JSON.toJSONString(cache.remove(request.getParameter("key"))));
                    break;
                case KEYS:
                    response.getWriter().append(JSON.toJSONString(cache.getKeys()));
                    break;
                case ENQUEUE:
                    CachedEvent event = new CachedEvent();
                    event.setFrom(request.getParameter("from"));
                    event.setTitle(request.getParameter("title"));
                    event.setMessage(request.getParameter("message"));
                    CacheQ q = Springs.get(CacheQ.BEAN_NAME);
                    if (q != null) {
                        q.enQ(event);
                        response.getWriter().append("enqueue success!");
                    }
                    else {
                        response.sendError(500, "cacheQ is not available");
                    }
                    break;
            }
        }
    }

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {

    }

    public enum ACTION {
        PUT,
        GET,
        REMOVE,
        KEYS,
        ENQUEUE
    }
}
