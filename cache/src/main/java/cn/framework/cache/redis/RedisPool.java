package cn.framework.cache.redis;

import cn.framework.core.container.Context;
import cn.framework.core.container.InitProvider;
import cn.framework.core.utils.Exceptions;
import cn.framework.core.utils.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static cn.framework.core.utils.Xmls.*;

/**
 * project code
 * package cn.framework.cache.redis
 * create at 16/4/8 下午7:20
 *
 * @author wenlai
 */
@Service(RedisPool.BEAN_NAME)
public class RedisPool implements InitProvider {

    /**
     * bean name
     */
    public static final String BEAN_NAME = "redisPool";

    /**
     * 自动资源回收器
     */
    private static Cache<String, Jedis> JEDIS_CACHE = CacheBuilder.newBuilder().expireAfterAccess(60, TimeUnit.SECONDS).removalListener(notification -> {
        Exceptions.logProcessor().logger().info("开始回收资源", notification.getKey());
        Jedis connection = (Jedis) notification.getValue();
        if (connection != null) {
            try {
                connection.close();
            }
            catch (Exception x) {
                Exceptions.processException(x);
            }
        }
    }).build();

    /**
     * 是否已经初始化过
     */
    private static volatile boolean INITED = false;

    /**
     * 连接
     */
    private Map<String, JedisPool> connectionHandlers = new HashMap<>();

    /**
     * 集群连接
     */
    private Map<String, JedisCluster> clusterHandlers = new HashMap<>();

