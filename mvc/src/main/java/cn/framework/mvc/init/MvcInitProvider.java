/**
 * framework-mvc
 *
 * @项目名称: framework
 * @文件名称: MvcInitProvider.java
 * @Date: 2015年10月26日
 * @author: wenlai
 * @type: MvcInitProvider
 */
package cn.framework.mvc.init;

import cn.framework.core.annotation.Auth;
import cn.framework.core.annotation.LoadOnStartup;
import cn.framework.core.annotation.Path;
import cn.framework.core.container.Context;
import cn.framework.core.container.InitProvider;
import cn.framework.core.log.LogProvider;
import cn.framework.core.utils.*;
import cn.framework.mvc.route.ActionContainer;
import cn.framework.mvc.route.ActionContainer.METHOD;
import cn.framework.mvc.route.Route;
import cn.framework.mvc.tag.*;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static cn.framework.core.utils.Xmls.*;

/**
 * mvc初始化入口，在配置文件中增加:<br>
 * provider 入口<br>
 * 使用如下:<br>
 * <p>
 * <pre>
 *
 * <code>@Action("/xxxx/xx.jsp")</code>
 * public static {@link cn.framework.mvc.model.ActionResult} funcName(<strong><b>@Request</b> {@link HttpServletRequest} req</strong>)
 * {
 *     .....
 * }
 * </pre>
 *
 * @author wenlai
 */
@Service("mvcInit")
public class MvcInitProvider implements InitProvider {

    /**
     * 构建mvc路由需要的action容器
     *
     * @param mvcNode mvc node
     */
    private Map<String, ActionContainer> buildActionObj(Node mvcNode) throws Exception {
        Map<String, ActionContainer> container = new HashMap<>();
        ArrayList<Node> controllerNodes = xpathNodesArray(".//controller", mvcNode);
        if (controllerNodes == null || controllerNodes.size() <= 0) {
            return container;
        }
        controllerNodes.parallelStream().forEach((controllerNode) -> {
            try {
                String controllerPkg = attr("package", controllerNode);
                ClassPath classHandler = ClassPath.from(Projects.MAIN_CLASS_LOADER);
                ImmutableSet<ClassPath.ClassInfo> classes = classHandler.getTopLevelClassesRecursive(controllerPkg);
                if (classes == null || classes.size() <= 0) {
                    return;
                }

                classes.parallelStream().forEach((classInfo) -> {
                    try {
                        Class<?> clazz = Class.forName(classInfo.getName());
                        Controller controller = clazz.getDeclaredAnnotation(Controller.class);
                        if (controller == null) {
                            return;
                        }

                        String controllerPath = controller.value().startsWith("/") ? controller.value() : "/" + controller.value();
                        Method[] methods = clazz.getMethods();
                        if (Arrays.isNotNullOrEmpty(methods)) {
                            for (Method method : methods) {
                                Action action = method.getDeclaredAnnotation(Action.class);
                                DefaultAction defaultAction = method.getDeclaredAnnotation(DefaultAction.class);
                                if (action != null || defaultAction != null) {
                                    ActionContainer actionContainer = new ActionContainer();
                                    actionContainer.actionClassName = clazz.getName();
                                    Parameter[] params = method.getParameters();
                                    actionContainer.methodName = method.getName();
                                    actionContainer.method = method.getDeclaredAnnotation(Get.class) != null ? method.getDeclaredAnnotation(Post.class) != null ? METHOD.ALL : METHOD.GET : method.getDeclaredAnnotation(Post.class) != null ? METHOD.POST : METHOD.GET;
                                    if (Arrays.isNotNullOrEmpty(params) && params.length == 1) {
                                        Parameter param = params[0];
                                        Resource req = param.getDeclaredAnnotation(Resource.class);
                                        if (req != null) {
                                            actionContainer.hasContextResource = true;
                                        }
                                        else {
                                            break;
                                        }
                                    }
                                    View view = method.getDeclaredAnnotation(View.class);
                                    if (view != null) {
                                        actionContainer.hasView = true;
                                        actionContainer.viewPath = view.value().startsWith("/") ? view.value() : "/" + view.value();
                                    }
                                    String actionPath = defaultAction != null ? Strings.EMPTY : action.value().startsWith("/") ? action.value() : "/" + action.value();
                                    container.put(controllerPath + actionPath, actionContainer);
                                }
                            }
                        }
                    }
                    catch (Exception x) {
                        Exceptions.processException(x);
                    }
                });
            }
            catch (Exception x) {
                Exceptions.processException(x);
            }
        });
        return container;
    }

