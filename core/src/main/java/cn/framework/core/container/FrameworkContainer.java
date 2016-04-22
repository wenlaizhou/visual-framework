/**
 * @项目名称: framework
 * @文件名称: FrameworkContainer.java
 * @Date: 2015年10月15日
 * @author: wenlai
 * @type: FrameworkContainer
 */
package cn.framework.core.container;

import cn.framework.core.utils.*;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

import static cn.framework.core.utils.Exceptions.processException;
import static cn.framework.core.utils.Pair.newPair;
import static cn.framework.core.utils.Xmls.*;


/**
 * tomcat容器类
 *
 * @author wenlai
 */
public class FrameworkContainer {

    /**
     * 角色名称
     */
    public static final String ROLE_NAME = "admin";

    /**
     * 界面头部信息 ${frame-header}
     */
    public static final String FRAMEWORK_STATIC_HEADER = "<link href=\"http://visual-framework.com:81/css/bootstrap.css\" rel=\"stylesheet\">";

    /**
     * 界面底部信息 ${frame-footer}
     */
    public static final String FRAMEWORK_STATIC_FOOTER = "<script src=\"http://visual-framework.com:81/js/jquery.js\"></script>\n" +
            "<script src=\"http://visual-framework.com:81/js/bootstrap.js\"></script>\n" +
            "<script src=\"http://visual-framework.com:81/js/echarts-all.js\"></script>\n" +
            "<script src=\"http://visual-framework.com:81/js/jquery.cookie.js\"></script>\n" +
            "<script src=\"http://visual-framework.com:81/js/framework.js\"></script>";

    /**
     * 界面版权信息 ${frame-powered}
     */
    public static final String FRAMEWORK_POWERED_BY = "<div class=\"navbar-fixed-bottom text-center\">\n" +
            "    <hr/>\n" +
            "    powered by framework 2.0 &nbsp;\n" +
            "    <a href=\"mailto:wenlai_zhou@126.com\">mailto wenlai</a>\n" +
            "</div>\n";

    /**
     * 框架默认用户名
     */
    private static volatile String USER_NAME = "wenlai";

    /**
     * 框架默认密码
     */
    private static volatile String PASSWORD = "admin";

    /**
     * 配置
     */
    public final Node config;

    /**
     * 容器实例
     */
    public final Tomcat tomcat;

    /**
     * 上下文
     */
    public final StandardContext context;

