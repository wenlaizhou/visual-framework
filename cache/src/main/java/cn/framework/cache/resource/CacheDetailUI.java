package cn.framework.cache.resource;

import cn.framework.cache.init.FrameworkCache;
import cn.framework.core.annotation.Auth;
import cn.framework.core.annotation.Path;
import cn.framework.core.container.FrameworkContainer;
import cn.framework.core.utils.Exceptions;
import cn.framework.core.utils.Springs;
import cn.framework.core.utils.Strings;
import com.alibaba.fastjson.JSON;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static cn.framework.core.utils.Pair.newPair;

/**
 * project code
 * package cn.framework.cache.resource
 * create at 16/4/13 下午5:47
 *
 * @author wenlai
 */
@Path("/cache-detail")
@Auth
public class CacheDetailUI implements Servlet {

    public static final String template = FrameworkContainer.buildUI("cn/framework/cache/resource/CacheDetail.html", CacheDetailUI.class.getClassLoader());

    public static final String tr = "<tr>" +
            "            <td>${no}</td>" +
            "            <td>${key}</td>" +
            "            <td>${value}</td>" +
            "            <td><a class=\"btn btn-danger btn-sm\" href=\"/cache/service?cache=${cache}&action=remove&key=${key}\" role=\"button\">删除</a></td>" +
            "        </tr>";

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {

    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    /**
     * cache ui service
     *
     * @param servletRequest  httpRequest
     * @param servletResponse httpResponse
     *
     * @throws ServletException
     * @throws IOException
     */
    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        servletResponse.setContentType("text/html;charset=UTF-8;pageEncoding=UTF-8");
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (Strings.isNullOrEmpty(request.getParameter("cache"))) {
            response.sendError(405);
            return;
        }

        FrameworkCache handler = Springs.get(FrameworkCache.BEAN_NAME);
        net.sf.ehcache.Cache cache;
        if (handler != null && (cache = handler.getCache(request.getParameter("cache"))) != null) {
            StringBuilder result = new StringBuilder();
            AtomicInteger i = new AtomicInteger(1);
            cache.getKeysWithExpiryCheck().parallelStream().forEach(k -> {
                try {
                    result.append(Strings.format(tr, newPair("no", i.getAndIncrement()), newPair("key", k), newPair("value", JSON.toJSONString(cache.getQuiet(k).getObjectValue())), newPair("cache", request.getParameter("cache"))));
                }
                catch (Exception x) {
                    Exceptions.processException(x);
                }
            });
            response.getWriter().append(template.replace("<content></content>", result));
        }
    }

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {

    }
}
