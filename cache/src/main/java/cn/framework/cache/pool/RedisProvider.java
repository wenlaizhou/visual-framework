/**
 * @项目名称: cache
 * @文件名称: CacheProvider.java
 * @Date: 2015年11月21日
 * @author: wenlai
 * @type: CacheProvider
 */
package cn.framework.cache.pool;

import java.util.Map;
import com.alibaba.fastjson.JSON;
import cn.framework.core.log.LogProvider;
import cn.framework.core.utils.Strings;

/**
 * 缓存提供类
 * 
 * @author wenlai
 */
public final class RedisProvider {
    
    /**
     * 获取hash类型数据中的值
     * @param connectionId 连接id
     * @param key
     * @return
     */
    public static Map<String, String> hashGet(String connectionId, String key) {
        try (Redis redis = RedisPool.getHandler(connectionId);) {
            Map<String, String> result = redis.isCluster() ? redis.getClusterHandler().hgetAll(key) : redis.getHandler().hgetAll(key);
            return result;
        }
        catch (Exception e) {
            LogProvider.getFrameworkErrorLogger().error(e.getMessage(), e);
        }
        return null;
    }
    
    /**
     * 加加
     * 
     * @param connectionId 连接id
     * @param key key
     * @param value 增加的值
     */
    public static long incrementBy(String connectionId, String key, long value) {
        try (Redis redis = RedisPool.getHandler(connectionId);) {
            long result = redis.isCluster() ? redis.getClusterHandler().incrBy(key, value) : redis.getHandler().incrBy(key, value);
            LogProvider.getFrameworkInfoLogger().info(result);
            return result;
        }
        catch (Exception e) {
            LogProvider.getFrameworkErrorLogger().error(e.getMessage(), e);
        }
        return -1;
    }
    
    /**
     * hash类型数据加加
     * 
     * @param connectionId
     * @param key
     * @param field
     * @param value
     */
    public static long hashIncrementBy(String connectionId, String key, String field, long value) {
        try (Redis redis = RedisPool.getHandler(connectionId);) {
            return redis.isCluster() ? redis.getClusterHandler().hincrBy(key, field, value) : redis.getHandler().hincrBy(key, field, value);
        }
        catch (Exception e) {
            LogProvider.getFrameworkErrorLogger().error(e.getMessage(), e);
        }
        return -1;
    }
    
    /**
     * 减减
     * 
     * @param connectionId
     * @param key
     * @param value
     */
    public static long decrementBy(String connectionId, String key, long value) {
        try (Redis redis = RedisPool.getHandler(connectionId);) {
            return redis.isCluster() ? redis.getClusterHandler().decrBy(key, value) : redis.getHandler().decrBy(key, value);
        }
        catch (Exception e) {
            LogProvider.getFrameworkErrorLogger().error(e.getMessage(), e);
        }
        return -1;
    }
    
    /**
     * 设置
     * 
     * @param connectionId 连接id
     * @param key key值
     * @param value value值
     */
    public static void set(String connectionId, String key, String value) {
        try (Redis redis = RedisPool.getHandler(connectionId);) {
            if (redis.isCluster())
                redis.getClusterHandler().set(key, value);
            else
                redis.getHandler().set(key, value);
        }
        catch (Exception e) {
            LogProvider.getFrameworkErrorLogger().error(e.getMessage(), e);
        }
    }
    
    /**
     * 获取值
     * 
     * @param connectionId 连接id
     * @param key key值
     * @return
     */
    public static String get(String connectionId, String key) {
        try (Redis redis = RedisPool.getHandler(connectionId);) {
            return redis.isCluster() ? redis.getClusterHandler().get(key) : redis.getHandler().get(key);
        }
        catch (Exception e) {
            LogProvider.getFrameworkErrorLogger().error(e.getMessage(), e);
        }
        return Strings.EMPTY;
    }
    
