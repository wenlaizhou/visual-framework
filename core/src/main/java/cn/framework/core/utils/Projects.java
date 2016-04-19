package cn.framework.core.utils;

import cn.framework.core.FrameworkStart;

import java.net.InetAddress;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

import static cn.framework.core.utils.Property.*;

/**
 * 项目帮助类<br />
 * 不要尝试格式化该文件代码
 *
 * @author wenlai
 */
public final class Projects {

    /**
     * 项目路径<br>
     * 没有 / 结尾
     */
    public static final String PROJECT_DIR = get(USER_DIR, "");

    /**
     * classloader使用,获取当前运行时或业务代码所在的类名称
     */
    public static final String MAIN_CLASS = get(Property.MAIN_CLASS, FrameworkStart.class.getName());

    /**
     * main loader
     */
    public static final ClassLoader MAIN_CLASS_LOADER;

    /**
     * 初始化classloader
     */
    static {
        ClassLoader loader = FrameworkStart.class.getClassLoader();
        try {
            loader = Class.forName(MAIN_CLASS).getClassLoader();
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
        finally {
            MAIN_CLASS_LOADER = loader;
        }
    }

    /**
     * 项目命名空间
     */
    public static final String PROJECT_PACKAGE = get(Property.PROJECT_PACKAGE, "cn.framework");

    /**
     * 是否解压
     */
    public static final boolean UNPACK_ENABLED = Boolean.parseBoolean(get(Property.UNPACK_ENABLED, "true"));

    /**
     * 解压路径
     */
    public static final String UNPACK_DIR = get(Property.UNPACK_DIR, PROJECT_DIR + "/target/classes");

    /**
     * 当前运行的jar包路径
     */
    public static final String MAIN_JAR_PATH = get(Property.MAIN_JAR_PATH);

    /**
     * 配置路径<br>
     * 没有 / 结尾
     */
    public static final String CONF_DIR = get(Property.CONF_DIR, PROJECT_DIR);

    /**
     * 日志路径
     */
    public static final String LOG_DIR = get(Property.LOG_DIR, PROJECT_DIR + "/log");

    /**
     * jsp文件路径
     */
    public static final String JSP_DIR = get(Property.JSP_DIR, UNPACK_DIR);

    /**
     * tomcat工作目录
     */
    public static final String WORK_DIR;

    /**
     * 设置work目录
     */
    static {
        if (!exist(Property.WORK_DIR)) {
            set(Property.WORK_DIR, Projects.PROJECT_DIR + "/work");
        }
        WORK_DIR = get(Property.WORK_DIR);
        if (!Files.exist(WORK_DIR)) {
            try {
                java.nio.file.Files.createDirectories(Paths.get(WORK_DIR));
            }
            catch (Exception x) {
                Exceptions.processException(x);
            }
        }
    }

    /**
     * 缓存目录
     */
    public static final String CACHE_DIR = get(Property.CACHE_DIR, WORK_DIR + "/cache");

    /**
     * 资源目录 <br/>
     * 增加静态文件访问路径?
     * 单独进程?
     */
    public static final String RESOURCE_DIR = get(Property.RESOURCE_DIR, WORK_DIR + "/resource");

    /**
     * 设置属性
     */
    static {
        if (!exist(Property.USER_DIR)) {
            Property.set(Property.USER_DIR, PROJECT_DIR);
        }
        if (!exist(Property.CONF_DIR)) {
            Property.set(Property.CONF_DIR, CONF_DIR);
        }
        if (!exist(Property.JSP_DIR)) {
            Property.set(Property.JSP_DIR, JSP_DIR);
        }
        if (!exist(Property.LOG_DIR)) {
            Property.set(Property.LOG_DIR, LOG_DIR);
        }
        if (!exist(Property.MAIN_JAR_PATH)) {
            Property.set(Property.MAIN_JAR_PATH, MAIN_JAR_PATH);
        }
        if (!exist(Property.WORK_DIR)) {
            Property.set(Property.WORK_DIR, WORK_DIR);
        }
        if (!exist(Property.UNPACK_ENABLED)) {
            Property.set(Property.UNPACK_ENABLED, Boolean.toString(UNPACK_ENABLED).toLowerCase());
        }
        if (!exist(Property.UNPACK_DIR)) {
            Property.set(Property.UNPACK_DIR, UNPACK_DIR);
        }
        if (!exist(Property.MAIN_CLASS)) {
            Property.set(Property.MAIN_CLASS, MAIN_CLASS);
        }
        if (!exist(Property.CACHE_DIR)) {
            Property.set(Property.CACHE_DIR, CACHE_DIR);
        }
        if (!exist(Property.RESOURCE_DIR)) {
            Property.set(Property.RESOURCE_DIR, RESOURCE_DIR);
        }
        if(!exist(Property.PROJECT_PACKAGE)) {
            Property.set(Property.PROJECT_PACKAGE, PROJECT_PACKAGE);
        }
    }

    /**
     * 打印所有预设属性
     */
    public static void printAll() {
        StringBuilder result = new StringBuilder("PROJECT_PATH : ");
        result.append(PROJECT_DIR).append("\n");
        result.append("UNPACK_ENABLED : ").append(UNPACK_ENABLED).append("\n");
        result.append("UNPACK_PATH : ").append(UNPACK_DIR).append("\n");
        result.append("MAIN_JAR_PATH : ").append(MAIN_JAR_PATH).append("\n");
        result.append("CONF_PATH : ").append(CONF_DIR).append("\n");
        result.append("LOG_PATH : ").append(LOG_DIR).append("\n");
        result.append("WORK_DIR : ").append(WORK_DIR).append("\n");
        result.append("MAIN_CLASS : ").append(MAIN_CLASS).append("\n");
        result.append("CACHE_DIR : ").append(CACHE_DIR).append("\n");
        result.append("RESOURCE_DIR : ").append(RESOURCE_DIR).append("\n");
        System.out.println(result.toString());
    }

    public static void init() {

    }
}