    /**
     * 初始化
     *
     * @param context 配置上下文
     *
     * @throws Exception
     */
    @Override
    public synchronized void init(Context context) throws Exception {
        if (INITED) {
            return;
        }
        try {
            Node cache = xpathNode("//cache", context.getConf());
            if (cache == null) {
                return;
            }
            ArrayList<Node> redisNodes = xpathNodesArray(".//redis", cache);
            if (redisNodes == null || redisNodes.size() <= 0) {
                return;
            }
            redisNodes.parallelStream().forEach(redisNode -> {
                Node hostsNode = xpathNode(".//hosts", redisNode);
                if (hostsNode != null) {
                    ArrayList<Node> hosts = xpathNodesArray(".//host", hostsNode);
                    if (hosts != null && hosts.size() > 0) {
                        Set<HostAndPort> nodeAddresses = new HashSet<>();
                        hosts.forEach(host -> {
                            nodeAddresses.add(new HostAndPort(attr("name", host), Strings.parseInt(attr("port", host))));
                        });
                        JedisCluster cluster = new JedisCluster(nodeAddresses, Strings.parseInt(attr("timeoutSeconds", redisNode, "3")), buildPoolConf(xpathNode(".//pool", redisNode)));
                        this.clusterHandlers.put(attr("id", redisNode), cluster);
                    }
                }
                else {
                    Node hostNode = xpathNode(".//host", redisNode);
                    Node pwdNode = xpathNode(".//password", redisNode);
                    Node dbNode = xpathNode(".//database", redisNode);
                    JedisPool jedisPool = new JedisPool(buildPoolConf(xpathNode(".//pool", redisNode)), attr("name", hostNode), Strings.parseInt(attr("port", hostNode)), Strings.parseInt(attr("timeoutSeconds", redisNode, "3")));
                    if (pwdNode != null) {
                        jedisPool = (dbNode == null) ? new JedisPool(buildPoolConf(xpathNode(".//pool", redisNode)), attr("name", hostNode), Strings.parseInt(attr("port", hostNode)), Strings.parseInt(attr("timeoutSeconds", redisNode, "3")), pwdNode.getTextContent().trim()) : new JedisPool(buildPoolConf(xpathNode(".//pool", redisNode)), attr("name", hostNode), Strings.parseInt(attr("port", hostNode)), Strings.parseInt(attr("timeoutSeconds", redisNode, "3")), pwdNode.getTextContent().trim(), Strings.parseInt(dbNode.getTextContent().trim(), 0));
                    }
                    else {
                        jedisPool = new JedisPool(buildPoolConf(xpathNode(".//pool", redisNode)), attr("name", hostNode), Strings.parseInt(attr("port", hostNode)), Strings.parseInt(attr("timeoutSeconds", redisNode, "3")));
                    }
                    this.connectionHandlers.put(attr("id", redisNode), jedisPool);
                }
            });
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
        finally {
            INITED = true;
        }
    }

    /**
     * 根据节点返回池配置
     *
     * @param poolNode 节点
     *
     * @return
     */
    private GenericObjectPoolConfig buildPoolConf(Node poolNode) {
        if (poolNode == null) {
            return this.defaultPoolConf();
        }
        GenericObjectPoolConfig result = new GenericObjectPoolConfig();
        result.setMaxTotal(Strings.parseInt(childTextContent("max", poolNode, "30")));
        result.setMinIdle(Strings.parseInt(childTextContent("min", poolNode, "3")));
        result.setMaxWaitMillis(Strings.parseInt(childTextContent("maxWaitMillis", poolNode, "1000")));
        result.setFairness(Strings.parseBool(childTextContent("fair", poolNode, "true")));
        return result;
    }

    /**
     * 返回默认池配置
     *
     * @return
     */
    private GenericObjectPoolConfig defaultPoolConf() {
        GenericObjectPoolConfig result = new GenericObjectPoolConfig();
        result.setMaxTotal(30);
        result.setMinIdle(3);
        result.setFairness(true);
        result.setMaxWaitMillis(2000);
        return result;
    }

    /**
     * 获取单点连接
     *
     * @param connection redis节点中的id
     *
     * @return null if not config right
     */
    public Jedis getConnection(String connection) {
        JedisPool pool = this.connectionHandlers.get(connection);
        if (pool == null) {
            Exceptions.processException(Strings.append("redis : ", connection, " is not found!"), new NullPointerException());
            return null;
        }
        Jedis conn = pool.getResource();
        JEDIS_CACHE.put(Strings.append(connection, new Date().toString()), conn);
        return conn;
    }

    /**
     * 获取集群连接
     *
     * @param connection redis节点中的id
     *
     * @return null if not config right
     */
    public JedisCluster getClusterConnection(String connection) {
        JedisCluster pool = this.clusterHandlers.get(connection);
        if (pool == null) {
            Exceptions.processException(Strings.append("redis : ", connection, " is not found!"), new NullPointerException());
            return null;
        }
        return pool;
    }

    /**
     * @return
     */
    public ArrayList<PoolCollection> getMoniterData() {
        ArrayList<PoolCollection> result = new ArrayList<>();

        for (String connName : connectionHandlers.keySet()) {
            PoolCollection info = new PoolCollection();
            info.connection = connName;
            info.maxBorrowWaitTimeMillis = this.connectionHandlers.get(connName).getMaxBorrowWaitTimeMillis();
            info.meanBorrowWaitTimeMillis = this.connectionHandlers.get(connName).getMeanBorrowWaitTimeMillis();
            info.numActive = this.connectionHandlers.get(connName).getNumActive();
            info.numIdle = this.connectionHandlers.get(connName).getNumIdle();
            info.numWaiters = this.connectionHandlers.get(connName).getNumWaiters();
            result.add(info);
        }

        for (String connName : clusterHandlers.keySet()) {
            Map<String, JedisPool> clusterNodes = clusterHandlers.get(connName).getClusterNodes();
            for (String key : clusterNodes.keySet()) {
                PoolCollection info = new PoolCollection();
                info.connection = Strings.append(connName, ":", key);
                info.maxBorrowWaitTimeMillis = clusterNodes.get(key).getMaxBorrowWaitTimeMillis();
                info.meanBorrowWaitTimeMillis = clusterNodes.get(key).getMeanBorrowWaitTimeMillis();
                info.numActive = clusterNodes.get(key).getNumActive();
                info.numIdle = clusterNodes.get(key).getNumIdle();
                info.numWaiters = clusterNodes.get(key).getNumWaiters();
                result.add(info);
            }
        }

        return result;
    }

    /**
     * redis-pool monitor data
     */
    public static class PoolCollection {

        /**
         * connection id
         */
        public String connection;

        /**
         * maxBorrowWaitTimeMillis
         */
        public long maxBorrowWaitTimeMillis;

        /**
         * meanBorrowWaitTimeMillis
         */
        public long meanBorrowWaitTimeMillis;

        /**
         * numActive
         */
        public int numActive;

        /**
         * numIdle
         */
        public int numIdle;

        /**
         * numWaiters
         */
        public int numWaiters;
    }


}