    /**
     * 构建容器
     *
     * @param confPath config文件路径或直接传递xml文件内容
     *
     * @throws Exception
     */
    public FrameworkContainer(String confPath) throws Exception {

        Document propertyProcessDoc = buildDocument(confPath);

        /**
         * add property support
         */
        ArrayList<Node> propertiesNodes = xpathNodesArray("//properties", propertyProcessDoc);
        if (Arrays.isNotNullOrEmpty(propertiesNodes)) {
            for (Node propertiesNode : propertiesNodes) {
                if (Strings.isNotNullOrEmpty(attr("src", propertiesNode))) { // 处理properties的src属性,可以附加.properties属性文件
                    Property.load(Property.fill(attr("src", propertiesNode)));
                }
                ArrayList<Node> properties = childs("property", propertiesNode);
                if (Arrays.isNotNullOrEmpty(properties)) {
                    for (Node property : properties) {
                        String textContent = property.getTextContent();
                        Property.set(attr("name", property), Property.fill(Strings.isNotNullOrEmpty(textContent) ? textContent.trim() : attr("value", property)));
                    }
                }
            }
        }

        /**
         * add dynamic property support
         */
        Node dynamicNode = xpathNode("//dynamicConfig", propertyProcessDoc);
        if (dynamicNode != null && Strings.isNotNullOrEmpty(attr("src", dynamicNode))) {
            String filePath = Property.fill(attr("src", dynamicNode));
            Thread t = new Thread(() -> {
                for (; ; ) {
                    try {
                        Thread.sleep(60000);
                        if (Files.existFilesOrResource(filePath)) {
                            Property.load(filePath);
                        }
                    }
                    catch (Exception x) {
                        x.printStackTrace();
                    }
                }
            });
            t.setDaemon(true);
            t.setName(Strings.append("dynamic-config-thread"));
            t.start();
        }

        /**
         * build node by xml config
         */
        this.config = node(confPath);
        System.out.println(toXmlString(this.config));

        /**
         * add security support
         */
        Node securityNode = xpathNode("//security", this.config);
        if (securityNode != null) {
            USER_NAME = attr("username", securityNode, "wenlai");
            PASSWORD = attr("password", securityNode, "admin");
            Property.set("username", USER_NAME);
            Property.set("password", PASSWORD);
        }

        /**
         * build tomcat
         */
        this.tomcat = new Tomcat();
        this.tomcat.setBaseDir(Projects.WORK_DIR);
        this.tomcat.addRole(USER_NAME, ROLE_NAME);
        this.tomcat.addUser(USER_NAME, PASSWORD);
        this.context = (StandardContext) tomcat.addContext("", null);
        this.context.setReloadable(true);
        this.context.setDelegate(true);
        this.context.setPrivileged(true);

        /**
         * add spring support
         */
        Node springNode = xpathNode("//spring", this.config);
        if (springNode != null) {
            String springConfPath = attr("src", springNode);
            if (Strings.isNotNullOrEmpty(springConfPath)) {
                this.context.addParameter("contextConfigLocation", springConfPath);
                this.context.addApplicationListener(ContextLoaderListener.class.getName());
            }
            String annotationConf = attr("class", springNode);
            try {
                if (Strings.isNotNullOrEmpty(annotationConf)) {
                    AnnotationConfigApplicationContext configApplicationContext = new AnnotationConfigApplicationContext(Class.forName(annotationConf));
                    Springs.addContext(configApplicationContext);
                }
            }
            catch (Exception x) {
                x.printStackTrace();
            }
        }
        this.context.addApplicationListener(Springs.ContextRegister.class.getName());
    }

