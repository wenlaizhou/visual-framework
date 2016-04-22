package cn.framework.cache.session;

import cn.framework.cache.init.FrameworkCache;
import cn.framework.core.utils.Exceptions;
import cn.framework.core.utils.Springs;
import cn.framework.core.utils.Strings;
import net.sf.ehcache.Element;

import javax.servlet.ServletRequest;

/**
 * project visual-framework
 * package cn.framework.cache.session
 * create at 16/4/22 上午12:14
 *
 * @author wenlai
 */
public class FrameworkSession {

    public final String sessionKey;

    public final String sessionId;

    public FrameworkSession(String sessionKey, String sessionId) {
        this.sessionId = sessionId;
        this.sessionKey = sessionKey;
    }

    /**
     * 从request之中获取session
     *
     * @param request
     *
     * @return
     */
    public static FrameworkSession getSession(ServletRequest request) {
        return (FrameworkSession) request.getAttribute(FrameworkSessionFilter.ATTR_KEY);
    }

    public static FrameworkSession wrap(String sessionKey, String sessionId) {
        return new FrameworkSession(sessionKey, sessionId);
    }

    /**
     * 获取值
     *
     * @param key
     *
     * @return
     */
    public <T> T get(String key) {
        try {
            FrameworkCache cache = Springs.get(FrameworkCache.BEAN_NAME);
            if (cache != null && cache.getCache("session") != null) {
                Element element = cache.getCache("session").get(Strings.append(sessionId, ":", key));
                if (element != null) {
                    return (T) element.getObjectValue();
                }
            }
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
        return null;
    }

    /**
     * 设置值
     *
     * @param key
     * @param value
     */
    public void set(String key, Object value) {
        FrameworkCache cache = Springs.get(FrameworkCache.BEAN_NAME);
        if (cache != null && cache.getCache("session") != null) {
            cache.getCache("session").put(new Element(Strings.append(sessionId, ":", key), value));
        }
    }

    /**
     * 删除值
     *
     * @param key
     */
    public void remove(String key) {
        FrameworkCache cache = Springs.get(FrameworkCache.BEAN_NAME);
        if (cache != null && cache.getCache("session") != null) {
            cache.getCache("session").remove(Strings.append(sessionId, ":", key));
        }
    }

}
