package cn.framework.core.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * project code
 * package cn.framework.core.utils
 * create at 16/3/30 下午3:39
 *
 * @author wenlai
 */
public class ThreadSafeKVMap extends HashMap<String, Object> {

    /**
     * 锁信息
     */
    private transient ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

    /**
     * 批量插入
     *
     * @param kvs kv 对集合
     */
    public void put(KVPair<String, Object>... kvs) {
        try {
            if (lock.writeLock().tryLock() || lock.writeLock().tryLock(500, TimeUnit.MILLISECONDS)) {
                if (kvs != null && kvs.length > 0) {
                    for (KVPair<String, Object> kv : kvs) {
                        this.put(kv.key(), kv.value());
                    }
                }
            }
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
        finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public int size() {
        try {
            if (lock.readLock().tryLock() || lock.readLock().tryLock(500, TimeUnit.MILLISECONDS)) {
                return super.size();
            }
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
        finally {
            lock.readLock().unlock();
        }
        return super.size();
    }

    @Override
    public Object get(Object key) {
        try {
            if (lock.readLock().tryLock() || lock.readLock().tryLock(500, TimeUnit.MILLISECONDS)) {
                return super.get(key);
            }
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
        finally {
            lock.readLock().unlock();
        }
        return super.get(key);
    }

    @Override
    public Object getOrDefault(Object key, Object defaultValue) {
        try {
            if (lock.readLock().tryLock() || lock.readLock().tryLock(500, TimeUnit.MILLISECONDS)) {
                return super.getOrDefault(key, defaultValue);
            }
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
        finally {
            lock.readLock().unlock();
        }
        return super.getOrDefault(key, defaultValue);

    }

    @Override
    public boolean containsKey(Object key) {
        try {
            if (lock.readLock().tryLock() || lock.readLock().tryLock(500, TimeUnit.MILLISECONDS)) {
                return super.containsKey(key);
            }
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
        finally {
            lock.readLock().unlock();
        }
        return super.containsKey(key);
    }

    @Override
    public Object put(String key, Object value) {
        try {
            if (lock.writeLock().tryLock() || lock.writeLock().tryLock(500, TimeUnit.MILLISECONDS)) {
                return super.put(key, value);
            }
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
        finally {
            lock.writeLock().unlock();
        }
        return super.put(key, value);
    }

    @Override
    public void putAll(Map<? extends String, ?> map) {
        try {
            if (lock.writeLock().tryLock() || lock.writeLock().tryLock(500, TimeUnit.MILLISECONDS)) {
                super.putAll(map);
                return;
            }
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
        finally {
            lock.writeLock().unlock();
        }
        super.putAll(map);
    }

    @Override
    public Object remove(Object key) {
        try {
            if (lock.writeLock().tryLock() || lock.writeLock().tryLock(500, TimeUnit.MILLISECONDS)) {
                return super.remove(key);
            }
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
        finally {
            lock.writeLock().unlock();
        }
        return super.remove(key);
    }

    @Override
    public void clear() {
        try {
            if (lock.writeLock().tryLock() || lock.writeLock().tryLock(500, TimeUnit.MILLISECONDS)) {
                super.clear();
                return;
            }
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
        finally {
            lock.writeLock().unlock();
        }
        super.clear();
    }

    @Override
    public boolean containsValue(Object value) {
        try {
            if (lock.readLock().tryLock() || lock.readLock().tryLock(500, TimeUnit.MILLISECONDS)) {
                return super.containsValue(value);
            }
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
        finally {
            lock.readLock().unlock();
        }
        return super.containsValue(value);
    }

    @Override
    public Object putIfAbsent(String key, Object value) {
        try {
            if (lock.writeLock().tryLock() || lock.writeLock().tryLock(500, TimeUnit.MILLISECONDS)) {
                return super.putIfAbsent(key, value);
            }
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
        finally {
            lock.writeLock().unlock();
        }
        return super.putIfAbsent(key, value);
    }
}
