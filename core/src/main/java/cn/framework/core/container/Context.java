/**
 * @项目名称: db
 * @文件名称: Initor.java
 * @Date: 2016年1月6日
 * @author: wenlai
 * @type: Initor
 */
package cn.framework.core.container;

import cn.framework.core.utils.KVMap;
import cn.framework.core.utils.Pair;
import cn.framework.core.utils.Strings;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.w3c.dom.Node;

import static cn.framework.core.utils.Exceptions.processException;

/**
 * 初始化参数<br>
 * 内部使用
 *
 * @author wenlai
 */
public class Context {

    /**
     * 自定义初始化参数
     */
    private KVMap params = null;
    /**
     * 上下文
     */
    private StandardContext context = null;
    /**
     * tomcat
     */
    private Tomcat tomcat = null;
    /**
     * 配置项
     */
    private Node conf = null;

    /**
     * 构造函数
     *
     * @param conf
     * @param tomcat
     * @param context
     */
    private Context(Node conf, Tomcat tomcat, StandardContext context) {
        this.conf = conf;
        this.tomcat = tomcat;
        this.context = context;
    }

    /**
     * 创建新的初始化参数
     *
     * @param conf
     * @param tomcat
     * @param context
     *
     * @return
     */
    static synchronized Context buildContext(Node conf, Tomcat tomcat, StandardContext context) {
        return new Context(conf, tomcat, context);
    }

    /**
     * 生成测试参数
     *
     * @param conf
     *
     * @return
     */
    public synchronized static Context buildByConf(Node conf) {
        return new Context(conf, null, null);
    }

    /**
     * 获取初始化参数
     *
     * @param name
     *
     * @return
     */
    public synchronized String getInitParameter(String name) {
        return params == null ? Strings.EMPTY : params.getString(name);
    }

    /**
     * 设置初始化参数
     *
     * @param params
     *
     * @return
     */
    public synchronized Context setParams(KVMap params) {
        if (this.params != null) {
            this.params.clear();
        }
        this.params = params;
        return this;
    }

    /**
     * 获取配置
     *
     * @return
     */
    public synchronized final Node getConf() {
        return this.conf;
    }

    /**
     * 获取tomcat
     *
     * @return
     */
    public synchronized final Tomcat getTomcat() {
        return this.tomcat;
    }

    /**
     * 获取上下文
     *
     * @return
     */
    public synchronized final StandardContext getContext() {
        return this.context;
    }

    /**
     * 向上下文中添加servlet
     */
    public synchronized void addServlet(String servletName, String servletClassName, String pattern) {
        addServlet(null, servletName, servletClassName, pattern, null);
    }

    /**
     * 向上下文中添加servlet
     */
    public synchronized void addServlet(String servletName, String servletClassName, String pattern, KVMap initParams) {
        addServlet(null, servletName, servletClassName, pattern, initParams);
    }

    /**
     * 向上下文中添加servlet
     */
    public synchronized void addServlet(String servletName, String servletClassName, String pattern, KVMap initParams, int loadOnStartup) {
        addServlet(null, servletName, servletClassName, pattern, initParams, loadOnStartup);
    }

    /**
     * 向上下文中添加servlet
     *
     * @param c
     * @param servletName
     * @param servletClassName
     * @param pattern
     */
    public synchronized void addServlet(StandardContext c, String servletName, String servletClassName, String pattern) {
        addServlet(c, servletName, servletClassName, pattern, null);
    }

    /**
     * 向上下文中添加servlet
     *
     * @param c                上下文
     * @param servletName      servlet名称
     * @param servletClassName servlet类名
     * @param pattern          匹配路径
     * @param initParams       初始化参数列表
     */
    public synchronized void addServlet(StandardContext c, String servletName, String servletClassName, String pattern, KVMap initParams) {
        addServlet(c, servletName, servletClassName, pattern, initParams, -1);
    }

