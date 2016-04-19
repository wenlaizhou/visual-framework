/**
 * @项目名称: db
 * @文件名称: DbInitProvider.java
 * @Date: 2015年11月18日
 * @author: wenlai
 * @type: DbInitProvider
 */
package cn.framework.db.init;

import cn.framework.core.container.Context;
import cn.framework.core.container.InitProvider;
import cn.framework.core.log.LogProvider;
import cn.framework.core.utils.Exceptions;
import cn.framework.core.utils.KVMap;
import cn.framework.db.pool.ConnectionPool;
import cn.framework.db.sql.Procedure;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

import static cn.framework.core.utils.Xmls.*;

/**
 * 数据服务初始化provider
 *
 * @author wenlai
 */
@Service("dbInit")
public class DbInitProvider implements InitProvider {

    /*
     * @see cn.framework.core.container.InitProvider#init(cn.framework.core.container.Context)
     */
    @Override
    public synchronized void init(final Context context) throws Exception {
        loadDriver();
        Node database = xpathNode("//database", context.getConf());
        if (database == null) {
            return;
        }

        /**
         * build connections
         */
        Node connections = xpathNode(".//connections", database);
        if (connections != null) {
            NodeList connectionList = xpathNodes(".//connection", connections);
            if (connectionList != null && connectionList.getLength() > 0) {
                for (int i = 0; i < connectionList.getLength(); i++) {
                    Node conn = connectionList.item(i);
                    KVMap conf = new KVMap();
                    conf.addKV("id", attr("id", conn));
                    conf.addKV("username", childTextContent("username", conn, ""));
                    conf.addKV("url", childTextContent("url", conn));
                    conf.addKV("pwd", childTextContent("password", conn, ""));
                    conf.addKV("name", "db-pool");
                    Node pool = xpathNode(".//pool", conn);
                    if (pool != null) {
                        conf.addKV("size", Integer.parseInt(childTextContent("size", pool, "100")));
                        conf.addKV("checkPeriodSecond", Integer.parseInt(childTextContent("ping-second", pool, "100")));
                    }
                    else {
                        conf.addKV("size", 100);
                        conf.addKV("checkPeriodSecond", 100);
                    }
                    ConnectionPool.createPool(conf);
                }
            }
        }

        /**
         * build procedures
         */
        ArrayList<Node> proceduresNodeList = xpathNodesArray(".//procedures", database);
        if (proceduresNodeList != null && proceduresNodeList.size() > 0) {
            for (Node proceduresNode : proceduresNodeList) {
                List<Node> procedureList = xpathNodesArray(".//procedure", proceduresNode);
                for (Node node : procedureList) {
                    try {
                        Procedure.createProcedure(node);
                    }
                    catch (Exception x) {
                        LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
                    }
                }
            }
        }
    }

    /**
     * 测试是否已加载驱动
     */
    public void loadDriver() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch (Exception x) {
            Exceptions.logProcessor().logger().error("no mysql driver");
        }
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        }
        catch (Exception x) {
            Exceptions.logProcessor().logger().error("no sqlserver driver");
        }
    }
}
