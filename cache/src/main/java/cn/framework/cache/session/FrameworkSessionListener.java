package cn.framework.cache.session;

import cn.framework.core.utils.Exceptions;
import cn.framework.core.utils.Springs;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.event.CacheEventListenerFactory;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

/**
 * project code
 * package cn.framework.cache.session
 * create at 16/4/15 下午5:26
 * <p>
 * session cache event listener
 *
 * @author wenlai
 */
public class FrameworkSessionListener extends CacheEventListenerFactory {

    /**
     * 提供监听者
     *
     * @param properties property
     *
     * @return
     */
    @Override
    public CacheEventListener createCacheEventListener(Properties properties) {
        return new Listener();
    }

    /**
     * session listener
     */
    public static class Listener implements CacheEventListener {

        private Logger logger = Exceptions.logProcessor().logger();

        private FrameworkSessionManager handler;

        public Listener() {
            this.handler = Springs.get(FrameworkSessionManager.BEAN_NAME);
        }

        @Override
        public void notifyElementRemoved(Ehcache cache, Element element) throws CacheException {
            logger.info("session cache : {} remove {}", cache, element);
            if (this.handler != null) {
                this.handler.remove(element.getObjectKey().toString());
            }
        }

        @Override
        public void notifyElementPut(Ehcache cache, Element element) throws CacheException {
            logger.info("session cache : {} put {}", cache, element);
            updateSession(element);
        }


        @Override
        public void notifyElementUpdated(Ehcache cache, Element element) throws CacheException {
            logger.info("session cache : {} update {}", cache, element);
            updateSession(element);
        }

        @Override
        public void notifyElementExpired(Ehcache cache, Element element) {
            logger.info("session cache : {} expire {}", cache, element);
            FrameworkSessionManager.CACHE.invalidate(element.getObjectKey().toString());
        }

        @Override
        public void notifyElementEvicted(Ehcache cache, Element element) {
            logger.info("session cache : {} evicted {}", cache, element);
            FrameworkSessionManager.CACHE.invalidate(element.getObjectKey().toString());
        }

        @Override
        public void notifyRemoveAll(Ehcache cache) {
            logger.info("session cache : {} remove all", cache);
        }

        @Override
        public void dispose() {

        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        /**
         * 元素更新,同步更新session
         *
         * @param element element
         */
        private void updateSession(Element element) {
            if (element.getObjectKey().toString().contains(":")) {
                return;
            }
            try {
                String key = element.getObjectKey().toString();
                if (this.handler.findSession(element.getObjectKey().toString()) == null) {
                    if (FrameworkSessionManager.CACHE.getIfPresent(element.getObjectKey()) == null) {
                        this.handler.createSession(key);
                    }
                }
            }
            catch (Exception x) {
                Exceptions.processException(x);
            }
        }
    }
}
