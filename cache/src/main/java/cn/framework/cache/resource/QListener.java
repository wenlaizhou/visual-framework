package cn.framework.cache.resource;

import cn.framework.core.utils.Exceptions;
import cn.framework.core.utils.Springs;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.event.CacheEventListenerFactory;

import java.util.Properties;

/**
 * project code
 * package cn.framework.cache.resource
 * create at 16/4/6 上午11:17
 *
 * @author wenlai
 */
public class QListener extends CacheEventListenerFactory {


    @Override
    public CacheEventListener createCacheEventListener(Properties properties) {
        return new EventListener(properties);
    }

    /**
     * bus
     */
    public class EventListener implements CacheEventListener {

        private final Properties properties;

        public EventListener(Properties properties) {
            this.properties = properties;
        }

        @Override
        public void notifyElementRemoved(Ehcache cache, Element element) throws CacheException {

        }

        @Override
        public void notifyElementPut(Ehcache cache, Element element) throws CacheException {
            CacheQ q = Springs.get(CacheQ.BEAN_NAME);
            if (q != null) {
                if (element != null) {
                    if (element.getObjectValue() != null && element.getObjectValue() instanceof CachedEvent) {
                        CachedEvent event = (CachedEvent) element.getObjectValue();
                        Exceptions.logProcessor().logger().info("get message : {} {} {} {}", event.getId(), event.getFrom(), event.getTitle(), event.getMessage());
                        q.directPut(event);
                    }
                    else {
                        Exceptions.logProcessor().logger().error("message element is invalid {}", element);
                    }
                }
                else {
                    Exceptions.logProcessor().logger().error("message element is null");
                }
            }
            else {
                Exceptions.logProcessor().logger().error("cacheq is not available !");
            }
        }

        @Override
        public void notifyElementUpdated(Ehcache cache, Element element) throws CacheException {

        }

        @Override
        public void notifyElementExpired(Ehcache cache, Element element) {

        }

        @Override
        public void notifyElementEvicted(Ehcache cache, Element element) {

        }

        @Override
        public void notifyRemoveAll(Ehcache cache) {

        }


        @Override
        public void dispose() throws CacheException {

        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }
}
