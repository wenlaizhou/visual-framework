package cn.framework.db.view;

import cn.framework.core.container.Context;
import cn.framework.core.container.InitProvider;
import cn.framework.core.utils.Exceptions;
import cn.framework.core.utils.KVMap;
import cn.framework.core.utils.Strings;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

import static cn.framework.core.utils.Xmls.*;

/**
 * project code
 * package cn.framework.db.process
 * create at 16/3/17 下午8:16
 *
 * @author wenlai
 */
@Service("dbViewInit")
public class DbViewInitProvider implements InitProvider {

    /**
     * View holder
     */
    private static final KVMap VIEWER_CONTAINER = new KVMap();

    /**
     * 是否初始化过
     */
    private static volatile boolean INIT = false;

    /**
     * 获取DbViewer
     *
     * @param action view container actionId
     *
     * @return
     */
    public final static DbViewer getDbViewer(String action) {
        return VIEWER_CONTAINER.get(action);
    }

    /**
     * 直接获取Viewer
     *
     * @return
     */
    public final static KVMap getDbViewers() {
        return VIEWER_CONTAINER;
    }

    /**
     * 初始化db-viewer
     *
     * @param context 配置上下文
     *
     * @throws Exception
     */
    @Override
    public synchronized void init(Context context) throws Exception {
        if (!INIT) {
            try {
                Node database = xpathNode("//database", context.getConf());
                if (database == null) {
                    return;
                }
                /**
                 * build view
                 */
                ArrayList<Node> proceduresNodeList = xpathNodesArray(".//procedures", database);
                if (proceduresNodeList != null && proceduresNodeList.size() > 0) {
                    for (Node proceduresNode : proceduresNodeList) {
                        List<Node> procedureList = xpathNodesArray(".//procedure", proceduresNode);
                        for (Node procedureNode : procedureList) {
                            try {
                                String procedureId = attr("id", procedureNode.getParentNode()) + "/" + attr("id", procedureNode);
                                Node viewNode = xpathNode(".//view", procedureNode);
                                if (viewNode != null && Strings.isNotNullOrEmpty(attr("action", viewNode))) {
                                    DbViewer dbViewer = new DbViewer();
                                    dbViewer.actionId = attr("action", viewNode);
                                    dbViewer.procedureId = procedureId;
                                    dbViewer.method = attr("method", viewNode, "POST").toUpperCase();
                                    dbViewer.name = attr("name", viewNode, dbViewer.actionId);
                                    String beforeId = attr("beforeId", viewNode);
                                    String afterId = attr("afterId", viewNode);
                                    if (Strings.isNotNullOrEmpty(beforeId)) {
                                        dbViewer.beforeId = beforeId;
                                    }
                                    if (Strings.isNotNullOrEmpty(afterId)) {
                                        dbViewer.afterId = afterId;
                                    }

                                    /**
                                     * build comment infomations
                                     */
                                    try {
                                        dbViewer.description = childTextContent("description", viewNode, dbViewer.procedureId + "/" + dbViewer.actionId);
                                        ArrayList<Node> paramNodes = xpathNodesArray(".//param", viewNode);
                                        if (paramNodes != null && paramNodes.size() > 0) {
                                            for (Node paramNode : paramNodes) {
                                                DbViewer.Param param = new DbViewer.Param();
                                                param.desctiption = paramNode.getTextContent();
                                                param.name = attr("name", paramNode);
                                                param.type = attr("type", paramNode);
                                                dbViewer.params.add(param);
                                            }
                                        }
                                    }
                                    catch (Exception x) {
                                        Exceptions.processException(x);
                                    }
                                    VIEWER_CONTAINER.put(dbViewer.actionId, dbViewer);
                                }
                            }
                            catch (Exception x) {
                                Exceptions.processException(x);
                            }
                        }
                    }
                }
                /**
                 * register dispather-servlet
                 */
                context.addServlet("data-service", DbActionProcessor.class.getName(), "/data/service");
                context.addServlet("database-ui", DbViewUI.class.getName(), "/database-ui/*", null, -1, true);
            }
            catch (Exception x) {
                Exceptions.processException(x);
            }
            finally {
                INIT = true;
            }
        }
    }
}