    /**
     * 构建ui界面 并添加版权信息
     *
     * @param filePath
     *
     * @return
     */
    public static String buildUI(String filePath, ClassLoader classLoader) {
        try {
            if (Files.existFilesOrResource(filePath, classLoader)) {
                return Strings.format(Files.readFileOrResourceText(filePath), newPair("frame-header", FRAMEWORK_STATIC_HEADER), newPair("frame-footer", FRAMEWORK_STATIC_FOOTER), newPair("frame-powered", FRAMEWORK_POWERED_BY));
            }
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
        return Strings.EMPTY;
    }

    /**
     * 框架基础认证
     *
     * @param req  request
     * @param resp response
     *
     * @return
     */
    public static boolean basicAuth(HttpServletRequest req, HttpServletResponse resp) {
        if (req == null || resp == null) {
            return false;
        }
        try {
            String auth = req.getHeader("Authorization");
            if (Strings.isNotNullOrEmpty(auth)) {
                String[] token = auth.split(" ");
                if (token != null && token.length > 1) { //multiple jugement
                    if (Strings.isNotNullOrEmpty(token[1])) {
                        if (Base64s.decode(token[1]).equals(String.format("%1$s:%2$s", getUsername(), getPassword()))) {
                            return true;
                        }
                    }
                }
                //Strings.isNotNullOrEmpty(auth) && Base64s.decode(auth.split(" ")[1]).equals(String.format("%1$s:%2$s", getUsername(), getPassword()))) {
            }
        }
        catch (Exception x) {
            processException(x);
        }
        try {
            resp.setHeader("Cache-Control", "no-store");
            resp.setDateHeader("Expires", 0);
            resp.setHeader("WWW-authenticate", "Basic Realm=\"wenlai.framework\"");
            resp.sendError(401, "FORBIDDEN");
        }
        catch (Exception e) {
            processException(e);
        }
        return false;
    }

    /**
     * 获取框架用户名
     *
     * @return
     */
    public final static String getUsername() {
        return USER_NAME;
    }

    /**
     * 获取框架密码
     *
     * @return
     */
    public static final String getPassword() {
        return PASSWORD;
    }

    /**
     * 初始化容器
     *
     * @throws Exception
     */
    public synchronized void init() throws Exception {

        /**
         * create web-resource context
         */
        final Context param = Context.buildContext(this.config, this.tomcat, this.context);

        /**
         * container init
         */
        initDefaultProvider(param);

        /**
         * xml based init
         */
        Node initNode = xpathNode(".//init", this.config);
        if (initNode != null) {
            ArrayList<Node> providers = xpathNodesArray(".//provider", initNode);
            if (Arrays.isNotNullOrEmpty(providers)) {
                for (Node providerNode : providers) {
                    param.setParams(null);
                    if (Strings.isNotNullOrEmpty(attr("name", providerNode))) {
                        try {
                            ArrayList<Node> paramsNodes = xpathNodesArray(".//init-param", providerNode);
                            if (Arrays.isNotNullOrEmpty(paramsNodes)) {
                                KVMap initParams = new KVMap();
                                for (Node node : paramsNodes)
                                    initParams.addKV(attr("name", node), attr("value", node));
                                param.setParams(initParams);
                            }
                            //InitProvider provider = Reflects.createInstance(attr("class", providerNode));
                            String name = attr("name", providerNode);
                            InitProvider provider = Springs.get(name);
                            if (provider != null) {
                                System.out.println(Strings.format("start init ${name}", Pair.newPair("name", name)));
                                provider.init(param);
                                System.out.println(Strings.format("init ${name} done!", Pair.newPair("name", name)));
                            }

                            else {
                                Exceptions.logProcessor().logger().error("provider not found : {}", name);
                            }
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
     * 判断是否是默认初始化provider
     *
     * @param className class-name
     *
     * @return
     */
    private boolean isDefaultProvider(String className) {
        if (Strings.isNotNullOrEmpty(className)) {
            if (className.equals(ServerInitProvider.class.getName())) {
                return true;
            }
            if (className.equals(WebAppInitProvider.class.getName())) {
                return true;
            }
            if (className.equals(MonitorInitProvider.class.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * init default init-provider
     *
     * @param context context
     */
    private void initDefaultProvider(Context context) {
        InitProvider serverInit = Springs.get("serverInit");
        try {
            serverInit.init(context);
            System.out.println("server init done!");
        }
        catch (Exception x) {
            x.printStackTrace();
        }
        InitProvider webInit = Springs.get("webInit");
        try {
            webInit.init(context);
            System.out.println("web init done!");
        }
        catch (Exception x) {
            x.printStackTrace();
        }
        InitProvider monitorInit = Springs.get("monitorInit");
        try {
            monitorInit.init(context);
            System.out.println("monitor init done!");
        }
        catch (Exception x) {
            x.printStackTrace();
        }
        InitProvider logInit = Springs.get("logInit");
        try {
            logInit.init(context);
            System.out.println("log init done!");
        }
        catch (Exception x) {
            x.printStackTrace();
        }
        InitProvider clusterInit = Springs.get("clusterInit");
        try {
            clusterInit.init(context);
            System.out.println("cluster init done!");
        }
        catch (Exception x) {
            x.printStackTrace();
        }
        InitProvider zkInit = Springs.get("zkInitor");
        try {
            zkInit.init(context);
        }
        catch (Exception x) {
            x.printStackTrace();
        }
    }

    /**
     * 启动容器
     *
     * @throws Exception
     */
    public void start() throws Exception {
        this.tomcat.start();
        System.out.println("visual-framework started!");
        System.out.println("powered by wenlai");
        System.out.println("https://github.com/wenlaizhou/wenlai-framework");
        this.tomcat.getServer().await();
    }
}
