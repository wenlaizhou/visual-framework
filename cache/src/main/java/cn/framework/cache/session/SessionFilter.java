/**
 * @项目名称: cache
 * @文件名称: SessionFilter.java
 * @Date: 2015年12月8日
 * @author: wenlai
 * @type: SessionFilter
 */
package cn.framework.cache.session;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import cn.framework.core.utils.Strings;

/**
 * @author wenlai
 *
 */
public class SessionFilter implements Filter {
    
    /*
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        
    }
    
    /*
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse rep, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) rep;
        if (Strings.isNullOrEmpty(SessionProvider.getSessionId(request)))
            SessionProvider.generateSessionId(request, response);
        chain.doFilter(req, rep);
    }
    
    /*
     * @see javax.servlet.Filter#destroy()
     */
    @Override
    public void destroy() {
        
    }
    
}
