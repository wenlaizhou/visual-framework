/**
 * @项目名称: core
 * @文件名称: FrameworkFilter.java
 * @Date: 2016年2月1日
 * @author: wenlai
 * @type: FrameworkFilter
 */
package cn.framework.core.container;

import org.springframework.stereotype.Service;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * @author wenlai
 */
@WebFilter
@Service("frameworkFilter")
public class FrameworkFilter implements Filter {

    /*
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // TODO Auto-generated method stub

    }

    /*
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // TODO Auto-generated method stub
        chain.doFilter(request, response);
    }

    /*
     * @see javax.servlet.Filter#destroy()
     */
    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }
}
