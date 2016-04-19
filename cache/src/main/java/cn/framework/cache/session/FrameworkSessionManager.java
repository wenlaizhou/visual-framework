package cn.framework.cache.session;

import cn.framework.cache.init.FrameworkCache;
import cn.framework.core.utils.Exceptions;
import cn.framework.core.utils.Property;
import cn.framework.core.utils.Springs;
import cn.framework.core.utils.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Session;
import org.apache.catalina.Valve;
import org.apache.catalina.session.StandardManager;
import org.apache.catalina.session.StandardSession;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * project code
 * package cn.framework.cache.session
 * create at 16/3/30 上午10:38
 *
 * @author wenlai
 */
@Service(FrameworkSessionManager.BEAN_NAME)
public class FrameworkSessionManager extends StandardManager {

    public static final String BEAN_NAME = "frameworkSessionManager";

    /**
     * logger
     */
    private static Logger logger = Exceptions.logProcessor().logger();

    /**
     * local cache
     */
    public static final Cache<String, FrameworkSession> CACHE = CacheBuilder.newBuilder().removalListener(notification -> {
        logger.info("session remove cause : {}", notification.getCause());
        logger.info("session key : {}", notification.getKey());
        logger.info("session value : {}", notification.getValue());
    }).expireAfterAccess(Strings.parseInt(Property.get("session.expireSeconds", "3600")), TimeUnit.SECONDS).build();

    /**
     * value handler
     */
    public FrameworkSessionValve valveHandler;

    /**
     * load
     *
     * @throws ClassNotFoundException
     * @throws IOException
     */
    @Override
    public void load() throws ClassNotFoundException, IOException {
        FrameworkCache handler = Springs.get(FrameworkCache.BEAN_NAME);
        if (handler != null) {
            net.sf.ehcache.Cache session = handler.getCache("session");
            if (session != null) {
                session.getKeysWithExpiryCheck().parallelStream().forEach(k -> {
                    try {
                        String key = k.toString();
                        if (!key.contains(":")) {
                            CACHE.put(key, new FrameworkSession(this, key));
                        }
                    }
                    catch (Exception x) {
                        Exceptions.processException(x);
                    }
                });
            }
        }
    }

    /**
     * start
     *
     * @throws LifecycleException
     */
    @Override
    protected void startInternal() throws LifecycleException {
        super.startInternal();
        for (Valve v : this.getContext().getPipeline().getValves()) {
            if (v instanceof FrameworkSessionValve) {
                this.valveHandler = (FrameworkSessionValve) v;
                this.valveHandler.setSessionManager(this);
                break;
            }
        }
        setDistributable(true);
    }

    /**
     * unload
     *
     * @throws IOException
     */
    @Override
    public void unload() throws IOException {

    }

    /**
     * create session
     *
     * @param sessionId sessionId
     *
     * @return
     */
    @Override
    public Session createSession(String sessionId) {
        sessionId = Strings.isNotNullOrEmpty(sessionId) ? sessionId : this.generateSessionId();
        FrameworkSession session = new FrameworkSession(this, sessionId);
        CACHE.put(session.getId(), session);
        return session;
    }

    @Override
    protected StandardSession getNewSession() {
        FrameworkSession session = new FrameworkSession(this, this.generateSessionId());
        CACHE.put(session.getId(), session);
        return session;
    }

    /**
     * add session
     *
     * @param session session
     */
    @Override
    public void add(Session session) {
        if (session != null) {
            if (session instanceof FrameworkSession) {
                FrameworkSession superSession = (FrameworkSession) session;
                CACHE.put(session.getId(), superSession);
            }
            else {
                this.commonAdd(session);
            }
        }
    }

    /**
     * add common session
     *
     * @param session
     */
    public void commonAdd(Session session) {
        super.add(session);
    }

    /**
     * find session by session id
     *
     * @param id sessionId
     *
     * @return
     *
     * @throws IOException
     */
    @Override
    public Session findSession(String id) throws IOException {
        return CACHE.getIfPresent(id);
    }

    /**
     * remove session
     *
     * @param session
     */
    @Override
    public void remove(Session session) {
        if (session == null) {
            return;
        }
        if (session instanceof FrameworkSession) {
            FrameworkSession realSession = (FrameworkSession) session;
            realSession.removeSelf();
            CACHE.invalidate(session.getId());
        }
        else {
            super.remove(session);
        }
    }

    /**
     * 删除session
     *
     * @param sessionId
     */
    public void remove(String sessionId) {
        FrameworkSession session = CACHE.getIfPresent(sessionId);
        if (session != null) {
            session.removeSelf();
            CACHE.invalidate(sessionId);
        }
    }

    /**
     * remove session and update
     *
     * @param session
     * @param update
     */
    @Override
    public void remove(Session session, boolean update) {
        if (session == null) {
            return;
        }
        if (session instanceof FrameworkSession) {
            FrameworkSession realSession = (FrameworkSession) session;
            realSession.removeSelf();
            CACHE.invalidate(session.getId());
        }
        else {
            super.remove(session, update);
        }
    }

    /**
     * create new session
     *
     * @return
     */
    @Override
    public Session createEmptySession() {
        FrameworkSession session = new FrameworkSession(this, this.generateSessionId());
        CACHE.put(session.getId(), session);
        return session;
    }
}
