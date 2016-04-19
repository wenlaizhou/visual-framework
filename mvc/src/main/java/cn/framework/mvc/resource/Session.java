/**
 * @项目名称: mvc
 * @文件名称: Session.java
 * @Date: 2015年12月31日
 * @author: wenlai
 * @type: Session
 */
package cn.framework.mvc.resource;

import cn.framework.cache.session.SessionProvider;

/**
 * @author wenlai
 *
 */
public class Session {
    
    /**
     * 上下文信息
     */
    private HttpContext context;
    
    /**
     * 构造
     * 
     * @param context
     */
    public Session(HttpContext context) {
        this.context = context;
    }
    
    /**
     * 设置值
     * 
     * @param key
     * @param value
     * @param req
     * @return
     */
    public void set(String key, Object value) {
        SessionProvider.set(key, value, this.context.getRequest());
    }
    
    /**
     * 登出
     * 
     * @param req
     */
    public void logout() {
        SessionProvider.logout(this.context.getRequest());
    }
    
    /**
     * 获取值
     * 
     * @param key
     * @param req
     * @return
     */
    public <T> T get(String key) {
        return SessionProvider.get(key, this.context.getRequest());
    }
    
    /**
     * 登录
     * 
     * @param req
     * @return
     */
    public void login(String username) {
        SessionProvider.login(username, this.context.getRequest());
    }
    
    /**
     * 获取username
     * 
     * @param req
     * @return
     */
    public String getUsername() {
        return SessionProvider.getUsername(this.context.getRequest());
    }
    
    /**
     * 判断是否登录
     * 
     * @param req
     * @return
     */
    public boolean isLogon() {
        return SessionProvider.isLogon(this.context.getRequest());
    }
    
    /**
     * 从cookie中获取sessionid
     * 
     * @param req
     * @return
     */
    public String getSessionId() {
        return SessionProvider.getSessionId(this.context.getRequest());
    }
    
}
