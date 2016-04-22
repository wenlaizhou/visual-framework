package cn.framework.cache.resource;

import cn.framework.cache.init.FrameworkCache;
import cn.framework.core.annotation.Auth;
import cn.framework.core.annotation.Path;
import cn.framework.core.container.FrameworkContainer;
import cn.framework.core.utils.Springs;
import cn.framework.core.utils.Strings;

import javax.servlet.*;
import java.io.IOException;

import static cn.framework.core.utils.Pair.newPair;

/**
 * project code
 * package cn.framework.cache.resource
 * create at 16/3/30 下午6:31
 *
 * @author wenlai
 */
@Auth
@Path("/cache-ui")
public class CacheMonitor implements Servlet {

    public static final String template = FrameworkContainer.buildUI("cn/framework/cache/resource/CacheMonitor.html", CacheMonitor.class.getClassLoader());

    public static final String tr_template = "<tr><td>${no}</td><td>${name}</td><td>${count}</td><td>${put}</td><td>${hit}</td><td>${expire}</td><td>${delete}</td><td>${evict}</td><td><a href='${detail}' target='_blank'>查看详细</a></td></tr>";

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {

    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {

        /**
         * 缓存put数量
         * 当前元素数量
         * 命中数
         * 命中率
         * 过期数
         * 删除数
         * 淘汰数
         */
        servletResponse.setContentType("text/html;charset=UTF-8;pageEncoding=UTF-8");
        StringBuilder result = new StringBuilder();
        FrameworkCache provider = Springs.get(FrameworkCache.BEAN_NAME);
        int no = 1;
        if (provider != null) {
            for (String cacheName : provider.getManager().getCacheNames()) {
                net.sf.ehcache.Cache cache = provider.getCache(cacheName);
                if (cache != null) {
                    result.append(Strings.format(tr_template, newPair("no", no++), newPair("name", cacheName), newPair("count", cache.getKeys().size()), newPair("put", cache.getStatistics().cachePutCount()), newPair("hit", cache.getStatistics().cacheHitCount()), newPair("expire", cache.getStatistics().cacheExpiredCount()), newPair("delete", cache.getStatistics().cacheRemoveCount()), newPair("evict", cache.getStatistics().cacheEvictedCount()), newPair("detail", Strings.append("/cache-detail?cache=", cacheName))));
                }
            }
        }
        servletResponse.getWriter().append(template.replace("<content></content>", result));
    }

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {

    }
}