    /**
     * 向上下文中添加servlet
     *
     * @param c                上下文
     * @param servletName      servlet名称
     * @param servletClassName servlet类名
     * @param pattern          匹配路径
     * @param initParams       初始化参数列表
     * @param loadOnStartup    启动顺序
     */
    public synchronized void addServlet(StandardContext c, String servletName, String servletClassName, String pattern, KVMap initParams, int loadOnStartup) {
        addServlet(c, servletName, servletClassName, pattern, initParams, loadOnStartup, false);
    }

    /**
     * 向上下文中添加servlet
     *
     * @param c                上下文
     * @param servletName      servlet名称
     * @param servletClassName servlet类名
     * @param pattern          匹配路径
     * @param initParams       初始化参数列表
     * @param loadOnStartup    启动顺序 小于0不会设置
     * @param auth             是否需要验证
     */
    public synchronized void addServlet(StandardContext c, String servletName, String servletClassName, String pattern, KVMap initParams, int loadOnStartup, boolean auth) {
        try {
            c = c == null ? this.context : c;
            if (c == null) {
                return;
            }
            Wrapper newServlet = c.createWrapper();
            newServlet.setEnabled(true);
            newServlet.setName(servletName);
            newServlet.setServletClass(servletClassName);
            if (loadOnStartup > -1) {
                newServlet.setLoadOnStartup(loadOnStartup);
            }
            if (initParams != null) {
                initParams.forEach((key, value) -> {
                    newServlet.addInitParameter(key, value != null ? value.toString() : Strings.EMPTY);
                });
            }
            newServlet.setAsyncSupported(true);
            newServlet.setLoadOnStartup(1);
            if (auth) {
                //newServlet.addSecurityReference(FrameworkContainer.ROLE_NAME, Strings.EMPTY);
                addFilter(Strings.append(servletName, "-authfilter"), AuthFilter.class.getName(), pattern); //change auth mode
            }
            c.addChild(newServlet);
            c.addServletMapping(pattern, servletName);
            System.out.println(String.format("register servlet : %s %s %s", servletName, servletClassName, pattern));
        }
        catch (Exception x) {
            processException(x);
        }
    }

    /**
     * 增加servlet
     *
     * @param servletName      servlet名称
     * @param servletClassName servlet类名
     * @param pattern          匹配路径
     * @param initParams       初始化参数列表
     * @param loadOnStartup    启动顺序 小于0不会设置
     * @param auth             是否需要验证
     */
    public synchronized void addServlet(String servletName, String servletClassName, String pattern, KVMap initParams, int loadOnStartup, boolean auth) {
        addServlet(null, servletName, servletClassName, pattern, initParams, loadOnStartup, auth);
    }

    /**
     * 添加filter
     *
     * @param filterName  filter名字
     * @param filterClass filter类全名
     * @param pattern     匹配路径
     */
    public synchronized void addFilter(String filterName, String filterClass, String pattern) {
        addFilter(filterName, filterClass, pattern, null);
    }

    /**
     * 添加filter
     *
     * @param filterName  filter名字
     * @param filterClass filter类全名
     * @param pattern     匹配路径
     * @param initParams  初始化参数
     */
    public synchronized void addFilter(String filterName, String filterClass, String pattern, KVMap initParams) {
        FilterDef sessionFD = new FilterDef();
        sessionFD.setFilterClass(filterClass);
        sessionFD.setFilterName(filterName);
        if (initParams != null && initParams.size() > 0) {
            for (Pair pair : initParams) {
                sessionFD.addInitParameter(pair.key, pair.value != null ? pair.value.toString() : Strings.EMPTY);
            }
        }
        FilterMap sessionMP = new FilterMap();
        sessionMP.setFilterName(filterName);
        sessionMP.addURLPattern(pattern);
        this.context.addFilterDef(sessionFD);
        this.context.addFilterMap(sessionMP);
    }
}