    /**
     * 设置过期时间，单位秒
     * 
     * @param connectionId 连接id
     * @param key key值
     * @param expireSecond 过期秒数
     */
    public static void expire(String connectionId, String key, int expireSecond) {
        try (Redis redis = RedisPool.getHandler(connectionId);) {
            if (redis.isCluster())
                redis.getClusterHandler().expire(key, expireSecond);
            else
                redis.getHandler().expire(key, expireSecond);
        }
        catch (Exception e) {
            LogProvider.getFrameworkErrorLogger().error(e.getMessage(), e);
        }
    }
    
    /**
     * 设置哈希类型缓存值<br>
     * key : {
     * field : value, field : value ...
     * }
     * 
     * @param connectionId 连接id
     * @param key
     * @param field
     * @param value
     */
    public static void hashSet(String connectionId, String key, String field, Object value) {
        hashSet(connectionId, key, field, value, -1);
    }
    
    /**
     * 设置哈希类型缓存值<br>
     * key : {
     * field : value, field : value ...
     * }
     * 
     * @param connectionId 连接id
     * @param key key值
     * @param field 列值
     * @param value 设置的值
     * @param expireSecond 过期秒数
     */
    public static void hashSet(String connectionId, String key, String field, Object value, int expireSecond) {
        try (Redis redis = RedisPool.getHandler(connectionId);) {
            if (redis.isCluster())
                redis.getClusterHandler().hset(key, field, JSON.toJSONString(value));
            else
                redis.getHandler().hset(key, field, JSON.toJSONString(value));
            if (expireSecond > 0)
                if (redis.isCluster())
                    redis.getClusterHandler().expire(key, expireSecond);
                else
                    redis.getHandler().expire(key, expireSecond);
        }
        catch (Exception e) {
            LogProvider.getFrameworkErrorLogger().error(e.getMessage(), e);
        }
    }
    
    /**
     * 获取哈希类型缓存值
     * 
     * @param connectionId
     * @param key
     * @param field
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T hashGet(String connectionId, String key, String field) {
        try (Redis redis = RedisPool.getHandler(connectionId);) {
            return (T) JSON.parse(redis.isCluster() ? redis.getClusterHandler().hget(key, field) : redis.getHandler().hget(key, field));
        }
        catch (Exception e) {
            LogProvider.getFrameworkErrorLogger().error(e.getMessage(), e);
        }
        return null;
    }
    
    /**
     * 删除map中指定的field
     * 
     * @param connectionId
     * @param key
     * @param field
     */
    public static void hashDelete(String connectionId, String key, String field) {
        try (Redis redis = RedisPool.getHandler(connectionId);) {
            if (Strings.isNotNullOrEmpty(field)) {
                if (redis.isCluster())
                    redis.getClusterHandler().hdel(key, field);
                else
                    redis.getHandler().hdel(key, field);
            }
            else {
                if (redis.isCluster())
                    redis.getClusterHandler().del(key);
                else
                    redis.getHandler().del(key);
            }
        }
        catch (Exception e) {
            LogProvider.getFrameworkErrorLogger().error(e.getMessage(), e);
        }
    }
    
    /**
     * 删除key对应的所有值
     * 
     * @param connectionId
     * @param key
     */
    public static void delete(String connectionId, String key) {
        hashDelete(connectionId, key, Strings.EMPTY);
    }
    
    /**
     * 是否存在key
     * 
     * @param connectionId
     * @param key
     * @return
     */
    public static boolean exists(String connectionId, String key) {
        try (Redis redis = RedisPool.getHandler(connectionId);) {
            return redis.isCluster() ? redis.getClusterHandler().exists(key) : redis.getHandler().exists(key);
        }
        catch (Exception e) {
            LogProvider.getFrameworkErrorLogger().error(e.getMessage(), e);
        }
        return false;
    }
    
    /**
     * 根据表名和key名构造key值
     * 
     * @param table
     * @param key
     * @return
     */
    public static String buildKey(String table, String key) {
        return new StringBuilder(table).append(":").append(key).toString();
    }
}
