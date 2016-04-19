/**
 * @项目名称: cache
 * @文件名称: RedisPool.java
 * @Date: 2015年11月21日
 * @author: wenlai
 * @type: RedisPool
 */
package cn.framework.cache.pool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import cn.framework.core.log.LogProvider;
import cn.framework.core.pool.Pool;
import cn.framework.core.utils.KVMap;

/**
 * @author wenlai
 *
 */
public class RedisPool extends Pool<Redis> {
    
    /**
     * 创建连接池
     * 
     * @param id
     * @param size
     * @param username
     * @param pwd
     * @param url
     * @param pingSecond
     * @throws Exception
     */
    public static void createPool(KVMap config) throws Exception {
        new RedisPool(config);
    }
    
    /**
     * @param config
     * @throws Exception
     */
    public RedisPool(KVMap config) throws Exception {
        super(config);
        poolContainer.put(id, this);
    }
    
    /*
     * @see cn.framework.core.pool.Pool#create()
     */
    @Override
    protected Redis create() {
        return new Redis(this.config);
    }
    
    /*
     * @see cn.framework.core.pool.Pool#activateObject(cn.framework.core.pool.PooledObject)
     */
    @Override
    protected void activateObject(Redis data) {
        data.activate();
    }
    
    /*
     * @see cn.framework.core.pool.Pool#isActive(cn.framework.core.pool.PooledObject)
     */
    @Override
    protected boolean isActive(Redis data) {
        return data.isActive();
    }
    
    /**
     * 获取缓存连接
     * 
     * @param id
     * @return
     */
    public static Redis getHandler(String id) {
        try {
            return poolContainer.get(id).get();
        }
        catch (Exception e) {
            LogProvider.getFrameworkErrorLogger().error(e.getMessage(), e);
        }
        return null;
    }
    
    /**
     * 获取缓存连接
     * 
     * @param id
     * @param timeoutMilisecond
     * @return
     */
    public static Redis getHandler(String id, int timeoutMilisecond) {
        try {
            return poolContainer.get(id).get(timeoutMilisecond);
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
    private static final Map<String, RedisPool> poolContainer = new ConcurrentHashMap<String, RedisPool>();
    
}
