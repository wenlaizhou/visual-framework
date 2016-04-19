package cn.framework.cache.init;

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
 * create at 16/3/29 下午8:14
 *
 * @author wenlai
 */
@Deprecated
@Service(EhCacheProvider.BEAN_NAME)
public class EhCacheProvider implements InitProvider {

    /**
     * spring bean name
     */
    public final static String BEAN_NAME = "ehCacheManager";
    public final static String DEFAULT_EHCACHE_CONF_PATH = "cn/framework/cache/init/ehcache.xml";
    public final static String EHCACHE_CONF_PATH = "ehcache.conf.path";
    private static volatile boolean INITED = false;
    private CacheManager manager = null;

    @Override
    public synchronized void init(Context context) {
        if (INITED) {
            return;
        }
        try {
            ByteArrayInputStream conf = null;
            try {
                if (Property.exist(EHCACHE_CONF_PATH)) {
                    String confPath = Property.get(EHCACHE_CONF_PATH);
                    if (Strings.isNotNullOrEmpty(confPath) && Files.existFilesOrResource(confPath, Class.forName(Property.CUSTOM_CLASS).getClassLoader())) {
                        if (Files.exist(confPath)) {
                            conf = new ByteArrayInputStream(Files.read(confPath, Charsets.UTF_8.name()).getBytes(Charsets.UTF_8));
                        }
                        else {
                            conf = new ByteArrayInputStream(Files.readResource(confPath, Projects.MAIN_CLASS_LOADER));
                        }
                    }
                }
            }
            catch (Exception x) {
                Exceptions.processException("获取ehcache自定义配置文件失败", x);
            }
            if (conf == null) {
                conf = new ByteArrayInputStream(Files.readResourceText(DEFAULT_EHCACHE_CONF_PATH, EhCacheProvider.class.getClassLoader()).getBytes(Charsets.UTF_8));
            }
            this.manager = CacheManager.create(conf);
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
        finally {
            INITED = true;
        }
    }

    /**
     * 获取EhCache 的  CacheManager
     *
     * @return
     */
    public final CacheManager getManager() {
        return this.manager;
    }

    public final Cache getCache(String cacheName) {
        return this.manager.getCache(cacheName);
    }

}
