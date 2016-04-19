/**
 * @项目名称: framework
 * @文件名称: RestserviceInit.java
 * @Date: 2015年10月15日
 * @author: wenlai
 * @type: RestserviceInit
 */
package cn.framework.rest.init;

import cn.framework.core.container.Context;
import cn.framework.core.container.InitProvider;
import cn.framework.core.utils.Arrays;
import cn.framework.core.utils.KVMap;
import cn.framework.core.utils.Reflects;
import cn.framework.rest.register.ServiceRegister;
import org.glassfish.jersey.servlet.ServletContainer;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;

import java.util.ArrayList;

import static cn.framework.core.utils.Xmls.attr;
import static cn.framework.core.utils.Xmls.xpathNodesArray;

/**
 * @author wenlai
 */
@Service("restServiceInit")
public class RestServiceInitProvider implements InitProvider {

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
                            for (Node packageNode : packageNodes)
                                packages.add(attr("value", packageNode));
                        }
                    }
                }
            }
            // org.glassfish.jersey.server.ResourceConfig 是主体配置类，包括资源扫描，上下文build等。。。
            // Thread.currentThread().setContextClassLoader(Class.forName(Projects.MAIN_CLASS).getClassLoader());
            Reflects.setField(ServiceRegister.class.getName(), "PACKAGES", packages.toArray(new String[0]), null);
            context.addServlet("jersey-servlet", ServletContainer.class.getName(), "/service/*", new KVMap("javax.ws.rs.Application", ServiceRegister.class.getName()), 1, false);
        }

    }

}