    /*
     * @see cn.framework.core.container.InitProvider#init(cn.framework.core.container.Context)
     */
    @Override
    public void init(final Context context) throws Exception {
        // TODO 无view的附加上通用view 是否需要？
        Node mvcNode = xpathNode(".//mvc", context.getConf());
        if (mvcNode != null) {

            /**
             * register global route
             */
            context.addServlet("mvc-route", Route.class.getName(), "/*");
            ArrayList<Node> viewNodes = xpathNodesArray(".//view", mvcNode);
            if (Arrays.isNotNullOrEmpty(viewNodes)) {
                ArrayList<Class<?>> classes = cn.framework.core.utils.Compiler.initViews(Projects.MAIN_CLASS);
                if (classes != null && classes.size() > 0) {
                    HashMap<String, String> viewPkgs = new HashMap<>();
                    viewNodes.forEach((viewNode) -> {
                        if (viewNode != null) {
                            try {
                                viewPkgs.put(attr("package", viewNode), attr("prefix", viewNode));
                            }
                            catch (Exception x) {
                                LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
                            }
                        }
                    });
                    classes.forEach((clazz) -> {
                        if (viewPkgs.containsKey(clazz.getPackage().getName())) {
                            String pattern = viewPkgs.get(clazz.getPackage().getName()) + "/" + clazz.getTypeName().replace("_jsp", "");
                            context.addServlet(pattern, clazz.getName(), pattern);
                        }
                    });
                }
            }

            //            autoScanServlet(context);

            if (Strings.isNotNullOrEmpty(attr("staticPattern", mvcNode))) {
                processStatusResource(context, attr("staticPattern", mvcNode));
            }

            /**
             * init actions
             */
            Reflects.setField(ActionContainer.class.getName(), "instance", buildActionObj(context.getConf()), null);
        }
    }

    /**
     * 扫描全部servlet并注册
     *
     * @param context
     */
    private void autoScanServlet(Context context) {
        try {
            ClassPath handler = ClassPath.from(Projects.MAIN_CLASS_LOADER);
            handler.getTopLevelClasses().parallelStream().forEach((classInfo) -> {
                try {
                    Class<?> clazz = Class.forName(classInfo.getName());
                    if (Servlet.class.isAssignableFrom(clazz)) {
                        if (classInfo.getPackageName().contains("cn.framework")) {
                            return;
                        }
                        Path path = clazz.getDeclaredAnnotation(Path.class);
                        if (path != null) {
                            boolean auth = clazz.getDeclaredAnnotation(Auth.class) != null;
                            int loadOnStartup = -1;
                            LoadOnStartup startup;
                            loadOnStartup = (startup = clazz.getDeclaredAnnotation(LoadOnStartup.class)) != null ? startup.value() : loadOnStartup;
                            context.addServlet(classInfo.getName(), classInfo.getName(), path.value(), null, loadOnStartup, auth);
                        }
                    }
                }
                catch (Exception x) {
                    Exceptions.processException(x);
                }
            });
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
    }

    /**
     * 静态资源解压及挂载<br/>
     * 目录结构如下:<br/>
     * /project/work/resource/js/ .....js<br/>
     * /project/work/resource/html/ .....js<br/>
     * /project/work/resource/css/ .....js<br/>
     * /project/work/resource/img/ .....js<br/>
     *
     * @param context
     */
    private void processStatusResource(Context context, String pattern) {

        try {

            String jsPath = Projects.RESOURCE_DIR + "/js";
            String htmlPath = Projects.RESOURCE_DIR + "/html";
            String cssPath = Projects.RESOURCE_DIR + "/css";
            String imgPath = Projects.RESOURCE_DIR + "/img";

            Files.createDirectory(jsPath);
            Files.createDirectory(htmlPath);
            Files.createDirectory(cssPath);
            Files.createDirectory(imgPath);

            Files.deleteFilesInPath(jsPath);
            Files.deleteFilesInPath(htmlPath);
            Files.deleteFilesInPath(cssPath);
            Files.deleteFilesInPath(imgPath);

            ClassPath classPath = ClassPath.from(Projects.MAIN_CLASS_LOADER);

            String resourceFilter = Projects.PROJECT_PACKAGE.replace(".", "/");

            classPath.getResources().parallelStream().filter(res -> res.getResourceName().contains(resourceFilter)).forEach(res -> {
                try {
                    if (res != null && (res.getResourceName().endsWith(".html") || res.getResourceName().endsWith(".js") || res.getResourceName().endsWith(".css") || res.getResourceName().endsWith(".jpg") || res.getResourceName().endsWith(".png") || res.getResourceName().endsWith(".gif"))) {
                        byte[] data = Files.readResource(res.getResourceName(), Projects.MAIN_CLASS_LOADER);
                        if (data != null && data.length > 0) {
                            String suffix = Files.getExtension(res.getResourceName());
                            String fileName = Files.getName(res.getResourceName());
                            switch (suffix) {
                                case "html":
                                    Files.write(Strings.append(htmlPath, "/", fileName), data);
                                    break;
                                case "js":
                                    Files.write(Strings.append(jsPath, "/", fileName), data);
                                    break;
                                case "css":
                                    Files.write(Strings.append(cssPath, "/", fileName), data);
                                    break;
                                case "jpg":
                                case "png":
                                case "gif":
                                    Files.write(Strings.append(imgPath, "/", fileName), data);
                                    break;
                            }
                        }
                    }
                }
                catch (Exception x) {
                    Exceptions.processException(x);
                }
            });

            context.getTomcat().addWebapp(pattern, Projects.RESOURCE_DIR);

        }
        catch (Exception x) {
            Exceptions.processException(x);
        }

    }

}
