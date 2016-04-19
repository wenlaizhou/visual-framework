/**
 * @项目名称: cache
 * @文件名称: SessionProvider.java
 * @Date: 2015年12月8日
 * @author: wenlai
 * @type: SessionProvider
 */
package cn.framework.cache.session;

import cn.framework.core.utils.Arrays;
import cn.framework.core.utils.Strings;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.UUID;

import static cn.framework.cache.pool.RedisProvider.*;

/**
 * TODO 添加过期时间
 *
 * @author wenlai
 */
public class SessionProvider {

    /**
     * from redis-session by github
     <Valve className="com.orangefunction.tomcat.redissessions.RedisSessionHandlerValve" />
     <Manager className="com.orangefunction.tomcat.redissessions.RedisSessionManager"
     host="localhost" <!-- optional: defaults to "localhost" -->
     port="6379" <!-- optional: defaults to "6379" -->
     database="0" <!-- optional: defaults to "0" -->
     maxInactiveInterval="60" <!-- optional: defaults to "60" (in seconds) -->
     sessionPersistPolicies="PERSIST_POLICY_1,PERSIST_POLICY_2,.." <!-- optional -->
     sentinelMaster="SentinelMasterName" <!-- optional -->
     sentinels="sentinel-host-1:port,sentinel-host-2:port,.." <!-- optional --> />
     The Valve must be declared before the Manager.
     1 add valve
     1 add manager
     */

    /**
     * SESSION_ID_KEY
     */
    private final static String SESSION_ID_KEY = "framework_sessionid";
    /**
     * cache连接id
     */
    private final static String CONN_ID = "session";
    /**
     * cache对应key表名
     */
    private final static String R_TABLE = "session";
    /**
     * userid对应的field名称
     */
    private final static String USER_FIELD = "username";
    /**
     * 是否启用本地session
     */
    private volatile static boolean LOCAL = true;

    /**
     * 设置值
     *
     * @param key
     * @param value
     * @param req
     *
     * @return
     */
    public static void set(String key, Object value, HttpServletRequest req) {
        if (LOCAL) {
            req.getSession(true).setAttribute(key, value);
            return;
        }
        hashSet(CONN_ID, buildKey(R_TABLE, getSessionId(req)), key, value);
    }

    /**
     * 登出
     *
     * @param req
     */
    public static void logout(HttpServletRequest req) {
        if (isLogon(req)) {
            if (LOCAL) {
                req.getSession(true).setAttribute(USER_FIELD, null);
                return;
            }
            delete(CONN_ID, buildKey(R_TABLE, getSessionId(req)));
        }
    }

    /**
     * 获取值
     *
     * @param key
     * @param req
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(String key, HttpServletRequest req) {
        if (LOCAL) {
            return (T) req.getSession(true).getAttribute(key);
        }
        return hashGet(CONN_ID, buildKey(R_TABLE, getSessionId(req)), key);
    }

    /**
     * 登录
     *
     * @param req
     *
     * @return
     */
    public static void login(String username, HttpServletRequest req) {
        if (LOCAL) {
            req.getSession(true).setAttribute(USER_FIELD, username);
            return;
        }
        String sessionId = getSessionId(req);
        hashSet(CONN_ID, buildKey(R_TABLE, sessionId), USER_FIELD, username);
    }

    /**
     * 获取username
     *
     * @param req
     *
     * @return
     */
    public static String getUsername(HttpServletRequest req) {
        if (LOCAL) {
            Object username = req.getSession(true).getAttribute(USER_FIELD);
            return username == null ? Strings.EMPTY : username.toString();
        }
        String sessionId = getSessionId(req);
        return hashGet(CONN_ID, buildKey(R_TABLE, sessionId), USER_FIELD);
    }

    /**
     * 判断是否登录
     *
     * @param req
     *
     * @return
     */
    public static boolean isLogon(HttpServletRequest req) {
        if (LOCAL) {
            return req.getSession(true).getAttribute(USER_FIELD) != null;
        }
        String sessionId = getSessionId(req);
        if (Strings.isNullOrEmpty(sessionId)) {
            return false;
        }
        if (!exists(CONN_ID, buildKey(R_TABLE, sessionId))) {
            return false;
        }
        String userId = hashGet(CONN_ID, buildKey(R_TABLE, sessionId), USER_FIELD);
        if (Strings.isNullOrEmpty(userId)) {
            return false;
        }
        return true;
    }

    /**
     * 从cookie中获取sessionid
     *
     * @param req
     *
     * @return
     */
    public static String getSessionId(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if (Arrays.isNotNullOrEmpty(cookies)) {
            for (Cookie cookie : cookies)
                if (cookie.getName().equals(SESSION_ID_KEY)/* && Strings.isNotNullOrEmpty(cookie.getPath()) && cookie.getPath().equals("/") */) {
                    return cookie.getValue();
                }
        }
        Object sessionId = req.getAttribute(SESSION_ID_KEY);
        return sessionId != null ? sessionId.toString() : Strings.EMPTY;
    }

    /**
     * 生成sessionid
     *
     * @return
     */
    static String generateSessionId(HttpServletRequest req, HttpServletResponse rep) {
        String sessionId = String.format("%1$s%2$s", new Date().getTime(), UUID.randomUUID().toString().toUpperCase());
        if (rep != null) {
            Cookie session = new Cookie(SESSION_ID_KEY, sessionId);
            session.setPath("/");
            session.setMaxAge(60 * 60 * 24 * 365);
            rep.addCookie(session);
            req.setAttribute(SESSION_ID_KEY, sessionId);
        }
        return sessionId;
    }

}
