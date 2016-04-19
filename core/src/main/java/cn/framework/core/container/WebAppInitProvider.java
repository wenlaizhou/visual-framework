/**
 * @项目名称: core
 * @文件名称: WebAppInitProvider.java
 * @Date: 2016年1月4日
 * @author: wenlai
 * @type: WebAppInitProvider
 */
package cn.framework.core.container;

import cn.framework.core.annotation.Auth;
import cn.framework.core.annotation.Path;
import cn.framework.core.log.LogProvider;
import cn.framework.core.utils.*;
import com.google.common.reflect.ClassPath;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;

import javax.servlet.Servlet;
import java.util.ArrayList;

import static cn.framework.core.utils.Xmls.*;

/**
 * @author wenlai
 */
@Service("webInit")
public class WebAppInitProvider implements InitProvider {

    private static volatile boolean INITED = false;

    /**
     * @see cn.framework.core.container.InitProvider#init(cn.framework.core.container.Context)
     */
    @Override
    public synchronized void init(final Context context) throws Exception {
        try {
            if (INITED) {
                return;
            }
            buildWebapps(context);
            buildServlets(context);
            buildFilters(context);
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
        finally {
            INITED = true;
        }
    }

    /**
     * 构建过滤器
     *
     * @param context context
     */
    private void buildFilters(Context context) {
        try {
            Node filters = xpathNode("//filters", context.getConf());
            if (filters != null) {
                ArrayList<Node> filterNodeList = xpathNodesArray(".//filter", filters);
                if (filterNodeList != null && filterNodeList.size() > 0) {
                    for (Node filterNode : filterNodeList) {
                        try {
                            context.addFilter(attr("name", filterNode), attr("class", filterNode), attr("pattern", filterNode), buildParams(".//param", filterNode));
                            LogProvider.getFrameworkInfoLogger().error("add buildFilters : {}", attr("class", filterNode));
                        }
                        catch (Exception x) {
                            Exceptions.processException(x);
                        }
                    }
                }
            }
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
    }

    /**
     * 构建servlet
     *
     * @param context context
     */
    private void buildServlets(Context context) {
        try {
            Node servlets = xpathNode("//servlets", context.getConf());
            if (servlets == null) {
                return;
            }
            if (Strings.isNotNullOrEmpty(attr("autoScan", servlets))) {
                ClassPath.from(Projects.MAIN_CLASS_LOADER).getTopLevelClassesRecursive(attr("autoScan", servlets)).parallelStream().forEach(classInfo -> {
                    try {
                        Class<?> clazz = Class.forName(classInfo.getName());
                        if (Servlet.class.isAssignableFrom(clazz)) {
                            Path servletPath = clazz.getDeclaredAnnotation(Path.class);
                            context.addServlet(clazz.getSimpleName(), clazz.getName(), servletPath.value(), null, -1, clazz.getDeclaredAnnotation(Auth.class) != null);
                        }
                    }
                    catch (Exception x) {
                        Exceptions.processException(x);
                    }
                });
            }
            ArrayList<Node> servletNodeList = xpathNodesArray(".//servlet", servlets);
            if (servletNodeList != null && servletNodeList.size() > 0) {
                for (Node servletNode : servletNodeList) {
                    try {
                        String loadUp = childTextContent("loadOnStartup", servletNode);
                        int loadOnStart = Strings.isNotNullOrEmpty(loadUp) ? Strings.parseInt(loadUp, -1) : -1;
                        boolean auth = Boolean.parseBoolean(childTextContent("auth", servletNode, "false"));
                        context.addServlet(null, attr("name", servletNode), attr("class", servletNode), attr("pattern", servletNode), buildParams(".//param", servletNode), loadOnStart, auth);
                        LogProvider.getFrameworkInfoLogger().error("add buildServlets : {}", attr("class", servletNode));
                    }
                    catch (Exception x) {
                        Exceptions.processException(x);
                    }
                }
            }
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
    }

    /**
     * 构建webapp
     *
     * @param context context
     */
    private void buildWebapps(Context context) {
        try {
            Node webApps = xpathNode("//web-apps", context.getConf());
            if (webApps != null) {
                ArrayList<Node> apps = xpathNodesArray(".//web-app", webApps);
                if (apps != null && apps.size() > 0) {
                    for (Node node : apps) {
                        try {
                            String pattern = Property.fill(attr("pattern", node));
                            String path = Property.fill(attr("path", node));
                            context.getTomcat().addWebapp(pattern, path);
                            System.out.println(Strings.append(pattern, " : ", path));
                        }
                        catch (Exception x) {
                            Exceptions.processException(x);
                        }
                    }
                }
            }
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
    }

    /**
     * 构建参数列表
     *
     * @param express  获取参数节点的xpath表达式 例如: .//param<br>
     *                 会获取所有param节点的key - value值并存入结果中
     * @param mainNode 主节点
     *
     * @return
     */
    private KVMap buildParams(String express, Node mainNode) {
        try {
            KVMap params = new KVMap();
            ArrayList<Node> paramNodes = xpathNodesArray(express, mainNode);
            if (paramNodes != null && paramNodes.size() > 0) {
                for (Node paramNode : paramNodes) {
                    params.addKV(attr("name", paramNode), attr("value", paramNode));
                }
            }
            return params;
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
        return null;
    }

}
