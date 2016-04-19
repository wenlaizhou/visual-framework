package cn.framework.cache.session;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.event.CacheEventListenerFactory;

import java.util.Properties;

/**
 * project code
 * package cn.framework.cache.session
 * create at 16/4/5 下午5:48
 *
 * @author wenlai
 */
public class CacheListener extends CacheEventListenerFactory {

    @Override
    public CacheEventListener createCacheEventListener(Properties properties) {
        System.out.println(properties);
        return new StoreCacheListener();
    }

    /**
     *
     */
    public class StoreCacheListener implements CacheEventListener {

        @Override
        public void notifyElementRemoved(Ehcache cache, Element element) throws CacheException {
            System.out.println(String.format("cache remove : %s", element.getObjectKey()));
        }

        @Override
        public void notifyElementPut(Ehcache cache, Element element) throws CacheException {
            System.out.println(String.format("cache put : %s", element.getObjectKey()));
        }

        @Override
        public void notifyElementUpdated(Ehcache cache, Element element) throws CacheException {
            System.out.println(String.format("cache update : %s", element.getObjectKey()));
        }

        @Override
        public void notifyElementExpired(Ehcache cache, Element element) {
            System.out.println(String.format("cache expire : %s", element.getObjectKey()));
        }

        @Override
        public void notifyElementEvicted(Ehcache cache, Element element) {
            System.out.println(String.format("cache evicted : %s", element.getObjectKey()));
        }

        @Override
        public void notifyRemoveAll(Ehcache cache) {
            System.out.println(String.format("cache remove all : %s", cache.getName()));
        }

        @Override
        public void dispose() {

        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }
}
