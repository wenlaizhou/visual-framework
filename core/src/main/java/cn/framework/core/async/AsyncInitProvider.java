/**
 * @项目名称: core
 * @文件名称: AsyncInitProvider.java
 * @Date: 2016年1月11日
 * @author: wenlai
 * @type: AsyncInitProvider
 */
package cn.framework.core.async;

import java.util.ArrayList;
import java.util.HashMap;
import org.w3c.dom.Node;
import cn.framework.core.container.Context;
import cn.framework.core.container.InitProvider;
import cn.framework.core.utils.Arrays;
import cn.framework.core.utils.KVMap;
import cn.framework.core.utils.Reflects;
import cn.framework.core.utils.Strings;
import static cn.framework.core.utils.Exceptions.processException;
import static cn.framework.core.utils.Xmls.*;

/**
 * @author wenlai
 *
 */
public class AsyncInitProvider implements InitProvider {
    
    /*
     * @see cn.framework.core.container.InitProvider#init(cn.framework.core.container.Context)
     */
    @Override
    public void init(Context context) throws Exception {
        Node asyncNode = xpathNode("//async", context.getConf());
        if (asyncNode == null)
            return;
        Reflects.setField("cn.framework.core.async.AsyncProvider", "SIZE", Strings.parseInt(attr("size", asyncNode, "20")), null);
        Node callbacksNode = xpathNode(".//callbacks", asyncNode);
        if (callbacksNode != null) {
            ArrayList<Node> callbacks = xpathNodesArray(".//callback", callbacksNode);
            HashMap<String, AsyncCallback> container = new HashMap<String, AsyncCallback>();
            if (Arrays.isNotNullOrEmpty(callbacks)) {
                for (Node callback : callbacks) {
                    String id = attr("id", callback);
                    if (Strings.isNullOrEmpty(id))
                        continue;
                    String className = attr("class", callback);
                    if (Strings.isNullOrEmpty(className))
                        continue;
                    try {
                        AsyncCallback callbackObj = (AsyncCallback) Class.forName(className).newInstance();
                        KVMap params = new KVMap();
                        ArrayList<Node> paramsNodes = xpathNodesArray(".//init-param", callback);
                        if (Arrays.isNotNullOrEmpty(paramsNodes))
                            for (Node paramNode : paramsNodes)
                                params.addKV(attr("key", paramNode), attr("value", paramNode));
                        callbackObj.init(params);
                        container.put(id, callbackObj);
                    }
                    catch (Exception x) {
                        processException(x);
                    }
                }
                Reflects.setField("cn.framework.core.async.AsyncProvider", "CONTAINER", container, null);
            }
        }
    }
}
