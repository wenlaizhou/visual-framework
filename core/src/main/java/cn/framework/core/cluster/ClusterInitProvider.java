package cn.framework.core.cluster;

import cn.framework.core.container.Context;
import cn.framework.core.container.InitProvider;
import cn.framework.core.utils.Exceptions;
import cn.framework.core.utils.Strings;
import cn.framework.core.utils.Xmls;
import com.google.common.net.HostAndPort;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static cn.framework.core.utils.Pair.newPair;

/**
 * project code
 * package cn.framework.core.cluster
 * create at 16/3/25 下午1:33
 *
 * @author wenlai
 */
@Service(ClusterInitProvider.BEAN_NAME)
public class ClusterInitProvider implements InitProvider {

    /**
     *
     */
    public static final String BEAN_NAME = "clusterInit";

    /**
     * cluster信息
     */
    public static final String TABLE_CLUSTER = "<h4>cluster-info:</h4><table class=\"table table-striped\"><thead><tr><th>name</th><th>host</th><th>link</th></tr></thead><tbody><content></content></tbody></table>";

    /**
     *
     */
    public static final String TR = "<tr><td>${name}</td><td>${host}</td><td><a href=\"${link}\">go</a></td></tr>";

    /**
     *
     */
    private volatile static boolean INITED = false;

    /**
     *
     */
    public boolean cluster = false;

    /**
     *
     */
    public Map<String, HostAndPort> nodes = new HashMap<>();

    /**
     *
     */
    public String tableHtml = TABLE_CLUSTER.replace("<content></content>", "");

    @Override
    public synchronized void init(Context context) throws Exception {
        try {
            if (!INITED) {
                //                ThreadPool.addTask(Task.wrap("cluster-running", () -> {
                //                    try {
                //                        IoAcceptor clusterServer = new NioSocketAcceptor();
                //                        LoggingFilter logger = new LoggingFilter("cluster.framework");
                //                        clusterServer.getFilterChain().addLast("logging", logger);
                //                        clusterServer.getFilterChain().addLast("text", new ProtocolCodecFilter(new TextLineCodecFactory(Charsets.UTF_8)));
                //                        clusterServer.setHandler(Springs.get("clusterHandler"));
                //                        clusterServer.getSessionConfig().setReadBufferSize(2048);
                //                        clusterServer.getSessionConfig().setReadBufferSize(2048);
                //                        clusterServer.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
                //                        clusterServer.bind(new InetSocketAddress(1024));
                //                    }
                //                    catch (Exception x) {
                //                        Exceptions.processException(x);
                //                    }
                //                }));
                Node clusterNode = Xmls.xpathNode("//framework-cluster", context.getConf());
                if (clusterNode == null) {
                    return;
                }
                ArrayList<Node> nodeList = Xmls.xpathNodesArray(".//node", clusterNode);
                if (nodeList != null && nodeList.size() > 0) {
                    this.cluster = true;
                    nodeList.forEach(node -> {
                        this.nodes.put(Xmls.attr("name", node), HostAndPort.fromString(Xmls.attr("host", node)));
                    });
                }
                if (this.cluster) {
                    StringBuilder builder = new StringBuilder();
                    this.nodes.forEach((name, host) -> {
                        String hostPortStr = Strings.append(host.getHostText(), ":", host.getPort());
                        builder.append(Strings.format(TR, newPair("name", name), newPair("host", hostPortStr), newPair("link", hostPortStr.startsWith("http://") ? hostPortStr : Strings.append("http://", hostPortStr))));
                    });
                    this.tableHtml = TABLE_CLUSTER.replace("<content></content>", builder);
                }
            }
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
        finally {
            INITED = true;
        }

    }
}
