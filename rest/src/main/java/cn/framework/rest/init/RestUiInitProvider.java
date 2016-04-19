/**
 * @项目名称: framework
 * @文件名称: RestResourceInit.java
 * @Date: 2015年10月15日
 * @author: wenlai
 * @type: RestResourceInit
 */
package cn.framework.rest.init;

import cn.framework.core.container.Context;
import cn.framework.core.container.InitProvider;
import cn.framework.core.utils.Arrays;
import cn.framework.core.utils.Reflects;
import cn.framework.rest.resource.RestUI;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;

import java.util.ArrayList;

import static cn.framework.core.utils.Xmls.attr;
import static cn.framework.core.utils.Xmls.xpathNodesArray;

/**
 * @author wenlai
 */
@Service("restUiInit")
public class RestUiInitProvider implements InitProvider {

    /*
     * @see cn.framework.core.container.InitProvider#init(cn.framework.core.container.Context)
     */
    @Override
    public void init(final Context context) throws Exception {
        ArrayList<Node> restServicesNodes = xpathNodesArray("//rest-services", context.getConf());
        if (Arrays.isNotNullOrEmpty(restServicesNodes)) {
            ArrayList<String> packages = new ArrayList<>();
            for (Node restServicesNode : restServicesNodes) {
                ArrayList<Node> serviceNodes = xpathNodesArray(".//service", restServicesNode);
                if (Arrays.isNotNullOrEmpty(serviceNodes)) {
                    for (Node serviceNode : serviceNodes) {
                        ArrayList<Node> packageNodes = xpathNodesArray("package", serviceNode);
                        if (Arrays.isNotNullOrEmpty(packageNodes)) {
                            for (Node packageNode : packageNodes) {
                                packages.add(attr("value", packageNode));
                            }
                        }
                    }
                }
            }

            Reflects.setField(RestUI.class.getName(), "PACKAGES", packages.toArray(new String[0]), null);
            context.addServlet("rest-ui", RestUI.class.getName(), "/service/ui/*", null, -1, true);
        }

    }

}
