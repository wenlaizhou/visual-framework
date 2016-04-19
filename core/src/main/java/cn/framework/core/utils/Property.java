/**
 * @项目名称: framework
 * @文件名称: Property.java
 * @Date: 2015年10月27日
 * @author: wenlai
 * @type: Property
 */
package cn.framework.core.utils;

import cn.framework.core.log.LogProvider;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;

/**
 * 属性类<br>
 * 约定：1、dir为目录路径；2、path为文件路径；3、前缀为名称；4、dir结尾都没有/号<br>
 * 配置属性：<br>
 * 1、user.dir<br>
 * 2、conf.dir<br>
 * 3、log.dir<br>
 * 4、upk.enabled<br>
 * 5、upk.dir<br>
 * 6、mjar.path<br>
 * 7、tomcat.dir<br>
 *
 * @author wenlai
 */
public final class Property {

    /**
     * 没有这个key
     */
    public static final String NO_THIS_KEY = "NO_THIS_KEY";

    /**
     * 程序工作路径
     */
    public static final String USER_DIR = "user.dir";

    /**
     * 配置文件路径
     */
    public static final String CONF_DIR = "conf.dir";

    /**
     * 日志路径
     */
    public static final String LOG_DIR = "log.dir";

    /**
     * tomcat工作目录
     */
    public static final String WORK_DIR = "work.dir";

    /**
     * jsp文件目录
     */
    public static final String JSP_DIR = "jsp.dir";

    /**
     * 是否解压
     */
    public static final String UNPACK_ENABLED = "upk.enabled";

    /**
     * 解压路径
     */
    public static final String UNPACK_DIR = "upk.dir";

    /**
     * 主jar路径
     */
    public static final String MAIN_JAR_PATH = "mjar.path";

    /**
     * 启动类名称
     */
    public static final String MAIN_CLASS = "main.class";

    /**
     * 自定义类名
     */
    public static final String CUSTOM_CLASS = "custom.class";

    /**
     * 集群端口
     */
    public static final String CLUSTER_PORT = "cluster.port";

    /**
     * 缓存目录
     */
    public static final String CACHE_DIR = "cache.dir";

    /**
     * 资源目录
     */
    public static final String RESOURCE_DIR = "resource.dir";

    /**
     * 项目命名空间
     */
    public static final String PROJECT_PACKAGE = "project.pkg";

    /**
     * 判断是否存在这个key
     *
     * @param key
     *
     * @return
     */
    public static boolean exist(String key) {
        return System.getProperties().containsKey(key);
    }

    /**
     * 打印所有属性信息
     */
    public static void printAll() {
        Properties properties = System.getProperties();
        Set<Object> keys = properties.keySet();
        for (Object key : keys) {
            String formattedLine = Strings.format("${key} : ${value}", Pair.newPair("key", key), Pair.newPair("value", properties.get(key)));
            System.out.println(formattedLine);
            // LogProvider.getFrameworkInfoLogger().info(formattedLine);
        }
    }

    /**
     * 获取系统属性
     *
     * @param key
     *
     * @return
     */
    public static String get(String key) {
        try {
            String result = System.getProperty(key);
            return Strings.isNotNullOrEmpty(result) ? result : Strings.EMPTY;
        }
        catch (Exception x) {
            LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
        }
        return Strings.EMPTY;
    }

    /**
     * 使用系统属性填充 ${property}
     *
     * @param content 待处理文本
     *
     * @return 属性填充后文本
     */
    public static String fill(String content) {
        String[] properties = Regexs.match("\\$\\{(.*?)\\}", content);
        if (properties != null && properties.length > 0) {
            for (String property : properties) {
                content = content.replace(String.format("${%1$s}", property), Property.get(property));
            }
        }
        return content;
    }

    /**
     * 获取系统属性
     *
     * @param key
     * @param defaultValue
     *
     * @return
     */
    public static String get(String key, String defaultValue) {
        try {
            String result = System.getProperty(key);
            return Strings.isNotNullOrEmpty(result) ? result : defaultValue;
        }
        catch (Exception x) {
            LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
        }
        return defaultValue;
    }

    /**
     * 设置系统属性
     *
     * @param key
     * @param value
     */
    public static void set(String key, String value) {
        if (Strings.isNotNullOrEmpty(key) && Strings.isNotNullOrEmpty(value)) {
            System.setProperty(key, value);
        }
    }

    /**
     * 添加属性
     * @param properties
     */
    public static void add(Properties properties) {
        if (properties != null && properties.size() > 0) {
            System.getProperties().putAll(properties);
        }
    }

    /**
     * 从properties文件中加载Property属性值
     *
     * @param path 文件路径或资源路径
     */
    public synchronized static void load(String path) {
        Properties newProperty = new Properties();
        if (Files.exist(path)) {
            try {
                newProperty.load(Files.newInputStream(path));
                add(newProperty);
                return;
            }
            catch (Exception x) {
                Exceptions.processException(x);
            }
        }
        if (Files.existResource(path)) {
            try {
                newProperty.load(Files.getResourceInputStream(path));
                add(newProperty);
            }
            catch (Exception x) {
                Exceptions.processException(x);
            }
        }

    }

    /**
     * 根据xml node获取property属性 <br />
     * <property name="" value="" />
     *
     * @param propertyNodes
     *
     * @return
     */
    public static Properties loadFromXmlNode(ArrayList<Node> propertyNodes) {
        if (propertyNodes == null || propertyNodes.size() <= 0) {
            return null;
        }
        Properties properties = new Properties();
        propertyNodes.parallelStream().forEach(node -> {
            properties.put(Xmls.attr("name", node), Xmls.attr("value", node));
        });
        return properties;
    }

    /**
     * 根据xml node获取property属性 <br />
     * <property name="" value="" />
     *
     * @param propertyNodes
     *
     * @return
     */
    public static Properties loadFromXmlNode(Node... propertyNodes) {
        if (propertyNodes == null || propertyNodes.length <= 0) {
            return null;
        }
        Properties properties = new Properties();
        for (Node propertyNode : propertyNodes) {
            properties.put(Xmls.attr("name", propertyNode), Xmls.attr("value", propertyNode));
        }
        return properties;
    }
}
