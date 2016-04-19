package cn.framework.core.zk;

import cn.framework.core.container.Context;
import cn.framework.core.container.InitProvider;
import cn.framework.core.utils.Exceptions;
import cn.framework.core.utils.Property;
import cn.framework.core.utils.Xmls;
import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;

import java.util.ArrayList;

/**
 * project code
 * package cn.framework.core.zk
 * create at 16/3/25 上午11:05
 *
 * @author wenlai
 */
@Service("zkInitor")
public class ZkInitProvider implements InitProvider {

    /**
     * zookeeper初始化信息
     *
     * @param context 配置上下文
     *
     * @throws Exception
     */
    @Override
    public void init(Context context) throws Exception {

        Node zookeeperNode = Xmls.xpathNode("//zookeeper", context.getConf());

        if (zookeeperNode == null) {
            return;
        }

        ArrayList<Node> properties = Xmls.xpathNodesArray(".//property", zookeeperNode);

        if (properties == null || properties.size() <= 0) {
            return;
        }


        //        Property
        //
        //
        //
        //        Properties properties = new Properties();
        //        properties.put("tickTime", 2000);
        //        properties.put("intLimit", 10);
        //
        //        properties.put("syncLimit", 5);
        //        properties.put("dataDir", Projects.WORK_DIR + "/zookeeper");
        //        properties.put("clientPort", 1234);
        //
        //        properties.put("maxClientCnxns", 60);

        /**
         * cluster :
         * tickTime=2000
         dataDir=/var/lib/zookeeper/
         clientPort=2181
         initLimit=5
         syncLimit=2
         server.1=zoo1:2888:3888
         server.2=zoo2:2888:3888
         server.3=zoo3:2888:3888
         You can find the meanings of these and other configuration settings in the section Configuration Parameters. A word though about a few here:
         Every machine that is part of the ZooKeeper ensemble should know about every other machine in the ensemble. You accomplish this with the series
         of lines of the form server.id=host:port:port. The parameters host and port are straightforward. You attribute the server id to each machine by
         creating a file named myid, one for each server, which resides in that server's data directory, as specified by the configuration file parameter dataDir.
         */

        /**
         *
         # The number of milliseconds of each tick
         tickTime=2000
         # The number of ticks that the initial
         # synchronization phase can take
         initLimit=10
         # The number of ticks that can pass between
         # sending a request and getting an acknowledgement
         syncLimit=5
         # the directory where the snapshot is stored.
         # do not use /tmp for storage, /tmp here is just
         # example sakes.
         dataDir=/tmp/zookeeper
         # the port at which the clients will connect
         clientPort=1281
         # the maximum number of client connections.
         # increase this if you need to handle more clients
         #maxClientCnxns=60
         #
         # Be sure to read the maintenance section of the
         # administrator guide before turning on autopurge.
         #
         # http://zookeeper.apache.org/doc/current/zookeeperAdmin.html#sc_maintenance
         #
         # The number of snapshots to retain in dataDir
         #autopurge.snapRetainCount=3
         # Purge task interval in hours
         # Set to "0" to disable auto purge feature
         #autopurge.purgeInterval=1
         */

        try {
            new Thread(() -> {
                try {
                    QuorumPeerConfig config = new QuorumPeerConfig();
                    config.parseProperties(Property.loadFromXmlNode(properties));
                    ZooKeeperServerMain main = new ZooKeeperServerMain();
                    ServerConfig serverConfig = new ServerConfig();
                    serverConfig.readFrom(config);
                    main.runFromConfig(serverConfig);
                }
                catch (Exception x) {
                    x.printStackTrace();
                }
            }, "zookeeper-thread").start();
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
    }
}
