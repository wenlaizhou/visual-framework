/**
 * @项目名称: mvc
 * @文件名称: Context.java
 * @Date: 2015年12月31日
 * @author: wenlai
 * @type: Context
 */
package cn.framework.mvc.resource;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import cn.framework.core.log.LogProvider;
import cn.framework.core.utils.Strings;
import cn.framework.mvc.route.ActionContainer.METHOD;

/**
 * 请求上下文
 * 
 * @author wenlai
 */
public class HttpContext {

    /**
     * 获取int类型的请求参数
     * @param key key
     * @return
     */
    public int getIntParam(String key) {
        return Strings.parseInt(key);
    }

    /**
     * 获取int类型请求参数
     * @param key key
     * @param defaultValue 默认值
     * @return
     */
    public int getIntParam(String key, int defaultValue) {
        return Strings.parseInt(key, defaultValue);
    }
    
    /**
     * 返回请求体内容
     * 
     * @return
     */
    public byte[] getRequestBody() {
        try {
            int length = this.req.getContentLength();
            if (length > 0) {
                byte[] buffer = new byte[length];
                this.req.getInputStream().read(buffer);
                return buffer;
            }
        }
        catch (Exception e) {
            LogProvider.getFrameworkErrorLogger().error(e.getMessage(), e);
        }
        return null;
    }
    
    /**
     * 请求句柄
     */
    private HttpServletRequest req;
    
    /**
     * 响应句柄
     */
    private HttpServletResponse resp;
    
    /**
     * session
     */
    private Session session;
    
    /**
     * 获取请求方法
     * 
     * @return
     */
    public METHOD getMethod() {
        return METHOD.valueOf(getRequest().getMethod().toUpperCase());
    }
    
    /**
     * 获取session
     * 
     * @return
     */
    public Session getSession() {
        return this.session;
    }
    
    /**
     * 获取请求头
     * 
     * @param key
     * @return
     */
    public String getHeader(String key) {
        return this.req.getHeader(key);
    }
    
    /**
     * 获取请求参数
     * 
     * @param key
     * @return
     */
    public String getParameter(String key) {
        return this.req.getParameter(key);
    }
    
    /**
     * 获取请求参数，如果没有，则返回默认值
     * 
     * @param key
     * @param defaultValue
     * @return
     */
    public String getParameter(String key, String defaultValue) {
        String result = this.req.getParameter(key);
        if (Strings.isNotNullOrEmpty(result))
            return result;
        return defaultValue;
    }
    
    /**
     * 获取请求句柄
     * 
     * @return
     */
    public final HttpServletRequest getRequest() {
        return this.req;
    }
    
    /**
     * 结束请求
     */
    public void end() {
        try {
            this.resp.flushBuffer();
            this.resp.getOutputStream().close();
        }
        catch (Exception e) {
            LogProvider.getFrameworkErrorLogger().error(e.getMessage(), e);
        }
    }
    
    /**
     * 获取响应句柄
     * 
     * @return
     */
    public final HttpServletResponse getResponse() {
        return this.resp;
    }
    
    /**
     * 创建cookie
     * 
     * @param req
     * @param resp
     */
    public HttpContext(HttpServletRequest req, HttpServletResponse resp) {
        this.req = req;
        this.resp = resp;
        this.session = new Session(this);
    }
    
    /**
     * 添加cookie
     * 
     * @param k
     * @param v
     */
    public void addCookie(String k, String v) {
        this.resp.addCookie(new Cookie(k, v));
    }
    
    /**
     * 添加cookie
     * 
     * @param k key
     * @param v value
     * @param domain 域
     * @param path 路径
     * @param expireSeconds 过期时间 大于-1
     */
    public void addCookie(String k, String v, String domain, String path, int expireSeconds) {
        Cookie cookie = new Cookie(k, v);
        if (Strings.isNotNullOrEmpty(domain))
            cookie.setDomain(domain);
        if (expireSeconds > -1)
            cookie.setMaxAge(expireSeconds);
        if (Strings.isNotNullOrEmpty(path))
            cookie.setPath(path);
        this.resp.addCookie(cookie);
    }
    
    /**
     * 删除cookie
     * 
     * @param key
     */
    public void deleteCookie(String key) {
        Cookie cookie = getCookieObj(key);
        if (cookie != null)
            cookie.setMaxAge(0);
    }
    
    /**
     * 获取cookie值
     * 
     * @param key
     * @return
     */
    public String getCookie(String key) {
        Cookie cookie = getCookieObj(key);
        if (cookie != null)
            return cookie.getValue();
        return Strings.EMPTY;
    }
    
    /**
     * 获取cookie对象
     * 
     * @param key
     * @return
     */
    public Cookie getCookieObj(String key) {
        Cookie[] cookies = this.req.getCookies();
        if (cookies != null && cookies.length > 0)
            for (Cookie cookie : cookies)
                if (cookie.getName().equals(key))
                    return cookie;
        return null;
    }
    
    /**
     * 包装请求及响应
     * 
     * @param req
     * @param resp
     * @return
     */
    public static HttpContext wrap(HttpServletRequest req, HttpServletResponse resp) {
        return new HttpContext(req, resp);
    }
}
