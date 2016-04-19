package cn.framework.cache.resource;

import cn.framework.cache.init.FrameworkCache;
import cn.framework.core.utils.Exceptions;
import cn.framework.core.utils.Springs;
import cn.framework.core.utils.Strings;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * project code
 * package cn.framework.cache.resource
 * create at 16/4/5 下午5:28
 *
 * @author wenlai
 */
public class CacheWrapper implements Closeable {

    /**
     * 分隔符
     */
    public static final String SEPERATOR = ":";

    /**
     * key
     */
    private final String key;

    /**
     * cache instance
     */
    private final Cache handler;

    /**
     * 字段集合
     */
    private volatile Set<String> fields = new HashSet<>();

    /**
     * constructor
     *
     * @param key
     */
    private CacheWrapper(String key) {
        Cache cache = null;
        try {
            FrameworkCache cacheHandler = Springs.get(FrameworkCache.BEAN_NAME);
            if (cacheHandler != null) {
                cache = cacheHandler.getCache("session");
            }
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
        finally {
            this.handler = cache;
        }
        this.key = key;
    }

    /**
     * create new instance
     *
     * @param key
     *
     * @return
     */
    public static final CacheWrapper wrap(String key) {
        CacheWrapper result = new CacheWrapper(key);
        if (result.handler.getQuiet(key) == null) {
            result.handler.put(new Element(key, ""));
        }
        return result;
    }

    /**
     * 添加attr
     *
     * @param field
     * @param value
     */
    public synchronized void add(String field, Object value) {
        try {
            Element element = new Element(Strings.append(this.key, SEPERATOR, field), value);
            this.handler.put(element);
            this.fields.add(field);
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
        finally {

        }
    }

    /**
     * delete attr
     *
     * @param field
     *
     * @return
     */
    public synchronized boolean delete(String field) {
        try {
            if (exist(field)) {
                return this.handler.remove(this.key + SEPERATOR + field);
            }
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
        return false;
    }

    /**
     * 获取字段对应的值
     *
     * @param field field
     *
     * @return
     */
    public Object select(String field) {
        try {
            if (exist(field)) {
                return this.handler.get(this.key + SEPERATOR + field).getObjectValue();
            }
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
        return null;
    }

    /**
     * 清除wrapper中全部缓存信息
     */
    public void removeAll() {
        try {
            if (this.handler.getQuiet(this.key) != null) {
                this.handler.remove(this.key);
            }
            this.fields.parallelStream().forEach(this::delete);
            this.fields.clear();
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
    }

    /**
     * 字段是否存在
     *
     * @param field field
     *
     * @return
     */
    public boolean exist(String field) {
        return this.fields.contains(field);
    }

    /**
     * close
     *
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        try {
            this.removeAll();
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
        finally {
            fields.clear();
        }
    }
}
