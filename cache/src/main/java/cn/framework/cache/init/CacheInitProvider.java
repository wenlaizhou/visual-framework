/**
 * @项目名称: cache
 * @文件名称: CacheInitProvider.java
 * @Date: 2015年11月21日
 * @author: wenlai
 * @type: CacheInitProvider
 */
package cn.framework.cache.init;

import cn.framework.cache.pool.RedisPool;
import cn.framework.cache.session.FrameworkSessionManager;
import cn.framework.cache.session.FrameworkSessionValve;
import cn.framework.core.container.Context;
import cn.framework.core.container.InitProvider;
import cn.framework.core.utils.Arrays;
import cn.framework.core.utils.KVMap;
import cn.framework.core.utils.Reflects;
import cn.framework.core.utils.Strings;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import redis.clients.jedis.HostAndPort;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static cn.framework.core.utils.Xmls.*;

/**
 * @author wenlai
 */
@Deprecated
@Service("cacheInit")
public class CacheInitProvider implements InitProvider {

    /*
     * @see cn.framework.core.container.InitProvider#init(cn.framework.core.container.Context)
     */
    @Override
    public void init(final Context context) throws Exception {
        FilterDef sessionFD = new FilterDef();
        sessionFD.setFilterClass("cn.framework.cache.session.SessionFilter");
        sessionFD.setFilterName("session");
        FilterMap sessionMP = new FilterMap();
        sessionMP.setFilterName("session");
        sessionMP.addURLPattern("/*");
        context.getContext().addFilterDef(sessionFD);
        context.getContext().addFilterMap(sessionMP);
        Node cacheNode = xpathNode(".//cache", context.getConf());
        if (cacheNode == null) {
            return;
        }
        Node sessionNode = xpathNode(".//session", cacheNode);
        Node connectionsNode = xpathNode(".//connections", cacheNode);
        ArrayList<Node> redisNodeList = xpathNodesArray(".//redis", connectionsNode);
        if (Arrays.isNotNullOrEmpty(redisNodeList)) {
            for (Node node : redisNodeList) {
                KVMap conf = new KVMap();
                conf.addKV("id", attr("id", node));
                Node hosts = xpathNode(".//hosts", node);
                ArrayList<Node> hostList = null;
                if (hosts != null && (hostList = xpathNodesArray(".//host", hosts)) != null && hostList.size() > 0) {
                    conf.addKV("isCluster", true);
                    Set<HostAndPort> clusterHosts = new HashSet<>();
                    for (Node clusterNode : hostList)
                        clusterHosts.add(new HostAndPort(attr("name", clusterNode), Strings.parseInt(attr("port", clusterNode))));
                    conf.addKV("clusterHosts", clusterHosts);
                }
                else {
                    conf.addKV("isCluster", false);
                    conf.addKV("host", childAttribute("host", "name", node));
                    conf.addKV("port", childAttribute("host", "port", node));
                }
                conf.addKV("password", childTextContent("password", node), "");
                conf.addKV("db", childTextContent("db", node), 0);
                conf.addKV("name", "redis-pool");
                Node poolNode = xpathNode("//pool", node);
                int size = poolNode != null ? Strings.parseInt(childTextContent("size", poolNode, "100")) : 100;
                size = conf.getBoolean("isCluster", false) ? size / 8 + 1 : size;
                conf.addKV("size", size);
                if (poolNode != null) {
                    conf.addKV("checkPeriodSecond", childTextContent("ping-seconds", poolNode), "900");
                }
                else {
                    conf.addKV("checkPeriodSecond", "900");
                }
                RedisPool.createPool(conf);
            }
            // TODO 时刻测试cache是否可用，如不可用，切换到本地session
            if (sessionNode != null) {
                Reflects.setField("cn.framework.cache.session.SessionProvider", "CONN_ID", null, attr("connection", sessionNode, "session"));
                switch (attr("type", sessionNode, "local")) {
                    case "local":
                        Reflects.setField("cn.framework.cache.session.SessionProvider", "LOCAL", null, true);
                        break;
                    case "cache":
                        Reflects.setField("cn.framework.cache.session.SessionProvider", "LOCAL", null, false);
                        break;
                    default:
                        Reflects.setField("cn.framework.cache.session.SessionProvider", "LOCAL", null, true);
                        break;
                }
            }
        }
        else {
            Reflects.setField("cn.framework.cache.session.SessionProvider", "LOCAL", true, null);
        }
        // TODO expire seconds

        context.getContext().setManager(new FrameworkSessionManager());

        context.getContext().addValve(new FrameworkSessionValve());
    }
}
