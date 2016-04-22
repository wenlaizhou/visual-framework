package cn.framework.cache.init;

import cn.framework.cache.session.FrameworkSessionFilter;
import cn.framework.core.container.Context;
import cn.framework.core.container.InitProvider;
import cn.framework.core.utils.*;
import com.google.common.base.Charsets;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

/**
 * project code
 * package cn.framework.cache.init
 * create at 16/4/8 下午7:00
 *
 * @author wenlai
 */
@Service(FrameworkCache.BEAN_NAME)
public class FrameworkCache implements InitProvider {

    public static final String CACHE_DIR = "cache.dir";

    /**
     * bean name
     */
    public final static String BEAN_NAME = "frameworkCache";

    /**
     * 默认配置文件
     */
    public final static String DEFAULT_EHCACHE_CONF_PATH = "cn/framework/cache/init/ehcache.xml";

    /**
     * 配置文件路径属性
     */
    public final static String EHCACHE_CONF_PATH = "ehcache.conf.path";

    private static boolean INITED = false;

    /**
     * handler
     */
    private CacheManager manager = null;

    /**
     * 初始化
     *
     * @param context context
     */
    @Override
    public synchronized void init(Context context) {
        if (INITED) {
            return;
        }
        INITED = true;
        if (!Property.exist(CACHE_DIR)) {
            Property.set(CACHE_DIR, Strings.append(Projects.WORK_DIR, "/cache"));
        }
        Files.createDirectory(Property.get("cache.dir"));
        ByteArrayInputStream conf = null;
        try {
            if (Property.exist(EHCACHE_CONF_PATH)) {
                String confPath = Property.get(EHCACHE_CONF_PATH);
                if (Strings.isNotNullOrEmpty(confPath) && Files.existFilesOrResource(confPath, Projects.MAIN_CLASS_LOADER)) {
                    if (Files.existFilesOrResource(confPath, Projects.MAIN_CLASS_LOADER)) {
                        conf = new ByteArrayInputStream(Property.fill(Files.readFileOrResourceText(confPath, Charsets.UTF_8, Projects.MAIN_CLASS_LOADER)).getBytes(Charsets.UTF_8));
                    }
                }
            }
        }
        catch (Exception x) {
            Exceptions.processException("获取ehcache自定义配置文件失败", x);
        }
        if (conf == null) {
            conf = new ByteArrayInputStream(Property.fill(Files.readResourceText(DEFAULT_EHCACHE_CONF_PATH, FrameworkCache.class.getClassLoader())).getBytes(Charsets.UTF_8));
        }
        try {
            this.manager = CacheManager.create(conf);
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
        if (this.manager != null) {
            //            context.getContext().setManager(Springs.get(FrameworkSessionManager.BEAN_NAME));
            System.out.println(this.manager.getActiveConfigurationText());
        }
        context.addFilter("framework-session-filter", FrameworkSessionFilter.class.getName(), "/*");
    }

    /**
     * 获取EhCache 的  CacheManager
     *
     * @return
     */
    public final CacheManager getManager() {
        return this.manager;
    }

    /**
     * 获取cache实例
     *
     * @param cacheName name
     *
     * @return
     */
    public final Cache getCache(String cacheName) {
        return this.manager.getCache(cacheName);
    }
}
