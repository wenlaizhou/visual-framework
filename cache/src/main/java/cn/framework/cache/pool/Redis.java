/**
 * @项目名称: cache
 * @文件名称: Redis.java
 * @Date: 2015年11月21日
 * @author: wenlai
 * @type: Redis
 */
package cn.framework.cache.pool;

import java.util.Set;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import cn.framework.core.log.LogProvider;
import cn.framework.core.pool.PooledObject;
import cn.framework.core.utils.KVMap;
import cn.framework.core.utils.Strings;

/**
 * 内部使用
 * 
 * @author wenlai
 */
class Redis extends PooledObject {
    
    /**
     * 构建redis实体
     * 
     * @param config {host, port, password, db}
     */
    public Redis(KVMap config) {
        this.isCluster = config.getBoolean("isCluster", false);
        this.host = config.getString("host");
        this.port = config.getInt("port");
        this.password = config.getString("password");
        this.db = config.getInt("db");
        if (this.isCluster)
            this.clusterHosts = config.get("clusterHosts");
        buildHandler();
    }
    
    /**
     * 创建handler实例
     */
    private void buildHandler() {
        if (this.isCluster) {
            this.clusterHandler = new JedisCluster(this.clusterHosts);
        }
        else {
            this.handler = new Jedis(this.host, this.port);
            this.handler.connect();
            this.handler.getClient().setDb(db);
            if (Strings.isNotNullOrEmpty(this.password))
                this.handler.auth(this.password);
        }
    }
    
    /**
     * 激活元素
     */
    public void activate() {
        try {
            closeHandler();
            buildHandler();
            if (!this.isCluster) {
                if (this.handler.getDB() != this.db)
                    this.handler.select(this.db);
            }
        }
        catch (final Exception e) {
            LogProvider.getFrameworkErrorLogger().error(e.getMessage(), e);
        }
    }
    
    /**
     * 关闭句柄
     */
    private void closeHandler() {
        try {
            if (this.isCluster) {
                this.clusterHandler.close();
            }
            else {
                if (this.handler.isConnected())
                    this.handler.close();
            }
        }
        catch (final Exception x) {
            LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
        }
    }
    
    /**
     * 判断元素是否可用
     * 
     * @return
     */
    public boolean isActive() {
        try {
            if (this.isCluster) {
                String response = this.clusterHandler.ping();
                if (response != null && response.equals("PONG"))
                    return true;
            }
            else {
                if (this.handler.isConnected()) {
                    String response = this.handler.ping();
                    if (response != null && response.equals("PONG"))
                        return true;
                }
            }
        }
        catch (Exception x) {
            LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
        }
        return false;
    }
    
    /**
     * 获取handler
     * 
     * @return
     */
    public final Jedis getHandler() {
        return this.handler;
    }
    
    /**
     * 获取handler
     * 
     * @return
     */
    public final JedisCluster getClusterHandler() {
        return this.clusterHandler;
    }
    
    /**
     * 是否是cluster
     * 
     * @return
     */
    public boolean isCluster() {
        return this.isCluster;
    }
    
    /**
     * 设置db值
     * 
     * @param dbIndex
     */
    public void setDb(int dbIndex) {
        this.db = dbIndex;
        this.handler.getClient().setDb(dbIndex);
    }
    
    /**
     * 获取db值
     * 
     * @return
     */
    public int getDb() {
        return this.db;
    }
    
    /**
     * 是否是集群
     */
    private boolean isCluster = false;
    
    /**
     * 操作句柄
     */
    private Jedis handler = null;
    
    /**
     * 操作句柄
     */
    private JedisCluster clusterHandler;
    
    /**
     * 服务ip
     */
    private String host;
    
    private Set<HostAndPort> clusterHosts = null;
    
    /**
     * 端口
     */
    private int port;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * db
     */
    private int db;
}
