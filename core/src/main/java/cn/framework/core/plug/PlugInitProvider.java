/**
 * @项目名称: core
 * @文件名称: PluginInitProvider.java
 * @Date: 2016年1月27日
 * @author: wenlai
 * @type: PluginInitProvider
 */
package cn.framework.core.plug;

import java.util.ArrayList;
import org.w3c.dom.Node;
import cn.framework.core.container.Context;
import cn.framework.core.container.InitProvider;
import cn.framework.core.utils.KVMap;
import cn.framework.core.utils.Strings;
import static cn.framework.core.utils.Exceptions.processException;
import static cn.framework.core.utils.Xmls.*;

/**
 * @author wenlai
 *
 */
public class PlugInitProvider implements InitProvider {
    
    /*
     * 初始化业务层
     */
    @Override
    public synchronized void init(Context context) throws Exception {
        ArrayList<Node> plugs = xpathNodesArray("//plugins", context.getConf());
        if (plugs != null && plugs.size() > 0) {
            for (Node plugsNode : plugs) {
                String plugsName = attr("name", plugsNode);
                ArrayList<Node> plugArr = xpathNodesArray(".//plugin", plugsNode);
                if (plugArr != null && plugArr.size() > 0) {
                    for (Node plugNode : plugArr) {
                        String plugNodeName = attr("name", plugNode);
                        if (Strings.isNullOrEmpty(plugNodeName))
                            continue;
                        String plugName = Strings.isNotNullOrEmpty(plugsName) ? new StringBuilder(plugsName).append("/").append(plugNodeName).toString() : plugNodeName;
                        PLUG_CONTAINER.addKV(plugName, Plugin.newInstance(attr("class", plugNode), new PluginWrapper()));
                    }
                }
                ArrayList<Node> callbackArr = xpathNodesArray(".//callback", plugsNode);
                if (callbackArr != null && callbackArr.size() > 0) {
                    for (Node callback : callbackArr) {
                        String callbackNodeName = attr("name", callback);
                        if (Strings.isNullOrEmpty(callbackNodeName))
                            continue;
                        String callbackName = Strings.isNotNullOrEmpty(plugsName) ? new StringBuilder(plugsName).append("/").append(callbackNodeName).toString() : callbackNodeName;
                        try {
                            Class<?> callbackClazz = Class.forName(attr("class", callback));
                            Object instance = callbackClazz.newInstance();
                            if (instance instanceof Callback)
                                PLUG_CONTAINER.addKV(callbackName, instance);
                        }
                        catch (Exception x) {
                            processException(x);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 容器
     */
    final static KVMap PLUG_CONTAINER = new KVMap();
}
