package cn.framework.cache.session;

import cn.framework.core.utils.Exceptions;
import cn.framework.core.utils.Guid;
import cn.framework.core.utils.Md5s;
import cn.framework.core.utils.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * project visual-framework
 * package cn.framework.cache.session
 * create at 16/4/22 上午12:15
 *
 * @author wenlai
 */
public class FrameworkSessionFilter implements Filter {

    public static final String ATTR_KEY = "frameworkSession";

    public static final String SESSION_ID_PRE = "framework";

    private Cache<String, String> kvCache = CacheBuilder.newBuilder().expireAfterAccess(3600, TimeUnit.SECONDS).build();

    private Cache<String, FrameworkSession> sessionCache = CacheBuilder.newBuilder().expireAfterAccess(3600, TimeUnit.SECONDS).build();

    /**
     * @param host
     *
     * @return
     */
    public String getSessionIdKey(String host) {
        try {
            return kvCache.get(host, () -> Md5s.compute(Strings.append(SESSION_ID_PRE, host)));
        }
        catch (Exception x) {
            Exceptions.processException(x);
            return Md5s.compute(Strings.append(SESSION_ID_PRE, host));
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        String host = request.getRemoteHost();
        String sessionId;
        if (Strings.isNotNullOrEmpty(sessionId = processSession(request))) {
            FrameworkSession session = sessionCache.getIfPresent(sessionId);
            if (session != null) {
                request.setAttribute(ATTR_KEY, session);
            }
            else {
                session = FrameworkSession.wrap(getSessionIdKey(host), sessionId);
                sessionCache.put(sessionId, session);
                request.setAttribute(ATTR_KEY, session);
            }
        }
        else {
            String sessionKey = getSessionIdKey(host);
            sessionId = Guid.guid();
            Cookie sessionCookie = new Cookie(sessionKey, sessionId);
            sessionCookie.setPath("/");
            response.addHeader(sessionKey, sessionId);
            response.addCookie(sessionCookie);
            FrameworkSession session = FrameworkSession.wrap(sessionKey, sessionId);
            sessionCache.put(sessionId, session);
            request.setAttribute(ATTR_KEY, session);
        }
        chain.doFilter(request, response);
    }

    private String processSession(HttpServletRequest request) {
        try {
            String sessionKey = getSessionIdKey(request.getRemoteHost());
            Cookie[] cookies = request.getCookies();
            if (cookies != null && cookies.length > 0) {
                for (Cookie cookie : cookies) {
                    String name = cookie.getName();
                    if (name.equals(sessionKey)) { //user have session and no need to build cookie
                        return cookie.getValue();
                    }
                }
            }
            String sessionId;
            if (Strings.isNotNullOrEmpty(sessionId = request.getParameter(sessionKey))) {
                return sessionId;
            }
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
        return Strings.EMPTY;
    }

    @Override
    public void destroy() {

    }
}
